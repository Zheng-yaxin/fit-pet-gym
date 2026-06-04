package com.gym.modules.equipment.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 器材巡检记录表实体类
 * 对应表：gym_equipment_check
 */
@Data
@TableName("gym_equipment_check") // 关联数据表名
public class EquipmentCheck {
    /**
     * 巡检ID
     */
    @TableId(type = IdType.AUTO) // 自增主键
    private Long id;

    /**
     * 器材ID（关联gym_equipment表）
     */
    private Long equipmentId;

    /**
     * 巡检人ID（关联员工表）
     */
    private Long checkerId;

    /**
     * 巡检时间
     */
    private Date checkTime;

    /**
     * 巡检结果（0正常 1异常）
     */
    private Integer result;

    /**
     * 异常描述（result=1时必填）
     */
    private String abnormalDesc;

    /**
     * 处理建议
     */
    private String suggestion;

    /**
     * 记录创建时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private Date createTime;

    /**
     * 逻辑删除（0未删 1已删），若表中无该字段可删除
     */
    @TableLogic
    private Integer deleted;
}