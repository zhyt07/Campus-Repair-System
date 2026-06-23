package com.campus.repair.controller;

import com.campus.repair.common.Result;
import com.campus.repair.dto.StatisticsDTO;
import com.campus.repair.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 统计分析控制器（管理员）
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取统计概览
     * GET /api/statistics/overview
     */
    @GetMapping("/overview")
    public Result<StatisticsDTO> overview() {
        StatisticsDTO dto = statisticsService.overview();
        return Result.success(dto);
    }

    /**
     * 清除统计缓存
     * DELETE /api/statistics/cache
     */
    @DeleteMapping("/cache")
    public Result<Void> clearCache() {
        statisticsService.clearCache();
        return Result.success("缓存已清除");
    }
}
