package com.gym.modules.course.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 预约详情VO
 */
@Data
public class EnrollmentDetailVo {
    /**
     * 预约ID
     */
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 会员姓名
     */
    private String memberName;

    /**
     * 会员手机号
     */
    private String memberPhone;

    /**
     * 课表ID
     */
    private Long scheduleId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程时长(分钟)
     */
    private Integer duration;

    /**
     * 课程价格
     */
    private BigDecimal price;

    /**
     * 教练ID
     */
    private Long coachId;

    /**
     * 教练名称
     */
    private String coachName;

    /**
     * 上课时间 (基准时间)
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date classTime;

    /**
     * 星期几 (1-7)
     */
    private Integer dayOfWeek;

    /**
     * 开始时间 (HH:mm)
     */
    private String startTime;

    /**
     * 结束时间 (HH:mm)
     */
    private String endTime;

    /**
     * 预约状态 0-已报名, 1-已取消, 2-已完成
     */
    private String status;

    /**
     * 预约时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}