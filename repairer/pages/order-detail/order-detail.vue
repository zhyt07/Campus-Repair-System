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

      <!-- 工单信息卡片 -->
      <view class="card">
        <view class="card-title">
          <text class="title-icon">📋</text>
          <text class="title-text">工单信息</text>
          <text v-if="isUrgent(order)" class="urgent-tag">紧急</text>
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
          <view class="info-item" v-if="order.assignTime">
            <text class="info-label">派单时间</text>
            <text class="info-value">{{ formatTime(order.assignTime) }}</text>
          </view>
          <view class="info-item" v-if="order.startTime">
            <text class="info-label">接单时间</text>
            <text class="info-value">{{ formatTime(order.startTime) }}</text>
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

      <!-- 学生信息卡片 -->
      <view v-if="order.student" class="card">
        <view class="card-title">
          <text class="title-icon">👤</text>
          <text class="title-text">报修学生</text>
        </view>
        <view class="student-info">
          <view class="student-avatar">
            <text class="avatar-text">{{ (order.student.name || '学').charAt(0) }}</text>
          </view>
          <view class="student-detail">
            <text class="student-name">{{ order.student.name }}</text>
            <text class="student-id">学号：{{ order.student.studentId }}</text>
          </view>
          <view class="student-phone" @tap="callStudent">
            <text class="phone-icon">📞</text>
            <text class="phone-text">联系</text>
          </view>
        </view>
      </view>

      <!-- 维修后照片（已完成时显示） -->
      <view v-if="afterImages.length > 0" class="card">
        <view class="card-title">
          <text class="title-icon">✅</text>
          <text class="title-text">维修后照片</text>
        </view>
        <view class="photo-grid">
          <image
            v-for="(img, index) in afterImages"
            :key="index"
            class="photo-item"
            :src="img"
            mode="aspectFill"
            @tap="previewAfterImage(index)"
          />
        </view>
      </view>

      <!-- 维修备注（已完成时显示） -->
      <view v-if="order.repairRemark" class="card">
        <view class="card-title">
          <text class="title-icon">📄</text>
          <text class="title-text">维修备注</text>
        </view>
        <text class="desc-text">{{ order.repairRemark }}</text>
      </view>

      <!-- 底部操作按钮 -->
      <view class="bottom-actions" v-if="showBottomAction">
        <!-- 待接单：确认接单 -->
        <button
          v-if="order.status === 'ASSIGNED' && canAccept"
          class="action-btn accept-btn"
          @tap="handleAccept"
        >确认接单</button>

        <!-- 维修中：完成维修 -->
        <button
          v-if="order.status === 'REPAIRING'"
          class="action-btn complete-btn"
          @tap="showCompleteModal = true"
        >完成维修</button>
      </view>

      <!-- 完成维修弹窗 -->
      <view class="modal-mask" v-if="showCompleteModal" @tap="showCompleteModal = false">
        <view class="modal-content" @tap.stop>
          <text class="modal-title">完成维修</text>

          <!-- 维修后照片 -->
          <view class="modal-section">
            <text class="modal-label">维修后照片（选填）</text>
            <view class="upload-grid">
              <view
                v-for="(img, index) in completeForm.photos"
                :key="index"
                class="upload-item"
              >
                <image class="upload-img" :src="img" mode="aspectFill" />
                <view class="upload-delete" @tap="removeCompletePhoto(index)">✕</view>
              </view>
              <view
                v-if="completeForm.photos.length < 3"
                class="upload-add"
                @tap="chooseCompletePhoto"
              >
                <text class="add-icon">+</text>
              </view>
            </view>
          </view>

          <!-- 维修备注 -->
          <view class="modal-section">
            <text class="modal-label">维修备注</text>
            <textarea
              class="remark-textarea"
              v-model="completeForm.remark"
              placeholder="请描述维修情况..."
              placeholder-style="color: #BFBFBF"
              maxlength="300"
            />
            <text class="remark-count">{{ completeForm.remark.length }}/300</text>
          </view>

          <!-- 按钮 -->
          <view class="modal-btns">
            <button class="modal-btn cancel-btn" @tap="showCompleteModal = false">取消</button>
            <button
              class="modal-btn confirm-btn"
              :disabled="completing"
              @tap="handleComplete"
            >{{ completing ? '提交中...' : '确认完成' }}</button>
          </view>
        </view>
      </view>

      <view class="safe-area-bottom"></view>
    </template>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { orderApi, fileApi } from '../../api/index'
import { useRepairerStore } from '../../store/repairer'
import {
  getTypeIcon, getTypeText, getStatusText, formatTime, isUrgent, previewImages
} from '../../utils/index'

const repairerStore = useRepairerStore()
const loading = ref(true)
const order = ref(null)
const orderId = ref('')
const showCompleteModal = ref(false)
const completing = ref(false)

