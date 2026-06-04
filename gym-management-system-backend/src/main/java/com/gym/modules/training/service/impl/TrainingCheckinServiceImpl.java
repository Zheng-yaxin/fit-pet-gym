package com.gym.modules.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.modules.training.domain.entity.TrainingCheckin;
import com.gym.modules.training.mapper.TrainingCheckinMapper;
import com.gym.modules.training.service.ITrainingCheckinService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
public class TrainingCheckinServiceImpl extends ServiceImpl<TrainingCheckinMapper, TrainingCheckin> implements ITrainingCheckinService {
    @Override
    public TrainingCheckin active(Long memberId) {
        return getOne(new LambdaQueryWrapper<TrainingCheckin>()
                .eq(TrainingCheckin::getMemberId, memberId)
                .eq(TrainingCheckin::getStatus, "0")
                .orderByDesc(TrainingCheckin::getStartTime)
                .last("LIMIT 1"), false);
    }

    @Override
    public TrainingCheckin start(Long memberId, Long planId) {
        TrainingCheckin active = active(memberId);
        if (active != null) {
            return active;
        }

        TrainingCheckin checkin = new TrainingCheckin();
        checkin.setMemberId(memberId);
        checkin.setPlanId(planId);
        checkin.setStartTime(new Date());
        checkin.setStatus("0");
        save(checkin);
        return checkin;
    }

    @Override
    public TrainingCheckin end(Long memberId, Long checkinId) {
        TrainingCheckin checkin = getById(checkinId);
        if (checkin == null || !memberId.equals(checkin.getMemberId())) {
            throw new ServiceException("训练打卡记录不存在");
        }
        if ("1".equals(checkin.getStatus())) {
            throw new ServiceException("训练打卡已结束");
        }
        if (checkin.getStartTime() == null) {
            throw new ServiceException("训练打卡缺少开始时间");
        }

        checkin.setEndTime(new Date());
        checkin.setStatus("1");
        long minutes = Duration.between(
                checkin.getStartTime().toInstant(),
                checkin.getEndTime().toInstant()
        ).toMinutes();
        checkin.setDurationMinutes(Math.max(1, (int) minutes));
        updateById(checkin);
        return checkin;
    }
}
