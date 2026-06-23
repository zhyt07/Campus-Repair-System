// 请求封装
const BASE_URL = 'http://localhost:8080/api'

// 请求拦截器
const requestInterceptor = (options) => {
  const token = uni.getStorageSync('token')
  if (token) {
    options.header = {
      ...options.header,
      'Authorization': `Bearer ${token}`
    }
  }
  options.header = {
    ...options.header,
    'Content-Type': 'application/json'
  }
  return options
}

// 响应拦截器
const responseInterceptor = (response) => {
  const { statusCode, data } = response
  if (statusCode === 200) {
    if (data.code === 200) {
      return data.data
    } else if (data.code === 401) {
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      uni.reLaunch({ url: '/pages/login/login' })
      return Promise.reject(new Error('登录已过期'))
    } else {
      uni.showToast({ title: data.message || '请求失败', icon: 'none' })
      return Promise.reject(new Error(data.message))
    }
  } else if (statusCode === 401) {
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.reLaunch({ url: '/pages/login/login' })
    return Promise.reject(new Error('未授权'))
  } else {
    uni.showToast({ title: `请求失败(${statusCode})`, icon: 'none' })
    return Promise.reject(new Error(`HTTP ${statusCode}`))
  }
}

// GET 请求
const get = (url, params = {}) => {
  return new Promise((resolve, reject) => {
    const options = {
      url: BASE_URL + url,
      method: 'GET',
      data: params
    }
    requestInterceptor(options)
    uni.request({
      ...options,
      success: (res) => {
        responseInterceptor(res).then(resolve).catch(reject)
      },
      fail: (err) => {
        uni.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

// POST 请求
const post = (url, data = {}) => {
  return new Promise((resolve, reject) => {
    const options = {
      url: BASE_URL + url,
      method: 'POST',
      data
    }
    requestInterceptor(options)
    uni.request({
      ...options,
      success: (res) => {
        responseInterceptor(res).then(resolve).catch(reject)
      },
      fail: (err) => {
        uni.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

// PUT 请求
const put = (url, data = {}) => {
  return new Promise((resolve, reject) => {
    const options = {
      url: BASE_URL + url,
      method: 'PUT',
      data
    }
    requestInterceptor(options)
    uni.request({
      ...options,
      success: (res) => {
        responseInterceptor(res).then(resolve).catch(reject)
      },
      fail: (err) => {
        uni.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

// DELETE 请求
const del = (url, params = {}) => {
  return new Promise((resolve, reject) => {
    const options = {
      url: BASE_URL + url,
      method: 'DELETE',
      data: params
    }
    requestInterceptor(options)
    uni.request({
      ...options,
      success: (res) => {
        responseInterceptor(res).then(resolve).catch(reject)
      },
      fail: (err) => {
        uni.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

// 文件上传
const upload = (url, filePath, formData = {}) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    uni.uploadFile({
      url: BASE_URL + url,
      filePath,
      name: 'file',
      formData,
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data)
          } else {
            uni.showToast({ title: data.message || '上传失败', icon: 'none' })
            reject(new Error(data.message))
          }
        } catch (e) {
          reject(e)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '上传失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

export default { get, post, put, del, upload }
