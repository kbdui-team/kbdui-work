/*
 Navicat Premium Data Transfer

 Source Server         : intelligauge
 Source Server Type    : MySQL
 Source Server Version : 80035 (8.0.35)
 Source Host           : localhost:3306
 Source Schema         : intelligauge_data

 Target Server Type    : MySQL
 Target Server Version : 80035 (8.0.35)
 File Encoding         : 65001

 Date: 22/07/2025 18:52:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for answer_history
-- ----------------------------
DROP TABLE IF EXISTS `answer_history`;
CREATE TABLE `answer_history`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL,
  `question_id` int NULL DEFAULT NULL,
  `user_answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_correct` int NULL DEFAULT NULL COMMENT '是否正确',
  `answer_time` timestamp NULL DEFAULT NULL COMMENT '回答时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of answer_history
-- ----------------------------
INSERT INTO `answer_history` VALUES (1, 6, 3, 'A', 0, '2025-07-22 22:00:39');

-- ----------------------------
-- Table structure for content_input
-- ----------------------------
DROP TABLE IF EXISTS `content_input`;
CREATE TABLE `content_input`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `lecture_id` int NULL DEFAULT NULL,
  `content_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `file_size` double NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of content_input
-- ----------------------------
INSERT INTO `content_input` VALUES (1, 1, 'pdf', 'Java基础教程第一章.pdf', '/uploads/lectures/1/java_chapter1.pdf', 2048.5);
INSERT INTO `content_input` VALUES (2, 1, 'pptx', 'Java面向对象编程.pptx', '/uploads/lectures/1/java_oop.pptx', 15360);
INSERT INTO `content_input` VALUES (3, 2, 'pdf', '数据结构概述.pdf', '/uploads/lectures/2/datastructure_overview.pdf', 3072.25);
INSERT INTO `content_input` VALUES (4, 3, 'pdf', '数据库设计规范.pdf', '/uploads/lectures/3/database_design.pdf', 4096.75);
INSERT INTO `content_input` VALUES (5, 3, 'docx', 'SQL语法参考手册.docx', '/uploads/lectures/3/sql_reference.docx', 1024);

-- ----------------------------
-- Table structure for lecture_participants
-- ----------------------------
DROP TABLE IF EXISTS `lecture_participants`;
CREATE TABLE `lecture_participants`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `lecture_id` int NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `anonymous` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lecture_participants
-- ----------------------------
INSERT INTO `lecture_participants` VALUES (1, 1, 1, 'teacher', 'false');
INSERT INTO `lecture_participants` VALUES (2, 1, 3, 'student', 'false');
INSERT INTO `lecture_participants` VALUES (3, 1, 4, 'student', 'true');
INSERT INTO `lecture_participants` VALUES (4, 1, 5, 'student', 'false');
INSERT INTO `lecture_participants` VALUES (5, 2, 1, 'teacher', 'false');
INSERT INTO `lecture_participants` VALUES (6, 2, 3, 'student', 'false');
INSERT INTO `lecture_participants` VALUES (7, 2, 6, 'student', 'true');
INSERT INTO `lecture_participants` VALUES (8, 3, 2, 'teacher', 'false');
INSERT INTO `lecture_participants` VALUES (9, 3, 4, 'student', 'false');
INSERT INTO `lecture_participants` VALUES (10, 3, 5, 'student', 'false');
INSERT INTO `lecture_participants` VALUES (11, 3, 6, 'student', 'true');
INSERT INTO `lecture_participants` VALUES (12, 4, 2, 'teacher', 'false');

-- ----------------------------
-- Table structure for lectures
-- ----------------------------
DROP TABLE IF EXISTS `lectures`;
CREATE TABLE `lectures`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `teacher_id` int NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lectures
-- ----------------------------
INSERT INTO `lectures` VALUES (1, 'Java程序设计基础', '学习Java编程语言的基础知识，包括面向对象编程、异常处理、集合框架等内容', 1, 'active');
INSERT INTO `lectures` VALUES (2, '数据结构与算法', '掌握常用数据结构和算法设计与分析方法，提高编程能力和问题解决能力', 1, 'active');
INSERT INTO `lectures` VALUES (3, '数据库系统原理', '学习关系数据库理论、SQL语言、数据库设计和优化等核心概念', 2, 'active');
INSERT INTO `lectures` VALUES (4, '计算机网络', '了解计算机网络体系结构、协议栈、网络安全等重要知识点', 2, 'inactive');

-- ----------------------------
-- Table structure for question_options
-- ----------------------------
DROP TABLE IF EXISTS `question_options`;
CREATE TABLE `question_options`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_id` int NULL DEFAULT NULL,
  `option_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `option_order` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_correct` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_options
-- ----------------------------
INSERT INTO `question_options` VALUES (1, 1, 'class', 'A', 'true');
INSERT INTO `question_options` VALUES (2, 1, 'public', 'B', 'false');
INSERT INTO `question_options` VALUES (3, 1, 'static', 'C', 'false');
INSERT INTO `question_options` VALUES (4, 1, 'void', 'D', 'false');
INSERT INTO `question_options` VALUES (5, 2, 'public', 'A', 'false');
INSERT INTO `question_options` VALUES (6, 2, 'private', 'B', 'false');
INSERT INTO `question_options` VALUES (7, 2, 'protected', 'C', 'false');
INSERT INTO `question_options` VALUES (8, 2, 'friend', 'D', 'true');
INSERT INTO `question_options` VALUES (9, 3, '封装', 'A', 'false');
INSERT INTO `question_options` VALUES (10, 3, '继承', 'B', 'false');
INSERT INTO `question_options` VALUES (11, 3, '多态', 'C', 'false');
INSERT INTO `question_options` VALUES (12, 3, '重载', 'D', 'true');
INSERT INTO `question_options` VALUES (13, 4, '栈(Stack)', 'A', 'false');
INSERT INTO `question_options` VALUES (14, 4, '队列(Queue)', 'B', 'true');
INSERT INTO `question_options` VALUES (15, 4, '链表(LinkedList)', 'C', 'false');
INSERT INTO `question_options` VALUES (16, 4, '树(Tree)', 'D', 'false');
INSERT INTO `question_options` VALUES (17, 5, 'n0 = n2 - 1', 'A', 'false');
INSERT INTO `question_options` VALUES (18, 5, 'n0 = n2 + 1', 'B', 'true');
INSERT INTO `question_options` VALUES (19, 5, 'n0 = 2 * n2', 'C', 'false');
INSERT INTO `question_options` VALUES (20, 5, 'n0 = n2', 'D', 'false');
INSERT INTO `question_options` VALUES (21, 6, '每个属性都是原子的，不可再分', 'A', 'true');
INSERT INTO `question_options` VALUES (22, 6, '消除部分函数依赖', 'B', 'false');
INSERT INTO `question_options` VALUES (23, 6, '消除传递函数依赖', 'C', 'false');
INSERT INTO `question_options` VALUES (24, 6, '消除多值依赖', 'D', 'false');
INSERT INTO `question_options` VALUES (25, 7, 'INSERT', 'A', 'false');
INSERT INTO `question_options` VALUES (26, 7, 'UPDATE', 'B', 'false');
INSERT INTO `question_options` VALUES (27, 7, 'SELECT', 'C', 'true');
INSERT INTO `question_options` VALUES (28, 7, 'DELETE', 'D', 'false');

-- ----------------------------
-- Table structure for questions
-- ----------------------------
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `lecture_id` int NULL DEFAULT NULL,
  `content_input_id` int NULL DEFAULT NULL,
  `question_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `question_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of questions
-- ----------------------------
INSERT INTO `questions` VALUES (1, 1, 1, '单选题', '在Java中，以下哪个关键字用于定义类？');
INSERT INTO `questions` VALUES (2, 1, 1, '单选题', 'Java中的访问修饰符不包括以下哪一个？');
INSERT INTO `questions` VALUES (3, 1, 2, '单选题', '面向对象编程的三大特性不包括？');
INSERT INTO `questions` VALUES (4, 2, 3, '单选题', '以下哪种数据结构遵循\"先进先出\"的原则？');
INSERT INTO `questions` VALUES (5, 2, 3, '单选题', '在二叉树中，度为2的节点数为n2，叶子节点数为n0，它们的关系是？');
INSERT INTO `questions` VALUES (6, 3, 4, '单选题', '关系数据库的第一范式(1NF)要求是？');
INSERT INTO `questions` VALUES (7, 3, 5, '单选题', 'SQL中用于查询数据的关键字是？');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'teacher_zhang', '张明', 'zhang.ming@university.edu', '13812345678', 'active', 'Aaa123', 'teacher');
INSERT INTO `user` VALUES (2, 'teacher_li', '李华', 'li.hua@university.edu', '13987654321', 'active', 'Aaa123', 'teacher');
INSERT INTO `user` VALUES (3, 'student_wang', '王小明', 'wang.xiaoming@student.edu', '15612345678', 'active', 'Aaa123', 'student');
INSERT INTO `user` VALUES (4, 'student_liu', '刘小红', 'liu.xiaohong@student.edu', '15887654321', 'active', 'Aaa123', 'student');
INSERT INTO `user` VALUES (5, 'student_chen', '陈小刚', 'chen.xiaogang@student.edu', '13698745612', 'active', 'Aaa123', 'student');
INSERT INTO `user` VALUES (6, 'student_zhao', '赵小丽', 'zhao.xiaoli@student.edu', '18765432109', 'active', 'Aaa123', 'student');

-- ----------------------------
-- Table structure for user_preferences
-- ----------------------------
DROP TABLE IF EXISTS `user_preferences`;
CREATE TABLE `user_preferences`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `allow_anonymous` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_preferences
-- ----------------------------
INSERT INTO `user_preferences` VALUES (1, 1, 'true');
INSERT INTO `user_preferences` VALUES (2, 2, 'true');
INSERT INTO `user_preferences` VALUES (3, 3, 'false');
INSERT INTO `user_preferences` VALUES (4, 4, 'true');
INSERT INTO `user_preferences` VALUES (5, 5, 'false');
INSERT INTO `user_preferences` VALUES (6, 6, 'true');

SET FOREIGN_KEY_CHECKS = 1;
