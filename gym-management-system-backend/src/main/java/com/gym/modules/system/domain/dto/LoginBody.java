package com.gym.modules.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录对象
 */
@Data
@Schema(description = "用户登录表单")
public class LoginBody {
    /**
     * 管理员登录使用
     */
    @Schema(description = "管理员用户名 (管理员登录必填)", example = "admin")
    private String username;

    /**
     * 会员登录使用
     */
    @Schema(description = "会员手机号 (会员登录必填)", example = "13800138000")
    private String phone;

    /**
     * 密码
     */
    @Schema(description = "密码 (必填)", example = "123456")
    private String password;
}