const completeForm = reactive({
  photos: [],
  remark: ''
})

// 图片列表
const images = computed(() => {
  if (!order.value?.images) return []
  const imgs = order.value.images
  return typeof imgs === 'string' ? imgs.split(',').filter(Boolean) : imgs
})

// 维修后图片
const afterImages = computed(() => {
  if (!order.value?.afterImages) return []
  const imgs = order.value.afterImages
  return typeof imgs === 'string' ? imgs.split(',').filter(Boolean) : imgs
})

// 是否可以接单（检查是否分配给当前维修人员）
const canAccept = computed(() => {
  if (!order.value?.repairerId) return true
  return order.value.repairerId === repairerStore.repairerId
})

// 状态图标
const statusIcon = computed(() => {
  const map = {
    'PENDING': '⏳', 'ASSIGNED': '📋', 'REPAIRING': '🔧',
    'COMPLETED': '✅', 'EVALUATED': '⭐', 'CANCELLED': '❌'
  }
  return map[order.value?.status] || '📋'
})

// 状态描述
const statusDesc = computed(() => {
  const map = {
    'PENDING': '等待管理员派单',
    'ASSIGNED': '等待维修人员接单',
    'REPAIRING': '维修人员正在处理中',
    'COMPLETED': '维修已完成',
    'EVALUATED': '已完成评价',
    'CANCELLED': '该工单已取消'
  }
  return map[order.value?.status] || ''
})

// 是否显示底部操作
const showBottomAction = computed(() => {
  const s = order.value?.status
  return (s === 'ASSIGNED' && canAccept.value) || s === 'REPAIRING'
})

// 预览图片
const previewImage = (index) => previewImages(images.value, index)
const previewAfterImage = (index) => previewImages(afterImages.value, index)

// 联系学生
const callStudent = () => {
  const phone = order.value?.student?.phone
  if (phone) {
    uni.makePhoneCall({ phoneNumber: phone })
  }
}

// 确认接单
const handleAccept = () => {
  uni.showModal({
    title: '确认接单',
    content: '接单后请在4小时内完成维修，确定接单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await orderApi.acceptOrder(orderId.value)
          uni.showToast({ title: '接单成功', icon: 'success' })
          setTimeout(() => loadDetail(), 500)
        } catch (e) { console.error('接单失败:', e) }
      }
    }
  })
}

// 选择完成照片
const chooseCompletePhoto = () => {
  uni.chooseImage({
    count: 3 - completeForm.photos.length,
    sizeType: ['compressed'],
    sourceType: ['camera', 'album'],
    success: async (res) => {
      uni.showLoading({ title: '上传中...' })
      try {
        for (const path of res.tempFilePaths) {
          const url = await fileApi.uploadImage(path)
          completeForm.photos.push(url)
        }
      } catch (e) { console.error('上传失败:', e) }
      finally { uni.hideLoading() }
    }
  })
}

// 删除完成照片
const removeCompletePhoto = (index) => {
  completeForm.photos.splice(index, 1)
}

