CREATE DATABASE `ai` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;
CREATE TABLE `api_inspect_fail_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id(increment)',
  `req_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'inspect URL',
  `req_class` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '請求class name',
  `req_method` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '請求method name',
  `req_argument` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '請求參數',
  `fail_msg` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '錯誤訊息',
  `stack_trace` text CHARACTER SET utf8 COLLATE utf8_bin COMMENT 'exception stackTrace',
  `update_datetime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'create DateTime',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1678 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='api inspect fail log';
CREATE TABLE `api_inspect_status` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id(increment)',
  `success_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'inspect URL success sum of count',
  `fail_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'inspect URL fail sum of count',
  `inspect_url` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'inspect url',
  `inspect_date` date NOT NULL COMMENT 'inspect date',
  `last_resp_status` int(10) DEFAULT NULL COMMENT 'last fail msg',
  `update_datetime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'create DateTime',
  `version` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `Unique` (`inspect_url`,`inspect_date`) /*!80000 INVISIBLE */
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='api inspect status';
