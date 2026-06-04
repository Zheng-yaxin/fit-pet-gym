package com.gym.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "新增器材分类DTO")
@Data
public class EquipmentCategoryAddDTO {

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "父分类ID不能为空")
    @Schema(description = "父分类ID（0为顶级分类）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long parentId;

    @Schema(description = "排序值（越小越靠前）")
    private Integer sort;

    @Schema(description = "分类描述")
    private String description;
}