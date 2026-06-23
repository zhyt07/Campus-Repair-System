<template>
  <view class="my-orders-page">
    <!-- 标签切换 -->
    <view class="filter-tabs">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentTab === tab.value }"
        @tap="switchTab(tab.value)"
      >
        <text class="tab-text">{{ tab.label }}</text>
        <text v-if="tab.count > 0" class="tab-count">{{ tab.count }}</text>
      </view>
    </view>

    <!-- 列表区域 -->
    <view class="list-area">
      <!-- 空状态 -->
      <view v-if="!loading && orderList.length === 0" class="empty-state">
        <text class="empty-icon">🔧</text>
        <text class="empty-text">{{ currentTab === 'REPAIRING' ? '暂无维修中的工单' : '暂无已完成的工单' }}</text>
        <text v-if="currentTab === 'REPAIRING'" class="empty-btn" @tap="goHall">去工单大厅接单</text>
      </view>

      <!-- 工单卡片 -->
      <view
        v-for="item in orderList"
        :key="item.id"
        class="order-card card"
        :class="{ 'overtime-card': item.overtime }"
        @tap="goDetail(item.id)"
      >
        <!-- 头部 -->
        <view class="order-header">
          <view class="order-left">
            <text class="type-emoji">{{ getTypeIcon(item.repairType) }}</text>
            <view class="order-info">
              <text class="order-type">{{ getTypeText(item.repairType) }}</text>
              <text class="order-no">{{ item.orderNo }}</text>
            </view>
          </view>
          <text :class="['status-tag', getStatusClass(item.status)]">
            {{ getStatusText(item.status) }}
          </text>
        </view>

        <!-- 描述 -->
        <view class="order-body">
          <text class="order-desc text-ellipsis-2">{{ item.description }}</text>
        </view>

        <!-- 底部信息 -->
        <view class="order-footer">
          <text class="order-location">📍 {{ item.location }}</text>
          <text class="order-time">{{ formatTime(item.createTime) }}</text>
        </view>

        <!-- 维修中：显示计时器 -->
        <view v-if="item.status === 'REPAIRING' && item.startTime" class="timer-area" :class="{ 'timer-overtime': item.overtime }">
          <view class="timer-icon-area">
            <text class="timer-icon">{{ item.overtime ? '⚠️' : '⏱️' }}</text>
          </view>
          <view class="timer-info">
            <text class="timer-label">{{ item.overtime ? '已超时' : '维修计时' }}</text>
            <text class="timer-value">{{ item.timerText || '00:00:00' }}</text>
          </view>
          <view v-if="item.overtime" class="overtime-tag">
            <text>超时</text>
          </view>
        </view>

        <!-- 已完成：显示评分 -->
        <view v-if="item.status === 'COMPLETED' || item.status === 'EVALUATED'" class="score-area" v-show="item.score">
          <text class="score-stars">{{ '★'.repeat(Math.floor(item.score || 0)) }}{{ '☆'.repeat(5 - Math.floor(item.score || 0)) }}</text>
          <text class="score-value">{{ item.score }}分</text>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view v-if="hasMore && !loading" class="load-more" @tap="loadMore">
      <text class="load-text">点击加载更多</text>
    </view>
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>
    <view v-if="!hasMore && orderList.length > 0" class="no-more">
      <text class="no-more-text">— 没有更多了 —</text>
    </view>

    <view class="safe-area-bottom"></view>
  </view>
</template>

<script setup>
import { ref, onMounted, onShow, onUnmounted } from 'vue'
import { orderApi } from '../../api/index'
import { getTypeIcon, getTypeText, getStatusText, getStatusClass, formatTime, formatTimer } from '../../utils/index'

const loading = ref(false)
const orderList = ref([])
const currentTab = ref('REPAIRING')
const currentPage = ref(1)
const pageSize = 10
const hasMore = ref(true)
let timerInterval = null

const tabs = [
  { label: '维修中', value: 'REPAIRING', count: 0 },
  { label: '已完成', value: 'COMPLETED', count: 0 }
]

// 切换标签
const switchTab = (value) => {
  if (currentTab.value === value) return
  currentTab.value = value
  currentPage.value = 1
  orderList.value = []
  hasMore.value = true
  loadOrders()
}

// 加载列表
const loadOrders = async () => {
  if (loading.value) return
  loading.value = true
  
  try {
    const params = {
      page: currentPage.value,
      size: pageSize,
      status: currentTab.value
    }
    // 如果是已完成标签，同时查已完成和已评价
    if (currentTab.value === 'COMPLETED') {
      params.status = 'COMPLETED,EVALUATED'
    }
    
    const res = await orderApi.getMyOrders(params)
    const records = (res.records || []).map(item => ({
      ...item,
      overtime: checkOvertime(item),
      timerText: item.status === 'REPAIRING' && item.startTime 
        ? calcTimerText(item.startTime) 
        : ''
    }))
    
    if (currentPage.value === 1) {
      orderList.value = records
    } else {
      orderList.value = [...orderList.value, ...records]
    }
    
    hasMore.value = records.length >= pageSize
  } catch (e) {
    console.error('加载我的工单失败:', e)
  } finally {
    loading.value = false
  }
}

// 检查是否超时（超过4小时）
const checkOvertime = (item) => {
  if (item.status !== 'REPAIRING' || !item.startTime) return false
  const start = new Date(item.startTime).getTime()
  const now = Date.now()
  return (now - start) > 4 * 60 * 60 * 1000
}

