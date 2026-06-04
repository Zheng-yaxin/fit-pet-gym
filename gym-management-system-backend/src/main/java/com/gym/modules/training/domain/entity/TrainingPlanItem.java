package com.gym.modules.training.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("gym_training_plan_item")
public class TrainingPlanItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planDayId;
    private Long exerciseId;
    private Integer sets;
    private String reps;
    private Integer restSeconds;
    private Integer sortOrder;

    @TableField(exist = false)
    private String exerciseName;

    @TableField(exist = false)
    private String targetMuscle;

    @TableField(exist = false)
    private String equipment;

    @TableField(exist = false)
    private String difficulty;

    @TableField(exist = false)
    private String videoUrl;

    @TableField(exist = false)
    private String steps;

    @TableField(exist = false)
    private String tips;
}
