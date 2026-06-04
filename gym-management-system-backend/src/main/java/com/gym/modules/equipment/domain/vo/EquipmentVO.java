package com.gym.modules.equipment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "器材返回VO")
@Data
public class EquipmentVO {

    @Schema(description = "器材ID")
    private Long id;

    @Schema(description = "器材名称")
    private String name;

    @Schema(description = "器材编号")
    private String code;

    @Schema(description = "分类名称")
    private String categoryName;

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

    @Schema(description = "位置")
    private String location;

    @Schema(description = "当前负责人ID")
    private Long currentManagerId;

    @Schema(description = "当前负责人姓名")
    private String managerName;

    @Schema(description = "状态 0正常 1维护中 2损坏 3报废")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private Date createTime;
}