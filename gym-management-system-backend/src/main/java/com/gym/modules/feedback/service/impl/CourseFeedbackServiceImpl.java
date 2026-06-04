package com.gym.modules.feedback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.coach.mapper.CoachMapper;
import com.gym.modules.coach.domain.entity.PersonalTrainingBooking;
import com.gym.modules.coach.mapper.PersonalTrainingBookingMapper;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.course.domain.entity.ClassSchedule;
import com.gym.modules.course.domain.entity.Course;
import com.gym.modules.course.domain.entity.CourseEnrollment;
import com.gym.modules.course.mapper.ClassScheduleMapper;
import com.gym.modules.course.mapper.CourseEnrollmentMapper;
import com.gym.modules.course.mapper.CourseMapper;
import com.gym.modules.feedback.domain.dto.AdminFeedbackHandleDto;
import com.gym.modules.feedback.domain.entity.CourseFeedback;
import com.gym.modules.feedback.domain.vo.AdminFeedbackVo;
import com.gym.modules.feedback.domain.vo.CourseFeedbackPendingVo;
import com.gym.modules.feedback.mapper.CourseFeedbackMapper;
import com.gym.modules.feedback.service.ICourseFeedbackService;
import com.gym.modules.member.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseFeedbackServiceImpl extends ServiceImpl<CourseFeedbackMapper, CourseFeedback> implements ICourseFeedbackService {
    private static final String TYPE_COURSE = "course";
    private static final String TYPE_PERSONAL_TRAINING = "personal_training";
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_HANDLED = "handled";

    @Autowired
    private CourseEnrollmentMapper enrollmentMapper;

    @Autowired
    private ClassScheduleMapper scheduleMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CoachMapper coachMapper;

    @Autowired
    private PersonalTrainingBookingMapper personalTrainingBookingMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public List<CourseFeedbackPendingVo> listPending(Long memberId) {
        List<CourseEnrollment> enrollments = enrollmentMapper.selectList(new LambdaQueryWrapper<CourseEnrollment>()
                .eq(CourseEnrollment::getMemberId, memberId)
                .eq(CourseEnrollment::getStatus, "2")
                .orderByDesc(CourseEnrollment::getUpdateTime));
        Set<Long> feedbackScheduleIds = list(new LambdaQueryWrapper<CourseFeedback>()
                .eq(CourseFeedback::getMemberId, memberId)
                .isNotNull(CourseFeedback::getScheduleId))
                .stream()
                .map(CourseFeedback::getScheduleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> feedbackBookingIds = list(new LambdaQueryWrapper<CourseFeedback>()
                .eq(CourseFeedback::getMemberId, memberId)
                .isNotNull(CourseFeedback::getBookingId))
                .stream()
                .map(CourseFeedback::getBookingId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<CourseEnrollment> pendingEnrollments = enrollments.stream()
                .filter(enrollment -> enrollment.getScheduleId() != null)
                .filter(enrollment -> !feedbackScheduleIds.contains(enrollment.getScheduleId()))
                .toList();
        List<CourseFeedbackPendingVo> coursePending = buildCoursePending(pendingEnrollments);
        List<CourseFeedbackPendingVo> privatePending = buildPersonalTrainingPending(memberId, feedbackBookingIds);

        return java.util.stream.Stream.concat(coursePending.stream(), privatePending.stream()).toList();
    }

    @Override
    public boolean submitMemberFeedback(Long memberId, CourseFeedback feedback) {
        feedback.setMemberId(memberId);
        if (feedback.getBookingId() != null) {
            fillPersonalTrainingFeedback(memberId, feedback);
        } else {
            fillCourseFeedback(memberId, feedback);
        }
        feedback.setHandleStatus(STATUS_PENDING);
        feedback.setFollowUpRequired(feedback.getFollowUpRequired() == null ? 0 : feedback.getFollowUpRequired());
        return save(feedback);
    }

    @Override
    public List<AdminFeedbackVo> listAdminFeedback() {
        List<CourseFeedback> feedbackList = list(new LambdaQueryWrapper<CourseFeedback>()
                .orderByDesc(CourseFeedback::getCreateTime));
        if (feedbackList.isEmpty()) {
            return List.of();
        }

        Map<Long, Member> members = selectMap(feedbackList.stream()
                .map(CourseFeedback::getMemberId)
                .filter(Objects::nonNull)
                .distinct()
                .toList(), ids -> memberMapper.selectBatchIds(ids), Member::getId);
        Map<Long, Course> courses = selectMap(feedbackList.stream()
                .map(CourseFeedback::getCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .toList(), ids -> courseMapper.selectBatchIds(ids), Course::getId);
        Map<Long, Coach> coaches = selectMap(feedbackList.stream()
                .map(CourseFeedback::getCoachId)
                .filter(Objects::nonNull)
                .distinct()
                .toList(), ids -> coachMapper.selectBatchIds(ids), Coach::getId);
        Map<Long, PersonalTrainingBooking> bookings = selectMap(feedbackList.stream()
                .map(CourseFeedback::getBookingId)
                .filter(Objects::nonNull)
                .distinct()
                .toList(), ids -> personalTrainingBookingMapper.selectBatchIds(ids), PersonalTrainingBooking::getId);

        return feedbackList.stream()
                .map(feedback -> toAdminVo(feedback, members, courses, coaches, bookings))
                .toList();
    }

    @Override
    public boolean handleAdminFeedback(Long feedbackId, AdminFeedbackHandleDto dto) {
        CourseFeedback feedback = getById(feedbackId);
        if (feedback == null || feedback.getDeleted() != null && feedback.getDeleted() == 1) {
            throw new ServiceException("Feedback not found");
        }

        String status = dto.getHandleStatus();
        feedback.setHandleStatus(status == null || status.isBlank() ? STATUS_HANDLED : status);
        feedback.setAdminReply(dto.getAdminReply());
        feedback.setFollowUpRequired(dto.getFollowUpRequired() == null ? 0 : dto.getFollowUpRequired());
        feedback.setHandleTime(new Date());
        return updateById(feedback);
    }

    private <T> Map<Long, T> selectMap(List<Long> ids, Function<List<Long>, List<T>> loader, Function<T, Long> idGetter) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return loader.apply(ids).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(idGetter, Function.identity(), (left, right) -> left));
    }

    private AdminFeedbackVo toAdminVo(
            CourseFeedback feedback,
            Map<Long, Member> members,
            Map<Long, Course> courses,
            Map<Long, Coach> coaches,
            Map<Long, PersonalTrainingBooking> bookings
    ) {
        Member member = members.get(feedback.getMemberId());
        Course course = courses.get(feedback.getCourseId());
        Coach coach = coaches.get(feedback.getCoachId());
        PersonalTrainingBooking booking = bookings.get(feedback.getBookingId());

        AdminFeedbackVo vo = new AdminFeedbackVo();
        vo.setId(feedback.getId());
        vo.setMemberId(feedback.getMemberId());
        vo.setMemberName(member == null ? null : member.getName());
        vo.setScheduleId(feedback.getScheduleId());
        vo.setBookingId(feedback.getBookingId());
        vo.setCourseId(feedback.getCourseId());
        vo.setCourseName(course == null ? null : course.getName());
        vo.setCoachId(feedback.getCoachId());
        vo.setCoachName(coach == null ? null : coach.getName());
        vo.setFeedbackType(resolveFeedbackType(feedback));
        vo.setTargetTitle(resolveTargetTitle(feedback, course, coach, booking));
        vo.setRating(feedback.getRating());
        vo.setIntensity(feedback.getIntensity());
        vo.setContent(feedback.getContent());
        vo.setTags(feedback.getTags());
        vo.setHandleStatus(resolveHandleStatus(feedback));
        vo.setAdminReply(feedback.getAdminReply());
        vo.setFollowUpRequired(feedback.getFollowUpRequired());
        vo.setHandleTime(feedback.getHandleTime());
        vo.setCreateTime(feedback.getCreateTime());
        return vo;
    }

    private String resolveHandleStatus(CourseFeedback feedback) {
        if (feedback.getHandleStatus() != null && !feedback.getHandleStatus().isBlank()) {
            return feedback.getHandleStatus();
        }
        return STATUS_PENDING;
    }

    private String resolveFeedbackType(CourseFeedback feedback) {
        if (feedback.getFeedbackType() != null && !feedback.getFeedbackType().isBlank()) {
            return feedback.getFeedbackType();
        }
        return feedback.getBookingId() == null ? TYPE_COURSE : TYPE_PERSONAL_TRAINING;
    }

    private String resolveTargetTitle(CourseFeedback feedback, Course course, Coach coach, PersonalTrainingBooking booking) {
        if (TYPE_PERSONAL_TRAINING.equals(resolveFeedbackType(feedback))) {
            String coachName = coach == null || coach.getName() == null ? "Coach" : coach.getName();
            String date = booking == null || booking.getDate() == null ? "" : " " + booking.getDate();
            String time = booking == null || booking.getStartTime() == null ? "" : " " + booking.getStartTime();
            return "PT with " + coachName + date + time;
        }
        if (course != null && course.getName() != null) {
            return course.getName();
        }
        return "Course feedback";
    }

    private List<CourseFeedbackPendingVo> buildCoursePending(List<CourseEnrollment> pendingEnrollments) {
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

    private List<CourseFeedbackPendingVo> buildPersonalTrainingPending(Long memberId, Set<Long> feedbackBookingIds) {
        List<PersonalTrainingBooking> bookings = personalTrainingBookingMapper.selectList(new LambdaQueryWrapper<PersonalTrainingBooking>()
                .eq(PersonalTrainingBooking::getMemberId, memberId)
                .eq(PersonalTrainingBooking::getStatus, 2)
                .eq(PersonalTrainingBooking::getDeleted, 0)
                .orderByDesc(PersonalTrainingBooking::getUpdateTime));
        List<PersonalTrainingBooking> pendingBookings = bookings.stream()
                .filter(booking -> booking.getId() != null)
                .filter(booking -> !feedbackBookingIds.contains(booking.getId()))
                .toList();
        if (pendingBookings.isEmpty()) {
            return List.of();
        }
        List<Long> coachIds = pendingBookings.stream()
                .map(PersonalTrainingBooking::getCoachId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, Coach> coaches = coachIds.isEmpty() ? Map.of() : coachMapper.selectBatchIds(coachIds).stream()
                .collect(Collectors.toMap(Coach::getId, Function.identity()));
        return pendingBookings.stream()
                .map(booking -> toPrivatePendingVo(booking, coaches.get(booking.getCoachId())))
                .toList();
    }

    private void fillCourseFeedback(Long memberId, CourseFeedback feedback) {
        if (feedback.getScheduleId() == null) {
            throw new ServiceException("Please select a completed course");
        }
        boolean completed = enrollmentMapper.selectCount(new LambdaQueryWrapper<CourseEnrollment>()
                .eq(CourseEnrollment::getMemberId, memberId)
                .eq(CourseEnrollment::getScheduleId, feedback.getScheduleId())
                .eq(CourseEnrollment::getStatus, "2")) > 0;
        if (!completed) {
            throw new ServiceException("Course is not completed");
        }
        boolean exists = count(new LambdaQueryWrapper<CourseFeedback>()
                .eq(CourseFeedback::getMemberId, memberId)
                .eq(CourseFeedback::getScheduleId, feedback.getScheduleId())
                .eq(CourseFeedback::getDeleted, 0)) > 0;
        if (exists) {
            throw new ServiceException("Course feedback already submitted");
        }
        feedback.setBookingId(null);
        feedback.setFeedbackType(TYPE_COURSE);
    }

    private void fillPersonalTrainingFeedback(Long memberId, CourseFeedback feedback) {
        PersonalTrainingBooking booking = personalTrainingBookingMapper.selectById(feedback.getBookingId());
        if (booking == null || booking.getDeleted() == 1 || !Objects.equals(booking.getMemberId(), memberId)) {
            throw new ServiceException("Personal training booking not found");
        }
        if (booking.getStatus() == null || booking.getStatus() != 2) {
            throw new ServiceException("Personal training is not completed");
        }
        boolean exists = count(new LambdaQueryWrapper<CourseFeedback>()
                .eq(CourseFeedback::getMemberId, memberId)
                .eq(CourseFeedback::getBookingId, feedback.getBookingId())
                .eq(CourseFeedback::getDeleted, 0)) > 0;
        if (exists) {
            throw new ServiceException("Personal training feedback already submitted");
        }
        feedback.setScheduleId(null);
        feedback.setCourseId(null);
        feedback.setCoachId(booking.getCoachId());
        feedback.setFeedbackType(TYPE_PERSONAL_TRAINING);
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
        vo.setFeedbackType(TYPE_COURSE);
        vo.setCourseId(schedule.getCourseId());
        vo.setCoachId(schedule.getCoachId());
        vo.setCourseName(course == null ? null : course.getName());
        vo.setCoachName(coach == null ? null : coach.getName());
        vo.setClassTime(schedule.getClassTime());
        vo.setStartTime(schedule.getStartTime());
        vo.setEndTime(schedule.getEndTime());
        return vo;
    }

    private CourseFeedbackPendingVo toPrivatePendingVo(PersonalTrainingBooking booking, Coach coach) {
        CourseFeedbackPendingVo vo = new CourseFeedbackPendingVo();
        vo.setBookingId(booking.getId());
        vo.setFeedbackType(TYPE_PERSONAL_TRAINING);
        vo.setCoachId(booking.getCoachId());
        vo.setCourseName("Personal training");
        vo.setCoachName(coach == null ? null : coach.getName());
        vo.setStartTime(booking.getStartTime() == null ? null : booking.getStartTime().toString());
        vo.setEndTime(booking.getEndTime() == null ? null : booking.getEndTime().toString());
        return vo;
    }
}
