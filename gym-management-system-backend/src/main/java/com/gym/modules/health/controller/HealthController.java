package com.gym.modules.health.controller;

import com.gym.common.result.R;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.health.domain.dto.DietRecordDTO;
import com.gym.modules.health.domain.entity.BodyImage;
import com.gym.modules.health.domain.entity.DietTarget;
import com.gym.modules.health.domain.entity.Food;
import com.gym.modules.health.domain.entity.HealthData;
import com.gym.modules.health.domain.vo.DietGapVO;
import com.gym.modules.health.domain.vo.DietSummaryVO;
import com.gym.modules.health.domain.vo.FoodAnalysisVO;
import com.gym.modules.health.mapper.DietTargetMapper;
import com.gym.modules.health.service.IHealthService;
import com.gym.modules.health.service.impl.AiFoodServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

@Tag(name = "健康管理")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private IHealthService healthService;

    @Autowired
    private DietTargetMapper dietTargetMapper;

    // --- 身体数据相关 ---

    @Operation(summary = "上传/记录身体数据")
    @PostMapping("/data")
    public R<Void> saveHealthData(@RequestBody HealthData healthData) {
        healthService.saveHealthData(healthData);
        return R.ok();
    }

    @Operation(summary = "获取最新身体数据")
    @GetMapping("/data/latest")
    public R<HealthData> getLatestHealthData() {
        return R.ok(healthService.getLatestHealthData(SecurityUtils.getUserId()));
    }

    @Operation(summary = "获取身体数据历史(图表用)")
    @GetMapping("/data/history")
    public R<List<HealthData>> getHealthDataHistory() {
        return R.ok(healthService.getHealthDataHistory());
    }

    // --- 身材照片相关 ---

    @Operation(summary = "上传图片文件")
    @PostMapping("/image/upload")
    public R<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = healthService.uploadBodyImage(file);
        return R.ok(url, "上传成功");
    }

    @Operation(summary = "保存身材照记录")
    @PostMapping("/image/record")
    public R<Void> saveImageRecord(@RequestBody BodyImage bodyImage) {
        healthService.saveBodyImageRecord(bodyImage.getImageUrl(), bodyImage.getRecordTime());
        return R.ok();
    }

    @Operation(summary = "获取身材照历史")
    @GetMapping("/image/history")
    public R<List<BodyImage>> getImageHistory() {
        return R.ok(healthService.getBodyImageHistory());
    }

    @Operation(summary = "删除身材照记录")
    @DeleteMapping("/image/{id}")
    public R<Void> deleteBodyImage(@PathVariable("id") Long id) {
        healthService.deleteBodyImage(id);
        return R.ok();
    }

    // --- 饮食相关 ---
    @Autowired
    private AiFoodServiceImpl aiFoodService; // 注入新写的服务

    @Operation(summary = "AI 识别食物")
    @PostMapping(value = "/diet/analyze", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<FoodAnalysisVO> analyzeFood(@RequestPart("file") MultipartFile file) {
        return R.ok(aiFoodService.analyzeFood(file));
    }
    @Operation(summary = "搜索食物列表")
    @GetMapping("/food/list")
    public R<List<Food>> getFoodList(@RequestParam(name = "keyword", required = false) String keyword) {
        return R.ok(healthService.getFoodList(keyword));
    }

    @Operation(summary = "添加自定义食物")
    @PostMapping("/food")
    public R<Food> addFood(@RequestBody Food food) {
        return R.ok(healthService.addFood(food));
    }

    @Operation(summary = "记录饮食")
    @PostMapping("/diet")
    public R<Void> recordDiet(@RequestBody DietRecordDTO dietRecordDTO) {
        healthService.recordDiet(dietRecordDTO);
        return R.ok();
    }

    @Operation(summary = "删除饮食记录")
    @DeleteMapping("/diet/{id}")
    public R<Void> deleteDietLog(@PathVariable("id") Long id) {
        healthService.deleteDietLog(id);
        return R.ok();
    }

    @Operation(summary = "获取每日饮食分析与建议")
    @GetMapping("/diet/summary")
    public R<DietSummaryVO> getDietSummary(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return R.ok(healthService.getDailyDietAnalysis(date));
    }

    @Operation(summary = "获取饮食目标")
    @GetMapping("/diet/target")
    public R<DietTarget> getDietTarget() {
        Long userId = SecurityUtils.getUserId();
        DietTarget target = dietTargetMapper.selectOne(new LambdaQueryWrapper<DietTarget>()
                .eq(DietTarget::getUserId, userId)
                .last("limit 1"));
        return R.ok(target);
    }

    @Operation(summary = "设置饮食目标")
    @PutMapping("/diet/target")
    public R<Void> saveDietTarget(@RequestBody DietTarget target) {
        Long userId = SecurityUtils.getUserId();
        target.setUserId(userId);
        DietTarget exists = dietTargetMapper.selectOne(new LambdaQueryWrapper<DietTarget>()
                .eq(DietTarget::getUserId, userId)
                .last("limit 1"));
        if (exists == null) {
            dietTargetMapper.insert(target);
        } else {
            target.setId(exists.getId());
            dietTargetMapper.updateById(target);
        }
        return R.ok();
    }

    @Operation(summary = "获取饮食缺口")
    @GetMapping("/diet/gap")
    public R<DietGapVO> getDietGap(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        Long userId = SecurityUtils.getUserId();
        DietTarget target = dietTargetMapper.selectOne(new LambdaQueryWrapper<DietTarget>()
                .eq(DietTarget::getUserId, userId)
                .last("limit 1"));
        DietSummaryVO summary = healthService.getDailyDietAnalysis(date);

        DietGapVO gap = new DietGapVO();
        gap.setCaloriesActual(value(summary.getTotalCalories()));
        gap.setProteinActual(value(summary.getTotalProtein()));
        gap.setFatActual(value(summary.getTotalFat()));
        gap.setCarbohydrateActual(value(summary.getTotalCarb()));

        gap.setCaloriesTarget(target != null && target.getCaloriesTarget() != null ? target.getCaloriesTarget() : value(summary.getRecommendCalories()));
        gap.setProteinTarget(target != null && target.getProteinTarget() != null ? target.getProteinTarget() : value(summary.getRecommendProtein()));
        gap.setFatTarget(target != null && target.getFatTarget() != null ? target.getFatTarget() : value(summary.getRecommendFat()));
        gap.setCarbohydrateTarget(target != null && target.getCarbohydrateTarget() != null ? target.getCarbohydrateTarget() : value(summary.getRecommendCarb()));

        gap.setCaloriesGap(gap.getCaloriesTarget().subtract(gap.getCaloriesActual()));
        gap.setProteinGap(gap.getProteinTarget().subtract(gap.getProteinActual()));
        gap.setFatGap(gap.getFatTarget().subtract(gap.getFatActual()));
        gap.setCarbohydrateGap(gap.getCarbohydrateTarget().subtract(gap.getCarbohydrateActual()));
        return R.ok(gap);
    }

    private BigDecimal value(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 获取当前登录用户ID
     */
}
