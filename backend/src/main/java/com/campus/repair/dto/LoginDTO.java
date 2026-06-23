package com.campus.repair.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求
 */
@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 角色：STUDENT, REPAIRER, ADMIN */
    @NotBlank(message = "角色不能为空")
    private String role;
}
