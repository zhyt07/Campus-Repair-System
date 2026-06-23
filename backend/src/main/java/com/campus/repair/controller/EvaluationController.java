package com.campus.repair.controller;

import com.campus.repair.common.Result;
import com.campus.repair.dto.EvaluationDTO;
import com.campus.repair.entity.Evaluation;
import com.campus.repair.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    /**
     * 提交评价
     * POST /api/evaluations
     */
    @PostMapping
    public Result<Evaluation> submit(@RequestAttribute("userId") Long userId,
                                      @Valid @RequestBody EvaluationDTO dto) {
        Evaluation evaluation = evaluationService.submitEvaluation(userId, dto);
        return Result.success("评价提交成功", evaluation);
    }

    /**
     * 查询工单评价
     * GET /api/evaluations/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public Result<Evaluation> getByOrderId(@PathVariable Long orderId) {
        Evaluation evaluation = evaluationService.getByOrderId(orderId);
        return Result.success(evaluation);
    }
}
