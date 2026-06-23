-- ============================================
-- 校园宿舍智能报修与服务评价系统 数据库初始化脚本
-- MySQL 8.0+
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_repair DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_repair;

-- ============================================
-- 1. 用户表（学生/管理员）
-- ============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` VARCHAR(50) NOT NULL COMMENT '学号/工号',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `real_name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色：STUDENT-学生, ADMIN-管理员',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `dormitory` VARCHAR(50) DEFAULT NULL COMMENT '宿舍楼栋',
    `room_number` VARCHAR(20) DEFAULT NULL COMMENT '宿舍号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：1-正常, 0-禁用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 维修人员表
-- ============================================
DROP TABLE IF EXISTS `repairer`;
CREATE TABLE `repairer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `job_number` VARCHAR(50) NOT NULL COMMENT '工号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `skills` VARCHAR(255) NOT NULL COMMENT '维修技能，逗号分隔：水电,木工,空调,网络,门窗',
    `area` VARCHAR(100) NOT NULL COMMENT '负责区域：东苑,西苑,南苑,北苑',
    `current_load` INT NOT NULL DEFAULT 0 COMMENT '当前负载',
    `max_load` INT NOT NULL DEFAULT 10 COMMENT '最大负载',
    `rating` DECIMAL(3,1) NOT NULL DEFAULT 5.0 COMMENT '综合评分（1-5）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-在岗, 0-离岗',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_job_number` (`job_number`),
    KEY `idx_area` (`area`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维修人员表';

-- ============================================
-- 3. 报修工单表
-- ============================================
DROP TABLE IF EXISTS `repair_order`;
CREATE TABLE `repair_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no` VARCHAR(20) NOT NULL COMMENT '报修编号：REP + yyyyMMdd + 4位序号',
    `student_id` BIGINT NOT NULL COMMENT '报修学生ID',
    `repair_type` VARCHAR(50) NOT NULL COMMENT '报修类型：水电,木工,空调,网络,门窗,其他',
    `description` VARCHAR(500) NOT NULL COMMENT '故障描述',
    `location` VARCHAR(100) NOT NULL COMMENT '报修位置（楼栋+宿舍号）',
    `images` VARCHAR(1000) DEFAULT NULL COMMENT '故障图片URL，逗号分隔',
    `repairer_id` BIGINT DEFAULT NULL COMMENT '维修人员ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '工单状态：PENDING-待派单, ASSIGNED-已派单, REPAIRING-维修中, COMPLETED-已完成, EVALUATED-已评价, CANCELLED-已取消',
    `assign_time` DATETIME DEFAULT NULL COMMENT '派单时间',
    `accept_time` DATETIME DEFAULT NULL COMMENT '接单时间',
    `complete_time` DATETIME DEFAULT NULL COMMENT '完工时间',
    `evaluate_time` DATETIME DEFAULT NULL COMMENT '评价时间',
    `repair_remark` VARCHAR(500) DEFAULT NULL COMMENT '维修备注',
    `repair_images` VARCHAR(1000) DEFAULT NULL COMMENT '维修后照片URL',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_repairer_id` (`repairer_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_repair_type` (`repair_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报修工单表';

-- ============================================
-- 4. 服务评价表
-- ============================================
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id` BIGINT NOT NULL COMMENT '关联工单ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `repairer_id` BIGINT NOT NULL COMMENT '维修人员ID',
    `rating` TINYINT NOT NULL COMMENT '星级评分（1-5）',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签，逗号分隔：服务态度好,维修速度快,技术过硬',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`),
    KEY `idx_repairer_id` (`repairer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务评价表';

-- ============================================
-- 5. 公告表
-- ============================================
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `publisher_id` BIGINT NOT NULL COMMENT '发布人ID',
    `priority` TINYINT NOT NULL DEFAULT 1 COMMENT '优先级：1-普通, 2-重要, 3-紧急',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：1-发布, 0-草稿',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ============================================
-- 6. 操作日志表
-- ============================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) NOT NULL COMMENT '操作人姓名',
    `operator_role` VARCHAR(20) NOT NULL COMMENT '操作人角色',
    `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `description` VARCHAR(500) NOT NULL COMMENT '操作描述',
    `order_id` BIGINT DEFAULT NULL COMMENT '关联工单ID',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 初始数据
-- 密码均为 123456（BCrypt加密）
-- ============================================

-- 管理员账号
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `phone`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'ADMIN', '13800000000', 1);

-- 学生账号
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `phone`, `dormitory`, `room_number`, `status`) VALUES
('2024001001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', 'STUDENT', '13800138001', '东苑1栋', '101', 1),
('2024001002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', 'STUDENT', '13800138002', '西苑2栋', '205', 1),
('2024001003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王五', 'STUDENT', '13800138003', '南苑3栋', '312', 1);

-- 维修人员账号
INSERT INTO `repairer` (`job_number`, `name`, `password`, `phone`, `skills`, `area`, `current_load`, `max_load`, `rating`, `status`) VALUES
('REP001', '赵师傅', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13900139001', '水电,门窗', '东苑', 0, 10, 4.8, 1),
('REP002', '钱师傅', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13900139002', '空调,网络', '西苑', 0, 8, 4.5, 1),
('REP003', '孙师傅', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13900139003', '木工,门窗', '南苑', 0, 10, 4.6, 1),
('REP004', '李师傅', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13900139004', '水电,网络,空调', '北苑', 0, 12, 4.9, 1);
