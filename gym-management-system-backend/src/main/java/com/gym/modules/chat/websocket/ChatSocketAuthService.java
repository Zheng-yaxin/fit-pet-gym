package com.gym.modules.chat.websocket;

import com.gym.common.core.domain.model.LoginUser;
import com.gym.common.enums.UserType;
import com.gym.common.utils.JwtUtils;
import com.gym.common.utils.RedisCache;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ChatSocketAuthService {

    @Resource
    private RedisCache redisCache;

    public ChatSocketUser authenticate(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            Claims claims = JwtUtils.parseToken(token);
            Object userIdObj = claims.get("userId");
            Object userTypeObj = claims.get("userType");
            if (userIdObj == null || userTypeObj == null) {
                return null;
            }

            String userId = String.valueOf(userIdObj);
            String userType = String.valueOf(userTypeObj);
            LoginUser loginUser = redisCache.getCacheObject("login_tokens:" + userType + ":" + userId);
            if (loginUser == null) {
                return null;
            }

            return new ChatSocketUser(Long.valueOf(userId), UserType.valueOf(userType).getCode());
        } catch (Exception e) {
            return null;
        }
    }
}
