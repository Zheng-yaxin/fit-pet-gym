package com.gym.modules.feedback.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gym.common.result.R;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.feedback.domain.entity.CourseFeedback;
import com.gym.modules.feedback.domain.vo.CourseFeedbackPendingVo;
import com.gym.modules.feedback.service.ICourseFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "课程课后反馈")
@RestController
@RequestMapping("/course/feedback")
public class CourseFeedbackController {
    @Autowired
    private ICourseFeedbackService feedbackService;

    @GetMapping("/pending")
    @Operation(summary = "查询待反馈课程")
    public R<List<CourseFeedbackPendingVo>> pending() {
        return R.ok(feedbackService.listPending(SecurityUtils.getUserId()));
    }

    @PostMapping
    @Operation(summary = "提交课后反馈")
    public R<Void> submit(@RequestBody CourseFeedback feedback) {
        feedback.setMemberId(SecurityUtils.getUserId());
        return feedbackService.save(feedback) ? R.ok() : R.fail("提交反馈失败");
    }

    @GetMapping("/mine")
    @Operation(summary = "查询我的反馈")
    public R<List<CourseFeedback>> mine() {
        return R.ok(feedbackService.list(new LambdaQueryWrapper<CourseFeedback>()
                .eq(CourseFeedback::getMemberId, SecurityUtils.getUserId())
                .orderByDesc(CourseFeedback::getCreateTime)));
    }
}
