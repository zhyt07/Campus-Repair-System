<template>
  <view class="mine-page">
    <!-- 个人信息头部 -->
    <view class="profile-header">
      <view class="profile-bg"></view>
      <view class="profile-content">
        <view class="avatar-area">
          <view class="avatar">
            <text class="avatar-text">{{ userName.charAt(0) }}</text>
          </view>
          <view class="profile-info">
            <text class="profile-name">{{ userName || '未登录' }}</text>
            <text class="profile-id">学号：{{ studentId }}</text>
            <text class="profile-class" v-if="userClass">{{ userClass }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 统计数据 -->
    <view class="stats-card card">
      <view class="stat-item">
        <text class="stat-num">{{ stats.totalOrders || 0 }}</text>
        <text class="stat-label">总报修</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-num success">{{ stats.completedOrders || 0 }}</text>
        <text class="stat-label">已完成</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-num warning">{{ stats.pendingEval || 0 }}</text>
        <text class="stat-label">待评价</text>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <!-- 我的报修 -->
      <view class="menu-card card">
        <view class="menu-group">
          <view class="menu-item" @tap="goMyOrders">
            <view class="menu-left">
              <view class="menu-icon orders-icon">
                <text class="icon-emoji">📋</text>
              </view>
              <text class="menu-text">我的报修</text>
            </view>
            <view class="menu-right">
              <text class="menu-badge" v-if="stats.totalOrders">{{ stats.totalOrders }}</text>
              <text class="menu-arrow">›</text>
            </view>
          </view>

          <view class="menu-divider"></view>

          <view class="menu-item" @tap="goPendingEval">
            <view class="menu-left">
              <view class="menu-icon eval-icon">
                <text class="icon-emoji">⭐</text>
              </view>
              <text class="menu-text">待评价</text>
            </view>
            <view class="menu-right">
              <text class="menu-badge warning" v-if="stats.pendingEval">{{ stats.pendingEval }}</text>
              <text class="menu-arrow">›</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 其他功能 -->
      <view class="menu-card card">
        <view class="menu-group">
          <view class="menu-item" @tap="goNotice">
            <view class="menu-left">
              <view class="menu-icon notice-icon">
                <text class="icon-emoji">🔔</text>
              </view>
              <text class="menu-text">消息通知</text>
            </view>
            <view class="menu-right">
              <text class="menu-arrow">›</text>
            </view>
          </view>

          <view class="menu-divider"></view>

          <view class="menu-item" @tap="callSupport">
            <view class="menu-left">
              <view class="menu-icon contact-icon">
                <text class="icon-emoji">📞</text>
              </view>
              <text class="menu-text">联系后勤</text>
            </view>
            <view class="menu-right">
              <text class="menu-arrow">›</text>
            </view>
          </view>

          <view class="menu-divider"></view>

          <view class="menu-item" @tap="showAbout">
            <view class="menu-left">
              <view class="menu-icon about-icon">
                <text class="icon-emoji">ℹ️</text>
              </view>
              <text class="menu-text">关于系统</text>
            </view>
            <view class="menu-right">
              <text class="menu-version">v1.0.0</text>
              <text class="menu-arrow">›</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-area">
      <button class="logout-btn" @tap="handleLogout">退出登录</button>
    </view>

    <view class="safe-area-bottom"></view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../store/user'
import { statisticsApi } from '../../api/index'

const userStore = useUserStore()
const userName = computed(() => userStore.userName)
const studentId = computed(() => userStore.studentId)
const userClass = computed(() => userStore.userInfo?.className || '')

const stats = ref({
  totalOrders: 0,
  completedOrders: 0,
  pendingEval: 0
})

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await statisticsApi.getStudentStats()
    if (res) {
      stats.value = res
    }
  } catch (e) {
    console.error('加载统计失败:', e)
  }
}

// 跳转我的报修
const goMyOrders = () => {
  uni.switchTab({ url: '/pages/order-list/order-list' })
}

// 跳转待评价
const goPendingEval = () => {
  uni.switchTab({ url: '/pages/order-list/order-list' })
}

