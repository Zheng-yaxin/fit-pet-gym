package com.gym.modules.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.chat.domain.dto.ChatMessageSendDto;
import com.gym.modules.chat.domain.entity.ChatMessage;
import com.gym.modules.chat.domain.vo.ChatContactVo;
import com.gym.modules.chat.domain.vo.ChatMessageVO;
import com.gym.modules.chat.mapper.ChatMessageMapper;
import com.gym.modules.chat.service.IChatService;
import com.gym.modules.chat.websocket.ChatSessionRegistry;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.coach.domain.entity.PersonalTrainingBooking;
import com.gym.modules.coach.service.ICoachService;
import com.gym.modules.coach.service.IPersonalTrainingBookingService;
import com.gym.modules.feedback.domain.entity.CourseFeedback;
import com.gym.modules.feedback.mapper.CourseFeedbackMapper;
import com.gym.modules.member.service.IMemberService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatService {

    @Resource
    private IPersonalTrainingBookingService bookingService;

    @Resource
    private ICoachService coachService;

    @Resource
    private IMemberService memberService;

    @Resource
    private ChatPushService chatPushService;

    @Resource
    private ChatSessionRegistry chatSessionRegistry;

    @Resource
    private CourseFeedbackMapper feedbackMapper;

    private String getCurrentUserRole() {
        try {
            return SecurityUtils.getLoginUser().getUserType().getCode();
        } catch (Exception e) {
            return "MEMBER";
        }
    }

    @Override
    public List<ChatContactVo> getContacts() {
        Long userId = SecurityUtils.getUserId();
        String userRole = getCurrentUserRole();
        List<ChatContactVo> contacts = new ArrayList<>();
        LambdaQueryWrapper<PersonalTrainingBooking> query = new LambdaQueryWrapper<>();

        if ("MEMBER".equals(userRole)) {
            query.eq(PersonalTrainingBooking::getMemberId, userId);
            Set<Long> contactIds = bookingService.list(query).stream()
                    .map(PersonalTrainingBooking::getCoachId)
                    .collect(Collectors.toCollection(HashSet::new));
            contactIds.addAll(listFeedbackFollowUpCoachIds(userId));
            if (!contactIds.isEmpty()) {
                contacts = coachService.listByIds(contactIds).stream().map(coach -> {
                    ChatContactVo vo = new ChatContactVo();
                    vo.setId(coach.getId());
                    vo.setName(coach.getName());
                    vo.setAvatar(coach.getAvatar());
                    vo.setRole("COACH");
                    vo.setUnreadCount(countUnread(coach.getId(), "COACH", userId, "MEMBER"));
                    vo.setOnline(chatSessionRegistry.isOnline(coach.getId(), "COACH"));
                    return vo;
                }).collect(Collectors.toList());
            }
        } else {
            query.eq(PersonalTrainingBooking::getCoachId, userId);
            Set<Long> contactIds = bookingService.list(query).stream()
                    .map(PersonalTrainingBooking::getMemberId)
                    .collect(Collectors.toCollection(HashSet::new));
            contactIds.addAll(listFeedbackFollowUpMemberIds(userId));
            if (!contactIds.isEmpty()) {
                contacts = memberService.listByIds(contactIds).stream().map(member -> {
                    ChatContactVo vo = new ChatContactVo();
                    vo.setId(member.getId());
                    vo.setName(member.getName());
                    vo.setAvatar(member.getAvatar());
                    vo.setRole("MEMBER");
                    vo.setUnreadCount(countUnread(member.getId(), "MEMBER", userId, "COACH"));
                    vo.setOnline(chatSessionRegistry.isOnline(member.getId(), "MEMBER"));
                    return vo;
                }).collect(Collectors.toList());
            }
        }
        return contacts;
    }

    private Set<Long> listFeedbackFollowUpCoachIds(Long memberId) {
        return feedbackMapper.selectList(new LambdaQueryWrapper<CourseFeedback>()
                        .eq(CourseFeedback::getMemberId, memberId)
                        .eq(CourseFeedback::getFollowUpRequired, 1)
                        .eq(CourseFeedback::getDeleted, 0)
                        .isNotNull(CourseFeedback::getCoachId))
                .stream()
                .map(CourseFeedback::getCoachId)
                .collect(Collectors.toSet());
    }

    private Set<Long> listFeedbackFollowUpMemberIds(Long coachId) {
        return feedbackMapper.selectList(new LambdaQueryWrapper<CourseFeedback>()
                        .eq(CourseFeedback::getCoachId, coachId)
                        .eq(CourseFeedback::getFollowUpRequired, 1)
                        .eq(CourseFeedback::getDeleted, 0)
                        .isNotNull(CourseFeedback::getMemberId))
                .stream()
                .map(CourseFeedback::getMemberId)
                .collect(Collectors.toSet());
    }

    private Long countUnread(Long senderId, String senderRole, Long receiverId, String receiverRole) {
        return this.count(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getSenderId, senderId)
                .eq(ChatMessage::getSenderRole, senderRole)
                .eq(ChatMessage::getReceiverId, receiverId)
                .eq(ChatMessage::getReceiverRole, receiverRole)
                .eq(ChatMessage::getIsRead, 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatMessageVO sendMessage(ChatMessageSendDto dto) {
        Long senderId = SecurityUtils.getUserId();
        String senderRole = getCurrentUserRole();
        return saveAndPushMessage(senderId, senderRole, dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatMessageVO sendMessageFromSocket(Long senderId, String senderRole, ChatMessageSendDto dto) {
        return saveAndPushMessage(senderId, senderRole, dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markReadFromSocket(Long readerId, String readerRole, Long contactId, String contactRole) {
        List<ChatMessage> unreadMessages = list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getSenderId, contactId)
                .eq(ChatMessage::getSenderRole, contactRole)
                .eq(ChatMessage::getReceiverId, readerId)
                .eq(ChatMessage::getReceiverRole, readerRole)
                .eq(ChatMessage::getIsRead, 0));
        if (!unreadMessages.isEmpty()) {
            unreadMessages.forEach(msg -> msg.setIsRead(1));
            updateBatchById(unreadMessages);
        }
    }

    private ChatMessageVO saveAndPushMessage(Long senderId, String senderRole, ChatMessageSendDto dto) {
        validateChatRelationship(senderId, senderRole, dto.getReceiverId(), dto.getReceiverRole());

        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setSenderRole(senderRole);
        message.setReceiverId(dto.getReceiverId());
        message.setReceiverRole(dto.getReceiverRole());
        message.setContent(dto.getContent());
        message.setIsRead(0);
        this.save(message);

        ChatMessageVO vo = buildMessageVo(message, senderId, senderRole, dto.getReceiverId(), dto.getReceiverRole());
        chatPushService.pushMessage(vo, dto.getReceiverId(), dto.getReceiverRole());
        return vo;
    }

    private void validateChatRelationship(Long senderId, String senderRole, Long receiverId, String receiverRole) {
        LambdaQueryWrapper<PersonalTrainingBooking> query = new LambdaQueryWrapper<>();
        if ("MEMBER".equals(senderRole) && "COACH".equals(receiverRole)) {
            query.eq(PersonalTrainingBooking::getMemberId, senderId)
                    .eq(PersonalTrainingBooking::getCoachId, receiverId);
        } else if ("COACH".equals(senderRole) && "MEMBER".equals(receiverRole)) {
            query.eq(PersonalTrainingBooking::getCoachId, senderId)
                    .eq(PersonalTrainingBooking::getMemberId, receiverId);
        } else {
            throw new ServiceException("不支持的聊天关系");
        }

        if (bookingService.count(query) > 0) {
            return;
        }

        if (!hasFeedbackFollowUpRelationship(senderId, senderRole, receiverId, receiverRole)) {
            throw new ServiceException("无预约关系，无法发起聊天");
        }
    }

    private boolean hasFeedbackFollowUpRelationship(Long senderId, String senderRole, Long receiverId, String receiverRole) {
        LambdaQueryWrapper<CourseFeedback> query = new LambdaQueryWrapper<CourseFeedback>()
                .eq(CourseFeedback::getFollowUpRequired, 1)
                .eq(CourseFeedback::getDeleted, 0);
        if ("MEMBER".equals(senderRole) && "COACH".equals(receiverRole)) {
            query.eq(CourseFeedback::getMemberId, senderId)
                    .eq(CourseFeedback::getCoachId, receiverId);
        } else if ("COACH".equals(senderRole) && "MEMBER".equals(receiverRole)) {
            query.eq(CourseFeedback::getCoachId, senderId)
                    .eq(CourseFeedback::getMemberId, receiverId);
        } else {
            return false;
        }
        return feedbackMapper.selectCount(query) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ChatMessageVO> getMessages(Long contactId, String contactRole) {
        Long currentUserId = SecurityUtils.getUserId();
        String currentUserRole = getCurrentUserRole();

        List<ChatMessage> list = this.list(new LambdaQueryWrapper<ChatMessage>()
                .and(wrapper -> wrapper
                        .nested(i -> i.eq(ChatMessage::getSenderId, currentUserId)
                                .eq(ChatMessage::getSenderRole, currentUserRole)
                                .eq(ChatMessage::getReceiverId, contactId)
                                .eq(ChatMessage::getReceiverRole, contactRole))
                        .or()
                        .nested(i -> i.eq(ChatMessage::getSenderId, contactId)
                                .eq(ChatMessage::getSenderRole, contactRole)
                                .eq(ChatMessage::getReceiverId, currentUserId)
                                .eq(ChatMessage::getReceiverRole, currentUserRole)))
                .orderByAsc(ChatMessage::getCreateTime));

        List<ChatMessage> unreadMessages = list.stream()
                .filter(msg -> !msg.getSenderId().equals(currentUserId) && msg.getIsRead() == 0)
                .collect(Collectors.toList());
        if (!unreadMessages.isEmpty()) {
            unreadMessages.forEach(msg -> msg.setIsRead(1));
            this.updateBatchById(unreadMessages);
        }

        return list.stream()
                .map(msg -> buildMessageVo(msg, currentUserId, currentUserRole, contactId, contactRole))
                .collect(Collectors.toList());
    }

    private ChatMessageVO buildMessageVo(ChatMessage msg, Long currentUserId, String currentUserRole, Long contactId, String contactRole) {
        ChatMessageVO vo = new ChatMessageVO();
        BeanUtils.copyProperties(msg, vo);
        boolean isSelf = msg.getSenderId().equals(currentUserId) && msg.getSenderRole().equals(currentUserRole);
        vo.setIsSelf(isSelf);

        if (isSelf) {
            fillSenderInfo(vo, currentUserId, currentUserRole);
        } else {
            fillSenderInfo(vo, contactId, contactRole);
        }
        return vo;
    }

    private void fillSenderInfo(ChatMessageVO vo, Long userId, String userRole) {
        if ("COACH".equals(userRole)) {
            Coach coach = coachService.getById(userId);
            if (coach != null) {
                vo.setSenderName(coach.getName());
                vo.setSenderAvatar(coach.getAvatar());
            }
            return;
        }

        Member member = memberService.getById(userId);
        if (member != null) {
            vo.setSenderName(member.getName());
            vo.setSenderAvatar(member.getAvatar());
        }
    }

    @Override
    public Long getUnreadCount() {
        Long currentUserId = SecurityUtils.getUserId();
        String currentUserRole = getCurrentUserRole();
        return this.count(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getReceiverId, currentUserId)
                .eq(ChatMessage::getReceiverRole, currentUserRole)
                .eq(ChatMessage::getIsRead, 0));
    }
}
