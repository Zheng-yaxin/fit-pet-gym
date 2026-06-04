// gym-management-system-backend/src/main/java/com/gym/modules/course/domain/entity/Course.java
package com.gym.modules.course.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("gym_course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String description;
    private Integer duration; // 课程时长(分钟)
    private BigDecimal price;
    private String imageUrl;
    private Integer maxParticipants;
    private String status; // 0-正常, 1-停用

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer deleted;
}
