package com.gym.modules.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.modules.training.domain.entity.TrainingLog;
import com.gym.modules.training.domain.vo.TrainingReviewVo;
import com.gym.modules.training.mapper.TrainingLogMapper;
import com.gym.modules.training.service.ITrainingGrowthService;
import com.gym.modules.training.service.ITrainingLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrainingLogServiceImpl extends ServiceImpl<TrainingLogMapper, TrainingLog> implements ITrainingLogService {
    private static final int DAILY_TARGET_MINUTES = 30;
    private static final int WEEKLY_TARGET_MINUTES = 150;
    private static final int XP_PER_LEVEL = 500;

    @Autowired
    private ITrainingGrowthService growthService;

    @Override
    public boolean saveWithGrowth(TrainingLog log) {
        boolean saved = save(log);
        if (saved && log.getMemberId() != null) {
            growthService.refreshMemberGrowth(log.getMemberId(), log);
        }
        return saved;
    }

    @Override
    public TrainingReviewVo buildMemberReview(Long memberId) {
        List<TrainingLog> logs = list(new LambdaQueryWrapper<TrainingLog>()
                .eq(TrainingLog::getMemberId, memberId)
                .orderByDesc(TrainingLog::getTrainingDate));
        return buildReview(memberId, logs);
    }

    @Override
    public List<TrainingReviewVo> buildAdminReviews() {
        List<TrainingLog> logs = list(new LambdaQueryWrapper<TrainingLog>()
                .orderByDesc(TrainingLog::getTrainingDate));
        Map<Long, List<TrainingLog>> byMember = logs.stream()
                .filter(log -> log.getMemberId() != null)
                .collect(Collectors.groupingBy(TrainingLog::getMemberId, LinkedHashMap::new, Collectors.toList()));

        return byMember.entrySet().stream()
                .map(entry -> buildReview(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(TrainingReviewVo::getWeeklyMinutes, Comparator.nullsLast(Integer::compareTo)).reversed())
                .collect(Collectors.toList());
    }

    private TrainingReviewVo buildReview(Long memberId, List<TrainingLog> logs) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        List<TrainingLog> safeLogs = logs == null ? List.of() : logs.stream()
                .filter(log -> log.getTrainingDate() != null)
                .collect(Collectors.toList());

        List<TrainingLog> todayLogs = safeLogs.stream()
                .filter(log -> toLocalDate(log.getTrainingDate()).isEqual(today))
                .collect(Collectors.toList());
        List<TrainingLog> weekLogs = safeLogs.stream()
                .filter(log -> {
                    LocalDate date = toLocalDate(log.getTrainingDate());
                    return !date.isBefore(weekStart) && !date.isAfter(today);
                })
                .collect(Collectors.toList());

        int todayMinutes = sumMinutes(todayLogs);
        int todayCalories = sumCalories(todayLogs);
        int weeklyMinutes = sumMinutes(weekLogs);
        int weeklyCalories = sumCalories(weekLogs);
        int xp = safeLogs.stream()
                .mapToInt(log -> value(log.getDurationMinutes()) * 4 + value(log.getCaloriesBurned()) / 2 + value(log.getIntensity()) * 8)
                .sum();
        int level = Math.max(1, xp / XP_PER_LEVEL + 1);
        int progress = Math.min(99, Math.max(0, (xp % XP_PER_LEVEL) * 100 / XP_PER_LEVEL));
        int streakDays = calculateStreakDays(safeLogs, today);

        TrainingReviewVo review = new TrainingReviewVo();
        review.setMemberId(memberId);
        review.setTodayMinutes(todayMinutes);
        review.setTodayCalories(todayCalories);
        review.setTodaySessions(todayLogs.size());
        review.setWeeklyMinutes(weeklyMinutes);
        review.setWeeklyCalories(weeklyCalories);
        review.setWeeklySessions(weekLogs.size());
        review.setTotalSessions(safeLogs.size());
        review.setStreakDays(streakDays);
        review.setXpEarned(xp);
        review.setLevel(level);
        review.setProgressPercent(progress);
        review.setBadgeTitle(resolveBadgeTitle(streakDays, weeklyMinutes, xp));
        review.setMood(resolveMood(todayMinutes, weeklyMinutes));
        review.setReview(resolveReview(todayMinutes, weeklyMinutes, streakDays));
        review.setNextAction(resolveNextAction(todayMinutes, weeklyMinutes));
        review.setSuggestions(resolveSuggestions(todayMinutes, weeklyMinutes, streakDays));
        return review;
    }

    private int calculateStreakDays(List<TrainingLog> logs, LocalDate today) {
        Set<LocalDate> trainedDates = logs.stream()
                .map(TrainingLog::getTrainingDate)
                .filter(Objects::nonNull)
                .map(this::toLocalDate)
                .collect(Collectors.toSet());
        int streak = 0;
        LocalDate cursor = today;
        while (trainedDates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private int sumMinutes(List<TrainingLog> logs) {
        return logs.stream().mapToInt(log -> value(log.getDurationMinutes())).sum();
    }

    private int sumCalories(List<TrainingLog> logs) {
        return logs.stream().mapToInt(log -> value(log.getCaloriesBurned())).sum();
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }

    private String resolveBadgeTitle(int streakDays, int weeklyMinutes, int xp) {
        if (streakDays >= 7) {
            return "一周连训守护者";
        }
        if (weeklyMinutes >= WEEKLY_TARGET_MINUTES) {
            return "本周节奏达成";
        }
        if (xp >= XP_PER_LEVEL) {
            return "训练经验升级";
        }
        return "热身起步徽章";
    }

    private String resolveMood(int todayMinutes, int weeklyMinutes) {
        if (todayMinutes >= DAILY_TARGET_MINUTES && weeklyMinutes >= WEEKLY_TARGET_MINUTES) {
            return "celebrate";
        }
        if (todayMinutes >= DAILY_TARGET_MINUTES) {
            return "training";
        }
        if (weeklyMinutes >= WEEKLY_TARGET_MINUTES) {
            return "steady";
        }
        return todayMinutes > 0 ? "warmup" : "idle";
    }

    private String resolveReview(int todayMinutes, int weeklyMinutes, int streakDays) {
        if (todayMinutes >= DAILY_TARGET_MINUTES && weeklyMinutes >= WEEKLY_TARGET_MINUTES) {
            return "今日训练和本周目标都已达成，可以安排轻恢复保持节奏。";
        }
        if (todayMinutes >= DAILY_TARGET_MINUTES) {
            return "今日训练量达标，本周总量还可以继续补齐。";
        }
        if (streakDays >= 2) {
            return "连续训练节奏不错，今天补一个短训练就能稳住成长线。";
        }
        return "今天还缺一次有效训练，建议先完成 20 到 30 分钟低门槛训练。";
    }

    private String resolveNextAction(int todayMinutes, int weeklyMinutes) {
        if (todayMinutes < 10) {
            return "先做 10 分钟热身和基础力量循环";
        }
        if (todayMinutes < DAILY_TARGET_MINUTES) {
            return "再补 " + (DAILY_TARGET_MINUTES - todayMinutes) + " 分钟即可达成今日训练";
        }
        if (weeklyMinutes < WEEKLY_TARGET_MINUTES) {
            return "本周还差 " + (WEEKLY_TARGET_MINUTES - weeklyMinutes) + " 分钟，下一次安排中等强度训练";
        }
        return "今日转入拉伸、补水和睡眠恢复";
    }

    private List<String> resolveSuggestions(int todayMinutes, int weeklyMinutes, int streakDays) {
        List<String> suggestions = new ArrayList<>();
        if (todayMinutes < DAILY_TARGET_MINUTES) {
            suggestions.add("优先完成 3 组自重深蹲、俯卧撑和划船动作。");
        } else {
            suggestions.add("训练后补充蛋白和水分，避免连续高强度堆叠。");
        }
        if (weeklyMinutes < WEEKLY_TARGET_MINUTES) {
            suggestions.add("把本周剩余训练拆成 2 次短课，完成率会更稳。");
        } else {
            suggestions.add("本周运动量已足够，下一次可降低强度做技术巩固。");
        }
        if (streakDays == 0) {
            suggestions.add("从今天开始重新点亮连续训练，不需要一次练太久。");
        } else {
            suggestions.add("连续训练 " + streakDays + " 天，明天保留 15 分钟轻量活动即可续上节奏。");
        }
        return suggestions;
    }
}
