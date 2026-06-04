package com.gym.modules.traffic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.modules.traffic.domain.entity.GymArea;
import com.gym.modules.traffic.mapper.GymAreaMapper;
import com.gym.modules.traffic.service.IGymAreaService;
import org.springframework.stereotype.Service;

@Service
public class GymAreaServiceImpl extends ServiceImpl<GymAreaMapper, GymArea> implements IGymAreaService {
}
