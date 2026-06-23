# Campus Repair System - 后端

校园宿舍智能报修与服务评价系统 Spring Boot 后端

## 技术栈

- **框架**: Spring Boot 2.7.18
- **ORM**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 8.0
- **缓存**: Redis 6.0
- **安全**: BCrypt + JWT
- **工具**: Lombok, Fastjson, Hutool

## 项目结构

```
backend/
├── pom.xml
├── src/main/java/com/campus/repair/
│   ├── CampusRepairApplication.java    # 启动类
│   ├── config/                          # 配置类
│   │   ├── CorsConfig.java             # 跨域配置
│   │   ├── MybatisPlusConfig.java      # MyBatis-Plus配置
│   │   ├── RedisConfig.java            # Redis配置
│   │   ├── SecurityConfig.java         # 安全配置(BCrypt)
│   │   ├── WebMvcConfig.java           # Web MVC配置
│   │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   └── interceptor/
│   │       └── AuthInterceptor.java    # JWT认证拦截器
│   ├── common/                          # 通用类
│   │   ├── Result.java                 # 统一响应
│   │   ├── PageResult.java             # 分页响应
│   │   └── BusinessException.java      # 业务异常
│   ├── dto/                             # 数据传输对象
│   ├── entity/                          # 实体类
│   │   ├── User.java                   # 用户(学生/管理员)
│   │   ├── Repairer.java              # 维修人员
│   │   ├── RepairOrder.java           # 报修工单
│   │   ├── Evaluation.java            # 服务评价
│   │   ├── Announcement.java          # 公告
│   │   └── OperationLog.java          # 操作日志
│   ├── mapper/                          # Mapper接口
│   ├── service/                         # 服务接口
│   │   └── impl/                       # 服务实现
│   ├── controller/                      # 控制器
│   └── util/                            # 工具类
│       ├── JwtUtil.java               # JWT工具
│       ├── OrderNoGenerator.java      # 报修编号生成器
│       └── PhoneDesensitizeUtil.java  # 手机号脱敏
└── src/main/resources/
    ├── application.yml                  # 主配置
    ├── application-dev.yml             # 开发环境配置
    └── db/
        └── init.sql                    # 数据库初始化脚本
```

## 快速开始

### 1. 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 数据库初始化

```bash
mysql -u root -p < src/main/resources/db/init.sql
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库和Redis连接信息。

### 4. 启动

```bash
mvn spring-boot:run
```

或者

```bash
mvn clean package -DskipTests
java -jar target/campus-repair-1.0.0.jar
```

### 5. 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 管理后台 |
| 学生 | 2024001001 | 123456 | 学生端小程序 |
| 维修工 | REP001 | 123456 | 维修端小程序 |

## API 接口

### 认证
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 登录 |

### 报修工单
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/orders | 提交报修 |
| GET | /api/orders/student | 学生-我的报修 |
| GET | /api/orders/repairer | 维修工-工单列表 |
| GET | /api/orders/admin | 管理员-全部工单 |
| GET | /api/orders/{id} | 工单详情(含时间轴) |
| PUT | /api/orders/{id}/accept | 接单 |
| PUT | /api/orders/{id}/complete | 完工上报 |
| PUT | /api/orders/{id}/cancel | 取消工单 |

### 派单
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/assign | 派单(智能/手动) |

### 评价
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/evaluations | 提交评价 |
| GET | /api/evaluations/order/{orderId} | 查询评价 |

### 公告
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/announcements | 发布公告 |
| GET | /api/announcements | 公告列表 |
| GET | /api/announcements/{id} | 公告详情 |
| DELETE | /api/announcements/{id} | 删除公告 |

### 维修人员管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/repairers | 人员列表 |
| POST | /api/repairers | 新增人员 |
| PUT | /api/repairers | 编辑人员 |
| DELETE | /api/repairers/{id} | 删除人员 |

### 统计分析
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/statistics/overview | 统计概览 |

### 文件上传
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/files/upload | 上传图片 |
