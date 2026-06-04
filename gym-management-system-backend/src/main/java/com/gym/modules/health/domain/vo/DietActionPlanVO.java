package com.gym.modules.health.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DietActionPlanVO {
    private String date;
    private String status;
    private String headline;
    private String nextMealFocus;
    private BigDecimal caloriesGap;
    private BigDecimal proteinGap;
    private BigDecimal fatGap;
    private BigDecimal carbohydrateGap;
    private List<ActionItem> actions;
    private List<String> notes;

    @Data
    public static class ActionItem {
        private String kind;
        private String priority;
        private String title;
        private String detail;
    }
}
