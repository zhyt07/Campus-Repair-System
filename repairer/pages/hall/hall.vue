<template>
  <view class="hall-page">
    <!-- 顶部筛选标签 -->
    <view class="filter-tabs">
      <view
        v-for="tab in statusTabs"
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
        <text class="empty-icon">📋</text>
        <text class="empty-text">{{ currentTab === 'PENDING' ? '暂无待接工单' : '暂无工单' }}</text>
      </view>

      <!-- 工单卡片 -->
      <view
        v-for="item in orderList"
        :key="item.id"
        class="order-card card"
        :class="{ 'urgent-card': isUrgent(item) }"
        @tap="goDetail(item.id)"
      >
        <!-- 紧急标识 -->
        <view v-if="isUrgent(item)" class="urgent-ribbon">
          <text class="urgent-text">紧急</text>
        </view>

        <!-- 头部 -->
        <view class="order-header">
          <view class="order-left">
            <text class="type-emoji">{{ getTypeIcon(item.repairType) }}</text>
            <view class="order-info">
              <view class="order-title-row">
                <text class="order-type">{{ getTypeText(item.repairType) }}</text>
                <text v-if="isNewOrder(item)" class="new-badge">新</text>
              </view>
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

        <!-- 位置和时间 -->
        <view class="order-footer">
          <text class="order-location">📍 {{ item.location }}</text>
          <text class="order-time">{{ relativeTime(item.createTime) }}</text>
        </view>

        <!-- 快速操作按钮 -->
        <view class="order-actions" v-if="item.status === 'ASSIGNED'">
          <button class="accept-btn" @tap.stop="handleAccept(item)">
            确认接单
          </button>
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
import { ref, onMounted, onShow } from 'vue'
import { orderApi } from '../../api/index'
import {
  getTypeIcon, getTypeText, getStatusText, getStatusClass, relativeTime,
  isUrgent, isNewOrder
} from '../../utils/index'

const loading = ref(false)
const orderList = ref([])
const currentTab = ref('')
const currentPage = ref(1)
const pageSize = 10
const hasMore = ref(true)

const statusTabs = [
  { label: '全部', value: '', count: 0 },
  { label: '待接单', value: 'PENDING', count: 0 }
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
      size: pageSize
    }
    if (currentTab.value) {
      params.status = currentTab.value
    }
    
    const res = await orderApi.getHallOrders(params)
    const records = res.records || []
    
    if (currentPage.value === 1) {
      orderList.value = records
    } else {
      orderList.value = [...orderList.value, ...records]
    }
    
    hasMore.value = records.length >= pageSize
  } catch (e) {
    console.error('加载工单大厅失败:', e)
  } finally {
    loading.value = false
  }
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loading.value) return
  currentPage.value++
  loadOrders()
}

// 确认接单
const handleAccept = (item) => {
  uni.showModal({
    title: '确认接单',
    content: '接单后请尽快处理，确定要接受该工单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await orderApi.acceptOrder(item.id)
          uni.showToast({ title: '接单成功', icon: 'success' })
          // 刷新列表
          currentPage.value = 1
          orderList.value = []
          hasMore.value = true
          loadOrders()
        } catch (e) {
          console.error('接单失败:', e)
        }
      }
    }
  })
}

// 跳转详情
const goDetail = (id) => {
  uni.navigateTo({ url: `/pages/order-detail/order-detail?id=${id}` })
}

onMounted(() => {
  loadOrders()
})

// 页面显示时刷新
onShow(() => {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    currentPage.value = 1
    orderList.value = []
    hasMore.value = true
    loadOrders()
  }
})
</script>

<style lang="scss" scoped>
.hall-page {
  min-height: 100vh;
  background: #F5F5F5;
}

/* 筛选标签 */
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

.tab-text {
  font-size: 26rpx;
  color: #595959;
}

.tab-count {
  background: #1677FF;
  color: #FFFFFF;
  font-size: 20rpx;
  padding: 2rpx 10rpx;
  border-radius: 16rpx;
  margin-left: 8rpx;
  min-width: 32rpx;
  text-align: center;
}

/* 列表 */
.list-area {
  padding: 20rpx 30rpx;
}

.order-card {
  padding: 24rpx;
  margin-bottom: 20rpx;
  position: relative;
  overflow: hidden;
}

/* 紧急工单红色边框 */
.urgent-card {
  border: 2rpx solid #FF4D4F;
}

.urgent-ribbon {
  position: absolute;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, #FF4D4F, #CF1322);
  color: #FFFFFF;
  padding: 6rpx 24rpx;
  font-size: 20rpx;
  font-weight: 600;
  border-radius: 0 16rpx 0 12rpx;
}

.urgent-text {
  color: #FFFFFF;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12rpx;
}

.order-left {
  display: flex;
  align-items: flex-start;
  flex: 1;
}

.type-emoji {
  font-size: 36rpx;
  margin-right: 12rpx;
  margin-top: 2rpx;
}

.order-info {
  display: flex;
  flex-direction: column;
}

.order-title-row {
  display: flex;
  align-items: center;
  margin-bottom: 4rpx;
}

.order-type {
  font-size: 28rpx;
  font-weight: 500;
  color: #262626;
}

/* 新工单标记 */
.new-badge {
  background: linear-gradient(135deg, #FF4D4F, #FF7A45);
  color: #FFFFFF;
  font-size: 18rpx;
  font-weight: 600;
  padding: 2rpx 10rpx;
  border-radius: 4rpx;
  margin-left: 8rpx;
}

.order-no {
  font-size: 22rpx;
  color: #8C8C8C;
}

.order-body {
  margin-bottom: 12rpx;
}

.order-desc {
  font-size: 26rpx;
  color: #595959;
  line-height: 1.6;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-location {
  font-size: 22rpx;
  color: #8C8C8C;
}

.order-time {
  font-size: 22rpx;
  color: #BFBFBF;
}

/* 快速接单按钮 */
.order-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #F0F0F0;
}

.accept-btn {
  background: linear-gradient(135deg, #1677FF 0%, #0958D9 100%);
  color: #FFFFFF;
  font-size: 26rpx;
  padding: 12rpx 40rpx;
  border-radius: 30rpx;
  border: none;
  font-weight: 500;
}

.accept-btn::after { border: none; }
.accept-btn:active { opacity: 0.85; }

/* 加载状态 */
.load-more, .loading-state, .no-more {
  display: flex;
  justify-content: center;
  padding: 30rpx 0;
}

.load-text { font-size: 26rpx; color: #1677FF; }
.loading-text { font-size: 26rpx; color: #8C8C8C; }
.no-more-text { font-size: 24rpx; color: #BFBFBF; }

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #8C8C8C;
}

.safe-area-bottom {
  height: 40rpx;
}
</style>
