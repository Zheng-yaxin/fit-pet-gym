package com.gym.modules.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.core.domain.model.LoginUser;
import com.gym.common.enums.UserType;
import com.gym.common.result.PageResult;
import com.gym.common.result.R;
import com.gym.modules.course.domain.dto.EnrollmentDto;
import com.gym.modules.course.domain.dto.EnrollmentQueryDto;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.course.domain.entity.Course;
import com.gym.modules.course.domain.vo.EnrollmentDetailVo;
import com.gym.modules.course.domain.vo.ScheduleDetailVo;
import com.gym.modules.coach.service.ICoachService;
import com.gym.modules.course.service.ICourseEnrollmentService;
import com.gym.modules.course.service.ICourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员端课程接口
 */
@Tag(name = "会员端-课程预约")
@RestController
@RequestMapping("/member/course")
public class MemberCourseController {

    @Autowired
    private ICourseEnrollmentService enrollmentService;
    
    @Autowired
    private ICourseService courseService;
    
    @Autowired
    private ICoachService coachService;

    /**
     * 获取当前登录会员ID
     */
    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            if (UserType.MEMBER.equals(loginUser.getUserType())) {
                return loginUser.getUserId();
            }
        }
        return null;
    }

    @Operation(summary = "获取所有课程列表")
    @GetMapping("/list")
    public R<List<Course>> getCourseList() {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getDeleted, 0)
               .eq(Course::getStatus, "0")
               .orderByDesc(Course::getCreateTime);
        return R.ok(courseService.list(wrapper));
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

    @Operation(summary = "获取所有教练列表")
    @GetMapping("/coaches")
    public R<List<Coach>> getCoachList() {
        LambdaQueryWrapper<Coach> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coach::getDeleted, 0)
               .eq(Coach::getStatus, "0")
               .orderByDesc(Coach::getCreateTime);
        return R.ok(coachService.list(wrapper));
    }

    @Operation(summary = "获取可预约的课表列表")
    @GetMapping("/schedules")
    public R<List<ScheduleDetailVo>> getAvailableSchedules(
            @RequestParam(name = "courseId", required = false) Long courseId,
            @RequestParam(name = "coachId", required = false) Long coachId) {
        Long memberId = getCurrentMemberId();
        List<ScheduleDetailVo> list = enrollmentService.getAvailableSchedules(memberId, courseId, coachId);
        return R.ok(list);
    }

    @Operation(summary = "获取课表详情")
    @GetMapping("/schedule/{id}")
    public R<ScheduleDetailVo> getScheduleDetail(@PathVariable("id") Long id) {
        Long memberId = getCurrentMemberId();
        ScheduleDetailVo detail = enrollmentService.getScheduleDetail(id, memberId);
        if (detail == null) {
            return R.fail("课表不存在");
        }
        return R.ok(detail);
    }

    @Operation(summary = "预约课程")
    @PostMapping("/enroll")
    public R<Void> enrollCourse(@RequestBody EnrollmentDto dto) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return R.fail("请先登录");
        }
        if (enrollmentService.enrollCourse(memberId, dto.getScheduleId())) {
            return R.ok();
        }
        return R.fail("预约失败");
    }

    @Operation(summary = "取消预约")
    @PostMapping("/cancel/{enrollmentId}")
    public R<Void> cancelEnrollment(@PathVariable("enrollmentId") Long enrollmentId) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return R.fail("请先登录");
        }
        if (enrollmentService.cancelEnrollment(memberId, enrollmentId)) {
            return R.ok();
        }
        return R.fail("取消失败");
    }

    @Operation(summary = "我的预约列表")
    @GetMapping("/my-enrollments")
    public R<PageResult<EnrollmentDetailVo>> getMyEnrollments(EnrollmentQueryDto queryDto) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return R.fail("请先登录");
        }
        Page<EnrollmentDetailVo> page = enrollmentService.getMemberEnrollments(memberId, queryDto);
        return R.ok(new PageResult<>(page.getRecords(), page.getTotal()));
    }

    @Operation(summary = "检查是否已预约某课表")
    @GetMapping("/check-enrolled/{scheduleId}")
    public R<Boolean> checkEnrolled(@PathVariable("scheduleId") Long scheduleId) {
        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return R.ok(false);
        }
        return R.ok(enrollmentService.isEnrolled(memberId, scheduleId));
    }
}
