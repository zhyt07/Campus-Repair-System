<template>
  <view class="home-page">
    <!-- 顶部用户信息栏 -->
    <view class="header-bar">
      <view class="user-info">
        <view class="avatar">
          <text class="avatar-text">{{ userName.charAt(0) }}</text>
        </view>
        <view class="user-text">
          <text class="greeting">{{ greeting }}，{{ userName || '同学' }}</text>
          <text class="student-id">学号：{{ studentId }}</text>
        </view>
      </view>
    </view>

    <!-- 公告轮播 -->
    <view class="notice-bar" v-if="notices.length > 0">
      <swiper
        class="notice-swiper"
        autoplay
        :interval="3000"
        circular
        vertical
      >
        <swiper-item v-for="(item, index) in notices" :key="index">
          <view class="notice-item" @tap="viewNotice(item)">
            <text class="notice-icon">📢</text>
            <text class="notice-text text-ellipsis">{{ item.title }}</text>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <!-- 功能入口 -->
    <view class="function-grid">
      <view class="func-item" @tap="goApply">
        <view class="func-icon apply-icon">
          <text class="icon-emoji">📝</text>
        </view>
        <text class="func-name">我要报修</text>
        <text class="func-desc">快速提交报修申请</text>
      </view>
      <view class="func-item" @tap="goMyOrders">
        <view class="func-icon order-icon">
          <text class="icon-emoji">📋</text>
        </view>
        <text class="func-name">我的报修</text>
        <text class="func-desc">查看报修进度</text>
      </view>
      <view class="func-item" @tap="goMyOrders">
        <view class="func-icon eval-icon">
          <text class="icon-emoji">⭐</text>
        </view>
        <text class="func-name">服务评价</text>
        <text class="func-desc">评价维修服务</text>
      </view>
      <view class="func-item" @tap="callSupport">
        <view class="func-icon contact-icon">
          <text class="icon-emoji">📞</text>
        </view>
        <text class="func-name">联系后勤</text>
        <text class="func-desc">紧急情况联系</text>
      </view>
    </view>

    <!-- 统计数据 -->
    <view class="stats-row" v-if="stats">
      <view class="stat-item">
        <text class="stat-num">{{ stats.totalOrders || 0 }}</text>
        <text class="stat-label">总报修</text>
      </view>
      <view class="stat-item">
        <text class="stat-num success">{{ stats.completedOrders || 0 }}</text>
        <text class="stat-label">已完成</text>
      </view>
      <view class="stat-item">
        <text class="stat-num warning">{{ stats.pendingEval || 0 }}</text>
        <text class="stat-label">待评价</text>
      </view>
    </view>

    <!-- 最近报修 -->
    <view class="section-header">
      <text class="section-title">最近报修</text>
      <text class="section-more" @tap="goMyOrders">全部 ›</text>
    </view>

    <view v-if="recentOrders.length === 0" class="empty-state">
      <text class="empty-icon">📭</text>
      <text class="empty-text">暂无报修记录</text>
      <text class="empty-btn" @tap="goApply">立即报修</text>
    </view>

    <view v-else class="order-list">
      <view
        class="order-card card"
        v-for="item in recentOrders"
        :key="item.id"
        @tap="goDetail(item.id)"
      >
        <view class="order-header">
          <view class="order-type">
            <text class="type-icon">{{ getTypeIcon(item.repairType) }}</text>
            <text class="type-name">{{ getTypeText(item.repairType) }}</text>
          </view>
          <text :class="['status-tag', getStatusClass(item.status)]">
            {{ getStatusText(item.status) }}
          </text>
        </view>
        <view class="order-body">
          <text class="order-desc text-ellipsis-2">{{ item.description }}</text>
        </view>
        <view class="order-footer">
          <text class="order-location">📍 {{ item.location }}</text>
          <text class="order-time">{{ relativeTime(item.createTime) }}</text>
        </view>
      </view>
    </view>

    <!-- 底部安全区 -->
    <view class="safe-area-bottom"></view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, onShow } from 'vue'
import { useUserStore } from '../../store/user'
import { orderApi, announcementApi, statisticsApi } from '../../api/index'
import {
  getTypeIcon, getTypeText, getStatusText, getStatusClass, relativeTime
} from '../../utils/index'

const userStore = useUserStore()
const userName = computed(() => userStore.userName)
const studentId = computed(() => userStore.studentId)

const notices = ref([])
const stats = ref(null)
const recentOrders = ref([])

