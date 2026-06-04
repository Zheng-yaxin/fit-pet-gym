package com.gym.modules.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.course.domain.dto.EnrollmentQueryDto;
import com.gym.modules.course.domain.entity.ClassSchedule;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.course.domain.entity.Course;
import com.gym.modules.course.domain.entity.CourseEnrollment;
import com.gym.modules.course.domain.vo.EnrollmentDetailVo;
import com.gym.modules.course.domain.vo.EnrollmentStatsVo;
import com.gym.modules.course.domain.vo.ScheduleDetailVo;
import com.gym.modules.course.mapper.CourseEnrollmentMapper;
import com.gym.modules.course.service.IClassScheduleService;
import com.gym.modules.coach.service.ICoachService;
import com.gym.modules.course.service.ICourseEnrollmentService;
import com.gym.modules.course.service.ICourseService;
import com.gym.modules.member.domain.entity.MemberTransaction;
import com.gym.modules.member.mapper.MemberMapper;
import com.gym.modules.member.mapper.MemberTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseEnrollmentServiceImpl extends ServiceImpl<CourseEnrollmentMapper, CourseEnrollment> implements ICourseEnrollmentService {

    @Autowired
    private IClassScheduleService scheduleService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ICoachService coachService;

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private MemberTransactionMapper transactionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enrollCourse(Long memberId, Long scheduleId) {
        // 1. 获取并校验课表
        ClassSchedule schedule = scheduleService.getById(scheduleId);
        if (schedule == null || schedule.getDeleted() == 1) {
            throw new ServiceException("课程不存在");
        }

        // --- 核心逻辑1：动态计算本次预约的目标上课时间 ---
        Date targetClassTime = calculateTargetClassTime(schedule, new Date()); // 基于当前时间计算

        // 校验时间
        boolean isRecurring = schedule.getDayOfWeek() != null;
        if (isRecurring) {
            // 周期性排课：检查是否已过失效日期
            if (schedule.getExpiryDate() != null && schedule.getExpiryDate().before(new Date())) {
                throw new ServiceException("该课程已结课，无法预约");
            }
        } else {
            // 一次性排课：检查具体上课时间是否已过
            if (schedule.getClassTime() != null && schedule.getClassTime().before(new Date())) {
                throw new ServiceException("该课程已结束，无法预约");
            }
        }

        // 安全处理 capacity 和 enrolledCount 防止空指针
        int capacity = schedule.getCapacity() != null ? schedule.getCapacity() : 0;
        int enrolledCount = schedule.getEnrolledCount() != null ? schedule.getEnrolledCount() : 0;

        if (enrolledCount >= capacity) {
            throw new ServiceException("该课程名额已满");
        }

        // 2. 检查是否存在【当前有效的】记录
        // 我们只拦截状态为 '0'(已报名) 的记录。如果以前有 '1'(已取消) 的记录，不拦截，允许再次报名生成新记录。
        long activeCount = this.count(new LambdaQueryWrapper<CourseEnrollment>()
                .eq(CourseEnrollment::getMemberId, memberId)
                .eq(CourseEnrollment::getScheduleId, scheduleId)
                .eq(CourseEnrollment::getStatus, "0") // 只查已报名的
                .eq(CourseEnrollment::getDeleted, 0));

        if (activeCount > 0) {
            throw new ServiceException("您已预约该课程，请勿重复预约");
        }

        CourseEnrollment inactiveEnrollment = this.getOne(new LambdaQueryWrapper<CourseEnrollment>()
                .eq(CourseEnrollment::getMemberId, memberId)
                .eq(CourseEnrollment::getScheduleId, scheduleId)
                .eq(CourseEnrollment::getDeleted, 0)
                .ne(CourseEnrollment::getStatus, "0")
                .orderByDesc(CourseEnrollment::getUpdateTime)
                .last("limit 1"));

        // 3. 余额扣费核心逻辑
        Course course = courseService.getById(schedule.getCourseId());
        BigDecimal price = (course != null && course.getPrice() != null) ? course.getPrice() : BigDecimal.ZERO;

        if (price.compareTo(BigDecimal.ZERO) > 0) {
            Member member = memberMapper.selectById(memberId);
            if (member == null) throw new ServiceException("会员不存在");

            BigDecimal currentBalance = member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO;
            if (currentBalance.compareTo(price) < 0) {
                throw new ServiceException("账户余额不足，请充值");
            }

            // 扣费
            member.setBalance(currentBalance.subtract(price));
            memberMapper.updateById(member);

            // 记录消费流水
            recordTransaction(memberId, "消费", price, member.getBalance(), "预约课程：" + course.getName());
        }

        CourseEnrollment enrollment = inactiveEnrollment != null ? inactiveEnrollment : new CourseEnrollment();
        if (inactiveEnrollment == null) {
            enrollment.setMemberId(memberId);
            enrollment.setScheduleId(scheduleId);
            enrollment.setDeleted(0);
        }
        enrollment.setStatus("0"); // 已报名
        enrollment.setCreateTime(new Date()); // 记录当前的预约操作时间
        enrollment.setUpdateTime(new Date());

        boolean success;
        try {
            success = inactiveEnrollment != null ? this.updateById(enrollment) : this.save(enrollment);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new ServiceException("您已预约该课程，请勿重复预约");
        }

        if (!success) {
            throw new ServiceException("预约失败，请稍后重试");
        }

        // 更新课表已报名人数
        schedule.setEnrolledCount(enrolledCount + 1);
        scheduleService.updateById(schedule);

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelEnrollment(Long memberId, Long enrollmentId) {
        CourseEnrollment enrollment = this.getById(enrollmentId);
        if (enrollment == null || enrollment.getDeleted() == 1) {
            throw new ServiceException("预约记录不存在");
        }

        if (!enrollment.getMemberId().equals(memberId)) {
            throw new ServiceException("无权取消该预约");
        }

        if (!"0".equals(enrollment.getStatus())) {
            throw new ServiceException("该预约状态无法取消");
        }

        ClassSchedule schedule = scheduleService.getById(enrollment.getScheduleId());
        if (schedule == null) {
            throw new ServiceException("相关课程排期不存在或已删除");
        }

        // --- 核心逻辑2：取消时的校验 ---
        // 我们需要根据预约记录的创建时间，还原出当初预约的是哪一天的课，来判断是否可以取消
        Date classTimeToCheck;
        if (schedule.getDayOfWeek() != null) {
            // 如果是周期性课程，基于预约时间算出那节课的上课时间
            classTimeToCheck = calculateTargetClassTime(schedule, enrollment.getCreateTime());
        } else {
            // 一次性课程直接用排课时间
            classTimeToCheck = schedule.getClassTime();
        }

        if (classTimeToCheck != null) {
            long twoHours = 2 * 60 * 60 * 1000L;
            long timeUntilClass = classTimeToCheck.getTime() - System.currentTimeMillis();

            // 如果距离上课小于2小时，或者课程已结束，无法取消
            if (timeUntilClass < twoHours) {
                throw new ServiceException("开课前2小时内（或课程已结束）无法取消预约");
            }
        }

        // 1. 退款逻辑
        Course course = courseService.getById(schedule.getCourseId());
        BigDecimal price = (course != null && course.getPrice() != null) ? course.getPrice() : BigDecimal.ZERO;

        if (price.compareTo(BigDecimal.ZERO) > 0) {
            Member member = memberMapper.selectById(memberId);
            BigDecimal currentBalance = member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO;

            // 退还余额
            member.setBalance(currentBalance.add(price));
            memberMapper.updateById(member);

            // 记录退款流水
            recordTransaction(memberId, "退款", price, member.getBalance(), "取消预约退款：" + (course != null ? course.getName() : ""));
        }

        // 2. 更新预约状态
        enrollment.setStatus("1"); // 已取消
        enrollment.setUpdateTime(new Date());
        boolean updated = this.updateById(enrollment);

        if (updated) {
            int currentCount = schedule.getEnrolledCount() != null ? schedule.getEnrolledCount() : 0;
            schedule.setEnrolledCount(Math.max(0, currentCount - 1));
            scheduleService.updateById(schedule);
        }

        return updated;
    }

    @Override
    public Page<EnrollmentDetailVo> getMemberEnrollments(Long memberId, EnrollmentQueryDto queryDto) {
        Page<EnrollmentDetailVo> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        Page<EnrollmentDetailVo> result = baseMapper.selectMemberEnrollments(page, memberId, queryDto.getStatus());

        // --- 核心逻辑3：修正列表展示的时间 ---
        // 数据库存的是通用的排课模版时间，这里我们需要根据预约记录的创建时间，
        // 动态计算出这条预约具体对应的是哪一天的课，并在前端展示
        if (result.getRecords() != null) {
            for (EnrollmentDetailVo vo : result.getRecords()) {
                // 如果是周期性排课 (dayOfWeek 不为空)，需要计算实际日期
                if (vo.getDayOfWeek() != null) {
                    // 构造一个临时的 Schedule 对象用于计算
                    ClassSchedule tempSchedule = new ClassSchedule();
                    tempSchedule.setDayOfWeek(vo.getDayOfWeek());
                    tempSchedule.setStartTime(vo.getStartTime());
                    tempSchedule.setClassTime(vo.getClassTime()); // 原始时间作为备用

                    // 基于预约的创建时间 (createTime) 来推算它是约的哪一周
                    // 如果 createTime 是 null，就用当前时间兜底
                    Date baseDate = vo.getCreateTime() != null ? vo.getCreateTime() : new Date();
                    Date realClassDate = calculateTargetClassTime(tempSchedule, baseDate);

                    vo.setClassTime(realClassDate); // 覆盖 VO 中的时间，让前端显示具体日期
                }
            }
        }
        return result;
    }

    /**
     * 核心算法：计算目标上课时间
     * 逻辑：根据基准时间(baseDate)和课程的周几/开始时间，算出最近的一个有效的未来上课时间。
     * * @param schedule 排课信息
     * @param baseDate 基准时间 (如果是预约时，传 Now；如果是回显历史，传 enrollment.createTime)
     */
    private Date calculateTargetClassTime(ClassSchedule schedule, Date baseDate) {
        // 如果不是周期排课，直接返回原有的 classTime
        if (schedule.getDayOfWeek() == null) {
            return schedule.getClassTime();
        }

        try {
            // 1. 解析开始时间 "09:00"
            String startTimeStr = schedule.getStartTime();
            if (startTimeStr == null || !startTimeStr.contains(":")) {
                return schedule.getClassTime();
            }

            String[] parts = startTimeStr.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            // 2. 设置基准时间
            Calendar cal = Calendar.getInstance();
            cal.setTime(baseDate); // 基于这个时间点往后找

            // 3. 计算目标日期的具体时间点
            // 先将 Calendar 设置为 baseDate 当天的具体开课时间
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            // 4. 处理周几的差异
            // DB dayOfWeek: 1=周一 ... 7=周日
            // Java Calendar: 1=周日, 2=周一 ... 7=周六
            int dbDay = schedule.getDayOfWeek();
            // 将 DB 的周几转换为 Calendar 的周几
            int calendarTargetDay = (dbDay % 7) + 1;

            int currentCalendarDay = cal.get(Calendar.DAY_OF_WEEK);

            // 计算需要加多少天
            int daysToAdd = (calendarTargetDay - currentCalendarDay + 7) % 7;

            // 如果计算出的天数是0 (意味着今天是周几，课程也是周几)
            if (daysToAdd == 0) {
                // 如果当前时间已经过了开课时间，说明这周的课赶不上了，得约下周的
                // 比如：现在是周三 10:00，课程是周三 09:00，那只能约下周三
                if (cal.getTime().before(baseDate)) {
                    daysToAdd = 7;
                }
            }

            cal.add(Calendar.DAY_OF_MONTH, daysToAdd);

            return cal.getTime();

        } catch (Exception e) {
            e.printStackTrace();
            return schedule.getClassTime();
        }
    }

    // 辅助方法：记录交易流水
    private void recordTransaction(Long memberId, String type, BigDecimal amount, BigDecimal balanceAfter, String remark) {
        MemberTransaction transaction = new MemberTransaction();
        transaction.setMemberId(memberId);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setRemark(remark);
        transaction.setCreateTime(new Date());
        transaction.setOperator("system");
        transactionMapper.insert(transaction);
    }

    @Override
    public ScheduleDetailVo getScheduleDetail(Long scheduleId, Long memberId) {
        ClassSchedule schedule = scheduleService.getById(scheduleId);
        if (schedule == null || schedule.getDeleted() == 1) {
            return null;
        }
        return buildScheduleDetailVo(schedule, memberId);
    }

    @Override
    public List<ScheduleDetailVo> getAvailableSchedules(Long memberId, Long courseId, Long coachId) {
        LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassSchedule::getDeleted, 0);

        // 筛选逻辑：一次性课程未结束 OR 周期性课程
        wrapper.and(w -> {
            w.gt(ClassSchedule::getClassTime, new Date())
                    .or(orW -> {
                        orW.isNotNull(ClassSchedule::getDayOfWeek)
                                .and(subW -> subW.isNull(ClassSchedule::getExpiryDate)
                                        .or()
                                        .ge(ClassSchedule::getExpiryDate, new Date()));
                    });
        });

        wrapper.orderByAsc(ClassSchedule::getDayOfWeek)
                .orderByAsc(ClassSchedule::getStartTime)
                .orderByAsc(ClassSchedule::getClassTime);

        if (courseId != null) wrapper.eq(ClassSchedule::getCourseId, courseId);
        if (coachId != null) wrapper.eq(ClassSchedule::getCoachId, coachId);

        List<ClassSchedule> schedules = scheduleService.list(wrapper);
        return schedules.stream()
                .map(s -> buildScheduleDetailVo(s, memberId))
                .collect(Collectors.toList());
    }

    @Override
    public Page<EnrollmentDetailVo> getEnrollmentList(EnrollmentQueryDto queryDto) {
        Page<EnrollmentDetailVo> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        return baseMapper.selectEnrollmentList(page, queryDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateEnrollmentStatus(Long enrollmentId, String status) {
        CourseEnrollment enrollment = this.getById(enrollmentId);
        if (enrollment == null || enrollment.getDeleted() == 1) throw new ServiceException("预约记录不存在");

        String oldStatus = enrollment.getStatus();
        enrollment.setStatus(status);
        enrollment.setUpdateTime(new Date());
        boolean updated = this.updateById(enrollment);

        if (updated && "0".equals(oldStatus) && "1".equals(status)) {
            ClassSchedule schedule = scheduleService.getById(enrollment.getScheduleId());
            if (schedule != null) {
                int currentCount = schedule.getEnrolledCount() != null ? schedule.getEnrolledCount() : 0;
                schedule.setEnrolledCount(Math.max(0, currentCount - 1));
                scheduleService.updateById(schedule);
            }
        }
        return updated;
    }

    @Override
    public EnrollmentStatsVo getEnrollmentStats() {
        return baseMapper.selectEnrollmentStats();
    }

    @Override
    public boolean isEnrolled(Long memberId, Long scheduleId) {
        LambdaQueryWrapper<CourseEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseEnrollment::getMemberId, memberId)
                .eq(CourseEnrollment::getScheduleId, scheduleId)
                .eq(CourseEnrollment::getStatus, "0")
                .eq(CourseEnrollment::getDeleted, 0);
        return this.count(wrapper) > 0;
    }

    private ScheduleDetailVo buildScheduleDetailVo(ClassSchedule schedule, Long memberId) {
        ScheduleDetailVo vo = new ScheduleDetailVo();
        vo.setId(schedule.getId());
        vo.setCourseId(schedule.getCourseId());
        vo.setCoachId(schedule.getCoachId());

        // --- 动态展示时间 ---
        // 即使是查询详情，也根据当前时间给用户展示最近的一次课
        Date displayTime = calculateTargetClassTime(schedule, new Date());
        vo.setClassTime(displayTime);
        // ------------------

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

        int capacity = schedule.getCapacity() != null ? schedule.getCapacity() : 0;
        int enrolled = schedule.getEnrolledCount() != null ? schedule.getEnrolledCount() : 0;
        vo.setCapacity(capacity);
        vo.setEnrolledCount(enrolled);
        vo.setRemainingSlots(Math.max(0, capacity - enrolled));

        Course course = courseService.getById(schedule.getCourseId());
        if (course != null) {
            vo.setCourseName(course.getName());
            vo.setCourseDescription(course.getDescription());
            vo.setDuration(course.getDuration());
            vo.setPrice(course.getPrice());
            vo.setCourseImage(course.getImageUrl());
        }

        Coach coach = coachService.getById(schedule.getCoachId());
        if (coach != null) {
            vo.setCoachName(coach.getName());
            vo.setCoachAvatar(coach.getAvatar());
            vo.setCoachSpecialty(coach.getSpecialties());
        }

        if (memberId != null) {
            LambdaQueryWrapper<CourseEnrollment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseEnrollment::getMemberId, memberId)
                    .eq(CourseEnrollment::getScheduleId, schedule.getId())
                    .eq(CourseEnrollment::getStatus, "0")
                    .eq(CourseEnrollment::getDeleted, 0);
            CourseEnrollment enrollment = this.getOne(wrapper);
            vo.setEnrolled(enrollment != null);
            vo.setEnrollmentId(enrollment != null ? enrollment.getId() : null);
        } else {
            vo.setEnrolled(false);
        }

        return vo;
    }
}
