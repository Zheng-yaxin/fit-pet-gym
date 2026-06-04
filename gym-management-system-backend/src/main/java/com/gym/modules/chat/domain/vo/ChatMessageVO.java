package com.gym.modules.chat.domain.vo;

import lombok.Data;
import java.util.Date;

/**
 * 聊天消息展示对象
 * 对应 HeartBridge 的 MessageVO
 */
@Data
public class ChatMessageVO {
    private Long id;
    private Long senderId;
    private String senderRole; // MEMBER, COACH

    private String senderName;   // 发送者姓名 (后端填充)
    private String senderAvatar; // 发送者头像 (后端填充)

    private String content;
    private Date createTime;

    private Boolean isSelf; // 是否是当前用户发送的
}