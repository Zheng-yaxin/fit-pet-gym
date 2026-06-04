package com.gym.modules.coach.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 私教时间段添加DTO
 */
@Data
public class PersonalTrainingSlotAddDto {
    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;
}
