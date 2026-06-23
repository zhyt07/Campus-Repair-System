package com.campus.repair.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 评价提交请求
 */
@Data
public class EvaluationDTO {

    /** 工单ID */
    @NotNull(message = "工单ID不能为空")
    private Long orderId;

    /** 星级评分（1-5） */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer rating;

    /** 评价内容 */
    private String content;

    /** 标签，逗号分隔 */
    private String tags;
}
