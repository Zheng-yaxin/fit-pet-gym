package com.gym.modules.feedback.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CourseFeedbackPendingVo {
    private Long enrollmentId;
    private Long scheduleId;
    private Long bookingId;
    private Long courseId;
    private Long coachId;
    private String feedbackType;
    private String courseName;
    private String coachName;
    private Date classTime;
    private String startTime;
    private String endTime;
}
