package com.gym.modules.coach.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.coach.domain.entity.Coach;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教练Mapper
 */
@Mapper
public interface CoachMapper extends BaseMapper<Coach> {
}
