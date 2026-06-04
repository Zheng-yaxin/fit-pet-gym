package com.gym.modules.exercise.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("gym_exercise_alternative")
public class ExerciseAlternative {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long exerciseId;
    private Long alternativeExerciseId;
    private String reason;
}
