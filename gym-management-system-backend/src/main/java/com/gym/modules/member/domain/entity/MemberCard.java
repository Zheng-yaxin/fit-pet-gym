package com.gym.modules.member.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gym_member_card")
public class MemberCard {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    @TableField(exist = false)
    private String memberName;
    private String cardNo;
    private String cardType;
    private Date issueDate;
    private Date expireDate;
    private Integer remainingTimes;
    private String status;
    private Integer suspendCount;
    private Integer suspendYear;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
