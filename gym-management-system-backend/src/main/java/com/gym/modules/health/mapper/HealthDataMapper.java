package com.gym.modules.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.health.domain.entity.HealthData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康数据 Mapper 接口
 */
@Mapper
public interface HealthDataMapper extends BaseMapper<HealthData> {
}