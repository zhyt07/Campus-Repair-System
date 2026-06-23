<template>
  <view class="apply-page">
    <!-- 报修类型 -->
    <view class="card">
      <view class="form-label">
        <text class="label-text">报修类型</text>
        <text class="label-required">*</text>
      </view>
      <view class="type-grid">
        <view
          v-for="item in repairTypes"
          :key="item.value"
          class="type-item"
          :class="{ active: form.repairType === item.value }"
          @tap="form.repairType = item.value"
        >
          <text class="type-emoji">{{ item.icon }}</text>
          <text class="type-label">{{ item.label }}</text>
        </view>
      </view>
    </view>

    <!-- 故障位置 -->
    <view class="card">
      <view class="form-label">
        <text class="label-text">故障位置</text>
        <text class="label-required">*</text>
      </view>
      <view class="location-row">
        <picker
          class="picker-box"
          mode="selector"
          :range="buildings"
          range-key="name"
          @change="onBuildingChange"
        >
          <view class="picker-content">
            <text class="picker-icon">🏢</text>
            <text :class="form.building ? 'picker-text' : 'picker-placeholder'">
              {{ form.building || '请选择楼栋' }}
            </text>
            <text class="picker-arrow">›</text>
          </view>
        </picker>
        <picker
          class="picker-box"
          mode="selector"
          :range="rooms"
          @change="onRoomChange"
        >
          <view class="picker-content">
            <text class="picker-icon">🚪</text>
            <text :class="form.room ? 'picker-text' : 'picker-placeholder'">
              {{ form.room || '请选择房间' }}
            </text>
            <text class="picker-arrow">›</text>
          </view>
        </picker>
      </view>
    </view>

    <!-- 故障描述 -->
    <view class="card">
      <view class="form-label">
        <text class="label-text">故障描述</text>
        <text class="label-required">*</text>
      </view>
      <textarea
        class="desc-textarea"
        v-model="form.description"
        placeholder="请详细描述故障情况，如：宿舍阳台水龙头漏水严重，无法正常关闭..."
        placeholder-style="color: #BFBFBF; font-size: 28rpx"
        maxlength="500"
        :auto-height="true"
      />
      <view class="textarea-footer">
        <text class="char-count">{{ form.description.length }}/500</text>
      </view>
    </view>

    <!-- 照片上传 -->
    <view class="card">
      <view class="form-label">
        <text class="label-text">现场照片</text>
        <text class="label-tip">（选填，最多5张）</text>
      </view>
      <view class="photo-grid">
        <view
          v-for="(item, index) in photos"
          :key="index"
          class="photo-item"
          @tap="previewPhoto(index)"
        >
          <image class="photo-img" :src="item" mode="aspectFill" />
          <view class="photo-delete" @tap.stop="removePhoto(index)">
            <text class="delete-icon">✕</text>
          </view>
        </view>
        <view
          v-if="photos.length < 5"
          class="photo-add"
          @tap="choosePhoto"
        >
          <text class="add-icon">+</text>
          <text class="add-text">添加图片</text>
        </view>
      </view>
    </view>

    <!-- 联系电话 -->
    <view class="card">
      <view class="form-label">
        <text class="label-text">联系电话</text>
        <text class="label-required">*</text>
      </view>
      <view class="input-wrapper">
        <text class="input-icon">📱</text>
        <input
          class="input-field"
          v-model="form.contactPhone"
          type="number"
          placeholder="请输入联系电话"
          placeholder-style="color: #BFBFBF"
          maxlength="11"
        />
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
        {{ submitting ? '提交中...' : '提交报修' }}
      </button>
    </view>

    <view class="safe-area-bottom"></view>
  </view>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { orderApi, fileApi } from '../../api/index'
import { previewImages } from '../../utils/index'

const submitting = ref(false)
const photos = ref([])

// 报修类型
const repairTypes = [
  { value: 'WATER_ELECTRIC', label: '水电', icon: '⚡' },
  { value: 'FURNITURE', label: '家具', icon: '🪑' },
  { value: 'WALL', label: '墙面', icon: '🧱' },
  { value: 'DOOR_WINDOW', label: '门窗', icon: '🚪' },
  { value: 'NETWORK', label: '网络', icon: '🌐' },
  { value: 'AIR_CONDITIONER', label: '空调', icon: '❄️' },
  { value: 'OTHER', label: '其他', icon: '🔧' }
]

// 楼栋数据
const buildings = [
  { name: '1号宿舍楼', rooms: 20 },
  { name: '2号宿舍楼', rooms: 20 },
  { name: '3号宿舍楼', rooms: 20 },
  { name: '4号宿舍楼', rooms: 20 },
  { name: '5号宿舍楼', rooms: 20 },
  { name: '6号宿舍楼', rooms: 20 }
]

// 房间列表（动态生成）
const rooms = ref([])

// 表单数据
const form = reactive({
  repairType: '',
  building: '',
  room: '',
  description: '',
  contactPhone: ''
})

// 选择楼栋
const onBuildingChange = (e) => {
  const index = e.detail.value
  const building = buildings[index]
  form.building = building.name
  // 生成房间号列表
  const roomList = []
  for (let floor = 1; floor <= Math.ceil(building.rooms / 4); floor++) {
    for (let i = 1; i <= 4; i++) {
      const roomNum = floor * 100 + i
      if (roomNum > 100 + building.rooms) break
      roomList.push(`${roomNum}室`)
    }
  }
  rooms.value = roomList
  form.room = ''
}

