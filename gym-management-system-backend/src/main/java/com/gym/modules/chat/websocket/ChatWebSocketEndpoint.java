package com.gym.modules.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.common.utils.SpringContextUtils;
import com.gym.modules.chat.domain.dto.ChatMessageSendDto;
import com.gym.modules.chat.domain.dto.ChatSocketEnvelope;
import com.gym.modules.chat.domain.dto.ChatSocketPayload;
import com.gym.modules.chat.domain.enums.ChatSocketMessageType;
import com.gym.modules.chat.domain.vo.ChatMessageVO;
import com.gym.modules.chat.service.IChatService;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;

@ServerEndpoint(value = "/ws/chat", configurator = ChatSocketEndpointConfig.class)
public class ChatWebSocketEndpoint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ATTR_USER_ID = "userId";
    private static final String ATTR_USER_ROLE = "userRole";

    @OnOpen
    public void onOpen(Session session) throws IOException {
        ChatSocketAuthService authService = SpringContextUtils.getBean(ChatSocketAuthService.class);
        ChatSessionRegistry sessionRegistry = SpringContextUtils.getBean(ChatSessionRegistry.class);

        ChatSocketUser user = authService.authenticate(firstQueryValue(session, "token"));
        if (user == null) {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized"));
            return;
        }

        session.getUserProperties().put(ATTR_USER_ID, user.getUserId());
        session.getUserProperties().put(ATTR_USER_ROLE, user.getUserRole());
        sessionRegistry.register(user.getUserId(), user.getUserRole(), session);
        sendConnectionStatus(session, true);
        broadcastOnlineStatus(user.getUserId(), user.getUserRole(), true);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        ChatSocketEnvelope envelope = OBJECT_MAPPER.readValue(message, ChatSocketEnvelope.class);
        if (envelope.getType() == null) {
            sendError(session, "Missing message type", envelope.getRequestId());
            return;
        }

        Long senderId = currentUserId(session);
        String senderRole = currentUserRole(session);
        if (senderId == null || senderRole == null) {
            sendError(session, "Unauthorized", envelope.getRequestId());
            return;
        }

        ChatSocketMessageType type;
        try {
            type = ChatSocketMessageType.valueOf(envelope.getType());
        } catch (IllegalArgumentException e) {
            sendError(session, "Unsupported message type", envelope.getRequestId());
            return;
        }

        try {
            if (type == ChatSocketMessageType.CHAT_MESSAGE) {
                handleChatMessage(session, envelope, senderId, senderRole);
                return;
            }

            if (type == ChatSocketMessageType.TYPING || type == ChatSocketMessageType.READ_RECEIPT) {
                forwardLightweightEvent(envelope, senderId, senderRole);
            }
        } catch (Exception e) {
            sendError(session, e.getMessage(), envelope.getRequestId());
        }
    }

    @OnClose
    public void onClose(Session session) {
        Long userId = currentUserId(session);
        String userRole = currentUserRole(session);
        SpringContextUtils.getBean(ChatSessionRegistry.class).remove(session);
        if (userId != null && userRole != null) {
            broadcastOnlineStatus(userId, userRole, false);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        if (session != null && session.isOpen()) {
            sendError(session, throwable.getMessage(), null);
        }
    }

    private void handleChatMessage(Session session, ChatSocketEnvelope envelope, Long senderId, String senderRole) throws IOException {
        ChatSocketPayload payload = envelope.getPayload();
        if (envelope.getReceiverId() == null || envelope.getReceiverRole() == null || payload == null || payload.getContent() == null) {
            sendError(session, "Invalid chat message", envelope.getRequestId());
            return;
        }

        ChatMessageSendDto dto = new ChatMessageSendDto();
        dto.setReceiverId(envelope.getReceiverId());
        dto.setReceiverRole(envelope.getReceiverRole());
        dto.setContent(payload.getContent());

        ChatMessageVO saved = SpringContextUtils.getBean(IChatService.class).sendMessageFromSocket(senderId, senderRole, dto);

        ChatSocketEnvelope ack = new ChatSocketEnvelope();
        ack.setType(ChatSocketMessageType.CHAT_MESSAGE.name());
        ack.setRequestId(envelope.getRequestId());
        ack.setSenderId(senderId);
        ack.setSenderRole(senderRole);
        ack.setReceiverId(envelope.getReceiverId());
        ack.setReceiverRole(envelope.getReceiverRole());
        ack.setTimestamp(System.currentTimeMillis());

        ChatSocketPayload ackPayload = new ChatSocketPayload();
        ackPayload.setMessageId(saved.getId());
        ackPayload.setContent(saved.getContent());
        ack.setPayload(ackPayload);

        session.getBasicRemote().sendText(OBJECT_MAPPER.writeValueAsString(ack));
    }

    private void forwardLightweightEvent(ChatSocketEnvelope envelope, Long senderId, String senderRole) {
        if (envelope.getReceiverId() == null || envelope.getReceiverRole() == null) {
            return;
        }

        if (ChatSocketMessageType.READ_RECEIPT.name().equals(envelope.getType())) {
            SpringContextUtils.getBean(IChatService.class)
                    .markReadFromSocket(senderId, senderRole, envelope.getReceiverId(), envelope.getReceiverRole());
        }

        envelope.setSenderId(senderId);
        envelope.setSenderRole(senderRole);
        envelope.setTimestamp(System.currentTimeMillis());
        sendToUser(envelope, envelope.getReceiverId(), envelope.getReceiverRole());
    }

    private void sendConnectionStatus(Session session, boolean connected) throws IOException {
        ChatSocketEnvelope envelope = new ChatSocketEnvelope();
        envelope.setType(ChatSocketMessageType.ONLINE_STATUS.name());
        envelope.setTimestamp(System.currentTimeMillis());
        ChatSocketPayload payload = new ChatSocketPayload();
        payload.setOnline(connected);
        envelope.setPayload(payload);
        session.getBasicRemote().sendText(OBJECT_MAPPER.writeValueAsString(envelope));
    }

    private void broadcastOnlineStatus(Long userId, String userRole, boolean online) {
        ChatSocketEnvelope envelope = new ChatSocketEnvelope();
        envelope.setType(ChatSocketMessageType.ONLINE_STATUS.name());
        envelope.setSenderId(userId);
        envelope.setSenderRole(userRole);
        envelope.setTimestamp(System.currentTimeMillis());

        ChatSocketPayload payload = new ChatSocketPayload();
        payload.setOnline(online);
        envelope.setPayload(payload);

        ChatSessionRegistry sessionRegistry = SpringContextUtils.getBean(ChatSessionRegistry.class);
        for (Session targetSession : sessionRegistry.allSessions()) {
            if (!targetSession.isOpen()) {
                sessionRegistry.remove(targetSession);
                continue;
            }
            targetSession.getAsyncRemote().sendText(toJson(envelope));
        }
    }

    private void sendToUser(ChatSocketEnvelope envelope, Long receiverId, String receiverRole) {
        ChatSessionRegistry sessionRegistry = SpringContextUtils.getBean(ChatSessionRegistry.class);
        for (Session targetSession : sessionRegistry.getSessions(receiverId, receiverRole)) {
            if (!targetSession.isOpen()) {
                sessionRegistry.remove(targetSession);
                continue;
            }
            targetSession.getAsyncRemote().sendText(toJson(envelope));
        }
    }

    private String toJson(ChatSocketEnvelope envelope) {
        try {
            return OBJECT_MAPPER.writeValueAsString(envelope);
        } catch (Exception e) {
            return "{\"type\":\"ERROR\"}";
        }
    }

    private Long currentUserId(Session session) {
        Object value = session.getUserProperties().get(ATTR_USER_ID);
        return value instanceof Long ? (Long) value : null;
    }

    private String currentUserRole(Session session) {
        Object value = session.getUserProperties().get(ATTR_USER_ROLE);
        return value == null ? null : String.valueOf(value);
    }

    private String firstQueryValue(Session session, String key) {
        Map<String, java.util.List<String>> params = session.getRequestParameterMap();
        java.util.List<String> values = params.get(key);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    private void sendError(Session session, String message, String requestId) throws IOException {
        ChatSocketEnvelope envelope = new ChatSocketEnvelope();
        envelope.setType(ChatSocketMessageType.ERROR.name());
        envelope.setRequestId(requestId);
        envelope.setTimestamp(System.currentTimeMillis());
        ChatSocketPayload payload = new ChatSocketPayload();
        payload.setContent(message);
        envelope.setPayload(payload);
        session.getBasicRemote().sendText(OBJECT_MAPPER.writeValueAsString(envelope));
    }
}
