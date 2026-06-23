package com.campus.repair.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 服务评价表
 */
@Data
@TableName("evaluation")
public class Evaluation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联工单ID */
    private Long orderId;

    /** 学生ID */
    private Long studentId;

    /** 维修人员ID */
    private Long repairerId;

    /** 星级评分（1-5） */
    private Integer rating;

    /** 评价内容 */
    private String content;

    /** 标签，逗号分隔：服务态度好,维修速度快,技术过硬 */
    private String tags;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