// 计算计时器文字
const calcTimerText = (startTime) => {
  const start = new Date(startTime).getTime()
  const now = Date.now()
  const seconds = Math.floor((now - start) / 1000)
  return formatTimer(seconds)
}

// 定时更新计时器
const startTimer = () => {
  stopTimer()
  timerInterval = setInterval(() => {
    orderList.value = orderList.value.map(item => {
      if (item.status === 'REPAIRING' && item.startTime) {
        return {
          ...item,
          overtime: checkOvertime(item),
          timerText: calcTimerText(item.startTime)
        }
      }
      return item
    })
  }, 1000)
}

const stopTimer = () => {
  if (timerInterval) {
    clearInterval(timerInterval)
    timerInterval = null
  }
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loading.value) return
  currentPage.value++
  loadOrders()
}

// 跳转详情
const goDetail = (id) => {
  uni.navigateTo({ url: `/pages/order-detail/order-detail?id=${id}` })
}

// 跳转大厅
const goHall = () => {
  uni.switchTab({ url: '/pages/hall/hall' })
}

onMounted(() => {
  loadOrders()
  startTimer()
})

onShow(() => {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    currentPage.value = 1
    orderList.value = []
    hasMore.value = true
    loadOrders()
  }
})

onUnmounted(() => {
  stopTimer()
})
</script>

<style lang="scss" scoped>
.my-orders-page {
  min-height: 100vh;
  background: #F5F5F5;
}

/* 标签切换 */
.filter-tabs {
  display: flex;
  background: #FFFFFF;
  padding: 16rpx 30rpx;
  position: sticky;
  top: 0;
  z-index: 10;
  gap: 16rpx;
}

.tab-item {
  display: flex;
  align-items: center;
  padding: 12rpx 28rpx;
  border-radius: 30rpx;
  background: #F5F5F5;
  transition: all 0.2s;

  &.active {
    background: #E6F4FF;
    
    .tab-text {
      color: #1677FF;
      font-weight: 600;
    }
  }
}

.tab-text { font-size: 26rpx; color: #595959; }
.tab-count {
  background: #1677FF; color: #FFFFFF; font-size: 20rpx;
  padding: 2rpx 10rpx; border-radius: 16rpx; margin-left: 8rpx;
  min-width: 32rpx; text-align: center;
}

/* 列表 */
.list-area { padding: 20rpx 30rpx; }

.order-card {
  padding: 24rpx; margin-bottom: 20rpx; position: relative;
}

.overtime-card {
  border-left: 6rpx solid #FF4D4F;
}

.order-header {
  display: flex; justify-content: space-between; align-items: flex-start;
  margin-bottom: 12rpx;
}

.order-left { display: flex; align-items: flex-start; flex: 1; }
.type-emoji { font-size: 36rpx; margin-right: 12rpx; margin-top: 2rpx; }
.order-info { display: flex; flex-direction: column; }
.order-type { font-size: 28rpx; font-weight: 500; color: #262626; margin-bottom: 4rpx; }
.order-no { font-size: 22rpx; color: #8C8C8C; }

.order-body { margin-bottom: 12rpx; }
.order-desc { font-size: 26rpx; color: #595959; line-height: 1.6; }

.order-footer {
  display: flex; justify-content: space-between; align-items: center;
}
.order-location { font-size: 22rpx; color: #8C8C8C; }
.order-time { font-size: 22rpx; color: #BFBFBF; }

/* 计时器区域 */
.timer-area {
  display: flex; align-items: center;
  margin-top: 16rpx; padding-top: 16rpx;
  border-top: 1rpx solid #F0F0F0;
  background: #F6FFED; border-radius: 8rpx; padding: 16rpx;
}

.timer-overtime {
  background: #FFF1F0;
}

.timer-icon-area { margin-right: 12rpx; }
.timer-icon { font-size: 36rpx; }
.timer-info { flex: 1; display: flex; flex-direction: column; }
.timer-label { font-size: 22rpx; color: #8C8C8C; }
.timer-value {
  font-size: 36rpx; font-weight: 700; color: #1677FF;
  font-family: 'Courier New', monospace;
}
.timer-overtime .timer-value { color: #FF4D4F; }

.overtime-tag {
  background: #FF4D4F; color: #FFFFFF; font-size: 20rpx;
  padding: 4rpx 16rpx; border-radius: 20rpx; font-weight: 600;
}

/* 评分区域 */
.score-area {
  display: flex; align-items: center;
  margin-top: 16rpx; padding-top: 16rpx;
  border-top: 1rpx solid #F0F0F0;
}

.score-stars {
  font-size: 28rpx; color: #FAAD14; letter-spacing: 4rpx; margin-right: 12rpx;
}

.score-value {
  font-size: 24rpx; color: #8C8C8C;
}

/* 加载状态 */
.load-more, .loading-state, .no-more { display: flex; justify-content: center; padding: 30rpx 0; }
.load-text { font-size: 26rpx; color: #1677FF; }
.loading-text { font-size: 26rpx; color: #8C8C8C; }
.no-more-text { font-size: 24rpx; color: #BFBFBF; }

/* 空状态 */
.empty-state {
  display: flex; flex-direction: column; align-items: center; padding: 120rpx 0;
}
.empty-icon { font-size: 80rpx; margin-bottom: 20rpx; }
.empty-text { font-size: 28rpx; color: #8C8C8C; margin-bottom: 24rpx; }
.empty-btn {
  font-size: 26rpx; color: #1677FF; border: 1rpx solid #1677FF;
  border-radius: 30rpx; padding: 12rpx 40rpx;
}

.safe-area-bottom { height: 40rpx; }
</style>
