package com.gym.modules.equipment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "器材分类树VO")
@Data
public class EquipmentCategoryTreeVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父ID")
    private Long parentId;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "子分类")
    private List<EquipmentCategoryTreeVO> children = new ArrayList<>();
}