package com.gym.modules.coach.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.coach.domain.entity.PersonalTrainingBooking;
import com.gym.modules.coach.domain.entity.PersonalTrainingSlot;
import com.gym.modules.coach.domain.dto.PersonalTrainingBookingDto;
import com.gym.modules.coach.domain.vo.PersonalTrainingBookingVo;
import com.gym.modules.coach.mapper.PersonalTrainingBookingMapper;
import com.gym.modules.coach.mapper.PersonalTrainingSlotMapper;
import com.gym.modules.coach.service.ICoachService;
import com.gym.modules.coach.service.IPersonalTrainingBookingService;
import com.gym.modules.member.domain.entity.MemberTransaction;
import com.gym.modules.member.mapper.MemberMapper;
import com.gym.modules.member.mapper.MemberTransactionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 私教预约Service实现
 */
@Service
public class PersonalTrainingBookingServiceImpl extends ServiceImpl<PersonalTrainingBookingMapper, PersonalTrainingBooking>
        implements IPersonalTrainingBookingService {

    @Autowired
    private ICoachService coachService;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private MemberTransactionMapper transactionMapper;

    @Autowired
    private PersonalTrainingSlotMapper slotMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bookPersonalTraining(Long memberId, PersonalTrainingBookingDto dto) {
        // 1. 基础校验
        Coach coach = coachService.getById(dto.getCoachId());
        if (coach == null || (coach.getStatus() != null && !coach.getStatus().equals("0"))) {
            throw new ServiceException("教练不存在或已停用");
        }

        // 2. 校验时间是否冲突
        // 2.1 检查是否被教练设置为休息 (Status = 2)
        // 逻辑：(StartA < EndB) && (EndA > StartB) 表示有重叠
        LambdaQueryWrapper<PersonalTrainingSlot> slotWrapper = new LambdaQueryWrapper<>();
        slotWrapper.eq(PersonalTrainingSlot::getCoachId, dto.getCoachId())
                .eq(PersonalTrainingSlot::getDate, dto.getDate())
                .eq(PersonalTrainingSlot::getDeleted, 0)
                .eq(PersonalTrainingSlot::getStatus, 2) // 2代表休息
                .and(w -> w
                        .lt(PersonalTrainingSlot::getStartTime, dto.getEndTime())
                        .gt(PersonalTrainingSlot::getEndTime, dto.getStartTime())
                );
        if (slotMapper.selectCount(slotWrapper) > 0) {
            throw new ServiceException("该时间段教练已休息，不可预约");
        }

        // 2.2 检查是否已被其他学员预约 (Status != 3 取消)
        LambdaQueryWrapper<PersonalTrainingBooking> bookingWrapper = new LambdaQueryWrapper<>();
        bookingWrapper.eq(PersonalTrainingBooking::getCoachId, dto.getCoachId())
                .eq(PersonalTrainingBooking::getDate, dto.getDate())
                .eq(PersonalTrainingBooking::getDeleted, 0)
                .ne(PersonalTrainingBooking::getStatus, 3) // 排除已取消的
                .and(w -> w
                        .lt(PersonalTrainingBooking::getStartTime, dto.getEndTime())
                        .gt(PersonalTrainingBooking::getEndTime, dto.getStartTime())
                );
        if (baseMapper.selectCount(bookingWrapper) > 0) {
            throw new ServiceException("该时间段已被预约，请选择其他时间");
        }

        // 3. 扣费逻辑
        Member member = memberMapper.selectById(memberId);
        if (member == null || member.getDeleted() == 1) {
            throw new ServiceException("会员不存在");
        }

        BigDecimal amount = coach.getHourlyRate() != null ? coach.getHourlyRate() : BigDecimal.ZERO;
        BigDecimal currentBalance = member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO;
        if (currentBalance.compareTo(amount) < 0) {
            throw new ServiceException("余额不足，请先充值");
        }

        member.setBalance(currentBalance.subtract(amount));
        member.setUpdateTime(new Date());
        memberMapper.updateById(member);

        // 4. 记录交易流水
        MemberTransaction transaction = new MemberTransaction();
        transaction.setMemberId(memberId);
        transaction.setTransactionType("消费");
        transaction.setAmount(amount);
        transaction.setRemark("预约私教课程 - " + coach.getName() + " " + dto.getDate() + " " + dto.getStartTime());
        transaction.setCreateTime(new Date());
        transaction.setOperator("system");
        transactionMapper.insert(transaction);

        // 5. 生成预约记录
        PersonalTrainingBooking booking = new PersonalTrainingBooking();
        booking.setMemberId(memberId);
        booking.setCoachId(dto.getCoachId());
        // 如果前端没传 slotId，存为 0
        booking.setSlotId(dto.getSlotId() != null ? dto.getSlotId() : 0L);
        booking.setDate(dto.getDate());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setAmount(amount);
        booking.setStatus(0); // 待确认
        booking.setRemark(dto.getRemark());
        save(booking);

        return true;
    }

    @Override
    public List<PersonalTrainingBookingVo> getMemberBookings(Long memberId) {
        LambdaQueryWrapper<PersonalTrainingBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonalTrainingBooking::getMemberId, memberId)
                .eq(PersonalTrainingBooking::getDeleted, 0)
                .orderByDesc(PersonalTrainingBooking::getCreateTime);

        List<PersonalTrainingBooking> bookings = list(wrapper);
        return bookings.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public List<PersonalTrainingBookingVo> getCoachBookings(Long coachId) {
        LambdaQueryWrapper<PersonalTrainingBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonalTrainingBooking::getCoachId, coachId)
                .eq(PersonalTrainingBooking::getDeleted, 0)
                .orderByDesc(PersonalTrainingBooking::getCreateTime);

        List<PersonalTrainingBooking> bookings = list(wrapper);
        return bookings.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelBooking(Long bookingId, Long userId, String userType) {
        PersonalTrainingBooking booking = getById(bookingId);
        if (booking == null || booking.getDeleted() == 1) {
            throw new ServiceException("预约记录不存在");
        }

        if ("member".equals(userType) && !booking.getMemberId().equals(userId)) {
            throw new ServiceException("无权取消该预约");
        }

        if ("coach".equals(userType) && !booking.getCoachId().equals(userId)) {
            throw new ServiceException("无权取消该预约");
        }

        Integer status = booking.getStatus();
        if (status != null && status == 2) {
            throw new ServiceException("课程已完成，无法取消");
        }

        if (status != null && status == 3) {
            throw new ServiceException("预约已取消");
        }

        // 退款
        Member member = memberMapper.selectById(booking.getMemberId());
        BigDecimal refundAmount = booking.getAmount() != null ? booking.getAmount() : BigDecimal.ZERO;
        if (member != null) {
            BigDecimal currentBalance = member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO;
            member.setBalance(currentBalance.add(refundAmount));
            member.setUpdateTime(new Date());
            memberMapper.updateById(member);

            MemberTransaction transaction = new MemberTransaction();
            transaction.setMemberId(booking.getMemberId());
            transaction.setTransactionType("退款");
            transaction.setAmount(refundAmount);
            transaction.setRemark("取消私教预约退款");
            transaction.setCreateTime(new Date());
            transaction.setOperator("system");
            transactionMapper.insert(transaction);
        }

        booking.setStatus(3); // 已取消
        booking.setUpdateTime(new Date());
        return updateById(booking);
    }

    @Override
    public boolean confirmBooking(Long coachId, Long bookingId) {
        PersonalTrainingBooking booking = getById(bookingId);
        if (booking == null || booking.getDeleted() == 1) {
            throw new ServiceException("预约记录不存在");
        }
        if (!booking.getCoachId().equals(coachId)) {
            throw new ServiceException("无权确认该预约");
        }
        if (booking.getStatus() != 0) {
            throw new ServiceException("预约状态不正确");
        }
        booking.setStatus(1); // 已确认
        return updateById(booking);
    }

    @Override
    public boolean completeBooking(Long coachId, Long bookingId) {
        PersonalTrainingBooking booking = getById(bookingId);
        if (booking == null || booking.getDeleted() == 1) {
            throw new ServiceException("预约记录不存在");
        }
        if (!booking.getCoachId().equals(coachId)) {
            throw new ServiceException("无权完成该预约");
        }
        if (booking.getStatus() != 1) {
            throw new ServiceException("预约状态不正确");
        }
        booking.setStatus(2); // 已完成
        updateById(booking);
        return true;
    }

    private PersonalTrainingBookingVo convertToVo(PersonalTrainingBooking booking) {
        PersonalTrainingBookingVo vo = new PersonalTrainingBookingVo();
        BeanUtils.copyProperties(booking, vo);
        Member member = memberMapper.selectById(booking.getMemberId());
        if (member != null) {
            vo.setMemberName(member.getName());
            vo.setMemberPhone(member.getPhone());
        }
        Coach coach = coachService.getById(booking.getCoachId());
        if (coach != null) {
            vo.setCoachName(coach.getName());
        }
        vo.setStatusText(getStatusText(booking.getStatus()));
        return vo;
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "待确认";
            case 1 -> "已确认";
            case 2 -> "已完成";
            case 3 -> "已取消";
            default -> "未知";
        };
    }
}
