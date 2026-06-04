package com.gym.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "修改器材分类DTO")
@EqualsAndHashCode(callSuper = true)
@Data
public class EquipmentCategoryUpdateDTO extends EquipmentCategoryAddDTO {

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
}