package com.campus.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.repair.common.BusinessException;
import com.campus.repair.dto.RepairerDTO;
import com.campus.repair.entity.Repairer;
import com.campus.repair.mapper.RepairerMapper;
import com.campus.repair.service.RepairerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepairerServiceImpl implements RepairerService {

    @Autowired
    private RepairerMapper repairerMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<Repairer> list(Integer page, Integer size, String name, String area) {
        LambdaQueryWrapper<Repairer> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Repairer::getName, name);
        }
        if (area != null && !area.isEmpty()) {
            wrapper.eq(Repairer::getArea, area);
        }
        wrapper.orderByDesc(Repairer::getCreateTime);
        return repairerMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Repairer detail(Long id) {
        Repairer repairer = repairerMapper.selectById(id);
        if (repairer == null) {
            throw new BusinessException("维修人员不存在");
        }
        return repairer;
    }

    @Override
    @Transactional
    public Repairer add(RepairerDTO dto) {
        // 检查工号唯一
        LambdaQueryWrapper<Repairer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Repairer::getJobNumber, dto.getJobNumber());
        if (repairerMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("工号已存在");
        }

        Repairer repairer = new Repairer();
        repairer.setJobNumber(dto.getJobNumber());
        repairer.setName(dto.getName());
        repairer.setPassword(passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "123456"));
        repairer.setPhone(dto.getPhone());
        repairer.setSkills(dto.getSkills());
        repairer.setArea(dto.getArea());
        repairer.setCurrentLoad(0);
        repairer.setMaxLoad(dto.getMaxLoad() != null ? dto.getMaxLoad() : 10);
        repairer.setRating(5.0);
        repairer.setStatus(1);

        repairerMapper.insert(repairer);
        return repairer;
    }

    @Override
    @Transactional
    public Repairer update(RepairerDTO dto) {
        Repairer repairer = repairerMapper.selectById(dto.getId());
        if (repairer == null) {
            throw new BusinessException("维修人员不存在");
        }

        repairer.setJobNumber(dto.getJobNumber());
        repairer.setName(dto.getName());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            repairer.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        repairer.setPhone(dto.getPhone());
        repairer.setSkills(dto.getSkills());
        repairer.setArea(dto.getArea());
        if (dto.getMaxLoad() != null) {
            repairer.setMaxLoad(dto.getMaxLoad());
        }

        repairerMapper.updateById(repairer);
        return repairer;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Repairer repairer = repairerMapper.selectById(id);
        if (repairer == null) {
            throw new BusinessException("维修人员不存在");
        }
        // 逻辑删除
        repairerMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Repairer repairer = repairerMapper.selectById(id);
        if (repairer == null) {
            throw new BusinessException("维修人员不存在");
        }
        repairer.setStatus(status);
        repairerMapper.updateById(repairer);
    }
}
