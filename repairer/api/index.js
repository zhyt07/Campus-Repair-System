import request from '../utils/request'

// ==================== 认证模块 ====================
export const authApi = {
  login(data) {
    return request.post('/auth/repairer/login', data)
  },
  getRepairerInfo() {
    return request.get('/auth/repairer/info')
  }
}

// ==================== 工单模块 ====================
export const orderApi = {
  // 获取工单大厅列表
  getHallOrders(params) {
    return request.get('/repair-order/hall', params)
  },
  // 获取工单详情
  getDetail(id) {
    return request.get(`/repair-order/${id}`)
  },
  // 确认接单
  acceptOrder(id) {
    return request.put(`/repair-order/${id}/accept`)
  },
  // 完成维修
  completeOrder(data) {
    return request.put(`/repair-order/${data.orderId}/complete`, data)
  },
  // 获取我的工单列表
  getMyOrders(params) {
    return request.get('/repair-order/my-repairer', params)
  }
}

// ==================== 评价模块 ====================
export const evaluationApi = {
  // 获取评价列表（我的被评价记录）
  getMyEvaluations(params) {
    return request.get('/evaluation/repairer/my', params)
  }
}

// ==================== 文件上传 ====================
export const fileApi = {
  uploadImage(filePath) {
    return request.upload('/file/upload/image', filePath)
  }
}

// ==================== 统计模块 ====================
export const statisticsApi = {
  getRepairerStats() {
    return request.get('/statistics/repairer')
  }
}
