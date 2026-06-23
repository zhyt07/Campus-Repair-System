<template>
  <view class="order-list-page">
    <!-- 状态筛选标签 -->
    <view class="filter-tabs">
      <scroll-view scroll-x class="tabs-scroll">
        <view class="tabs-row">
          <view
            v-for="tab in statusTabs"
            :key="tab.value"
            class="tab-item"
            :class="{ active: currentStatus === tab.value }"
            @tap="switchTab(tab.value)"
          >
            <text class="tab-text">{{ tab.label }}</text>
            <text v-if="currentStatus === tab.value" class="tab-indicator"></text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 列表区域 -->
    <view class="list-area">
      <!-- 空状态 -->
      <view v-if="!loading && orderList.length === 0" class="empty-state">
        <text class="empty-icon">📭</text>
        <text class="empty-text">{{ currentStatus ? '暂无该状态的报修' : '暂无报修记录' }}</text>
        <text class="empty-btn" @tap="goApply">去报修</text>
      </view>

      <!-- 报修卡片 -->
      <view
        v-for="item in orderList"
        :key="item.id"
        class="order-card card"
        @tap="goDetail(item.id)"
      >
        <!-- 头部 -->
        <view class="order-header">
          <view class="order-left">
            <text class="type-emoji">{{ getTypeIcon(item.repairType) }}</text>
            <view class="order-info">
              <text class="order-no">报修编号：{{ item.orderNo }}</text>
              <text class="order-type">{{ getTypeText(item.repairType) }}</text>
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

        <!-- 照片缩略图（如有） -->
        <view v-if="item.images" class="order-images">
          <image
            v-for="(img, i) in getImageList(item.images).slice(0, 3)"
            :key="i"
            class="order-thumb"
            :src="img"
            mode="aspectFill"
          />
          <text v-if="getImageList(item.images).length > 3" class="more-images">
            +{{ getImageList(item.images).length - 3 }}
          </text>
        </view>

        <!-- 底部信息 -->
        <view class="order-footer">
          <text class="order-location">📍 {{ item.location }}</text>
          <text class="order-time">{{ relativeTime(item.createTime) }}</text>
        </view>

        <!-- 操作按钮 -->
        <view class="order-actions" v-if="showActions(item.status)">
          <text
            v-if="item.status === 'PENDING'"
            class="action-btn cancel-btn"
            @tap.stop="handleCancel(item)"
          >取消报修</text>
          <text
            v-if="item.status === 'COMPLETED'"
            class="action-btn eval-btn"
            @tap.stop="goEvaluation(item.id)"
          >去评价</text>
          <text
            v-if="item.status === 'ASSIGNED' || item.status === 'REPAIRING'"
            class="action-btn urge-btn"
            @tap.stop="handleUrge(item)"
          >催办</text>
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
  getTypeIcon, getTypeText, getStatusText, getStatusClass, relativeTime
} from '../../utils/index'

const loading = ref(false)
const orderList = ref([])
const currentStatus = ref('')
const currentPage = ref(1)
const pageSize = 10
const hasMore = ref(true)

// 状态筛选标签
const statusTabs = [
  { label: '全部', value: '' },
  { label: '待派单', value: 'PENDING' },
  { label: '维修中', value: 'REPAIRING' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已评价', value: 'EVALUATED' }
]

// 切换标签
const switchTab = (value) => {
  if (currentStatus.value === value) return
  currentStatus.value = value
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
    if (currentStatus.value) {
      params.status = currentStatus.value
    }
    
    const res = await orderApi.getMyOrders(params)
    const records = res.records || []
    
    if (currentPage.value === 1) {
      orderList.value = records
    } else {
      orderList.value = [...orderList.value, ...records]
    }
    
    hasMore.value = records.length >= pageSize
  } catch (e) {
    console.error('加载列表失败:', e)
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

// 获取图片列表
const getImageList = (images) => {
  if (!images) return []
  if (typeof images === 'string') {
    return images.split(',').filter(Boolean)
  }
  return images
}

// 是否显示操作按钮
const showActions = (status) => {
  return ['PENDING', 'ASSIGNED', 'REPAIRING', 'COMPLETED'].includes(status)
}

// 取消报修
const handleCancel = (item) => {
  uni.showModal({
    title: '取消报修',
    content: '确定要取消该报修申请吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await orderApi.cancel(item.id)
          uni.showToast({ title: '已取消', icon: 'success' })
          // 刷新列表
          currentPage.value = 1
          orderList.value = []
          hasMore.value = true
          loadOrders()
        } catch (e) {
          console.error('取消失败:', e)
        }
      }
    }
  })
}