// 问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// 加载公告
const loadNotices = async () => {
  try {
    const res = await announcementApi.getList({ page: 1, size: 5 })
    notices.value = res.records || []
  } catch (e) {
    console.error('加载公告失败:', e)
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await statisticsApi.getStudentStats()
    stats.value = res
  } catch (e) {
    console.error('加载统计失败:', e)
  }
}

// 加载最近报修
const loadRecentOrders = async () => {
  try {
    const res = await orderApi.getMyOrders({ page: 1, size: 5 })
    recentOrders.value = res.records || []
  } catch (e) {
    console.error('加载最近报修失败:', e)
  }
}

// 查看公告详情
const viewNotice = (item) => {
  uni.showModal({
    title: item.title,
    content: item.content,
    showCancel: false,
    confirmText: '我知道了'
  })
}

// 跳转报修申请
const goApply = () => {
  uni.navigateTo({ url: '/pages/apply/apply' })
}

// 跳转我的报修
const goMyOrders = () => {
  uni.switchTab({ url: '/pages/order-list/order-list' })
}

// 跳转详情
const goDetail = (id) => {
  uni.navigateTo({ url: `/pages/order-detail/order-detail?id=${id}` })
}

// 联系后勤
const callSupport = () => {
  uni.showActionSheet({
    itemList: ['拨打后勤热线', '在线咨询'],
    success: (res) => {
      if (res.tapIndex === 0) {
        uni.makePhoneCall({ phoneNumber: '10086' })
      } else {
        uni.showToast({ title: '功能开发中', icon: 'none' })
      }
    }
  })
}

onMounted(() => {
  loadNotices()
  loadStats()
  loadRecentOrders()
})

// 下拉刷新
onShow(() => {
  loadRecentOrders()
})
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: #F5F5F5;
}

/* 顶部栏 */
.header-bar {
  background: linear-gradient(135deg, #1677FF 0%, #0958D9 100%);
  padding: 30rpx 30rpx 40rpx;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  border: 3rpx solid rgba(255, 255, 255, 0.4);
}

.avatar-text {
  font-size: 36rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.user-text {
  display: flex;
  flex-direction: column;
}

.greeting {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFFFFF;
  margin-bottom: 4rpx;
}

.student-id {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
}

/* 公告栏 */
.notice-bar {
  margin: -20rpx 30rpx 20rpx;
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 16rpx 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 1;
}

.notice-swiper {
  height: 40rpx;
}

.notice-item {
  display: flex;
  align-items: center;
  height: 40rpx;
}

.notice-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
}

.notice-text {
  font-size: 26rpx;
  color: #262626;
  flex: 1;
}

/* 功能入口 */
.function-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  padding: 0 30rpx;
  margin-bottom: 20rpx;
}

.func-item {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 30rpx 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.func-item:active {
  background: #F5F5F5;
}

.func-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16rpx;
}

.apply-icon { background: linear-gradient(135deg, #E6F4FF, #BAE0FF); }
.order-icon { background: linear-gradient(135deg, #F6FFED, #D9F7BE); }
.eval-icon { background: linear-gradient(135deg, #FFF7E6, #FFE58F); }
.contact-icon { background: linear-gradient(135deg, #FFF1F0, #FFCCC7); }

.icon-emoji {
  font-size: 40rpx;
}

.func-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #262626;
  display: block;
  margin-bottom: 4rpx;
}

.func-desc {
  font-size: 22rpx;
  color: #8C8C8C;
}

/* 统计行 */
.stats-row {
  display: flex;
  margin: 0 30rpx 20rpx;
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 20rpx 0;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-right: 1rpx solid #F0F0F0;

  &:last-child {
    border-right: none;
  }
}

.stat-num {
  font-size: 40rpx;
  font-weight: 700;
  color: #1677FF;
  margin-bottom: 4rpx;

  &.success { color: #52C41A; }
  &.warning { color: #FAAD14; }
}

.stat-label {
  font-size: 22rpx;
  color: #8C8C8C;
}

/* 分区标题 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx 16rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #262626;
}

.section-more {
  font-size: 26rpx;
  color: #1677FF;
}

/* 报修列表 */
.order-list {
  padding: 0 30rpx;
}

.order-card {
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.order-type {
  display: flex;
  align-items: center;
}

.type-icon {
  font-size: 32rpx;
  margin-right: 8rpx;
}

.type-name {
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

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 0;
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

/* 安全区 */
.safe-area-bottom {
  height: 40rpx;
}
</style>
