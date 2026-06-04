package com.gym.modules.course.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 预约查询DTO
 */
@Data
public class EnrollmentQueryDto {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    
    /**
     * 会员ID
     */
    private Long memberId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 教练ID
     */
    private Long coachId;
    
    /**
     * 预约状态 0-已报名, 1-已取消, 2-已完成
     */
    private String status;
    
    /**
     * 开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    
    /**
     * 结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
