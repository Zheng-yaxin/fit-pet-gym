package com.gym.modules.equipment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.R;
import com.gym.modules.equipment.domain.dto.RepairLogAddDTO;
import com.gym.modules.equipment.domain.dto.RepairLogHandleDTO;
import com.gym.modules.equipment.domain.dto.RepairLogQueryDTO;
import com.gym.modules.equipment.domain.vo.RepairLogVO;
import com.gym.modules.equipment.service.RepairLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "器材报修管理")
@RestController
@RequestMapping("/equipment/repair")
public class RepairLogController {

    @Autowired
    private RepairLogService repairLogService;

    // ================== 新增的 create 接口 (解决 404 问题) ==================
    @Operation(summary = "提交报修申请(Create)")
    @PostMapping("/create")
    public R<Void> create(@Valid @RequestBody RepairLogAddDTO addDTO) {
        // 复用 Service 中已有的报修逻辑，确保数据一致性
        repairLogService.addReport(addDTO);
        return R.ok();
    }
    // ====================================================================

    @Operation(summary = "提交报修申请")
    @PostMapping("/submit")
    @PreAuthorize("@ss.hasPermi('equipment:repair:submit')")
    public R<Void> submit(@Valid @RequestBody RepairLogAddDTO addDTO) {
        // 修改：对应 Service 中的 addReport 方法
        repairLogService.addReport(addDTO);
        return R.ok();
    }

    @Operation(summary = "处理报修申请")
    @PostMapping("/handle")
    @PreAuthorize("@ss.hasPermi('equipment:repair:handle')")
    public R<Void> handle(@Valid @RequestBody RepairLogHandleDTO handleDTO) {
        repairLogService.handle(handleDTO);
        return R.ok();
    }

    @Operation(summary = "分页查询报修记录")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermi('equipment:repair:list')")
    public R<Page<RepairLogVO>> page(RepairLogQueryDTO queryDTO) {
        return R.ok(repairLogService.pageList(queryDTO));
    }

    @Operation(summary = "查询我的报修记录")
    @GetMapping("/my")
    public R<Page<RepairLogVO>> myRepair(RepairLogQueryDTO queryDTO) {
        return R.ok(repairLogService.myRepair(queryDTO));
    }

    @Operation(summary = "查询报修详情")
    @GetMapping("/{id}")
    public R<RepairLogVO> detail(@PathVariable("id") Long id) {
        return R.ok(repairLogService.detail(id));
    }
}
