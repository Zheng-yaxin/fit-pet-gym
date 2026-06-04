package com.gym.modules.exercise.controller;

import com.gym.common.result.R;
import com.gym.modules.exercise.domain.entity.Exercise;
import com.gym.modules.exercise.service.IExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理端动作库")
@RestController
@RequestMapping("/admin/exercise")
public class AdminExerciseController {
    @Autowired
    private IExerciseService exerciseService;

    @PostMapping
    @Operation(summary = "新增动作")
    public R<Void> add(@RequestBody Exercise exercise) {
        exercise.setStatus(exercise.getStatus() == null ? "0" : exercise.getStatus());
        return exerciseService.save(exercise) ? R.ok() : R.fail("新增动作失败");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新动作")
    public R<Void> update(@PathVariable("id") Long id, @RequestBody Exercise exercise) {
        exercise.setId(id);
        return exerciseService.updateById(exercise) ? R.ok() : R.fail("更新动作失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除动作")
    public R<Void> delete(@PathVariable("id") Long id) {
        return exerciseService.removeById(id) ? R.ok() : R.fail("删除动作失败");
    }
}
