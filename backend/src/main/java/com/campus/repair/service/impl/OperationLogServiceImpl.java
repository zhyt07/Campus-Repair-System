package com.campus.repair.service.impl;

import com.campus.repair.entity.OperationLog;
import com.campus.repair.mapper.OperationLogMapper;
import com.campus.repair.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public void log(Long operatorId, String operatorName, String operatorRole,
                    String operationType, String description, Long orderId, String ip) {
        OperationLog log = new OperationLog();
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperatorRole(operatorRole);
        log.setOperationType(operationType);
        log.setDescription(description);
        log.setOrderId(orderId);
        log.setIp(ip);
        operationLogMapper.insert(log);
    }
}
