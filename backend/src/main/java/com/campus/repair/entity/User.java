package com.campus.repair.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表（学生/管理员）
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学号/工号 */
    private String username;

    /** 密码（BCrypt加密） */
    private String password;

    /** 姓名 */
    private String realName;

    /** 角色：STUDENT-学生, ADMIN-管理员 */
    private String role;

    /** 手机号 */
    private String phone;

    /** 宿舍楼栋 */
    private String dormitory;

    /** 宿舍号 */
    private String roomNumber;

    /** 账号状态：1-正常, 0-禁用 */
    private Integer status;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
