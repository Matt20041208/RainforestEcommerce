# 🌿 雨林电商系统 (Rainforest Ecommerce)

> 一个功能完整的 B2C 电商平台，支持用户购物和商家销售双重角色

## 📋 项目简介

雨林电商系统是一个前后端分离的 Full-Stack 电商平台，采用 Vue 3 + Spring Boot 3 技术栈构建。系统支持普通用户购物和商家入驻销售的双重商业模式，提供了完整的商品浏览、购物车、订单管理、评论系统等功能。

## 🏗️ 项目架构

```
RainforestEcommerce/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/yulin/ecommerce/
│   │   ├── config/             # 配置类
│   │   │   ├── GlobalExceptionHandler.java    # 全局异常处理
│   │   │   ├── RedisConfig.java               # Redis 配置
│   │   │   ├── ResourceConfig.java            # 资源配置
│   │   │   ├── SecurityConfig.java            # Spring Security 配置
│   │   │   └── jwt/                           # JWT 认证
│   │   │       ├── JwtAuthenticationFilter.java
│   │   │       └── JwtUtils.java
│   │   ├── user/               # 用户模块
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── mapper/
│   │   │   ├── entity/
│   │   │   └── dto/
│   │   ├── product/            # 商品模块
│   │   ├── cart/               # 购物车模块
│   │   ├── order/              # 订单模块
│   │   ├── comment/            # 评论模块
│   │   └── address/            # 地址模块
│   ├── src/main/resources/
│   │   ├── mapper/             # MyBatis XML 映射文件
│   │   └── application.properties
│   └── uploads/                # 上传的图片文件
│
└── frontend/                    # Vue 3 前端
    ├── src/
    │   ├── components/         # Vue 组件
    │   │   ├── user/           # 用户相关组件
    │   │   ├── product/        # 商品相关组件
    │   │   ├── cart/           # 购物车组件
    │   │   ├── order/          # 订单组件
    │   │   ├── seller/         # 商家组件
    │   │   └── layout/         # 布局组件
    │   ├── router/             # 路由配置
    │   ├── utils/              # 工具类
    │   ├── App.vue
    │   └── main.js
    └── package.json
```

## 🛠️ 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.2.2 | 框架 |
| Spring Security | - | 安全框架 |
| MyBatis | 3.0.3 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | - | 缓存数据库 |
| JWT | 0.11.5 | 令牌认证 |
| PageHelper | 2.1.0 | 分页插件 |
| Lombok | - | 代码简化 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.13 | 框架 |
| Vue Router | 4.6.4 | 路由 |
| Vite | 6.0.5 | 构建工具 |

## 📦 核心功能模块

### 1. 用户模块 (User)

- **用户注册/登录**: 支持普通用户和商家两种角色注册
- **用户信息管理**: 修改个人信息、头像、性别、生日等
- **JWT 认证**: 基于 Token 的无状态认证

### 2. 商品模块 (Product)

- **商品列表**: 展示所有启用的商品，支持分页
- **商品详情**: 查看商品详细信息
- **商品分类**: 分类管理，支持多级分类
- **商品搜索**: 按名称搜索商品

### 3. 购物车模块 (Cart)

- **添加商品**: 将商品加入购物车
- **数量修改**: 支持增减商品数量
- **价格计算**: 自动计算购物车总价
- **Redis 存储**: 购物车数据持久化到 Redis

### 4. 订单模块 (Order)

- **创建订单**: 将购物车转为订单
- **订单列表**: 查看用户所有订单
- **订单状态**: 待付款、待发货、待收货、已完成等
- **商家订单管理**: 商家查看采购订单

### 5. 评论模块 (Comment)

- **商品评论**: 用户对商品进行评分和评论
- **商家回复**: 商家可回复用户评论
- **评分系统**: 1-5 星评分

### 6. 地址模块 (Address)

- **地址管理**: 增删改查收货地址
- **默认地址**: 设置默认收货地址

## 🗄️ 数据库设计

### 核心表结构

