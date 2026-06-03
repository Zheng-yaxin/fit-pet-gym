package com.gym.modules.training.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class TrainingReviewVo {
    private Long memberId;
    private Integer todayMinutes;
    private Integer todayCalories;
    private Integer todaySessions;
    private Integer weeklyMinutes;
    private Integer weeklyCalories;
    private Integer weeklySessions;
    private Integer totalSessions;
    private Integer streakDays;
    private Integer xpEarned;
    private Integer level;
    private Integer progressPercent;
    private String badgeTitle;
    private String mood;
    private String review;
    private String nextAction;
    private List<String> suggestions;
}
