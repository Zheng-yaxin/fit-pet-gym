package com.gym.modules.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 健身房会员实体
 */
@Data
@TableName("gym_member")
public class Member {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会员姓名 (真名)
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
     * 帐号状态 (0正常 1停用)
     */
    private String status;

    /**
     * 会员卡号
     */
    private String cardNo;

    /**
     * 会员卡类型 (年卡/月卡/次卡)
     */
    private String cardType;

    // 已移除 username 属性

    /**
     * 入会时间
     */
    private Date joinDate;

    /**
     * 过期时间
     */
    private Date expireDate;

    /**
     * 余额 (使用BigDecimal保证金额计算精度)
     */
    private BigDecimal balance;

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