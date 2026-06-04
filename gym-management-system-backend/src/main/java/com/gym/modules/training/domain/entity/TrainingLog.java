package com.gym.modules.training.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gym.common.json.FlexibleDateDeserializer;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gym_training_log")
public class TrainingLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private Long planId;
    @JsonDeserialize(using = FlexibleDateDeserializer.class)
    private Date trainingDate;
    private Integer durationMinutes;
    private Integer intensity;
    private Integer caloriesBurned;
    private String feeling;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableLogic
    private Integer deleted;
}
