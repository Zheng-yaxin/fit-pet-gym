package com.gym.modules.health.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gym.common.json.FlexibleDateDeserializer;
import lombok.Data;
import java.util.Date;

/**
 * 身材照片实体
 */
@Data
@TableName("gym_body_image")
public class BodyImage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 记录/拍照时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = FlexibleDateDeserializer.class)
    private Date recordTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableLogic
    private Integer deleted;
}
