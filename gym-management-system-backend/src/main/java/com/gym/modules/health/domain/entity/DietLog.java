package com.gym.modules.health.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 饮食记录
 */
@Data
@TableName("gym_diet_log")
public class DietLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 食物ID
     */
    private Long foodId;

    /**
     * 摄入量 (克)
     */
    private BigDecimal amount;

    /**
     * 餐别 (1:早餐, 2:午餐, 3:晚餐, 4:加餐)
     */
    private Integer mealType;

    /**
     * 进食日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date eatDate;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}