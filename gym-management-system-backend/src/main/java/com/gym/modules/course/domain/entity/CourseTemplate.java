package com.gym.modules.course.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 课程模板实体 - 用于周期性自动排课
 */
@Data
@TableName("gym_course_template")
public class CourseTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId; // 课程ID
    private Long coachId; // 固定授课教练ID

    /**
     * 星期几上课，使用逗号分隔
     * 1=周一, 2=周二, 3=周三, 4=周四, 5=周五, 6=周六, 7=周日
     * 例如: "1,3,5" 表示每周一、三、五上课
     */
    private String weekDays;

    private String startTime; // 开始时间 HH:mm:ss
    private Integer capacity; // 课程容量

    /**
     * 状态: 0-启用, 1-停用
     */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer deleted;
}
