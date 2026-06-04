// gym-management-system-backend/src/main/java/com/gym/modules/course/controller/CoachController.java
package com.gym.modules.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.PageResult;
import com.gym.common.result.R;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.coach.service.ICoachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "教练管理")
@RestController
@RequestMapping("/coach")
public class CoachController {

    @Autowired
    private ICoachService coachService;

    @Operation(summary = "分页查询教练列表")
    @GetMapping("/list")
    public R<PageResult<Coach>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "keyword", required = false) String keyword) {

        Page<Coach> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Coach> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Coach::getName, keyword)
                   .or()
                   .like(Coach::getPhone, keyword);
        }

        wrapper.eq(Coach::getDeleted, 0)
               .orderByDesc(Coach::getCreateTime);

        coachService.page(page, wrapper);

        return R.ok(new PageResult<>(page.getRecords(), page.getTotal()));
    }

    @Operation(summary = "查询所有教练")
    @GetMapping("/all")
    public R<List<Coach>> getAllCoaches() {
        LambdaQueryWrapper<Coach> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coach::getDeleted, 0)
               .eq(Coach::getStatus, "0")
               .orderByDesc(Coach::getCreateTime);

        List<Coach> list = coachService.list(wrapper);
        return R.ok(list);
    }

    @Operation(summary = "根据ID获取教练详情")
    @GetMapping("/{id}")
    public R<Coach> getInfo(@PathVariable("id") Long id) {
        Coach coach = coachService.getById(id);
        if (coach == null || coach.getDeleted() == 1) {
            return R.fail("教练不存在");
        }
        return R.ok(coach);
    }

    @Operation(summary = "新增教练")
    @PostMapping
    public R<Void> add(@RequestBody Coach coach) {
        coach.setCreateTime(new Date());
        coach.setUpdateTime(new Date());
        coach.setDeleted(0);
        coach.setStatus("0");

        if (coachService.save(coach)) {
            return R.ok();
        }
        return R.fail("新增失败");
    }

    @Operation(summary = "修改教练")
    @PutMapping
    public R<Void> update(@RequestBody Coach coach) {
        coach.setUpdateTime(new Date());
        if (coachService.updateById(coach)) {
            return R.ok();
        }
        return R.fail("修改失败");
    }

    @Operation(summary = "删除教练")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable("id") Long id) {
        if (coachService.removeById(id)) {
            return R.ok();
        }
        return R.fail("删除失败");
    }
}
