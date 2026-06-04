package com.gym.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Schema(description = "新增器材巡检DTO")
@Data
public class EquipmentCheckAddDTO {

    @NotNull(message = "器材ID不能为空")
    @Schema(description = "器材ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long equipmentId;

    @NotNull(message = "巡检人ID不能为空")
    @Schema(description = "巡检人ID（员工ID）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long checkerId;

    @NotNull(message = "巡检时间不能为空")
    @Schema(description = "巡检时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date checkTime;

    @NotNull(message = "巡检结果不能为空")
    @Schema(description = "巡检结果（0正常 1异常）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer result;

    @Schema(description = "异常描述（result=1时必填）")
    private String abnormalDesc;

    @Schema(description = "处理建议")
    private String suggestion;
}