#### 1. 用户表 (user)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(255) | 密码（加密存储） |
| email | VARCHAR(100) | 邮箱 |
| phone | VARCHAR(20) | 手机号 |
| address | VARCHAR(255) | 默认地址 |
| avatar | VARCHAR(255) | 头像 URL |
| gender | INT | 性别（0未知/1男/2女） |
| birthday | DATE | 生日 |
| role | VARCHAR(20) | 角色（user/seller） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 2. 商品表 (product)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(100) | 商品名称 |
| description | TEXT | 商品描述 |
| image | VARCHAR(255) | 商品图片 |
| price | DECIMAL(10,2) | 价格 |
| stock | INT | 库存数量 |
| sales | INT | 销量 |
| enabled | BOOLEAN | 是否启用 |
| category_id | BIGINT | 分类 ID |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 3. 分类表 (category)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(50) | 分类名称 |
| description | VARCHAR(255) | 分类描述 |
| sort_order | INT | 排序 |
| enabled | BOOLEAN | 是否启用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 4. 订单表 (orders)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户 ID |
| order_type | VARCHAR(20) | 订单类型（user/seller） |
| status | VARCHAR(20) | 订单状态 |
| total_price | DECIMAL(10,2) | 订单总价 |
| create_time | DATETIME | 创建时间 |

#### 5. 订单项表 (order_item)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| order_id | BIGINT | 订单 ID |
| product_id | BIGINT | 商品 ID |
| product_name | VARCHAR(100) | 商品名称 |
| product_price | DECIMAL(10,2) | 购买价格 |
| quantity | INT | 数量 |
| subtotal | DECIMAL(10,2) | 小计 |

#### 6. 评论表 (comment)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户 ID |
| product_id | BIGINT | 商品 ID |
| rating | INT | 评分（1-5） |
| content | TEXT | 评论内容 |
| reply | TEXT | 商家回复 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 7. 地址表 (address)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户 ID |
| receiver | VARCHAR(50) | 收货人 |
| phone | VARCHAR(20) | 联系电话 |
| province | VARCHAR(50) | 省份 |
| city | VARCHAR(50) | 城市 |
| district | VARCHAR(50) | 区/县 |
| detail_address | VARCHAR(255) | 详细地址 |
| is_default | BOOLEAN | 是否默认 |

### SQL 建表语句

```sql
-- 创建数据库
CREATE DATABASE yulin_ecommerce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 用户表
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100),
    `phone` VARCHAR(20),
    `address` VARCHAR(255),
    `avatar` VARCHAR(255),
    `gender` INT DEFAULT 0,
    `birthday` DATE,
    `role` VARCHAR(20) DEFAULT 'user',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 分类表
CREATE TABLE `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255),
    `sort_order` INT DEFAULT 0,
    `enabled` BOOLEAN DEFAULT TRUE,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE `product` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `image` VARCHAR(255),
    `price` DECIMAL(10,2) NOT NULL,
    `stock` INT DEFAULT 0,
    `sales` INT DEFAULT 0,
    `enabled` BOOLEAN DEFAULT TRUE,
    `category_id` BIGINT,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`category_id`) REFERENCES `category`(`id`)
);

-- 订单表
CREATE TABLE `orders` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `order_type` VARCHAR(20) DEFAULT 'user',
    `status` VARCHAR(20) DEFAULT '待付款',
    `total_price` DECIMAL(10,2) NOT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

-- 订单项表
CREATE TABLE `order_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `product_name` VARCHAR(100),
    `product_price` DECIMAL(10,2),
    `quantity` INT NOT NULL,
    `subtotal` DECIMAL(10,2),
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)
);

-- 评论表
CREATE TABLE `comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `rating` INT NOT NULL,
    `content` TEXT,
    `reply` TEXT,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)
);

-- 地址表
CREATE TABLE `address` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `receiver` VARCHAR(50) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `province` VARCHAR(50) NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `district` VARCHAR(50) NOT NULL,
    `detail_address` VARCHAR(255) NOT NULL,
    `is_default` BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);
```

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+
- Redis

### 后端启动

1. **配置数据库**

修改 `backend/src/main/resources/application.properties`:

```properties
# MySQL配置
spring.datasource.url=jdbc:mysql://localhost:3306/yulin_ecommerce?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=你的密码

