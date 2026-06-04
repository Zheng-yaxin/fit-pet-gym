package com.gym.modules.member.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("gym_member_transaction")
public class MemberTransaction {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId; // 会员ID

    private String transactionType; // 交易类型(充值/办卡/消费/退款)

    private BigDecimal amount; // 交易金额

    // 核心修复：添加缺失的字段，对应数据库中的 balance_after
    private BigDecimal balanceAfter; // 交易后余额

    private String cardNo; // 关联卡号

    private String remark; // 备注

    @TableField(fill = FieldFill.INSERT)
    private Date createTime; // 交易时间

    private String operator; // 操作人
}