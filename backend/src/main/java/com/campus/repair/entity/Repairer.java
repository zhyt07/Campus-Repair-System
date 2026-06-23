package com.campus.repair.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 维修人员表
 */
@Data
@TableName("repairer")
public class Repairer {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工号 */
    private String jobNumber;

    /** 姓名 */
    private String name;

    /** 密码（BCrypt加密） */
    private String password;

    /** 手机号 */
    private String phone;

    /** 维修技能，逗号分隔：水电,木工,空调,网络,门窗 */
    private String skills;

    /** 负责区域：东苑,西苑,南苑,北苑 */
    private String area;

    /** 当前负载（正在处理中的工单数） */
    private Integer currentLoad;

    /** 最大负载 */
    private Integer maxLoad;

    /** 综合评分（1-5） */
    private Double rating;

    /** 状态：1-在岗, 0-离岗 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
