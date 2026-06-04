package com.gym.modules.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.modules.training.domain.entity.TrainingGrowth;
import com.gym.modules.training.domain.entity.TrainingLog;
import com.gym.modules.training.domain.vo.TrainingGrowthVo;
import com.gym.modules.training.mapper.TrainingGrowthMapper;
import com.gym.modules.training.mapper.TrainingLogMapper;
import com.gym.modules.training.service.ITrainingGrowthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrainingGrowthServiceImpl extends ServiceImpl<TrainingGrowthMapper, TrainingGrowth> implements ITrainingGrowthService {
    private static final int XP_PER_LEVEL = 500;
    private static final int WEEKLY_TARGET_MINUTES = 150;

    @Autowired
    private TrainingLogMapper trainingLogMapper;

    @Override
    public TrainingGrowthVo getMemberGrowth(Long memberId) {
        return refreshMemberGrowth(memberId, null);
    }

    @Override
    public TrainingGrowthVo refreshMemberGrowth(Long memberId, TrainingLog rewardSource) {
        List<TrainingLog> logs = trainingLogMapper.selectList(new LambdaQueryWrapper<TrainingLog>()
                .eq(TrainingLog::getMemberId, memberId)
                .orderByDesc(TrainingLog::getTrainingDate));
        TrainingGrowth growth = buildGrowth(memberId, logs, rewardSource);
        TrainingGrowth existing = getOne(new LambdaQueryWrapper<TrainingGrowth>()
                .eq(TrainingGrowth::getMemberId, memberId)
                .last("LIMIT 1"), false);
        if (existing == null) {
            save(growth);
        } else {
            growth.setId(existing.getId());
            updateById(growth);
        }
        return toVo(growth);
    }

    @Override
    public List<TrainingGrowthVo> buildAdminGrowth() {
        List<TrainingLog> logs = trainingLogMapper.selectList(new LambdaQueryWrapper<TrainingLog>()
                .orderByDesc(TrainingLog::getTrainingDate));
        Map<Long, List<TrainingLog>> byMember = logs.stream()
                .filter(log -> log.getMemberId() != null)
                .collect(Collectors.groupingBy(TrainingLog::getMemberId, LinkedHashMap::new, Collectors.toList()));

        List<TrainingGrowthVo> refreshed = byMember.entrySet().stream()
                .map(entry -> refreshMemberGrowth(entry.getKey(), null))
                .collect(Collectors.toList());

        Set<Long> refreshedMemberIds = refreshed.stream()
                .map(TrainingGrowthVo::getMemberId)
                .collect(Collectors.toSet());
        List<TrainingGrowthVo> storedOnly = list().stream()
                .filter(growth -> !refreshedMemberIds.contains(growth.getMemberId()))
                .map(this::toVo)
                .collect(Collectors.toList());

        refreshed.addAll(storedOnly);
        return refreshed.stream()
                .sorted(Comparator.comparing(TrainingGrowthVo::getTotalXp, Comparator.nullsLast(Integer::compareTo)).reversed())
                .collect(Collectors.toList());
    }

    private TrainingGrowth buildGrowth(Long memberId, List<TrainingLog> logs, TrainingLog rewardSource) {
        List<TrainingLog> safeLogs = logs == null ? List.of() : logs.stream()
                .filter(log -> log.getTrainingDate() != null)
                .collect(Collectors.toList());
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        List<TrainingLog> weekLogs = safeLogs.stream()
                .filter(log -> {
                    LocalDate date = toLocalDate(log.getTrainingDate());
                    return !date.isBefore(weekStart) && !date.isAfter(today);
                })
                .collect(Collectors.toList());

        int totalXp = safeLogs.stream().mapToInt(this::calculateXp).sum();
        int level = Math.max(1, totalXp / XP_PER_LEVEL + 1);
        int currentLevelXp = Math.max(0, totalXp % XP_PER_LEVEL);
        int progress = Math.min(99, currentLevelXp * 100 / XP_PER_LEVEL);
        int streakDays = calculateStreakDays(safeLogs, today);
        int weeklyMinutes = weekLogs.stream().mapToInt(log -> value(log.getDurationMinutes())).sum();
        Date lastTrainingDate = safeLogs.stream()
                .map(TrainingLog::getTrainingDate)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);

        TrainingGrowth growth = new TrainingGrowth();
        growth.setMemberId(memberId);
        growth.setTotalXp(totalXp);
        growth.setLevel(level);
        growth.setCurrentLevelXp(currentLevelXp);
        growth.setNextLevelXp(XP_PER_LEVEL);
        growth.setProgressPercent(progress);
        growth.setStreakDays(streakDays);
        growth.setTotalSessions(safeLogs.size());
        growth.setWeeklyMinutes(weeklyMinutes);
        growth.setWeeklySessions(weekLogs.size());
        growth.setLastTrainingDate(lastTrainingDate);
        growth.setPetMood(resolvePetMood(streakDays, weeklyMinutes));
        growth.setBadgeTitle(resolveBadgeTitle(streakDays, weeklyMinutes, totalXp));
        growth.setLastRewardXp(rewardSource == null ? 0 : calculateXp(rewardSource));
        growth.setLastRewardReason(rewardSource == null ? "Synced from training logs" : buildRewardReason(rewardSource));
        return growth;
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

    private int calculateXp(TrainingLog log) {
        return value(log.getDurationMinutes()) * 4
                + value(log.getCaloriesBurned()) / 2
                + value(log.getIntensity()) * 8;
    }

    private String buildRewardReason(TrainingLog log) {
        int minutes = value(log.getDurationMinutes());
        if (minutes >= 45) {
            return "Long training session reward";
        }
        if (minutes >= 20) {
            return "Daily training completion reward";
        }
        return "Quick workout reward";
    }

    private String resolvePetMood(int streakDays, int weeklyMinutes) {
        if (streakDays >= 7 || weeklyMinutes >= WEEKLY_TARGET_MINUTES) {
            return "celebrate";
        }
        if (streakDays >= 2) {
            return "steady";
        }
        return weeklyMinutes > 0 ? "training" : "idle";
    }

    private String resolveBadgeTitle(int streakDays, int weeklyMinutes, int totalXp) {
        if (streakDays >= 7) {
            return "Seven-day streak guardian";
        }
        if (weeklyMinutes >= WEEKLY_TARGET_MINUTES) {
            return "Weekly rhythm finisher";
        }
        if (totalXp >= XP_PER_LEVEL) {
            return "Training level-up badge";
        }
        return "Warm-up starter badge";
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }

    private TrainingGrowthVo toVo(TrainingGrowth growth) {
        TrainingGrowthVo vo = new TrainingGrowthVo();
        BeanUtils.copyProperties(growth, vo);
        return vo;
    }
}
