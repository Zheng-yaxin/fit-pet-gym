package com.gym.modules.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("gym_chat_message")
public class ChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long senderId;
    private String senderRole; // MEMBER, COACH
    private Long receiverId;
    private String receiverRole; // MEMBER, COACH
    private String content;
    private Integer isRead; // 0:未读, 1:已读

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableLogic
    private Integer deleted;
}
