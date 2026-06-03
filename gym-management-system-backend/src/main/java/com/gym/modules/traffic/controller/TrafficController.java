package com.gym.modules.traffic.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gym.common.result.R;
import com.gym.modules.traffic.domain.entity.GymArea;
import com.gym.modules.traffic.domain.entity.TrafficSnapshot;
import com.gym.modules.traffic.domain.vo.TrafficRecommendationVo;
import com.gym.modules.traffic.service.IGymAreaService;
import com.gym.modules.traffic.service.ITrafficSnapshotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Tag(name = "健身房人流")
@RestController
@RequestMapping("/gym/traffic")
public class TrafficController {
    @Autowired
    private ITrafficSnapshotService snapshotService;

    @Autowired
    private IGymAreaService areaService;

    @GetMapping("/current")
    @Operation(summary = "查询当前人流")
    public R<List<TrafficSnapshot>> current() {
        List<TrafficSnapshot> snapshots = snapshotService.list(new LambdaQueryWrapper<TrafficSnapshot>()
                .orderByDesc(TrafficSnapshot::getSnapshotTime)
                .last("limit 20"));

        // 填充区域名称
        fillAreaNames(snapshots);
        return R.ok(snapshots);
    }

    @GetMapping("/heatmap")
    @Operation(summary = "查询人流热力图")
    public R<List<TrafficSnapshot>> heatmap() {
        List<TrafficSnapshot> snapshots = snapshotService.list(new LambdaQueryWrapper<TrafficSnapshot>()
                .orderByDesc(TrafficSnapshot::getSnapshotTime)
                .last("limit 200"));

        fillAreaNames(snapshots);
        return R.ok(snapshots);
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Build smart area recommendations from live traffic")
    public R<List<TrafficRecommendationVo>> recommendations(
            @org.springframework.web.bind.annotation.RequestParam(name = "goal", required = false) String goal,
            @org.springframework.web.bind.annotation.RequestParam(name = "limit", defaultValue = "5") Integer limit) {
        List<TrafficSnapshot> snapshots = snapshotService.list(new LambdaQueryWrapper<TrafficSnapshot>()
                .orderByDesc(TrafficSnapshot::getSnapshotTime)
                .last("limit 200"));
        List<GymArea> areas = areaService.list();
        Map<Long, GymArea> areaMap = areas.stream()
                .collect(Collectors.toMap(GymArea::getId, area -> area, (a, b) -> a, LinkedHashMap::new));
        Map<Long, TrafficSnapshot> latestByArea = latestSnapshotByArea(snapshots);

        List<TrafficRecommendationVo> recommendations = areaMap.values().stream()
                .map(area -> buildRecommendation(area, latestByArea.get(area.getId()), goal))
                .sorted((a, b) -> {
                    int scoreCompare = Integer.compare(nullToZero(b.getScore()), nullToZero(a.getScore()));
                    if (scoreCompare != 0) {
                        return scoreCompare;
                    }
                    return Integer.compare(nullToZero(a.getOccupancyPercent()), nullToZero(b.getOccupancyPercent()));
                })
                .limit(Math.max(1, Math.min(limit == null ? 5 : limit, 12)))
                .toList();
        return R.ok(recommendations);
    }

    private void fillAreaNames(List<TrafficSnapshot> snapshots) {
        if (snapshots.isEmpty()) return;

        List<Long> areaIds = snapshots.stream()
                .map(TrafficSnapshot::getAreaId)
                .filter(id -> id != null)
                .distinct()
                .toList();

        if (areaIds.isEmpty()) return;

        Map<Long, String> areaNameMap = areaService.listByIds(areaIds).stream()
                .collect(Collectors.toMap(GymArea::getId, GymArea::getName, (a, b) -> a));

        snapshots.forEach(s -> {
            if (s.getAreaId() != null) {
                s.setAreaName(areaNameMap.getOrDefault(s.getAreaId(), "未知区域"));
            }
        });
    }

    private Map<Long, TrafficSnapshot> latestSnapshotByArea(List<TrafficSnapshot> snapshots) {
        Map<Long, TrafficSnapshot> latest = new LinkedHashMap<>();
        for (TrafficSnapshot snapshot : snapshots) {
            if (snapshot.getAreaId() == null || latest.containsKey(snapshot.getAreaId())) {
                continue;
            }
            latest.put(snapshot.getAreaId(), snapshot);
        }
        return latest;
    }

    private TrafficRecommendationVo buildRecommendation(GymArea area, TrafficSnapshot snapshot, String goal) {
        int capacity = firstPositive(snapshot == null ? null : snapshot.getCapacity(), area.getCapacity(), 30);
        int currentCount = Math.max(0, snapshot == null || snapshot.getCurrentCount() == null ? 0 : snapshot.getCurrentCount());
        int occupancy = capacity <= 0 ? 0 : Math.min(100, currentCount * 100 / capacity);
        String areaName = area.getName() == null ? "Training area" : area.getName();
        String normalizedGoal = goal == null ? "" : goal.toLowerCase();
        String areaText = (areaName + " " + (area.getLocation() == null ? "" : area.getLocation())).toLowerCase();

        int goalFit = goalFitScore(normalizedGoal, areaText);
        int crowdFit = Math.max(0, 100 - occupancy);
        int score = Math.min(100, Math.round(crowdFit * 0.7f + goalFit * 0.3f));

        TrafficRecommendationVo vo = new TrafficRecommendationVo();
        vo.setAreaId(area.getId());
        vo.setAreaName(areaName);
        vo.setLocation(area.getLocation());
        vo.setCurrentCount(currentCount);
        vo.setCapacity(capacity);
        vo.setOccupancyPercent(occupancy);
        vo.setScore(score);
        vo.setPriority(resolvePriority(score, occupancy));
        vo.setStatusLabel(resolveStatus(occupancy));
        vo.setBestFor(resolveBestFor(normalizedGoal, areaText));
        vo.setReason(resolveReason(occupancy, goalFit));
        vo.setAction(resolveAction(occupancy, normalizedGoal));
        vo.setSnapshotTime(snapshot == null ? null : snapshot.getSnapshotTime());
        return vo;
    }

    private int goalFitScore(String goal, String areaText) {
        if (goal.contains("fat") || goal.contains("cardio") || goal.contains("conditioning") || goal.contains("endurance")
                || goal.contains("\u51cf\u8102") || goal.contains("\u4f53\u80fd")) {
            return areaText.contains("cardio") || areaText.contains("aerobic") || areaText.contains("\u6709\u6c27") ? 100 : 68;
        }
        if (goal.contains("strength") || goal.contains("muscle") || goal.contains("hypertrophy")
                || goal.contains("\u589e\u808c") || goal.contains("\u529b\u91cf")) {
            return areaText.contains("strength") || areaText.contains("weight") || areaText.contains("free")
                    || areaText.contains("\u529b\u91cf") || areaText.contains("\u81ea\u7531") ? 100 : 70;
        }
        if (goal.contains("stretch") || goal.contains("recover") || goal.contains("mobility")
                || goal.contains("\u62c9\u4f38") || goal.contains("\u6062\u590d")) {
            return areaText.contains("stretch") || areaText.contains("yoga")
                    || areaText.contains("\u62c9\u4f38") || areaText.contains("\u745c\u4f3d") ? 100 : 72;
        }
        return 82;
    }

    private String resolveBestFor(String goal, String areaText) {
        if (areaText.contains("cardio") || areaText.contains("aerobic") || areaText.contains("\u6709\u6c27")) {
            return "Cardio block";
        }
        if (areaText.contains("strength") || areaText.contains("weight") || areaText.contains("free")
                || areaText.contains("\u529b\u91cf") || areaText.contains("\u81ea\u7531")) {
            return "Strength block";
        }
        if (areaText.contains("stretch") || areaText.contains("yoga")
                || areaText.contains("\u62c9\u4f38") || areaText.contains("\u745c\u4f3d")) {
            return "Mobility block";
        }
        if (goal.contains("fat") || goal.contains("conditioning") || goal.contains("\u51cf\u8102")) {
            return "Short conditioning";
        }
        return "General training";
    }

    private String resolveReason(int occupancy, int goalFit) {
        if (occupancy >= 80) {
            return "High crowd pressure; use only if this area is essential.";
        }
        if (occupancy <= 35 && goalFit >= 90) {
            return "Low occupancy and a strong match for the current goal.";
        }
        if (occupancy <= 50) {
            return "Comfortable traffic level; good for focused training.";
        }
        return "Moderate traffic; keep the session short or use alternates.";
    }

    private String resolveAction(int occupancy, String goal) {
        if (occupancy >= 80) {
            return "Delay 20 minutes or choose the next recommended area.";
        }
        if (goal.contains("strength") || goal.contains("muscle") || goal.contains("\u589e\u808c") || goal.contains("\u529b\u91cf")) {
            return "Start with compound lifts, then move to accessories.";
        }
        if (goal.contains("fat") || goal.contains("cardio") || goal.contains("conditioning")
                || goal.contains("\u51cf\u8102") || goal.contains("\u4f53\u80fd")) {
            return "Run a 20-minute interval block while traffic is manageable.";
        }
        return "Use this area for the next main training block.";
    }

    private String resolveStatus(int occupancy) {
        if (occupancy >= 80) {
            return "Crowded";
        }
        if (occupancy >= 55) {
            return "Busy";
        }
        if (occupancy >= 25) {
            return "Comfortable";
        }
        return "Open";
    }

    private int resolvePriority(int score, int occupancy) {
        if (score >= 82 && occupancy <= 50) {
            return 1;
        }
        if (occupancy >= 80) {
            return 3;
        }
        return 2;
    }

    private int firstPositive(Integer first, Integer second, int fallback) {
        if (first != null && first > 0) {
            return first;
        }
        if (second != null && second > 0) {
            return second;
        }
        return fallback;
    }

    private int nullToZero(Integer value) {
        return value == null ? 0 : value;
    }
}
