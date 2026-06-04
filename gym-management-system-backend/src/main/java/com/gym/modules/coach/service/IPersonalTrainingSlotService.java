package com.gym.modules.coach.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.coach.domain.entity.PersonalTrainingSlot;
import com.gym.modules.coach.domain.dto.PersonalTrainingSlotAddDto;
import com.gym.modules.coach.domain.vo.PersonalTrainingSlotVo;
import java.time.LocalDate;
import java.util.List;

/**
 * 私教时间段Service接口
 */
public interface IPersonalTrainingSlotService extends IService<PersonalTrainingSlot> {
    /**
     * 教练添加可预约时间段
     */
    boolean addSlot(Long coachId, PersonalTrainingSlotAddDto dto);

    /**
     * 教练查询自己的时间段
     */
    List<PersonalTrainingSlotVo> getCoachSlots(Long coachId, LocalDate startDate, LocalDate endDate);

    /**
     * 会员查询教练的可预约时间段
     */
    List<PersonalTrainingSlotVo> getAvailableSlots(Long coachId, LocalDate startDate, LocalDate endDate);

    /**
     * 删除时间段
     */
    boolean deleteSlot(Long coachId, Long slotId);

    /**
     * 更新时间段状态
     */
    boolean updateSlotStatus(Long slotId, Integer status);
}
