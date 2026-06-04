// gym-management-system-backend/src/main/java/com/gym/modules/course/mapper/CourseMapper.java
package com.gym.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.course.domain.entity.Course;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
