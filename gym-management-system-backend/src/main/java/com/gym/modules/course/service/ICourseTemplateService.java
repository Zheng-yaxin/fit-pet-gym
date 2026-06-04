package com.gym.modules.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.course.domain.entity.CourseTemplate;

public interface ICourseTemplateService extends IService<CourseTemplate> {
    /**
     * 根据所有启用的模板生成未来N周的排课
     * @param weeks 生成未来几周的排课
     */
    void generateSchedulesFromTemplates(int weeks);
}
