package com.gym.modules.chat.domain.vo;

import lombok.Data;

@Data
public class ChatContactVo {
    private Long id;
    private String name;
    private String avatar;
    private String role; // COACH or MEMBER
    private String latestMessage;
    private String latestMessageTime;
    private Boolean online;

    // 新增字段：未读消息数
    private Long unreadCount;
}
