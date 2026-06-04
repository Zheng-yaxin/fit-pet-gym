package com.gym.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "器材报修记录查询条件")
@Data
public class RepairLogQueryDTO {

    @Schema(description = "页码，默认1")
    private Integer pageNum = 1;

    @Schema(description = "页大小，默认10")
    private Integer pageSize = 10;

    @Schema(description = "器材ID")
    private Long equipmentId;

    @Schema(description = "报修人ID")
    private Long reporterId;

    @Schema(description = "维修状态 0待处理 1维修中 2已完成 3已取消")
    private Integer status;

    @Schema(description = "开始时间（yyyy-MM-dd HH:mm:ss）")
    private String startTime;

    @Schema(description = "结束时间（yyyy-MM-dd HH:mm:ss）")
    private String endTime;
}