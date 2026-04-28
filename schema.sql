CREATE TABLE IF NOT EXISTS `user` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  openid VARCHAR(64) NOT NULL UNIQUE,
  nickname VARCHAR(64),
  avatar_url VARCHAR(255),
  city VARCHAR(64),
  points INT NOT NULL DEFAULT 0,
  lottery_times INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_openid (openid)
);

CREATE TABLE IF NOT EXISTS merchant (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  city VARCHAR(64) NOT NULL,
  category VARCHAR(64),
  description TEXT,
  logo_url VARCHAR(255),
  rating DECIMAL(3,1) DEFAULT 5.0,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_city (city),
  INDEX idx_active_city (is_active, city)
);

CREATE TABLE IF NOT EXISTS door_qr (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL UNIQUE,
  merchant_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_code (code),
  INDEX idx_merchant (merchant_id)
);

CREATE TABLE IF NOT EXISTS door_checkin (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  merchant_id BIGINT NOT NULL,
  checkin_date DATE NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_merchant_date (user_id, merchant_id, checkin_date),
  INDEX idx_user (user_id)
);

CREATE TABLE IF NOT EXISTS cashier (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  merchant_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_username (username),
  INDEX idx_merchant (merchant_id)
);

CREATE TABLE IF NOT EXISTS consume_code (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL UNIQUE,
  merchant_id BIGINT NOT NULL,
  cashier_id BIGINT NOT NULL,
  amount DECIMAL(10,2),
  status TINYINT NOT NULL DEFAULT 0 COMMENT '0=待使用 1=已核销 2=已过期',
  expired_at DATETIME NOT NULL,
  consumed_at DATETIME,
  consumed_by BIGINT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_code (code),
  INDEX idx_status_expired (status, expired_at)
);

CREATE TABLE IF NOT EXISTS points_ledger (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  delta INT NOT NULL COMMENT '正数为加分，负数为扣分',
  source VARCHAR(64) NOT NULL COMMENT 'DOOR_CHECKIN/CONSUME/LOTTERY',
  ref_id BIGINT COMMENT '关联ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_created (user_id, created_at),
  INDEX idx_user_delta_created (user_id, delta, created_at)
);

CREATE TABLE IF NOT EXISTS lottery_prize (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  type VARCHAR(32) NOT NULL COMMENT 'COUPON/POINTS/NONE',
  points_value INT DEFAULT 0,
  probability INT NOT NULL COMMENT '概率权重',
  stock INT NOT NULL DEFAULT -1 COMMENT '-1表示无限',
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_active (is_active)
);

CREATE TABLE IF NOT EXISTS coupon_code (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  prize_id BIGINT NOT NULL,
  code VARCHAR(128) NOT NULL UNIQUE,
  status TINYINT NOT NULL DEFAULT 0 COMMENT '0=未使用 1=已发放',
  issued_to BIGINT,
  issued_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_prize_status (prize_id, status)
);

CREATE TABLE IF NOT EXISTS lottery_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  prize_id BIGINT NOT NULL,
  prize_name VARCHAR(128),
  coupon_code_id BIGINT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user (user_id)
);

CREATE TABLE IF NOT EXISTS tier (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  min_points INT NOT NULL,
  max_points INT NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  INDEX idx_points (min_points, max_points)
);

CREATE TABLE IF NOT EXISTS rank_week_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  week_id VARCHAR(10) NOT NULL COMMENT 'yyyyWW格式',
  user_id BIGINT NOT NULL,
  week_points INT NOT NULL DEFAULT 0,
  rank_num INT,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_week_user (week_id, user_id),
  INDEX idx_week_points (week_id, week_points DESC)
);

CREATE TABLE IF NOT EXISTS rank_week_snapshot (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  week_id VARCHAR(10) NOT NULL,
  user_id BIGINT NOT NULL,
  week_points INT NOT NULL DEFAULT 0,
  rank_num INT NOT NULL,
  tier_name VARCHAR(64),
  snapshot_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_week_user (week_id, user_id),
  INDEX idx_week_rank (week_id, rank_num)
);
