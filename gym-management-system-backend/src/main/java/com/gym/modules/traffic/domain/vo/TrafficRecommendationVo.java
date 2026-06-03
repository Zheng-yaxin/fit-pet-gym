package com.gym.modules.traffic.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TrafficRecommendationVo {
    private Long areaId;
    private String areaName;
    private String location;
    private Integer currentCount;
    private Integer capacity;
    private Integer occupancyPercent;
    private Integer score;
    private Integer priority;
    private String statusLabel;
    private String bestFor;
    private String reason;
    private String action;
    private Date snapshotTime;
}
