// gym-management-system-backend/src/main/java/com/gym/modules/course/service/impl/CourseServiceImpl.java
package com.gym.modules.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.modules.course.domain.entity.Course;
import com.gym.modules.course.mapper.CourseMapper;
import com.gym.modules.course.service.ICourseService;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {
}

