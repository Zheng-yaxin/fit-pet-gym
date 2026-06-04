package com.gym.modules.equipment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.result.R;
import com.gym.modules.equipment.domain.dto.EquipmentAddDTO;
import com.gym.modules.equipment.domain.dto.EquipmentQueryDTO;
import com.gym.modules.equipment.domain.dto.EquipmentUpdateDTO;
import com.gym.modules.equipment.domain.vo.EquipmentVO;
import com.gym.modules.equipment.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "健身器材管理")
@RestController
@RequestMapping("/equipment")
@Validated
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @Operation(summary = "分页查询器材")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermi('equipment:equipment:list')")
    public R<Page<EquipmentVO>> page(EquipmentQueryDTO queryDTO) {
        return R.ok(equipmentService.pageList(queryDTO));
    }

    @Operation(summary = "会员端报修器材选项")
    @GetMapping("/member-options")
    public R<Page<EquipmentVO>> memberOptions(EquipmentQueryDTO queryDTO) {
        return R.ok(equipmentService.memberOptions(queryDTO));
    }

    @Operation(summary = "器材详情")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('equipment:equipment:query')")
    public R<EquipmentVO> detail(@PathVariable("id") Long id) {
        return R.ok(equipmentService.detail(id));
    }

    @Operation(summary = "新增器材")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('equipment:equipment:add')")
    public R<Void> add(@Valid @RequestBody EquipmentAddDTO addDTO) {
        equipmentService.add(addDTO);
        return R.ok();
    }

    @Operation(summary = "修改器材")
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('equipment:equipment:edit')")
    public R<Void> update(@PathVariable("id") Long id, @Valid @RequestBody EquipmentUpdateDTO updateDTO) {
        updateDTO.setId(id);
        equipmentService.update(updateDTO);
        return R.ok();
    }

    @Operation(summary = "删除器材")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('equipment:equipment:remove')")
    public R<Void> delete(@PathVariable("id") Long id) {
        equipmentService.deleteById(id);
        return R.ok();
    }

    @Operation(summary = "修改器材状态")
    @PatchMapping("/{id}/status")
    @PreAuthorize("@ss.hasPermi('equipment:equipment:status')")
    public R<Void> changeStatus(@PathVariable("id") Long id, @RequestParam("status") Integer status) {
        equipmentService.changeStatus(id, status);
        return R.ok();
    }
}
