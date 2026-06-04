package com.gym.modules.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gym.common.core.domain.model.LoginUser;
import com.gym.common.exception.ServiceException;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.health.domain.dto.DietRecordDTO;
import com.gym.modules.health.domain.entity.*;
import com.gym.modules.health.domain.vo.DietSummaryVO;
import com.gym.modules.health.mapper.*;
import com.gym.modules.health.service.IHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 健康模块服务实现类
 * 优化点：解决了饮食查询的 N+1 问题，增加了智能数据补全和严谨的权限校验
 */
@Service
public class HealthServiceImpl implements IHealthService {

    @Autowired
    private HealthDataMapper healthDataMapper;
    @Autowired
    private BodyImageMapper bodyImageMapper;
    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    private DietLogMapper dietLogMapper;

    // --- 辅助方法：获取当前登录用户ID ---
    private Long getCurrentUserId() {
        try {
            // 尝试从 SecurityContext 获取用户信息
            Object principal = SecurityUtils.getAuthentication().getPrincipal();
            if (principal instanceof LoginUser) {
                return ((LoginUser) principal).getUserId();
            }
            // 兜底方案：如果 Principal 是 username (视配置而定)，需要额外处理，这里假设是 ID 或标准 LoginUser
            return Long.valueOf(SecurityUtils.getAuthentication().getName());
        } catch (Exception e) {
            throw new ServiceException("无法获取当前用户信息，请重新登录", 401);
        }
    }

    // ================= 健康数据 (Health Data) =================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveHealthData(HealthData healthData) {
        Long userId = getCurrentUserId();
        healthData.setUserId(userId);

        // 1. 智能补全：如果用户此次没填身高、生日、性别，尝试去上一条记录找
        //    这对于计算 BMI 和 BMR 至关重要
        HealthData lastData = getLatestHealthData(userId);
        if (lastData != null) {
            if (healthData.getHeight() == null) healthData.setHeight(lastData.getHeight());
            if (healthData.getBirthDate() == null) healthData.setBirthDate(lastData.getBirthDate());
            if (healthData.getGender() == null) healthData.setGender(lastData.getGender());
        }

        // 2. 自动计算 BMI: weight(kg) / (height(m))^2
        if (healthData.getWeight() != null && healthData.getHeight() != null && healthData.getHeight().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightM = healthData.getHeight().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = healthData.getWeight().divide(heightM.multiply(heightM), 1, RoundingMode.HALF_UP);
            healthData.setBmi(bmi);
        }

        if (healthData.getMeasureTime() == null) {
            healthData.setMeasureTime(new Date());
        }

