package com.campus.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.repair.dto.AnnouncementDTO;
import com.campus.repair.entity.Announcement;

public interface AnnouncementService {

    /**
     * 发布公告
     */
    Announcement publish(Long publisherId, AnnouncementDTO dto);

    /**
     * 公告列表
     */
    Page<Announcement> list(Integer page, Integer size);

    /**
     * 公告详情
     */
    Announcement detail(Long id);

    /**
     * 删除公告
     */
    void delete(Long id);
}
