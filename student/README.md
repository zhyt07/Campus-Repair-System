# 校园宿舍智能报修 - 学生端小程序

基于 **uni-app (Vue 3)** 开发的微信小程序，提供便捷的宿舍报修服务。

## 技术栈

- **框架**: uni-app 3.x (Vue 3 Composition API)
- **状态管理**: Pinia
- **样式**: SCSS
- **目标平台**: 微信小程序（可同时编译为 H5、App）

## 目录结构

```
student/
├── pages/
│   ├── login/          # 登录页
│   ├── index/          # 首页（公告轮播、功能入口、最近报修）
│   ├── apply/          # 报修申请（类型选择、位置选择、照片上传）
│   ├── order-list/     # 我的报修（状态筛选、列表分页）
│   ├── order-detail/   # 报修详情（信息卡片、时间轴、操作按钮）
│   ├── evaluation/     # 服务评价（星级评分、标签选择、文字评价）
│   └── mine/           # 个人中心（统计、菜单、退出登录）
├── api/                # API 接口封装
├── store/              # Pinia 状态管理（用户信息）
├── utils/              # 工具函数（请求封装、格式化、工具函数）
├── static/             # 静态资源（图标等）
├── App.vue             # 全局样式变量
├── main.js             # 入口文件
├── manifest.json       # 应用配置
├── pages.json          # 页面路由与 tabBar
├── uni.scss            # 全局 SCSS 变量
└── package.json        # 依赖配置
```

## 页面说明

| 页面 | 路径 | 功能描述 |
|------|------|----------|
| 登录 | `/pages/login/login` | 学号+密码登录，Logo展示 |
| 首页 | `/pages/index/index` | 公告轮播、功能入口网格、统计数据、最近报修列表 |
| 报修申请 | `/pages/apply/apply` | 类型选择（7种）、楼栋房间两级Picker、照片上传（最多5张）、故障描述 |
| 我的报修 | `/pages/order-list/order-list` | 横向滚动状态筛选、报修卡片列表、取消/催办/评价操作 |
| 报修详情 | `/pages/order-detail/order-detail` | 状态横幅、信息卡片、维修人员、垂直时间轴（呼吸动画） |
| 服务评价 | `/pages/evaluation/evaluation` | 五星评分（点击放大动画）、评价标签多选、文字评价（0-300字） |
| 个人中心 | `/pages/mine/mine` | 头像信息、统计数字、功能菜单、退出登录（二次确认） |

## 设计规范

- 主色调: `#1677FF`
- 页面背景: `#F5F5F5`
- 卡片圆角: `16rpx`
- 按钮圆角: `8rpx`
- 页面边距: `30rpx`
- 标题字号: `36rpx`，正文: `28rpx`，辅助: `24rpx`

## 运行方式

```bash
# 1. 安装依赖
npm install

# 2. 启动开发（微信小程序）
npm run dev:mp-weixin

# 3. 用微信开发者工具打开 dist/dev/mp-weixin 目录

# 或启动 H5 开发
npm run dev:h5
```

## 后端 API 地址

默认连接 `http://localhost:8080/api`，可在 `utils/request.js` 中修改 `BASE_URL`。
