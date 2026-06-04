package com.gym.modules.training.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gym_training_checkin")
public class TrainingCheckin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private Long planId;
    private Date startTime;
    private Date endTime;
    private Integer durationMinutes;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
