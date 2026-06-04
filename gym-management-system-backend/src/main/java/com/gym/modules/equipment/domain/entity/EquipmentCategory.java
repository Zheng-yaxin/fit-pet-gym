package com.gym.modules.equipment.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

/**
 * 器材分类表实体类
 * 对应表：gym_equipment_category
 */
@Schema(description = "器材分类")
@Data
@TableName("gym_equipment_category")
public class EquipmentCategory {

    @TableId(type = IdType.AUTO)
    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父分类ID（0为顶级）")
    private Long parentId;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "分类描述")
    private String description;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updateTime;

    @TableLogic
    @Schema(description = "逻辑删除")
    private Integer deleted;
}