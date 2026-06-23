package com.campus.repair.service;

import com.campus.repair.dto.EvaluationDTO;
import com.campus.repair.entity.Evaluation;

public interface EvaluationService {

    /**
     * 提交评价
     */
    Evaluation submitEvaluation(Long studentId, EvaluationDTO dto);

    /**
     * 查询工单的评价
     */
    Evaluation getByOrderId(Long orderId);
}
