package com.gym.modules.health.domain.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DietSummaryVO {
    private String date;
    private Long id;
    // 实际摄入
    private BigDecimal totalCalories;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;
    private BigDecimal totalCarb;

    // 推荐摄入
    private BigDecimal recommendCalories;
    private BigDecimal recommendProtein;
    private BigDecimal recommendFat;
    private BigDecimal recommendCarb;

    // 饮食建议
    private List<String> suggestions;

    // 详细记录列表
    private List<DietDetailVO> details;

    @Data
    public static class DietDetailVO {
        private Long id;
        private String foodName;
        private BigDecimal amount;
        private BigDecimal calories;
        private BigDecimal protein;
        private BigDecimal fat;
        private BigDecimal carbohydrate;
        private Integer mealType;
    }
}