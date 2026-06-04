package com.gym;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动程序
 */
@SpringBootApplication
// 扫描 com.gym 目录下所有模块的 mapper 接口
@MapperScan("com.gym.**.mapper")
public class GymApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  健身房管理系统启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}