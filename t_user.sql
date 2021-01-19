/*
 Navicat Premium Data Transfer

 Source Server         : localhost_mysql
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : localhost:3306
 Source Schema         : movie

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 20/01/2021 00:15:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '密码',
  `enabled` tinyint(1) DEFAULT 1,
  `locked` tinyint(1) DEFAULT 0,
  `type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '数据库类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'admin', '$2a$10$IY5j3hAh.yKeAxt0Lh225.yqzf7rmJTtCPQ17f2nAIBpjrRNQnDTO', 1, 0, 'mv');
INSERT INTO `t_user` VALUES (7, 'choleen', '$2a$10$0Minrjj.Jhc4LIVqPG/1ceT.8DUEAp8Y7JrP7fkf9fy95hPCsFZiO', 1, 0, 'mv');
INSERT INTO `t_user` VALUES (8, 'root', '$2a$10$IJN5ASTC5Gdc8C7EEAjEqOUaXOJCtYqpChvct.UKhOCdbWJCOr2sG', 1, 0, 'mv');

SET FOREIGN_KEY_CHECKS = 1;
