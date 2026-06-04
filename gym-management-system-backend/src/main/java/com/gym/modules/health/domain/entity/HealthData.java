package com.gym.modules.health.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gym.common.json.FlexibleDateDeserializer;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员健康数据实体
 */
@Data
@TableName("gym_health_data")
public class HealthData {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 性别 (0:女, 1:男) - 用于计算基础代谢
     */
    private Integer gender;

    /**
     * 出生日期 - 用于计算年龄
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = FlexibleDateDeserializer.class)
    private Date birthDate;

    /**
     * 身高 (cm)
     */
    private BigDecimal height;

    /**
     * 体重 (kg)
     */
    private BigDecimal weight;

    /**
     * BMI指数
     */
    private BigDecimal bmi;

    /**
     * 体脂率 (%)
     */
    private BigDecimal bodyFatRate;

    /**
     * 测量时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = FlexibleDateDeserializer.class)
    private Date measureTime;

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
