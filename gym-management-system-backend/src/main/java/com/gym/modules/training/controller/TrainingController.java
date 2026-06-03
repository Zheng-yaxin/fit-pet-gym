package com.gym.modules.training.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gym.common.result.R;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.training.domain.dto.TrainingPlanGenerateDTO;
import com.gym.modules.training.domain.entity.TrainingCheckin;
import com.gym.modules.training.domain.entity.TrainingLog;
import com.gym.modules.training.domain.entity.TrainingPlan;
import com.gym.modules.training.domain.vo.TrainingReviewVo;
import com.gym.modules.training.service.ITrainingCheckinService;
import com.gym.modules.training.service.ITrainingLogService;
import com.gym.modules.training.service.ITrainingPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Tag(name = "会员训练")
@RestController
@RequestMapping("/training")
public class TrainingController {
    @Autowired
    private ITrainingPlanService planService;

    @Autowired
    private ITrainingLogService logService;

    @Autowired
    private ITrainingCheckinService checkinService;

    @PostMapping("/plans/generate")
    @Operation(summary = "生成智能训练计划框架")
    public R<TrainingPlan> generatePlan(@RequestBody TrainingPlanGenerateDTO dto) {
        return R.ok(planService.generate(SecurityUtils.getUserId(), dto));
    }

    @GetMapping("/plans/current")
    @Operation(summary = "获取当前训练计划")
    public R<TrainingPlan> currentPlan() {
        return R.ok(planService.current(SecurityUtils.getUserId()));
    }

    @PostMapping("/checkin/start")
    @Operation(summary = "开始训练打卡")
    public R<TrainingCheckin> startCheckin(@RequestParam(name = "planId", required = false) Long planId) {
        return R.ok(checkinService.start(SecurityUtils.getUserId(), planId));
    }

    @PostMapping("/checkin/end")
    @Operation(summary = "结束训练打卡")
    public R<TrainingCheckin> endCheckin(@RequestParam("checkinId") Long checkinId) {
        return R.ok(checkinService.end(SecurityUtils.getUserId(), checkinId));
    }

    @PostMapping("/logs")
    @Operation(summary = "记录训练日志")
    public R<Void> addLog(@RequestBody TrainingLog log) {
        log.setMemberId(SecurityUtils.getUserId());
        log.setTrainingDate(log.getTrainingDate() == null ? new Date() : log.getTrainingDate());
        return logService.save(log) ? R.ok() : R.fail("保存训练日志失败");
    }

    @GetMapping("/logs")
    @Operation(summary = "查询我的训练日志")
    public R<List<TrainingLog>> myLogs() {
        return R.ok(logService.list(new LambdaQueryWrapper<TrainingLog>()
                .eq(TrainingLog::getMemberId, SecurityUtils.getUserId())
                .orderByDesc(TrainingLog::getTrainingDate)));
    }

    @GetMapping("/review")
    @Operation(summary = "获取训练复盘和成长进度")
    public R<TrainingReviewVo> review() {
        return R.ok(logService.buildMemberReview(SecurityUtils.getUserId()));
    }
}
