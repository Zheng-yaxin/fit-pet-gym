package com.gym.common.config;

import com.gym.common.core.domain.model.LoginUser;
import com.gym.common.filter.JwtAuthenticationTokenFilter;
import com.gym.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.CollectionUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. 放行 Swagger 文档
                        .requestMatchers("/doc.html", "/webjars/**", "/v3/api-docs/**").permitAll()
                        // 2. 放行所有登录接口
                        .requestMatchers(
                                "/auth/admin/login",
                                "/auth/member/login",
                                "/auth/coach/login",
                                "/auth/register"
                        ).permitAll()
                        // 3. 放行静态资源
                        .requestMatchers("/files/**").permitAll()
                        // 4. 其他所有请求需要认证
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("ss")
    public PermissionService permissionService() {
        return new PermissionService();
    }

    public static class PermissionService {
        public boolean hasPermi(String permission) {
            if (permission == null || permission.isEmpty()) {
                return false;
            }
            try {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser == null || CollectionUtils.isEmpty(loginUser.getAuthorities())) {
                    return SecurityUtils.isAdmin(loginUser != null ? loginUser.getUserId() : null);
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}