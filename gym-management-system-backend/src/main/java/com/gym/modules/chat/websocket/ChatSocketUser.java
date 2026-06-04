package com.gym.modules.chat.websocket;

public class ChatSocketUser {
    private Long userId;
    private String userRole;

    public ChatSocketUser(Long userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserRole() {
        return userRole;
    }
}
