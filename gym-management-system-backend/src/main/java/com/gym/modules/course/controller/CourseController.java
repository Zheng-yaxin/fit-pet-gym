package com.gym.modules.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.PageResult;
import com.gym.common.result.R;
import com.gym.modules.course.domain.entity.Course;
import com.gym.modules.course.service.ICourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "课程管理")
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private ICourseService courseService;

    @Operation(summary = "分页查询课程列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('course:course:list')")
    public R<PageResult<Course>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "keyword", required = false) String keyword) {

        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Course::getName, keyword);
        }

        wrapper.eq(Course::getDeleted, 0)
                .orderByDesc(Course::getCreateTime);

        courseService.page(page, wrapper);

        return R.ok(new PageResult<>(page.getRecords(), page.getTotal()));
    }

    @Operation(summary = "查询所有课程")
    @GetMapping("/all")
    public R<List<Course>> getAllCourses() {
        // 通常此接口用于下拉列表，可能需要放宽权限或仅限登录用户
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getDeleted, 0)
                .eq(Course::getStatus, "0")
                .orderByDesc(Course::getCreateTime);

        List<Course> list = courseService.list(wrapper);
        return R.ok(list);
    }

    @Operation(summary = "根据ID获取课程详情")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('course:course:query')")
    public R<Course> getInfo(@PathVariable("id") Long id) {
        Course course = courseService.getById(id);
        if (course == null || course.getDeleted() == 1) {
            return R.fail("课程不存在");
        }
        return R.ok(course);
    }

    @Operation(summary = "新增课程")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('course:course:add')")
    public R<Void> add(@RequestBody Course course) {
        course.setCreateTime(new Date());
        course.setUpdateTime(new Date());
        course.setDeleted(0);
        course.setStatus("0");

        if (courseService.save(course)) {
            return R.ok();
        }
        return R.fail("新增失败");
    }

    @Operation(summary = "修改课程")
    @PutMapping
    @PreAuthorize("@ss.hasPermi('course:course:edit')")
    public R<Void> update(@RequestBody Course course) {
        course.setUpdateTime(new Date());
        if (courseService.updateById(course)) {
            return R.ok();
        }
        return R.fail("修改失败");
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('course:course:remove')")
    public R<Void> delete(@PathVariable("id") Long id) {
        if (courseService.removeById(id)) {
            return R.ok();
        }
        return R.fail("删除失败");
    }
}
