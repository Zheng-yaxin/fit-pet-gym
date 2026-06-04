package com.gym.modules.feedback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.feedback.domain.dto.AdminFeedbackHandleDto;
import com.gym.modules.feedback.domain.entity.CourseFeedback;
import com.gym.modules.feedback.domain.vo.AdminFeedbackVo;
import com.gym.modules.feedback.domain.vo.CourseFeedbackPendingVo;

import java.util.List;

public interface ICourseFeedbackService extends IService<CourseFeedback> {
    List<CourseFeedbackPendingVo> listPending(Long memberId);

    boolean submitMemberFeedback(Long memberId, CourseFeedback feedback);

    List<AdminFeedbackVo> listAdminFeedback();

    boolean handleAdminFeedback(Long feedbackId, AdminFeedbackHandleDto dto);
}
