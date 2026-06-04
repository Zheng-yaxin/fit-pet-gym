package com.gym.modules.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.PageResult;
import com.gym.common.result.R;
import com.gym.modules.course.domain.entity.ClassSchedule;
import com.gym.modules.course.domain.entity.Course;
import com.gym.modules.course.domain.entity.CourseEnrollment;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.course.domain.vo.EnrollmentDetailVo;
import com.gym.modules.course.domain.vo.ScheduleDetailVo;
import com.gym.modules.course.mapper.CourseEnrollmentMapper;
import com.gym.modules.course.service.IClassScheduleService;
import com.gym.modules.course.service.ICourseEnrollmentService;
import com.gym.modules.course.service.ICourseService;
import com.gym.modules.coach.service.ICoachService;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.member.mapper.MemberMapper;
import com.gym.common.utils.SecurityUtils;
import com.gym.common.core.domain.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教练端课程接口
 */
@Tag(name = "教练端-我的课程")
@RestController
@RequestMapping("/coach/my")
public class CoachCourseController {

    @Autowired
    private IClassScheduleService scheduleService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ICoachService coachService;

    @Autowired
    private ICourseEnrollmentService enrollmentService;

    @Autowired
    private CourseEnrollmentMapper enrollmentMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Operation(summary = "获取当前登录教练信息")
    @GetMapping("/info")
    public R<Coach> getCurrentCoachInfo() {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null) {
                return R.fail("未登录");
            }
            if (loginUser.getCoach() == null) {
                return R.fail("非教练用户");
            }
            return R.ok(loginUser.getCoach());
        } catch (Exception e) {
            return R.fail("获取用户信息失败");
        }
    }

    @Operation(summary = "获取当前登录教练的课程列表")
    @GetMapping("/schedules")
    public R<List<ScheduleDetailVo>> getMySchedules() {
        try {
            LoginUser loginUser = (LoginUser) SecurityUtils.getAuthentication().getPrincipal();
            if (loginUser == null || loginUser.getCoach() == null) {
                return R.fail("未登录或非教练用户");
            }
            Long coachId = loginUser.getCoach().getId();

            // 查询该教练的所有课表
            LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ClassSchedule::getCoachId, coachId)
                    .eq(ClassSchedule::getDeleted, 0)
                    .ge(ClassSchedule::getClassTime, new Date()) // 只查询未来的课程
                    .orderByAsc(ClassSchedule::getClassTime);

            List<ClassSchedule> schedules = scheduleService.list(wrapper);

            List<ScheduleDetailVo> result = schedules.stream().map(schedule -> {
                ScheduleDetailVo vo = new ScheduleDetailVo();
                vo.setId(schedule.getId());
                vo.setCourseId(schedule.getCourseId());
                vo.setCoachId(schedule.getCoachId());
                vo.setClassTime(schedule.getClassTime());
                vo.setCapacity(schedule.getCapacity());
                vo.setEnrolledCount(schedule.getEnrolledCount());
                vo.setRemainingSlots(schedule.getCapacity() - schedule.getEnrolledCount());

                // ================= 新增字段映射 =================
                vo.setDayOfWeek(schedule.getDayOfWeek());
                vo.setStartTime(schedule.getStartTime());
                vo.setEndTime(schedule.getEndTime());
                vo.setEffectiveDate(schedule.getEffectiveDate());
                vo.setExpiryDate(schedule.getExpiryDate());

                if (schedule.getDayOfWeek() != null) {
                    String[] weekDays = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
                    if (schedule.getDayOfWeek() >= 1 && schedule.getDayOfWeek() <= 7) {
                        vo.setDayOfWeekName(weekDays[schedule.getDayOfWeek()]);
                    }
                }
                // ===============================================

                // 获取课程信息
                Course course = courseService.getById(schedule.getCourseId());
                if (course != null) {
                    vo.setCourseName(course.getName());
                    vo.setDuration(course.getDuration());
                    vo.setPrice(course.getPrice());
                }

                return vo;
            }).collect(Collectors.toList());

            return R.ok(result);
        } catch (Exception e) {
            return R.fail("系统异常：" + e.getMessage());
        }
    }

    @Operation(summary = "获取某节课的学员列表")
    @GetMapping("/schedule/{scheduleId}/students")
    public R<List<EnrollmentDetailVo>> getScheduleStudents(@PathVariable("scheduleId") Long scheduleId) {
        // 查询该课表的所有预约记录
        LambdaQueryWrapper<CourseEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseEnrollment::getScheduleId, scheduleId)
                .eq(CourseEnrollment::getDeleted, 0)
                .ne(CourseEnrollment::getStatus, "1"); // 排除已取消的

        List<CourseEnrollment> enrollments = enrollmentService.list(wrapper);

        List<EnrollmentDetailVo> result = new ArrayList<>();
        for (CourseEnrollment enrollment : enrollments) {
            EnrollmentDetailVo vo = new EnrollmentDetailVo();
            vo.setId(enrollment.getId());
            vo.setMemberId(enrollment.getMemberId());
            vo.setScheduleId(enrollment.getScheduleId());
            vo.setStatus(enrollment.getStatus());
            vo.setCreateTime(enrollment.getCreateTime());

            Member member = memberMapper.selectById(enrollment.getMemberId());
            if (member != null) {
                vo.setMemberName(member.getName());
                vo.setMemberPhone(member.getPhone());
            }

            result.add(vo);
        }

        return R.ok(result);
    }

    @Operation(summary = "教练标记学员签到")
    @PostMapping("/checkin/{enrollmentId}")
    public R<Void> checkinStudent(@PathVariable("enrollmentId") Long enrollmentId) {
        CourseEnrollment enrollment = enrollmentService.getById(enrollmentId);
        if (enrollment == null) {
            return R.fail("预约记录不存在");
        }
        enrollment.setStatus("2"); // 已完成/已签到
        enrollment.setUpdateTime(new Date());
        if (enrollmentService.updateById(enrollment)) {
            return R.ok();
        }
        return R.fail("签到失败");
    }

    @Operation(summary = "获取当前登录教练的历史课程")
    @GetMapping("/history")
    public R<PageResult<ScheduleDetailVo>> getHistorySchedules(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            LoginUser loginUser = (LoginUser) SecurityUtils.getAuthentication().getPrincipal();
            if (loginUser == null || loginUser.getCoach() == null) {
                return R.fail("未登录或非教练用户");
            }
            Long coachId = loginUser.getCoach().getId();

            Page<ClassSchedule> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ClassSchedule::getCoachId, coachId)
                    .eq(ClassSchedule::getDeleted, 0)
                    .lt(ClassSchedule::getClassTime, new Date()) // 只查询过去的课程
                    .orderByDesc(ClassSchedule::getClassTime);

            scheduleService.page(page, wrapper);

            List<ScheduleDetailVo> voList = page.getRecords().stream().map(schedule -> {
                ScheduleDetailVo vo = new ScheduleDetailVo();
                vo.setId(schedule.getId());
                vo.setCourseId(schedule.getCourseId());
                vo.setCoachId(schedule.getCoachId());
                vo.setClassTime(schedule.getClassTime());
                vo.setCapacity(schedule.getCapacity());
                vo.setEnrolledCount(schedule.getEnrolledCount());

                // 历史记录也补充字段，虽然主要按日期查，但保持一致性
                vo.setDayOfWeek(schedule.getDayOfWeek());
                vo.setStartTime(schedule.getStartTime());
                vo.setEndTime(schedule.getEndTime());

                if (schedule.getDayOfWeek() != null) {
                    String[] weekDays = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
                    if (schedule.getDayOfWeek() >= 1 && schedule.getDayOfWeek() <= 7) {
                        vo.setDayOfWeekName(weekDays[schedule.getDayOfWeek()]);
                    }
                }

                Course course = courseService.getById(schedule.getCourseId());
                if (course != null) {
                    vo.setCourseName(course.getName());
                    vo.setDuration(course.getDuration());
                    vo.setPrice(course.getPrice());
                }

                return vo;
            }).collect(Collectors.toList());

            return R.ok(new PageResult<>(voList, page.getTotal()));
        } catch (Exception e) {
            return R.fail("系统异常：" + e.getMessage());
        }
    }
}
