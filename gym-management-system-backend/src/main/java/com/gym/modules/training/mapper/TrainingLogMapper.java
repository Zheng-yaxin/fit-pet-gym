package com.gym.modules.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.training.domain.entity.TrainingLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainingLogMapper extends BaseMapper<TrainingLog> {
}
