package com.gym.modules.traffic.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gym_gym_area")
public class GymArea {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer capacity;
    private String location;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableLogic
    private Integer deleted;
}
