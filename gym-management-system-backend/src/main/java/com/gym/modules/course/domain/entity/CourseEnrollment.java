// gym-management-system-backend/src/main/java/com/gym/modules/course/domain/entity/CourseEnrollment.java
package com.gym.modules.course.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gym_course_enrollment")
public class CourseEnrollment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;
    private Long scheduleId;
    private String status; // 0-已报名, 1-已取消, 2-已完成

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer deleted;
}
