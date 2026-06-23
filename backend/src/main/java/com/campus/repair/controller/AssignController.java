package com.campus.repair.controller;

import com.campus.repair.common.Result;
import com.campus.repair.dto.AssignDTO;
import com.campus.repair.service.RepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 派单控制器（管理员）
 */
@RestController
@RequestMapping("/api/assign")
public class AssignController {

    @Autowired
    private RepairOrderService orderService;

    /**
     * 派单（智能派单或手动派单）
     * POST /api/assign
     */
    @PostMapping
    public Result<Void> assign(@RequestAttribute("userId") Long userId,
                                @Valid @RequestBody AssignDTO dto) {
        orderService.assignOrder(userId, dto);
        return Result.success("派单成功");
    }
}
