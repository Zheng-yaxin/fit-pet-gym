package com.gym.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "器材分页查询参数")
@Data
public class EquipmentQueryDTO {

    @Schema(description = "页码，默认1")
    private Integer pageNum = 1;

    @Schema(description = "页大小，默认10")
    private Integer pageSize = 10;

    @Schema(description = "关键词（名称或编号模糊搜索）")
    private String keyword;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "状态 0正常 1维护中 2损坏 3报废")
    private Integer status;

    @Schema(description = "负责人ID")
    private Long currentManagerId;
}