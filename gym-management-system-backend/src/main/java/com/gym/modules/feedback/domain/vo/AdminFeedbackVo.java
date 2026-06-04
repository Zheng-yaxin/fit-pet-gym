package com.gym.modules.feedback.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AdminFeedbackVo {
    private Long id;
    private Long memberId;
    private String memberName;
    private Long scheduleId;
    private Long bookingId;
    private Long courseId;
    private String courseName;
    private Long coachId;
    private String coachName;
    private String feedbackType;
    private String targetTitle;
    private Integer rating;
    private Integer intensity;
    private String content;
    private String tags;
    private String handleStatus;
    private String adminReply;
    private Integer followUpRequired;
    private Date handleTime;
    private Date createTime;
}
