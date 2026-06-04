package com.gym.modules.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.PageResult;
import com.gym.common.result.R;
import com.gym.modules.course.domain.dto.EnrollmentQueryDto;
import com.gym.modules.course.domain.entity.ClassSchedule;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.course.domain.entity.Course;
import com.gym.modules.course.domain.vo.EnrollmentDetailVo;
import com.gym.modules.course.domain.vo.EnrollmentStatsVo;
import com.gym.modules.course.domain.vo.ScheduleDetailVo;
import com.gym.modules.course.service.IClassScheduleService;
import com.gym.modules.coach.service.ICoachService;
import com.gym.modules.course.service.ICourseEnrollmentService;
import com.gym.modules.course.service.ICourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员端课程管理接口
 */
@Tag(name = "管理员端-课程管理")
@RestController
@RequestMapping("/admin/course")
public class AdminCourseController {

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ICoachService coachService;

    @Autowired
    private IClassScheduleService scheduleService;

    @Autowired
    private ICourseEnrollmentService enrollmentService;

    // ==================== 课程管理 ====================

    @Operation(summary = "分页查询课程列表")
    @GetMapping("/list")
    public R<PageResult<Course>> getCourseList(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status) {

        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Course::getName, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Course::getStatus, status);
        }

        wrapper.eq(Course::getDeleted, 0)
                .orderByDesc(Course::getCreateTime);

        courseService.page(page, wrapper);
        return R.ok(new PageResult<>(page.getRecords(), page.getTotal()));
    }

    @Operation(summary = "获取课程详情")
    @GetMapping("/{id}")
    public R<Course> getCourseDetail(@PathVariable("id") Long id) {
        Course course = courseService.getById(id);
        if (course == null || course.getDeleted() == 1) {
            return R.fail("课程不存在");
        }
        return R.ok(course);
    }

    @Operation(summary = "新增课程")
    @PostMapping
    public R<Void> addCourse(@RequestBody Course course) {
        course.setCreateTime(new Date());
        course.setUpdateTime(new Date());
        course.setDeleted(0);
        if (course.getStatus() == null) {
            course.setStatus("0");
        }
        if (courseService.save(course)) {
            return R.ok();
        }
        return R.fail("新增失败");
    }

    @Operation(summary = "修改课程")
    @PutMapping
    public R<Void> updateCourse(@RequestBody Course course) {
        course.setUpdateTime(new Date());
        if (courseService.updateById(course)) {
            return R.ok();
        }
        return R.fail("修改失败");
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public R<Void> deleteCourse(@PathVariable("id") Long id) {
        if (courseService.removeById(id)) {
            return R.ok();
        }
        return R.fail("删除失败");
    }

    @Operation(summary = "获取所有课程（下拉选择用）")
    @GetMapping("/all")
    public R<List<Course>> getAllCourses() {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getDeleted, 0)
                .eq(Course::getStatus, "0");
        return R.ok(courseService.list(wrapper));
    }

    // ==================== 教练管理 ====================

    @Operation(summary = "分页查询教练列表")
    @GetMapping("/coach/list")
    public R<PageResult<Coach>> getCoachList(
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

    @Operation(summary = "获取所有教练（下拉选择用）")
    @GetMapping("/coach/all")
    public R<List<Coach>> getAllCoaches() {
        LambdaQueryWrapper<Coach> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coach::getDeleted, 0)
                .eq(Coach::getStatus, "0");
        return R.ok(coachService.list(wrapper));
    }

    @Operation(summary = "新增教练")
    @PostMapping("/coach")
    public R<Void> addCoach(@RequestBody Coach coach) {
        coach.setCreateTime(new Date());
        coach.setUpdateTime(new Date());
        coach.setDeleted(0);
        if (coach.getStatus() == null) {
            coach.setStatus("0");
        }
        if (coachService.save(coach)) {
            return R.ok();
        }
        return R.fail("新增失败");
    }

    @Operation(summary = "修改教练")
    @PutMapping("/coach")
    public R<Void> updateCoach(@RequestBody Coach coach) {
        coach.setUpdateTime(new Date());
        if (coachService.updateById(coach)) {
            return R.ok();
        }
        return R.fail("修改失败");
    }

    @Operation(summary = "删除教练")
    @DeleteMapping("/coach/{id}")
    public R<Void> deleteCoach(@PathVariable("id") Long id) {
        if (coachService.removeById(id)) {
            return R.ok();
        }
        return R.fail("删除失败");
    }

    // ==================== 课表管理 ====================

    @Operation(summary = "分页查询课表列表")
    @GetMapping("/schedule/list")
    public R<PageResult<ScheduleDetailVo>> getScheduleList(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "courseId", required = false) Long courseId,
            @RequestParam(name = "coachId", required = false) Long coachId,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        Page<ClassSchedule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();

        if (courseId != null) {
            wrapper.eq(ClassSchedule::getCourseId, courseId);
        }
        if (coachId != null) {
            wrapper.eq(ClassSchedule::getCoachId, coachId);
        }
        if (startDate != null) {
            wrapper.ge(ClassSchedule::getClassTime, startDate);
        }
        if (endDate != null) {
            wrapper.le(ClassSchedule::getClassTime, endDate);
        }

        wrapper.eq(ClassSchedule::getDeleted, 0)
                .orderByDesc(ClassSchedule::getClassTime);

        scheduleService.page(page, wrapper);

        // 关键修改：转换实体对象为VO，并填充关联名称
        List<ScheduleDetailVo> voList = page.getRecords().stream().map(schedule -> {
            ScheduleDetailVo vo = new ScheduleDetailVo();
            vo.setId(schedule.getId());
            vo.setCourseId(schedule.getCourseId());
            vo.setCoachId(schedule.getCoachId());
            vo.setClassTime(schedule.getClassTime());

            // ==================== 修复逻辑开始 ====================
            // 补充缺失的字段映射
            vo.setDayOfWeek(schedule.getDayOfWeek());
            if (schedule.getDayOfWeek() != null) {
                String[] weekDays = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
                if (schedule.getDayOfWeek() >= 1 && schedule.getDayOfWeek() <= 7) {
                    vo.setDayOfWeekName(weekDays[schedule.getDayOfWeek()]);
                }
            }
            vo.setStartTime(schedule.getStartTime());
            vo.setEndTime(schedule.getEndTime());
            vo.setEffectiveDate(schedule.getEffectiveDate());
            vo.setExpiryDate(schedule.getExpiryDate());
            // ==================== 修复逻辑结束 ====================

            vo.setCapacity(schedule.getCapacity());
            vo.setEnrolledCount(schedule.getEnrolledCount());

            // 查询并设置课程名称
            Course course = courseService.getById(schedule.getCourseId());
            if (course != null) {
                vo.setCourseName(course.getName());
            }

            // 查询并设置教练名称
            Coach coach = coachService.getById(schedule.getCoachId());
            if (coach != null) {
                vo.setCoachName(coach.getName());
            }

            return vo;
        }).collect(Collectors.toList());

        return R.ok(new PageResult<>(voList, page.getTotal()));
    }

    @Operation(summary = "获取课表详情（含课程和教练信息）")
    @GetMapping("/schedule/{id}")
    public R<ScheduleDetailVo> getScheduleDetail(@PathVariable("id") Long id) {
        ScheduleDetailVo detail = enrollmentService.getScheduleDetail(id, null);
        if (detail == null) {
            return R.fail("课表不存在");
        }
        return R.ok(detail);
    }

    @Operation(summary = "新增课表")
    @PostMapping("/schedule")
    public R<Void> addSchedule(@RequestBody ClassSchedule schedule) {
        schedule.setCreateTime(new Date());
        schedule.setUpdateTime(new Date());
        schedule.setDeleted(0);
        schedule.setEnrolledCount(0);

        // 处理数据库 class_time 不能为空的问题
        if (schedule.getClassTime() == null) {
            if (schedule.getEffectiveDate() != null) {
                schedule.setClassTime(schedule.getEffectiveDate());
                if (StringUtils.hasText(schedule.getStartTime())) {
                    try {
                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
                        String dateStr = sdfDate.format(schedule.getEffectiveDate());
                        String timeStr = schedule.getStartTime();
                        if (timeStr.length() == 5) {
                            timeStr += ":00";
                        }
                        String fullDateTime = dateStr + " " + timeStr;
                        SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        schedule.setClassTime(sdfFull.parse(fullDateTime));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                schedule.setClassTime(new Date());
            }
        }

        if (scheduleService.save(schedule)) {
            return R.ok();
        }
        return R.fail("新增失败");
    }

    @Operation(summary = "修改课表")
    @PutMapping("/schedule")
    public R<Void> updateSchedule(@RequestBody ClassSchedule schedule) {
        schedule.setUpdateTime(new Date());
        if (scheduleService.updateById(schedule)) {
            return R.ok();
        }
        return R.fail("修改失败");
    }

    @Operation(summary = "删除课表")
    @DeleteMapping("/schedule/{id}")
    public R<Void> deleteSchedule(@PathVariable("id") Long id) {
        if (scheduleService.removeById(id)) {
            return R.ok();
        }
        return R.fail("删除失败");
    }

    // ==================== 预约管理 ====================

    @Operation(summary = "分页查询预约记录")
    @GetMapping("/enrollment/list")
    public R<PageResult<EnrollmentDetailVo>> getEnrollmentList(EnrollmentQueryDto queryDto) {
        Page<EnrollmentDetailVo> page = enrollmentService.getEnrollmentList(queryDto);
        return R.ok(new PageResult<>(page.getRecords(), page.getTotal()));
    }

    @Operation(summary = "更新预约状态")
    @PutMapping("/enrollment/{id}/status")
    public R<Void> updateEnrollmentStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {
        if (enrollmentService.updateEnrollmentStatus(id, status)) {
            return R.ok();
        }
        return R.fail("更新失败");
    }

    @Operation(summary = "获取预约统计数据")
    @GetMapping("/enrollment/stats")
    public R<EnrollmentStatsVo> getEnrollmentStats() {
        return R.ok(enrollmentService.getEnrollmentStats());
    }
}
