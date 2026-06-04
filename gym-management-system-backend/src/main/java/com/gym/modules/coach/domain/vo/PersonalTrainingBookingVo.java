package com.gym.modules.coach.domain.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * 私教预约VO
 */
@Data
public class PersonalTrainingBookingVo {
    private Long id;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private Long coachId;
    private String coachName;
    private Long slotId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal amount;
    private Integer status;
    private String statusText;
    private String remark;
    private Date createTime;
}
