package com.gym.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 保留原有的 Knife4j 文档配置
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

        // 2. [新增] 配置本地文件映射
        // 当访问 http://localhost:8080/files/xxx.jpg 时，映射到本地项目根目录下的 files/xxx.jpg
        // 注意：file: 后面必须拼接绝对路径，System.getProperty("user.dir") 获取当前运行根目录
        String uploadPath = "file:" + System.getProperty("user.dir") + "/files/";

        registry.addResourceHandler("/files/**")
                .addResourceLocations(uploadPath);
    }
}