package com.gym.modules.course.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 课程模板VO - 用于前端展示
 */
@Data
public class CourseTemplateVo {
    private Long id;
    private Long courseId;
    private String courseName; // 课程名称
    private Long coachId;
    private String coachName; // 教练名称
    private String weekDays; // 星期几上课
    private String weekDaysText; // 星期几上课（中文显示）
    private String startTime; // 开始时间
    private Integer capacity; // 课程容量
    private String status; // 状态

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
