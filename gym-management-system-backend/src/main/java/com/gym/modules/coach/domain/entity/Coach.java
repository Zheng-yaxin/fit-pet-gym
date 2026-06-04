package com.gym.modules.coach.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 健身教练实体
 */
@Data
@TableName("gym_coach")
public class Coach {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 教练姓名
     */
    private String name;

    /**
     * 手机号 (登录账号)
     */
    private String phone;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 性别 (0女 1男 2未知)
     */
    private Integer gender;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 专长领域(如: 瑜伽,HIIT,力量训练)
     */
    private String specialties;

    /**
     * 从业年限
     */
    private Integer experienceYears;

    /**
     * 资格证书
     */
    private String certification;

    /**
     * 私教课时费(元/小时)
     */
    private BigDecimal hourlyRate;

    /**
     * 帐号状态 (0正常 1停用)
     */
    private String status;

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
