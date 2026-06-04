package com.gym.modules.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.PageResult;
import com.gym.common.result.R;
import com.gym.modules.course.domain.entity.CourseTemplate;
import com.gym.modules.course.service.ICourseTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "课程模板管理")
@RestController
@RequestMapping("/admin/course/template")
public class CourseTemplateController {
    @Autowired
    private ICourseTemplateService courseTemplateService;

    @GetMapping("/list")
    @Operation(summary = "分页查询课程模板")
    public R<PageResult<CourseTemplate>> list(@RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              @RequestParam(name = "status", required = false) String status,
                                              @RequestParam(name = "courseId", required = false) Long courseId,
                                              @RequestParam(name = "coachId", required = false) Long coachId) {
        Page<CourseTemplate> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CourseTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), CourseTemplate::getStatus, status)
                .eq(courseId != null, CourseTemplate::getCourseId, courseId)
                .eq(coachId != null, CourseTemplate::getCoachId, coachId)
                .orderByDesc(CourseTemplate::getCreateTime);
        courseTemplateService.page(page, wrapper);
        return R.ok(new PageResult<>(page.getRecords(), page.getTotal()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询课程模板详情")
    public R<CourseTemplate> detail(@PathVariable("id") Long id) {
        CourseTemplate template = courseTemplateService.getById(id);
        return template == null ? R.fail("课程模板不存在") : R.ok(template);
    }

    @PostMapping
    @Operation(summary = "新增课程模板")
    public R<Void> add(@RequestBody CourseTemplate template) {
        template.setStatus(template.getStatus() == null ? "0" : template.getStatus());
        return courseTemplateService.save(template) ? R.ok() : R.fail("新增课程模板失败");
    }

    @PutMapping
    @Operation(summary = "更新课程模板")
    public R<Void> update(@RequestBody CourseTemplate template) {
        if (template.getId() == null) {
            return R.fail("课程模板ID不能为空");
        }
        return courseTemplateService.updateById(template) ? R.ok() : R.fail("更新课程模板失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程模板")
    public R<Void> delete(@PathVariable("id") Long id) {
        return courseTemplateService.removeById(id) ? R.ok() : R.fail("删除课程模板失败");
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新课程模板状态")
    public R<Void> updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        CourseTemplate template = new CourseTemplate();
        template.setId(id);
        template.setStatus(status);
        return courseTemplateService.updateById(template) ? R.ok() : R.fail("更新课程模板状态失败");
    }

    @PostMapping("/generate")
    @Operation(summary = "根据模板生成排课")
    public R<Void> generate(@RequestParam(name = "weeks", defaultValue = "4") Integer weeks) {
        courseTemplateService.generateSchedulesFromTemplates(weeks);
        return R.ok();
    }
}
