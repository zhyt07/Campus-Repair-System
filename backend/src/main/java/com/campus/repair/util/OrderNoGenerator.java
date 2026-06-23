package com.campus.repair.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 报修单编号生成器
 * 格式：REP + yyyyMMdd + 4位序号
 */
@Component
public class OrderNoGenerator {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "REP";
    private static final String REDIS_KEY_PREFIX = "order:seq:";

    /**
     * 生成下一个报修单编号
     * 优先从Redis获取自增序号，若不可用则查询数据库
     */
    public String generate() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String redisKey = REDIS_KEY_PREFIX + dateStr;

        Long seq;
        try {
            seq = redisTemplate.opsForValue().increment(redisKey);
        } catch (Exception e) {
            // Redis不可用时的降级方案：使用时间戳后4位
            seq = System.currentTimeMillis() % 10000;
        }

        return PREFIX + dateStr + String.format("%04d", seq % 10000);
    }
}
