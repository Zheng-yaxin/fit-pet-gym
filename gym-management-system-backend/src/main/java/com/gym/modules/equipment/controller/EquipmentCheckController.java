package com.gym.modules.equipment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.R;
import com.gym.modules.equipment.domain.dto.EquipmentCheckAddDTO;
import com.gym.modules.equipment.domain.dto.EquipmentCheckQueryDTO;
import com.gym.modules.equipment.domain.vo.EquipmentCheckVO;
import com.gym.modules.equipment.service.EquipmentCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "器材点检管理")
@RestController
@RequestMapping("/equipment/check")
public class EquipmentCheckController {

    @Autowired
    private EquipmentCheckService checkService;

    @Operation(summary = "分页查询点检记录")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermi('equipment:check:list')")
    public R<Page<EquipmentCheckVO>> page(EquipmentCheckQueryDTO queryDTO) {
        return R.ok(checkService.pageList(queryDTO));
    }

    @Operation(summary = "新增点检记录")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('equipment:check:add')")
    public R<Void> add(@Valid @RequestBody EquipmentCheckAddDTO addDTO) {
        // 修改：对应 Service 中的 addCheck 方法
        checkService.addCheck(addDTO);
        return R.ok();
    }
}