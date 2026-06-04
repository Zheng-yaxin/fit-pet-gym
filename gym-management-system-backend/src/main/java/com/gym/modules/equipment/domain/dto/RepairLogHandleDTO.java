package com.gym.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "处理维修记录DTO")
@Data
public class RepairLogHandleDTO {

    @NotNull(message = "报修记录ID不能为空")
    @Schema(description = "报修记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "维修人员ID")
    private Long repairerId;

    @Schema(description = "维修状态（1维修中 2已完成 3已取消）")
    private Integer status;

    @Schema(description = "维修完成时间（状态为已完成时填写）")
    private Date repairTime;

    @Schema(description = "维修费用")
    private BigDecimal cost;

    @Schema(description = "备注（如维修方案）")
    private String remark;
}