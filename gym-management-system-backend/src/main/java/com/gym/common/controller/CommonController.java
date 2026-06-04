package com.gym.common.controller;

import com.gym.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Tag(name = "通用接口")
@RestController
@RequestMapping("/common")
public class CommonController {

    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }

        try {
            // 获取项目根目录下的 files 目录
            String projectPath = System.getProperty("user.dir");
            String uploadPath = projectPath + "/files/";
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

            // 保存文件
            File dest = new File(uploadPath + fileName);
            file.transferTo(dest);

            // 生成访问 URL
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/")
                    .path(fileName)
                    .toUriString();

            // 【关键修改】将 fileUrl 放在第一个参数，作为 data 返回
            return R.ok(fileUrl);
            // 或者使用 return R.ok(fileUrl, "上传成功"); 取决于 R.java 的定义，通常 ok(data) 是最稳妥的

        } catch (IOException e) {
            e.printStackTrace();
            return R.fail("文件上传失败: " + e.getMessage());
        }
    }
}