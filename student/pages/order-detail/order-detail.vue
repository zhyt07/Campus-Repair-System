<template>
  <view class="detail-page">
    <!-- 加载中 -->
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>

    <template v-else-if="order">
      <!-- 状态横幅 -->
      <view class="status-banner" :class="'banner-' + order.status.toLowerCase()">
        <view class="banner-content">
          <text class="banner-icon">{{ statusIcon }}</text>
          <view class="banner-text">
            <text class="banner-title">{{ getStatusText(order.status) }}</text>
            <text class="banner-desc">{{ statusDesc }}</text>
          </view>
        </view>
      </view>

      <!-- 报修信息卡片 -->
      <view class="card">
        <view class="card-title">
          <text class="title-icon">📋</text>
          <text class="title-text">报修信息</text>
        </view>
        <view class="info-grid">
          <view class="info-item">
            <text class="info-label">报修编号</text>
            <text class="info-value">{{ order.orderNo }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">报修类型</text>
            <text class="info-value">{{ getTypeText(order.repairType) }} {{ getTypeIcon(order.repairType) }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">故障位置</text>
            <text class="info-value">📍 {{ order.location }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">提交时间</text>
            <text class="info-value">{{ formatTime(order.createTime) }}</text>
          </view>
          <view class="info-item" v-if="order.contactPhone">
            <text class="info-label">联系电话</text>
            <text class="info-value">{{ desensitizePhone(order.contactPhone) }}</text>
          </view>
        </view>
      </view>

      <!-- 故障描述 -->
      <view class="card">
        <view class="card-title">
          <text class="title-icon">📝</text>
          <text class="title-text">故障描述</text>
        </view>
        <text class="desc-text">{{ order.description }}</text>
      </view>

      <!-- 现场照片 -->
      <view v-if="images.length > 0" class="card">
        <view class="card-title">
          <text class="title-icon">📷</text>
          <text class="title-text">现场照片</text>
        </view>
        <view class="photo-grid">
          <image
            v-for="(img, index) in images"
            :key="index"
            class="photo-item"
            :src="img"
            mode="aspectFill"
            @tap="previewImage(index)"
          />
        </view>
      </view>

      <!-- 维修人员卡片（已派单后显示） -->
      <view v-if="order.repairer" class="card">
        <view class="card-title">
          <text class="title-icon">👨‍🔧</text>
          <text class="title-text">维修人员</text>
        </view>
        <view class="repairer-info">
          <view class="repairer-avatar">
            <text class="avatar-text">{{ order.repairer.name?.charAt(0) || '维' }}</text>
          </view>
          <view class="repairer-detail">
            <text class="repairer-name">{{ order.repairer.name }}</text>
            <text class="repairer-skills">
              <text v-for="skill in (order.repairer.skills || '').split(',')" :key="skill" class="skill-tag">
                {{ skill }}
              </text>
            </text>
          </view>
          <view class="repairer-phone" @tap="callRepairer">
            <text class="phone-icon">📞</text>
            <text class="phone-text">联系</text>
          </view>
        </view>
      </view>

      <!-- 时间轴 -->
      <view class="card">
        <view class="card-title">
          <text class="title-icon">🕐</text>
          <text class="title-text">进度追踪</text>
        </view>
        <view class="timeline">
          <view
            v-for="(node, index) in timelineNodes"
            :key="index"
            class="timeline-item"
            :class="{
              active: node.active,
              completed: node.completed
            }"
          >
            <!-- 节点 -->
            <view class="timeline-node-area">
              <view class="timeline-dot" :class="{ 'dot-breath': node.active }">
                <text v-if="node.completed" class="dot-icon">✓</text>
              </view>
              <view
                v-if="index < timelineNodes.length - 1"
                class="timeline-line"
                :class="{ 'line-active': node.completed }"
              ></view>
            </view>
            <!-- 内容 -->
            <view class="timeline-content">
              <text class="timeline-title">{{ node.title }}</text>
              <text class="timeline-time" v-if="node.time">{{ node.time }}</text>
              <text class="timeline-desc" v-if="node.desc">{{ node.desc }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部操作 -->
      <view class="bottom-actions" v-if="showBottomAction">
        <button
          v-if="order.status === 'COMPLETED'"
          class="action-btn eval-btn"
          @tap="goEvaluation"
        >去评价</button>
        <button
          v-if="order.status === 'ASSIGNED' || order.status === 'REPAIRING'"
          class="action-btn urge-btn"
          @tap="handleUrge"
        >催办</button>
        <button
          v-if="order.status === 'PENDING'"
          class="action-btn cancel-btn"
          @tap="handleCancel"
        >取消报修</button>
      </view>

      <view class="safe-area-bottom"></view>
    </template>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { orderApi } from '../../api/index'
import {
  getTypeIcon, getTypeText, getStatusText, formatTime, desensitizePhone, previewImages
} from '../../utils/index'

const loading = ref(true)
const order = ref(null)
const orderId = ref('')

// 图片列表
const images = computed(() => {
  if (!order.value?.images) return []
  const imgs = order.value.images
  return typeof imgs === 'string' ? imgs.split(',').filter(Boolean) : imgs
})

// 状态图标
const statusIcon = computed(() => {
  const map = {
    'PENDING': '⏳',
    'ASSIGNED': '📋',
    'REPAIRING': '🔧',
    'COMPLETED': '✅',
    'EVALUATED': '⭐',
    'CANCELLED': '❌'
  }
  return map[order.value?.status] || '📋'
})

// 状态描述
const statusDesc = computed(() => {
  const map = {
    'PENDING': '您的报修已提交，等待管理员派单',
    'ASSIGNED': '已为您分配维修人员，请耐心等待',
    'REPAIRING': '维修人员正在处理中',
    'COMPLETED': '维修已完成，请对服务进行评价',
    'EVALUATED': '感谢您的评价',
    'CANCELLED': '该报修已取消'
  }
  return map[order.value?.status] || ''
})

// 时间轴节点
const timelineNodes = computed(() => {
  if (!order.value) return []
  
  const o = order.value
  const nodes = [
    {
      title: '提交报修',
      time: formatTime(o.createTime),
      desc: '报修申请已提交',
      active: true,
      completed: true
    }
  ]
  
  const statusFlow = [
    { key: 'PENDING', title: '等待派单', time: null, desc: '等待管理员分配维修人员' },
    { key: 'ASSIGNED', title: '已派单', time: formatTime(o.assignTime), desc: o.repairer ? `已分配给${o.repairer.name}` : '已分配维修人员' },
    { key: 'REPAIRING', title: '维修中', time: formatTime(o.startTime), desc: '维修人员正在处理故障' },
    { key: 'COMPLETED', title: '维修完成', time: formatTime(o.completeTime), desc: '维修已完成，请评价服务' },
    { key: 'EVALUATED', title: '已评价', time: formatTime(o.evaluateTime), desc: '感谢您的评价反馈' }
  ]
  
  const statusOrder = ['PENDING', 'ASSIGNED', 'REPAIRING', 'COMPLETED', 'EVALUATED']
  const currentIndex = statusOrder.indexOf(o.status)
  
  for (let i = 0; i < statusFlow.length; i++) {
    const node = statusFlow[i]
    const statusIndex = statusOrder.indexOf(node.key)
    nodes.push({
      ...node,
      active: statusIndex === currentIndex && o.status !== 'CANCELLED',
      completed: statusIndex <= currentIndex && o.status !== 'CANCELLED' && node.key !== 'PENDING'
    })
  }
  
  return nodes
})

// 是否显示底部操作
const showBottomAction = computed(() => {
  const s = order.value?.status
  return ['PENDING', 'ASSIGNED', 'REPAIRING', 'COMPLETED'].includes(s)
})

// 预览图片
const previewImage = (index) => {
  previewImages(images.value, index)
}

// 联系维修人员
const callRepairer = () => {
  const phone = order.value?.repairer?.phone
  if (phone) {
    uni.makePhoneCall({ phoneNumber: phone })
  }
}

// 去评价
const goEvaluation = () => {
  uni.navigateTo({ url: `/pages/evaluation/evaluation?orderId=${orderId.value}` })
}

// 催办
const handleUrge = () => {
  uni.showModal({
    title: '催办提醒',
    content: '确定要催促维修人员尽快处理吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await orderApi.urge(orderId.value)
          uni.showToast({ title: '已催办', icon: 'success' })
        } catch (e) {
          console.error('催办失败:', e)
        }
      }
    }
  })
}

