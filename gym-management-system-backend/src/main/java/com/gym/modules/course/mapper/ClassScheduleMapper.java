// gym-management-system-backend/src/main/java/com/gym/modules/course/mapper/ClassScheduleMapper.java
package com.gym.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.modules.course.domain.entity.ClassSchedule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClassScheduleMapper extends BaseMapper<ClassSchedule> {
}

