package com.gym.modules.course.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ScheduleDetailVo {

    private Long id;

    private Long courseId;

    private String courseName;

    private String courseDescription;

    private Integer duration;

    private BigDecimal price;

    private String courseImage;

    private Long coachId;

    private String coachName;

    private String coachAvatar;

    private String coachSpecialty;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date classTime;

    // ================= 新增字段 =================
    /**
     * 星期几 (1-7)
     */
    private Integer dayOfWeek;

    /**
     * 星期几中文名称 (如：周一)
     */
    private String dayOfWeekName;

    /**
     * 开始时间 (HH:mm)
     */
    private String startTime;

    /**
     * 结束时间 (HH:mm)
     */
    private String endTime;

    /**
     * 生效日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDate;

    /**
     * 失效日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;
    // ===========================================

    private Integer capacity;

    private Integer enrolledCount;

    private Integer remainingSlots;

    private Boolean enrolled;

    private Long enrollmentId;
}