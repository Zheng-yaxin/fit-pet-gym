package com.gym.modules.feedback.domain.dto;

import lombok.Data;

@Data
public class AdminFeedbackHandleDto {
    private String handleStatus;
    private String adminReply;
    private Integer followUpRequired;
}
