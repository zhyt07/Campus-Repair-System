package com.campus.repair.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.repair.entity.RepairOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {

    /**
     * 按类型统计
     */
    @Select("SELECT repair_type AS name, COUNT(*) AS value FROM repair_order WHERE deleted = 0 GROUP BY repair_type")
    List<Map<String, Object>> countByType();

    /**
     * 按区域统计
     */
    @Select("SELECT location AS name, COUNT(*) AS value FROM repair_order WHERE deleted = 0 GROUP BY location")
    List<Map<String, Object>> countByArea();

    /**
     * 近7天趋势
     */
    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS count FROM repair_order WHERE deleted = 0 AND create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> trend7Days();

    /**
     * 获取当日最大序号
     */
    @Select("SELECT MAX(CAST(SUBSTRING(order_no, 12) AS UNSIGNED)) FROM repair_order WHERE order_no LIKE CONCAT('REP', DATE_FORMAT(CURDATE(), '%Y%m%d'), '%')")
    Long getTodayMaxSeq();
}
