package com.gym.modules.health.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DietGapVO {
    private BigDecimal caloriesTarget;
    private BigDecimal caloriesActual;
    private BigDecimal caloriesGap;
    private BigDecimal proteinTarget;
    private BigDecimal proteinActual;
    private BigDecimal proteinGap;
    private BigDecimal fatTarget;
    private BigDecimal fatActual;
    private BigDecimal fatGap;
    private BigDecimal carbohydrateTarget;
    private BigDecimal carbohydrateActual;
    private BigDecimal carbohydrateGap;
}
