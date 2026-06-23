package com.campus.repair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.repair.common.PageResult;
import com.campus.repair.common.Result;
import com.campus.repair.dto.AnnouncementDTO;
import com.campus.repair.entity.Announcement;
import com.campus.repair.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 管理员发布公告
     * POST /api/announcements
     */
    @PostMapping
    public Result<Announcement> publish(@RequestAttribute("userId") Long userId,
                                         @Valid @RequestBody AnnouncementDTO dto) {
        Announcement announcement = announcementService.publish(userId, dto);
        return Result.success("发布成功", announcement);
    }

    /**
     * 公告列表（所有人可查看）
     * GET /api/announcements?page=1&size=10
     */
    @GetMapping
    public Result<PageResult<Announcement>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Announcement> result = announcementService.list(page, size);
        return Result.success(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    /**
     * 公告详情
     * GET /api/announcements/{id}
     */
    @GetMapping("/{id}")
    public Result<Announcement> detail(@PathVariable Long id) {
        Announcement announcement = announcementService.detail(id);
        return Result.success(announcement);
    }

    /**
     * 删除公告
     * DELETE /api/announcements/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success("删除成功");
    }
}
