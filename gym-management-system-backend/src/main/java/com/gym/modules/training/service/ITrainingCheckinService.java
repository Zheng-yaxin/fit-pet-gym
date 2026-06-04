package com.gym.modules.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.training.domain.entity.TrainingCheckin;

public interface ITrainingCheckinService extends IService<TrainingCheckin> {
    TrainingCheckin active(Long memberId);

    TrainingCheckin start(Long memberId, Long planId);

    TrainingCheckin end(Long memberId, Long checkinId);
}
