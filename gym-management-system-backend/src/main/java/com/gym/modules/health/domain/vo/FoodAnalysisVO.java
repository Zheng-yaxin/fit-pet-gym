package com.gym.modules.health.domain.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FoodAnalysisVO {
    // AI 识别的食物名称
    private String name;

    // 预估总重量 (g)
    private BigDecimal estimatedWeight;

    // 下面是每100g的营养素，用于自动创建 Food 对象
    private BigDecimal caloriesPer100g;
    private BigDecimal proteinPer100g;
    private BigDecimal fatPer100g;
    private BigDecimal carbPer100g;

    // AI 给出的简短分析建议
    private String analysis;
}