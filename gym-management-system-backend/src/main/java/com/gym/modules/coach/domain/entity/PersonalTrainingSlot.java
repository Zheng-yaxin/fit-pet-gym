package com.gym.modules.coach.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * 私教可预约时间段实体
 */
@Data
@TableName("gym_personal_training_slot")
public class PersonalTrainingSlot {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 教练ID
     */
    private Long coachId;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 状态 (0:可预约, 1:已预约, 2:已完成, 3:已取消)
     */
    private Integer status;

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

    @TableLogic
    private Integer deleted;
}
