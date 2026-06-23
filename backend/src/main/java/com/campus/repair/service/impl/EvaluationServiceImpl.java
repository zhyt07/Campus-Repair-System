package com.campus.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.repair.common.BusinessException;
import com.campus.repair.dto.EvaluationDTO;
import com.campus.repair.entity.Evaluation;
import com.campus.repair.entity.RepairOrder;
import com.campus.repair.entity.Repairer;
import com.campus.repair.mapper.EvaluationMapper;
import com.campus.repair.mapper.RepairOrderMapper;
import com.campus.repair.mapper.RepairerMapper;
import com.campus.repair.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private RepairOrderMapper orderMapper;

    @Autowired
    private RepairerMapper repairerMapper;

    @Override
    @Transactional
    public Evaluation submitEvaluation(Long studentId, EvaluationDTO dto) {
        RepairOrder order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new BusinessException("当前工单状态不允许评价");
        }
        if (!studentId.equals(order.getStudentId())) {
            throw new BusinessException("只能评价自己的报修工单");
        }

        // 检查是否已评价
        LambdaQueryWrapper<Evaluation> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(Evaluation::getOrderId, dto.getOrderId());
        if (evaluationMapper.selectCount(checkWrapper) > 0) {
            throw new BusinessException("该工单已评价");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setOrderId(dto.getOrderId());
        evaluation.setStudentId(studentId);
        evaluation.setRepairerId(order.getRepairerId());
        evaluation.setRating(dto.getRating());
        evaluation.setContent(dto.getContent());
        evaluation.setTags(dto.getTags());

        evaluationMapper.insert(evaluation);

        // 更新工单状态
        order.setStatus("EVALUATED");
        order.setEvaluateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 更新维修人员综合评分
        if (order.getRepairerId() != null) {
            updateRepairerRating(order.getRepairerId());
        }

        return evaluation;
    }

    @Override
    public Evaluation getByOrderId(Long orderId) {
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getOrderId, orderId);
        return evaluationMapper.selectOne(wrapper);
    }

    /**
     * 更新维修人员综合评分（取所有评价的平均分）
     */
    private void updateRepairerRating(Long repairerId) {
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getRepairerId, repairerId);
        wrapper.select(Evaluation::getRating);

        Double avgRating = evaluationMapper.selectList(wrapper).stream()
                .mapToInt(Evaluation::getRating)
                .average()
                .orElse(0);

        Repairer repairer = repairerMapper.selectById(repairerId);
        if (repairer != null) {
            repairer.setRating(Math.round(avgRating * 10.0) / 10.0);
            repairerMapper.updateById(repairer);
        }
    }
}
