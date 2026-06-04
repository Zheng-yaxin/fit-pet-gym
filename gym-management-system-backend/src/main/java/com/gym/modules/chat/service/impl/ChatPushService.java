package com.gym.modules.chat.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.modules.chat.domain.dto.ChatSocketEnvelope;
import com.gym.modules.chat.domain.dto.ChatSocketPayload;
import com.gym.modules.chat.domain.enums.ChatSocketMessageType;
import com.gym.modules.chat.domain.vo.ChatMessageVO;
import com.gym.modules.chat.websocket.ChatSessionRegistry;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ChatPushService {

    @Resource
    private ChatSessionRegistry sessionRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean pushMessage(ChatMessageVO message, Long receiverId, String receiverRole) {
        Set<Session> sessions = sessionRegistry.getSessions(receiverId, receiverRole);
        if (sessions.isEmpty()) {
            return false;
        }

        ChatSocketEnvelope envelope = new ChatSocketEnvelope();
        envelope.setType(ChatSocketMessageType.CHAT_MESSAGE.name());
        envelope.setSenderId(message.getSenderId());
        envelope.setSenderRole(message.getSenderRole());
        envelope.setReceiverId(receiverId);
        envelope.setReceiverRole(receiverRole);
        envelope.setTimestamp(System.currentTimeMillis());

        ChatSocketPayload payload = new ChatSocketPayload();
        payload.setMessageId(message.getId());
        payload.setContent(message.getContent());
        envelope.setPayload(payload);

        String text;
        try {
            text = objectMapper.writeValueAsString(envelope);
        } catch (Exception e) {
            return false;
        }

        boolean pushed = false;
        for (Session session : sessions) {
            if (!session.isOpen()) {
                sessionRegistry.remove(session);
                continue;
            }
            try {
                session.getAsyncRemote().sendText(text);
                pushed = true;
            } catch (Exception e) {
                sessionRegistry.remove(session);
            }
        }
        return pushed;
    }
}
