package com.gym.modules.coach.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.coach.domain.entity.Coach;
import com.gym.modules.coach.domain.dto.CoachUpdateDto;
import com.gym.modules.coach.domain.vo.CoachInfoVo;

/**
 * 教练Service接口
 */
public interface ICoachService extends IService<Coach> {
    /**
     * 根据手机号查询教练
     */
    Coach getByPhone(String phone);

    /**
     * 获取教练信息
     */
    CoachInfoVo getCoachInfo(Long coachId);

    /**
     * 更新教练信息
     */
    boolean updateCoachInfo(Long coachId, CoachUpdateDto dto);
}
