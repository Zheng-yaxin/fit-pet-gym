package com.gym.modules.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.health.domain.entity.BodyImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 身材照片 Mapper 接口
 */
@Mapper
public interface BodyImageMapper extends BaseMapper<BodyImage> {
}