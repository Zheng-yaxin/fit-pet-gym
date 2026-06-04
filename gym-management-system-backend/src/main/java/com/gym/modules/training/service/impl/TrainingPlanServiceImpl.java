package com.gym.modules.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.modules.exercise.domain.entity.Exercise;
import com.gym.modules.exercise.mapper.ExerciseMapper;
import com.gym.modules.training.domain.dto.TrainingPlanGenerateDTO;
import com.gym.modules.training.domain.entity.TrainingPlan;
import com.gym.modules.training.domain.entity.TrainingPlanDay;
import com.gym.modules.training.domain.entity.TrainingPlanItem;
import com.gym.modules.training.mapper.TrainingPlanDayMapper;
import com.gym.modules.training.mapper.TrainingPlanItemMapper;
import com.gym.modules.training.mapper.TrainingPlanMapper;
import com.gym.modules.training.service.ITrainingPlanService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrainingPlanServiceImpl extends ServiceImpl<TrainingPlanMapper, TrainingPlan> implements ITrainingPlanService {
    @Resource
    private TrainingPlanDayMapper planDayMapper;

    @Resource
    private TrainingPlanItemMapper planItemMapper;

    @Resource
    private ExerciseMapper exerciseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TrainingPlan generate(Long memberId, TrainingPlanGenerateDTO dto) {
        TrainingPlan plan = new TrainingPlan();
        plan.setMemberId(memberId);
        String goal = dto == null || dto.getGoal() == null ? "提升体能" : dto.getGoal();
        Integer weeklyFrequency = dto == null || dto.getWeeklyFrequency() == null ? 3 : dto.getWeeklyFrequency();
        weeklyFrequency = Math.max(1, Math.min(6, weeklyFrequency));
        plan.setGoal(goal);
        plan.setWeeklyFrequency(weeklyFrequency);
        plan.setSource("SYSTEM");
        plan.setStatus("0");
        save(plan);
        createPlanDetails(plan, goal, weeklyFrequency);
        return withDetails(plan);
    }

    @Override
    public TrainingPlan current(Long memberId) {
        TrainingPlan plan = getOne(new LambdaQueryWrapper<TrainingPlan>()
                .eq(TrainingPlan::getMemberId, memberId)
                .eq(TrainingPlan::getStatus, "0")
                .orderByDesc(TrainingPlan::getCreateTime)
                .last("limit 1"));
        return withDetails(plan);
    }

    private void createPlanDetails(TrainingPlan plan, String goal, int weeklyFrequency) {
        List<DayTemplate> templates = templatesFor(goal, weeklyFrequency);
        Map<String, Long> exerciseIds = loadExerciseIds(templates);

        for (int i = 0; i < templates.size(); i++) {
            DayTemplate template = templates.get(i);
            TrainingPlanDay day = new TrainingPlanDay();
            day.setPlanId(plan.getId());
            day.setDayIndex(i + 1);
            day.setTitle(template.title());
            day.setTargetMuscle(template.targetMuscle());
            day.setEstimatedMinutes(template.estimatedMinutes());
            planDayMapper.insert(day);

            List<TrainingPlanItem> items = new ArrayList<>();
            for (int j = 0; j < template.items().size(); j++) {
                ItemTemplate item = template.items().get(j);
                TrainingPlanItem planItem = new TrainingPlanItem();
                planItem.setPlanDayId(day.getId());
                planItem.setExerciseId(exerciseIds.get(item.exerciseName()));
                planItem.setSets(item.sets());
                planItem.setReps(item.reps());
                planItem.setRestSeconds(item.restSeconds());
                planItem.setSortOrder(j + 1);
                planItem.setExerciseName(item.exerciseName());
                planItemMapper.insert(planItem);
                items.add(planItem);
            }
            day.setItems(items);
        }
    }

    private TrainingPlan withDetails(TrainingPlan plan) {
        if (plan == null) {
            return null;
        }

        List<TrainingPlanDay> days = planDayMapper.selectList(new LambdaQueryWrapper<TrainingPlanDay>()
                .eq(TrainingPlanDay::getPlanId, plan.getId())
                .orderByAsc(TrainingPlanDay::getDayIndex));
        if (days.isEmpty()) {
            plan.setDays(days);
            return plan;
        }

        List<Long> dayIds = days.stream().map(TrainingPlanDay::getId).toList();
        List<TrainingPlanItem> items = planItemMapper.selectList(new LambdaQueryWrapper<TrainingPlanItem>()
                .in(TrainingPlanItem::getPlanDayId, dayIds)
                .orderByAsc(TrainingPlanItem::getSortOrder));
        fillExerciseNames(items);

        Map<Long, List<TrainingPlanItem>> itemsByDay = items.stream()
                .collect(Collectors.groupingBy(TrainingPlanItem::getPlanDayId, LinkedHashMap::new, Collectors.toList()));
        days.forEach(day -> day.setItems(itemsByDay.getOrDefault(day.getId(), List.of())));
        plan.setDays(days);
        return plan;
    }

    private void fillExerciseNames(List<TrainingPlanItem> items) {
        List<Long> exerciseIds = items.stream()
                .map(TrainingPlanItem::getExerciseId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (exerciseIds.isEmpty()) {
            return;
        }

        Map<Long, Exercise> exercises = exerciseMapper.selectBatchIds(exerciseIds).stream()
                .collect(Collectors.toMap(Exercise::getId, exercise -> exercise));
        items.forEach(item -> {
            Exercise exercise = exercises.get(item.getExerciseId());
            if (exercise == null) {
                return;
            }
            item.setExerciseName(exercise.getName());
            item.setTargetMuscle(exercise.getTargetMuscle());
            item.setEquipment(exercise.getEquipment());
            item.setDifficulty(exercise.getDifficulty());
            item.setVideoUrl(exercise.getVideoUrl());
            item.setSteps(exercise.getSteps());
            item.setTips(exercise.getTips());
        });
    }

    private Map<String, Long> loadExerciseIds(List<DayTemplate> templates) {
        List<String> names = templates.stream()
                .flatMap(day -> day.items().stream())
                .map(ItemTemplate::exerciseName)
                .distinct()
                .toList();
        Map<String, Long> ids = new LinkedHashMap<>();
        if (names.isEmpty()) {
            return ids;
        }

        List<Exercise> exercises = exerciseMapper.selectList(new LambdaQueryWrapper<Exercise>()
                .in(Exercise::getName, names)
                .eq(Exercise::getStatus, "0"));
        for (Exercise exercise : exercises) {
            ids.put(exercise.getName(), exercise.getId());
        }
        return ids;
    }

    private List<DayTemplate> templatesFor(String goal, int weeklyFrequency) {
        List<DayTemplate> base;
        if (goal.contains("增肌") || goal.contains("力量")) {
            base = hypertrophyTemplates();
        } else if (goal.contains("减脂") || goal.contains("体能")) {
            base = conditioningTemplates();
        } else {
            base = fullBodyTemplates();
        }

        List<DayTemplate> result = new ArrayList<>();
        for (int i = 0; i < weeklyFrequency; i++) {
            DayTemplate source = base.get(i % base.size());
            result.add(new DayTemplate(source.title() + " " + (i + 1), source.targetMuscle(), source.estimatedMinutes(), source.items()));
        }
        return result;
    }

    private List<DayTemplate> fullBodyTemplates() {
        return List.of(
                day("全身力量 A", "全身", 60,
                        item("深蹲", 3, "6-10", 120),
                        item("卧推", 3, "6-10", 120),
                        item("高位下拉", 3, "8-12", 90),
                        item("罗马尼亚硬拉", 3, "8-10", 120),
                        item("平板支撑", 3, "45秒", 60)),
                day("全身力量 B", "全身", 60,
                        item("硬拉", 3, "5", 150),
                        item("站姿推举", 3, "6-8", 120),
                        item("坐姿划船", 3, "8-12", 90),
                        item("腿举", 3, "10-12", 90),
                        item("绳索卷腹", 3, "12-15", 60)),
                day("全身力量 C", "全身", 55,
                        item("保加利亚分腿蹲", 3, "8-10/侧", 90),
                        item("上斜哑铃卧推", 3, "8-12", 90),
                        item("杠铃划船", 3, "8-10", 90),
                        item("臀桥", 3, "10-12", 75),
                        item("农夫走", 3, "30米", 75))
        );
    }

    private List<DayTemplate> hypertrophyTemplates() {
        return List.of(
                day("Push 推举", "胸肩三头", 60,
                        item("卧推", 4, "6-8", 120),
                        item("哑铃肩推", 3, "8-10", 90),
                        item("上斜哑铃卧推", 3, "8-12", 90),
                        item("侧平举", 3, "12-15", 60),
                        item("绳索下压", 3, "10-15", 60)),
                day("Pull 拉力", "背部二头", 60,
                        item("硬拉", 3, "5", 150),
                        item("杠铃划船", 4, "8", 90),
                        item("引体向上", 3, "力竭前2次", 90),
                        item("面拉", 3, "12-15", 60),
                        item("杠铃弯举", 3, "10-12", 60)),
                day("Legs 腿臀", "腿臀核心", 65,
                        item("深蹲", 4, "6-8", 150),
                        item("腿举", 3, "12", 90),
                        item("罗马尼亚硬拉", 3, "8-10", 120),
                        item("腿弯举", 3, "12", 75),
                        item("小腿提踵", 4, "12-15", 60)),
                day("Upper Pump", "上肢容量", 55,
                        item("俯卧撑", 3, "10-15", 60),
                        item("坐姿划船", 3, "10-12", 75),
                        item("反向飞鸟", 3, "12-15", 60),
                        item("锤式弯举", 3, "10-12", 60),
                        item("双杠臂屈伸", 3, "8-12", 75))
        );
    }

    private List<DayTemplate> conditioningTemplates() {
        return List.of(
                day("代谢全身", "全身体能", 50,
                        item("深蹲", 3, "10", 75),
                        item("俯卧撑", 3, "10-15", 60),
                        item("坐姿划船", 3, "12", 60),
                        item("弓步蹲", 3, "10/侧", 60),
                        item("农夫走", 4, "30米", 60)),
                day("下肢燃脂", "腿臀核心", 50,
                        item("腿举", 3, "12-15", 75),
                        item("保加利亚分腿蹲", 3, "10/侧", 75),
                        item("臀桥", 3, "12-15", 60),
                        item("小腿提踵", 3, "15", 45),
                        item("绳索卷腹", 3, "12-15", 45)),
                day("上肢循环", "上肢体能", 45,
                        item("高位下拉", 3, "12", 60),
                        item("哑铃肩推", 3, "10-12", 60),
                        item("俯卧撑", 3, "力竭前2次", 60),
                        item("侧平举", 3, "15", 45),
                        item("平板支撑", 3, "45秒", 45)),
                day("臀腿核心", "臀腿核心", 55,
                        item("杠铃臀推", 4, "8-10", 90),
                        item("罗马尼亚硬拉", 3, "10", 90),
                        item("腿屈伸", 3, "12-15", 60),
                        item("腿弯举", 3, "12-15", 60),
                        item("绳索卷腹", 3, "12-15", 45))
        );
    }

    private DayTemplate day(String title, String targetMuscle, int estimatedMinutes, ItemTemplate... items) {
        return new DayTemplate(title, targetMuscle, estimatedMinutes, List.of(items));
    }

    private ItemTemplate item(String exerciseName, int sets, String reps, int restSeconds) {
        return new ItemTemplate(exerciseName, sets, reps, restSeconds);
    }

    private record DayTemplate(String title, String targetMuscle, Integer estimatedMinutes, List<ItemTemplate> items) {
    }

    private record ItemTemplate(String exerciseName, Integer sets, String reps, Integer restSeconds) {
    }
}
