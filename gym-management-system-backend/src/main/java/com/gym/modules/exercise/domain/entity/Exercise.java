package com.gym.modules.exercise.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gym_exercise")
public class Exercise {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String targetMuscle;
    private String equipment;
    private String difficulty;
    private String imageUrl;
    private String videoUrl;
    private String primaryMuscle;
    private String secondaryMuscles;
    private String movementPattern;
    private String steps;
    private String commonMistakes;
    private String alternatives;
    private String mediaLicense;
    private String tips;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer deleted;
}
