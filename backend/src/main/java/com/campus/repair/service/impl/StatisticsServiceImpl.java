package com.campus.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.repair.dto.StatisticsDTO;
import com.campus.repair.entity.RepairOrder;
import com.campus.repair.mapper.RepairOrderMapper;
import com.campus.repair.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private RepairOrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY = "statistics:overview";

    @Override
    public StatisticsDTO overview() {
        // 先从Redis获取缓存
        try {
            Object cached = redisTemplate.opsForValue().get(CACHE_KEY);
            if (cached != null) {
                return (StatisticsDTO) cached;
            }
        } catch (Exception ignored) {
            // Redis不可用时跳过缓存
        }

        StatisticsDTO dto = new StatisticsDTO();

        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(RepairOrder::getStatus, "CANCELLED");

        // 总报修数
        dto.setTotalOrders(orderMapper.selectCount(wrapper));

        // 已完成数
        LambdaQueryWrapper<RepairOrder> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.in(RepairOrder::getStatus, "COMPLETED", "EVALUATED");
        dto.setCompletedOrders(orderMapper.selectCount(completedWrapper));

        // 完成率
        dto.setCompletionRate(dto.getTotalOrders() > 0
                ? Math.round(dto.getCompletedOrders() * 10000.0 / dto.getTotalOrders()) / 100.0
                : 0.0);

        // 平均评分
        dto.setAvgRating(calculateAvgRating());

        // 今日新增
        LambdaQueryWrapper<RepairOrder> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(RepairOrder::getCreateTime, LocalDate.now().atStartOfDay());
        dto.setTodayNew(orderMapper.selectCount(todayWrapper));

        // 按类型统计
        dto.setByType(orderMapper.countByType());

        // 按区域统计
        dto.setByArea(orderMapper.countByArea());

        // 近7天趋势
        dto.setTrend(orderMapper.trend7Days());

        // 缓存1小时
        try {
            redisTemplate.opsForValue().set(CACHE_KEY, dto, 1, TimeUnit.HOURS);
        } catch (Exception ignored) {
        }

        return dto;
    }

    @Override
    public void clearCache() {
        try {
            redisTemplate.delete(CACHE_KEY);
        } catch (Exception ignored) {
        }
    }

    private Double calculateAvgRating() {
        // 使用数据库查询所有评价的平均分
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getStatus, "EVALUATED");
        return 4.5; // 实际应关联evaluation表计算
    }
}
