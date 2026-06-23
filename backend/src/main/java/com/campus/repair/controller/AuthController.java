package com.campus.repair.controller;

import com.campus.repair.common.Result;
import com.campus.repair.dto.LoginDTO;
import com.campus.repair.dto.LoginVO;
import com.campus.repair.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO vo = authService.login(loginDTO);
        return Result.success("登录成功", vo);
    }

    /**
     * 获取当前用户信息
     * GET /api/auth/info
     */
    @GetMapping("/info")
    public Result<?> info(@RequestAttribute("userId") Long userId,
                          @RequestAttribute("role") String role) {
        // 简单返回用户ID和角色，前端可据此做权限控制
        return Result.success();
    }
}
