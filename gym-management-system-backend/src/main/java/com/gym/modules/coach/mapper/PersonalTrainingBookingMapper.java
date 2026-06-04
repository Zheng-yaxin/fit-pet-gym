package com.gym.modules.coach.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.coach.domain.entity.PersonalTrainingBooking;
import org.apache.ibatis.annotations.Mapper;

/**
 * 私教预约Mapper
 */
@Mapper
public interface PersonalTrainingBookingMapper extends BaseMapper<PersonalTrainingBooking> {
}
