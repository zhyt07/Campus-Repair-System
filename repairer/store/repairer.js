import { defineStore } from 'pinia'

export const useRepairerStore = defineStore('repairer', {
  state: () => ({
    token: uni.getStorageSync('repairer_token') || '',
    repairerInfo: uni.getStorageSync('repairerInfo') ? JSON.parse(uni.getStorageSync('repairerInfo')) : null
  }),
  
  getters: {
    isLogin: (state) => !!state.token,
    repairerId: (state) => state.repairerInfo?.id || '',
    repairerName: (state) => state.repairerInfo?.name || '',
    repairerPhone: (state) => state.repairerInfo?.phone || '',
    repairerNo: (state) => state.repairerInfo?.repairerNo || ''
  },
  
  actions: {
    setToken(token) {
      this.token = token
      uni.setStorageSync('repairer_token', token)
    },
    
    setRepairerInfo(info) {
      this.repairerInfo = info
      uni.setStorageSync('repairerInfo', JSON.stringify(info))
    },
    
    logout() {
      this.token = ''
      this.repairerInfo = null
      uni.removeStorageSync('repairer_token')
      uni.removeStorageSync('repairerInfo')
      uni.reLaunch({ url: '/pages/login/login' })
    }
  }
})
