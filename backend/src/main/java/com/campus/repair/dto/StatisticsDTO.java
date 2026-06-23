package com.campus.repair.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 统计结果
 */
@Data
public class StatisticsDTO {

    /** 总报修数 */
    private Long totalOrders;

    /** 已完成数 */
    private Long completedOrders;

    /** 完成率 */
    private Double completionRate;

    /** 平均评分 */
    private Double avgRating;

    /** 今日新增 */
    private Long todayNew;

    /** 按类型统计 */
    private List<Map<String, Object>> byType;

    /** 按区域统计 */
    private List<Map<String, Object>> byArea;

    /** 近7天趋势 */
    private List<Map<String, Object>> trend;
}
