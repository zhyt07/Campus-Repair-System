<template>
  <view class="evaluation-page">
    <!-- 工单简要信息 -->
    <view v-if="orderInfo" class="card order-summary">
      <view class="summary-header">
        <text class="summary-icon">{{ getTypeIcon(orderInfo.repairType) }}</text>
        <view class="summary-info">
          <text class="summary-title">{{ getTypeText(orderInfo.repairType) }}维修</text>
          <text class="summary-no">报修编号：{{ orderInfo.orderNo }}</text>
        </view>
        <text class="summary-tag completed">已完成</text>
      </view>
      <text class="summary-desc text-ellipsis">{{ orderInfo.description }}</text>
    </view>

    <!-- 星级评分 -->
    <view class="card">
      <view class="card-title">
        <text class="title-text">总体评分</text>
      </view>
      <view class="star-rating">
        <view
          v-for="i in 5"
          :key="i"
          class="star-item"
          :class="{ active: rating >= i, 'star-animate': rating === i && animating }"
          @tap="setRating(i)"
        >
          <text class="star-icon">{{ rating >= i ? '★' : '☆' }}</text>
        </view>
        <text class="rating-text">{{ ratingText }}</text>
      </view>
    </view>

    <!-- 评价标签 -->
    <view class="card">
      <view class="card-title">
        <text class="title-text">服务亮点</text>
        <text class="title-tip">（可多选）</text>
      </view>
      <view class="tag-grid">
        <view
          v-for="tag in evalTags"
          :key="tag"
          class="tag-item"
          :class="{ selected: selectedTags.includes(tag) }"
          @tap="toggleTag(tag)"
        >
          <text class="tag-text">{{ tag }}</text>
          <text v-if="selectedTags.includes(tag)" class="tag-check">✓</text>
        </view>
      </view>
    </view>

    <!-- 评价内容 -->
    <view class="card">
      <view class="card-title">
        <text class="title-text">评价内容</text>
        <text class="title-tip">（选填）</text>
      </view>
      <textarea
        class="eval-textarea"
        v-model="content"
        placeholder="分享您的维修体验，帮助提升服务质量..."
        placeholder-style="color: #BFBFBF; font-size: 28rpx"
        maxlength="300"
        :auto-height="true"
      />
      <view class="textarea-footer">
        <text class="char-count">{{ content.length }}/300</text>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-area">
      <button
        class="submit-btn"
        :class="{ 'btn-loading': submitting }"
        :disabled="submitting"
        @tap="handleSubmit"
      >
        {{ submitting ? '提交中...' : '提交评价' }}
      </button>
      <text class="submit-tip">评价提交后不可修改哦~</text>
    </view>

    <view class="safe-area-bottom"></view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { evaluationApi, orderApi } from '../../api/index'
import { getTypeIcon, getTypeText } from '../../utils/index'

const orderId = ref('')
const orderInfo = ref(null)
const submitting = ref(false)
const animating = ref(false)

const rating = ref(0)
const content = ref('')
const selectedTags = ref([])

// 评价标签
const evalTags = [
  '响应快速',
  '质量好',
  '态度好',
  '技术专业',
  '环境整洁'
]

// 评分文字
const ratingText = computed(() => {
  const texts = ['', '非常差', '比较差', '一般', '满意', '非常满意']
  return rating.value > 0 ? texts[rating.value] : '点击星星评分'
})

// 设置评分
const setRating = (value) => {
  rating.value = value
  animating.value = true
  setTimeout(() => { animating.value = false }, 300)
}

// 切换标签
const toggleTag = (tag) => {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
}

// 提交评价
const handleSubmit = async () => {
  // 验证
  if (rating.value === 0) {
    uni.showToast({ title: '请给出您的评分', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    await evaluationApi.submit({
      orderId: orderId.value,
      score: rating.value,
      tags: selectedTags.value.join(','),
      content: content.value.trim()
    })
    
    uni.showToast({ title: '评价成功，感谢反馈！', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (e) {
    console.error('评价失败:', e)
  } finally {
    submitting.value = false
  }
}

// 加载工单信息
const loadOrderInfo = async () => {
  try {
    const res = await orderApi.getDetail(orderId.value)
    orderInfo.value = res
  } catch (e) {
    console.error('加载工单信息失败:', e)
  }
}

onLoad((options) => {
  orderId.value = options.orderId
  loadOrderInfo()
})
</script>

<style lang="scss" scoped>
.evaluation-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding: 20rpx 30rpx;
}

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
}

.title-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #262626;
}

.title-tip {
  font-size: 24rpx;
  color: #BFBFBF;
  margin-left: 8rpx;
}

/* 工单摘要 */
.order-summary {
  .summary-header {
    display: flex;
    align-items: center;
    margin-bottom: 12rpx;
  }

  .summary-icon {
    font-size: 40rpx;
    margin-right: 12rpx;
  }

  .summary-info {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .summary-title {
    font-size: 28rpx;
    font-weight: 500;
    color: #262626;
  }

  .summary-no {
    font-size: 22rpx;
    color: #8C8C8C;
    margin-top: 2rpx;
  }

  .summary-tag {
    padding: 4rpx 16rpx;
    border-radius: 6rpx;
    font-size: 22rpx;
    font-weight: 500;

    &.completed {
      background: #F6FFED;
      color: #389E0D;
    }
  }

  .summary-desc {
    font-size: 24rpx;
    color: #8C8C8C;
    line-height: 1.5;
  }
}

/* 星级评分 */
.star-rating {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 0 10rpx;
}

.star-item {
  padding: 0 8rpx;
}

.star-icon {
  font-size: 64rpx;
  color: #E8E8E8;
  transition: all 0.2s ease;
}

.star-item.active .star-icon {
  color: #FAAD14;
}

.star-animate .star-icon {
  transform: scale(1.2);
}

.rating-text {
  font-size: 28rpx;
  color: #FAAD14;
  font-weight: 500;
  margin-left: 16rpx;
  min-width: 120rpx;
}

/* 标签网格 */
.tag-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.tag-item {
  display: flex;
  align-items: center;
  padding: 12rpx 24rpx;
  border: 2rpx solid #E8E8E8;
  border-radius: 30rpx;
  transition: all 0.2s;
  background: #FFFFFF;

  &.selected {
    border-color: #1677FF;
    background: #E6F4FF;
  }
}

.tag-text {
  font-size: 26rpx;
  color: #595959;
}

.tag-item.selected .tag-text {
  color: #1677FF;
  font-weight: 500;
}

.tag-check {
  font-size: 22rpx;
  color: #1677FF;
  font-weight: bold;
  margin-left: 6rpx;
}

/* 评价内容 */
.eval-textarea {
  width: 100%;
  min-height: 180rpx;
  font-size: 28rpx;
  color: #262626;
  line-height: 1.6;
  box-sizing: border-box;
}

.textarea-footer {
  display: flex;
  justify-content: flex-end;
}

.char-count {
  font-size: 22rpx;
  color: #BFBFBF;
}

/* 提交区域 */
.submit-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx 0;
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #1677FF 0%, #0958D9 100%);
  color: #FFFFFF;
  font-size: 32rpx;
  font-weight: 500;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  box-shadow: 0 4rpx 16rpx rgba(22, 119, 255, 0.3);
}

.submit-btn::after {
  border: none;
}

.submit-btn:active {
  opacity: 0.85;
}

.btn-loading {
  opacity: 0.7;
}

.submit-tip {
  font-size: 22rpx;
  color: #BFBFBF;
  margin-top: 16rpx;
}

.safe-area-bottom {
  height: 40rpx;
}
</style>
