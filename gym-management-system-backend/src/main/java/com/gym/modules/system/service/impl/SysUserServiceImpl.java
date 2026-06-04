package com.gym.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.system.domain.entity.SysUser;
import com.gym.modules.system.mapper.SysUserMapper;
import com.gym.modules.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 管理员业务层实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Override
    public SysUser selectUserByUserName(String userName) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, userName));
    }

    @Override
    public boolean checkUserNameUnique(String userName) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, userName));
        return count == 0;
    }

    @Override
    public boolean insertUser(SysUser user) {
        // 1. 校验用户名
        if (!checkUserNameUnique(user.getUsername())) {
            throw new ServiceException("新增管理员失败，账号 '" + user.getUsername() + "' 已存在");
        }
        // 2. 密码加密
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        } else {
            // 默认密码
            user.setPassword(SecurityUtils.encryptPassword("123456"));
        }
        // 3. 设置默认属性
        user.setStatus("0"); // 正常
        user.setDeleted(0);

        return save(user);
    }
}