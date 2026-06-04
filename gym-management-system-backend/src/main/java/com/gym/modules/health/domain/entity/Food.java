package com.gym.modules.health.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 常见食物库
 */
@Data
@TableName("gym_food")
public class Food {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 食物名称
     */
    private String name;

    /**
     * 热量 (kcal/100g)
     */
    private BigDecimal calories;

    /**
     * 蛋白质 (g/100g)
     */
    private BigDecimal protein;

    /**
     * 脂肪 (g/100g)
     */
    private BigDecimal fat;

    /**
     * 碳水化合物 (g/100g)
     */
    private BigDecimal carbohydrate;
}