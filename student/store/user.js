import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: uni.getStorageSync('token') || '',
    userInfo: uni.getStorageSync('userInfo') ? JSON.parse(uni.getStorageSync('userInfo')) : null
  }),
  
  getters: {
    isLogin: (state) => !!state.token,
    studentId: (state) => state.userInfo?.studentId || '',
    userName: (state) => state.userInfo?.name || '',
    userPhone: (state) => state.userInfo?.phone || ''
  },
  
  actions: {
    setToken(token) {
      this.token = token
      uni.setStorageSync('token', token)
    },
    
    setUserInfo(info) {
      this.userInfo = info
      uni.setStorageSync('userInfo', JSON.stringify(info))
    },
    
    logout() {
      this.token = ''
      this.userInfo = null
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      uni.reLaunch({ url: '/pages/login/login' })
    }
  }
})
