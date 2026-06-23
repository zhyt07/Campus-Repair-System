package com.campus.repair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.repair.common.PageResult;
import com.campus.repair.common.Result;
import com.campus.repair.dto.RepairerDTO;
import com.campus.repair.entity.Repairer;
import com.campus.repair.service.RepairerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 维修人员管理控制器（管理员）
 */
@RestController
@RequestMapping("/api/repairers")
public class RepairerController {

    @Autowired
    private RepairerService repairerService;

    /**
     * 维修人员列表
     * GET /api/repairers?page=1&size=10&name=&area=
     */
    @GetMapping
    public Result<PageResult<Repairer>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String area) {
        Page<Repairer> result = repairerService.list(page, size, name, area);
        return Result.success(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 维修人员详情
     * GET /api/repairers/{id}
     */
    @GetMapping("/{id}")
    public Result<Repairer> detail(@PathVariable Long id) {
        Repairer repairer = repairerService.detail(id);
        return Result.success(repairer);
    }

    /**
     * 新增维修人员
     * POST /api/repairers
     */
    @PostMapping
    public Result<Repairer> add(@Valid @RequestBody RepairerDTO dto) {
        Repairer repairer = repairerService.add(dto);
        return Result.success("新增成功", repairer);
    }

    /**
     * 编辑维修人员
     * PUT /api/repairers
     */
    @PutMapping
    public Result<Repairer> update(@Valid @RequestBody RepairerDTO dto) {
        Repairer repairer = repairerService.update(dto);
        return Result.success("编辑成功", repairer);
    }

    /**
     * 删除维修人员
     * DELETE /api/repairers/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        repairerService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 更新维修人员在岗状态
     * PUT /api/repairers/{id}/status
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                      @RequestParam Integer status) {
        repairerService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }
}
