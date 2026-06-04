package com.gym.modules.equipment.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 报修维修记录表实体类
 * 对应表：gym_repair_log
 */
@Data
@TableName("gym_repair_log")
public class RepairLog {
    /**
     * 报修ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 器材ID
     */
    private Long equipmentId;

    /**
     * 报修人ID
     */
    private Long reporterId;

    /**
     * 报修时间
     */
    private Date reportTime;

    /**
     * 故障描述
     */
    private String faultDesc;

    /**
     * 维修人员ID
     */
    private Long repairerId;

    /**
     * 维修状态（0待处理 1维修中 2已完成 3已取消）
     */
    private Integer status;

    /**
     * 维修完成时间
     */
    private Date repairTime;

    /**
     * 维修费用
     */
    private BigDecimal cost;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}