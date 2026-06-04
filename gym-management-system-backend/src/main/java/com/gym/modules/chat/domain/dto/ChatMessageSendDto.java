package com.gym.modules.chat.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatMessageSendDto {
    @NotNull(message = "接收人ID不能为空")
    private Long receiverId;

    @NotBlank(message = "接收人角色不能为空")
    private String receiverRole;

    @NotBlank(message = "消息内容不能为空")
    private String content;
}
