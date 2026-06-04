package com.gym.modules.training.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TrainingGrowthVo {
    private Long memberId;
    private Integer totalXp;
    private Integer level;
    private Integer currentLevelXp;
    private Integer nextLevelXp;
    private Integer progressPercent;
    private Integer streakDays;
    private Integer totalSessions;
    private Integer weeklyMinutes;
    private Integer weeklySessions;
    private Date lastTrainingDate;
    private String petMood;
    private String badgeTitle;
    private Integer lastRewardXp;
    private String lastRewardReason;
}
