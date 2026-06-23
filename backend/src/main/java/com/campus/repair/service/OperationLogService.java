package com.campus.repair.service;

import com.campus.repair.entity.OperationLog;

public interface OperationLogService {

    /**
     * 记录操作日志
     */
    void log(Long operatorId, String operatorName, String operatorRole,
             String operationType, String description, Long orderId, String ip);
}
