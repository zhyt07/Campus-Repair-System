// 全局工具函数

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

// 格式化日期（不含时间）
export function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
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
  return formatDate(dateStr)
}

// 获取状态文字
export function getStatusText(status) {
  const map = {
    'PENDING': '待派单',
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

// 手机号脱敏
export function desensitizePhone(phone) {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 防抖
export function debounce(fn, delay = 300) {
  let timer = null
  return function(...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

// 节流
export function throttle(fn, delay = 300) {
  let last = 0
  return function(...args) {
    const now = Date.now()
    if (now - last >= delay) {
      last = now
      fn.apply(this, args)
    }
  }
}

// 预览图片
export function previewImages(urls, current = 0) {
  uni.previewImage({
    urls,
    current: urls[current] || urls[0]
  })
}
