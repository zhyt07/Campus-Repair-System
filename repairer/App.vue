<script>
export default {
  onLaunch() {
    console.log('维修端小程序启动')
  }
}
</script>

<style lang="scss">
/* 全局样式变量 - 与学生端保持一致 */

/* 主色调 */
$primary-color: #1677FF;
$primary-light: #E6F4FF;
$primary-dark: #0958D9;

/* 状态色 */
$success-color: #52C41A;
$warning-color: #FAAD14;
$error-color: #FF4D4F;

/* 文字色 */
$text-primary: #262626;
$text-secondary: #8C8C8C;
$text-hint: #BFBFBF;

/* 背景色 */
$bg-page: #F5F5F5;
$bg-card: #FFFFFF;
$divider-color: #F0F0F0;

/* 圆角 */
$radius-card: 16rpx;
$radius-btn: 8rpx;

/* 间距 */
$spacing-page: 30rpx;
$spacing-card: 24rpx;
$spacing-item: 20rpx;

/* 字体 */
$font-title: 36rpx;
$font-body: 28rpx;
$font-assist: 24rpx;
$font-mini: 20rpx;

/* 全局样式 */
page {
  background-color: #F5F5F5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 28rpx;
  color: #262626;
  line-height: 1.5;
}

.card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.btn-primary {
  background: linear-gradient(135deg, #1677FF 0%, #0958D9 100%);
  color: #FFFFFF;
  border: none;
  border-radius: 8rpx;
  font-size: 32rpx;
  font-weight: 500;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-primary:active {
  opacity: 0.85;
}

/* 状态标签 */
.status-tag {
  display: inline-flex;
  align-items: center;
  padding: 4rpx 16rpx;
  border-radius: 6rpx;
  font-size: 22rpx;
  font-weight: 500;
}

.status-pending { background: #FFF7E6; color: #D48806; }
.status-assigned { background: #E6F4FF; color: #1677FF; }
.status-repairing { background: #E6F4FF; color: #1677FF; }
.status-completed { background: #F6FFED; color: #389E0D; }
.status-evaluated { background: #F5F5F5; color: #8C8C8C; }

/* 单行省略 */
.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 两行省略 */
.text-ellipsis-2 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 安全区 */
.safe-area-bottom {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}
</style>
