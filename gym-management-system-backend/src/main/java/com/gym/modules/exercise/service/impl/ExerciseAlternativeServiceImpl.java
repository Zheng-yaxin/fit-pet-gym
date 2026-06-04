package com.gym.modules.exercise.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.modules.exercise.domain.entity.ExerciseAlternative;
import com.gym.modules.exercise.mapper.ExerciseAlternativeMapper;
import com.gym.modules.exercise.service.IExerciseAlternativeService;
import org.springframework.stereotype.Service;

@Service
public class ExerciseAlternativeServiceImpl extends ServiceImpl<ExerciseAlternativeMapper, ExerciseAlternative> implements IExerciseAlternativeService {
}
