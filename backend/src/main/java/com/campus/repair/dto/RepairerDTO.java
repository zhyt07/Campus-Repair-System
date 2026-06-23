package com.campus.repair.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 维修人员新增/编辑请求
 */
@Data
public class RepairerDTO {

    private Long id;

    @NotBlank(message = "工号不能为空")
    private String jobNumber;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String password;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    /** 维修技能，逗号分隔 */
    @NotBlank(message = "技能不能为空")
    private String skills;

    /** 负责区域 */
    @NotBlank(message = "区域不能为空")
    private String area;

    private Integer maxLoad;
}
