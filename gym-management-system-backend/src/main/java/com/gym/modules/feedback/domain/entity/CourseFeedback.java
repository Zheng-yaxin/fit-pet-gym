package com.gym.modules.feedback.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gym_course_feedback")
public class CourseFeedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private Long scheduleId;
    private Long bookingId;
    private Long courseId;
    private Long coachId;
    private String feedbackType;
    private Integer rating;
    private Integer intensity;
    private String content;
    private String tags;
    private String handleStatus;
    private String adminReply;
    private Integer followUpRequired;
    private Date handleTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableLogic
    private Integer deleted;
}
