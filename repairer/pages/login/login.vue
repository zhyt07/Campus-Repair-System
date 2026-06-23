<template>
  <view class="login-page">
    <!-- Logo区域 -->
    <view class="logo-area">
      <view class="logo-icon">
        <text class="logo-emoji">🔧</text>
      </view>
      <text class="system-name">维修人员登录</text>
      <text class="system-subtitle">校园宿舍智能报修系统</text>
    </view>

    <!-- 登录表单 -->
    <view class="login-form">
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">👤</text>
          <input
            class="input-field"
            v-model="form.repairerNo"
            type="text"
            placeholder="请输入工号"
            placeholder-style="color: #BFBFBF"
            maxlength="20"
          />
        </view>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">🔒</text>
          <input
            class="input-field"
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            placeholder-style="color: #BFBFBF"
            maxlength="20"
          />
        </view>
      </view>
      <view class="form-item">
        <button
          class="login-btn"
          :class="{ 'btn-loading': loading }"
          :disabled="loading"
          @tap="handleLogin"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </view>
    </view>

    <!-- 底部提示 -->
    <view class="login-footer">
      <text class="footer-text">首次登录请联系管理员获取账号</text>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRepairerStore } from '../../store/repairer'
import { authApi } from '../../api/index'

const repairerStore = useRepairerStore()
const loading = ref(false)

const form = reactive({
  repairerNo: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.repairerNo.trim()) {
    uni.showToast({ title: '请输入工号', icon: 'none' })
    return
  }
  if (!form.password.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }
  
  loading.value = true
  try {
    const res = await authApi.login({
      username: form.repairerNo,
      password: form.password
    })
    
    repairerStore.setToken(res.token)
    repairerStore.setRepairerInfo(res.repairerInfo || { repairerNo: form.repairerNo })
    
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.switchTab({ url: '/pages/hall/hall' })
    }, 500)
  } catch (e) {
    console.error('登录失败:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #E6F4FF 0%, #F5F5F5 60%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 60rpx;
  box-sizing: border-box;
}

.logo-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 160rpx;
  margin-bottom: 80rpx;
}

.logo-icon {
  width: 200rpx;
  height: 200rpx;
  background: linear-gradient(135deg, #1677FF 0%, #0958D9 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30rpx;
  box-shadow: 0 8rpx 32rpx rgba(22, 119, 255, 0.3);
}

.logo-emoji {
  font-size: 80rpx;
}

.system-name {
  font-size: 40rpx;
  font-weight: 700;
  color: #1677FF;
  margin-bottom: 12rpx;
}

.system-subtitle {
  font-size: 24rpx;
  color: #8C8C8C;
}

.login-form {
  width: 100%;
}

.form-item {
  margin-bottom: 24rpx;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: #FFFFFF;
  border-radius: 8rpx;
  height: 88rpx;
  padding: 0 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.input-icon {
  font-size: 36rpx;
  margin-right: 16rpx;
}

.input-field {
  flex: 1;
  height: 88rpx;
  font-size: 28rpx;
  color: #262626;
}

.login-btn {
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
  margin-top: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(22, 119, 255, 0.3);
}

.login-btn::after { border: none; }
.login-btn:active { opacity: 0.85; }
.btn-loading { opacity: 0.7; }

.login-footer {
  position: absolute;
  bottom: 80rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.footer-text {
  font-size: 22rpx;
  color: #BFBFBF;
  line-height: 36rpx;
}
</style>
