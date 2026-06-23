package com.campus.repair.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private Long total;

    /** 当前页 */
    private Long current;

    /** 每页大小 */
    private Long size;

    /** 数据列表 */
    private List<T> records;

    public static <T> PageResult<T> of(Long total, Long current, Long size, List<T> records) {
        PageResult<T> r = new PageResult<>();
        r.total = total;
        r.current = current;
        r.size = size;
        r.records = records;
        return r;
    }
}
