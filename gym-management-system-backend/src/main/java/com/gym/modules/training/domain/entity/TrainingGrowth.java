package com.gym.modules.training.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gym_member_growth")
public class TrainingGrowth {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private Integer totalXp;
    private Integer level;
    private Integer currentLevelXp;
    private Integer nextLevelXp;
    private Integer progressPercent;
    private Integer streakDays;
    private Integer totalSessions;
    private Integer weeklyMinutes;
    private Integer weeklySessions;
    private Date lastTrainingDate;
    private String petMood;
    private String badgeTitle;
    private Integer lastRewardXp;
    private String lastRewardReason;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
