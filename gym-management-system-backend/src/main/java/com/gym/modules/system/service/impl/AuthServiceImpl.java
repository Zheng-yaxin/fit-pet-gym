package com.gym.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gym.common.core.domain.model.LoginUser;
import com.gym.common.enums.UserType;
import com.gym.common.exception.ServiceException;
import com.gym.common.utils.JwtUtils;
import com.gym.common.utils.RedisCache;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.auth.domain.entity.Member;
import com.gym.modules.member.mapper.MemberMapper;
import com.gym.modules.system.domain.dto.LoginBody;
import com.gym.modules.system.domain.dto.RegisterBody;
import com.gym.modules.system.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private com.gym.modules.coach.mapper.CoachMapper coachMapper;

    // --- 管理员登录 ---
    @Override
    public String loginAdmin(LoginBody loginBody) {
        if (!StringUtils.hasText(loginBody.getUsername())) {
            throw new ServiceException("用户名不能为空");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginBody.getUsername(), loginBody.getPassword());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            throw new ServiceException("登录失败，用户名或密码错误");
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return createTokenAndCache(loginUser);
    }

    // --- 会员登录 ---
    @Override
    public String loginMember(LoginBody loginBody) {
        String phone = loginBody.getPhone();
        if (!StringUtils.hasText(phone)) {
            phone = loginBody.getUsername();
        }
        if (!StringUtils.hasText(phone)) {
            throw new ServiceException("手机号不能为空");
        }

        Member member = memberMapper.selectOne(new LambdaQueryWrapper<Member>()
                .eq(Member::getPhone, phone));

        if (member == null) {
            throw new ServiceException("登录失败，用户不存在");
        }
        if (!SecurityUtils.matchesPassword(loginBody.getPassword(), member.getPassword())) {
            throw new ServiceException("登录失败，密码错误");
        }
        if ("1".equals(member.getStatus())) {
            throw new ServiceException("账号已被停用");
        }

        LoginUser loginUser = new LoginUser(member);
        return createTokenAndCache(loginUser);
    }

    // --- 会员注册 ---
    @Override
    public void registerMember(RegisterBody registerBody) {
        if (!StringUtils.hasText(registerBody.getPhone())) {
            throw new ServiceException("手机号不能为空");
        }
        Long count = memberMapper.selectCount(new LambdaQueryWrapper<Member>()
                .eq(Member::getPhone, registerBody.getPhone()));
        if (count > 0) {
            throw new ServiceException("注册失败，手机号已存在");
        }

        Member member = new Member();
        member.setPhone(registerBody.getPhone());
        member.setName(registerBody.getName());
        member.setGender(registerBody.getGender());
        member.setPassword(SecurityUtils.encryptPassword(registerBody.getPassword()));
        member.setStatus("0");
        memberMapper.insert(member);
    }

    // --- 教练登录 ---
    @Override
    public String loginCoach(LoginBody loginBody) {
        String phone = loginBody.getPhone();
        if (!StringUtils.hasText(phone)) phone = loginBody.getUsername();
        if (!StringUtils.hasText(phone)) throw new ServiceException("手机号不能为空");

        com.gym.modules.coach.domain.entity.Coach coach = coachMapper.selectOne(
                new LambdaQueryWrapper<com.gym.modules.coach.domain.entity.Coach>()
                        .eq(com.gym.modules.coach.domain.entity.Coach::getPhone, phone)
                        .eq(com.gym.modules.coach.domain.entity.Coach::getDeleted, 0));

        if (coach == null) {
            throw new ServiceException("登录失败，教练账号不存在");
        }
        if (!SecurityUtils.matchesPassword(loginBody.getPassword(), coach.getPassword())) {
            throw new ServiceException("登录失败，密码错误");
        }
        if ("1".equals(coach.getStatus())) {
            throw new ServiceException("账号已被停用");
        }

        LoginUser loginUser = new LoginUser(coach);
        return createTokenAndCache(loginUser);
    }

    /**
     * 生成Token并缓存
     */
    private String createTokenAndCache(LoginUser loginUser) {
        String userId = String.valueOf(loginUser.getUserId());
        String userType = loginUser.getUserType().getCode();

        // [修改点] Redis Key 包含 userType，防止 ID 冲突
        // 例如: login_tokens:COACH:1 vs login_tokens:MEMBER:1
        String redisKey = "login_tokens:" + userType + ":" + userId;

        // 存入 Redis (7天)
        redisCache.setCacheObject(redisKey, loginUser, 7, TimeUnit.DAYS);

        // 生成 JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        // [修改点] 确保 userType 放入 Token，以便 Filter 解析时知道查哪个 Redis Key
        claims.put("userType", userType);

        return JwtUtils.createToken(claims);
    }
}