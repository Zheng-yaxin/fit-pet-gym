package com.gym.modules.training.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gym.common.result.R;
import com.gym.modules.training.domain.entity.TrainingCheckin;
import com.gym.modules.training.domain.entity.TrainingLog;
import com.gym.modules.training.domain.vo.TrainingReviewVo;
import com.gym.modules.training.service.ITrainingCheckinService;
import com.gym.modules.training.service.ITrainingLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "管理端训练日志")
@RestController
@RequestMapping("/admin/training")
public class AdminTrainingController {
    @Autowired
    private ITrainingLogService logService;

    @Autowired
    private ITrainingCheckinService checkinService;

    @GetMapping("/logs")
    @Operation(summary = "查询训练日志")
    @PreAuthorize("@ss.hasPermi('training:log:list')")
    public R<List<TrainingLog>> logs() {
        return R.ok(logService.list(new LambdaQueryWrapper<TrainingLog>()
                .orderByDesc(TrainingLog::getTrainingDate)));
    }

    @GetMapping("/reviews")
    @Operation(summary = "查询会员训练复盘汇总")
    @PreAuthorize("@ss.hasPermi('training:log:list')")
    public R<List<TrainingReviewVo>> reviews() {
        return R.ok(logService.buildAdminReviews());
    }

    @GetMapping("/checkins/active")
    @Operation(summary = "查询进行中的训练打卡")
    @PreAuthorize("@ss.hasPermi('training:log:list')")
    public R<List<TrainingCheckin>> activeCheckins() {
        return R.ok(checkinService.list(new LambdaQueryWrapper<TrainingCheckin>()
                .eq(TrainingCheckin::getStatus, "0")
                .orderByDesc(TrainingCheckin::getStartTime)));
    }
}
