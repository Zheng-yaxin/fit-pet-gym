package com.gym.modules.traffic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.traffic.domain.entity.TrafficSnapshot;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrafficSnapshotMapper extends BaseMapper<TrafficSnapshot> {
}
