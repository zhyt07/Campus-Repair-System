package com.campus.repair.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修工单表
 */
@Data
@TableName("repair_order")
public class RepairOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 报修编号：REP + yyyyMMdd + 4位序号 */
    private String orderNo;

    /** 报修学生ID */
    private Long studentId;

    /** 报修类型：水电,木工,空调,网络,门窗,其他 */
    private String repairType;

    /** 故障描述 */
    private String description;

    /** 报修位置（楼栋+宿舍号） */
    private String location;

    /** 故障图片URL，逗号分隔 */
    private String images;

    /** 维修人员ID */
    private Long repairerId;

    /** 工单状态：PENDING-待派单, ASSIGNED-已派单, REPAIRING-维修中, COMPLETED-已完成, EVALUATED-已评价, CANCELLED-已取消 */
    private String status;

    /** 派单时间 */
    private LocalDateTime assignTime;

    /** 接单时间 */
    private LocalDateTime acceptTime;

    /** 完工时间 */
    private LocalDateTime completeTime;

    /** 评价时间 */
    private LocalDateTime evaluateTime;

    /** 维修备注 */
    private String repairRemark;

    /** 维修后照片URL */
    private String repairImages;

    /** 备注 */
    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
