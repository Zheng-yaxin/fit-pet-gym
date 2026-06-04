package com.gym.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "提交器材报修DTO")
@Data
public class RepairLogAddDTO {

    @NotNull(message = "器材ID不能为空")
    @Schema(description = "器材ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long equipmentId;

    // 修改说明：移除了 @NotNull 注解。
    // 业务逻辑中会优先从 Token (SecurityContext) 获取当前登录用户ID作为 reporterId。
    // 只有在未登录等特殊情况（如允许匿名报修）下，才会依赖此字段或抛出异常。
    @Schema(description = "报修人ID（会员或员工），非必填，默认取当前登录用户")
    private Long reporterId;

    @NotBlank(message = "故障描述不能为空")
    @Schema(description = "故障描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String faultDesc;

    @Schema(description = "备注")
    private String remark;
}