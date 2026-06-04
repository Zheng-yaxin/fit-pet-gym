package com.gym.modules.exercise.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.PageResult;
import com.gym.common.result.R;
import com.gym.modules.exercise.domain.entity.Exercise;
import com.gym.modules.exercise.domain.entity.ExerciseAlternative;
import com.gym.modules.exercise.service.IExerciseAlternativeService;
import com.gym.modules.exercise.service.IExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "动作教学库")
@RestController
@RequestMapping("/exercise")
public class ExerciseController {
    @Autowired
    private IExerciseService exerciseService;

    @Autowired
    private IExerciseAlternativeService alternativeService;

    @GetMapping("/list")
    @Operation(summary = "分页查询动作")
    public R<PageResult<Exercise>> list(@RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                        @RequestParam(name = "keyword", required = false) String keyword,
                                        @RequestParam(name = "targetMuscle", required = false) String targetMuscle,
                                        @RequestParam(name = "difficulty", required = false) String difficulty) {
        Page<Exercise> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Exercise> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), Exercise::getName, keyword)
                .eq(StringUtils.hasText(targetMuscle), Exercise::getTargetMuscle, targetMuscle)
                .eq(StringUtils.hasText(difficulty), Exercise::getDifficulty, difficulty)
                .eq(Exercise::getStatus, "0")
                .orderByDesc(Exercise::getCreateTime);
        exerciseService.page(page, wrapper);
        return R.ok(new PageResult<>(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询动作详情")
    public R<Exercise> detail(@PathVariable("id") Long id) {
        Exercise exercise = exerciseService.getById(id);
        return exercise == null ? R.fail("动作不存在") : R.ok(exercise);
    }

    @GetMapping("/{id}/alternatives")
    @Operation(summary = "查询替代动作")
    public R<List<ExerciseAlternative>> alternatives(@PathVariable("id") Long id) {
        return R.ok(alternativeService.list(new LambdaQueryWrapper<ExerciseAlternative>()
                .eq(ExerciseAlternative::getExerciseId, id)));
    }
}
