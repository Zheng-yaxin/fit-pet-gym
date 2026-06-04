package com.gym.modules.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.training.domain.dto.TrainingPlanGenerateDTO;
import com.gym.modules.training.domain.entity.TrainingPlan;

public interface ITrainingPlanService extends IService<TrainingPlan> {
    TrainingPlan generate(Long memberId, TrainingPlanGenerateDTO dto);

    TrainingPlan current(Long memberId);
}
