package com.gym.modules.exercise.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.modules.exercise.domain.entity.Exercise;
import com.gym.modules.exercise.mapper.ExerciseMapper;
import com.gym.modules.exercise.service.IExerciseService;
import org.springframework.stereotype.Service;

@Service
public class ExerciseServiceImpl extends ServiceImpl<ExerciseMapper, Exercise> implements IExerciseService {
}
