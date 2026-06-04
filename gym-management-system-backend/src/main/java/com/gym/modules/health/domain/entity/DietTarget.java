package com.gym.modules.health.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("gym_diet_target")
public class DietTarget {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private BigDecimal caloriesTarget;
    private BigDecimal proteinTarget;
    private BigDecimal fatTarget;
    private BigDecimal carbohydrateTarget;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
