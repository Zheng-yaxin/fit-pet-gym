package com.gym.modules.training.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("gym_training_plan")
public class TrainingPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private String goal;
    private Integer weeklyFrequency;
    private String source;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private List<TrainingPlanDay> days;
}
