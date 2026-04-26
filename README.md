# life-game-mvp-backend

生活游戏后端 MVP —— Java 17 + Spring Boot 3 + MySQL + MyBatis-Plus + JWT

---

## 目录

1. [环境要求](#环境要求)
2. [建库与执行 schema.sql](#建库与执行-schemasql)
3. [配置 application.yml](#配置-applicationyml)
4. [启动方式](#启动方式)
5. [接口测试顺序（含 curl 示例）](#接口测试顺序含-curl-示例)
6. [初始化测试数据 SQL](#初始化测试数据-sql)
7. [注意事项](#注意事项)

---

## 环境要求

| 组件 | 版本要求 |
|------|----------|
| JDK  | 17+      |
| Maven | 3.8+   |
| MySQL | 8.0+   |

---

## 建库与执行 schema.sql

```sql
-- 1. 创建数据库（UTF-8，时区 Asia/Shanghai）
CREATE DATABASE IF NOT EXISTS life_game
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE life_game;

-- 2. 执行建表脚本
SOURCE schema.sql;
```

或使用命令行：

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS life_game CHARACTER SET utf8mb4;"
mysql -u root -p life_game < schema.sql
```

执行完毕后应看到以下表：

```
user, merchant, door_qr, door_checkin, cashier,
consume_code, points_ledger, lottery_prize, coupon_code,
lottery_record, tier, rank_week_user, rank_week_snapshot
```

---

## 配置 application.yml

文件位置：`src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/life_game?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root        # 修改为实际用户名
    password: root        # 修改为实际密码

app:
  jwt:
    secret: lifegame-mvp-jwt-secret-key-must-be-at-least-256-bits-long  # 可自定义，长度 ≥ 32 字符
    expiration: 86400000  # JWT 有效期，单位毫秒，默认 24 小时
```

---

## 启动方式

```bash
# 1. 克隆代码
git clone https://github.com/sunyuxiu/life-game-mvp-backend.git
cd life-game-mvp-backend

# 2. 编译打包（跳过测试）
mvn clean package -DskipTests

# 3. 启动
java -jar target/life-game-mvp-backend-0.0.1-SNAPSHOT.jar

# 或直接通过 Maven 启动
mvn spring-boot:run
```

启动成功后监听 `http://localhost:8080`。

---

## 接口测试顺序（含 curl 示例）

> 所有需要用户鉴权的接口都需要在 Header 中携带 `Authorization: Bearer <TOKEN>`。

### 1. 用户登录（mock 微信）

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"code":"test_code_001"}'
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "openid": "openid_test_code_001"
  }
}
```

> 后续请求请将 token 保存为 `TOKEN` 变量使用：`export TOKEN=eyJhbGciOiJIUzI1NiJ9...`

---

### 2. 查看用户信息

```bash
curl http://localhost:8080/api/user/me \
  -H "Authorization: Bearer $TOKEN"
```

---

### 3. 更新城市

```bash
curl -X PUT http://localhost:8080/api/user/city \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"city":"上海"}'
```

---

### 4. 查看商家推荐列表（同城降级）

```bash
curl "http://localhost:8080/api/merchants?city=上海" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 5. 查看商家详情

```bash
curl http://localhost:8080/api/merchants/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### 6. 扫码解析

```bash
# 解析门口码
curl "http://localhost:8080/api/qr/parse?code=DOOR_merchant1" \
  -H "Authorization: Bearer $TOKEN"

# 解析消费码
curl "http://localhost:8080/api/qr/parse?code=CONSUME_xxx" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 7. 门口码打卡（每日一次，+10积分 +1抽奖次数）

```bash
curl -X POST http://localhost:8080/api/checkin/door \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"code":"DOOR_merchant1"}'
```

---

### 8. 收银端登录（获取收银员 Token）

```bash
curl -X POST http://localhost:8080/api/cashier/login \
  -H "Content-Type: application/json" \
  -d '{"username":"cashier01","password":"123456"}'
```

> 保存收银员 token：`export CASHIER_TOKEN=eyJhbGciOiJIUzI1NiJ9...`

---

### 9. 收银端生成消费码（有效期 10 分钟）

```bash
curl -X POST http://localhost:8080/api/cashier/consume-code \
  -H "Authorization: Bearer $CASHIER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"amount":99.99}'
```

---

### 10. 用户核销消费码（+50积分 +2抽奖次数）

```bash
curl -X POST http://localhost:8080/api/consume \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"code":"CONSUME_xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"}'
```

---

### 11. 抽奖

```bash
curl -X POST http://localhost:8080/api/lottery/draw \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"type":"NORMAL"}'
```

---

### 12. 查看本周排行榜

```bash
# 查看当前周排行榜（默认 top50）
curl "http://localhost:8080/api/rank/week" \
  -H "Authorization: Bearer $TOKEN"

# 查看指定周（ISO周，如202418表示2024年第18周）
curl "http://localhost:8080/api/rank/week?weekId=202418" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 13. 查看我的段位

```bash
curl http://localhost:8080/api/tier/me \
  -H "Authorization: Bearer $TOKEN"
```

返回示例：

```json
{
  "code": 200,
  "data": {
    "currentTier": "黄金段位",
    "currentPoints": 85,
    "nextTier": "铂金段位",
    "pointsNeeded": 15
  }
}
```

---

## 初始化测试数据 SQL

在执行 `schema.sql` 之后，运行以下 SQL 初始化测试数据：

```sql
USE life_game;

-- 商家数据
INSERT INTO merchant (name, city, category, description, rating) VALUES
('星巴克（上海徐汇店）', '上海', '咖啡', '精品咖啡连锁', 4.8),
('麦当劳（上海静安店）', '上海', '快餐', '全球连锁快餐', 4.5),
('奈雪的茶（北京朝阳店）', '北京', '茶饮', '精品茶饮', 4.7),
('海底捞（北京五道口店）', '北京', '火锅', '知名火锅品牌', 4.9);

-- 门口码（DOOR类型二维码）
INSERT INTO door_qr (code, merchant_id) VALUES
('DOOR_merchant1', 1),
('DOOR_merchant2', 2),
('DOOR_merchant3', 3),
('DOOR_merchant4', 4);

-- 段位数据（按本周积分区间）
INSERT INTO tier (name, min_points, max_points, sort_order) VALUES
('青铜段位',   0,   49,  1),
('白银段位',  50,   99,  2),
('黄金段位', 100,  199,  3),
('铂金段位', 200,  499,  4),
('钻石段位', 500, 9999,  5);

-- 收银员（密码明文存储，MVP阶段，生产环境需改为BCrypt）
INSERT INTO cashier (username, password, merchant_id) VALUES
('cashier01', '123456', 1),
('cashier02', '123456', 2);

-- 奖品数据
INSERT INTO lottery_prize (name, type, points_value, probability, stock) VALUES
('谢谢参与', 'NONE', 0, 50, -1),
('积分 +20', 'POINTS', 20, 30, -1),
('精品优惠券', 'COUPON', 0, 15, 100),
('大奖优惠券', 'COUPON', 0, 5, 20);

-- 优惠券库存（对应 prize_id=3 的精品优惠券）
INSERT INTO coupon_code (prize_id, code) VALUES
(3, 'COUPON-GOLD-001'),
(3, 'COUPON-GOLD-002'),
(3, 'COUPON-GOLD-003'),
(3, 'COUPON-GOLD-004'),
(3, 'COUPON-GOLD-005');

-- 大奖优惠券库存（对应 prize_id=4 的大奖优惠券）
INSERT INTO coupon_code (prize_id, code) VALUES
(4, 'COUPON-SUPER-001'),
(4, 'COUPON-SUPER-002'),
(4, 'COUPON-SUPER-003');
```

---

## 注意事项

### 1. JWT Secret 长度
`app.jwt.secret` 必须 **≥ 32 个英文字符**（即 256 位）。使用 HMAC-SHA256 签名，密钥太短会导致启动报错。可以使用以下命令生成安全密钥：

```bash
openssl rand -base64 48
```

### 2. Mock 登录说明
本项目使用 mock 微信登录：传入任意 `code`，后端将 `openid = "openid_" + code` 作为用户唯一标识，首次自动注册用户，无需真实微信接口。

**生产环境**需替换为真实微信 `code2session` 接口调用。

### 3. 时区配置
- MySQL 服务器时区需配置为 `Asia/Shanghai`（可在 MySQL 配置文件中设置 `default-time-zone='+8:00'`）
- `application.yml` 中 JDBC URL 已追加 `serverTimezone=Asia/Shanghai`
- `spring.jackson.time-zone: Asia/Shanghai` 确保返回 JSON 时间字段为北京时间

### 4. 幂等与并发安全

| 操作 | 幂等保障机制 |
|------|------------|
| 门口打卡 | `UNIQUE KEY uk_user_merchant_date (user_id, merchant_id, checkin_date)` 数据库唯一约束 |
| 消费码核销 | `UPDATE ... WHERE status=0` 原子更新，影响行数为 0 则拒绝 |
| 抽奖扣次数 | `UPDATE user SET lottery_times=lottery_times-1 WHERE id=? AND lottery_times>0` |
| 发放优惠券 | `SELECT ... FOR UPDATE` + `UPDATE status=1` 防止一码多发 |

### 5. 定时任务
定时任务需要数据库有数据才会生效：
- **每小时整点**：刷新 `rank_week_user` 表（从 `points_ledger` 汇总本周积分）
- **每周一 00:00**：固化上周排行榜至 `rank_week_snapshot` 表

如需立即触发测试，可直接调用 RankService 中的方法（开发阶段可暴露测试接口）。

### 6. 周榜口径说明
- **weekId 格式**：`yyyyWW`，如 `202418` 表示 **2024 年 ISO 第 18 周**
- **ISO 周标准**（ISO 8601）：周一为每周第一天，包含当年第一个星期四的那周为第 1 周
- **口径**：仅统计 `points_ledger.delta > 0` 的记录（扣分不计入周榜）
- **数据库查询**：使用 `YEARWEEK(created_at, 1)` 模式 1（ISO 周模式）
- **Java 计算**：使用 `WeekFields.ISO` 确保与数据库 ISO 周定义一致

### 7. 收银员密码
MVP 阶段密码明文存储（`123456`），**生产环境必须改用 BCrypt 哈希**。

### 8. 消费码有效期
消费码生成后 **10 分钟内有效**。过期后尝试核销会返回"消费码已过期"。

---

## 项目结构

```
src/main/java/com/lifegame/mvp/
├── LifeGameApplication.java      # 启动类（含 @EnableScheduling）
├── common/
│   ├── JwtUtil.java              # JWT 工具（生成/解析）
│   ├── Result.java               # 统一响应包装
│   └── UserContext.java          # ThreadLocal 存储当前用户/收银员信息
├── config/
│   ├── JwtConfig.java            # JWT 配置读取
│   ├── MybatisPlusConfig.java    # MyBatis-Plus 配置
│   └── WebConfig.java            # 拦截器注册（排除 /api/auth/login, /api/cashier/login）
├── controller/                   # RESTful 控制器（10个）
├── service/                      # 业务逻辑层（11个）
├── entity/                       # 数据库实体（13个）
├── mapper/                       # MyBatis-Plus Mapper（13个接口+2个XML）
├── dto/                          # 请求/响应 DTO
├── interceptor/
│   └── JwtInterceptor.java       # JWT 鉴权拦截器（支持 USER/CASHIER 双Token）
└── schedule/
    └── RankScheduler.java        # 定时任务（排行榜刷新+快照）
```
