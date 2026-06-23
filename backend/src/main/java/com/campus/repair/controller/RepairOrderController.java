package com.campus.repair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.repair.common.PageResult;
import com.campus.repair.common.Result;
import com.campus.repair.dto.RepairOrderDTO;
import com.campus.repair.entity.RepairOrder;
import com.campus.repair.service.RepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 报修工单控制器
 */
@RestController
@RequestMapping("/api/orders")
public class RepairOrderController {

    @Autowired
    private RepairOrderService orderService;

    /**
     * 学生提交报修
     * POST /api/orders
     */
    @PostMapping
    public Result<RepairOrder> create(@RequestAttribute("userId") Long userId,
                                       @Valid @RequestBody RepairOrderDTO dto) {
        RepairOrder order = orderService.createOrder(userId, dto);
        return Result.success("报修提交成功", order);
    }

    /**
     * 学生端 - 我的报修列表
     * GET /api/orders/student?page=1&size=10&status=
     */
    @GetMapping("/student")
    public Result<PageResult<RepairOrder>> studentList(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        Page<RepairOrder> result = orderService.studentOrderList(userId, page, size, status);
        return Result.success(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 维修端 - 工单列表
     * GET /api/orders/repairer?page=1&size=10&status=
     */
    @GetMapping("/repairer")
    public Result<PageResult<RepairOrder>> repairerList(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        Page<RepairOrder> result = orderService.repairerOrderList(userId, page, size, status);
        return Result.success(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 管理端 - 全部工单列表
     * GET /api/orders/admin?page=1&size=10&status=&repairType=
     */
    @GetMapping("/admin")
    public Result<PageResult<RepairOrder>> adminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String repairType) {
        Page<RepairOrder> result = orderService.adminOrderList(page, size, status, repairType);
        return Result.success(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 工单详情（含时间轴）
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Map<String, Object> result = orderService.orderDetail(id);
        return Result.success(result);
    }

    /**
     * 维修人员接单
     * PUT /api/orders/{id}/accept
     */
    @PutMapping("/{id}/accept")
    public Result<Void> accept(@RequestAttribute("userId") Long userId,
                                @PathVariable Long id) {
        orderService.acceptOrder(userId, id);
        return Result.success("接单成功");
    }

    /**
     * 维修人员完工上报
     * PUT /api/orders/{id}/complete
     */
    @PutMapping("/{id}/complete")
    public Result<Void> complete(@RequestAttribute("userId") Long userId,
                                  @PathVariable Long id,
                                  @RequestParam(required = false) String remark,
                                  @RequestParam(required = false) String images) {
        orderService.completeOrder(userId, id, remark, images);
        return Result.success("完工上报成功");
    }

    /**
     * 取消工单
     * PUT /api/orders/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@RequestAttribute("userId") Long userId,
                                @RequestAttribute("role") String role,
                                @PathVariable Long id) {
        orderService.cancelOrder(userId, id, role);
        return Result.success("工单已取消");
    }

    /**
     * 维修人员工单统计
     * GET /api/orders/repairer/stats
     */
    @GetMapping("/repairer/stats")
    public Result<Map<String, Object>> repairerStats(@RequestAttribute("userId") Long userId) {
        Map<String, Object> stats = orderService.repairerStats(userId);
        return Result.success(stats);
    }
}
