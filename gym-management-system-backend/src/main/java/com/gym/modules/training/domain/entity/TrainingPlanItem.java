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
}
