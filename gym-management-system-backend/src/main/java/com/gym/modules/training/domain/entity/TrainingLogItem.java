package com.gym.modules.training.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("gym_training_log_item")
public class TrainingLogItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long logId;
    private Long exerciseId;
    private BigDecimal weight;
    private Integer reps;
    private Integer sets;
    private String completed;
}
