package com.gym.modules.coach.domain.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 教练信息VO
 */
@Data
public class CoachInfoVo {
    private Long id;
    private String name;
    private String phone;
    private Integer gender;
    private String avatar;
    private String bio;
    private String specialties;
    private Integer experienceYears;
    private String certification;
    private BigDecimal hourlyRate;
    private String status;
    private Date createTime;
}
