package com.gym.modules.coach.domain.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 私教时间段VO
 */
@Data
public class PersonalTrainingSlotVo {
    private Long id;
    private Long coachId;
    private String coachName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer status;
    private String statusText;
}
