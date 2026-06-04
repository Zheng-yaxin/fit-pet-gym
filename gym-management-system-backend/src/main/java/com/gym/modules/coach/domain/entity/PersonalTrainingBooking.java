package com.gym.modules.coach.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * 私教预约记录实体
 */
@Data
@TableName("gym_personal_training_booking")
public class PersonalTrainingBooking {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 教练ID
     */
    private Long coachId;

    /**
     * 时间段ID
     */
    private Long slotId;

    /**
     * 预约日期
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
     * 费用金额
     */
    private BigDecimal amount;

    /**
     * 状态 (0:待确认, 1:已确认, 2:已完成, 3:已取消)
     */
    private Integer status;

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

    @TableLogic
    private Integer deleted;
}
