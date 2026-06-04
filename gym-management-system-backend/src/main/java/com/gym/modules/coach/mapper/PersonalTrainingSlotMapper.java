package com.gym.modules.coach.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.coach.domain.entity.PersonalTrainingSlot;
import org.apache.ibatis.annotations.Mapper;

/**
 * 私教时间段Mapper
 */
@Mapper
public interface PersonalTrainingSlotMapper extends BaseMapper<PersonalTrainingSlot> {
}
