package com.gym.modules.health.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gym.common.exception.ServiceException;
import com.gym.modules.health.domain.vo.FoodAnalysisVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class AiFoodServiceImpl {

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.api-url}")
    private String apiUrl;

    public FoodAnalysisVO analyzeFood(MultipartFile file) {
        try {
            // 1. 图片转 Base64
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            // 智谱/OpenAI 格式要求的图片前缀
            String imageUrl = "data:image/jpeg;base64," + base64Image;

            // 2. 构造提示词 (Prompt)
            // 提示词非常关键，要求它只返回纯 JSON
            String prompt = "你是一名专业营养师。请分析图片中的食物。请严格只返回一个 JSON 格式的字符串，不要包含 ```json 等标记，不要包含任何多余的文字。\n" +
                    "JSON 格式要求如下：\n" +
                    "{\n" +
                    "  \"name\": \"食物名称\",\n" +
                    "  \"estimatedWeight\": 预估总重量(数字，单位克，整数),\n" +
                    "  \"caloriesPer100g\": 每100g热量(数字，单位千卡),\n" +
                    "  \"proteinPer100g\": 每100g蛋白质(数字，单位克),\n" +
                    "  \"fatPer100g\": 每100g脂肪(数字，单位克),\n" +
                    "  \"carbPer100g\": 每100g碳水(数字，单位克),\n" +
                    "  \"analysis\": \"简短评价(20字以内)\"\n" +
                    "}";

            // 3. 构造请求体 (智谱 GLM-4V 格式)
            Map<String, Object> payload = new HashMap<>();
            // 重要：模型名称必须是 glm-4v 或 glm-4v-flash (flash 更快更便宜)
            payload.put("model", "glm-4v-flash");
            payload.put("temperature", 0.5); // 降低随机性，让输出更稳定

            // 构造消息内容
            List<Map<String, Object>> contentList = new ArrayList<>();

            // 放入文字提示
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", prompt);
            contentList.add(textContent);

            // 放入图片
            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image_url");
            Map<String, Object> urlMap = new HashMap<>();
            urlMap.put("url", imageUrl);
            imageContent.put("image_url", urlMap);
            contentList.add(imageContent);

            payload.put("messages", Collections.singletonList(
                    new HashMap<String, Object>() {{
                        put("role", "user");
                        put("content", contentList);
                    }}
            ));

            // 4. 发送请求
            // 注意：智谱的 API Key 是可以直接作为 Bearer Token 使用的
            String response = HttpRequest.post(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(payload))
                    .timeout(60000) // 图片识别比较慢，建议设置 60秒超时
                    .execute()
                    .body();

            log.info("AI 原始响应: {}", response);

            // 5. 解析结果
            JSONObject jsonResponse = JSONUtil.parseObj(response);

            // 检查是否有错误 -> 降级返回模拟分析结果
            if (jsonResponse.containsKey("error")) {
                String errMsg = jsonResponse.getJSONObject("error").getStr("message");
                log.warn("AI API 不可用，返回降级结果: {}", errMsg);
                return buildFallbackResult(file);
            }

            // 获取内容
            String content = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");

            // 清理可能的 Markdown 格式 (有时候 AI 还是会忍不住加 ```json)
            content = content.replaceAll("```json", "").replaceAll("```", "").trim();

            // 尝试找到 JSON 的开始和结束（防止 AI 在前后说废话）
            int jsonStart = content.indexOf("{");
            int jsonEnd = content.lastIndexOf("}");
            if (jsonStart != -1 && jsonEnd != -1) {
                content = content.substring(jsonStart, jsonEnd + 1);
            }

            return JSONUtil.toBean(content, FoodAnalysisVO.class);

        } catch (IOException e) {
            throw new ServiceException("图片处理失败");
        } catch (Exception e) {
            log.error("AI 识别异常，返回降级结果", e);
            return buildFallbackResult(file);
        }
    }

    /**
     * AI API 不可用时的降级方案：返回可用的模拟分析结果
     */
    private FoodAnalysisVO buildFallbackResult(MultipartFile file) {
        FoodAnalysisVO result = new FoodAnalysisVO();
        result.setName("食物（待确认）");
result.setEstimatedWeight(java.math.BigDecimal.valueOf(200));
        result.setCaloriesPer100g(java.math.BigDecimal.valueOf(150));
        result.setProteinPer100g(java.math.BigDecimal.valueOf(10));
        result.setFatPer100g(java.math.BigDecimal.valueOf(5));
        result.setCarbPer100g(java.math.BigDecimal.valueOf(18));
        result.setAnalysis("AI 服务暂时不可用，请手动录入或稍后重试。以上为估算值，仅供参考。");
        return result;
    }
}