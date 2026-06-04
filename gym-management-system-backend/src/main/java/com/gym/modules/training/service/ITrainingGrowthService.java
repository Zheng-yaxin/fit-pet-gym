package com.gym.modules.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.training.domain.entity.TrainingGrowth;
import com.gym.modules.training.domain.entity.TrainingLog;
import com.gym.modules.training.domain.vo.TrainingGrowthVo;

import java.util.List;

public interface ITrainingGrowthService extends IService<TrainingGrowth> {
    TrainingGrowthVo getMemberGrowth(Long memberId);

    TrainingGrowthVo refreshMemberGrowth(Long memberId, TrainingLog rewardSource);

    List<TrainingGrowthVo> buildAdminGrowth();
}
