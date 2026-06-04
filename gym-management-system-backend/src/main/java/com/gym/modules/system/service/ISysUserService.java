package com.gym.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.system.domain.entity.SysUser;

/**
 * 管理员业务层接口
 */
public interface ISysUserService extends IService<SysUser> {
    /**
     * 根据用户名查询管理员
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 校验用户名是否唯一
     */
    boolean checkUserNameUnique(String userName);

    /**
     * 新增管理员
     */
    boolean insertUser(SysUser user);
}