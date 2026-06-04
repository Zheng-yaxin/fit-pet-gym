package com.gym.modules.chat.websocket;

import jakarta.websocket.Session;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionRegistry {

    private static final String ATTR_USER_KEY = "chatUserKey";
    private final ConcurrentHashMap<String, Set<Session>> sessions = new ConcurrentHashMap<>();

    public void register(Long userId, String userRole, Session session) {
        String userKey = buildUserKey(userId, userRole);
        session.getUserProperties().put(ATTR_USER_KEY, userKey);
        sessions.computeIfAbsent(userKey, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public Set<Session> getSessions(Long userId, String userRole) {
        return sessions.getOrDefault(buildUserKey(userId, userRole), Collections.emptySet());
    }

    public boolean isOnline(Long userId, String userRole) {
        return !getSessions(userId, userRole).isEmpty();
    }

    public Set<Session> allSessions() {
        Set<Session> result = ConcurrentHashMap.newKeySet();
        sessions.values().forEach(result::addAll);
        return result;
    }

    public void remove(Session session) {
        Object keyObj = session.getUserProperties().get(ATTR_USER_KEY);
        if (keyObj == null) {
            return;
        }
        String userKey = String.valueOf(keyObj);
        Set<Session> userSessions = sessions.get(userKey);
        if (userSessions == null) {
            return;
        }
        userSessions.remove(session);
        if (userSessions.isEmpty()) {
            sessions.remove(userKey);
        }
    }

    private String buildUserKey(Long userId, String userRole) {
        return userId + ":" + userRole;
    }
}