// 完成维修
const handleComplete = async () => {
  completing.value = true
  try {
    await orderApi.completeOrder({
      orderId: orderId.value,
      afterImages: completeForm.photos.join(','),
      repairRemark: completeForm.remark.trim()
    })
    
    uni.showToast({ title: '维修完成', icon: 'success' })
    showCompleteModal.value = false
    setTimeout(() => loadDetail(), 500)
  } catch (e) {
    console.error('完成维修失败:', e)
  } finally {
    completing.value = false
  }
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
  border-radius: 16rpx; padding: 30rpx; margin-bottom: 20rpx; color: #FFFFFF;
}
.banner-pending { background: linear-gradient(135deg, #FAAD14, #D48806); }
.banner-assigned { background: linear-gradient(135deg, #1677FF, #0958D9); }
.banner-repairing { background: linear-gradient(135deg, #1677FF, #0958D9); }
.banner-completed { background: linear-gradient(135deg, #52C41A, #389E0D); }
.banner-evaluated { background: linear-gradient(135deg, #8C8C8C, #595959); }
.banner-cancelled { background: linear-gradient(135deg, #FF4D4F, #CF1322); }

.banner-content { display: flex; align-items: center; }
.banner-icon { font-size: 48rpx; margin-right: 20rpx; }
.banner-text { display: flex; flex-direction: column; }
.banner-title { font-size: 36rpx; font-weight: 700; margin-bottom: 4rpx; }
.banner-desc { font-size: 24rpx; opacity: 0.9; }

/* 卡片 */
.card {
  background: #FFFFFF; border-radius: 16rpx; padding: 24rpx;
  margin-bottom: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.card-title {
  display: flex; align-items: center; margin-bottom: 20rpx;
  padding-bottom: 16rpx; border-bottom: 1rpx solid #F0F0F0;
}
.title-icon { font-size: 32rpx; margin-right: 12rpx; }
.title-text { font-size: 28rpx; font-weight: 600; color: #262626; flex: 1; }

.urgent-tag {
  background: #FF4D4F; color: #FFFFFF; font-size: 20rpx;
  padding: 4rpx 16rpx; border-radius: 4rpx; font-weight: 600;
}

/* 信息网格 */
.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20rpx; }
.info-item { display: flex; flex-direction: column; }
.info-label { font-size: 22rpx; color: #8C8C8C; margin-bottom: 6rpx; }
.info-value { font-size: 26rpx; color: #262626; font-weight: 500; }

/* 描述 */
.desc-text {
  font-size: 28rpx; color: #595959; line-height: 1.8; white-space: pre-wrap;
}

/* 照片网格 */
.photo-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12rpx; }
.photo-item { width: 100%; aspect-ratio: 1; border-radius: 8rpx; overflow: hidden; }

/* 学生信息 */
.student-info { display: flex; align-items: center; }
.student-avatar {
  width: 72rpx; height: 72rpx; background: linear-gradient(135deg, #1677FF, #0958D9);
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  margin-right: 16rpx;
}
.avatar-text { color: #FFFFFF; font-size: 32rpx; font-weight: 600; }
.student-detail { flex: 1; }
.student-name { font-size: 28rpx; font-weight: 500; color: #262626; display: block; margin-bottom: 4rpx; }
.student-id { font-size: 24rpx; color: #8C8C8C; }

.student-phone {
  display: flex; flex-direction: column; align-items: center;
  padding: 12rpx 20rpx; background: #E6F4FF; border-radius: 12rpx;
}
.phone-icon { font-size: 32rpx; }
.phone-text { font-size: 20rpx; color: #1677FF; margin-top: 4rpx; }

/* 底部操作 */
.bottom-actions {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #FFFFFF; padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.action-btn {
  width: 100%; height: 80rpx; border-radius: 8rpx;
  font-size: 30rpx; font-weight: 500; border: none;
  display: flex; align-items: center; justify-content: center;
}
.action-btn::after { border: none; }

.accept-btn { background: linear-gradient(135deg, #1677FF, #0958D9); color: #FFFFFF; }
.complete-btn { background: linear-gradient(135deg, #52C41A, #389E0D); color: #FFFFFF; }

/* 弹窗 */
.modal-mask {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5); display: flex; align-items: flex-end;
  z-index: 100;
}
.modal-content {
  width: 100%; background: #FFFFFF; border-radius: 32rpx 32rpx 0 0;
  padding: 40rpx 30rpx; padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
  max-height: 80vh; overflow-y: auto;
}
.modal-title { font-size: 34rpx; font-weight: 700; color: #262626; display: block; margin-bottom: 30rpx; }
.modal-section { margin-bottom: 30rpx; }
.modal-label { font-size: 28rpx; font-weight: 500; color: #262626; display: block; margin-bottom: 16rpx; }

.upload-grid { display: flex; gap: 16rpx; }
.upload-item { position: relative; width: 160rpx; height: 160rpx; border-radius: 8rpx; overflow: hidden; }
.upload-img { width: 100%; height: 100%; }
.upload-delete {
  position: absolute; top: 0; right: 0; width: 40rpx; height: 40rpx;
  background: rgba(0, 0, 0, 0.5); border-radius: 0 8rpx 0 8rpx;
  display: flex; align-items: center; justify-content: center;
  color: #FFFFFF; font-size: 24rpx;
}
.upload-add {
  width: 160rpx; height: 160rpx; border: 2rpx dashed #D9D9D9;
  border-radius: 8rpx; display: flex; align-items: center; justify-content: center;
  background: #FAFAFA;
}
.add-icon { font-size: 60rpx; color: #BFBFBF; }

.remark-textarea {
  width: 100%; height: 160rpx; background: #F5F5F5; border-radius: 8rpx;
  padding: 16rpx; font-size: 28rpx; box-sizing: border-box;
}
.remark-count { font-size: 22rpx; color: #BFBFBF; text-align: right; display: block; margin-top: 8rpx; }

.modal-btns { display: flex; gap: 20rpx; }
.modal-btn {
  flex: 1; height: 80rpx; border-radius: 8rpx; font-size: 30rpx;
  display: flex; align-items: center; justify-content: center; border: none;
}
.modal-btn::after { border: none; }
.cancel-btn { background: #F5F5F5; color: #595959; }
.confirm-btn { background: linear-gradient(135deg, #1677FF, #0958D9); color: #FFFFFF; }

/* 加载 */
.loading-state { display: flex; justify-content: center; padding-top: 200rpx; }
.loading-text { font-size: 28rpx; color: #8C8C8C; }

.safe-area-bottom { height: 120rpx; }
</style>
