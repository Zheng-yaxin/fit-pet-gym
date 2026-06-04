package com.gym.modules.health.service;

import com.gym.modules.health.domain.dto.DietRecordDTO;
import com.gym.modules.health.domain.entity.BodyImage;
import com.gym.modules.health.domain.entity.Food;
import com.gym.modules.health.domain.entity.HealthData;
import com.gym.modules.health.domain.vo.BodyInsightVO;
import com.gym.modules.health.domain.vo.DietActionPlanVO;
import com.gym.modules.health.domain.vo.DietSummaryVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface IHealthService {
    // --- 健康数据 ---
    /** 保存健康数据 */
    void saveHealthData(HealthData healthData);

    /** 获取最新的一条健康数据 */
    HealthData getLatestHealthData(Long userId);

    /** 获取健康数据历史列表 (用于绘制图表) */
    List<HealthData> getHealthDataHistory();

    // --- 身材照片 ---
    String uploadBodyImage(MultipartFile file);
    void saveBodyImageRecord(String imageUrl, Date recordTime);
    List<BodyImage> getBodyImageHistory();
    /** 删除身材照 */
    void deleteBodyImage(Long id);

    // --- 饮食部分 ---
    List<Food> getFoodList(String keyword);
    /** 添加自定义食物 */
    Food addFood(Food food);

    void recordDiet(DietRecordDTO dietRecordDTO);
    /** 删除饮食记录 */
    void deleteDietLog(Long id);

    DietSummaryVO getDailyDietAnalysis(Date date);

    DietActionPlanVO getDietActionPlan(Date date);

    BodyInsightVO getBodyInsight();
}
