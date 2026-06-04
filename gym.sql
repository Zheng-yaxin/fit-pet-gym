/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80400 (8.4.0)
 Source Host           : localhost:3306
 Source Schema         : gym

 Target Server Type    : MySQL
 Target Server Version : 80400 (8.4.0)
 File Encoding         : 65001

 Date: 22/05/2026 11:04:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gym_body_image
-- ----------------------------
DROP TABLE IF EXISTS `gym_body_image`;
CREATE TABLE `gym_body_image`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `image_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片地址',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `deleted` int NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员身材照片记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_body_image
-- ----------------------------
INSERT INTO `gym_body_image` VALUES (1, 1, 'http://localhost:8080/files/2053d2d20e764e1f9fa7d84b47dcf955_test.png', '2025-12-11 17:09:09', NULL, 1);
INSERT INTO `gym_body_image` VALUES (2, 1, 'http://localhost:8080/files/b95da56912d148378c84d11d8365062e_D26BAFEAED55DF97803268FF2AC339D4.jpg', '2025-12-11 18:41:30', NULL, 1);
INSERT INTO `gym_body_image` VALUES (3, 1, 'http://localhost:8080/files/f3071adf6b8b40d7a9dc89dbd91db8a7_32B6EE80C651BEDD50AB217455CCB4AB.jpg', '2025-12-11 19:19:04', NULL, 1);
INSERT INTO `gym_body_image` VALUES (4, 1, 'http://localhost:8080/files/7dd3c9bbf56344aa8dc1ddf10e652202_D26BAFEAED55DF97803268FF2AC339D4.jpg', '2025-12-11 19:19:31', NULL, 1);
INSERT INTO `gym_body_image` VALUES (5, 1, 'http://localhost:8080/files/991e40c53e584dd486e6b60da9da0bad_D26BAFEAED55DF97803268FF2AC339D4.jpg', '2025-12-11 19:34:12', NULL, 0);
INSERT INTO `gym_body_image` VALUES (6, 1, 'http://localhost:8080/files/cf2d7f563a324617af457ca1e41ad9a6_10667F553F38CD737D1D5A78D1C798C2.jpg', '2025-12-11 19:34:27', NULL, 0);
INSERT INTO `gym_body_image` VALUES (7, 1, 'http://localhost:8080/files/315a2264cc974ff5b0101c1b8c7356c8_10667F553F38CD737D1D5A78D1C798C2.jpg', '2025-12-30 18:39:54', '2025-12-30 10:39:55', 0);

-- ----------------------------
-- Table structure for gym_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `gym_chat_message`;
CREATE TABLE `gym_chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `sender_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送者角色 (MEMBER/COACH)',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `receiver_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收者角色 (MEMBER/COACH)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '聊天内容',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读 (0未读 1已读)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sender`(`sender_id` ASC, `sender_role` ASC) USING BTREE,
  INDEX `idx_receiver`(`receiver_id` ASC, `receiver_role` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_chat_message
-- ----------------------------
INSERT INTO `gym_chat_message` VALUES (1, 1, 'MEMBER', 3, 'COACH', '你好教练', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (2, 1, 'MEMBER', 1, 'COACH', '111', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (3, 1, 'MEMBER', 1, 'COACH', '11', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (4, 1, 'MEMBER', 1, 'COACH', '1', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (5, 1, 'MEMBER', 1, 'COACH', '你好', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (6, 1, 'MEMBER', 1, 'COACH', '1', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (7, 1, 'MEMBER', 2, 'COACH', '11', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (8, 1, 'MEMBER', 2, 'COACH', '1', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (9, 1, 'MEMBER', 1, 'COACH', 'hello', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (10, 1, 'MEMBER', 1, 'COACH', '11', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (11, 1, 'MEMBER', 1, 'COACH', '11', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (12, 1, 'MEMBER', 2, 'COACH', '1', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (13, 1, 'MEMBER', 2, 'COACH', '1', 0, '2025-12-25 23:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (14, 1, 'MEMBER', 2, 'COACH', '1', 0, NULL, 0);
INSERT INTO `gym_chat_message` VALUES (15, 1, 'MEMBER', 1, 'COACH', '2', 0, '2025-12-25 23:47:46', 0);
INSERT INTO `gym_chat_message` VALUES (16, 1, 'COACH', 1, 'MEMBER', '你好', 0, '2025-12-25 23:55:39', 0);
INSERT INTO `gym_chat_message` VALUES (17, 1, 'MEMBER', 1, 'COACH', '11', 0, '2025-12-29 10:06:32', 0);
INSERT INTO `gym_chat_message` VALUES (18, 1, 'MEMBER', 1, 'COACH', 'sb', 0, '2025-12-29 10:06:47', 0);
INSERT INTO `gym_chat_message` VALUES (19, 1, 'MEMBER', 2, 'COACH', 'hello', 0, '2025-12-29 10:07:01', 0);
INSERT INTO `gym_chat_message` VALUES (20, 1, 'MEMBER', 1, 'COACH', '11', 0, '2025-12-29 15:55:11', 0);
INSERT INTO `gym_chat_message` VALUES (21, 1, 'MEMBER', 1, 'COACH', 'hello', 0, '2025-12-29 22:45:32', 0);
INSERT INTO `gym_chat_message` VALUES (22, 1, 'COACH', 1, 'MEMBER', '你好', 0, '2025-12-30 10:48:03', 0);

-- ----------------------------
-- Table structure for gym_class_schedule
-- ----------------------------
DROP TABLE IF EXISTS `gym_class_schedule`;
CREATE TABLE `gym_class_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排课ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `class_time` datetime NOT NULL COMMENT '上课时间',
  `day_of_week` int NULL DEFAULT NULL COMMENT '星期几（1-7，1表示周一，7表示周日）',
  `start_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开始时间（格式：HH:mm，如 09:00）',
  `end_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '结束时间（格式：HH:mm，如 10:30）',
  `effective_date` date NULL DEFAULT NULL COMMENT '生效日期（从哪天开始生效）',
  `expiry_date` date NULL DEFAULT NULL COMMENT '失效日期（到哪天结束，null表示长期有效）',
  `capacity` int NULL DEFAULT 20 COMMENT '最大容量',
  `enrolled_count` int NULL DEFAULT 0 COMMENT '已报名人数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_class_time`(`class_time` ASC) USING BTREE,
  INDEX `idx_day_of_week`(`day_of_week` ASC) USING BTREE,
  INDEX `idx_effective_date`(`effective_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程排课表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_class_schedule
-- ----------------------------
INSERT INTO `gym_class_schedule` VALUES (1, 1, 1, '2026-01-01 09:00:00', 1, '09:00', '10:00', '2026-01-01', '2026-12-31', 20, 0, '2025-12-28 19:58:23', '2025-12-28 19:58:23', 0);
INSERT INTO `gym_class_schedule` VALUES (3, 2, 2, '2026-01-01 08:00:00', 2, '08:00', '09:00', '2026-01-01', '2026-12-31', 15, 1, '2025-12-28 19:58:23', '2025-12-28 19:58:23', 0);
INSERT INTO `gym_class_schedule` VALUES (5, 3, 3, '2026-01-01 09:00:00', 3, '09:00', '09:45', '2026-01-01', '2026-12-31', 20, 0, '2025-12-28 19:58:23', '2025-12-28 19:58:23', 0);
INSERT INTO `gym_class_schedule` VALUES (7, 4, 4, '2026-01-01 18:00:00', 4, '18:00', '19:00', '2025-08-17', '2026-12-30', 15, 0, '2025-12-28 19:58:23', '2025-12-28 20:40:45', 0);
INSERT INTO `gym_class_schedule` VALUES (9, 5, 5, '2026-01-01 10:00:00', 5, '10:00', '10:50', '2026-01-01', '2026-12-31', 12, 0, '2025-12-28 19:58:23', '2025-12-28 19:58:23', 0);

-- ----------------------------
-- Table structure for gym_coach
-- ----------------------------
DROP TABLE IF EXISTS `gym_coach`;
CREATE TABLE `gym_coach`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教练ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教练姓名',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号(登录账号)',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码',
  `gender` int NULL DEFAULT 0 COMMENT '性别 (0女 1男 2未知)',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `bio` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人简介',
  `specialties` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专长领域(如: 瑜伽,HIIT,力量训练)',
  `experience_years` int NULL DEFAULT 0 COMMENT '从业年限',
  `certification` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资格证书',
  `hourly_rate` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '私教课时费(元/小时)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE COMMENT '手机号唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健身教练表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_coach
-- ----------------------------
INSERT INTO `gym_coach` VALUES (1, 'Tony', '13800001111', '$2a$10$ig1iDlKSp49WKfQbbRi3z.StfTCcyt/o/sam1HARp5aqA81KjX4fa', 1, NULL, '资深健身教练，擅长HIIT和力量训练', 'HIIT,力量训练,减脂', 5, '国家健身教练资格证', 200.00, '0', 0, '2025-12-24 00:00:00', '2025-12-24 00:00:00');
INSERT INTO `gym_coach` VALUES (2, 'Lisa', '13800002222', '$2a$10$ig1iDlKSp49WKfQbbRi3z.StfTCcyt/o/sam1HARp5aqA81KjX4fa', 0, NULL, '瑜伽导师，注重身心平衡', '瑜伽,普拉提,拉伸', 8, '国际瑜伽联盟认证', 180.00, '0', 0, '2025-12-24 00:00:00', '2025-12-24 00:00:00');
INSERT INTO `gym_coach` VALUES (3, 'Mike', '13800003333', '$2a$10$ig1iDlKSp49WKfQbbRi3z.StfTCcyt/o/sam1HARp5aqA81KjX4fa', 1, NULL, '专业健美教练，擅长增肌塑形', '增肌,健美,力量训练', 6, '国家一级健身教练', 220.00, '0', 0, '2025-12-24 00:00:00', '2025-12-24 00:00:00');
INSERT INTO `gym_coach` VALUES (4, 'David', '13800004444', '$2a$10$ig1iDlKSp49WKfQbbRi3z.StfTCcyt/o/sam1HARp5aqA81KjX4fa', 1, NULL, NULL, '综合格斗,拳击,体能训练', 0, NULL, 250.00, '0', 0, '2025-12-28 19:58:23', '2025-12-28 19:58:23');
INSERT INTO `gym_coach` VALUES (5, 'Sarah', '13800005555', '$2a$10$ig1iDlKSp49WKfQbbRi3z.StfTCcyt/o/sam1HARp5aqA81KjX4fa', 0, NULL, NULL, '尊巴,爵士舞,有氧操', 0, NULL, 200.00, '0', 0, '2025-12-28 19:58:23', '2025-12-28 19:58:23');

-- ----------------------------
-- Table structure for gym_course
-- ----------------------------
DROP TABLE IF EXISTS `gym_course`;
CREATE TABLE `gym_course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程描述',
  `duration` int NULL DEFAULT 60 COMMENT '课程时长(分钟)',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '课程价格',
  `image_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程图片',
  `max_participants` int NULL DEFAULT 20 COMMENT '最大参与人数',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健身课程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course
-- ----------------------------
INSERT INTO `gym_course` VALUES (1, '减脂HIIT', '高强度间歇训练，快速燃脂', 60, 0.00, NULL, 15, '0', '2025-12-12 22:30:46', '2025-12-30 14:50:06', 0);
INSERT INTO `gym_course` VALUES (2, '流瑜伽', '身心舒展，放松身体', 60, 10.00, NULL, 10, '0', '2025-12-12 22:30:46', '2025-12-12 23:16:28', 0);
INSERT INTO `gym_course` VALUES (3, '动感单车', '有氧燃脂，增强心肺功能', 45, 0.00, NULL, 20, '0', '2025-12-12 22:30:46', '2025-12-12 22:30:46', 0);
INSERT INTO `gym_course` VALUES (4, '力量训练', '增肌塑形，提升力量', 60, 50.00, NULL, 8, '0', '2025-12-12 22:30:46', '2025-12-12 22:30:46', 0);
INSERT INTO `gym_course` VALUES (5, '普拉提', '核心训练，改善体态', 50, 50.00, NULL, 12, '0', '2025-12-12 22:30:46', '2025-12-23 20:47:22', 0);

-- ----------------------------
-- Table structure for gym_course_backup
-- ----------------------------
DROP TABLE IF EXISTS `gym_course_backup`;
CREATE TABLE `gym_course_backup`  (
  `id` bigint NOT NULL DEFAULT 0 COMMENT '课程ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `coach_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教练姓名',
  `location` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上课地点(如: 瑜伽房A)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `max_people` int NULL DEFAULT 20 COMMENT '最大人数',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '课程价格(0为免费团课)',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course_backup
-- ----------------------------
INSERT INTO `gym_course_backup` VALUES (1, '减脂HIIT', 'Tony', '一号操房', '2025-12-10 18:00:00', '2025-12-10 19:00:00', 15, 0.00, '高强度间歇训练', '2025-12-09 11:52:46', 0);
INSERT INTO `gym_course_backup` VALUES (2, '流瑜伽', 'Lisa', '二号瑜伽室', '2025-12-11 19:00:00', '2025-12-11 20:00:00', 10, 0.00, '身心舒展', '2025-12-09 11:52:46', 0);

-- ----------------------------
-- Table structure for gym_course_enrollment
-- ----------------------------
DROP TABLE IF EXISTS `gym_course_enrollment`;
CREATE TABLE `gym_course_enrollment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `schedule_id` bigint NOT NULL COMMENT '排课ID',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0已报名 1已取消 2已完成)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_member_schedule`(`member_id` ASC, `schedule_id` ASC, `deleted` ASC) USING BTREE,
  INDEX `idx_member_id`(`member_id` ASC) USING BTREE,
  INDEX `idx_schedule_id`(`schedule_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程预约表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course_enrollment
-- ----------------------------
INSERT INTO `gym_course_enrollment` VALUES (1, 2, 1, '1', '2025-12-12 22:43:11', '2025-12-12 22:43:23', 0);
INSERT INTO `gym_course_enrollment` VALUES (2, 2, 7, '2', '2025-12-12 22:43:31', '2025-12-12 22:44:54', 0);
INSERT INTO `gym_course_enrollment` VALUES (3, 2, 9, '2', '2025-12-12 22:53:40', '2025-12-22 09:52:20', 0);
INSERT INTO `gym_course_enrollment` VALUES (7, 2, 3, '0', '2025-12-12 23:11:34', '2025-12-12 23:11:34', 0);
INSERT INTO `gym_course_enrollment` VALUES (8, 2, 5, '0', '2025-12-12 23:11:36', '2025-12-12 23:11:36', 0);
INSERT INTO `gym_course_enrollment` VALUES (17, 3, 7, '2', '2025-12-12 23:15:16', '2025-12-27 23:48:39', 0);
INSERT INTO `gym_course_enrollment` VALUES (18, 3, 5, '0', '2025-12-12 23:15:17', '2025-12-12 23:15:17', 0);
INSERT INTO `gym_course_enrollment` VALUES (19, 3, 3, '2', '2025-12-12 23:15:20', '2025-12-23 19:24:41', 0);
INSERT INTO `gym_course_enrollment` VALUES (20, 3, 1, '2', '2025-12-12 23:15:22', '2025-12-22 17:15:45', 0);
INSERT INTO `gym_course_enrollment` VALUES (22, 1, 1, '1', '2025-12-22 00:22:47', '2025-12-22 00:22:54', 0);
INSERT INTO `gym_course_enrollment` VALUES (23, 1, 7, '1', '2025-12-22 08:37:07', '2025-12-29 09:33:11', 0);
INSERT INTO `gym_course_enrollment` VALUES (24, 1, 5, '0', '2025-12-22 23:19:37', '2025-12-22 23:19:37', 0);
INSERT INTO `gym_course_enrollment` VALUES (28, 1, 9, '1', '2025-12-23 12:55:56', '2025-12-29 23:17:57', 0);
INSERT INTO `gym_course_enrollment` VALUES (33, 1, 3, '0', '2025-12-29 19:37:43', '2025-12-29 19:37:43', 0);

-- ----------------------------
-- Table structure for gym_course_legacy
-- ----------------------------
DROP TABLE IF EXISTS `gym_course_legacy`;
CREATE TABLE `gym_course_legacy`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `coach_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教练姓名',
  `location` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上课地点(如: 瑜伽房A)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `max_people` int NULL DEFAULT 20 COMMENT '最大人数',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '课程价格(0为免费团课)',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健身课程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course_legacy
-- ----------------------------
INSERT INTO `gym_course_legacy` VALUES (1, '减脂HIIT', 'Tony', '一号操房', '2025-12-10 18:00:00', '2025-12-10 19:00:00', 15, 0.00, '高强度间歇训练', '2025-12-09 11:52:46', 0);
INSERT INTO `gym_course_legacy` VALUES (2, '流瑜伽', 'Lisa', '二号瑜伽室', '2025-12-11 19:00:00', '2025-12-11 20:00:00', 10, 0.00, '身心舒展', '2025-12-09 11:52:46', 0);

-- ----------------------------
-- Table structure for gym_course_record
-- ----------------------------
DROP TABLE IF EXISTS `gym_course_record`;
CREATE TABLE `gym_course_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态 (0:已预约, 1:已签到, 2:已取消)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
  `checkin_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_course`(`member_id` ASC, `course_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程预约记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course_record
-- ----------------------------

-- ----------------------------
-- Table structure for gym_course_template
-- ----------------------------
DROP TABLE IF EXISTS `gym_course_template`;
CREATE TABLE `gym_course_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `coach_id` bigint NOT NULL COMMENT '固定授课教练ID',
  `week_days` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '星期几上课(1-7,逗号分隔,1=周一)',
  `start_time` time NOT NULL COMMENT '开始时间',
  `capacity` int NULL DEFAULT 20 COMMENT '课程容量',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0启用 1停用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程模板表-周期性排课' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_course_template
-- ----------------------------
INSERT INTO `gym_course_template` VALUES (1, 1, 1, '1,3,5', '18:00:00', 15, '0', '2025-12-28 00:48:03', '2025-12-28 00:48:03', 0);
INSERT INTO `gym_course_template` VALUES (2, 2, 2, '2,4', '19:00:00', 10, '0', '2025-12-28 00:48:03', '2025-12-28 00:48:03', 0);

-- ----------------------------
-- Table structure for gym_diet_log
-- ----------------------------
DROP TABLE IF EXISTS `gym_diet_log`;
CREATE TABLE `gym_diet_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `food_id` bigint NOT NULL COMMENT '食物ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '摄入量(g)',
  `meal_type` int NOT NULL COMMENT '餐别 (1:早餐, 2:午餐, 3:晚餐, 4:加餐)',
  `eat_date` date NOT NULL COMMENT '进食日期',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员饮食记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_diet_log
-- ----------------------------
INSERT INTO `gym_diet_log` VALUES (1, 1, 11, 110.00, 1, '2025-12-09', NULL);
INSERT INTO `gym_diet_log` VALUES (2, 1, 12, 100.00, 2, '2025-12-11', NULL);
INSERT INTO `gym_diet_log` VALUES (3, 1, 13, 100.00, 4, '2025-12-11', NULL);
INSERT INTO `gym_diet_log` VALUES (4, 1, 1, 400.00, 2, '2025-12-11', NULL);
INSERT INTO `gym_diet_log` VALUES (5, 1, 9, 400.00, 2, '2025-12-11', NULL);
INSERT INTO `gym_diet_log` VALUES (8, 1, 12, 180.00, 1, '2025-12-11', NULL);
INSERT INTO `gym_diet_log` VALUES (9, 1, 32, 300.00, 3, '2025-12-11', NULL);
INSERT INTO `gym_diet_log` VALUES (10, 1, 33, 50.00, 1, '2025-12-11', NULL);
INSERT INTO `gym_diet_log` VALUES (11, 1, 34, 67.00, 2, '2025-12-11', NULL);
INSERT INTO `gym_diet_log` VALUES (12, 1, 27, 200.00, 1, '2025-12-29', '2025-12-29 15:54:36');
INSERT INTO `gym_diet_log` VALUES (13, 1, 32, 200.00, 3, '2025-12-29', '2025-12-29 16:48:35');
INSERT INTO `gym_diet_log` VALUES (14, 1, 35, 50.00, 4, '2025-12-29', '2025-12-29 23:19:28');
INSERT INTO `gym_diet_log` VALUES (15, 1, 11, 200.00, 1, '2025-12-30', '2025-12-30 09:27:15');
INSERT INTO `gym_diet_log` VALUES (17, 1, 35, 100.00, 2, '2025-12-30', '2025-12-30 10:33:10');
INSERT INTO `gym_diet_log` VALUES (18, 1, 20, 100.00, 2, '2025-12-30', '2025-12-30 10:35:56');
INSERT INTO `gym_diet_log` VALUES (20, 1, 34, 300.00, 2, '2025-12-30', '2025-12-30 10:39:32');
INSERT INTO `gym_diet_log` VALUES (21, 1, 8, 250.00, 3, '2025-12-30', '2025-12-30 15:08:32');

-- ----------------------------
-- Table structure for gym_entry_log
-- ----------------------------
DROP TABLE IF EXISTS `gym_entry_log`;
CREATE TABLE `gym_entry_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `member_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员姓名(冗余字段)',
  `entry_time` datetime NOT NULL COMMENT '入场时间',
  `exit_time` datetime NULL DEFAULT NULL COMMENT '出场时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_entry_time`(`entry_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '进出场记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_entry_log
-- ----------------------------
INSERT INTO `gym_entry_log` VALUES (1, 1, '张三', '2025-12-08 09:00:00', '2025-12-08 10:30:00', 0);
INSERT INTO `gym_entry_log` VALUES (2, 2, '李四', '2025-12-08 10:15:00', NULL, 0);
INSERT INTO `gym_entry_log` VALUES (3, 4, '赵六', '2025-12-08 14:00:00', NULL, 0);

-- ----------------------------
-- Table structure for gym_equipment
-- ----------------------------
DROP TABLE IF EXISTS `gym_equipment`;
CREATE TABLE `gym_equipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '器材ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '器材名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '器材编号',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID（关联器材分类表）',
  `brand` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌',
  `model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '器材型号',
  `image_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '器材图片URL',
  `buy_date` date NULL DEFAULT NULL COMMENT '购买日期',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '购买价格',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '放置位置',
  `current_manager_id` bigint NULL DEFAULT NULL COMMENT '当前负责人ID（关联管理员/员工表）',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态（0正常 1维护中 2损坏 3报废）',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category_id` ASC) USING BTREE COMMENT '分类索引',
  INDEX `idx_code`(`code` ASC) USING BTREE,
  INDEX `idx_brand`(`brand` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健身器材表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_equipment
-- ----------------------------
INSERT INTO `gym_equipment` VALUES (1, '跑步机', 'EQ-001', 1, 'LifeFitness', 'T5', 'http://localhost:8080/files/1c7809e38fce4ba1b02a148859577b82.png', '2022-01-01', 5000.00, '有氧区', 1, 0, 0, '正常运行', '2025-12-08 00:01:43', '2025-12-30 16:52:04');
INSERT INTO `gym_equipment` VALUES (2, '动感单车', 'EQ-002', 1, 'Schwinn', 'IC4', 'http://localhost:8080/files/2df377949bc441eb9c74280448823f5f.jpg', '2022-03-15', 3000.00, '单车房', 1, 0, 0, '踏板松动，维护中', '2025-12-08 00:01:43', '2025-12-30 16:59:25');
INSERT INTO `gym_equipment` VALUES (3, '哑铃组', 'EQ-003', 2, 'Rogue', 'Rubber Hex', 'http://localhost:8080/files/6ee842d6f5e848aab07f14a2d08d0b86.jpg', '2021-06-20', 2000.00, '力量区', 1, 0, 0, '正常', '2025-12-08 00:01:43', '2025-12-30 16:59:33');
INSERT INTO `gym_equipment` VALUES (4, '蝴蝶机', 'EQ-004', 2, 'Matrix', 'G3', 'http://localhost:8080/files/77bbec9f12e94798921274b8b9e3977f.jpg', '2020-11-11', 8000.00, '力量区', 1, 2, 0, '钢索断裂，待维修', '2025-12-08 00:01:43', '2025-12-30 16:59:42');
INSERT INTO `gym_equipment` VALUES (5, '划船机', 'EQ-005', 1, 'Concept2', 'Model D', 'http://localhost:8080/files/42634aa2e5a348c9bef4730552378ac0.jpg', '2023-02-01', 9000.00, '有氧区', 1, 2, 0, '显示屏故障，待维修', '2025-12-08 00:01:43', '2025-12-30 16:59:50');
INSERT INTO `gym_equipment` VALUES (18, 'test', '11', 1, '1', '1', NULL, NULL, NULL, '1', NULL, 1, 1, '1', NULL, NULL);
INSERT INTO `gym_equipment` VALUES (19, '汉堡', '11', 1, NULL, NULL, '上传成功', NULL, NULL, '11', NULL, 0, 1, NULL, '2025-12-30 16:40:22', '2025-12-30 16:45:50');
INSERT INTO `gym_equipment` VALUES (20, '汉堡', '999', 2, NULL, NULL, 'http://localhost:8080/files/d5e076047db84cf18dd645f1f6329c11.jpg', NULL, NULL, '111', NULL, 0, 1, NULL, '2025-12-30 16:46:05', '2025-12-30 16:59:51');

-- ----------------------------
-- Table structure for gym_equipment_category
-- ----------------------------
DROP TABLE IF EXISTS `gym_equipment_category`;
CREATE TABLE `gym_equipment_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID（0为顶级分类）',
  `sort` int NULL DEFAULT 0 COMMENT '排序值（升序）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '分类描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除（0-未删 1-已删）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '器材分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_equipment_category
-- ----------------------------
INSERT INTO `gym_equipment_category` VALUES (1, '有氧器械', 0, 1, '提高心肺功能的器械', '2025-12-22 09:20:19', NULL, 0);
INSERT INTO `gym_equipment_category` VALUES (2, '力量器械', 0, 2, '增肌塑形的器械', '2025-12-22 09:20:19', NULL, 0);

-- ----------------------------
-- Table structure for gym_equipment_check
-- ----------------------------
DROP TABLE IF EXISTS `gym_equipment_check`;
CREATE TABLE `gym_equipment_check`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '巡检ID',
  `equipment_id` bigint NOT NULL COMMENT '器材ID（关联gym_equipment）',
  `checker_id` bigint NOT NULL COMMENT '巡检人ID（关联员工表）',
  `check_time` datetime NOT NULL COMMENT '巡检时间',
  `result` tinyint(1) NOT NULL COMMENT '巡检结果（0正常 1异常）',
  `abnormal_desc` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '异常描述（result=1时必填）',
  `suggestion` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理建议',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除（0未删 1已删）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_equipment`(`equipment_id` ASC) USING BTREE COMMENT '器材索引',
  INDEX `idx_check_time`(`check_time` ASC) USING BTREE COMMENT '巡检时间索引',
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '器材巡检记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_equipment_check
-- ----------------------------

-- ----------------------------
-- Table structure for gym_exercise
-- ----------------------------
DROP TABLE IF EXISTS `gym_exercise`;
CREATE TABLE `gym_exercise`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `target_muscle` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `equipment` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `difficulty` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `image_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `video_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `primary_muscle` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `secondary_muscles` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `movement_pattern` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `steps` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `common_mistakes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `alternatives` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `media_license` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `tips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gym_exercise
-- ----------------------------

-- ----------------------------
-- Table structure for gym_exercise_alternative
-- ----------------------------
DROP TABLE IF EXISTS `gym_exercise_alternative`;
CREATE TABLE `gym_exercise_alternative`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exercise_id` bigint NOT NULL,
  `alternative_exercise_id` bigint NOT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exercise_id`(`exercise_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gym_exercise_alternative
-- ----------------------------

-- ----------------------------
-- Table structure for gym_food
-- ----------------------------
DROP TABLE IF EXISTS `gym_food`;
CREATE TABLE `gym_food`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '食物名称',
  `calories` decimal(10, 2) NOT NULL COMMENT '热量(kcal/100g)',
  `protein` decimal(10, 2) NOT NULL COMMENT '蛋白质(g/100g)',
  `fat` decimal(10, 2) NOT NULL COMMENT '脂肪(g/100g)',
  `carbohydrate` decimal(10, 2) NOT NULL COMMENT '碳水(g/100g)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '常见食物库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_food
-- ----------------------------
INSERT INTO `gym_food` VALUES (1, '米饭(蒸)', 116.00, 2.60, 0.30, 25.90);
INSERT INTO `gym_food` VALUES (2, '糙米饭', 111.00, 2.60, 0.90, 23.00);
INSERT INTO `gym_food` VALUES (3, '燕麦片(干)', 377.00, 15.00, 6.70, 61.60);
INSERT INTO `gym_food` VALUES (4, '全麦面包', 246.00, 8.50, 3.40, 44.20);
INSERT INTO `gym_food` VALUES (5, '馒头', 223.00, 7.00, 1.10, 47.00);
INSERT INTO `gym_food` VALUES (6, '面条(煮)', 109.00, 2.70, 0.20, 24.30);
INSERT INTO `gym_food` VALUES (7, '红薯(蒸)', 86.00, 1.60, 0.10, 20.10);
INSERT INTO `gym_food` VALUES (8, '土豆(蒸)', 69.00, 1.70, 0.10, 15.30);
INSERT INTO `gym_food` VALUES (9, '甜玉米(煮)', 106.00, 4.00, 1.20, 22.80);
INSERT INTO `gym_food` VALUES (10, '小米粥', 46.00, 1.40, 0.70, 8.40);
INSERT INTO `gym_food` VALUES (11, '鸡胸肉(生)', 133.00, 19.40, 5.00, 2.50);
INSERT INTO `gym_food` VALUES (12, '鸡蛋(全蛋)', 147.00, 12.70, 9.00, 1.50);
INSERT INTO `gym_food` VALUES (13, '蛋白(煮)', 52.00, 10.90, 0.20, 0.70);
INSERT INTO `gym_food` VALUES (14, '瘦牛肉', 106.00, 20.20, 2.30, 1.20);
INSERT INTO `gym_food` VALUES (15, '猪里脊肉', 155.00, 20.40, 7.90, 0.00);
INSERT INTO `gym_food` VALUES (16, '三文鱼', 139.00, 19.80, 6.30, 0.00);
INSERT INTO `gym_food` VALUES (17, '虾仁(熟)', 93.00, 22.60, 0.20, 0.20);
INSERT INTO `gym_food` VALUES (18, '纯牛奶', 65.00, 3.00, 3.20, 3.40);
INSERT INTO `gym_food` VALUES (19, '无糖酸奶', 62.00, 3.20, 3.00, 5.00);
INSERT INTO `gym_food` VALUES (20, '北豆腐', 116.00, 12.20, 4.80, 4.20);
INSERT INTO `gym_food` VALUES (21, '西兰花(煮)', 34.00, 4.10, 0.60, 4.30);
INSERT INTO `gym_food` VALUES (22, '菠菜(煮)', 23.00, 2.90, 0.40, 3.60);
INSERT INTO `gym_food` VALUES (23, '生菜', 15.00, 1.40, 0.20, 2.90);
INSERT INTO `gym_food` VALUES (24, '西红柿', 18.00, 0.90, 0.20, 3.90);
INSERT INTO `gym_food` VALUES (25, '黄瓜', 16.00, 0.80, 0.10, 2.90);
INSERT INTO `gym_food` VALUES (26, '苹果', 53.00, 0.40, 0.20, 13.70);
INSERT INTO `gym_food` VALUES (27, '香蕉', 93.00, 1.40, 0.20, 22.00);
INSERT INTO `gym_food` VALUES (28, '橙子', 47.00, 0.90, 0.10, 11.80);
INSERT INTO `gym_food` VALUES (29, '蓝莓', 57.00, 0.70, 0.30, 14.50);
INSERT INTO `gym_food` VALUES (30, '杏仁(原味)', 578.00, 21.20, 49.90, 21.60);
INSERT INTO `gym_food` VALUES (31, '红油抄手', 247.00, 8.60, 9.10, 46.90);
INSERT INTO `gym_food` VALUES (32, '双层芝士汉堡', 260.00, 18.00, 17.00, 45.00);
INSERT INTO `gym_food` VALUES (33, '蒸包', 70.00, 6.50, 1.80, 34.00);
INSERT INTO `gym_food` VALUES (34, '煎蛋', 143.00, 13.60, 11.80, 1.40);
INSERT INTO `gym_food` VALUES (35, '包子', 364.00, 9.70, 8.60, 52.20);
INSERT INTO `gym_food` VALUES (36, '回锅肉', 100.00, 2.00, 2.00, 2.00);

-- ----------------------------
-- Table structure for gym_health_data
-- ----------------------------
DROP TABLE IF EXISTS `gym_health_data`;
CREATE TABLE `gym_health_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `gender` int NULL DEFAULT 1 COMMENT '性别 (0:女, 1:男)',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `height` decimal(10, 2) NULL DEFAULT NULL COMMENT '身高 (cm)',
  `weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '体重 (kg)',
  `bmi` decimal(10, 2) NULL DEFAULT NULL COMMENT 'BMI指数',
  `body_fat_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT '体脂率 (%)',
  `measure_time` datetime NULL DEFAULT NULL COMMENT '测量时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` int NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员健康数据' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_health_data
-- ----------------------------
INSERT INTO `gym_health_data` VALUES (1, 1, 1, NULL, 180.00, 80.00, 24.70, 18.00, '2025-12-10 04:55:48', NULL, NULL, 0);
INSERT INTO `gym_health_data` VALUES (2, 1, 1, NULL, 180.00, 83.00, 25.60, NULL, '2025-12-10 23:37:28', NULL, NULL, 0);
INSERT INTO `gym_health_data` VALUES (3, 1, 1, NULL, 183.00, 83.00, 24.80, 20.00, '2025-12-11 17:11:14', NULL, NULL, 0);
INSERT INTO `gym_health_data` VALUES (4, 1, 1, NULL, 183.00, 54.00, 16.10, 16.00, '2025-12-11 17:50:11', NULL, NULL, 0);
INSERT INTO `gym_health_data` VALUES (5, 1, 1, NULL, 183.00, 80.00, 23.90, 20.00, '2025-12-29 23:54:51', '2025-12-29 15:54:51', '2025-12-29 15:54:51', 0);
INSERT INTO `gym_health_data` VALUES (6, 1, 1, NULL, 185.00, 81.00, 23.70, 21.00, '2025-12-30 17:39:12', '2025-12-30 09:39:13', '2025-12-30 09:39:13', 0);

-- ----------------------------
-- Table structure for gym_member
-- ----------------------------
DROP TABLE IF EXISTS `gym_member`;
CREATE TABLE `gym_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会员ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会员姓名(真名)',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号(登录账号)',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码',
  `gender` int NULL DEFAULT 0 COMMENT '性别 (0女 1男 2未知)',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `card_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员卡号',
  `card_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '年卡' COMMENT '卡类型',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '余额',
  `points` int NULL DEFAULT 0 COMMENT '积分',
  `join_date` date NULL DEFAULT NULL COMMENT '入会时间',
  `expire_date` date NULL DEFAULT NULL COMMENT '到期时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE COMMENT '手机号唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健身房会员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_member
-- ----------------------------
INSERT INTO `gym_member` VALUES (1, '郑雅心', '123456', '$2a$10$ig1iDlKSp49WKfQbbRi3z.StfTCcyt/o/sam1HARp5aqA81KjX4fa', 0, NULL, NULL, '8000008541', '月卡', 2082.00, 0, '2025-12-23', '2030-12-23', '0', 0, NULL, '2025-12-30 10:47:19');
INSERT INTO `gym_member` VALUES (2, '唐儿子', '18908337812', '$2a$10$y2kS6zrdGnZDxK9mkDxVP.ccdjO6WgJDtciUZBCcAUUVdfmKiFPHi', 1, NULL, NULL, '8000165644', '年卡', 300.00, 0, '2025-12-28', '2027-12-28', '0', 0, NULL, '2025-12-29 16:50:04');
INSERT INTO `gym_member` VALUES (3, 'yang', '18908331234', '$2a$10$Rc509W3r4RstEGyynPFcteIzarJ6bId2VHLU.kqZFQWnd.MvAU9JS', 1, NULL, NULL, '8000056945', '年卡', 1000.00, 0, '2025-12-28', '2027-12-28', '0', 0, NULL, '2025-12-28 00:19:12');
INSERT INTO `gym_member` VALUES (4, '杨', '123', '$2a$10$qyeKvnvG2ozMZoa9yrxk6.5j6URfLtNo419k2RZSv6tb3aQAw2zLe', 1, NULL, NULL, NULL, '年卡', 0.00, 0, NULL, NULL, '0', 0, '2025-12-29 16:31:22', '2025-12-30 15:00:50');

-- ----------------------------
-- Table structure for gym_member_card
-- ----------------------------
DROP TABLE IF EXISTS `gym_member_card`;
CREATE TABLE `gym_member_card`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `card_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员卡号',
  `card_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '卡类型(年卡/月卡/次卡)',
  `issue_date` datetime NULL DEFAULT NULL COMMENT '发卡日期',
  `expire_date` datetime NULL DEFAULT NULL COMMENT '过期日期',
  `remaining_times` int NULL DEFAULT NULL COMMENT '剩余次数(次卡专用)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0:正常, 1:挂失, 2:过期)',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除(0:未删, 1:已删)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `suspend_count` int NULL DEFAULT 0 COMMENT '当年已停卡次数',
  `suspend_year` int NULL DEFAULT NULL COMMENT '停卡计数年份',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员卡表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_member_card
-- ----------------------------
INSERT INTO `gym_member_card` VALUES (1, 1, 'NO.888888', '年卡', '2025-12-23 00:09:59', '2026-12-23 00:09:59', 0, '0', 0, '2025-12-23 00:09:59', '2025-12-23 00:09:59', 0, NULL);
INSERT INTO `gym_member_card` VALUES (3, 1, '8000822112', '次卡', '2025-12-23 00:13:55', '2026-12-23 00:13:55', 10, '0', 0, NULL, NULL, 0, NULL);
INSERT INTO `gym_member_card` VALUES (4, 1, '8000306298', '年卡', '2025-12-23 11:04:35', '2026-12-23 11:04:35', NULL, '0', 0, NULL, NULL, 0, NULL);
INSERT INTO `gym_member_card` VALUES (5, 1, '8000339309', '年卡', '2025-12-23 11:23:06', '2026-12-23 11:23:06', NULL, '0', 0, NULL, NULL, 0, 2025);
INSERT INTO `gym_member_card` VALUES (6, 1, '8000121609', '年卡', '2025-12-23 12:56:44', '2030-12-23 12:56:44', NULL, '0', 0, NULL, '2025-12-29 16:16:14', 0, 2025);
INSERT INTO `gym_member_card` VALUES (7, 1, '8000008541', '月卡', '2025-12-23 23:45:32', '2026-05-23 23:45:32', NULL, '0', 0, NULL, '2025-12-27 23:06:35', 0, 2025);
INSERT INTO `gym_member_card` VALUES (8, 2, '8000441068', '月卡', '2025-12-24 23:43:34', '2026-01-24 23:43:34', NULL, '0', 0, NULL, NULL, 0, 2025);
INSERT INTO `gym_member_card` VALUES (9, 2, '8000165644', '年卡', '2025-12-28 00:19:42', '2027-12-28 00:19:42', NULL, '0', 0, '2025-12-28 00:19:42', '2025-12-28 00:19:42', 0, 2025);
INSERT INTO `gym_member_card` VALUES (10, 3, '8000056945', '年卡', '2025-12-28 00:19:58', '2027-12-28 00:19:58', NULL, '0', 0, '2025-12-28 00:19:58', '2025-12-28 00:19:58', 0, 2025);

-- ----------------------------
-- Table structure for gym_member_transaction
-- ----------------------------
DROP TABLE IF EXISTS `gym_member_transaction`;
CREATE TABLE `gym_member_transaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `transaction_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易类型(充值/消费/退款)',
  `amount` decimal(10, 2) NOT NULL COMMENT '交易金额',
  `balance_after` decimal(10, 2) NULL DEFAULT NULL COMMENT '交易后余额',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `operator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `card_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员卡号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员交易流水表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_member_transaction
-- ----------------------------
INSERT INTO `gym_member_transaction` VALUES (1, 1, '充值', 100.00, NULL, '用户自助充值', 'system', '2025-12-23 00:12:20', NULL);
INSERT INTO `gym_member_transaction` VALUES (2, 1, '充值', 1000.00, NULL, '用户自助充值', 'system', '2025-12-23 00:12:25', NULL);
INSERT INTO `gym_member_transaction` VALUES (3, 1, '充值', 100.00, NULL, '用户自助充值', 'system', '2025-12-23 00:12:28', NULL);
INSERT INTO `gym_member_transaction` VALUES (4, 1, '办卡', 100.00, NULL, '', 'system', '2025-12-23 00:13:55', '8000822112');
INSERT INTO `gym_member_transaction` VALUES (5, 1, '办卡', 1000.00, NULL, '', 'system', '2025-12-23 11:04:35', '8000306298');
INSERT INTO `gym_member_transaction` VALUES (6, 1, '办卡', 1000.00, NULL, '', 'system', '2025-12-23 11:23:06', '8000339309');
INSERT INTO `gym_member_transaction` VALUES (7, 1, '办卡', 1000.00, NULL, '', 'system', '2025-12-23 12:56:44', '8000121609');
INSERT INTO `gym_member_transaction` VALUES (9, 1, '消费', 50.00, 1250.00, '预约课程：力量训练', 'system', '2025-12-23 13:21:23', NULL);
INSERT INTO `gym_member_transaction` VALUES (10, 1, '消费', 10.00, 1240.00, '预约课程：流瑜伽', 'system', '2025-12-23 19:21:24', NULL);
INSERT INTO `gym_member_transaction` VALUES (11, 1, '退款', 100.00, 1340.00, '取消预约退款：soccer', 'system', '2025-12-23 23:45:12', NULL);
INSERT INTO `gym_member_transaction` VALUES (12, 1, '续费', 1000.00, NULL, '续费年卡', 'system', '2025-12-23 23:45:25', '8000121609');
INSERT INTO `gym_member_transaction` VALUES (13, 1, '办卡', 200.00, NULL, '新办月卡', 'system', '2025-12-23 23:45:32', '8000008541');
INSERT INTO `gym_member_transaction` VALUES (14, 1, '续费', 200.00, NULL, '续费月卡', 'system', '2025-12-23 23:45:39', '8000008541');
INSERT INTO `gym_member_transaction` VALUES (15, 1, '续费', 2000.00, NULL, '续费年卡', 'system', '2025-12-23 23:45:43', '8000121609');
INSERT INTO `gym_member_transaction` VALUES (16, 1, '消费', 100.00, 1240.00, '预约课程：soccer', 'system', '2025-12-23 23:45:59', NULL);
INSERT INTO `gym_member_transaction` VALUES (17, 1, '消费', 10.00, 1230.00, '预约课程：流瑜伽', 'system', '2025-12-24 22:59:27', NULL);
INSERT INTO `gym_member_transaction` VALUES (18, 2, '充值', 200.00, NULL, '', 'system', '2025-12-24 23:11:45', NULL);
INSERT INTO `gym_member_transaction` VALUES (19, 2, '办卡', 1.00, NULL, '新办月卡', 'system', '2025-12-24 23:43:34', '8000441068');
INSERT INTO `gym_member_transaction` VALUES (20, 1, '退款', 100.00, 1330.00, '取消预约退款：soccer', 'system', '2025-12-25 12:42:50', NULL);
INSERT INTO `gym_member_transaction` VALUES (21, 1, '消费', 200.00, NULL, '预约私教课程 - Tony 2025-12-26 11:00', 'system', '2025-12-25 13:33:37', NULL);
INSERT INTO `gym_member_transaction` VALUES (22, 1, '消费', 200.00, NULL, '预约私教课程 - Tony 2025-12-28 15:00', 'system', '2025-12-27 23:06:09', NULL);
INSERT INTO `gym_member_transaction` VALUES (23, 1, '续费', 100.00, NULL, '续费月卡', 'system', '2025-12-27 23:06:35', '8000008541');
INSERT INTO `gym_member_transaction` VALUES (24, 1, '退款', 200.00, NULL, '取消私教预约退款', 'system', '2025-12-27 23:11:45', NULL);
INSERT INTO `gym_member_transaction` VALUES (25, 3, '充值', 1000.00, NULL, '', 'system', '2025-12-28 00:19:12', NULL);
INSERT INTO `gym_member_transaction` VALUES (26, 2, '办卡', 1111.00, NULL, '新办年卡', 'system', '2025-12-28 00:19:42', '8000165644');
INSERT INTO `gym_member_transaction` VALUES (27, 3, '办卡', 1000.00, NULL, '新办年卡', 'system', '2025-12-28 00:19:58', '8000056945');
INSERT INTO `gym_member_transaction` VALUES (28, 1, '消费', 50.00, 1080.00, '预约课程：力量训练', 'system', '2025-12-28 19:59:52', NULL);
INSERT INTO `gym_member_transaction` VALUES (29, 1, '充值', 500.00, NULL, 'iOS Re', 'system', '2025-12-28 23:38:17', NULL);
INSERT INTO `gym_member_transaction` VALUES (30, 1, '退款', 50.00, 1630.00, '取消预约退款：力量训练', 'system', '2025-12-29 09:33:11', NULL);
INSERT INTO `gym_member_transaction` VALUES (31, 1, '消费', 100.00, 1530.00, '预约课程：11', 'system', '2025-12-29 09:33:14', NULL);
INSERT INTO `gym_member_transaction` VALUES (32, 1, '续费', 1000.00, NULL, '续费年卡', 'system', '2025-12-29 16:16:14', '8000121609');
INSERT INTO `gym_member_transaction` VALUES (33, 1, '充值', 1000.00, NULL, 'iOS Re', 'system', '2025-12-29 16:16:19', NULL);
INSERT INTO `gym_member_transaction` VALUES (34, 1, '消费', 88.00, 2442.00, '预约课程：test123', 'system', '2025-12-29 16:16:55', NULL);
INSERT INTO `gym_member_transaction` VALUES (35, 2, '充值', 100.00, NULL, '后台充值', 'system', '2025-12-29 16:50:04', NULL);
INSERT INTO `gym_member_transaction` VALUES (36, 1, '消费', 10.00, 2432.00, '预约课程：流瑜伽', 'system', '2025-12-29 19:37:43', NULL);
INSERT INTO `gym_member_transaction` VALUES (37, 1, '消费', 200.00, NULL, '预约私教课程 - Tony 2026-01-22 09:00', 'system', '2025-12-29 22:45:48', NULL);
INSERT INTO `gym_member_transaction` VALUES (38, 1, '退款', 50.00, 2282.00, '取消预约退款：普拉提', 'system', '2025-12-29 23:17:57', NULL);
INSERT INTO `gym_member_transaction` VALUES (39, 1, '消费', 200.00, NULL, '预约私教课程 - Tony 2025-12-31 13:00', 'system', '2025-12-30 10:47:19', NULL);
INSERT INTO `gym_member_transaction` VALUES (40, 2, '续费', 1000.00, NULL, '续费年卡', 'system', '2025-12-30 11:44:26', '8000165644');

-- ----------------------------
-- Table structure for gym_payment_log
-- ----------------------------
DROP TABLE IF EXISTS `gym_payment_log`;
CREATE TABLE `gym_payment_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `type` tinyint(1) NOT NULL COMMENT '类型 (1:充值, 2:消费)',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注 (如: 购买私教课, 充值活动)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资金流水表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_payment_log
-- ----------------------------

-- ----------------------------
-- Table structure for gym_personal_training_booking
-- ----------------------------
DROP TABLE IF EXISTS `gym_personal_training_booking`;
CREATE TABLE `gym_personal_training_booking`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `slot_id` bigint NOT NULL COMMENT '时间段ID',
  `date` date NOT NULL COMMENT '预约日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `amount` decimal(10, 2) NOT NULL COMMENT '费用金额',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态 (0:待确认, 1:已确认, 2:已完成, 3:已取消)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_id`(`member_id` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_slot_id`(`slot_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '私教预约记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_personal_training_booking
-- ----------------------------
INSERT INTO `gym_personal_training_booking` VALUES (1, 1, 1, 0, '2025-12-26', '11:00:00', '12:00:00', 200.00, 2, NULL, 0, NULL, '2025-12-25 23:55:43');
INSERT INTO `gym_personal_training_booking` VALUES (2, 1, 1, 0, '2025-12-28', '15:00:00', '16:00:00', 200.00, 3, NULL, 0, '2025-12-27 23:06:09', '2025-12-27 23:06:09');
INSERT INTO `gym_personal_training_booking` VALUES (3, 1, 1, 0, '2026-01-22', '09:00:00', '10:00:00', 200.00, 0, NULL, 0, '2025-12-29 22:45:48', '2025-12-29 22:45:48');
INSERT INTO `gym_personal_training_booking` VALUES (4, 1, 1, 0, '2025-12-31', '13:00:00', '14:00:00', 200.00, 0, NULL, 0, '2025-12-30 10:47:19', '2025-12-30 10:47:19');

-- ----------------------------
-- Table structure for gym_personal_training_slot
-- ----------------------------
DROP TABLE IF EXISTS `gym_personal_training_slot`;
CREATE TABLE `gym_personal_training_slot`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '时间段ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `date` date NOT NULL COMMENT '日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态 (0:可预约, 1:已预约, 2:已完成, 3:已取消)',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coach_date`(`coach_id` ASC, `date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '私教可预约时间段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_personal_training_slot
-- ----------------------------
INSERT INTO `gym_personal_training_slot` VALUES (1, 1, '2025-12-25', '09:00:00', '10:00:00', 0, 0, '2025-12-24 00:00:00', '2025-12-24 00:00:00');
INSERT INTO `gym_personal_training_slot` VALUES (2, 1, '2025-12-25', '10:00:00', '11:00:00', 0, 0, '2025-12-24 00:00:00', '2025-12-24 00:00:00');
INSERT INTO `gym_personal_training_slot` VALUES (3, 1, '2025-12-25', '14:00:00', '15:00:00', 0, 0, '2025-12-24 00:00:00', '2025-12-24 00:00:00');
INSERT INTO `gym_personal_training_slot` VALUES (4, 2, '2025-12-25', '15:00:00', '16:00:00', 0, 0, '2025-12-24 00:00:00', '2025-12-24 00:00:00');
INSERT INTO `gym_personal_training_slot` VALUES (5, 2, '2025-12-25', '16:00:00', '17:00:00', 0, 0, '2025-12-24 00:00:00', '2025-12-24 00:00:00');
INSERT INTO `gym_personal_training_slot` VALUES (6, 1, '2025-12-29', '07:00:00', '10:00:00', 2, 0, '2025-12-28 00:16:59', '2025-12-28 00:16:59');
INSERT INTO `gym_personal_training_slot` VALUES (7, 1, '2025-12-30', '06:00:00', '22:00:00', 2, 0, '2025-12-28 00:17:13', '2025-12-28 00:17:13');

-- ----------------------------
-- Table structure for gym_repair_log
-- ----------------------------
DROP TABLE IF EXISTS `gym_repair_log`;
CREATE TABLE `gym_repair_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报修ID',
  `equipment_id` bigint NOT NULL COMMENT '器材ID（关联gym_equipment）',
  `reporter_id` bigint NOT NULL COMMENT '报修人ID（可关联会员/员工表）',
  `report_time` datetime NOT NULL COMMENT '报修时间',
  `fault_desc` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '故障描述',
  `repairer_id` bigint NULL DEFAULT NULL COMMENT '维修人员ID（关联员工表）',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '维修状态（0待处理 1维修中 2已完成 3已取消）',
  `repair_time` datetime NULL DEFAULT NULL COMMENT '维修完成时间',
  `cost` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '维修费用',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注（如维修方案）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除（0未删 1已删）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_equipment`(`equipment_id` ASC) USING BTREE COMMENT '器材索引',
  INDEX `idx_status`(`status` ASC) USING BTREE COMMENT '状态索引',
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '器材报修维修记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gym_repair_log
-- ----------------------------
INSERT INTO `gym_repair_log` VALUES (5, 1, 1, '2025-12-23 14:41:53', '11', NULL, 2, '2025-12-23 20:46:52', 0.00, 'test', NULL, NULL, 0);

-- ----------------------------
-- Table structure for gym_training_checkin
-- ----------------------------
DROP TABLE IF EXISTS `gym_training_checkin`;
CREATE TABLE `gym_training_checkin`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `plan_id` bigint NULL DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NULL DEFAULT NULL,
  `duration_minutes` int NULL DEFAULT NULL,
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_id`(`member_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gym_training_checkin
-- ----------------------------
INSERT INTO `gym_training_checkin` VALUES (1, 1, NULL, '2026-05-20 22:20:39', NULL, NULL, '0', '2026-05-20 22:20:39');
INSERT INTO `gym_training_checkin` VALUES (2, 1, NULL, '2026-05-20 22:20:42', NULL, NULL, '0', '2026-05-20 22:20:42');

-- ----------------------------
-- Table structure for gym_training_log
-- ----------------------------
DROP TABLE IF EXISTS `gym_training_log`;
CREATE TABLE `gym_training_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `plan_id` bigint NULL DEFAULT NULL,
  `training_date` datetime NOT NULL,
  `duration_minutes` int NULL DEFAULT NULL,
  `intensity` int NULL DEFAULT NULL,
  `calories_burned` int NULL DEFAULT NULL,
  `feeling` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_date`(`member_id` ASC, `training_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gym_training_log
-- ----------------------------
INSERT INTO `gym_training_log` VALUES (1, 1, NULL, '2026-05-20 22:13:47', 60, 6, 250, '', NULL, '2026-05-20 22:13:47', 0);

-- ----------------------------
-- Table structure for gym_training_log_item
-- ----------------------------
DROP TABLE IF EXISTS `gym_training_log_item`;
CREATE TABLE `gym_training_log_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `log_id` bigint NOT NULL,
  `exercise_id` bigint NULL DEFAULT NULL,
  `weight` decimal(10, 2) NULL DEFAULT NULL,
  `reps` int NULL DEFAULT NULL,
  `sets` int NULL DEFAULT NULL,
  `completed` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_log_id`(`log_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gym_training_log_item
-- ----------------------------

-- ----------------------------
-- Table structure for gym_training_plan
-- ----------------------------
DROP TABLE IF EXISTS `gym_training_plan`;
CREATE TABLE `gym_training_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `goal` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `weekly_frequency` int NULL DEFAULT 3,
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'SYSTEM',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_id`(`member_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gym_training_plan
-- ----------------------------
INSERT INTO `gym_training_plan` VALUES (1, 1, '减脂塑形', 3, 'SYSTEM', '0', '2026-05-20 22:13:40', '2026-05-20 22:13:40', 0);
INSERT INTO `gym_training_plan` VALUES (2, 1, '提升体能', 4, 'SYSTEM', '0', '2026-05-21 11:16:33', '2026-05-21 11:16:33', 0);
INSERT INTO `gym_training_plan` VALUES (3, 1, '增肌力量', 4, 'SYSTEM', '0', '2026-05-21 11:16:38', '2026-05-21 11:16:38', 0);

-- ----------------------------
-- Table structure for gym_training_plan_day
-- ----------------------------
DROP TABLE IF EXISTS `gym_training_plan_day`;
CREATE TABLE `gym_training_plan_day`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_id` bigint NOT NULL,
  `day_index` int NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `target_muscle` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `estimated_minutes` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gym_training_plan_day
-- ----------------------------

-- ----------------------------
-- Table structure for gym_training_plan_item
-- ----------------------------
DROP TABLE IF EXISTS `gym_training_plan_item`;
CREATE TABLE `gym_training_plan_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_day_id` bigint NOT NULL,
  `exercise_id` bigint NULL DEFAULT NULL,
  `sets` int NULL DEFAULT NULL,
  `reps` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `rest_seconds` int NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_day_id`(`plan_day_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gym_training_plan_item
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由参数',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2037 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码 (BCrypt加密)',
  `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除（0未删除 1已删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$ig1iDlKSp49WKfQbbRi3z.StfTCcyt/o/sam1HARp5aqA81KjX4fa', '系统管理员', '', '', '0', '', '0', 0, '', NULL, '', '2025-12-08 00:01:43', '', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
