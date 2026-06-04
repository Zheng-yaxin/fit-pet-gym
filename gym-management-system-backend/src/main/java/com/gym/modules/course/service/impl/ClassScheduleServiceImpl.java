// gym-management-system-backend/src/main/java/com/gym/modules/course/service/impl/ClassScheduleServiceImpl.java
package com.gym.modules.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.modules.course.domain.entity.ClassSchedule;
import com.gym.modules.course.mapper.ClassScheduleMapper;
import com.gym.modules.course.service.IClassScheduleService;
import org.springframework.stereotype.Service;

@Service
public class ClassScheduleServiceImpl extends ServiceImpl<ClassScheduleMapper, ClassSchedule> implements IClassScheduleService {
}

