package com.gym.modules.coach.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.coach.domain.dto.CoachUpdateDto;
import com.gym.modules.coach.domain.vo.CoachInfoVo;
import com.gym.modules.coach.mapper.CoachMapper;
import com.gym.modules.coach.service.ICoachService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 教练Service实现
 */
@Service
public class CoachServiceImpl extends ServiceImpl<CoachMapper, Coach> implements ICoachService {

    @Override
    public Coach getByPhone(String phone) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Coach>()
                .eq(Coach::getPhone, phone)
                .eq(Coach::getDeleted, 0));
    }

    @Override
    public CoachInfoVo getCoachInfo(Long coachId) {
        Coach coach = getById(coachId);
        if (coach == null) {
            throw new ServiceException("教练不存在");
        }

        CoachInfoVo vo = new CoachInfoVo();
        BeanUtils.copyProperties(coach, vo);
        return vo;
    }

    @Override
    public boolean updateCoachInfo(Long coachId, CoachUpdateDto dto) {
        Coach coach = getById(coachId);
        if (coach == null) {
            throw new ServiceException("教练不存在");
        }

        BeanUtils.copyProperties(dto, coach);
        return updateById(coach);
    }
}
