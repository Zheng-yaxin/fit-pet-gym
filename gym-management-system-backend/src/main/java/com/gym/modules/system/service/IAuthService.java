package com.gym.modules.system.service;

import com.gym.modules.system.domain.dto.LoginBody;
import com.gym.modules.system.domain.dto.RegisterBody;

public interface IAuthService {
    /**
     * 管理员登录
     */
    String loginAdmin(LoginBody loginBody);

    /**
     * 会员登录
     */
    String loginMember(LoginBody loginBody);

    /**
     * 教练登录
     */
    String loginCoach(LoginBody loginBody);

    /**
     * 会员注册
     */
    void registerMember(RegisterBody registerBody);
}