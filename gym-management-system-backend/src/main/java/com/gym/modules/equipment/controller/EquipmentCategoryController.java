package com.gym.modules.equipment.controller;

import com.gym.common.result.R;
import com.gym.modules.equipment.domain.dto.EquipmentCategoryAddDTO;
import com.gym.modules.equipment.domain.dto.EquipmentCategoryUpdateDTO;
import com.gym.modules.equipment.domain.vo.EquipmentCategoryTreeVO;
import com.gym.modules.equipment.service.EquipmentCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "器材分类管理")
@RestController
@RequestMapping("/equipment/category")
public class EquipmentCategoryController {

    @Autowired
    private EquipmentCategoryService categoryService;

    @Operation(summary = "获取分类树形结构")
    @GetMapping("/tree")
    public R<List<EquipmentCategoryTreeVO>> getTree() {
        // 修改：对应 Service 中的 treeList 方法
        return R.ok(categoryService.treeList());
    }

    @Operation(summary = "新增分类")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('equipment:category:add')")
    public R<Void> add(@Valid @RequestBody EquipmentCategoryAddDTO addDTO) {
        categoryService.add(addDTO);
        return R.ok();
    }

    @Operation(summary = "修改分类")
    @PutMapping
    @PreAuthorize("@ss.hasPermi('equipment:category:edit')")
    public R<Void> update(@Valid @RequestBody EquipmentCategoryUpdateDTO updateDTO) {
        categoryService.update(updateDTO);
        return R.ok();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('equipment:category:remove')")
    public R<Void> delete(@PathVariable("id") Long id) {
        categoryService.deleteById(id);
        return R.ok();
    }
}