// 选择房间
const onRoomChange = (e) => {
  form.room = rooms.value[e.detail.value]
}

// 选择照片
const choosePhoto = () => {
  uni.chooseImage({
    count: 5 - photos.value.length,
    sizeType: ['compressed'],
    sourceType: ['camera', 'album'],
    success: async (res) => {
      uni.showLoading({ title: '上传中...' })
      try {
        for (const path of res.tempFilePaths) {
          const url = await fileApi.uploadImage(path)
          photos.value.push(url)
        }
      } catch (e) {
        console.error('上传失败:', e)
      } finally {
        uni.hideLoading()
      }
    }
  })
}

// 预览照片
const previewPhoto = (index) => {
  previewImages(photos.value, index)
}

// 删除照片
const removePhoto = (index) => {
  photos.value.splice(index, 1)
}

// 提交报修
const handleSubmit = async () => {
  // 表单验证
  if (!form.repairType) {
    uni.showToast({ title: '请选择报修类型', icon: 'none' })
    return
  }
  if (!form.building) {
    uni.showToast({ title: '请选择楼栋', icon: 'none' })
    return
  }
  if (!form.room) {
    uni.showToast({ title: '请选择房间', icon: 'none' })
    return
  }
  if (!form.description.trim()) {
    uni.showToast({ title: '请填写故障描述', icon: 'none' })
    return
  }
  if (form.description.trim().length < 5) {
    uni.showToast({ title: '故障描述至少5个字符', icon: 'none' })
    return
  }
  if (!form.contactPhone.trim()) {
    uni.showToast({ title: '请输入联系电话', icon: 'none' })
    return
  }
  if (!/^1\d{10}$/.test(form.contactPhone.trim())) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }

  // 确认提交
  uni.showModal({
    title: '确认提交',
    content: '提交后将无法修改，确定要提交报修申请吗？',
    success: async (res) => {
      if (!res.confirm) return
      
      submitting.value = true
      try {
        await orderApi.submit({
          repairType: form.repairType,
          location: `${form.building} ${form.room}`,
          description: form.description.trim(),
          contactPhone: form.contactPhone.trim(),
          images: photos.value.join(',')
        })
        
        uni.showToast({ title: '报修提交成功', icon: 'success' })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (e) {
        console.error('提交失败:', e)
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  // 自动填充手机号
  const phone = uni.getStorageSync('userPhone')
  if (phone) {
    form.contactPhone = phone
  }
})
</script>

<style lang="scss" scoped>
.apply-page {
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

/* 表单标签 */
.form-label {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.label-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #262626;
}

.label-required {
  color: #FF4D4F;
  font-size: 28rpx;
  margin-left: 4rpx;
}

.label-tip {
  font-size: 24rpx;
  color: #8C8C8C;
  margin-left: 8rpx;
}

/* 类型选择 */
.type-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
}

.type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 8rpx;
  border: 2rpx solid #F0F0F0;
  border-radius: 12rpx;
  transition: all 0.2s;

  &.active {
    border-color: #1677FF;
    background: #E6F4FF;
  }
}

.type-emoji {
  font-size: 40rpx;
  margin-bottom: 8rpx;
}

.type-label {
  font-size: 24rpx;
  color: #262626;
}

/* 位置选择 */
.location-row {
  display: flex;
  gap: 20rpx;
}

.picker-box {
  flex: 1;
}

.picker-content {
  display: flex;
  align-items: center;
  height: 80rpx;
  background: #F5F5F5;
  border-radius: 8rpx;
  padding: 0 20rpx;
}

.picker-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.picker-text {
  flex: 1;
  font-size: 28rpx;
  color: #262626;
}

.picker-placeholder {
  flex: 1;
  font-size: 28rpx;
  color: #BFBFBF;
}

.picker-arrow {
  font-size: 36rpx;
  color: #BFBFBF;
  font-weight: bold;
}

/* 描述输入 */
.desc-textarea {
  width: 100%;
  min-height: 200rpx;
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

/* 照片上传 */
.photo-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
}

.photo-item {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  border-radius: 8rpx;
  overflow: hidden;
}

.photo-img {
  width: 100%;
  height: 100%;
}

.photo-delete {
  position: absolute;
  top: 0;
  right: 0;
  width: 40rpx;
  height: 40rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 0 8rpx 0 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-icon {
  color: #FFFFFF;
  font-size: 24rpx;
  font-weight: bold;
}

.photo-add {
  width: 100%;
  aspect-ratio: 1;
  border: 2rpx dashed #D9D9D9;
  border-radius: 8rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #FAFAFA;
}

.add-icon {
  font-size: 48rpx;
  color: #BFBFBF;
  line-height: 1;
}

.add-text {
  font-size: 20rpx;
  color: #BFBFBF;
  margin-top: 4rpx;
}

/* 电话输入 */
.input-wrapper {
  display: flex;
  align-items: center;
  height: 80rpx;
  background: #F5F5F5;
  border-radius: 8rpx;
  padding: 0 20rpx;
}

.input-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.input-field {
  flex: 1;
  height: 80rpx;
  font-size: 28rpx;
  color: #262626;
}

/* 提交按钮 */
.submit-area {
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

.safe-area-bottom {
  height: 40rpx;
}
</style>
