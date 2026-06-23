package com.campus.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.repair.dto.AssignDTO;
import com.campus.repair.dto.RepairOrderDTO;
import com.campus.repair.entity.RepairOrder;

import java.util.Map;

public interface RepairOrderService {

    /**
     * 学生提交报修申请
     */
    RepairOrder createOrder(Long studentId, RepairOrderDTO dto);

    /**
     * 学生查询自己的报修列表
     */
    Page<RepairOrder> studentOrderList(Long studentId, Integer page, Integer size, String status);

    /**
     * 维修人员查询工单列表
     */
    Page<RepairOrder> repairerOrderList(Long repairerId, Integer page, Integer size, String status);

    /**
     * 管理员查询所有工单
     */
    Page<RepairOrder> adminOrderList(Integer page, Integer size, String status, String repairType);

    /**
     * 查询工单详情（含操作日志时间轴）
     */
    Map<String, Object> orderDetail(Long orderId);

    /**
     * 派单（智能派单或手动派单）
     */
    void assignOrder(Long adminId, AssignDTO dto);

    /**
     * 维修人员接单
     */
    void acceptOrder(Long repairerId, Long orderId);

    /**
     * 维修人员完工上报
     */
    void completeOrder(Long repairerId, Long orderId, String remark, String images);

    /**
     * 取消工单
     */
    void cancelOrder(Long operatorId, Long orderId, String role);

    /**
     * 获取维修人员的工单统计
     */
    Map<String, Object> repairerStats(Long repairerId);
}
