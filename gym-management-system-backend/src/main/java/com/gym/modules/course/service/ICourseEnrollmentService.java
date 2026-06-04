// gym-management-system-backend/src/main/java/com/gym/modules/course/service/ICourseEnrollmentService.java
package com.gym.modules.course.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.course.domain.dto.EnrollmentQueryDto;
import com.gym.modules.course.domain.entity.CourseEnrollment;
import com.gym.modules.course.domain.vo.EnrollmentDetailVo;
import com.gym.modules.course.domain.vo.EnrollmentStatsVo;
import com.gym.modules.course.domain.vo.ScheduleDetailVo;

import java.util.List;

public interface ICourseEnrollmentService extends IService<CourseEnrollment> {
    
    /**
     * 会员预约课程
     */
    boolean enrollCourse(Long memberId, Long scheduleId);
    
    /**
     * 会员取消预约
     */
    boolean cancelEnrollment(Long memberId, Long enrollmentId);
    
    /**
     * 查询会员的预约列表
     */
    Page<EnrollmentDetailVo> getMemberEnrollments(Long memberId, EnrollmentQueryDto queryDto);
    
    /**
     * 查询课表详情（包含会员预约状态）
     */
    ScheduleDetailVo getScheduleDetail(Long scheduleId, Long memberId);
    
    /**
     * 查询可预约的课表列表（会员端）
     */
    List<ScheduleDetailVo> getAvailableSchedules(Long memberId, Long courseId, Long coachId);
    
    /**
     * 管理员查询所有预约记录
     */
    Page<EnrollmentDetailVo> getEnrollmentList(EnrollmentQueryDto queryDto);
    
    /**
     * 管理员更新预约状态（完成/取消）
     */
    boolean updateEnrollmentStatus(Long enrollmentId, String status);
    
    /**
     * 获取预约统计数据
     */
    EnrollmentStatsVo getEnrollmentStats();
    
    /**
     * 检查会员是否已预约该课表
     */
    boolean isEnrolled(Long memberId, Long scheduleId);
}
