package com.gym.modules.feedback.controller;

import com.gym.common.result.R;
import com.gym.modules.feedback.domain.dto.AdminFeedbackHandleDto;
import com.gym.modules.feedback.domain.entity.CourseFeedback;
import com.gym.modules.feedback.domain.vo.AdminFeedbackVo;
import com.gym.modules.feedback.service.ICourseFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "管理端课后反馈")
@RestController
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {
    @Autowired
    private ICourseFeedbackService feedbackService;

    @GetMapping("/list")
    @Operation(summary = "查询反馈列表")
    public R<List<AdminFeedbackVo>> list() {
        return R.ok(feedbackService.listAdminFeedback());
    }

    @GetMapping("/stats")
    @Operation(summary = "查询反馈统计")
    public R<Map<String, Object>> stats() {
        List<CourseFeedback> list = feedbackService.list();
        double avgRating = list.stream().filter(item -> item.getRating() != null)
                .mapToInt(CourseFeedback::getRating).average().orElse(0);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", list.size());
        data.put("avgRating", avgRating);
        data.put("courseTotal", list.stream()
                .filter(item -> item.getBookingId() == null)
                .count());
        data.put("personalTrainingTotal", list.stream()
                .filter(item -> item.getBookingId() != null || "personal_training".equals(item.getFeedbackType()))
                .count());
        data.put("pendingTotal", list.stream()
                .filter(item -> item.getHandleStatus() == null || "pending".equals(item.getHandleStatus()))
                .count());
        data.put("handledTotal", list.stream()
                .filter(item -> "handled".equals(item.getHandleStatus()))
                .count());
        return R.ok(data);
    }

    @PutMapping("/{id}/handle")
    @Operation(summary = "处理反馈")
    public R<Void> handle(@PathVariable("id") Long id, @RequestBody AdminFeedbackHandleDto dto) {
        return feedbackService.handleAdminFeedback(id, dto) ? R.ok() : R.fail("Handle feedback failed");
    }
}
