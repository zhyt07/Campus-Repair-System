package com.campus.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.repair.common.BusinessException;
import com.campus.repair.dto.AssignDTO;
import com.campus.repair.dto.RepairOrderDTO;
import com.campus.repair.entity.OperationLog;
import com.campus.repair.entity.RepairOrder;
import com.campus.repair.entity.Repairer;
import com.campus.repair.mapper.RepairOrderMapper;
import com.campus.repair.mapper.RepairerMapper;
import com.campus.repair.mapper.OperationLogMapper;
import com.campus.repair.service.RepairOrderService;
import com.campus.repair.util.OrderNoGenerator;
import com.campus.repair.util.PhoneDesensitizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RepairOrderServiceImpl implements RepairOrderService {

    @Autowired
    private RepairOrderMapper orderMapper;

    @Autowired
    private RepairerMapper repairerMapper;

    @Autowired
    private OperationLogMapper logMapper;

    @Autowired
    private OrderNoGenerator orderNoGenerator;

    @Override
    @Transactional
    public RepairOrder createOrder(Long studentId, RepairOrderDTO dto) {
        // 检查5分钟内是否存在同类未完成的报修
        LambdaQueryWrapper<RepairOrder> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(RepairOrder::getStudentId, studentId)
                .eq(RepairOrder::getRepairType, dto.getRepairType())
                .eq(RepairOrder::getLocation, dto.getLocation())
                .notIn(RepairOrder::getStatus, "COMPLETED", "EVALUATED", "CANCELLED")
                .ge(RepairOrder::getCreateTime, LocalDateTime.now().minusMinutes(5));
        Long count = orderMapper.selectCount(checkWrapper);
        if (count > 0) {
            throw new BusinessException("5分钟内已存在同类未完成的报修，请勿重复提交");
        }

        RepairOrder order = new RepairOrder();
        order.setOrderNo(orderNoGenerator.generate());
        order.setStudentId(studentId);
        order.setRepairType(dto.getRepairType());
        order.setDescription(dto.getDescription());
        order.setLocation(dto.getLocation());
        order.setImages(dto.getImages());
        order.setStatus("PENDING");

        orderMapper.insert(order);
        return order;
    }

    @Override
    public Page<RepairOrder> studentOrderList(Long studentId, Integer page, Integer size, String status) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getStudentId, studentId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(RepairOrder::getStatus, status);
        }
        wrapper.orderByDesc(RepairOrder::getCreateTime);
        Page<RepairOrder> pageResult = orderMapper.selectPage(new Page<>(page, size), wrapper);
        // 脱敏处理
        return pageResult;
    }

    @Override
    public Page<RepairOrder> repairerOrderList(Long repairerId, Integer page, Integer size, String status) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        if ("pending".equals(status)) {
            // 待处理：已派单或维修中
            wrapper.in(RepairOrder::getStatus, "ASSIGNED", "REPAIRING");
        } else if ("mine".equals(status)) {
            // 我的工单
            wrapper.eq(RepairOrder::getRepairerId, repairerId)
                    .in(RepairOrder::getStatus, "ASSIGNED", "REPAIRING", "COMPLETED", "EVALUATED");
        } else if ("completed".equals(status)) {
            // 已完成
            wrapper.eq(RepairOrder::getRepairerId, repairerId)
                    .in(RepairOrder::getStatus, "COMPLETED", "EVALUATED");
        } else if (status != null && !status.isEmpty()) {
            wrapper.eq(RepairOrder::getRepairerId, repairerId)
                    .eq(RepairOrder::getStatus, status);
        } else {
            wrapper.eq(RepairOrder::getRepairerId, repairerId);
        }
        wrapper.orderByDesc(RepairOrder::getCreateTime);
        return orderMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Page<RepairOrder> adminOrderList(Integer page, Integer size, String status, String repairType) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(RepairOrder::getStatus, status);
        }
        if (repairType != null && !repairType.isEmpty()) {
            wrapper.eq(RepairOrder::getRepairType, repairType);
        }
        wrapper.orderByDesc(RepairOrder::getCreateTime);
        return orderMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Map<String, Object> orderDetail(Long orderId) {
        RepairOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }

        // 查询操作日志（时间轴）
        LambdaQueryWrapper<OperationLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(OperationLog::getOrderId, orderId)
                .orderByAsc(OperationLog::getCreateTime);
        List<OperationLog> logs = logMapper.selectList(logWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("timeline", logs);
        return result;
    }

    @Override
    @Transactional
    public void assignOrder(Long adminId, AssignDTO dto) {
        RepairOrder order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException("当前工单状态不允许派单");
        }

        Long repairerId;
        if (dto.getRepairerId() != null) {
            // 手动派单
            Repairer repairer = repairerMapper.selectById(dto.getRepairerId());
            if (repairer == null || repairer.getStatus() == 0) {
                throw new BusinessException("维修人员不存在或已离岗");
            }
            repairerId = dto.getRepairerId();
        } else {
            // 智能派单
            repairerId = smartAssign(order);
        }

        order.setRepairerId(repairerId);
        order.setStatus("ASSIGNED");
        order.setAssignTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 更新维修人员负载
        Repairer repairer = repairerMapper.selectById(repairerId);
        if (repairer != null) {
            repairer.setCurrentLoad(repairer.getCurrentLoad() + 1);
            repairerMapper.updateById(repairer);
        }
    }

    @Override
    @Transactional
    public void acceptOrder(Long repairerId, Long orderId) {
        RepairOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"ASSIGNED".equals(order.getStatus())) {
            throw new BusinessException("当前工单状态不允许接单");
        }
        if (!repairerId.equals(order.getRepairerId())) {
            throw new BusinessException("该工单已分配给其他维修人员");
        }

        order.setStatus("REPAIRING");
        order.setAcceptTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void completeOrder(Long repairerId, Long orderId, String remark, String images) {
        RepairOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"REPAIRING".equals(order.getStatus())) {
            throw new BusinessException("当前工单状态不允许完工");
        }
        if (!repairerId.equals(order.getRepairerId())) {
            throw new BusinessException("您不是该工单的维修人员");
        }

        order.setStatus("COMPLETED");
        order.setCompleteTime(LocalDateTime.now());
        order.setRepairRemark(remark);
        order.setRepairImages(images);
        orderMapper.updateById(order);

        // 更新维修人员负载
        Repairer repairer = repairerMapper.selectById(repairerId);
        if (repairer != null && repairer.getCurrentLoad() > 0) {
            repairer.setCurrentLoad(repairer.getCurrentLoad() - 1);
            repairerMapper.updateById(repairer);
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Long operatorId, Long orderId, String role) {
        RepairOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        // 已完成和已评价的工单不可取消
        if ("COMPLETED".equals(order.getStatus()) || "EVALUATED".equals(order.getStatus())) {
            throw new BusinessException("已完成/已评价的工单不可取消");
        }

        order.setStatus("CANCELLED");
        orderMapper.updateById(order);

        // 如果已派单，恢复维修人员负载
        if (order.getRepairerId() != null) {
            Repairer repairer = repairerMapper.selectById(order.getRepairerId());
            if (repairer != null && repairer.getCurrentLoad() > 0) {
                repairer.setCurrentLoad(repairer.getCurrentLoad() - 1);
                repairerMapper.updateById(repairer);
            }
        }
    }

    @Override
    public Map<String, Object> repairerStats(Long repairerId) {
        Map<String, Object> stats = new HashMap<>();

        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getRepairerId, repairerId);

        // 今日新派单
        long todayNew = orderMapper.selectCount(wrapper.clone()
                .ge(RepairOrder::getAssignTime, LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)));

        // 维修中
        long repairing = orderMapper.selectCount(wrapper.clone()
                .eq(RepairOrder::getStatus, "REPAIRING"));

        // 本月完成
        long monthCompleted = orderMapper.selectCount(wrapper.clone()
                .in(RepairOrder::getStatus, "COMPLETED", "EVALUATED")
                .ge(RepairOrder::getCompleteTime, LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)));

        // 总完成
        long totalCompleted = orderMapper.selectCount(wrapper.clone()
                .in(RepairOrder::getStatus, "COMPLETED", "EVALUATED"));

        stats.put("todayNew", todayNew);
        stats.put("repairing", repairing);
        stats.put("monthCompleted", monthCompleted);
        stats.put("totalCompleted", totalCompleted);

        return stats;
    }

    /**
     * 智能派单算法：匹配度评分（总分100分）
     * 技能匹配 40分 + 区域匹配 30分 + 负载均衡 20分 + 历史评价 10分
     */
    private Long smartAssign(RepairOrder order) {
        // 获取所有在岗维修人员
        LambdaQueryWrapper<Repairer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Repairer::getStatus, 1);
        List<Repairer> repairers = repairerMapper.selectList(wrapper);

        if (repairers.isEmpty()) {
            throw new BusinessException("当前没有在岗的维修人员，请稍后再试");
        }

        // 计算每个维修人员的匹配度评分
        List<RepairerScore> scores = repairers.stream().map(repairer -> {
            int score = 0;

            // 1. 技能匹配（40分）
            if (repairer.getSkills() != null && repairer.getSkills().contains(order.getRepairType())) {
                score += 40;
            }

            // 2. 区域匹配（30分）
            if (repairer.getArea() != null && order.getLocation() != null
                    && order.getLocation().contains(repairer.getArea())) {
                score += 30;
            }

            // 3. 负载均衡（20分）：负载越低分越高
            if (repairer.getMaxLoad() != null && repairer.getMaxLoad() > 0) {
                double loadRate = (double) repairer.getCurrentLoad() / repairer.getMaxLoad();
                if (loadRate <= 0.3) {
                    score += 20;
                } else if (loadRate <= 0.6) {
                    score += 12;
                } else if (loadRate <= 0.8) {
                    score += 6;
                }
                // 满载不得分
            }

            // 4. 历史评价（10分）：评分越高分越高
            if (repairer.getRating() != null) {
                score += (int) (repairer.getRating() * 2);
            }

            return new RepairerScore(repairer.getId(), score);
        }).sorted((a, b) -> b.score - a.score).collect(Collectors.toList());

        RepairerScore best = scores.get(0);

        // 最高分低于60分，转人工派单
        if (best.score < 60) {
            throw new BusinessException(400, "无合适维修人员自动匹配（最高" + best.score + "分），请转人工派单");
        }

        return best.repairerId;
    }

    private static class RepairerScore {
        Long repairerId;
        int score;

        RepairerScore(Long repairerId, int score) {
            this.repairerId = repairerId;
            this.score = score;
        }
    }
}
