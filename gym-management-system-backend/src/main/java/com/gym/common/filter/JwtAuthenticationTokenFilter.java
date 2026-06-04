package com.gym.common.filter;

import com.gym.common.core.domain.model.LoginUser;
import com.gym.common.utils.JwtUtils;
import com.gym.common.utils.RedisCache;
import com.gym.common.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = JwtUtils.parseToken(token);

            // [安全解析] 兼容 Integer/Long/String 类型
            Object userIdObj = claims.get("userId");
            Object userTypeObj = claims.get("userType");

            if (userIdObj != null) {
                String userId = String.valueOf(userIdObj);

                // [关键逻辑] 构造 Redis Key
                // 默认使用 "SYS_USER" 兼容旧逻辑，或者从 Token 获取 userType
                String userType = (userTypeObj != null) ? String.valueOf(userTypeObj) : "SYS_USER";

                // 对应 AuthServiceImpl 中的 Key 生成策略: login_tokens:UserType:UserId
                // 如果是旧 token (没有userType)，尝试用旧 key 格式 (login_tokens:userId) 作为回退，或者直接要求重新登录
                String redisKey;
                if (userTypeObj == null) {
                    // 兼容旧代码生成的 Token (假设只有管理员)
                    redisKey = "login_tokens:" + userId;
                } else {
                    redisKey = "login_tokens:" + userType + ":" + userId;
                }

                LoginUser loginUser = redisCache.getCacheObject(redisKey);

                if (Objects.nonNull(loginUser) && Objects.isNull(SecurityUtils.getAuthentication())) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception e) {
            // Token 过期或非法，记录日志但不抛出异常，让 Spring Security 处理 401/403
            logger.error("Token认证解析失败: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }
}