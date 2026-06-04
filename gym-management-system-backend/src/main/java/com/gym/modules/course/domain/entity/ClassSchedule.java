// gym-management-system-backend/src/main/java/com/gym/modules/course/domain/entity/ClassSchedule.java
package com.gym.modules.course.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gym_class_schedule")
public class ClassSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;
    private Long coachId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date classTime; // 上课时间（用于兼容旧数据，新排课不使用）

    private Integer dayOfWeek; // 星期几（1-7，1表示周一，7表示周日）
    private String startTime; // 开始时间（格式：HH:mm，如 09:00）
    private String endTime; // 结束时间（格式：HH:mm，如 10:30）

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDate; // 生效日期（从哪天开始生效）

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate; // 失效日期（到哪天结束，null表示长期有效）

    private Integer capacity; // 最大容量
    private Integer enrolledCount; // 已报名人数

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer deleted;
}
