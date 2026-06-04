package com.gym.modules.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员注册对象
 */
@Data
@Schema(description = "会员注册表单")
public class RegisterBody {

    @Schema(description = "手机号 (必填，作为登录账号)", example = "13800138000")
    private String phone;

    @Schema(description = "真实姓名 (必填)", example = "张三")
    private String name;

    @Schema(description = "密码 (必填)", example = "123456")
    private String password;

    @Schema(description = "性别 (0女 1男)", example = "1")
    private Integer gender;
}