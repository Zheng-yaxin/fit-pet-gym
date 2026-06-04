package com.gym.modules.coach.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 教练信息更新DTO
 */
@Data
public class CoachUpdateDto {
    private String name;
    private String avatar;
    private String bio;
    private String specialties;
    private Integer experienceYears;
    private String certification;
    private BigDecimal hourlyRate;
    private Integer gender;
}
