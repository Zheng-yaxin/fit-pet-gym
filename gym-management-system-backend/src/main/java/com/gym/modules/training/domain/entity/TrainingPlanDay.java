package com.gym.modules.training.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@TableName("gym_training_plan_day")
public class TrainingPlanDay {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private Integer dayIndex;
    private String title;
    private String targetMuscle;
    private Integer estimatedMinutes;

    @TableField(exist = false)
    private List<TrainingPlanItem> items;
}
