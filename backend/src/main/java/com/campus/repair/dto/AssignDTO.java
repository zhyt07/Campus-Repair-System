package com.campus.repair.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 派单请求
 */
@Data
public class AssignDTO {

    @NotNull(message = "工单ID不能为空")
    private Long orderId;

    /** 维修人员ID，为空则使用智能派单 */
    private Long repairerId;
}
