# TabBar 图标说明

请将以下图标放置在本目录下（建议 40x40px 的 PNG 图片）：

- `home.png` - 首页图标（灰色）
- `home-active.png` - 首页图标（激活态，#1677FF）
- `order.png` - 报修图标（灰色）
- `order-active.png` - 报修图标（激活态，#1677FF）
- `mine.png` - 我的图标（灰色）
- `mine-active.png` - 我的图标（激活态，#1677FF）

推荐使用 iconfont 或从以下网站下载图标：
- https://www.iconfont.cn/
- https://icons8.com/

如暂未准备图标，可以删除 tabBar.list 中的 iconPath 和 selectedIconPath 字段，
使用纯文字 tabBar（uni-app 会自动处理）。