// 取消报修
const handleCancel = () => {
  uni.showModal({
    title: '取消报修',
    content: '确定要取消该报修申请吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await orderApi.cancel(orderId.value)
          uni.showToast({ title: '已取消', icon: 'success' })
          setTimeout(() => uni.navigateBack(), 1000)
        } catch (e) {
          console.error('取消失败:', e)
        }
      }
    }
  })
}

// 加载详情
const loadDetail = async () => {
  loading.value = true
  try {
    const res = await orderApi.getDetail(orderId.value)
    order.value = res
  } catch (e) {
    console.error('加载详情失败:', e)
  } finally {
    loading.value = false
  }
}

onLoad((options) => {
  orderId.value = options.id
  loadDetail()
})
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding: 20rpx 30rpx;
}

/* 状态横幅 */
.status-banner {
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  color: #FFFFFF;
}

.banner-pending { background: linear-gradient(135deg, #FAAD14, #D48806); }
.banner-assigned { background: linear-gradient(135deg, #1677FF, #0958D9); }
.banner-repairing { background: linear-gradient(135deg, #1677FF, #0958D9); }
.banner-completed { background: linear-gradient(135deg, #52C41A, #389E0D); }
.banner-evaluated { background: linear-gradient(135deg, #8C8C8C, #595959); }
.banner-cancelled { background: linear-gradient(135deg, #FF4D4F, #CF1322); }

.banner-content {
  display: flex;
  align-items: center;
}

.banner-icon {
  font-size: 48rpx;
  margin-right: 20rpx;
}

.banner-text {
  display: flex;
  flex-direction: column;
}

.banner-title {
  font-size: 36rpx;
  font-weight: 700;
  margin-bottom: 4rpx;
}

.banner-desc {
  font-size: 24rpx;
  opacity: 0.9;
}

/* 卡片 */
.card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.card-title {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.title-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.title-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #262626;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 22rpx;
  color: #8C8C8C;
  margin-bottom: 6rpx;
}

.info-value {
  font-size: 26rpx;
  color: #262626;
  font-weight: 500;
}

/* 描述 */
.desc-text {
  font-size: 28rpx;
  color: #595959;
  line-height: 1.8;
  white-space: pre-wrap;
}

/* 照片网格 */
.photo-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12rpx;
}

.photo-item {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 8rpx;
  overflow: hidden;
}

/* 维修人员 */
.repairer-info {
  display: flex;
  align-items: center;
}

.repairer-avatar {
  width: 72rpx;
  height: 72rpx;
  background: linear-gradient(135deg, #1677FF, #0958D9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16rpx;
}

.avatar-text {
  color: #FFFFFF;
  font-size: 32rpx;
  font-weight: 600;
}

.repairer-detail {
  flex: 1;
}

.repairer-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #262626;
  display: block;
  margin-bottom: 6rpx;
}

.repairer-skills {
  display: flex;
  flex-wrap: wrap;
}

.skill-tag {
  font-size: 20rpx;
  color: #1677FF;
  background: #E6F4FF;
  padding: 2rpx 12rpx;
  border-radius: 4rpx;
  margin-right: 8rpx;
  margin-bottom: 4rpx;
}

.repairer-phone {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12rpx 20rpx;
  background: #E6F4FF;
  border-radius: 12rpx;
}

.phone-icon {
  font-size: 32rpx;
}

.phone-text {
  font-size: 20rpx;
  color: #1677FF;
  margin-top: 4rpx;
}

/* 时间轴 */
.timeline {
  padding-left: 8rpx;
}

.timeline-item {
  display: flex;
  padding-bottom: 8rpx;

  &:last-child {
    padding-bottom: 0;
  }
}

.timeline-node-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 48rpx;
  margin-right: 20rpx;
}

.timeline-dot {
  width: 28rpx;
  height: 28rpx;
  border-radius: 50%;
  border: 3rpx solid #D9D9D9;
  background: #F5F5F5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.timeline-item.completed .timeline-dot {
  border-color: #52C41A;
  background: #52C41A;
}

.timeline-item.active .timeline-dot {
  border-color: #1677FF;
  background: #1677FF;
  box-shadow: 0 0 0 6rpx rgba(22, 119, 255, 0.2);
}

.dot-breath {
  animation: breath 2s ease-in-out infinite;
}

@keyframes breath {
  0%, 100% { box-shadow: 0 0 0 6rpx rgba(22, 119, 255, 0.2); }
  50% { box-shadow: 0 0 0 14rpx rgba(22, 119, 255, 0.1); }
}

.dot-icon {
  color: #FFFFFF;
  font-size: 18rpx;
  font-weight: bold;
}

.timeline-line {
  width: 2rpx;
  flex: 1;
  min-height: 36rpx;
  background: #E8E8E8;
  margin: 4rpx 0;
}

.line-active {
  background: #52C41A;
}

.timeline-content {
  flex: 1;
  padding-bottom: 24rpx;
}

.timeline-item:last-child .timeline-content {
  padding-bottom: 0;
}

.timeline-title {
  font-size: 28rpx;
  font-weight: 500;
  color: #8C8C8C;
  display: block;
  margin-bottom: 4rpx;
}

.timeline-item.active .timeline-title {
  color: #1677FF;
  font-weight: 600;
}

.timeline-item.completed .timeline-title {
  color: #262626;
}

.timeline-time {
  font-size: 22rpx;
  color: #BFBFBF;
  display: block;
  margin-bottom: 2rpx;
}

.timeline-desc {
  font-size: 24rpx;
  color: #8C8C8C;
}

/* 底部操作 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #FFFFFF;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: center;
}

.action-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 8rpx;
  font-size: 30rpx;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.action-btn::after {
  border: none;
}

.eval-btn {
  background: linear-gradient(135deg, #1677FF, #0958D9);
  color: #FFFFFF;
}

.urge-btn {
  background: #FFF7E6;
  color: #D48806;
  border: 1rpx solid #FFE58F;
}

.cancel-btn {
  background: #F5F5F5;
  color: #8C8C8C;
}

/* 加载 */
.loading-state {
  display: flex;
  justify-content: center;
  padding-top: 200rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #8C8C8C;
}

.safe-area-bottom {
  height: 120rpx;
}
</style>
