package com.gym.modules.course.domain.vo;

import lombok.Data;

/**
 * 预约统计VO
 */
@Data
public class EnrollmentStatsVo {
    /**
     * 总预约数
     */
    private Long totalEnrollments;
    
    /**
     * 已完成数
     */
    private Long completedCount;
    
    /**
     * 已取消数
     */
    private Long cancelledCount;
    
    /**
     * 待上课数
     */
    private Long pendingCount;
    
    /**
     * 今日预约数
     */
    private Long todayEnrollments;
    
    /**
     * 本周预约数
     */
    private Long weekEnrollments;
}
