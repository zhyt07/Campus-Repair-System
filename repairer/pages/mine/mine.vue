<template>
  <view class="mine-page">
    <!-- 个人信息头部 -->
    <view class="profile-header">
      <view class="profile-bg"></view>
      <view class="profile-content">
        <view class="avatar-area">
          <view class="avatar">
            <text class="avatar-text">{{ repairerName.charAt(0) }}</text>
          </view>
          <view class="profile-info">
            <text class="profile-name">{{ repairerName || '维修人员' }}</text>
            <text class="profile-no">工号：{{ repairerNo }}</text>
            <text class="profile-group" v-if="groupName">{{ groupName }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 工作统计 -->
    <view class="stats-card card">
      <view class="stat-item">
        <text class="stat-num">{{ stats.monthOrders || 0 }}</text>
        <text class="stat-label">本月完成</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <view class="stat-num-row">
          <text class="stat-num">{{ stats.avgScore || 0 }}</text>
          <text class="stat-unit">分</text>
        </view>
        <text class="stat-label">平均评分</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-num">{{ stats.totalOrders || 0 }}</text>
        <text class="stat-label">总工单</text>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-card card">
        <view class="menu-group">
          <view class="menu-item" @tap="goMyOrders">
            <view class="menu-left">
              <view class="menu-icon orders-icon">
                <text class="icon-emoji">📋</text>
              </view>
              <text class="menu-text">历史工单</text>
            </view>
            <view class="menu-right">
              <text class="menu-arrow">›</text>
            </view>
          </view>

          <view class="menu-divider"></view>

          <view class="menu-item" @tap="goEvaluations">
            <view class="menu-left">
              <view class="menu-icon eval-icon">
                <text class="icon-emoji">⭐</text>
              </view>
              <text class="menu-text">评价查看</text>
            </view>
            <view class="menu-right">
              <text class="menu-badge" v-if="stats.avgScore">{{ stats.avgScore }}分</text>
              <text class="menu-arrow">›</text>
            </view>
          </view>
        </view>
      </view>

      <view class="menu-card card">
        <view class="menu-group">
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
import { useRepairerStore } from '../../store/repairer'
import { statisticsApi } from '../../api/index'

const repairerStore = useRepairerStore()
const repairerName = computed(() => repairerStore.repairerName)
const repairerNo = computed(() => repairerStore.repairerNo)
const groupName = computed(() => repairerStore.repairerInfo?.groupName || '')

const stats = ref({
  monthOrders: 0,
  avgScore: 0,
  totalOrders: 0
})

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await statisticsApi.getRepairerStats()
    if (res) {
      stats.value = {
        monthOrders: res.monthOrders || 0,
        avgScore: res.avgScore ? Number(res.avgScore).toFixed(1) : 0,
        totalOrders: res.totalOrders || 0
      }
    }
  } catch (e) {
    console.error('加载统计失败:', e)
  }
}

// 跳转历史工单（已完成）
const goMyOrders = () => {
  uni.switchTab({ url: '/pages/my-orders/my-orders' })
}

// 跳转评价查看
const goEvaluations = () => {
  uni.showToast({ title: '评价查看功能开发中', icon: 'none' })
}

// 关于
const showAbout = () => {
  uni.showModal({
    title: '关于系统',
    content: '校园宿舍智能报修与服务评价系统 v1.0.0\n\n维修端小程序\n\n湖南工业大学 数字媒体技术专业',
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
        repairerStore.logout()
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
.profile-header { position: relative; }
.profile-bg { height: 280rpx; background: linear-gradient(135deg, #1677FF 0%, #0958D9 100%); }
.profile-content { position: absolute; bottom: -60rpx; left: 30rpx; right: 30rpx; }

.avatar-area {
  display: flex; align-items: center; background: #FFFFFF;
  border-radius: 16rpx; padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
}

.avatar {
  width: 96rpx; height: 96rpx;
  background: linear-gradient(135deg, #1677FF 0%, #0958D9 100%);
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  margin-right: 24rpx; border: 4rpx solid #FFFFFF;
  box-shadow: 0 4rpx 12rpx rgba(22, 119, 255, 0.3);
}

.avatar-text { font-size: 44rpx; font-weight: 700; color: #FFFFFF; }

.profile-info { display: flex; flex-direction: column; }
.profile-name { font-size: 34rpx; font-weight: 600; color: #262626; margin-bottom: 4rpx; }
.profile-no { font-size: 24rpx; color: #8C8C8C; margin-bottom: 2rpx; }
.profile-group { font-size: 24rpx; color: #BFBFBF; }

/* 统计卡片 */
.stats-card {
  display: flex; align-items: center;
  margin: 80rpx 30rpx 20rpx; padding: 30rpx 0;
}

.stat-item { flex: 1; display: flex; flex-direction: column; align-items: center; }
.stat-num { font-size: 44rpx; font-weight: 700; color: #1677FF; margin-bottom: 6rpx; }
.stat-num-row { display: flex; align-items: baseline; }
.stat-unit { font-size: 24rpx; color: #8C8C8C; margin-left: 4rpx; }
.stat-label { font-size: 24rpx; color: #8C8C8C; }
.stat-divider { width: 1rpx; height: 50rpx; background: #F0F0F0; }

/* 菜单 */
.menu-section { padding: 0 30rpx; }

.menu-card { padding: 0; margin-bottom: 20rpx; overflow: hidden; }

.menu-group {
  .menu-item {
    display: flex; justify-content: space-between; align-items: center;
    padding: 28rpx 24rpx;
  }
  .menu-item:active { background: #F5F5F5; }
  .menu-divider { height: 1rpx; background: #F0F0F0; margin: 0 24rpx; }
}

.menu-left { display: flex; align-items: center; }

.menu-icon {
  width: 52rpx; height: 52rpx; border-radius: 12rpx;
  display: flex; align-items: center; justify-content: center;
  margin-right: 20rpx;
}
.orders-icon { background: linear-gradient(135deg, #E6F4FF, #BAE0FF); }
.eval-icon { background: linear-gradient(135deg, #FFF7E6, #FFE58F); }
.about-icon { background: linear-gradient(135deg, #F9F0FF, #D3ADF7); }

.icon-emoji { font-size: 28rpx; }
.menu-text { font-size: 28rpx; color: #262626; }

.menu-right { display: flex; align-items: center; }
.menu-badge {
  background: #1677FF; color: #FFFFFF; font-size: 20rpx;
  padding: 2rpx 12rpx; border-radius: 20rpx; margin-right: 12rpx;
}
.menu-arrow { font-size: 36rpx; color: #BFBFBF; font-weight: bold; }
.menu-version { font-size: 24rpx; color: #BFBFBF; margin-right: 8rpx; }

/* 退出登录 */
.logout-area { padding: 40rpx 30rpx; }
.logout-btn {
  width: 100%; height: 88rpx; background: #FFFFFF; color: #FF4D4F;
  font-size: 30rpx; border-radius: 8rpx; border: 1rpx solid #FFE7E7;
  display: flex; align-items: center; justify-content: center;
}
.logout-btn::after { border: none; }
.logout-btn:active { background: #FFF1F0; }

.safe-area-bottom { height: 40rpx; }
</style>
