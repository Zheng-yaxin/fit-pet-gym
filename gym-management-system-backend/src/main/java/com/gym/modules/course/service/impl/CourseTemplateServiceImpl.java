package com.gym.modules.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.modules.course.domain.entity.ClassSchedule;
import com.gym.modules.course.domain.entity.Course;
import com.gym.modules.course.domain.entity.CourseTemplate;
import com.gym.modules.course.mapper.CourseTemplateMapper;
import com.gym.modules.course.service.IClassScheduleService;
import com.gym.modules.course.service.ICourseService;
import com.gym.modules.course.service.ICourseTemplateService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class CourseTemplateServiceImpl extends ServiceImpl<CourseTemplateMapper, CourseTemplate> implements ICourseTemplateService {

    @Resource
    private IClassScheduleService classScheduleService;

    @Resource
    private ICourseService courseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateSchedulesFromTemplates(int weeks) {
        // 获取所有启用的模板
        LambdaQueryWrapper<CourseTemplate> query = new LambdaQueryWrapper<>();
        query.eq(CourseTemplate::getStatus, "0");
        List<CourseTemplate> templates = this.list(query);

        if (templates.isEmpty()) {
            log.info("没有启用的课程模板，跳过自动排课");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusWeeks(weeks);

        List<ClassSchedule> schedulesToCreate = new ArrayList<>();

        for (CourseTemplate template : templates) {
            // 检查课程是否存在且正常
            Course course = courseService.getById(template.getCourseId());
            if (course == null || !"0".equals(course.getStatus())) {
                log.warn("课程ID {} 不存在或已停用，跳过", template.getCourseId());
                continue;
            }

            // 解析星期几上课
            String[] weekDaysArray = template.getWeekDays().split(",");
            List<Integer> weekDays = new ArrayList<>();
            for (String day : weekDaysArray) {
                try {
                    weekDays.add(Integer.parseInt(day.trim()));
                } catch (NumberFormatException e) {
                    log.error("解析星期配置失败: {}", day, e);
                }
            }

            // 解析开始时间
            LocalTime startTime;
            try {
                startTime = LocalTime.parse(template.getStartTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            } catch (Exception e) {
                log.error("解析时间失败: {}", template.getStartTime(), e);
                continue;
            }

            // 生成未来N周的排课
            LocalDate currentDate = today;
            while (!currentDate.isAfter(endDate)) {
                int dayOfWeek = currentDate.getDayOfWeek().getValue(); // 1=周一, 7=周日

                if (weekDays.contains(dayOfWeek)) {
                    LocalDateTime classDateTime = LocalDateTime.of(currentDate, startTime);

                    // 检查是否已存在该时间的排课（避免重复）
                    Date classTime = Date.from(classDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    LambdaQueryWrapper<ClassSchedule> existQuery = new LambdaQueryWrapper<>();
                    existQuery.eq(ClassSchedule::getCourseId, template.getCourseId())
                            .eq(ClassSchedule::getCoachId, template.getCoachId())
                            .eq(ClassSchedule::getClassTime, classTime);

                    if (classScheduleService.count(existQuery) == 0) {
                        ClassSchedule schedule = new ClassSchedule();
                        schedule.setCourseId(template.getCourseId());
                        schedule.setCoachId(template.getCoachId());
                        schedule.setClassTime(classTime);
                        schedule.setCapacity(template.getCapacity());
                        schedule.setEnrolledCount(0);
                        schedulesToCreate.add(schedule);
                    }
                }

                currentDate = currentDate.plusDays(1);
            }
        }

        if (!schedulesToCreate.isEmpty()) {
            classScheduleService.saveBatch(schedulesToCreate);
            log.info("自动生成了 {} 条排课记录", schedulesToCreate.size());
        } else {
            log.info("没有需要生成的新排课");
        }
    }
}
