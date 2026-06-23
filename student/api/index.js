import request from '../utils/request'

// ==================== 认证模块 ====================
export const authApi = {
  // 学生登录
  login(data) {
    return request.post('/auth/student/login', data)
  },
  // 获取当前用户信息
  getUserInfo() {
    return request.get('/auth/info')
  }
}

// ==================== 报修工单模块 ====================
export const orderApi = {
  // 提交报修
  submit(data) {
    return request.post('/repair-order/submit', data)
  },
  // 获取我的报修列表
  getMyOrders(params) {
    return request.get('/repair-order/my', params)
  },
  // 获取报修详情
  getDetail(id) {
    return request.get(`/repair-order/${id}`)
  },
  // 取消报修
  cancel(id) {
    return request.put(`/repair-order/${id}/cancel`)
  },
  // 催办
  urge(id) {
    return request.post(`/repair-order/${id}/urge`)
  }
}

// ==================== 评价模块 ====================
export const evaluationApi = {
  // 提交评价
  submit(data) {
    return request.post('/evaluation/submit', data)
  },
  // 获取评价详情
  getByOrderId(orderId) {
    return request.get(`/evaluation/order/${orderId}`)
  },
  // 获取我的评价列表
  getMyEvaluations(params) {
    return request.get('/evaluation/my', params)
  }
}

// ==================== 公告模块 ====================
export const announcementApi = {
  // 获取公告列表
  getList(params) {
    return request.get('/announcement/list', params)
  },
  // 获取公告详情
  getDetail(id) {
    return request.get(`/announcement/${id}`)
  }
}

// ==================== 文件上传 ====================
export const fileApi = {
  // 上传图片
  uploadImage(filePath) {
    return request.upload('/file/upload/image', filePath)
  }
}

// ==================== 统计模块 ====================
export const statisticsApi = {
  // 获取学生端统计数据
  getStudentStats() {
    return request.get('/statistics/student')
  }
}
