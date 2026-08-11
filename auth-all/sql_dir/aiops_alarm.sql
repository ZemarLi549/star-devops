-- MySQL dump 10.14  Distrib 5.5.68-MariaDB, for Linux (x86_64)
--
-- Host: 172.30.94.79    Database: aiops_alarm
-- ------------------------------------------------------
-- Server version	5.7.25-28-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alarm_engine`
--

DROP TABLE IF EXISTS `alarm_engine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alarm_engine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `node` varchar(100) NOT NULL,
  `clock` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alarm_engine`
--

LOCK TABLES `alarm_engine` WRITE;
/*!40000 ALTER TABLE `alarm_engine` DISABLE KEYS */;
INSERT INTO `alarm_engine` VALUES (3,'172.30.94.78:19000',1749193190004),(15,'10.5.170.143:19000',1749193190018);
/*!40000 ALTER TABLE `alarm_engine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assign_group`
--

DROP TABLE IF EXISTS `assign_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assign_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assign_id` bigint(20) DEFAULT NULL COMMENT '分派策略ID',
  `group_id` bigint(20) DEFAULT NULL COMMENT '分派通知组ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assign_group`
--

LOCK TABLES `assign_group` WRITE;
/*!40000 ALTER TABLE `assign_group` DISABLE KEYS */;
INSERT INTO `assign_group` VALUES (1,1,1,'2025-06-03 06:27:35','2025-06-03 06:27:35'),(3,3,3,'2025-06-06 06:30:02','2025-06-06 06:30:02');
/*!40000 ALTER TABLE `assign_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assign_policy`
--

DROP TABLE IF EXISTS `assign_policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assign_policy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分派策略ID',
  `workspace_id` bigint(20) NOT NULL COMMENT '工作空间ID',
  `status` tinyint(1) NOT NULL,
  `assign_name` varchar(32) NOT NULL,
  `assign_condition` text NOT NULL COMMENT '分派条件',
  `assign_aggregate` text NOT NULL COMMENT '告警聚合',
  `remark` text COMMENT '分派策略备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(32) NOT NULL,
  `updater` varchar(32) NOT NULL,
  `notify_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assign_policy`
--

LOCK TABLES `assign_policy` WRITE;
/*!40000 ALTER TABLE `assign_policy` DISABLE KEYS */;
INSERT INTO `assign_policy` VALUES (1,3,1,'飞书','[{\"dataGroupId\":5,\"items\":[{\"condition\":\"等于\",\"name\":\"level\",\"values\":[\"1\"]},{\"condition\":\"等于\",\"name\":\"source\",\"values\":[\"0\"]}],\"relation\":1}]','{\"aggregate\":true,\"labelKey\":\"level\",\"period\":1}','','2025-06-03 06:27:35','2025-06-03 06:27:35','admin','admin',1),(3,1,1,'测试','[{\"dataGroupId\":2,\"items\":[],\"relation\":1}]','{\"aggregate\":true,\"labelKey\":\"source\",\"period\":60}','','2025-06-06 06:30:02','2025-06-06 06:30:02','admin','admin',3);
/*!40000 ALTER TABLE `assign_policy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `block_rule`
--

DROP TABLE IF EXISTS `block_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `block_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) NOT NULL COMMENT '状态 1启用 0禁用',
  `workspace_id` bigint(20) NOT NULL,
  `rule_name` varchar(100) NOT NULL,
  `block_condition` text NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `notify_time` varchar(256) NOT NULL,
  `remark` varchar(256) DEFAULT NULL,
  `creator` varchar(32) NOT NULL,
  `updater` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `block_rule`
--

LOCK TABLES `block_rule` WRITE;
/*!40000 ALTER TABLE `block_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `block_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chain_alarm_rule`
--

DROP TABLE IF EXISTS `chain_alarm_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chain_alarm_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `alarm_rule_name_zh` varchar(100) NOT NULL COMMENT '告警规则名称',
  `alarm_rule_name` varchar(50) NOT NULL COMMENT '告警规则唯一标识',
  `metrics_name` varchar(50) NOT NULL COMMENT '指标唯一标识',
  `application_name` varchar(100) DEFAULT NULL COMMENT '告警规则作用的服务名称',
  `instance_name` varchar(100) DEFAULT NULL COMMENT '告警规则作用的实例名称',
  `endpoint_name` varchar(100) DEFAULT NULL COMMENT '告警规则作用的接口名称',
  `alarm_type` varchar(10) DEFAULT NULL COMMENT '被告警的对象类型',
  `threshold` varchar(50) NOT NULL COMMENT '阈值',
  `unit` varchar(10) NOT NULL COMMENT '单位',
  `op` varchar(10) NOT NULL COMMENT '操作符',
  `alarm_count` int(11) NOT NULL COMMENT '触发次数',
  `alarm_period` int(11) NOT NULL COMMENT '时间窗口，单位分钟',
  `alarm_silence_status` tinyint(1) DEFAULT '0',
  `alarm_silence_period` int(11) DEFAULT NULL COMMENT '静默时间窗口，单位分钟',
  `message` varchar(500) NOT NULL COMMENT '告警信息',
  `alarm_level` varchar(20) NOT NULL COMMENT '告警级别',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active_status` tinyint(1) DEFAULT '1' COMMENT '启动状态',
  `update_account` varchar(20) DEFAULT NULL COMMENT '更新人',
  `workspace_id` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `chain_alarm_rule_alarm_rule_name_uindex` (`alarm_rule_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chain_alarm_rule`
--

LOCK TABLES `chain_alarm_rule` WRITE;
/*!40000 ALTER TABLE `chain_alarm_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `chain_alarm_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chain_metrics`
--

DROP TABLE IF EXISTS `chain_metrics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chain_metrics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `metrics_name_zh` varchar(50) NOT NULL COMMENT '指标中文名称',
  `metrics_name` varchar(50) NOT NULL COMMENT '指标英文名称',
  `scope` varchar(30) NOT NULL,
  `functions` varchar(10) NOT NULL,
  `unit` varchar(10) DEFAULT NULL,
  `meaning` varchar(255) DEFAULT NULL,
  `disabled` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chain_metrics`
--

LOCK TABLES `chain_metrics` WRITE;
/*!40000 ALTER TABLE `chain_metrics` DISABLE KEYS */;
INSERT INTO `chain_metrics` VALUES (6,'服务平均响应时间 (毫秒)\n','service_resp_time','应用','longAvg','ms','服务在某单位时间内平均响应时间',0),(8,'服务成功率(%)','service_sla','应用','percent','%','服务在某单位时间请求响应成功占总请求的百分比',0),(10,'服务请求量(次/分)','service_cpm','应用','cpm','次','服务在某单位时间内请求次数',0),(12,'服务响应时间分位数(毫秒)','service_percentile','应用','percentile','ms','服务在某时间单位内所有的请求按照响应时间从小到达排列后分成100组，分别在第50,75,90,95,99组的响应时间大小（百分位数是一种分位数，通过细分为 100 组获得。第 25 个百分位数也称为第一个四分位数 （Q1），第 50 个百分位数称为中位数或第二个四分位数 （Q2），第 75 个百分位数称为第三个四分位数 （Q3）。）',0),(14,'服务健康度','service_apdex','应用','apdex','-','服务在单位时间内的健康度(健康度是应用程序性能指数，是由公司联盟开发的开放标准，用于衡量计算中软件应用程序的性能。其目的是通过指定一种统一的方式来分析和报告测量的性能满足用户期望的程度，从而将测量结果转换为有关用户满意度的见解。)',0),(16,'消息队列消费数','service_mq_consume_count','应用','count','次','请求类型是消息队列的服务在单位时间内的请求次数',0),(18,'消息队列平均消费耗时(毫秒)','service_mq_consume_latency','应用','longAvg','ms','请求类型是消息队列的服务在单位时间内平均消费耗时(毫秒)',0),(20,'客户端请求量(次/分)','service_relation_client_cpm','应用关系','cpm','次','客户端请求量(次/分)',1),(22,'服务端请求量(次/分)','service_relation_server_cpm','应用关系','cpm','次','服务端请求量(次/分)',1),(24,'客户端成功率','service_relation_client_call_sla','应用关系','percent','%','客户端成功率',1),(26,'服务端成功率','service_relation_server_call_sla','应用关系','percent','%','服务端成功率',1),(28,'客户端平均响应时间','service_relation_client_resp_time','应用关系','longAvg','ms','客户端平均响应时间',1),(30,'服务端平均响应时间','service_relation_server_resp_time','应用关系','longAvg','ms','服务端平均响应时间',1),(32,'客户端响应时间分位数（毫秒）','service_relation_client_percentile','应用关系','percentile','ms','客户端响应时间分位数（毫秒）',1),(34,'服务端响应时间分位数（毫秒）','service_relation_server_percentile','应用关系','percentile','ms','服务端响应时间分位数（毫秒）',1),(36,'客户端服务实例请求量（次/分）','service_instance_relation_client_cpm','实例关系','cpm','次','客户端服务实例请求量（次/分）',1),(38,'服务端服务实例请求量（次/分）','service_instance_relation_server_cpm','实例关系','cpm','次','服务端服务实例请求量（次/分）',1),(40,'客户端服务实例成功率','service_instance_relation_client_call_sla','实例关系','percent','%','客户端服务实例成功率',1),(42,'服务端服务实例成功率','service_instance_relation_server_call_sla','实例关系','percent','%','服务端服务实例成功率',1),(44,'客户端服务实例平均响应时间','service_instance_relation_client_resp_time','实例关系','longAvg()','ms','客户端服务实例平均响应时间',1),(46,'服务端服务实例平均响应时间','service_instance_relation_server_resp_time','实例关系','longAvg()','ms','服务端服务实例平均响应时间',1),(48,'客户端服务实例响应时间分位数（毫秒）','service_instance_relation_client_percentile','实例关系','percentile','ms','客户端服务实例响应时间分位数（毫秒）',1),(50,'服务端服务实例响应时间分位数（毫秒）','service_instance_relation_server_percentile','实例关系','percentile','ms','服务端服务实例响应时间分位数（毫秒）',1),(52,'服务实例成功率(%)','service_instance_sla','实例','percent','%','服务实例在某单位时间内请求响应成功占总请求的百分比',0),(54,'服务实例平均响应时间(毫秒)','service_instance_resp_time','实例','longAvg()','ms','服务实例在某单位时间内平均响应时间(单位毫秒)',0),(56,'服务实例请求量（次/分）','service_instance_cpm','实例','cpm','次','服务实例在某单位时间内请求次数',0),(58,'接口请求量（次/分）','endpoint_cpm','接口','cpm','次','接口在某单位时间内请求次数',0),(60,'接口响应时间分位数（毫秒）','endpoint_percentile','接口','percentile','ms','接口在某时间单位内所有的请求按照响应时间从小到达排列后分成100组，分别在第50,75,90,95,99组的响应时间大小（百分位数是一种分位数，通过细分为 100 组获得。第 25 个百分位数也称为第一个四分位数 （Q1），第 50 个百分位数称为中位数或第二个四分位数 （Q2），第 75 个百分位数称为第三个四分位数 （Q3）。）',0),(62,'服务端接口rpc请求响应分位数（毫秒）','endpoint_relation_percentile','接口关系','percentile','ms','服务端接口rpc请求响应分位数（毫秒）',1),(64,'接口平均响应时间 (毫秒)\n','endpoint_resp_time','接口','longAvg','ms','接口在某单位时间内平均响应时间',0),(66,'接口成功率','endpoint_sla','接口','percent','%','接口在某单位时间请求响应成功占总请求的百分比',0),(68,'消息队列接口平均消费耗时','endpoint_mq_consume_latency','接口','longAvg','ms','请求类型为消息队列的接口消费平均耗时',0),(70,'服务端接口请求量（次/分）','endpoint_relation_cpm','接口关系','cpm','次','服务端接口请求量（次/分）',1),(72,'服务端接口rpc请求平均响应','endpoint_relation_resp_time','接口关系','longAvg','ms','服务端接口rpc请求平均响应',1),(74,'服务端接口成功率','endpoint_relation_sla','接口关系','percent','%','服务端接口成功率',1);
/*!40000 ALTER TABLE `chain_metrics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_label`
--

DROP TABLE IF EXISTS `custom_label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `label_key` varchar(100) NOT NULL COMMENT '标签名称',
  `label_value` text COMMENT '标签默认值',
  `name` varchar(100) DEFAULT NULL COMMENT '标签名称',
  `exp` text NOT NULL COMMENT 'json标签表达式',
  `creator` varchar(100) NOT NULL COMMENT '创建人',
  `updater` varchar(100) NOT NULL COMMENT '更新人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `is_default` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否是内置标签',
  `workspace_id` bigint(20) DEFAULT NULL COMMENT '工作空间ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_label`
--

LOCK TABLES `custom_label` WRITE;
/*!40000 ALTER TABLE `custom_label` DISABLE KEYS */;
INSERT INTO `custom_label` VALUES (10,'level',NULL,'告警级别','-','admin','admin','2024-07-03 07:26:14','2024-07-03 07:26:14',1,1,NULL),(12,'content',NULL,'告警内容','-','admin','admin','2024-07-03 07:26:14','2024-07-03 07:26:14',1,1,NULL),(14,'source',NULL,'告警来源','-','admin','admin','2024-07-03 07:26:14','2024-07-03 07:26:14',1,1,NULL),(16,'cate',NULL,'告警类型','-','admin','admin','2024-07-03 07:26:14','2024-07-03 07:26:14',1,1,NULL),(17,'ttime','','告警时间','$.trigger_time','admin','admin','2025-06-03 03:33:31','2025-06-03 03:33:31',1,0,3);
/*!40000 ALTER TABLE `custom_label` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `decision_condition`
--

DROP TABLE IF EXISTS `decision_condition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `decision_condition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(32) NOT NULL COMMENT '判断主题',
  `detail` text NOT NULL COMMENT '对应title的判断详情 包括：判断符+对应可选值',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(32) NOT NULL,
  `updater` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1770058991133650995 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `decision_condition`
--

LOCK TABLES `decision_condition` WRITE;
/*!40000 ALTER TABLE `decision_condition` DISABLE KEYS */;
INSERT INTO `decision_condition` VALUES (1770058991133650988,'告警级别','[{\"decision\":\"等于\",\"isSingle\":true,\"options\":[\"信息\",\"警告\",\"次要\",\"重要\",\"严重\"],\"useRegex\":false},{\"decision\":\"不等于\",\"isSingle\":true,\"options\":[\"信息\",\"警告\",\"次要\",\"重要\",\"严重\"],\"useRegex\":false},{\"decision\":\"在列表中\",\"isSingle\":false,\"options\":[\"信息\",\"警告\",\"次要\",\"重要\",\"严重\"],\"useRegex\":false},{\"decision\":\"不在列表中\",\"isSingle\":false,\"options\":[\"信息\",\"警告\",\"次要\",\"重要\",\"严重\"],\"useRegex\":false}]','2024-04-16 08:01:58','2024-04-16 08:01:58','admin','admin'),(1770058991133650990,'告警内容','[{\"decision\":\"等于\",\"isSingle\":true,\"useRegex\":true},{\"decision\":\"不等于\",\"isSingle\":true,\"useRegex\":true},{\"decision\":\"包含\",\"isSingle\":false,\"useRegex\":true},{\"decision\":\"不包含\",\"isSingle\":false,\"useRegex\":true}]','2024-04-16 08:01:58','2024-04-16 08:01:58','admin','admin'),(1770058991133650992,'告警类型','[{\"decision\":\"等于\",\"isSingle\":true,\"options\":[\"其他\",\"指标\",\"链路\",\"日志\"],\"useRegex\":false},{\"decision\":\"不等于\",\"isSingle\":true,\"options\":[\"其他\",\"指标\",\"链路\",\"日志\"],\"useRegex\":false},{\"decision\":\"在列表中\",\"isSingle\":false,\"options\":[\"其他\",\"指标\",\"链路\",\"日志\"],\"useRegex\":false},{\"decision\":\"不在列表中\",\"isSingle\":false,\"options\":[\"其他\",\"指标\",\"链路\",\"日志\"],\"useRegex\":false}]','2024-04-16 08:01:58','2024-04-16 08:01:58','admin','admin'),(1770058991133650994,'告警来源','[{\"decision\":\"等于\",\"isSingle\":true,\"options\":[\"星迹可观测\",\"Prometheus\"],\"useRegex\":false},{\"decision\":\"不等于\",\"isSingle\":true,\"options\":[\"星迹可观测\",\"Prometheus\"],\"useRegex\":false},{\"decision\":\"在列表中\",\"isSingle\":false,\"options\":[\"星迹可观测\",\"Prometheus\"],\"useRegex\":false},{\"decision\":\"不在列表中\",\"isSingle\":false,\"options\":[\"星迹可观测\",\"Prometheus\"],\"useRegex\":false}]','2024-04-16 08:01:58','2024-04-16 08:01:58','admin','admin');
/*!40000 ALTER TABLE `decision_condition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `global_config`
--

DROP TABLE IF EXISTS `global_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `global_config` (
  `key_name` varchar(32) NOT NULL COMMENT '配置名称',
  `value` text NOT NULL COMMENT '配置内容',
  PRIMARY KEY (`key_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `global_config`
--

LOCK TABLES `global_config` WRITE;
/*!40000 ALTER TABLE `global_config` DISABLE KEYS */;
INSERT INTO `global_config` VALUES ('channel_config','{\n\"mail\": \"host=\\\"\\\"\\nport=\\nuser=\\\"\\\"\\npassword=\\\"\\\"\\nfrom=\\\"\\\"\",\n\"qywechat\": \"corpid=\\\"\\\"\\nsecret=\\\"\\\"\\nagentid=\",\n\"message\": \"appid=\\\"\\\"\\ntid=\\\"\\\"\",\n\"mrsr_message\": \"senderSystem=\\\"\\\"\\nauthCode=\\\"\\\"\\nextend=\\\"{\\\\\\\"msgTid\\\\\\\":\\\\\\\"\\\\\\\"}\\\"\",\n\"zhpt_message\": \"ak=\\\"\\\"\\nsk=\\\"\\\"\\nsmsCode=\\\"\\\"\\ntokenHmac=\\\"\\\"\",\n\"qywechat_bot\": \"webhook=\\\"\\\"\",\n\"feishu_bot\": \"webhook=\\\"\\\"\\nsign=\\\"\\\"\"\n}');
/*!40000 ALTER TABLE `global_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `label_tpl`
--

DROP TABLE IF EXISTS `label_tpl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `label_tpl` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自定义标签ID',
  `content` text NOT NULL COMMENT '自定义标签模板内容',
  `source` int(11) NOT NULL COMMENT '告警来源 0星迹可观测 1Prometheus',
  `cate` int(11) NOT NULL COMMENT '告警类型 0其他 1指标 2调用链 3日志',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` varchar(100) NOT NULL COMMENT '创建人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近一次更新时间',
  `updater` varchar(100) NOT NULL COMMENT '最近一次更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='自定义标签模板';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `label_tpl`
--

LOCK TABLES `label_tpl` WRITE;
/*!40000 ALTER TABLE `label_tpl` DISABLE KEYS */;
INSERT INTO `label_tpl` VALUES (2,'{\"id\":405578,\"cate\":\"prometheus\",\"group_id\":0,\"group_name\":\"默认业务组\",\"hash\":\"922cc804094e0880d5f232f9fb1cfba7\",\"rule_id\":65,\"rule_name\":\"CPU告警\",\"rule_note\":\"\",\"rule_prod\":\"metric\",\"rule_algo\":\"\",\"severity\":3,\"prom_for_duration\":60,\"prom_ql\":\"cpu_usage_total >1\",\"rule_config\":{\"queries\":[{\"prom_ql\":\"cpu_usage_total >1\",\"severity\":3}]},\"prom_eval_interval\":30,\"callbacks\":[],\"runbook_url\":\"\",\"notify_recovered\":1,\"notify_channels\":[\"email\",\"sms\"],\"notify_groups\":[],\"notify_groups_obj\":[],\"target_ident\":\"observability-119\",\"target_note\":\"\",\"trigger_time\":1722406785,\"trigger_value\":\"13.13349\",\"tags\":[\"__name__=cpu_usage_total\",\"cpu=cpu-total\",\"data_group=tok_4f4c61eabe01436091d9ce1a77be1aba\",\"host=observability-119\",\"host_ip=172.29.228.119/24\",\"ident=observability-119\",\"key=host\",\"rulename=CPU告警\"],\"annotations\":{},\"is_recovered\":false,\"notify_users_obj\":[],\"last_eval_time\":1722406785,\"last_escalation_notify_time\":0,\"last_sent_time\":1722406785,\"notify_cur_number\":505,\"first_trigger_time\":1722391665,\"extra_config\":null,\"status\":0,\"claimant\":\"\",\"sub_rule_id\":0,\"ack_time\":0,\"ack_state\":0,\"data_group\":\"tok_4f4c61eabe01436091d9ce1a77be1aba\"}',0,1,'2024-07-02 09:40:57','admin','2024-07-02 09:40:57','admin'),(4,'{\"eventId\":\"20240731102222_service_sla1722218654779_rule_YWxhcm18dG9rXzNkYzJhNWUxNDU3NzQ0Mzc5ZGFkNjRlNWRhNjlkZjE4fA==.1_\",\"origin\":\"6070ff7784a104269ed03ef6ed2de19a\",\"triggerTime\":1722392542904,\"level\":3,\"content\":\"alarm服务成功率小于100%\",\"scope\":\"SERVICE\",\"applicationName\":\"alarm|tok_3dc2a5e1457744379dad64e5da69df18|\",\"ruleName\":\"service_sla1722218654779_rule\"}',0,2,'2024-07-03 07:30:16','admin','2024-07-03 07:30:16','admin'),(6,'{\"eventId\":\"25d8c275-b850-4394-8819-8e6f35491b04\",\"applicationName\":\"alarm\",\"origin\":\"f0c9d58b-31f0-49c7-b236-b8b47305c7fd\",\"triggerTime\":1722407054039,\"level\":1,\"title\":\"alarm_15应用日志告警\",\"content\":\"alarm日志告警\"}',0,3,'2024-07-03 07:30:40','admin','2024-07-03 07:30:40','admin'),(8,'{\"origin\":\"a738ddc557b9e1b649008f2e9d314734\",\"id\":\"9410a428e98f514eada75f2d1908b55b\",\"task_name\":\"python3存在和不存在\",\"point_name\":\"172.31.186.189_node1\",\"ident\":\"node1\",\"url\":\"\",\"lx\":\"SCRIPT\",\"frequency\":\"5m\",\"trigger_time\":1722406629419,\"fail_reason\":\"errMsg:请求结果为空\",\"rule_content\":\"{\\\"success_when\\\":[{\\\"custom_field\\\":{},\\\"result\\\":[],\\\"success\\\":[{\\\"contains\\\":\\\"0\\\"}]}],\\\"success_when_logic\\\":\\\"and\\\",\\\"script_type\\\":\\\"python3\\\",\\\"script_content\\\":\\\"import time\\\\nimport json\\\\n\\\\ntime.sleep(6)\\\\nsuccess = 0\\\\nresult = \\\\\\\"kkk\\\\\\\"\\\\nerrMsg = \\\\\\\"请求结果为空\\\\\\\"\\\\noutput = {\\\\\\\"success\\\\\\\":success, \\\\\\\"result\\\\\\\":result, \\\\\\\"errMsg\\\\\\\":errMsg}\\\\nprint(json.dumps(output))\\\",\\\"exec_user\\\":\\\"root\\\"}\"}',0,4,'2024-07-03 07:31:18','weipan4','2024-07-03 07:31:18','weipan4');
/*!40000 ALTER TABLE `label_tpl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monitor_system`
--

DROP TABLE IF EXISTS `monitor_system`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `monitor_system` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` tinyint(1) NOT NULL COMMENT '启用状态：true启用 false禁用',
  `token` varchar(50) NOT NULL COMMENT 'Token',
  `type` varchar(32) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `workspace_id` bigint(20) NOT NULL COMMENT '工作空间ID',
  `creator` varchar(32) NOT NULL,
  `updater` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monitor_system`
--

LOCK TABLES `monitor_system` WRITE;
/*!40000 ALTER TABLE `monitor_system` DISABLE KEYS */;
/*!40000 ALTER TABLE `monitor_system` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notify_channel`
--

DROP TABLE IF EXISTS `notify_channel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notify_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL COMMENT '通知方式名称',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(32) NOT NULL,
  `updater` varchar(32) NOT NULL,
  `status` tinyint(1) NOT NULL COMMENT '该通知方式是否已经启用',
  `content` text COMMENT '模板',
  `workspace_id` bigint(20) NOT NULL,
  `config` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notify_channel`
--

LOCK TABLES `notify_channel` WRITE;
/*!40000 ALTER TABLE `notify_channel` DISABLE KEYS */;
INSERT INTO `notify_channel` VALUES (2,'mail','2024-03-20 01:14:24','2024-03-20 01:14:24','admin','admin',1,'<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n    <title>可观测告警通知</title>\n    <style type=\"text/css\">\n        .wrapper {\n            background-color: #f8f8f8;\n            padding: 15px;\n            height: 100%;\n        }\n        .main {\n            width: 600px;\n            padding: 30px;\n            margin: 0 auto;\n            background-color: #fff;\n            font-size: 12px;\n            font-family: verdana,\'Microsoft YaHei\',Consolas,\'Deja Vu Sans Mono\',\'Bitstream Vera Sans Mono\';\n        }\n        header {\n            border-radius: 2px 2px 0 0;\n        }\n        header .title {\n            font-size: 14px;\n            color: #333333;\n            margin: 0;\n        }\n        header .sub-desc {\n            color: #333;\n            font-size: 14px;\n            margin-top: 6px;\n            margin-bottom: 0;\n        }\n        hr {\n            margin: 20px 0;\n            height: 0;\n            border: none;\n            border-top: 1px solid #e5e5e5;\n        }\n        em {\n            font-weight: 600;\n        }\n        table {\n            margin: 20px 0;\n            width: 100%;\n        }\n\n        table tbody tr{\n            font-weight: 200;\n            font-size: 12px;\n            color: #666;\n            height: 32px;\n        }\n\n        .succ {\n            background-color: green;\n            color: #fff;\n        }\n\n        .fail {\n            background-color: red;\n            color: #fff;\n        }\n\n        .succ th, .succ td, .fail th, .fail td {\n            color: #fff;\n        }\n\n        table tbody tr th {\n            width: 80px;\n            text-align: right;\n        }\n        .text-right {\n            text-align: right;\n        }\n        .body {\n            margin-top: 24px;\n        }\n        .body-text {\n            color: #666666;\n            -webkit-font-smoothing: antialiased;\n        }\n        .body-extra {\n            -webkit-font-smoothing: antialiased;\n        }\n        .body-extra.text-right a {\n            text-decoration: none;\n            color: #333;\n        }\n        .body-extra.text-right a:hover {\n            color: #666;\n        }\n        .button {\n            width: 200px;\n            height: 50px;\n            margin-top: 20px;\n            text-align: center;\n            border-radius: 2px;\n            background: #2D77EE;\n            line-height: 50px;\n            font-size: 20px;\n            color: #FFFFFF;\n            cursor: pointer;\n        }\n        .button:hover {\n            background: rgb(25, 115, 255);\n            border-color: rgb(25, 115, 255);\n            color: #fff;\n        }\n        footer {\n            margin-top: 10px;\n            text-align: right;\n        }\n        .footer-logo {\n            text-align: right;\n        }\n        .footer-logo-image {\n            width: 108px;\n            height: 27px;\n            margin-right: 10px;\n        }\n        .copyright {\n            margin-top: 10px;\n            font-size: 12px;\n            text-align: right;\n            color: #999;\n            -webkit-font-smoothing: antialiased;\n        }\n        .alarm-content{\n            white-space: wrap;\n            word-break: break-all;\n        }\n    </style>\n</head>\n<body>\n<div class=\"wrapper\">\n    <div class=\"main\">\n        <header>\n            <h3 class=\"title\">可观测告警通知</h3>\n            <p class=\"sub-desc\"></p>\n        </header>\n        <hr>\n        <div class=\"body\">\n            <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n                <tbody>\n                <#if isRecovered>\n                <tr class=\"succ\">\n                    <th>级别状态：</th>\n                    <#switch level>\n                        <#case 1>\n                            <td>信息 recovered</td>\n                            <#break>\n                        <#case 2>\n                            <td>警告 recovered</td>\n                            <#break>\n                        <#case 3>\n                            <td>次要 recovered</td>\n                            <#break>\n                        <#case 4>\n                            <td>重要 recovered</td>\n                            <#break>\n                        <#case 5>\n                            <td>严重 recovered</td>\n                            <#break>\n                        <#default>\n                            <#break>\n                    </#switch>\n                </tr>\n                <#else>\n                <tr class=\"fail\">\n                    <th>级别状态：</th>\n                    <#switch level>\n                        <#case 1>\n                        <td>信息 triggered</td>\n                            <#break>\n                        <#case 2>\n                        <td>警告 triggered</td>\n                            <#break>\n                        <#case 3>\n                        <td>次要 triggered</td>\n                            <#break>\n                        <#case 4>\n                        <td>重要 triggered</td>\n                            <#break>\n                        <#case 5>\n                        <td>严重 triggered</td>\n                            <#break>\n                        <#default>\n                            <#break>\n                    </#switch>\n                </tr>\n                </#if>\n\n                <tr class=\"alarm-content\">\n                    <th>内容：</th>\n                    <td>${content}</td>\n                </tr>\n\n                <#if isRecovered>\n                <tr>\n                    <th>恢复时间：</th>\n                    <td>${closeTime?datetime}</td>\n                </tr>\n                <#else>\n                <tr>\n                    <th>触发时间：</th>\n                    <td>${firstTriggerTime?datetime}</td>\n                </tr>\n                </#if>\n\n                <tr>\n                    <th>发送时间：</th>\n                    <td>\n                        ${now?datetime}\n                    </td>\n                </tr>\n                </tbody>\n            </table>\n        </div>\n    </div>\n    <hr>\n    <footer>\n        <div class=\"copyright\" style=\"font-style: italic\">\n            FROM 可观测告警平台.\n        </div>\n    </footer>\n</div>\n</body>\n</html>',1,''),(8,'qywechat','2024-03-26 02:42:54','2024-03-26 02:42:54','admin','admin',1,'级别状态: ${level?switch(1, \'信息\', 2, \'警告\', 3, \'次要\', 4, \'重要\', 5, \'严重\')} ${isRecovered?then(\'Recovered\', \'Triggered\')}\n告警内容: ${content}\n${isRecovered?then(\'恢复时间: ${closeTime?datetime}\', \'触发时间: ${firstTriggerTime?datetime}\')}\n发送时间: ${now?datetime}',1,''),(9,'feishu_bot','2025-06-03 03:37:35','2025-06-03 06:15:06','admin','admin',1,'{\n    \"msg_type\": \"text\",\n    \"content\": {\n        \"text\": \"可观测告警通知\\n级别状态: ${level?switch(1, \'信息\', 2, \'警告\', 3, \'次要\', 4, \'重要\', 5, \'严重\')} ${isRecovered?then(\'Recovered\', \'Triggered\')}\\n告警内容: ${content}\\n${isRecovered?then(\'恢复时间: ${closeTime?datetime}\', \'触发时间: ${firstTriggerTime?datetime}\')}\\n发送时间: ${now?datetime}\"\n    }\n}',3,'webhook=\"https://open.xfchat.iflytek.com/open-apis/bot/v2/hook/8fc1e861-e41a-4a09-b0a1-4be954dda399\"\nsign=\"\"'),(11,'feishu_bot','2025-06-04 07:10:40','2025-06-06 06:26:57','admin','admin',1,'{\n    \"msg_type\": \"text\",\n    \"content\": {\n        \"text\": \"可观测告警通知\\n级别状态: ${level?switch(1, \'信息\', 2, \'警告\', 3, \'次要\', 4, \'重要\', 5, \'严重\')} ${isRecovered?then(\'Recovered\', \'Triggered\')}\\n告警内容: ${content}\\n${isRecovered?then(\'恢复时间: ${closeTime?datetime}\', \'触发时间: ${firstTriggerTime?datetime}\')}\\n发送时间: ${now?datetime}\"\n    }\n}',1,'webhook=\"https://open.xfchat.iflytek.com/open-apis/bot/v2/hook/8fc1e861-e41a-4a09-b0a1-4be954dda399\"\nsign=\"\"');
/*!40000 ALTER TABLE `notify_channel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notify_group`
--

DROP TABLE IF EXISTS `notify_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notify_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(32) NOT NULL,
  `workspace_id` bigint(20) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) DEFAULT NULL COMMENT '通知组状态 true启用 false禁用',
  `creator` varchar(32) NOT NULL,
  `updater` varchar(32) NOT NULL,
  `remark` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notify_group`
--

LOCK TABLES `notify_group` WRITE;
/*!40000 ALTER TABLE `notify_group` DISABLE KEYS */;
INSERT INTO `notify_group` VALUES (1,'11',3,'2025-06-03 03:34:00','2025-06-03 03:34:00',1,'admin','admin',NULL),(3,'测试',1,'2025-06-06 06:27:18','2025-06-06 06:27:18',1,'admin','admin',NULL);
/*!40000 ALTER TABLE `notify_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notify_policy`
--

DROP TABLE IF EXISTS `notify_policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notify_policy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `notify_name` varchar(32) NOT NULL,
  `alarm_status` varchar(32) NOT NULL COMMENT '告警状态：发生时、认领时、关闭时、全部：支持多选',
  `notify_time` varchar(256) NOT NULL COMMENT '通知时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL COMMENT '通知策略状态 true启用 false停用',
  `updater` varchar(20) NOT NULL COMMENT '更新人',
  `workspace_id` bigint(20) NOT NULL COMMENT '工作空间ID',
  `remark` varchar(256) DEFAULT NULL COMMENT '通知策略内容',
  `alarm_level` varchar(32) NOT NULL COMMENT '告警级别：严重 警告 信息',
  `creator` varchar(32) NOT NULL,
  `notify_channel_name` varchar(100) NOT NULL COMMENT '通知方式名称，用逗号分开',
  `notify_channel` varchar(100) NOT NULL COMMENT '通知方式 用逗号隔开',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notify_policy`
--

LOCK TABLES `notify_policy` WRITE;
/*!40000 ALTER TABLE `notify_policy` DISABLE KEYS */;
INSERT INTO `notify_policy` VALUES (1,'飞书1','[0]','{\"type\":0,\"dayOfWeek\":[],\"startTime\":\"\",\"endTime\":\"\"}','2025-06-03 06:24:52','2025-06-03 06:24:52',1,'admin',3,NULL,'[5]','admin','[\"飞书机器人\"]','[\"feishu_bot\"]'),(3,'测试','[0,1,2]','{\"type\":0,\"dayOfWeek\":[],\"startTime\":\"\",\"endTime\":\"\"}','2025-06-06 06:28:34','2025-06-06 06:28:34',1,'admin',1,NULL,'[5,4,3,2,1]','admin','[\"飞书机器人\"]','[\"feishu_bot\"]');
/*!40000 ALTER TABLE `notify_policy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group`
--

LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
INSERT INTO `user_group` VALUES (3,5,1,'2025-06-03 08:07:57','2025-06-03 08:07:57'),(5,5,3,'2025-06-06 06:27:27','2025-06-06 06:27:27');
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-06 14:59:55
