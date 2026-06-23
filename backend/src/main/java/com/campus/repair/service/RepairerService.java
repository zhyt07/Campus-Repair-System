package com.campus.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.repair.dto.RepairerDTO;
import com.campus.repair.entity.Repairer;

public interface RepairerService {

    /**
     * 维修人员列表
     */
    Page<Repairer> list(Integer page, Integer size, String name, String area);

    /**
     * 维修人员详情
     */
    Repairer detail(Long id);

    /**
     * 新增维修人员
     */
    Repairer add(RepairerDTO dto);

    /**
     * 编辑维修人员
     */
    Repairer update(RepairerDTO dto);

    /**
     * 删除维修人员（逻辑删除）
     */
    void delete(Long id);

    /**
     * 更新维修人员在岗状态
     */
    void updateStatus(Long id, Integer status);
}
