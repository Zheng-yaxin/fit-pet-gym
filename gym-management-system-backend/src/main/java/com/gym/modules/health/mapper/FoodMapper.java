package com.gym.modules.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.health.domain.entity.Food;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodMapper extends BaseMapper<Food> {
}