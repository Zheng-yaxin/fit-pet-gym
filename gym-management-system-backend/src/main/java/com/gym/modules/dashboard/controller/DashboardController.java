package com.gym.modules.dashboard.controller;

import com.gym.common.result.R;
import com.gym.modules.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "管理端仪表盘")
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/overview")
    @Operation(summary = "获取仪表盘概览")
    public R<Map<String, Object>> overview() {
        return R.ok(dashboardService.overview());
    }
}
