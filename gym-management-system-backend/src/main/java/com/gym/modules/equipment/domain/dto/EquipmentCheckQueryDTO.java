package com.gym.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "器材巡检记录查询条件")
@Data
public class EquipmentCheckQueryDTO {

    @Schema(description = "页码，默认1")
    private Integer pageNum = 1;

    @Schema(description = "页大小，默认10")
    private Integer pageSize = 10;

    @Schema(description = "器材ID")
    private Long equipmentId;

    @Schema(description = "巡检人ID")
    private Long checkerId;

    @Schema(description = "巡检结果 0正常 1异常")
    private Integer result;

    @Schema(description = "开始时间（yyyy-MM-dd HH:mm:ss）")
    private String startTime;

    @Schema(description = "结束时间（yyyy-MM-dd HH:mm:ss）")
    private String endTime;
}