# Redis配置
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=你的密码
```

2. **创建数据库**

```sql
CREATE DATABASE yulin_ecommerce;
```

3. **启动后端**

```bash
cd backend
mvn spring-boot:run
```

后端启动后访问 `http://localhost:8080`

### 前端启动

1. **安装依赖**

```bash
cd frontend
npm install
```

2. **启动开发服务器**

```bash
npm run dev
```

3. **构建生产版本**

```bash
npm run build
```

## 🔐 安全机制

### JWT 认证流程

1. 用户登录成功后，服务端生成 JWT Token
2. Token 包含用户信息和过期时间
3. 客户端将 Token 存储在 localStorage
4. 每次请求在 Header 中携带 Token
5. 服务端验证 Token 有效性

### Spring Security 配置

- 登录/注册接口无需认证
- 商品/分类接口公开访问
- 其他接口需要携带有效 Token
- 使用 BCrypt 加密存储密码

## 📝 API 接口概览

### 用户接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |
| GET | /api/user/{username} | 获取用户信息 |
| PUT | /api/user/{userId} | 更新用户信息 |

### 商品接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/product | 获取所有商品 |
| GET | /api/product/{id} | 获取商品详情 |
| GET | /api/product/enabled | 获取启用的商品 |
| POST | /api/product | 创建商品 |
| PUT | /api/product/{id} | 更新商品 |
| DELETE | /api/product/{id} | 删除商品 |

### 分类接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/category | 获取所有分类 |
| POST | /api/category | 创建分类 |
| PUT | /api/category/{id} | 更新分类 |
| DELETE | /api/category/{id} | 删除分类 |

### 购物车接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/cart | 获取购物车 |
| POST | /api/cart/add | 添加商品 |
| PUT | /api/cart/update | 更新数量 |
| DELETE | /api/cart/{productId} | 移除商品 |
| DELETE | /api/cart/clear | 清空购物车 |

### 订单接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/order | 获取用户订单 |
| POST | /api/order | 创建订单 |
| PUT | /api/order/{id} | 更新订单状态 |

## 💡 核心设计思路

### 1. 双角色系统

系统支持普通用户和商家两种角色：
- **普通用户**: 购物、查看订单、评价商品、管理收货地址
- **商家**: 发布商品、管理分类、查看销售订单、回复评论

### 2. 购物车设计

- 购物车存储在 Redis 中，提高访问速度
- 支持商品数量修改，自动计算小计
- 页面刷新后购物车数据不丢失

### 3. 订单流程

```
加入购物车 → 确认订单 → 创建订单 → 支付 → 待发货 → 待收货 → 完成
```

### 4. 评论系统

- 用户购买后可对商品进行评分和评论
- 商家可回复用户评论
- 评论与商品关联，支持查看商品评价

## 📂 项目结构说明

```
backend/src/main/java/com/yulin/ecommerce/
├── YulinEcommerceApplication.java    # 启动类
├── config/                           # 配置
├── user/                             # 用户模块
│   ├── controller/                  # 控制层
│   ├── service/                     # 业务层
│   ├── mapper/                      # 数据访问层
│   ├── entity/                      # 实体类
│   └── dto/                         # 数据传输对象
├── product/                          # 商品模块（同上结构）
├── cart/                             # 购物车模块
├── order/                            # 订单模块
├── comment/                          # 评论模块
└── address/                          # 地址模块
```

## 🛠️ 开发建议

### 后端开发

1. **遵循 MVC 架构**: Controller → Service → Mapper
2. **使用 Lombok**: 减少样板代码
3. **事务管理**: 多表操作使用 @Transactional
4. **异常处理**: 使用全局异常处理器

### 前端开发

1. **组件化开发**: 复用 Vue 组件
2. **路由守卫**: 实现登录拦截
3. **请求封装**: 统一处理 Token 和错误
4. **响应式设计**: 适配不同设备

## 📌 注意事项

1. 数据库字符集使用 `utf8mb4` 支持 emoji
2. 生产环境需修改 JWT 密钥
3. 文件上传路径需根据实际情况配置
4. Redis 密码根据实际设置
5. 前端默认连接 localhost:8080，如需修改请修改 `request.js`

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！
