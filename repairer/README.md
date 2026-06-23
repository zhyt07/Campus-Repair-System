# 校园宿舍智能报修 - 维修端小程序

基于 **uni-app (Vue 3)** 开发的微信小程序，供维修人员使用。

## 技术栈

- **框架**: uni-app 3.x (Vue 3 Composition API)
- **状态管理**: Pinia
- **样式**: SCSS
- **目标平台**: 微信小程序（可同时编译为 H5、App）

## 目录结构

```
repairer/
├── pages/
│   ├── login/          # 登录页（工号+密码）
│   ├── hall/           # 工单大厅（全部/待接单筛选，紧急/新工单标记）
│   ├── my-orders/      # 我的工单（维修中/已完成，计时器）
│   ├── order-detail/   # 工单详情（接单/完成维修弹窗）
│   └── mine/           # 个人中心（工作统计、历史工单、评价查看）
├── api/                # API 接口封装
├── store/              # Pinia 状态管理
├── utils/              # 工具函数
├── static/             # 静态资源
├── App.vue             # 全局样式
├── main.js             # 入口文件
├── manifest.json       # 应用配置
├── pages.json          # 页面路由与 tabBar
├── uni.scss            # 全局 SCSS 变量
└── package.json        # 依赖配置
```

## 页面说明

| 页面 | 路径 | 功能描述 |
|------|------|----------|
| 登录 | `/pages/login/login` | 工号+密码登录，标题"维修人员登录" |
| 工单大厅 | `/pages/hall/hall` | 全部/待接单筛选、紧急工单红色边框、新工单"新"标记、快速接单 |
| 我的工单 | `/pages/my-orders/my-orders` | 维修中/已完成切换、实时计时器（超4小时变红）、评分展示 |
| 工单详情 | `/pages/order-detail/order-detail` | 工单/学生信息、确认接单按钮、完成维修弹窗（上传维修后照片+备注） |
| 个人中心 | `/pages/mine/mine` | 人员信息、本月完成/平均评分/总工单、历史工单/评价查看、退出登录 |

## 设计规范

- 主色调: `#1677FF`
- 页面背景: `#F5F5F5`
- 卡片圆角: `16rpx`
- 按钮圆角: `8rpx`
- 页面边距: `30rpx`
- 标题字号: `36rpx`，正文: `28rpx`，辅助: `24rpx`

## 运行方式

```bash
cd repairer
npm install
npm run dev:mp-weixin    # 微信小程序
# 或
npm run dev:h5            # H5浏览器预览
```

## 后端 API 地址

默认连接 `http://localhost:8080/api`，可在 `utils/request.js` 中修改 `BASE_URL`。
