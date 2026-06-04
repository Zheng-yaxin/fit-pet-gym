package com.gym.modules.traffic.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gym_traffic_snapshot")
public class TrafficSnapshot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long areaId;
    private Integer currentCount;
    private Integer capacity;
    private Integer heatLevel;
    private Date snapshotTime;

    @TableField(exist = false)
    private String areaName;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
