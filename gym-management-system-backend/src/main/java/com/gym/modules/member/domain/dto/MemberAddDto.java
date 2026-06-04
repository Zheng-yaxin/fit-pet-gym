package com.gym.modules.member.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "新增会员DTO")
public class MemberAddDto {
    // 已移除 username 属性

    private String password;
    private String nickname; // 注意：nickname 如果没有使用建议也可以后续清理，但此处仅按要求清理 username

    @NotBlank(message = "会员姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String gender;
    private Integer age;
    private String cardNo;
    private String cardType;
    private String joinDate;
    private String expireDate;
}