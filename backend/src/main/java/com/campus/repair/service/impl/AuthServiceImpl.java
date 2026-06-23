package com.campus.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.repair.common.BusinessException;
import com.campus.repair.dto.LoginDTO;
import com.campus.repair.dto.LoginVO;
import com.campus.repair.entity.Repairer;
import com.campus.repair.entity.User;
import com.campus.repair.mapper.RepairerMapper;
import com.campus.repair.mapper.UserMapper;
import com.campus.repair.service.AuthService;
import com.campus.repair.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RepairerMapper repairerMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String role = loginDTO.getRole();
        String username = loginDTO.getUsername();

        if ("REPAIRER".equals(role)) {
            // 维修人员登录
            LambdaQueryWrapper<Repairer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Repairer::getJobNumber, username);
            Repairer repairer = repairerMapper.selectOne(wrapper);
            if (repairer == null) {
                throw new BusinessException("工号不存在");
            }
            if (repairer.getStatus() == 0) {
                throw new BusinessException("账号已被禁用");
            }
            if (!passwordEncoder.matches(loginDTO.getPassword(), repairer.getPassword())) {
                throw new BusinessException("密码错误");
            }
            String token = jwtUtil.generateToken(repairer.getId(), repairer.getJobNumber(), "REPAIRER");
            return new LoginVO(token, repairer.getId(), repairer.getJobNumber(),
                    repairer.getName(), "REPAIRER", repairer.getPhone());
        } else {
            // 学生/管理员登录
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            User user = userMapper.selectOne(wrapper);
            if (user == null) {
                throw new BusinessException("用户名不存在");
            }
            if (user.getStatus() == 0) {
                throw new BusinessException("账号已被禁用");
            }
            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                throw new BusinessException("密码错误");
            }
            if (!role.equals(user.getRole())) {
                throw new BusinessException("角色不匹配");
            }
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            return new LoginVO(token, user.getId(), user.getUsername(),
                    user.getRealName(), user.getRole(), user.getPhone());
        }
    }

    @Override
    public Object findUserByUsernameAndRole(String username, String role) {
        if ("REPAIRER".equals(role)) {
            LambdaQueryWrapper<Repairer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Repairer::getJobNumber, username);
            return repairerMapper.selectOne(wrapper);
        } else {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            return userMapper.selectOne(wrapper);
        }
    }
}
