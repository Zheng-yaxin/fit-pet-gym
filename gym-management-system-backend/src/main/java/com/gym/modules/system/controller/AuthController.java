package com.gym.modules.system.controller;

import com.gym.common.result.R;
import com.gym.modules.system.domain.dto.LoginBody;
import com.gym.modules.system.domain.dto.RegisterBody;
import com.gym.modules.system.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gym.common.core.domain.model.LoginUser;
import com.gym.common.utils.RedisCache;
import com.gym.common.utils.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @Autowired
    private RedisCache redisCache;

    @PostMapping("/admin/login")
    @Operation(summary = "管理员登录")
    public R<Map<String, String>> loginAdmin(@RequestBody LoginBody loginBody) {
        String token = authService.loginAdmin(loginBody);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return R.ok(map);
    }

    @PostMapping("/member/login")
    @Operation(summary = "会员登录")
    public R<Map<String, String>> loginMember(@RequestBody LoginBody loginBody) {
        String token = authService.loginMember(loginBody);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return R.ok(map);
    }

    @PostMapping("/coach/login")
    @Operation(summary = "教练登录")
    public R<Map<String, String>> loginCoach(@RequestBody LoginBody loginBody) {
        String token = authService.loginCoach(loginBody);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return R.ok(map);
    }

    @PostMapping("/register")
    @Operation(summary = "会员注册")
    public R<Void> register(@RequestBody RegisterBody registerBody) {
        authService.registerMember(registerBody);
        return R.ok();
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public R<Void> logout() {
        Authentication authentication = SecurityUtils.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            String redisKey = "login_tokens:" + loginUser.getUserType().getCode() + ":" + loginUser.getUserId();
            redisCache.deleteObject(redisKey);
        }
        SecurityContextHolder.clearContext();
        return R.ok();
    }
}
