package com.campus.repair.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应
 */
@Data
@AllArgsConstructor
public class LoginVO {

    /** JWT Token */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 姓名 */
    private String realName;

    /** 角色 */
    private String role;

    /** 手机号 */
    private String phone;
}
