package com.gym.modules.health.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BodyInsightVO {
    private String status;
    private String headline;
    private String bmiStatus;
    private String trendLabel;
    private BigDecimal latestWeight;
    private BigDecimal latestBmi;
    private BigDecimal latestBodyFatRate;
    private BigDecimal weightDelta;
    private BigDecimal bodyFatDelta;
    private String latestMeasureTime;
    private List<String> explanations;
    private List<String> actions;
}
