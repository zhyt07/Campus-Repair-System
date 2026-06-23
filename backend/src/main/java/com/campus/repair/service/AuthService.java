package com.campus.repair.service;

import com.campus.repair.dto.LoginDTO;
import com.campus.repair.dto.LoginVO;

public interface AuthService {

    /**
     * 登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 根据用户名和角色查找用户
     */
    Object findUserByUsernameAndRole(String username, String role);
}
