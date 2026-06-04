package com.gym.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "修改器材DTO")
@EqualsAndHashCode(callSuper = true)
@Data
public class EquipmentUpdateDTO extends EquipmentAddDTO {

    @NotNull(message = "器材ID不能为空")
    @Schema(description = "器材ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
}