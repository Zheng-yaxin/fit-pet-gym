package com.gym.modules.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gym.common.core.domain.model.LoginUser;
import com.gym.common.exception.ServiceException;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.health.domain.dto.DietRecordDTO;
import com.gym.modules.health.domain.entity.*;
import com.gym.modules.health.domain.vo.BodyInsightVO;
import com.gym.modules.health.domain.vo.DietActionPlanVO;
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

    @Override
    public DietActionPlanVO getDietActionPlan(Date date) {
        DietSummaryVO summary = getDailyDietAnalysis(date);

        BigDecimal caloriesTarget = valueOr(summary.getRecommendCalories(), new BigDecimal("1800"));
        BigDecimal proteinTarget = valueOr(summary.getRecommendProtein(), new BigDecimal("90"));
        BigDecimal fatTarget = valueOr(summary.getRecommendFat(), new BigDecimal("55"));
        BigDecimal carbTarget = valueOr(summary.getRecommendCarb(), new BigDecimal("220"));

        BigDecimal caloriesGap = caloriesTarget.subtract(valueOr(summary.getTotalCalories(), BigDecimal.ZERO));
        BigDecimal proteinGap = proteinTarget.subtract(valueOr(summary.getTotalProtein(), BigDecimal.ZERO));
        BigDecimal fatGap = fatTarget.subtract(valueOr(summary.getTotalFat(), BigDecimal.ZERO));
        BigDecimal carbGap = carbTarget.subtract(valueOr(summary.getTotalCarb(), BigDecimal.ZERO));

        List<DietActionPlanVO.ActionItem> actions = new ArrayList<>();
        List<String> notes = new ArrayList<>();
        String status = "balanced";
        String headline = "今天摄入接近目标，晚餐保持清淡稳定。";
        String nextMealFocus = "优先选择高蛋白、低油烹饪和足量饮水。";

        if (valueOr(summary.getTotalCalories(), BigDecimal.ZERO).compareTo(BigDecimal.ZERO) == 0) {
            status = "empty";
            headline = "今天还没有饮食记录，先完成一餐录入。";
            nextMealFocus = "用 AI 识别或手动记录第一餐，系统会继续计算缺口。";
            actions.add(action("record", "high", "先记录最近一餐", "上传食物照片或从食物库选择，至少记录重量和餐别。"));
            notes.add("记录越完整，热量和三大营养素建议越稳定。");
        } else if (caloriesGap.compareTo(new BigDecimal("300")) > 0) {
            status = "need_intake";
            headline = "今天热量仍有明显缺口，下一餐需要补足能量。";
            nextMealFocus = "补一份主食，加一份优质蛋白，避免只靠零食补热量。";
            actions.add(action("meal", "high", "下一餐加主食", "可增加米饭、燕麦、红薯或全麦面包，目标补足约 300 kcal。"));
        } else if (caloriesGap.compareTo(new BigDecimal("-200")) < 0) {
            status = "over_calories";
            headline = "今天热量已经超出目标，后续餐次以轻负担为主。";
            nextMealFocus = "减少油脂和甜饮，晚餐选择蔬菜、蛋白和低油做法。";
            actions.add(action("control", "high", "控制后续热量", "避免奶茶、油炸和大份主食；如状态允许，可加 20-30 分钟低强度有氧。"));
        }

        if (proteinGap.compareTo(new BigDecimal("20")) > 0) {
            actions.add(action("protein", "high", "补足蛋白质", "下一餐加入鸡蛋、鸡胸、鱼虾、牛肉、豆腐或无糖酸奶。"));
        } else if (proteinGap.compareTo(BigDecimal.ZERO) > 0) {
            actions.add(action("protein", "medium", "蛋白质小幅补齐", "加一份蛋、奶或豆制品即可，不需要额外加油。"));
        }

        if (fatGap.compareTo(new BigDecimal("-10")) < 0) {
            actions.add(action("fat", "medium", "降低脂肪摄入", "下一餐少油烹饪，暂缓坚果、肥肉、奶油和油炸食物。"));
        }

        if (carbGap.compareTo(new BigDecimal("60")) > 0 && caloriesGap.compareTo(BigDecimal.ZERO) > 0) {
            actions.add(action("carb", "medium", "补一点训练燃料", "训练日前后可加一份低 GI 主食，帮助维持训练表现。"));
        } else if (carbGap.compareTo(new BigDecimal("-50")) < 0) {
            actions.add(action("carb", "low", "主食份量收一收", "后续餐次把主食减半，多用蔬菜增加饱腹感。"));
        }

        if (actions.isEmpty()) {
            actions.add(action("maintain", "medium", "保持当前节奏", "继续按餐记录，优先保证蛋白、蔬菜和饮水。"));
        }
        if (summary.getSuggestions() != null) {
            notes.addAll(summary.getSuggestions());
        }

        DietActionPlanVO vo = new DietActionPlanVO();
        vo.setDate(summary.getDate());
        vo.setStatus(status);
        vo.setHeadline(headline);
        vo.setNextMealFocus(nextMealFocus);
        vo.setCaloriesGap(caloriesGap.setScale(1, RoundingMode.HALF_UP));
        vo.setProteinGap(proteinGap.setScale(1, RoundingMode.HALF_UP));
        vo.setFatGap(fatGap.setScale(1, RoundingMode.HALF_UP));
        vo.setCarbohydrateGap(carbGap.setScale(1, RoundingMode.HALF_UP));
        vo.setActions(actions);
        vo.setNotes(notes);
        return vo;
    }

    @Override
    public BodyInsightVO getBodyInsight() {
        Long userId = getCurrentUserId();
        List<HealthData> history = healthDataMapper.selectList(new LambdaQueryWrapper<HealthData>()
                .eq(HealthData::getUserId, userId)
                .orderByDesc(HealthData::getMeasureTime)
                .last("LIMIT 30"));

        BodyInsightVO vo = new BodyInsightVO();
        List<String> explanations = new ArrayList<>();
        List<String> actions = new ArrayList<>();

        if (history.isEmpty()) {
            vo.setStatus("empty");
            vo.setHeadline("还没有体测记录，先补一次基础数据。");
            vo.setBmiStatus("未记录");
            vo.setTrendLabel("等待首次记录");
            explanations.add("录入身高、体重和体脂率后，系统会解释 BMI、体重趋势和下一步建议。");
            actions.add("先完成一次体测打卡，并在 7 天后补第二次记录用于趋势判断。");
            vo.setExplanations(explanations);
            vo.setActions(actions);
            return vo;
        }

        HealthData latest = history.get(0);
        HealthData previous = history.size() > 1 ? history.get(1) : null;
        BigDecimal weightDelta = previous != null && latest.getWeight() != null && previous.getWeight() != null
                ? latest.getWeight().subtract(previous.getWeight()).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal bodyFatDelta = previous != null && latest.getBodyFatRate() != null && previous.getBodyFatRate() != null
                ? latest.getBodyFatRate().subtract(previous.getBodyFatRate()).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal bmi = valueOr(latest.getBmi(), BigDecimal.ZERO);
        String bmiStatus = resolveBmiStatus(bmi);
        String trendLabel = resolveTrendLabel(weightDelta, bodyFatDelta);
        String headline = buildBodyHeadline(bmiStatus, weightDelta, bodyFatDelta);

        explanations.add("BMI 当前为 " + (bmi.compareTo(BigDecimal.ZERO) > 0 ? bmi : "--") + "，属于" + bmiStatus + "区间。");
        if (previous == null) {
            explanations.add("目前只有 1 条体测记录，趋势判断还需要下一次记录。");
            actions.add("保持同一时间、同一体测设备记录，减少水分波动带来的误差。");
        } else {
            explanations.add("较上次体测，体重变化 " + signed(weightDelta) + " kg，体脂变化 " + signed(bodyFatDelta) + "%。");
            if (weightDelta.abs().compareTo(new BigDecimal("1.5")) > 0) {
                actions.add("体重短期变化较大，优先复查饮食记录、睡眠和水分摄入。");
            }
            if (bodyFatDelta.compareTo(BigDecimal.ZERO) < 0 && weightDelta.compareTo(BigDecimal.ZERO) <= 0) {
                actions.add("体脂和体重同步下降，继续保持力量训练和蛋白摄入。");
            } else if (bodyFatDelta.compareTo(BigDecimal.ZERO) > 0) {
                actions.add("体脂上升时，先降低油脂和精制糖，再保证每周 2-3 次力量训练。");
            }
        }

        if ("偏瘦".equals(bmiStatus)) {
            actions.add("增加每日总热量和蛋白质，训练以增肌力量为主。");
        } else if ("超重".equals(bmiStatus) || "肥胖".equals(bmiStatus)) {
            actions.add("把目标设为每周小幅下降，优先控制热量缺口和增加步行。");
        } else {
            actions.add("BMI 正常时，重点关注围度、体脂和训练表现，不只看体重。");
        }

        vo.setStatus("ready");
        vo.setHeadline(headline);
        vo.setBmiStatus(bmiStatus);
        vo.setTrendLabel(trendLabel);
        vo.setLatestWeight(latest.getWeight());
        vo.setLatestBmi(latest.getBmi());
        vo.setLatestBodyFatRate(latest.getBodyFatRate());
        vo.setWeightDelta(weightDelta);
        vo.setBodyFatDelta(bodyFatDelta);
        vo.setLatestMeasureTime(latest.getMeasureTime() == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(latest.getMeasureTime()));
        vo.setExplanations(explanations);
        vo.setActions(actions);
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

    private BigDecimal valueOr(BigDecimal value, BigDecimal fallback) {
        return value == null ? fallback : value;
    }

    private DietActionPlanVO.ActionItem action(String kind, String priority, String title, String detail) {
        DietActionPlanVO.ActionItem item = new DietActionPlanVO.ActionItem();
        item.setKind(kind);
        item.setPriority(priority);
        item.setTitle(title);
        item.setDetail(detail);
        return item;
    }

    private String resolveBmiStatus(BigDecimal bmi) {
        if (bmi == null || bmi.compareTo(BigDecimal.ZERO) <= 0) {
            return "未记录";
        }
        if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
            return "偏瘦";
        }
        if (bmi.compareTo(new BigDecimal("24")) < 0) {
            return "标准";
        }
        if (bmi.compareTo(new BigDecimal("28")) < 0) {
            return "超重";
        }
        return "肥胖";
    }

    private String resolveTrendLabel(BigDecimal weightDelta, BigDecimal bodyFatDelta) {
        if (weightDelta == null || bodyFatDelta == null) {
            return "趋势不足";
        }
        if (weightDelta.compareTo(BigDecimal.ZERO) < 0 && bodyFatDelta.compareTo(BigDecimal.ZERO) <= 0) {
            return "减脂趋势";
        }
        if (weightDelta.compareTo(BigDecimal.ZERO) > 0 && bodyFatDelta.compareTo(BigDecimal.ZERO) <= 0) {
            return "增肌趋势";
        }
        if (weightDelta.abs().compareTo(new BigDecimal("0.3")) <= 0 && bodyFatDelta.abs().compareTo(new BigDecimal("0.5")) <= 0) {
            return "基本稳定";
        }
        return "需要观察";
    }

    private String buildBodyHeadline(String bmiStatus, BigDecimal weightDelta, BigDecimal bodyFatDelta) {
        if ("未记录".equals(bmiStatus)) {
            return "基础体测信息还不完整，先补齐身高和体重。";
        }
        if (weightDelta.compareTo(BigDecimal.ZERO) < 0 && bodyFatDelta.compareTo(BigDecimal.ZERO) <= 0) {
            return "体重和体脂正在下降，当前方向偏向减脂。";
        }
        if (weightDelta.compareTo(BigDecimal.ZERO) > 0 && bodyFatDelta.compareTo(BigDecimal.ZERO) <= 0) {
            return "体重上升但体脂未升，可能是训练带来的增肌信号。";
        }
        if ("标准".equals(bmiStatus)) {
            return "BMI 处于标准区间，继续看体脂和训练表现。";
        }
        return "身体指标需要持续观察，用饮食和训练一起调整。";
    }

    private String signed(BigDecimal value) {
        if (value == null) {
            return "--";
        }
        return value.compareTo(BigDecimal.ZERO) > 0 ? "+" + value : value.toString();
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
