package com.gym.modules.member.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "购买/续费会员卡DTO")
public class CardBuyDto {
    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @NotBlank(message = "卡类型不能为空")
    private String cardType;

    private BigDecimal amount;
    private Integer duration; // 时长(月)
    private Integer times; // 次数(仅次卡)
    private String remark;
}