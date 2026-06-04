package com.gym.modules.coach.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.modules.coach.domain.entity.PersonalTrainingBooking;
import com.gym.modules.coach.domain.entity.PersonalTrainingSlot;
import com.gym.modules.coach.domain.dto.PersonalTrainingSlotAddDto;
import com.gym.modules.coach.domain.vo.PersonalTrainingSlotVo;
import com.gym.modules.coach.mapper.PersonalTrainingBookingMapper;
import com.gym.modules.coach.mapper.PersonalTrainingSlotMapper;
import com.gym.modules.coach.service.IPersonalTrainingSlotService;
import com.gym.modules.coach.service.ICoachService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 私教时间段Service实现
 */
@Service
public class PersonalTrainingSlotServiceImpl extends ServiceImpl<PersonalTrainingSlotMapper, PersonalTrainingSlot>
        implements IPersonalTrainingSlotService {

    @Autowired
    private ICoachService coachService;

    @Autowired
    private PersonalTrainingBookingMapper bookingMapper;

    // 指定运营时区，防止服务器时区不一致导致的时间判断错误
    private static final ZoneId ZONE_CN = ZoneId.of("Asia/Shanghai");

    @Override
    public boolean addSlot(Long coachId, PersonalTrainingSlotAddDto dto) {
        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new ServiceException("开始时间不能晚于结束时间");
        }

        LambdaQueryWrapper<PersonalTrainingSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonalTrainingSlot::getCoachId, coachId)
                .eq(PersonalTrainingSlot::getDate, dto.getDate())
                .eq(PersonalTrainingSlot::getDeleted, 0)
                .and(w -> w
                        .between(PersonalTrainingSlot::getStartTime, dto.getStartTime(), dto.getEndTime())
                        .or()
                        .between(PersonalTrainingSlot::getEndTime, dto.getStartTime(), dto.getEndTime())
                );

        if (baseMapper.selectCount(wrapper) > 0) {
            throw new ServiceException("该时间段已设置为休息");
        }

        LambdaQueryWrapper<PersonalTrainingBooking> bookingWrapper = new LambdaQueryWrapper<>();
        bookingWrapper.eq(PersonalTrainingBooking::getCoachId, coachId)
                .eq(PersonalTrainingBooking::getDate, dto.getDate())
                .ne(PersonalTrainingBooking::getStatus, 3)
                .eq(PersonalTrainingBooking::getDeleted, 0)
                .and(w -> w
                        .ge(PersonalTrainingBooking::getStartTime, dto.getStartTime())
                        .lt(PersonalTrainingBooking::getEndTime, dto.getEndTime())
                );
        if (bookingMapper.selectCount(bookingWrapper) > 0) {
            throw new ServiceException("该时间段已有学员预约，无法设置为休息");
        }

        PersonalTrainingSlot slot = new PersonalTrainingSlot();
        BeanUtils.copyProperties(dto, slot);
        slot.setCoachId(coachId);
        slot.setStatus(2);
        return save(slot);
    }

    @Override
    public List<PersonalTrainingSlotVo> getCoachSlots(Long coachId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<PersonalTrainingSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonalTrainingSlot::getCoachId, coachId)
                .eq(PersonalTrainingSlot::getDeleted, 0);

        if (startDate != null) {
            wrapper.ge(PersonalTrainingSlot::getDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(PersonalTrainingSlot::getDate, endDate);
        }

        wrapper.orderByAsc(PersonalTrainingSlot::getDate, PersonalTrainingSlot::getStartTime);

        List<PersonalTrainingSlot> slots = list(wrapper);
        return slots.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public List<PersonalTrainingSlotVo> getAvailableSlots(Long coachId, LocalDate startDate, LocalDate endDate) {
        List<PersonalTrainingSlotVo> result = new ArrayList<>();

        // 营业时间：09:00 - 22:00
        int startHour = 9;
        int endHour = 22;

        LocalDate current = startDate;
        LocalDate end = (endDate == null) ? startDate : endDate;

        // 获取当前时区的准确日期和时间
        LocalDate today = LocalDate.now(ZONE_CN);
        LocalTime nowTime = LocalTime.now(ZONE_CN);

        while (!current.isAfter(end)) {
            // 修复：过滤掉过去的日期
            if (current.isBefore(today)) {
                current = current.plusDays(1);
                continue;
            }

            // 查询休息设置
            LambdaQueryWrapper<PersonalTrainingSlot> slotWrapper = new LambdaQueryWrapper<>();
            slotWrapper.eq(PersonalTrainingSlot::getCoachId, coachId)
                    .eq(PersonalTrainingSlot::getDate, current)
                    .eq(PersonalTrainingSlot::getDeleted, 0);
            List<PersonalTrainingSlot> blockedSlots = list(slotWrapper);

            // 查询预约记录
            LambdaQueryWrapper<PersonalTrainingBooking> bookingWrapper = new LambdaQueryWrapper<>();
            bookingWrapper.eq(PersonalTrainingBooking::getCoachId, coachId)
                    .eq(PersonalTrainingBooking::getDate, current)
                    .ne(PersonalTrainingBooking::getStatus, 3)
                    .eq(PersonalTrainingBooking::getDeleted, 0);
            List<PersonalTrainingBooking> bookings = bookingMapper.selectList(bookingWrapper);

            for (int h = startHour; h < endHour; h++) {
                LocalTime slotStart = LocalTime.of(h, 0);
                LocalTime slotEnd = LocalTime.of(h + 1, 0);

                PersonalTrainingSlotVo vo = new PersonalTrainingSlotVo();
                vo.setDate(current);
                vo.setStartTime(slotStart);
                vo.setEndTime(slotEnd);
                vo.setCoachId(coachId);

                // 状态判断：检查休息时间段是否与当前时间槽有重叠
                // 两个时间段重叠的条件：休息开始时间 < 时间槽结束时间 && 休息结束时间 > 时间槽开始时间
                boolean isBlocked = blockedSlots.stream().anyMatch(s ->
                        s.getStartTime().isBefore(slotEnd) && s.getEndTime().isAfter(slotStart)
                );

                if (isBlocked) {
                    vo.setStatus(2);
                    vo.setStatusText("休息中");
                } else {
                    boolean isBooked = bookings.stream().anyMatch(b ->
                            b.getStartTime().equals(slotStart)
                    );

                    if (isBooked) {
                        vo.setStatus(1);
                        vo.setStatusText("已被约");
                    } else {
                        vo.setStatus(0);
                        vo.setStatusText("可预约");
                    }
                }

                // 修复：使用指定时区判断当前时间，防止时区错误导致当天时间段不显示
                if (current.isEqual(today)) {
                    if (slotStart.isAfter(nowTime)) {
                        result.add(vo);
                    }
                } else {
                    result.add(vo);
                }
            }
            current = current.plusDays(1);
        }
        return result;
    }

    @Override
    public boolean deleteSlot(Long coachId, Long slotId) {
        PersonalTrainingSlot slot = getById(slotId);
        if (slot == null) throw new ServiceException("记录不存在");
        if (!slot.getCoachId().equals(coachId)) throw new ServiceException("无权操作");
        return removeById(slotId);
    }

    @Override
    public boolean updateSlotStatus(Long slotId, Integer status) {
        PersonalTrainingSlot slot = getById(slotId);
        if (slot == null) throw new ServiceException("记录不存在");
        slot.setStatus(status);
        return updateById(slot);
    }

    private PersonalTrainingSlotVo convertToVo(PersonalTrainingSlot slot) {
        PersonalTrainingSlotVo vo = new PersonalTrainingSlotVo();
        BeanUtils.copyProperties(slot, vo);
        vo.setStatusText(getStatusText(slot.getStatus()));
        return vo;
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "可预约";
            case 1 -> "已预约";
            case 2 -> "休息中";
            default -> "未知";
        };
    }
}