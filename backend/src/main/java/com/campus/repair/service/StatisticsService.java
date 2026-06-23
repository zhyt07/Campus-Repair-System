package com.campus.repair.service;

import com.campus.repair.dto.StatisticsDTO;

public interface StatisticsService {

    /**
     * 获取统计概览数据（带Redis缓存）
     */
    StatisticsDTO overview();

    /**
     * 清除统计缓存
     */
    void clearCache();
}
