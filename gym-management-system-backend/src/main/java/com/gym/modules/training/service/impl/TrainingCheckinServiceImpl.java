package com.gym.modules.training.service.impl;

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
    public TrainingCheckin start(Long memberId, Long planId) {
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
        checkin.setEndTime(new Date());
        checkin.setStatus("1");
        checkin.setDurationMinutes((int) Duration.between(
                checkin.getStartTime().toInstant(),
                checkin.getEndTime().toInstant()
        ).toMinutes());
        updateById(checkin);
        return checkin;
    }
}
