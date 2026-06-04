package com.gym.modules.chat.websocket;

import jakarta.websocket.Session;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatSessionRegistryTest {

    private final ChatSessionRegistry registry = new ChatSessionRegistry();

    @Test
    void registerAndRemoveSessionByUserKey() {
        Session session = mock(Session.class);
        when(session.getUserProperties()).thenReturn(new ConcurrentHashMap<>());

        registry.register(7L, "MEMBER", session);

        assertEquals(1, registry.getSessions(7L, "MEMBER").size());
        assertTrue(registry.isOnline(7L, "MEMBER"));

        registry.remove(session);

        assertTrue(registry.getSessions(7L, "MEMBER").isEmpty());
        assertFalse(registry.isOnline(7L, "MEMBER"));
    }
}