// 消息通知
const goNotice = () => {
  uni.showToast({ title: '暂无新消息', icon: 'none' })
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

// 关于
const showAbout = () => {
  uni.showModal({
    title: '关于系统',
    content: '校园宿舍智能报修与服务评价系统 v1.0.0\n\n湖南工业大学\n数字媒体技术专业\n\n提供便捷高效的宿舍报修服务',
    showCancel: false,
    confirmText: '我知道了'
  })
}

// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: '退出登录',
    content: '确定要退出当前账号吗？',
    confirmColor: '#FF4D4F',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
      }
    }
  })
}

onMounted(() => {
  loadStats()
})
</script>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  background: #F5F5F5;
}

/* 个人信息头部 */
.profile-header {
  position: relative;
}

.profile-bg {
  height: 280rpx;
  background: linear-gradient(135deg, #1677FF 0%, #0958D9 100%);
}

.profile-content {
  position: absolute;
  bottom: -60rpx;
  left: 30rpx;
  right: 30rpx;
}

.avatar-area {
  display: flex;
  align-items: center;
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
}

.avatar {
  width: 96rpx;
  height: 96rpx;
  background: linear-gradient(135deg, #1677FF 0%, #0958D9 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  border: 4rpx solid #FFFFFF;
  box-shadow: 0 4rpx 12rpx rgba(22, 119, 255, 0.3);
}

.avatar-text {
  font-size: 44rpx;
  font-weight: 700;
  color: #FFFFFF;
}

.profile-info {
  display: flex;
  flex-direction: column;
}

.profile-name {
  font-size: 34rpx;
  font-weight: 600;
  color: #262626;
  margin-bottom: 4rpx;
}

.profile-id {
  font-size: 24rpx;
  color: #8C8C8C;
  margin-bottom: 2rpx;
}

.profile-class {
  font-size: 24rpx;
  color: #BFBFBF;
}

/* 统计卡片 */
.stats-card {
  display: flex;
  align-items: center;
  margin: 80rpx 30rpx 20rpx;
  padding: 30rpx 0;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-num {
  font-size: 44rpx;
  font-weight: 700;
  color: #1677FF;
  margin-bottom: 6rpx;

  &.success { color: #52C41A; }
  &.warning { color: #FAAD14; }
}

.stat-label {
  font-size: 24rpx;
  color: #8C8C8C;
}

.stat-divider {
  width: 1rpx;
  height: 50rpx;
  background: #F0F0F0;
}

/* 菜单 */
.menu-section {
  padding: 0 30rpx;
}

.menu-card {
  padding: 0;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.menu-group {
  .menu-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 28rpx 24rpx;
  }

  .menu-item:active {
    background: #F5F5F5;
  }

  .menu-divider {
    height: 1rpx;
    background: #F0F0F0;
    margin: 0 24rpx;
  }
}

.menu-left {
  display: flex;
  align-items: center;
}

.menu-icon {
  width: 52rpx;
  height: 52rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}

.orders-icon { background: linear-gradient(135deg, #E6F4FF, #BAE0FF); }
.eval-icon { background: linear-gradient(135deg, #FFF7E6, #FFE58F); }
.notice-icon { background: linear-gradient(135deg, #FFF1F0, #FFCCC7); }
.contact-icon { background: linear-gradient(135deg, #F6FFED, #D9F7BE); }
.about-icon { background: linear-gradient(135deg, #F9F0FF, #D3ADF7); }

.icon-emoji {
  font-size: 28rpx;
}

.menu-text {
  font-size: 28rpx;
  color: #262626;
}

.menu-right {
  display: flex;
  align-items: center;
}

.menu-badge {
  background: #1677FF;
  color: #FFFFFF;
  font-size: 20rpx;
  padding: 2rpx 12rpx;
  border-radius: 20rpx;
  margin-right: 12rpx;
  min-width: 36rpx;
  text-align: center;

  &.warning {
    background: #FAAD14;
  }
}

.menu-arrow {
  font-size: 36rpx;
  color: #BFBFBF;
  font-weight: bold;
}

.menu-version {
  font-size: 24rpx;
  color: #BFBFBF;
  margin-right: 8rpx;
}

/* 退出登录 */
.logout-area {
  padding: 40rpx 30rpx;
}

.logout-btn {
  width: 100%;
  height: 88rpx;
  background: #FFFFFF;
  color: #FF4D4F;
  font-size: 30rpx;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1rpx solid #FFE7E7;
}

.logout-btn::after {
  border: none;
}

.logout-btn:active {
  background: #FFF1F0;
}

.safe-area-bottom {
  height: 40rpx;
}
</style>
