package com.gym.modules.equipment.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 器材信息表实体类
 * 对应表：gym_equipment
 */
@Schema(description = "健身器材信息")
@Data
@TableName("gym_equipment")
public class Equipment {

    @TableId(type = IdType.AUTO)
    @Schema(description = "器材ID")
    private Long id;

    @Schema(description = "器材名称")
    private String name;

    @Schema(description = "器材编号")
    private String code;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "器材型号")
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

    @Schema(description = "状态：0正常 1维护中 2损坏 3报废")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updateTime;

    @TableLogic
    @Schema(description = "逻辑删除：0未删 1已删")
    private Integer deleted;
}