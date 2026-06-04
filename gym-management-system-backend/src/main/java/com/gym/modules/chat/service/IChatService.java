package com.gym.modules.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.chat.domain.dto.ChatMessageSendDto;
import com.gym.modules.chat.domain.entity.ChatMessage;
import com.gym.modules.chat.domain.vo.ChatContactVo;
import com.gym.modules.chat.domain.vo.ChatMessageVO;

import java.util.List;

public interface IChatService extends IService<ChatMessage> {

    List<ChatContactVo> getContacts();

    ChatMessageVO sendMessage(ChatMessageSendDto dto);

    ChatMessageVO sendMessageFromSocket(Long senderId, String senderRole, ChatMessageSendDto dto);

    void markReadFromSocket(Long readerId, String readerRole, Long contactId, String contactRole);

    List<ChatMessageVO> getMessages(Long contactId, String contactRole);

    Long getUnreadCount();
}
