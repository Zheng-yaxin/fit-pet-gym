package com.gym.common.core.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.common.enums.UserType;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.system.domain.entity.SysUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 登录用户身份权限对象
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户类型
     */
    private UserType userType;

    /**
     * 后台用户信息
     */
    private SysUser sysUser;

    /**
     * 会员用户信息
     */
    private Member member;

    /**
     * 教练用户信息
     */
    private Coach coach;

    /**
     * 系统用户构造
     */
    public LoginUser(SysUser sysUser) {
        this.sysUser = sysUser;
        this.userId = sysUser.getUserId();
        this.userType = UserType.SYS_USER;
    }

    /**
     * 会员用户构造
     */
    public LoginUser(Member member) {
        this.member = member;
        this.userId = member.getId();
        this.userType = UserType.MEMBER;
    }

    /**
     * 教练用户构造
     * 注意：需要在 UserType 枚举中确保有 COACH 类型
     */
    public LoginUser(Coach coach) {
        this.coach = coach;
        this.userId = coach.getId();
        // 假设 UserType 枚举中添加了 COACH 类型，如果没有请在 UserType 中添加
        this.userType = UserType.COACH;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        if (UserType.SYS_USER.equals(userType) && sysUser != null) {
            return sysUser.getPassword();
        }
        if (UserType.MEMBER.equals(userType) && member != null) {
            return member.getPassword();
        }
        // 假设 Coach 实体中有 getPassword 方法，如果是通过手机号验证码登录，这里可能需要调整
        if (UserType.COACH.equals(userType) && coach != null) {
            // 这里假设 Coach 也有密码字段，如果没有，视业务逻辑返回空或特定值
            return coach.getPassword();
        }
        return "";
    }

    @Override
    public String getUsername() {
        if (UserType.SYS_USER.equals(userType) && sysUser != null) {
            return sysUser.getUsername();
        }
        // 会员返回手机号作为“用户名”
        if (UserType.MEMBER.equals(userType) && member != null) {
            return member.getPhone();
        }
        // 教练返回手机号作为“用户名”
        if (UserType.COACH.equals(userType) && coach != null) {
            return coach.getPhone();
        }
        return "";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 如果后续有角色权限需求，需根据 userType 返回不同权限集合
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        if (UserType.SYS_USER.equals(userType) && sysUser != null) {
            return "0".equals(sysUser.getStatus());
        }
        if (UserType.MEMBER.equals(userType) && member != null) {
            return "0".equals(member.getStatus());
        }
        // 假设 Coach 也有 status 字段，0表示正常
        if (UserType.COACH.equals(userType) && coach != null) {
            // 注意：需确认 Coach 实体中状态字段是 Integer 还是 String，这里假设是 Integer 0
            // 如果 Coach 实体没有 getDeleted 或 getStatus，请根据实际情况调整
            return coach.getDeleted() != null && coach.getDeleted() == 0;
        }
        return true;
    }
}