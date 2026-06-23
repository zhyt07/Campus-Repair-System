package com.campus.repair.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志表
 */
@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人姓名 */
    private String operatorName;

    /** 操作人角色 */
    private String operatorRole;

    /** 操作类型：CREATE_ORDER, ASSIGN_ORDER, ACCEPT_ORDER, COMPLETE_ORDER, EVALUATE, CANCEL_ORDER 等 */
    private String operationType;

    /** 操作描述 */
    private String description;

    /** 关联工单ID */
    private Long orderId;

    /** 操作IP */
    private String ip;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
