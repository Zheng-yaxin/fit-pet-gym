package com.gym.modules.feedback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.feedback.domain.entity.CourseFeedback;
import com.gym.modules.feedback.domain.vo.CourseFeedbackPendingVo;

import java.util.List;

public interface ICourseFeedbackService extends IService<CourseFeedback> {
    List<CourseFeedbackPendingVo> listPending(Long memberId);
}
