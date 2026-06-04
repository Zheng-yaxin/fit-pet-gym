package com.gym.modules.feedback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.coach.mapper.CoachMapper;
import com.gym.modules.course.domain.entity.ClassSchedule;
import com.gym.modules.course.domain.entity.Course;
import com.gym.modules.course.domain.entity.CourseEnrollment;
import com.gym.modules.course.mapper.ClassScheduleMapper;
import com.gym.modules.course.mapper.CourseEnrollmentMapper;
import com.gym.modules.course.mapper.CourseMapper;
import com.gym.modules.feedback.domain.entity.CourseFeedback;
import com.gym.modules.feedback.domain.vo.CourseFeedbackPendingVo;
import com.gym.modules.feedback.mapper.CourseFeedbackMapper;
import com.gym.modules.feedback.service.ICourseFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseFeedbackServiceImpl extends ServiceImpl<CourseFeedbackMapper, CourseFeedback> implements ICourseFeedbackService {
    @Autowired
    private CourseEnrollmentMapper enrollmentMapper;

    @Autowired
    private ClassScheduleMapper scheduleMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CoachMapper coachMapper;

    @Override
    public List<CourseFeedbackPendingVo> listPending(Long memberId) {
        List<CourseEnrollment> enrollments = enrollmentMapper.selectList(new LambdaQueryWrapper<CourseEnrollment>()
                .eq(CourseEnrollment::getMemberId, memberId)
                .eq(CourseEnrollment::getStatus, "2")
                .orderByDesc(CourseEnrollment::getUpdateTime));
        if (enrollments.isEmpty()) {
            return List.of();
        }

        Set<Long> feedbackScheduleIds = list(new LambdaQueryWrapper<CourseFeedback>()
                .eq(CourseFeedback::getMemberId, memberId)
                .isNotNull(CourseFeedback::getScheduleId))
                .stream()
                .map(CourseFeedback::getScheduleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<CourseEnrollment> pendingEnrollments = enrollments.stream()
                .filter(enrollment -> enrollment.getScheduleId() != null)
                .filter(enrollment -> !feedbackScheduleIds.contains(enrollment.getScheduleId()))
                .toList();
        if (pendingEnrollments.isEmpty()) {
            return List.of();
        }

        List<Long> scheduleIds = pendingEnrollments.stream().map(CourseEnrollment::getScheduleId).distinct().toList();
        Map<Long, ClassSchedule> schedules = scheduleMapper.selectBatchIds(scheduleIds).stream()
                .collect(Collectors.toMap(ClassSchedule::getId, Function.identity()));
        List<Long> courseIds = schedules.values().stream()
                .map(ClassSchedule::getCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<Long> coachIds = schedules.values().stream()
                .map(ClassSchedule::getCoachId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, Course> courses = courseIds.isEmpty() ? Map.of() : courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));
        Map<Long, Coach> coaches = coachIds.isEmpty() ? Map.of() : coachMapper.selectBatchIds(coachIds).stream()
                .collect(Collectors.toMap(Coach::getId, Function.identity()));

        return pendingEnrollments.stream()
                .map(enrollment -> toPendingVo(enrollment, schedules.get(enrollment.getScheduleId()), courses, coaches))
                .filter(Objects::nonNull)
                .toList();
    }

    private CourseFeedbackPendingVo toPendingVo(
            CourseEnrollment enrollment,
            ClassSchedule schedule,
            Map<Long, Course> courses,
            Map<Long, Coach> coaches
    ) {
        if (schedule == null) {
            return null;
        }

        Course course = courses.get(schedule.getCourseId());
        Coach coach = coaches.get(schedule.getCoachId());
        CourseFeedbackPendingVo vo = new CourseFeedbackPendingVo();
        vo.setEnrollmentId(enrollment.getId());
        vo.setScheduleId(schedule.getId());
        vo.setCourseId(schedule.getCourseId());
        vo.setCoachId(schedule.getCoachId());
        vo.setCourseName(course == null ? null : course.getName());
        vo.setCoachName(coach == null ? null : coach.getName());
        vo.setClassTime(schedule.getClassTime());
        vo.setStartTime(schedule.getStartTime());
        vo.setEndTime(schedule.getEndTime());
        return vo;
    }
}
