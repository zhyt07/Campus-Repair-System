// 格式化时间
export function formatTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 获取相对时间
export function relativeTime(dateStr) {
  if (!dateStr) return ''
  const now = Date.now()
  const target = new Date(dateStr).getTime()
  const diff = now - target
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  
  if (diff < minute) return '刚刚'
  if (diff < hour) return `${Math.floor(diff / minute)}分钟前`
  if (diff < day) return `${Math.floor(diff / hour)}小时前`
  if (diff < 30 * day) return `${Math.floor(diff / day)}天前`
  return formatTime(dateStr).split(' ')[0]
}

// 格式化时长（分钟 -> 时:分）
export function formatDuration(minutes) {
  if (!minutes && minutes !== 0) return '--'
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  if (h > 0) return `${h}小时${m}分钟`
  return `${m}分钟`
}

// 格式化计时器显示（秒 -> HH:MM:SS）
export function formatTimer(seconds) {
  if (!seconds && seconds !== 0) return '00:00:00'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  const pad = (n) => String(n).padStart(2, '0')
  return `${pad(h)}:${pad(m)}:${pad(s)}`
}

// 获取状态文字
export function getStatusText(status) {
  const map = {
    'PENDING': '待接单',
    'ASSIGNED': '已派单',
    'REPAIRING': '维修中',
    'COMPLETED': '已完成',
    'EVALUATED': '已评价',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

// 获取状态样式类名
export function getStatusClass(status) {
  const map = {
    'PENDING': 'status-pending',
    'ASSIGNED': 'status-assigned',
    'REPAIRING': 'status-repairing',
    'COMPLETED': 'status-completed',
    'EVALUATED': 'status-evaluated',
    'CANCELLED': 'status-evaluated'
  }
  return map[status] || ''
}

// 获取报修类型图标
export function getTypeIcon(type) {
  const map = {
    'WATER_ELECTRIC': '⚡',
    'FURNITURE': '🪑',
    'WALL': '🧱',
    'DOOR_WINDOW': '🚪',
    'NETWORK': '🌐',
    'AIR_CONDITIONER': '❄️',
    'OTHER': '🔧'
  }
  return map[type] || '🔧'
}

// 获取报修类型文字
export function getTypeText(type) {
  const map = {
    'WATER_ELECTRIC': '水电',
    'FURNITURE': '家具',
    'WALL': '墙面',
    'DOOR_WINDOW': '门窗',
    'NETWORK': '网络',
    'AIR_CONDITIONER': '空调',
    'OTHER': '其他'
  }
  return map[type] || type
}

// 获取紧急程度
export function getUrgencyLevel(order) {
  if (order.urgent) return 'urgent'
  const createTime = new Date(order.createTime).getTime()
  const now = Date.now()
  const hours = (now - createTime) / (1000 * 60 * 60)
  if (hours > 24) return 'urgent'
  if (hours > 4) return 'warning'
  return 'normal'
}

// 是否为紧急工单
export function isUrgent(order) {
  return order.urgent || getUrgencyLevel(order) === 'urgent'
}

// 是否为新工单（5分钟内）
export function isNewOrder(order) {
  const createTime = new Date(order.createTime).getTime()
  const now = Date.now()
  return (now - createTime) < 5 * 60 * 1000
}

// 手机号脱敏
export function desensitizePhone(phone) {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 预览图片
export function previewImages(urls, current = 0) {
  uni.previewImage({ urls, current: urls[current] || urls[0] })
}
