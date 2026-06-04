package com.gym.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "新增器材DTO")
@Data
public class EquipmentAddDTO {

    @NotBlank(message = "器材名称不能为空")
    @Schema(description = "器材名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "器材编号")
    private String code;

    @NotNull(message = "分类不能为空")
    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "型号")
    private String model;

    @Schema(description = "器材图片URL")
    private String imageUrl;

    @Schema(description = "购买日期")
    private Date buyDate;

    @Schema(description = "购买价格")
    private BigDecimal price;

    @Schema(description = "放置位置")
    private String location;

    @Schema(description = "当前负责人ID")
    private Long currentManagerId;

    @Schema(description = "备注")
    private String remark;
}