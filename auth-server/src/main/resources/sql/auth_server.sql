/*
 Navicat Premium Dump SQL

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80033 (8.0.33)
 Source Host           : localhost:3306
 Source Schema         : auth_server

 Target Server Type    : MySQL
 Target Server Version : 80033 (8.0.33)
 File Encoding         : 65001

 Date: 01/11/2025 13:54:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for oauth2_authorization
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorization`;
CREATE TABLE `oauth2_authorization` (
    `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `registered_client_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `principal_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `authorization_grant_type` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
    `authorized_scopes` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `attributes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
    `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `authorization_code_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `authorization_code_issued_at` datetime DEFAULT NULL,
    `authorization_code_expires_at` datetime DEFAULT NULL,
    `authorization_code_metadata` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `access_token_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `access_token_issued_at` datetime DEFAULT NULL,
    `access_token_expires_at` datetime DEFAULT NULL,
    `access_token_metadata` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `access_token_type` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `access_token_scopes` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `refresh_token_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `refresh_token_issued_at` datetime DEFAULT NULL,
    `refresh_token_expires_at` datetime DEFAULT NULL,
    `refresh_token_metadata` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `oidc_id_token_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `oidc_id_token_issued_at` datetime DEFAULT NULL,
    `oidc_id_token_expires_at` datetime DEFAULT NULL,
    `oidc_id_token_metadata` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `oidc_id_token_claims` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `user_code_value` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `user_code_issued_at` datetime DEFAULT NULL,
    `user_code_expires_at` datetime DEFAULT NULL,
    `user_code_metadata` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `device_code_value` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `device_code_issued_at` datetime DEFAULT NULL,
    `device_code_expires_at` datetime DEFAULT NULL,
    `device_code_metadata` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL,
    `create_by` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` int NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for oauth2_authorization_consent
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorization_consent`;
CREATE TABLE `oauth2_authorization_consent` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `registered_client_id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
    `principal_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL,
    `authorities` varchar(1000) COLLATE utf8mb4_general_ci NOT NULL,
    `create_by` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` int NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx_registered_client_id_principal_name` (`registered_client_id`,`principal_name`,`deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for oauth2_registered_client
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_registered_client`;
CREATE TABLE `oauth2_registered_client` (
    `id` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
    `client_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端ID eg: messaging-client',
    `client_id_issued_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `client_secret` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '客户端密码',
    `client_secret_expires_at` datetime DEFAULT NULL COMMENT '客户端密码过期时间',
    `client_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端名称',
    `client_authentication_methods` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端认证方法',
    `authorization_grant_types` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端授权方式',
    `redirect_uris` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '重定向uri',
    `post_logout_redirect_uris` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '登出后重定向uri',
    `scopes` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端权限',
    `client_settings` varchar(2000) COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户端额外配置',
    `token_settings` varchar(2000) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'token配置',
    `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_client_id` (`client_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
