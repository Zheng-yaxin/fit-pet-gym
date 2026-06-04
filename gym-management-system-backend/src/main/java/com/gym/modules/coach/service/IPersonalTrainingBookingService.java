package com.gym.modules.coach.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.coach.domain.entity.PersonalTrainingBooking;
import com.gym.modules.coach.domain.dto.PersonalTrainingBookingDto;
import com.gym.modules.coach.domain.vo.PersonalTrainingBookingVo;
import java.util.List;

/**
 * 私教预约Service接口
 */
public interface IPersonalTrainingBookingService extends IService<PersonalTrainingBooking> {
    /**
     * 会员预约私教
     */
    boolean bookPersonalTraining(Long memberId, PersonalTrainingBookingDto dto);

    /**
     * 会员查询自己的预约记录
     */
    List<PersonalTrainingBookingVo> getMemberBookings(Long memberId);

    /**
     * 教练查询自己的预约记录
     */
    List<PersonalTrainingBookingVo> getCoachBookings(Long coachId);

    /**
     * 取消预约
     */
    boolean cancelBooking(Long bookingId, Long userId, String userType);

    /**
     * 教练确认预约
     */
    boolean confirmBooking(Long coachId, Long bookingId);

    /**
     * 完成预约
     */
    boolean completeBooking(Long coachId, Long bookingId);
}
