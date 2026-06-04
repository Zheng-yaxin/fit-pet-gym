// gym-management-system-backend/src/main/java/com/gym/modules/course/controller/ScheduleController.java
package com.gym.modules.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.PageResult;
import com.gym.common.result.R;
import com.gym.modules.course.domain.entity.ClassSchedule;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.course.domain.entity.Course;
import com.gym.modules.course.domain.vo.ScheduleDetailVo;
import com.gym.modules.course.service.IClassScheduleService;
import com.gym.modules.coach.service.ICoachService;
import com.gym.modules.course.service.ICourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "课表管理")
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private IClassScheduleService scheduleService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ICoachService coachService;

    @Operation(summary = "分页查询课表")
    @GetMapping("/list")
    public R<PageResult<ScheduleDetailVo>> list(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "dayOfWeek", required = false) Integer dayOfWeek,
            @RequestParam(name = "courseId", required = false) Long courseId,
            @RequestParam(name = "coachId", required = false) Long coachId) {

        Page<ClassSchedule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();

        // 按星期筛选
        if (dayOfWeek != null) {
            wrapper.eq(ClassSchedule::getDayOfWeek, dayOfWeek);
        }

        // 按课程筛选
        if (courseId != null) {
            wrapper.eq(ClassSchedule::getCourseId, courseId);
        }

        // 按教练筛选
        if (coachId != null) {
            wrapper.eq(ClassSchedule::getCoachId, coachId);
        }

        wrapper.eq(ClassSchedule::getDeleted, 0)
               .orderByAsc(ClassSchedule::getDayOfWeek)
               .orderByAsc(ClassSchedule::getStartTime);

        scheduleService.page(page, wrapper);

        // Convert to ScheduleDetailVo with course and coach names
        List<ScheduleDetailVo> voList = page.getRecords().stream().map(schedule -> {
            ScheduleDetailVo vo = new ScheduleDetailVo();
            vo.setId(schedule.getId());
            vo.setCourseId(schedule.getCourseId());
            vo.setCoachId(schedule.getCoachId());
            vo.setClassTime(schedule.getClassTime());
            vo.setDayOfWeek(schedule.getDayOfWeek());
            vo.setDayOfWeekName(getDayOfWeekName(schedule.getDayOfWeek()));
            vo.setStartTime(schedule.getStartTime());
            vo.setEndTime(schedule.getEndTime());
            vo.setEffectiveDate(schedule.getEffectiveDate());
            vo.setExpiryDate(schedule.getExpiryDate());
            vo.setCapacity(schedule.getCapacity());
            vo.setEnrolledCount(schedule.getEnrolledCount());
            vo.setRemainingSlots(schedule.getCapacity() - schedule.getEnrolledCount());

            // Get course name
            Course course = courseService.getById(schedule.getCourseId());
            if (course != null) {
                vo.setCourseName(course.getName());
                vo.setDuration(course.getDuration());
                vo.setPrice(course.getPrice());
            }

            // Get coach name
            Coach coach = coachService.getById(schedule.getCoachId());
            if (coach != null) {
                vo.setCoachName(coach.getName());
            }

            return vo;
        }).collect(Collectors.toList());

        return R.ok(new PageResult<>(voList, page.getTotal()));
    }

    /**
     * 获取星期几的中文名称
     */
    private String getDayOfWeekName(Integer dayOfWeek) {
        if (dayOfWeek == null) {
            return "";
        }
        String[] names = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return dayOfWeek >= 1 && dayOfWeek <= 7 ? names[dayOfWeek] : "";
    }

    @Operation(summary = "根据ID获取课表详情")
    @GetMapping("/{id}")
    public R<ClassSchedule> getInfo(@PathVariable("id") Long id) {
        ClassSchedule schedule = scheduleService.getById(id);
        if (schedule == null || schedule.getDeleted() == 1) {
            return R.fail("课表不存在");
        }
        return R.ok(schedule);
    }

    @Operation(summary = "新增课表")
    @PostMapping
    public R<Void> add(@RequestBody ClassSchedule schedule) {
        // 验证必填字段
        if (schedule.getCourseId() == null || schedule.getCoachId() == null) {
            return R.fail("课程和教练不能为空");
        }

        if (schedule.getDayOfWeek() == null || schedule.getDayOfWeek() < 1 || schedule.getDayOfWeek() > 7) {
            return R.fail("请选择有效的星期");
        }

        if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
            return R.fail("请选择开始和结束时间");
        }

        if (schedule.getEffectiveDate() == null) {
            return R.fail("请选择生效日期");
        }

        // 检查同一教练在同一时间段是否有冲突
        LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassSchedule::getCoachId, schedule.getCoachId())
               .eq(ClassSchedule::getDayOfWeek, schedule.getDayOfWeek())
               .eq(ClassSchedule::getDeleted, 0);

        List<ClassSchedule> existingSchedules = scheduleService.list(wrapper);
        for (ClassSchedule existing : existingSchedules) {
            if (isTimeOverlap(schedule.getStartTime(), schedule.getEndTime(),
                    existing.getStartTime(), existing.getEndTime())) {
                return R.fail("该教练在此时间段已有排课");
            }
        }

        schedule.setCreateTime(new Date());
        schedule.setUpdateTime(new Date());
        schedule.setDeleted(0);
        schedule.setEnrolledCount(0);

        if (scheduleService.save(schedule)) {
            return R.ok();
        }
        return R.fail("新增失败");
    }

    /**
     * 检查两个时间段是否重叠
     */
    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }
        // 时间格式：HH:mm，可以直接字符串比较
        return start1.compareTo(end2) < 0 && end1.compareTo(start2) > 0;
    }

    @Operation(summary = "修改课表")
    @PutMapping
    public R<Void> update(@RequestBody ClassSchedule schedule) {
        schedule.setUpdateTime(new Date());
        if (scheduleService.updateById(schedule)) {
            return R.ok();
        }
        return R.fail("修改失败");
    }

    @Operation(summary = "删除课表")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable("id") Long id) {
        if (scheduleService.removeById(id)) {
            return R.ok();
        }
        return R.fail("删除失败");
    }

    @Operation(summary = "获取未来一周的课程安排")
    @GetMapping("/upcoming")
    public R<List<ClassSchedule>> getUpcomingSchedules() {
        Date now = new Date();
        Date oneWeekLater = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000L);

        LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(ClassSchedule::getClassTime, now, oneWeekLater)
               .eq(ClassSchedule::getDeleted, 0)
               .orderByAsc(ClassSchedule::getClassTime);

        List<ClassSchedule> list = scheduleService.list(wrapper);
        return R.ok(list);
    }
}