// 催办
const handleUrge = (item) => {
  uni.showModal({
    title: '催办提醒',
    content: '确定要催促维修人员尽快处理吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await orderApi.urge(item.id)
          uni.showToast({ title: '已催办', icon: 'success' })
        } catch (e) {
          console.error('催办失败:', e)
        }
      }
    }
  })
}

// 跳转详情
const goDetail = (id) => {
  uni.navigateTo({ url: `/pages/order-detail/order-detail?id=${id}` })
}

// 跳转评价
const goEvaluation = (orderId) => {
  uni.navigateTo({ url: `/pages/evaluation/evaluation?orderId=${orderId}` })
}

// 跳转申请
const goApply = () => {
  uni.navigateTo({ url: '/pages/apply/apply' })
}

onMounted(() => {
  loadOrders()
})

// 页面显示时刷新（下拉刷新由 pages.json 配置）
onShow(() => {
  // 如果从详情页返回，刷新列表
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
.order-list-page {
  min-height: 100vh;
  background: #F5F5F5;
}

/* 筛选标签 */
.filter-tabs {
  background: #FFFFFF;
  padding: 16rpx 0;
  position: sticky;
  top: 0;
  z-index: 10;
}

.tabs-scroll {
  white-space: nowrap;
}

.tabs-row {
  display: flex;
  padding: 0 30rpx;
}

.tab-item {
  position: relative;
  padding: 12rpx 28rpx;
  margin-right: 8rpx;
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

/* 列表 */
.list-area {
  padding: 20rpx 30rpx;
}

.order-card {
  padding: 24rpx;
  margin-bottom: 20rpx;
  position: relative;
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

.order-no {
  font-size: 22rpx;
  color: #8C8C8C;
  margin-bottom: 4rpx;
}

.order-type {
  font-size: 28rpx;
  font-weight: 500;
  color: #262626;
}

.order-body {
  margin-bottom: 12rpx;
}

.order-desc {
  font-size: 26rpx;
  color: #595959;
  line-height: 1.6;
}

/* 照片缩略图 */
.order-images {
  display: flex;
  margin-bottom: 12rpx;
}

.order-thumb {
  width: 120rpx;
  height: 120rpx;
  border-radius: 8rpx;
  margin-right: 12rpx;
}

.more-images {
  width: 120rpx;
  height: 120rpx;
  border-radius: 8rpx;
  background: rgba(0, 0, 0, 0.4);
  color: #FFFFFF;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
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

/* 操作按钮 */
.order-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #F0F0F0;
}

.action-btn {
  font-size: 24rpx;
  padding: 8rpx 24rpx;
  border-radius: 24rpx;
  margin-left: 16rpx;
}

.eval-btn {
  color: #1677FF;
  border: 1rpx solid #1677FF;
  background: #E6F4FF;
}

.cancel-btn {
  color: #8C8C8C;
  border: 1rpx solid #D9D9D9;
}

.urge-btn {
  color: #FAAD14;
  border: 1rpx solid #FAAD14;
  background: #FFF7E6;
}

/* 加载状态 */
.load-more, .loading-state, .no-more {
  display: flex;
  justify-content: center;
  padding: 30rpx 0;
}

.load-text {
  font-size: 26rpx;
  color: #1677FF;
}

.loading-text {
  font-size: 26rpx;
  color: #8C8C8C;
}

.no-more-text {
  font-size: 24rpx;
  color: #BFBFBF;
}

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
  margin-bottom: 24rpx;
}

.empty-btn {
  font-size: 26rpx;
  color: #1677FF;
  border: 1rpx solid #1677FF;
  border-radius: 30rpx;
  padding: 12rpx 40rpx;
}

.safe-area-bottom {
  height: 40rpx;
}
</style>
