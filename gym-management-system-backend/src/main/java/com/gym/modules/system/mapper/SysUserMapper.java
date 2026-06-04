package com.gym.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.system.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员表 数据层
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}