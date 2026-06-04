package com.gym.modules.coach.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 私教预约DTO
 */
@Data
public class PersonalTrainingBookingDto {
    @NotNull(message = "教练ID不能为空")
    private Long coachId;

    // 允许为空，因为现在的逻辑是基于时间段预约
    private Long slotId;

    @NotNull(message = "预约日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private String remark;
}