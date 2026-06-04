// gym-management-system-backend/src/main/java/com/gym/modules/course/mapper/CourseEnrollmentMapper.java
package com.gym.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.modules.course.domain.dto.EnrollmentQueryDto;
import com.gym.modules.course.domain.entity.CourseEnrollment;
import com.gym.modules.course.domain.vo.EnrollmentDetailVo;
import com.gym.modules.course.domain.vo.EnrollmentStatsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseEnrollmentMapper extends BaseMapper<CourseEnrollment> {
    
    /**
     * 查询会员的预约列表
     */
    Page<EnrollmentDetailVo> selectMemberEnrollments(Page<EnrollmentDetailVo> page, 
                                                      @Param("memberId") Long memberId,
                                                      @Param("status") String status);
    
    /**
     * 管理员查询所有预约记录
     */
    Page<EnrollmentDetailVo> selectEnrollmentList(Page<EnrollmentDetailVo> page, 
                                                   @Param("query") EnrollmentQueryDto query);
    
    /**
     * 获取预约统计数据
     */
    EnrollmentStatsVo selectEnrollmentStats();
}

