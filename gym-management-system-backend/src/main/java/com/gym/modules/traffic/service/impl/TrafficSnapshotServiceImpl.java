package com.gym.modules.traffic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.modules.traffic.domain.entity.TrafficSnapshot;
import com.gym.modules.traffic.mapper.TrafficSnapshotMapper;
import com.gym.modules.traffic.service.ITrafficSnapshotService;
import org.springframework.stereotype.Service;

@Service
public class TrafficSnapshotServiceImpl extends ServiceImpl<TrafficSnapshotMapper, TrafficSnapshot> implements ITrafficSnapshotService {
}
