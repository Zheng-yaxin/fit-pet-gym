package com.gym.modules.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.training.domain.entity.TrainingLog;
import com.gym.modules.training.domain.vo.TrainingReviewVo;

import java.util.List;

public interface ITrainingLogService extends IService<TrainingLog> {
    TrainingReviewVo buildMemberReview(Long memberId);

    List<TrainingReviewVo> buildAdminReviews();
}
