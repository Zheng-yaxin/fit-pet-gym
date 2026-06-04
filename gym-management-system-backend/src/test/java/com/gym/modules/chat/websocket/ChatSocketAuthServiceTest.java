package com.gym.modules.chat.websocket;

import com.gym.common.core.domain.model.LoginUser;
import com.gym.common.utils.RedisCache;
import com.gym.modules.auth.domain.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class ChatSocketAuthServiceTest {

    @Test
    void authenticateShouldReturnUserForCachedToken() {
        ChatSocketAuthService authService = new ChatSocketAuthService();
        RedisCache redisCache = new RedisCache();

        Member member = new Member();
        member.setId(7L);
        member.setPhone("13800138000");
        member.setStatus("0");
        redisCache.setCacheObject("login_tokens:MEMBER:7", new LoginUser(member));

        ReflectionTestUtils.setField(authService, "redisCache", redisCache);

        String token = com.gym.common.utils.JwtUtils.createToken(java.util.Map.of(
                "userId", "7",
                "userType", "MEMBER"
        ));

        ChatSocketUser user = authService.authenticate(token);

        assertNotNull(user);
        assertEquals(7L, user.getUserId());
        assertEquals("MEMBER", user.getUserRole());
    }

    @Test
    void authenticateShouldRejectMissingToken() {
        ChatSocketAuthService authService = new ChatSocketAuthService();
        assertNull(authService.authenticate(null));
    }
}
