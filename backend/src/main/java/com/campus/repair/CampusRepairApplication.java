package com.campus.repair;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园宿舍智能报修与服务评价系统 - 启动类
 */
@SpringBootApplication
public class CampusRepairApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusRepairApplication.class, args);
        System.out.println("========================================");
        System.out.println("  校园宿舍智能报修与服务评价系统 启动成功");
        System.out.println("  API 地址: http://localhost:8080");
        System.out.println("========================================");
    }
}
