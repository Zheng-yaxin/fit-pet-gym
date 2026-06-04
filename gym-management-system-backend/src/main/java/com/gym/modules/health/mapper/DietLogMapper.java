package com.gym.modules.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.health.domain.entity.DietLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DietLogMapper extends BaseMapper<DietLog> {
}