        // 3. 始终插入新记录，保留历史趋势
        healthData.setId(null);
        healthDataMapper.insert(healthData);
    }

    @Override
    public HealthData getLatestHealthData(Long userId) {
        // 如果外部调用未传 userId，则使用当前用户
        if (userId == null) {
            userId = getCurrentUserId();
        }
        return healthDataMapper.selectOne(new LambdaQueryWrapper<HealthData>()
                .eq(HealthData::getUserId, userId)
                .orderByDesc(HealthData::getMeasureTime)
                .last("LIMIT 1"));
    }

    @Override
    public List<HealthData> getHealthDataHistory() {
        Long userId = getCurrentUserId();
        // 获取最近 30 条记录，按时间正序排列以便前端画图 (或倒序取出后前端反转)
        // 这里按时间倒序取最近30条
        return healthDataMapper.selectList(new LambdaQueryWrapper<HealthData>()
                .eq(HealthData::getUserId, userId)
                .orderByDesc(HealthData::getMeasureTime)
                .last("LIMIT 30"));
    }

    // ================= 身材照片 (Body Image) =================

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/files/";

    @Override
    public String uploadBodyImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }

        // 1. 检查并创建目录
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // 如果目录不存在，自动创建
        }

        // 2. 生成唯一文件名 (防止重名覆盖)
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = file.getOriginalFilename();
        // 获取后缀名，如果没有则默认为 .jpg (防止无后缀文件)
        String suffix = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";

        String fileName = uuid + "_" + originalFilename;

        // 3. 构建目标文件对象
        File dest = new File(dir, fileName);

        try {
            // 4. 将上传的文件写入到目标路径
            file.transferTo(dest);
        } catch (IOException e) {
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }

        // 5. 返回可访问的 URL
        // 注意：端口号 8080 应与您 application.yml 中的 server.port 一致
        return "http://localhost:8080/files/" + fileName;
    }
    @Override
    public void saveBodyImageRecord(String imageUrl, Date recordTime) {
        Long userId = getCurrentUserId();
        BodyImage bodyImage = new BodyImage();
        bodyImage.setUserId(userId);
        bodyImage.setImageUrl(imageUrl);
        bodyImage.setRecordTime(recordTime == null ? new Date() : recordTime);
        bodyImageMapper.insert(bodyImage);
    }

    @Override
    public List<BodyImage> getBodyImageHistory() {
        Long userId = getCurrentUserId();
        return bodyImageMapper.selectList(new LambdaQueryWrapper<BodyImage>()
                .eq(BodyImage::getUserId, userId)
                .orderByDesc(BodyImage::getRecordTime));
    }

    @Override
    public void deleteBodyImage(Long id) {
        Long userId = getCurrentUserId();
        BodyImage image = bodyImageMapper.selectById(id);
        if (image == null) {
            throw new ServiceException("记录不存在");
        }
        // 权限校验：只能删除自己的照片
        if (!image.getUserId().equals(userId)) {
            throw new ServiceException("无权删除此记录");
        }
        // MyBatisPlus 会根据实体上的 @TableLogic 注解自动处理为逻辑删除
        bodyImageMapper.deleteById(id);
    }

    // ================= 饮食记录 (Diet Log) =================

    @Override
    public List<Food> getFoodList(String keyword) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Food::getName, keyword);
        }
        wrapper.orderByAsc(Food::getName).last("LIMIT 50");
        return foodMapper.selectList(wrapper);
    }

    @Override
    public Food addFood(Food food) {
        if (food.getName() == null || food.getName().trim().isEmpty()) {
            throw new ServiceException("食物名称不能为空");
        }
        food.setName(food.getName().trim());
        food.setCalories(defaultValue(food.getCalories()));
        food.setProtein(defaultValue(food.getProtein()));
        food.setFat(defaultValue(food.getFat()));
        food.setCarbohydrate(defaultValue(food.getCarbohydrate()));
        // 简单查重，如果已存在则不插入
        Long count = foodMapper.selectCount(new LambdaQueryWrapper<Food>().eq(Food::getName, food.getName()));
        if (count > 0) {
            return foodMapper.selectOne(new LambdaQueryWrapper<Food>()
                    .eq(Food::getName, food.getName())
                    .last("limit 1"));
        }
        foodMapper.insert(food);
        return food;
    }

    @Override
    public void recordDiet(DietRecordDTO dto) {
        Long userId = getCurrentUserId();
        // 校验食物是否存在
        Food food = foodMapper.selectById(dto.getFoodId());
        if (food == null) {
            throw new ServiceException("选择的食物不存在");
        }

        DietLog log = new DietLog();
        log.setUserId(userId);
        log.setFoodId(dto.getFoodId());
        log.setAmount(dto.getAmount());
        log.setMealType(dto.getMealType());
        // 如果没传日期，默认为今天
        log.setEatDate(dto.getEatDate() != null ? dto.getEatDate() : new Date());

        dietLogMapper.insert(log);
    }

    @Override
    public void deleteDietLog(Long id) {
        Long userId = getCurrentUserId();
        DietLog log = dietLogMapper.selectById(id);
        if (log == null) {
            throw new ServiceException("记录不存在");
        }
        if (!log.getUserId().equals(userId)) {
            throw new ServiceException("无权删除此记录");
        }
        // 数据库中 gym_diet_log 没有 deleted 字段，这里执行物理删除
        dietLogMapper.deleteById(id);
    }

    @Override
    public DietSummaryVO getDailyDietAnalysis(Date date) {
        Long userId = getCurrentUserId();

        // 1. 格式化日期，确保只匹配年月日 (数据库类型为 DATE)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);

        // 2. 查询当日所有饮食记录
        // 使用 apply 确保忽略时分秒差异，精确匹配日期
        List<DietLog> logs = dietLogMapper.selectList(new LambdaQueryWrapper<DietLog>()
                .eq(DietLog::getUserId, userId)
                .apply("DATE_FORMAT(eat_date, '%Y-%m-%d') = {0}", dateStr));

        DietSummaryVO vo = new DietSummaryVO();
        vo.setDate(dateStr);

        if (logs.isEmpty()) {
            // 如果没有记录，返回空数据，防止前端报错
            initEmptySummary(vo);
            // 依然计算推荐值
            calculateRecommendations(vo, getLatestHealthData(userId));
            return vo;
        }

        // 3. 批量查询食物信息 (解决 N+1 问题)
        Set<Long> foodIds = logs.stream().map(DietLog::getFoodId).collect(Collectors.toSet());
        List<Food> foods = foodMapper.selectBatchIds(foodIds);
        Map<Long, Food> foodMap = foods.stream().collect(Collectors.toMap(Food::getId, Function.identity()));

        // 4. 统计营养素
        BigDecimal totalCal = BigDecimal.ZERO;
        BigDecimal totalPro = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalCarb = BigDecimal.ZERO;
        List<DietSummaryVO.DietDetailVO> details = new ArrayList<>();

        for (DietLog log : logs) {
            Food food = foodMap.get(log.getFoodId());
            if (food != null) {
                // 计算比例: 摄入量 / 100
                BigDecimal ratio = log.getAmount().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

                BigDecimal itemCal = defaultValue(food.getCalories()).multiply(ratio);
                BigDecimal itemPro = defaultValue(food.getProtein()).multiply(ratio);
                BigDecimal itemFat = defaultValue(food.getFat()).multiply(ratio);
                BigDecimal itemCarb = defaultValue(food.getCarbohydrate()).multiply(ratio);

                totalCal = totalCal.add(itemCal);
                totalPro = totalPro.add(itemPro);
                totalFat = totalFat.add(itemFat);
                totalCarb = totalCarb.add(itemCarb);

                DietSummaryVO.DietDetailVO detail = new DietSummaryVO.DietDetailVO();
                detail.setId(log.getId()); // 用于前端删除
                detail.setFoodName(food.getName());
                detail.setAmount(log.getAmount());
                detail.setCalories(itemCal.setScale(1, RoundingMode.HALF_UP));
                detail.setProtein(itemPro.setScale(1, RoundingMode.HALF_UP));
                detail.setFat(itemFat.setScale(1, RoundingMode.HALF_UP));
                detail.setCarbohydrate(itemCarb.setScale(1, RoundingMode.HALF_UP));
                detail.setMealType(log.getMealType());
                details.add(detail);
            }
        }

        vo.setTotalCalories(totalCal.setScale(1, RoundingMode.HALF_UP));
        vo.setTotalProtein(totalPro.setScale(1, RoundingMode.HALF_UP));
        vo.setTotalFat(totalFat.setScale(1, RoundingMode.HALF_UP));
        vo.setTotalCarb(totalCarb.setScale(1, RoundingMode.HALF_UP));
        vo.setDetails(details);

        // 5. 计算推荐值
        calculateRecommendations(vo, getLatestHealthData(userId));

        return vo;
    }

    private void initEmptySummary(DietSummaryVO vo) {
        vo.setTotalCalories(BigDecimal.ZERO);
        vo.setTotalProtein(BigDecimal.ZERO);
        vo.setTotalFat(BigDecimal.ZERO);
        vo.setTotalCarb(BigDecimal.ZERO);
        vo.setDetails(new ArrayList<>());
    }

    private BigDecimal defaultValue(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 根据最新的健康数据计算推荐摄入量 (BMR & TDEE)
     * 修改：默认使用 25 岁进行计算，只需要体重和身高即可
     */
    private void calculateRecommendations(DietSummaryVO vo, HealthData health) {
        List<String> suggestions = new ArrayList<>();
        BigDecimal bmr;

        // Mifflin-St Jeor 公式
        // 只要有体重和身高，就可以计算（默认 25 岁）
        if (health != null && health.getWeight() != null && health.getHeight() != null) {

            // 直接按照默认的 25 岁来计算
            int age = 25;

            BigDecimal weightPart = health.getWeight().multiply(new BigDecimal("10"));
            BigDecimal heightPart = health.getHeight().multiply(new BigDecimal("6.25"));
            BigDecimal agePart = new BigDecimal(age).multiply(new BigDecimal("5"));

            bmr = weightPart.add(heightPart).subtract(agePart);

            // 依然保留性别区分：男性 +5，女性/未知 -161
            if (health.getGender() != null && health.getGender() == 1) {
                bmr = bmr.add(new BigDecimal("5"));
            } else {
                bmr = bmr.subtract(new BigDecimal("161"));
            }
        } else {
            // 缺省值
            bmr = new BigDecimal("1500");
            suggestions.add("⚠️ 缺少身体数据，建议尽快完善个人信息以获取精准推荐。");
        }

        // TDEE (总能量消耗) = BMR * 活动系数 (默认轻度活动 1.375)
        BigDecimal tdee = bmr.multiply(new BigDecimal("1.375"));
        vo.setRecommendCalories(tdee.setScale(0, RoundingMode.HALF_UP));

        // 宏量营养素推荐比例: 碳水 50%, 蛋白质 30%, 脂肪 20%
        vo.setRecommendCarb(tdee.multiply(new BigDecimal("0.5")).divide(new BigDecimal("4"), 0, RoundingMode.HALF_UP));
        vo.setRecommendProtein(tdee.multiply(new BigDecimal("0.3")).divide(new BigDecimal("4"), 0, RoundingMode.HALF_UP));
        vo.setRecommendFat(tdee.multiply(new BigDecimal("0.2")).divide(new BigDecimal("9"), 0, RoundingMode.HALF_UP));

        // 智能建议生成
        if (vo.getTotalCalories().compareTo(BigDecimal.ZERO) > 0) {
            if (vo.getTotalCalories().compareTo(tdee.multiply(new BigDecimal("1.1"))) > 0) {
                suggestions.add("🔴 今日热量摄入超标，建议晚餐保持清淡或增加30分钟有氧运动。");
            } else if (vo.getTotalCalories().compareTo(tdee.multiply(new BigDecimal("0.8"))) < 0) {
                suggestions.add("🟡 热量摄入偏低，长期可能导致基础代谢下降，请适当加餐。");
            } else {
                suggestions.add("🟢 热量控制非常棒，继续保持！");
            }
        }

        vo.setSuggestions(suggestions);
    }
}
