package com.campus.repair.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 报修申请请求
 */
@Data
public class RepairOrderDTO {

    /** 报修类型：水电,木工,空调,网络,门窗,其他 */
    @NotBlank(message = "报修类型不能为空")
    private String repairType;

    /** 故障描述 */
    @NotBlank(message = "故障描述不能为空")
    @Size(min = 5, max = 500, message = "描述长度5-500字")
    private String description;

    /** 报修位置（楼栋+宿舍号） */
    @NotBlank(message = "报修位置不能为空")
    private String location;

    /** 故障图片URL，逗号分隔 */
    private String images;
}
