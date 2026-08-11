-- MySQL dump 10.14  Distrib 5.5.68-MariaDB, for Linux (x86_64)
--
-- Host: 172.30.94.79    Database: aiops_resource
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
-- Table structure for table `sys_application`
--

DROP TABLE IF EXISTS `sys_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_application` (
  `application_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '应用id',
  `work_space_id` bigint(20) NOT NULL COMMENT '工作空间id',
  `data_group_id` bigint(20) NOT NULL COMMENT '数据单元id',
  `application_name` varchar(100) DEFAULT NULL COMMENT '应用名称',
  `application_code` varchar(200) DEFAULT NULL COMMENT '应用code',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `isvalid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_user` bigint(20) DEFAULT NULL COMMENT '更新者',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`application_id`),
  KEY `sys_application_work_space_id` (`work_space_id`),
  KEY `sys_application_data_group_id` (`data_group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='应用';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_application`
--

LOCK TABLES `sys_application` WRITE;
/*!40000 ALTER TABLE `sys_application` DISABLE KEYS */;
INSERT INTO `sys_application` VALUES (5,3,5,'test_nginx_60','app_ba98499d4af847d39395b696d8f93816','',1,1,'2025-06-03 13:58:33',1,'2025-06-03 13:58:33'),(9,1,2,'test-nginx-60','app_401d5243b79b49b99549aa77f1653aac','',1,3,'2025-06-03 14:05:57',3,'2025-06-03 14:05:57'),(11,5,9,'data-app','app_86ac70a7e09d416d8ac867697c83954f','',1,1,'2025-06-03 14:18:33',1,'2025-06-03 14:18:33'),(13,1,2,'ceshi','app_6263080afc3b4d388c09faeb28bd93e2','',1,1,'2025-06-06 14:33:00',1,'2025-06-06 14:33:00');
/*!40000 ALTER TABLE `sys_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父id',
  `property_type` varchar(60) NOT NULL COMMENT '类型 菜单按钮标识/子平台编码等',
  `property_key` varchar(100) NOT NULL COMMENT '属性名',
  `property_value` varchar(200) DEFAULT NULL COMMENT '属性值',
  `sort_num` int(11) NOT NULL DEFAULT '0' COMMENT '平级内序号',
  `isvalid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='公用配置/字典表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,0,'MODULE_TYPE','WORK_BENCH','工作台',0,1,''),(2,0,'MODULE_TYPE','BUSINESS_OBSERVATION','业务观测',1,1,''),(3,0,'MODULE_TYPE','ALARM_MANAGEMENT','告警管理',2,1,''),(4,0,'MODULE_TYPE','ENVIRONMENT_CONFIGURATION','环境配置',3,1,''),(5,0,'MODULE_TYPE','RESOURCE_CONTROL','权限管理',4,1,''),(6,0,'USER_DATA','DEFAULT_PWD','star@1q2w',0,1,''),(7,0,'DEPLOY_MODULE','observe-log','日志',0,1,'100'),(8,0,'DEPLOY_MODULE','observe-alarm','告警',0,1,'300'),(9,0,'DEPLOY_MODULE','observe-trace','链路',0,1,'200'),(10,0,'DEPLOY_MODULE','observe-metric','指标',0,1,'400'),(11,0,'DEPLOY_MODULE','observe-config','配置',0,1,'500');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_data_group`
--

DROP TABLE IF EXISTS `sys_data_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_data_group` (
  `data_group_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `work_space_id` bigint(20) NOT NULL COMMENT '工作空间id',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父id',
  `data_group_name` varchar(100) DEFAULT NULL COMMENT '名称',
  `data_group_token` varchar(100) DEFAULT NULL COMMENT 'token',
  `iselement` tinyint(1) NOT NULL COMMENT '是否数据单元',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `sort_num` int(11) NOT NULL DEFAULT '0' COMMENT '序号',
  `isvalid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_user` bigint(20) DEFAULT NULL COMMENT '更新者',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`data_group_id`),
  KEY `sys_data_group_work_space_id` (`work_space_id`),
  KEY `sys_data_group_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='业务组';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_data_group`
--

LOCK TABLES `sys_data_group` WRITE;
/*!40000 ALTER TABLE `sys_data_group` DISABLE KEYS */;
INSERT INTO `sys_data_group` VALUES (1,1,0,'内置业务组',NULL,0,NULL,0,1,0,'2023-12-20 14:42:11',0,'2023-12-20 14:42:11'),(2,1,1,'内置数据单元','tok_294c7bd237c34500b25c862c833fd236',1,NULL,0,1,0,'2023-12-20 14:43:54',0,'2023-12-20 14:43:54'),(5,3,0,'数据单元','tok_c19bd1999a474bd19c21fff11672fba3',1,'',1,1,1,'2025-06-03 13:58:18',1,'2025-06-03 13:58:18'),(9,5,0,'data','tok_b7ffc2cf70d34033a18f2a9a8290231c',1,'',1,1,1,'2025-06-03 14:18:27',1,'2025-06-03 14:18:27');
/*!40000 ALTER TABLE `sys_data_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_deploy_meta_info`
--

DROP TABLE IF EXISTS `sys_deploy_meta_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_deploy_meta_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `module_name` varchar(100) NOT NULL COMMENT '模块名称',
  `module_url` varchar(100) NOT NULL COMMENT '前端用于微服务之间的跳转',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='部署信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_deploy_meta_info`
--

LOCK TABLES `sys_deploy_meta_info` WRITE;
/*!40000 ALTER TABLE `sys_deploy_meta_info` DISABLE KEYS */;
INSERT INTO `sys_deploy_meta_info` VALUES (1,'observe-config','http://172.30.94.78:80/observe-config/','2025-05-29 09:15:45','2025-06-05 16:45:15'),(3,'observe-metric','http://172.30.94.78:80/observe-metric/','2025-05-29 09:38:46','2025-06-06 14:47:57'),(5,'observe-log','http://172.30.94.78:80/observe-log/','2025-05-29 11:09:35','2025-06-05 14:54:45'),(7,'observe-trace','http://172.30.94.78:80/observe-trace/','2025-05-29 11:38:12','2025-06-05 21:01:48'),(9,'observe-alarm','http://172.30.94.78:80/observe-alarm/','2025-05-29 11:46:22','2025-06-06 14:08:34');
/*!40000 ALTER TABLE `sys_deploy_meta_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单id',
  `menu_name` varchar(100) DEFAULT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父id',
  `module_type` varchar(30) DEFAULT NULL COMMENT '服务模块，来自sys_config表',
  `menu_path` varchar(100) DEFAULT NULL COMMENT '菜单地址',
  `isgroup` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否菜单分组',
  `ismenu` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否菜单',
  `isoutlink` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否外链',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `sort_num` int(11) NOT NULL DEFAULT '0' COMMENT '序号',
  `isdefault` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认，默认菜单每个用户都有',
  `isvalid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_user` bigint(20) DEFAULT NULL COMMENT '更新者',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `menu_no` varchar(60) DEFAULT NULL COMMENT '菜单编码',
  PRIMARY KEY (`menu_id`),
  UNIQUE KEY `sys_menu_menu_no` (`menu_no`),
  KEY `sys_menu_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=534 DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'工作台',0,'WORK_BENCH','/control',0,1,0,'',0,1,1,1,'2025-05-29 08:55:25',1,'2025-05-29 08:55:25','gongzuotai'),(2,'空间',0,'RESOURCE_CONTROL','/usermanage/space',0,1,0,'icon-kongjian',0,0,1,1,'2025-05-29 08:55:25',1,'2025-05-29 08:55:25','kongjian'),(3,'业务组管理',2,'RESOURCE_CONTROL','/usermanage/space/business',0,1,0,'icon-yewuzuguanli',0,0,1,1,'2025-05-29 08:55:25',1,'2025-05-29 08:55:25','yewuzuguanli'),(4,'用户管理',2,'RESOURCE_CONTROL','/usermanage/space/user',0,1,0,'icon-yonghuguanli',0,0,1,1,'2025-05-29 08:55:25',1,'2025-05-29 08:55:25','yonghuguanli'),(5,'角色管理',2,'RESOURCE_CONTROL','/usermanage/space/role',0,1,0,'icon-jiaoseguanli',0,0,1,1,'2025-05-29 08:55:25',1,'2025-05-29 08:55:25','jueseguanli'),(6,'用户池',0,'RESOURCE_CONTROL','/usermanage/pool',0,1,0,'icon-yonghuchi',0,0,1,1,'2025-05-29 08:55:25',1,'2025-05-29 08:55:25','yonghuchi'),(7,'菜单',0,'RESOURCE_CONTROL','/usermanage/menu',0,1,0,'icon-caidan',0,0,1,1,'2025-05-29 08:55:25',1,'2025-05-29 08:55:25','caidan'),(400,'仪表盘',0,'BUSINESS_OBSERVATION','/business-observe/observe-metric/dashboardRebuild',0,1,0,'icon-yibiaopan1',10,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','yibiaopan'),(401,'告警事件',0,'BUSINESS_OBSERVATION',NULL,0,0,0,'icon-gaojingshijian',9,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','gaojingshijian'),(402,'指标告警',401,'BUSINESS_OBSERVATION','/business-observe/observe-metric/alert-event',0,1,0,NULL,1,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','zhibiaogaojing'),(406,'全景监控',0,'BUSINESS_OBSERVATION','/',1,0,0,NULL,6,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','quanjingjiankong'),(407,'基础设施监控',406,'BUSINESS_OBSERVATION','/',0,0,0,'icon-jichusheshijiance1',2,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','jichusheshijiankong'),(408,'机器列表',407,'BUSINESS_OBSERVATION','/business-observe/observe-metric/targets',0,1,0,'',0,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','jiqiliebiao'),(409,'容器列表',407,'BUSINESS_OBSERVATION','/business-observe/observe-metric/containers',0,1,0,NULL,0,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','rongqiliebiao'),(410,'网络设备',407,'BUSINESS_OBSERVATION','/business-observe/observe-metric/network',0,1,0,NULL,0,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','wangluoshebeiliebiao'),(411,'可用性监控',406,'BUSINESS_OBSERVATION','/business-observe/observe-metric/available-monitor/task',0,1,0,'icon-keyongxingjiance',0,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','keyongxingjiankong'),(412,'数据探索',0,'BUSINESS_OBSERVATION','/',1,0,0,NULL,0,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','shujutansuo'),(413,'时序指标',412,'BUSINESS_OBSERVATION','/',0,0,0,'icon-shixuzhibiao3',3,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','shixuzhibiao'),(414,'即时查询',413,'BUSINESS_OBSERVATION','/business-observe/observe-metric/metric/explorer',0,1,0,'',0,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','jishichaxun'),(415,'快捷视图',413,'BUSINESS_OBSERVATION','/business-observe/observe-metric/object/explorer',0,1,0,'',0,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','kuaijieshitu'),(416,'记录规则',413,'BUSINESS_OBSERVATION','/business-observe/observe-metric/recording-rules',0,1,0,'',0,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','jiluguize'),(417,'接入中心',0,'BUSINESS_OBSERVATION','/',1,0,0,NULL,0,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','jieruzhongxin'),(418,'数据接入',417,'BUSINESS_OBSERVATION',NULL,0,0,0,'icon-shujujieru',5,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','shujujieru'),(419,'指标接入',418,'BUSINESS_OBSERVATION','/business-observe/observe-metric/integration',0,1,0,NULL,4,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','zhibiaojieru'),(420,'拨测点接入',418,'BUSINESS_OBSERVATION','/business-observe/observe-metric/probe-point/access',0,1,0,NULL,1,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','bocedianjieru'),(421,'进程监控',407,'BUSINESS_OBSERVATION','/business-observe/observe-metric/process',0,1,0,NULL,0,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','jinchengjiankong'),(422,'作业管理',0,'BUSINESS_OBSERVATION',NULL,0,0,0,'icon-zuoyeguanli',8,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','zuoyeguanli'),(423,'脚本',422,'BUSINESS_OBSERVATION','/business-observe/observe-metric/work-platform/script',0,1,0,NULL,3,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','jiaoben'),(424,'作业任务',422,'BUSINESS_OBSERVATION','/business-observe/observe-metric/work-platform/task',0,1,0,NULL,2,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','zuoyerenwu'),(425,'执行结果',422,'BUSINESS_OBSERVATION','/business-observe/observe-metric/work-platform/result',0,1,0,NULL,1,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','zhixingjieguo'),(426,'用户访问监控',406,'BUSINESS_OBSERVATION',NULL,0,0,0,'icon-yonghufangwenjiankong',1,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','yonghufangwenjiankong'),(427,'应用列表',426,'BUSINESS_OBSERVATION','/business-observe/observe-metric/rum/list',0,1,0,NULL,3,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','yingyongliebiao'),(428,'查看器',426,'BUSINESS_OBSERVATION','/business-observe/observe-metric/rum/viewer',0,1,0,NULL,2,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','chakanqi'),(429,'分析看板',426,'BUSINESS_OBSERVATION','/business-observe/observe-metric/rum/dashboard',0,1,0,NULL,1,0,1,NULL,'2025-05-29 09:38:47',NULL,'2025-05-29 09:38:47','fenxikanban'),(500,'准备',0,'ENVIRONMENT_CONFIGURATION','/',1,0,0,'',0,0,1,NULL,'2025-05-29 09:15:45',NULL,'2025-05-29 09:15:45','zhunbei'),(501,'安装盒管理',500,'ENVIRONMENT_CONFIGURATION','/environment-config/observe-config/config/installBox',0,1,0,'icon-anzhuangheguanli',3,0,1,NULL,'2025-05-29 09:15:45',NULL,'2025-05-29 09:15:45','anzhuangheguanli'),(502,'Agent环境管理',500,'ENVIRONMENT_CONFIGURATION','/environment-config/observe-config/config/index',0,1,0,'icon-Agenthuanjingguanli',2,0,1,NULL,'2025-05-29 09:15:45',NULL,'2025-05-29 09:15:45','agenthuanjingguanli'),(503,'插件管理',500,'ENVIRONMENT_CONFIGURATION','/environment-config/observe-config/plugin/pluginList',0,1,0,'icon-chajianguanli',1,0,1,NULL,'2025-05-29 09:15:45',NULL,'2025-05-29 09:15:45','chajianguanli'),(504,'操作',0,'ENVIRONMENT_CONFIGURATION','/',1,0,0,'',0,0,1,NULL,'2025-05-29 09:15:45',NULL,'2025-05-29 09:15:45','caozuo'),(505,'Agent管理',504,'ENVIRONMENT_CONFIGURATION','/environment-config/observe-config/agent/agentList',0,1,0,'icon-agentguanli',0,0,1,NULL,'2025-05-29 09:15:45',NULL,'2025-05-29 09:15:45','agentguanli'),(506,'Proxy管理',504,'ENVIRONMENT_CONFIGURATION','/environment-config/observe-config/proxy/proxyList',0,1,0,'icon-Proxyguanli',0,0,1,NULL,'2025-05-29 09:15:45',NULL,'2025-05-29 09:15:45','proxyguanli'),(507,'插件操作',504,'ENVIRONMENT_CONFIGURATION','/environment-config/observe-config/plugin/pluginSteps',0,1,0,'icon-chajiancaozuo',0,0,1,NULL,'2025-05-29 09:15:45',NULL,'2025-05-29 09:15:45','chajiancaozuo'),(508,'任务历史',504,'ENVIRONMENT_CONFIGURATION','/environment-config/observe-config/task/taskList',0,1,0,'icon-renwulishi',0,0,1,NULL,'2025-05-29 09:15:45',NULL,'2025-05-29 09:15:45','renwulishi'),(509,'日志告警',401,'BUSINESS_OBSERVATION','/business-observe/observe-log/alarm-config',0,1,0,'',3,0,1,NULL,'2025-05-29 11:09:36',NULL,'2025-05-29 11:09:36','rizhigaojing'),(510,'日志分析',412,'BUSINESS_OBSERVATION','',0,0,0,'icon-rizhijiansuo',0,0,1,NULL,'2025-05-29 11:09:36',NULL,'2025-05-29 11:09:36','rizhifenxi'),(511,'日志检索',510,'BUSINESS_OBSERVATION','/business-observe/observe-log/log-analysis',0,1,0,'',0,0,1,NULL,'2025-05-29 11:09:36',NULL,'2025-05-29 11:09:36','rizhijiansuo'),(512,'日志接入',418,'BUSINESS_OBSERVATION','/business-observe/observe-log/integration-center',0,1,0,'',2,0,1,NULL,'2025-05-29 11:09:36',NULL,'2025-05-29 11:09:36','rizhijieru'),(513,'应用性能监控',406,'BUSINESS_OBSERVATION','/business-observe/observe-trace/general',0,1,0,'icon-yingyongxingnengjiance1',3,0,1,NULL,'2025-05-29 11:38:13',NULL,'2025-05-29 11:38:13','yingyongxingnengjiankong'),(514,'链路追踪',412,'BUSINESS_OBSERVATION','/business-observe/observe-trace/trace',0,1,0,'icon-lianluzhuizong1',2,0,1,NULL,'2025-05-29 11:38:13',NULL,'2025-05-29 11:38:13','lianluzhuizong'),(515,'链路接入',418,'BUSINESS_OBSERVATION','/business-observe/observe-trace/integration-center',0,1,0,'',3,0,1,NULL,'2025-05-29 11:38:13',NULL,'2025-05-29 11:38:13','lianlujieru'),(516,'告警概览',0,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/alarm-overview/index',0,1,0,'icon-gaojinggailan1',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','gaojinggailan'),(517,'告警管理',0,'ALARM_MANAGEMENT',NULL,1,0,0,NULL,0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','gaojingguanli'),(518,'我的告警',517,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/self-alarm',0,1,0,'icon-a-wodegaojingheise',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','wodegaojing'),(519,'所有告警',517,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/all-alarm',0,1,0,'icon-suoyougaojing',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','suoyougaojing'),(520,'未分派告警',517,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/not-assign-alarm',0,1,0,'icon-weifenpaigaojing',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','weifenpaigaojing'),(521,'屏蔽告警',517,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/block-alarm',0,1,0,'icon-pingbigaojing',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','pingbigaojing'),(522,'所有通知',517,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/all-notify',0,1,0,'icon-suoyoutongzhi',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','suoyoutongzhi'),(523,'告警策略',0,'ALARM_MANAGEMENT',NULL,1,0,0,NULL,0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','gaojingcelue'),(524,'分派策略',523,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/assign-police',0,1,0,'icon-fenpaicelve',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','fenpaicelue'),(525,'配置',0,'ALARM_MANAGEMENT',NULL,1,0,0,NULL,0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','peizhi'),(526,'屏蔽规则',525,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/shield-rule',0,1,0,'icon-pingbiguize',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','pingbiguize'),(527,'通知策略',525,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/notice-police',0,1,0,'icon-tongzhicelve',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','tongzhicelue'),(528,'通知方式',525,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/notice-channel',0,1,0,'icon-tongzhizu',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','tongzhifangshi'),(529,'通知组',525,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/notice-group',0,1,0,'icon-tongzhifangshi',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','tongzhizu'),(530,'自定义标签',525,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/custom-label',0,1,0,'icon-zidingyibiaoqian',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','zidingyibiaoqian'),(531,'集成',0,'ALARM_MANAGEMENT',NULL,1,0,0,NULL,0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','jicheng'),(532,'监控系统',531,'ALARM_MANAGEMENT','/alarm-center/observe-alarm/monitor-sys',0,1,0,'icon-jiankongxitong',0,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','jiankongxitong'),(533,'链路告警',401,'BUSINESS_OBSERVATION','/business-observe/observe-trace/alarm-config',0,1,0,NULL,2,0,1,NULL,'2025-05-29 11:46:23',NULL,'2025-05-29 11:46:23','lianlugaojing');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu_resource`
--

DROP TABLE IF EXISTS `sys_menu_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_menu_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `menu_id` bigint(20) NOT NULL,
  `method` varchar(255) DEFAULT NULL,
  `path` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9591 DEFAULT CHARSET=utf8mb4 COMMENT='菜单接口资源关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu_resource`
--

LOCK TABLES `sys_menu_resource` WRITE;
/*!40000 ALTER TABLE `sys_menu_resource` DISABLE KEYS */;
INSERT INTO `sys_menu_resource` VALUES (1,1,'POST','/auth/updatePassword','2025-05-29 08:55:25'),(3,1,'POST','/auth/updateUser','2025-05-29 08:55:25'),(5,1,'GET','/auth/userBaseInfo','2025-05-29 08:55:25'),(7,1,'GET','/auth/menu','2025-05-29 08:55:25'),(9,1,'GET','/auth/workspace','2025-05-29 08:55:25'),(11,1,'GET','/auth/meta','2025-05-29 08:55:25'),(13,1,'GET','/auth/validateIntegrality','2025-05-29 08:55:25'),(15,1,'POST','/auth/deleteShortcutMenu/*','2025-05-29 08:55:25'),(17,1,'GET','/auth/shortcutMenu','2025-05-29 08:55:25'),(19,1,'POST','/auth/saveShortcutMenu','2025-05-29 08:55:25'),(21,1,'POST','/alarm/analysis/level','2025-05-29 08:55:25'),(23,1,'POST','/alarm/statistics/task','2025-05-29 08:55:25'),(25,1,'POST','/alarm/statistics/level','2025-05-29 08:55:25'),(27,1,'POST','/alarm/alarm/overview','2025-05-29 08:55:25'),(29,1,'POST','/graphql/listServicesNew','2025-05-29 08:55:25'),(31,1,'POST','/graphql/getErrorPm','2025-05-29 08:55:25'),(33,1,'POST','/auth/guideState','2025-05-29 08:55:25'),(35,1,'POST','/graphql/readMetricsValues','2025-05-29 08:55:25'),(37,1,'POST','/log-platform/log/getApplicationErrorLogCount','2025-05-29 08:55:25'),(39,1,'GET','/log-platform/application/list','2025-05-29 08:55:25'),(41,1,'GET','/log-platform/analysis/getErrorLogDistribution','2025-05-29 08:55:25'),(43,2,'GET','/auth/menu','2025-05-29 08:55:25'),(45,2,'GET','/auth/workspace','2025-05-29 08:55:25'),(47,2,'GET','/auth/meta','2025-05-29 08:55:25'),(49,2,'POST','/auth/sys/workSpace/list','2025-05-29 08:55:25'),(51,2,'POST','/auth/sys/workSpace/update','2025-05-29 08:55:25'),(53,2,'POST','/auth/sys/workSpace/insert','2025-05-29 08:55:25'),(55,2,'POST','/auth/sys/workSpace/list','2025-05-29 08:55:25'),(57,2,'POST','/auth/sys/dataGroup/bizTree','2025-05-29 08:55:25'),(59,2,'POST','/auth/sys/workSpace/delete','2025-05-29 08:55:25'),(61,2,'POST','/auth/sys/workSpace/user/insertUser','2025-05-29 08:55:25'),(63,2,'GET','/auth/sys/user/getDefaultPwd','2025-05-29 08:55:25'),(65,2,'GET','/auth/sys/workSpace/listTotalDrown','2025-05-29 08:55:25'),(67,3,'POST','/auth/sys/dataGroup/dataGroupTree','2025-05-29 08:55:25'),(69,3,'POST','/auth/sys/dataGroup/dataGroupTree','2025-05-29 08:55:25'),(71,3,'POST','/auth/sys/dataGroup/moveDataGroup','2025-05-29 08:55:25'),(73,3,'POST','/auth/sys/application/delete','2025-05-29 08:55:25'),(75,3,'POST','/auth/sys/application/update','2025-05-29 08:55:25'),(77,3,'POST','/auth/sys/application/insert','2025-05-29 08:55:25'),(79,3,'POST','/auth/sys/dataGroup/update','2025-05-29 08:55:25'),(81,3,'POST','/auth/sys/dataGroup/insert','2025-05-29 08:55:25'),(83,3,'POST','/auth/sys/dataGroup/delete','2025-05-29 08:55:25'),(85,3,'POST','/auth/sys/dataGroup/update','2025-05-29 08:55:25'),(87,3,'POST','/auth/sys/application/page','2025-05-29 08:55:25'),(89,3,'POST','/auth/sys/dataGroup/insert','2025-05-29 08:55:25'),(91,4,'POST','/auth/sys/workSpace/user/save','2025-05-29 08:55:25'),(93,4,'GET','/auth/sys/role/listSpaceTotal','2025-05-29 08:55:25'),(95,4,'GET','/auth/sys/workSpace/user/getDetail','2025-05-29 08:55:25'),(97,4,'POST','/auth/sys/workSpace/user/batchRemove','2025-05-29 08:55:25'),(99,4,'POST','/auth/sys/workSpace/user/save','2025-05-29 08:55:25'),(101,4,'GET','/auth/sys/role/detail','2025-05-29 08:55:25'),(103,4,'GET','/auth/sys/workSpace/user/listUserDrown','2025-05-29 08:55:25'),(105,4,'POST','/auth/sys/workSpace/user/page','2025-05-29 08:55:25'),(107,5,'POST','/auth/sys/role/page','2025-05-29 08:55:25'),(109,5,'GET','/auth/sys/role/listTemplate','2025-05-29 08:55:25'),(111,5,'GET','/auth/sys/role/getAuthMenuTree','2025-05-29 08:55:25'),(113,5,'POST','/auth/sys/role/delete','2025-05-29 08:55:25'),(115,5,'POST','/auth/sys/role/delete','2025-05-29 08:55:25'),(117,5,'POST','/auth/sys/role/update','2025-05-29 08:55:25'),(119,5,'GET','/auth/sys/role/detail','2025-05-29 08:55:25'),(121,5,'POST','/auth/sys/role/insert','2025-05-29 08:55:25'),(123,6,'POST','/auth/sys/user/page','2025-05-29 08:55:25'),(125,6,'GET','/auth/sys/user/getDefaultPwd','2025-05-29 08:55:25'),(127,6,'POST','/auth/sys/user/delete','2025-05-29 08:55:25'),(129,6,'POST','/auth/sys/user/resetPassword','2025-05-29 08:55:25'),(131,6,'POST','/auth/sys/user/update','2025-05-29 08:55:25'),(133,6,'POST','/auth/sys/user/changeActive','2025-05-29 08:55:25'),(135,6,'POST','/auth/sys/user/insert','2025-05-29 08:55:25'),(137,6,'GET','/auth/sys/workSpace/listTotalDrown','2025-05-29 08:55:25'),(139,7,'GET','/auth/sys/menu/getModuleSubTree','2025-05-29 08:55:25'),(141,7,'POST','/auth/sys/menu/delete','2025-05-29 08:55:25'),(143,7,'POST','/auth/sys/menu/update','2025-05-29 08:55:25'),(145,7,'GET','/auth/sys/menu/detail','2025-05-29 08:55:25'),(147,7,'POST','/auth/sys/menu/insert','2025-05-29 08:55:25'),(6993,509,'GET','/log-platform/alarm/event/list','2025-06-05 14:54:44'),(6995,509,'GET','/log-platform/application/list','2025-06-05 14:54:44'),(6997,509,'GET','/log-platform/alarm/rule/list','2025-06-05 14:54:44'),(6999,509,'GET','/log-platform/alarm/rule/detail','2025-06-05 14:54:44'),(7001,509,'GET','/log-platform/alarm/rule/checkSql','2025-06-05 14:54:44'),(7003,509,'POST','/log-platform/alarm/rule/update','2025-06-05 14:54:44'),(7005,509,'POST','/log-platform/alarm/rule/enable','2025-06-05 14:54:44'),(7007,509,'POST','/log-platform/alarm/rule/add','2025-06-05 14:54:44'),(7009,509,'POST','/log-platform/alarm/rule/delete','2025-06-05 14:54:44'),(7011,509,'GET','/log-platform/alarm/event/detail','2025-06-05 14:54:44'),(7013,511,'GET','/log-platform/application/list','2025-06-05 14:54:44'),(7015,511,'GET','/log-platform/config/getConfig','2025-06-05 14:54:44'),(7017,511,'GET','/log-platform/config/getAppConfig','2025-06-05 14:54:44'),(7019,511,'GET','/log-platform/operator/list','2025-06-05 14:54:44'),(7021,511,'GET','/log-platform/operator/dataTypeOperatorList','2025-06-05 14:54:44'),(7023,511,'GET','/log-platform/application/getLogRecordColumn','2025-06-05 14:54:44'),(7025,511,'POST','/log-platform/analysis/logDistribution','2025-06-05 14:54:44'),(7027,511,'POST','/log-platform/log/getLogs','2025-06-05 14:54:44'),(7029,511,'POST','/log-platform/log/exportLog','2025-06-05 14:54:44'),(7031,511,'GET','/log-platform/analysis/function','2025-06-05 14:54:44'),(7033,511,'POST','/log-platform/analysis/chart','2025-06-05 14:54:44'),(7035,511,'POST','/log-platform/analysis/errorType','2025-06-05 14:54:44'),(7037,511,'GET','/log-platform/application/getColumnValueProportion','2025-06-05 14:54:44'),(7039,511,'GET','/log-platform/alarm/rule/checkSql','2025-06-05 14:54:44'),(7041,511,'POST','/log-platform/alarm/rule/add','2025-06-05 14:54:44'),(7043,511,'POST','/log-platform/analysis/getErrorLogDistribution','2025-06-05 14:54:44'),(7045,511,'GET','/log-platform/log/detail','2025-06-05 14:54:44'),(7047,511,'GET','/log-platform/log/context','2025-06-05 14:54:44'),(7049,511,'GET','/log-platform/application/snapshot/list','2025-06-05 14:54:44'),(7051,511,'POST','/log-platform/application/snapshot/save','2025-06-05 14:54:44'),(7053,511,'DELETE','/log-platform/application/snapshot/delete','2025-06-05 14:54:44'),(7055,511,'POST','/api/n9e/builtin-boards-detail','2025-06-05 14:54:44'),(7057,511,'GET','/api/n9e/proxy/api/v1/series','2025-06-05 14:54:44'),(7059,511,'POST','/api/n9e/query-range-batch','2025-06-05 14:54:44'),(7061,511,'GET','/api/n9e/builtin-boards-cates','2025-06-05 14:54:44'),(7063,511,'GET','/api/n9e/targets','2025-06-05 14:54:44'),(7065,511,'GET','/api/n9e/host/middleware/list','2025-06-05 14:54:44'),(7067,511,'GET','/api/n9e/host/object','2025-06-05 14:54:44'),(7069,512,'GET','/log-platform/application/list/all','2025-06-05 14:54:44'),(7071,512,'GET','/log-platform/application/access/plugin/info','2025-06-05 14:54:44'),(7073,512,'GET','/log-platform/application/access/info','2025-06-05 14:54:44'),(7075,512,'POST','/log-platform/application/access/checkStatus','2025-06-05 14:54:44'),(7077,512,'POST','/log-platform/application/access/quick','2025-06-05 14:54:44'),(7079,512,'POST','/log-platform/application/access/quickRepair','2025-06-05 14:54:44'),(7081,512,'GET','/log-platform/application/access/config/vectorConfigPreview','2025-06-05 14:54:44'),(7083,512,'GET','/log-platform/application/access/detail','2025-06-05 14:54:44'),(7085,512,'GET','/log-platform/application/access/getDataType','2025-06-05 14:54:44'),(7087,512,'POST','/log-platform/log/field/parse','2025-06-05 14:54:44'),(7089,512,'POST','/log-platform/application/access/config','2025-06-05 14:54:44'),(7091,512,'GET','/log-platform/log/getParseFailedLogs','2025-06-05 14:54:44'),(7093,512,'GET','/log-platform/application/access/getPredefinedConfig','2025-06-05 14:54:44'),(7095,512,'POST','/log-platform/application/access/config/draftSave','2025-06-05 14:54:44'),(7097,512,'POST','/log-platform/application/access/config/draftDelete','2025-06-05 14:54:44'),(7099,512,'GET','/log-platform/application/access/config/draftDetail','2025-06-05 14:54:44'),(7101,512,'DELETE','/log-platform/application/access/config/delete','2025-06-05 14:54:44'),(7103,512,'POST','/log-platform/application/access/config/startRegexCheck','2025-06-05 14:54:44'),(7105,512,'POST','/log-platform/application/access/config/regexCheck','2025-06-05 14:54:44'),(7107,512,'GET','/log-platform/config/getConfig','2025-06-05 14:54:44'),(7419,501,NULL,'/api/v1/agent_package/box/*','2025-06-05 16:45:14'),(7421,501,'POST','/api/v1/agent_package/upload','2025-06-05 16:45:14'),(7423,502,NULL,'/api/v1/agent_package/**','2025-06-05 16:45:14'),(7425,503,NULL,'/api/v1/plugin/**','2025-06-05 16:45:14'),(7427,505,'GET','/api/v1/agent_package/list','2025-06-05 16:45:14'),(7429,505,NULL,'/api/v1/node/**','2025-06-05 16:45:14'),(7431,505,'POST','/api/v1/node','2025-06-05 16:45:14'),(7433,505,'POST','/api/v1/update/command','2025-06-05 16:45:14'),(7435,505,'GET','/api/v1/plugin/baseinfo/list','2025-06-05 16:45:14'),(7437,505,'POST','/auth/sys/dataGroup/dataGroupTree','2025-06-05 16:45:14'),(7439,505,'POST','/api/v1/internal/operate','2025-06-05 16:45:14'),(7441,506,'GET','/api/v1/agent_package/list','2025-06-05 16:45:14'),(7443,506,NULL,'/api/v1/proxy/*','2025-06-05 16:45:14'),(7445,506,NULL,'/api/v1/node/**','2025-06-05 16:45:14'),(7447,506,'POST','/api/v1/update/command','2025-06-05 16:45:14'),(7449,506,'POST','/api/v1/internal/operate','2025-06-05 16:45:14'),(7451,507,'GET','/api/v1/agent_package/list','2025-06-05 16:45:14'),(7453,507,NULL,'/api/v1/plugin/**','2025-06-05 16:45:14'),(7455,508,'GET','/api/v1/node/deploy/task/**','2025-06-05 16:45:14'),(7875,513,'POST','/graphql/getTimeInfo','2025-06-05 21:01:48'),(7877,513,'POST','/graphql/listMetrics','2025-06-05 21:01:48'),(7879,513,'POST','/graphql/listServicesNew','2025-06-05 21:01:48'),(7881,513,'POST','/graphql/getErrorPm','2025-06-05 21:01:48'),(7883,513,'GET','/log-platform/application/list','2025-06-05 21:01:48'),(7885,513,'GET','/log-platform/log/getApplicationErrorLogCount','2025-06-05 21:01:48'),(7887,513,'POST','/graphql/readMetricsValues','2025-06-05 21:01:48'),(7889,513,'POST','/graphql/getServicesTopology','2025-06-05 21:01:48'),(7891,513,'POST','/graphql/getValues','2025-06-05 21:01:48'),(7893,513,'POST','/graphql/findEndpoint','2025-06-05 21:01:48'),(7895,513,'POST','/graphql/listInstances','2025-06-05 21:01:48'),(7897,513,'POST','/graphql/queryTrace','2025-06-05 21:01:48'),(7899,513,'GET','/api/n9e/targets','2025-06-05 21:01:48'),(7901,513,'GET','/api/n9e/builtin-boards-cates','2025-06-05 21:01:48'),(7903,513,'POST','/api/n9e/builtin-boards-detail','2025-06-05 21:01:48'),(7905,513,'GET','/api/n9e/proxy/api/v1/series','2025-06-05 21:01:48'),(7907,513,'GET','/api/n9e/query-range-batch','2025-06-05 21:01:48'),(7909,513,'GET','/api/n9e/host/object','2025-06-05 21:01:48'),(7911,513,'GET','/api/n9e/host/middleware/list','2025-06-05 21:01:48'),(7913,513,'GET','/api/n9e/host/object','2025-06-05 21:01:48'),(7915,513,'GET','/alarm-chain/metrics/list','2025-06-05 21:01:48'),(7917,513,'GET','/alarm-chain/rule/saveOrUpdate','2025-06-05 21:01:48'),(7919,513,'POST','/graphql/queryEvents','2025-06-05 21:01:48'),(7921,513,'POST','/graphql/readLabeledMetricsValues','2025-06-05 21:01:48'),(7923,513,'POST','/graphql/readMetricsValue','2025-06-05 21:01:48'),(7925,513,'POST','/graphql/sortMetrics','2025-06-05 21:01:48'),(7927,513,'POST','/graphql/getAllTemplates','2025-06-05 21:01:48'),(7929,513,'POST','/graphql/getServiceGroup','2025-06-05 21:01:48'),(7931,513,'POST','/graphql/getEndpointDependencies','2025-06-05 21:01:48'),(7933,513,'POST','/graphql/getEndpointInfo','2025-06-05 21:01:48'),(7935,513,'POST','/graphql/queryStatisticsInfoParallel','2025-06-05 21:01:48'),(7937,513,'POST','/graphql/getProfileTaskSegmentList','2025-06-05 21:01:48'),(7939,513,'POST','/graphql/createProfileTask','2025-06-05 21:01:48'),(7941,513,'POST','/graphql/getProfiledSegment','2025-06-05 21:01:48'),(7943,513,'POST','/graphql/getProfileAnalyze','2025-06-05 21:01:48'),(7945,513,'POST','/graphql/getProfileTaskLogs','2025-06-05 21:01:48'),(7947,513,'POST','/graphql/getService','2025-06-05 21:01:48'),(7949,513,'POST','/graphql/queryBasicTraces','2025-06-05 21:01:48'),(7951,513,'POST','/graphql/getProfileTaskList','2025-06-05 21:01:48'),(7953,513,'POST','/graphql/getInstance','2025-06-05 21:01:48'),(7955,513,'GET','/alarm/metrics/list','2025-06-05 21:01:48'),(7957,513,'POST','/alarm/rule/saveOrUpdate','2025-06-05 21:01:48'),(7959,514,'POST','/graphql/getTimeInfo','2025-06-05 21:01:48'),(7961,514,'POST','/graphql/queryBasicTraces','2025-06-05 21:01:48'),(7963,514,'POST','/graphql/getAllTemplates','2025-06-05 21:01:48'),(7965,514,'POST','/graphql/findEndpoint','2025-06-05 21:01:48'),(7967,514,'POST','/graphql/listInstances','2025-06-05 21:01:48'),(7969,514,'POST','/graphql/queryTrace','2025-06-05 21:01:48'),(7971,514,'GET','/api/n9e/targets','2025-06-05 21:01:48'),(7973,514,'GET','/api/n9e/builtin-boards-cates','2025-06-05 21:01:48'),(7975,514,'POST','/api/n9e/builtin-boards-detail','2025-06-05 21:01:48'),(7977,514,'GET','/api/n9e/proxy/api/v1/series','2025-06-05 21:01:48'),(7979,514,'GET','/api/n9e/query-range-batch','2025-06-05 21:01:48'),(7981,514,'GET','/api/n9e/host/object','2025-06-05 21:01:48'),(7983,514,'GET','/api/n9e/host/middleware/list','2025-06-05 21:01:48'),(7985,514,'GET','/api/n9e/host/object','2025-06-05 21:01:48'),(7987,514,'GET','/alarm/metrics/list','2025-06-05 21:01:48'),(7989,514,'POST','/alarm/rule/saveOrUpdate','2025-06-05 21:01:48'),(7991,514,'POST','/graphql/getInstance','2025-06-05 21:01:48'),(7993,514,'POST','/graphql/getService','2025-06-05 21:01:48'),(7995,514,'POST','/graphql/getProfileTaskList','2025-06-05 21:01:48'),(7997,515,'GET','/api/v1/node/datagroup/plugins','2025-06-05 21:01:48'),(7999,515,'GET','/auth/applications','2025-06-05 21:01:48'),(8001,515,'POST','/graphql/getTimeInfo','2025-06-05 21:01:48'),(9173,516,'POST','/alarm/analysis/level','2025-06-06 14:08:34'),(9175,516,'POST','/alarm/event/compression','2025-06-06 14:08:34'),(9177,516,'POST','/alarm/alarm/overview','2025-06-06 14:08:34'),(9179,516,'POST','/alarm/statistics/level','2025-06-06 14:08:34'),(9181,516,'POST','/alarm/analysis/classification','2025-06-06 14:08:34'),(9183,516,'POST','/alarm/analysis/source','2025-06-06 14:08:34'),(9185,516,'POST','/alarm/top/dataGroup','2025-06-06 14:08:34'),(9187,516,'POST','/alarm/statistics/task','2025-06-06 14:08:34'),(9189,516,'POST','/alarm/statistics/cate','2025-06-06 14:08:34'),(9191,518,'POST','/alarm/alarm/agg/count','2025-06-06 14:08:34'),(9193,518,'POST','/alarm/alarm/agg/page','2025-06-06 14:08:34'),(9195,518,'POST','/alarm/alarm/agg/detail','2025-06-06 14:08:34'),(9197,518,'POST','/alarm/alarm/unique/page','2025-06-06 14:08:34'),(9199,518,'POST','/alarm/event/page/agg','2025-06-06 14:08:34'),(9201,518,'POST','/auth/guideState','2025-06-06 14:08:34'),(9203,518,'GET','/alarm/alarm/unique/labels/*','2025-06-06 14:08:34'),(9205,518,'GET','/alarm/alarm/unique/content/*','2025-06-06 14:08:34'),(9207,518,'POST','/alarm/alarm/agg/canAck','2025-06-06 14:08:34'),(9209,518,'POST','/alarm/alarm/agg/canClose','2025-06-06 14:08:34'),(9211,518,'POST','/alarm/alarm/agg/close','2025-06-06 14:08:34'),(9213,518,'POST','/alarm/alarm/agg/ack','2025-06-06 14:08:34'),(9215,518,'GET','/alarm/assign/policy/list','2025-06-06 14:08:34'),(9217,519,'POST','/alarm/alarm/agg/count','2025-06-06 14:08:34'),(9219,519,'POST','/alarm/alarm/agg/page','2025-06-06 14:08:34'),(9221,519,'POST','/alarm/alarm/agg/detail','2025-06-06 14:08:34'),(9223,519,'POST','/alarm/alarm/unique/page','2025-06-06 14:08:34'),(9225,519,'POST','/alarm/event/page/agg','2025-06-06 14:08:34'),(9227,519,'POST','/auth/guideState','2025-06-06 14:08:34'),(9229,519,'GET','/alarm/alarm/unique/labels/*','2025-06-06 14:08:34'),(9231,519,'GET','/alarm/alarm/unique/content/*','2025-06-06 14:08:34'),(9233,519,'GET','/alarm/assign/policy/list','2025-06-06 14:08:34'),(9235,520,'POST','/alarm/alarm/unique/page/unassign','2025-06-06 14:08:34'),(9237,520,'POST','/alarm/alarm/unique/detail/unassign','2025-06-06 14:08:34'),(9239,520,'POST','/alarm/event/page/unique','2025-06-06 14:08:34'),(9241,520,'GET','/alarm/alarm/unique/labels/*','2025-06-06 14:08:34'),(9243,520,'GET','/alarm/alarm/unique/content/*','2025-06-06 14:08:34'),(9245,521,'POST','/alarm/alarm/unique/page/block','2025-06-06 14:08:34'),(9247,521,'POST','/alarm/alarm/unique/detail/block','2025-06-06 14:08:34'),(9249,521,'POST','/alarm/event/page/unique','2025-06-06 14:08:34'),(9251,521,'GET','/alarm/alarm/unique/labels/*','2025-06-06 14:08:34'),(9253,521,'GET','/alarm/alarm/unique/content/*','2025-06-06 14:08:34'),(9255,522,'POST','/alarm/notice/page','2025-06-06 14:08:34'),(9257,522,'POST','/alarm/notice/detail','2025-06-06 14:08:34'),(9259,522,'POST','/alarm/notify/policy/list','2025-06-06 14:08:34'),(9261,524,'POST','/alarm/assign/policy/page','2025-06-06 14:08:34'),(9263,524,'POST','/alarm/assign/policy/disable','2025-06-06 14:08:34'),(9265,524,'POST','/alarm/assign/policy/enable','2025-06-06 14:08:34'),(9267,524,'POST','/alarm/decision/condition/list','2025-06-06 14:08:34'),(9269,524,'POST','/auth/sys/dataGroup/dataGroupTree','2025-06-06 14:08:34'),(9271,524,'POST','/alarm/notify/group/list','2025-06-06 14:08:34'),(9273,524,'POST','/alarm/notify/policy/list','2025-06-06 14:08:34'),(9275,524,'POST','/alarm/decision/condition/list/metrics','2025-06-06 14:08:34'),(9277,524,'POST','/alarm/assign/policy/create','2025-06-06 14:08:34'),(9279,524,'POST','/alarm/assign/policy/detail','2025-06-06 14:08:34'),(9281,524,'POST','/alarm/assign/policy/edit','2025-06-06 14:08:34'),(9283,524,'POST','/alarm/assign/policy/delete','2025-06-06 14:08:34'),(9285,526,'POST','/alarm/block/rule/page','2025-06-06 14:08:34'),(9287,526,'POST','/auth/sys/dataGroup/dataGroupTree','2025-06-06 14:08:34'),(9289,526,'POST','/alarm/decision/condition/list','2025-06-06 14:08:34'),(9291,526,'POST','/alarm/decision/condition/list/metrics','2025-06-06 14:08:34'),(9293,526,'POST','/alarm/block/rule/create','2025-06-06 14:08:34'),(9295,526,'POST','/alarm/block/rule/detail','2025-06-06 14:08:34'),(9297,526,'POST','/alarm/block/rule/edit','2025-06-06 14:08:34'),(9299,526,'POST','/alarm/block/rule/disable','2025-06-06 14:08:34'),(9301,526,'POST','/alarm/block/rule/enable','2025-06-06 14:08:34'),(9303,526,'POST','/alarm/block/rule/delete','2025-06-06 14:08:34'),(9305,527,'POST','/alarm/notify/channel/list','2025-06-06 14:08:34'),(9307,527,'POST','/alarm/notify/policy/page','2025-06-06 14:08:34'),(9309,527,'POST','/alarm/notify/policy/create','2025-06-06 14:08:34'),(9311,527,'POST','/alarm/notify/policy/clone','2025-06-06 14:08:34'),(9313,527,'POST','/alarm/notify/policy/edit','2025-06-06 14:08:34'),(9315,527,'POST','/alarm/notify/policy/disable','2025-06-06 14:08:34'),(9317,527,'POST','/alarm/notify/policy/enable','2025-06-06 14:08:34'),(9319,527,'POST','/alarm/notify/policy/delete','2025-06-06 14:08:34'),(9321,528,'POST','/alarm/notify/channel/list','2025-06-06 14:08:34'),(9323,528,'POST','/alarm/notify/channel/edit','2025-06-06 14:08:34'),(9325,528,'POST','/alarm/notify/channel/create','2025-06-06 14:08:34'),(9327,529,'POST','/alarm/notify/group/list','2025-06-06 14:08:34'),(9329,529,'POST','/alarm/notify/group/listByName','2025-06-06 14:08:34'),(9331,529,'POST','/alarm/notify/group/pageUser','2025-06-06 14:08:34'),(9333,529,'POST','/alarm/notify/group/create','2025-06-06 14:08:34'),(9335,529,'POST','/alarm/notify/group/edit','2025-06-06 14:08:34'),(9337,529,'POST','/alarm/notify/group/delete','2025-06-06 14:08:34'),(9339,529,'POST','/alarm/auth/listExternalUsers','2025-06-06 14:08:34'),(9341,529,'POST','/alarm/notify/group/addUsers','2025-06-06 14:08:34'),(9343,529,'POST','/alarm/notify/group/deleteUser','2025-06-06 14:08:34'),(9345,529,'POST','/alarm/notify/group/removeUsers','2025-06-06 14:08:34'),(9347,530,'POST','/alarm/custom/label/page','2025-06-06 14:08:34'),(9349,530,'POST','/alarm/label/tpl/get','2025-06-06 14:08:34'),(9351,530,'POST','/alarm/label/tpl/create','2025-06-06 14:08:34'),(9353,530,'POST','/alarm/label/tpl/edit','2025-06-06 14:08:34'),(9355,530,'POST','/alarm/custom/label/test','2025-06-06 14:08:34'),(9357,530,'POST','/alarm/custom/label/add','2025-06-06 14:08:34'),(9359,530,'POST','/alarm/custom/label/detail','2025-06-06 14:08:34'),(9361,530,'POST','/alarm/custom/label/edit','2025-06-06 14:08:34'),(9363,530,'POST','/alarm/custom/label/disable','2025-06-06 14:08:34'),(9365,530,'POST','/alarm/custom/label/enable','2025-06-06 14:08:34'),(9367,530,'POST','/alarm/custom/label/delete','2025-06-06 14:08:34'),(9369,532,'POST','/auth/sys/dataGroup/dataGroupTree','2025-06-06 14:08:34'),(9371,532,'POST','/alarm/auth/createToken','2025-06-06 14:08:34'),(9373,532,'POST','/alarm/monitor/system/esight/subscribe','2025-06-06 14:08:34'),(9375,532,'GET','/alarm/monitor/system/esight/cancel','2025-06-06 14:08:34'),(9377,532,'POST','/alarm/monitor/system/fd/subscribe','2025-06-06 14:08:34'),(9379,532,'GET','/alarm/monitor/system/fd/list','2025-06-06 14:08:34'),(9381,532,'GET','/alarm/monitor/system/fd/delete/*','2025-06-06 14:08:34'),(9383,532,'PATCH','/alarm/monitor/system/fd/update/*','2025-06-06 14:08:34'),(9385,533,'POST','/alarm/event/page','2025-06-06 14:08:34'),(9387,533,'POST','/alarm/rule/saveOrUpdate','2025-06-06 14:08:34'),(9389,533,'DELETE','/alarm/rule/*','2025-06-06 14:08:34'),(9391,533,'POST','/alarm/rule/page','2025-06-06 14:08:34'),(9393,533,'POST','/alarm/rule/changeActiveStatus','2025-06-06 14:08:34'),(9395,533,'GET','/alarm/metrics/list','2025-06-06 14:08:34'),(9397,533,'GET','/alarm/metrics/list','2025-06-06 14:08:34'),(9399,533,'POST','/graphql/getTimeInfo','2025-06-06 14:08:34'),(9401,533,'POST','/graphql/listServicesNew','2025-06-06 14:08:34'),(9403,533,'POST','/graphql/listInstances','2025-06-06 14:08:34'),(9405,533,'POST','/graphql/findEndpoint','2025-06-06 14:08:34'),(9407,400,'GET','/api/n9e/builtin-boards-cates','2025-06-06 14:47:57'),(9409,400,'POST','/api/n9e/busi-group/*/board/*/clone','2025-06-06 14:47:57'),(9411,400,NULL,'/api/n9e/busi-group/*/boards','2025-06-06 14:47:57'),(9413,400,NULL,'/api/n9e/board/**','2025-06-06 14:47:57'),(9415,400,'DELETE','/api/n9e/boards','2025-06-06 14:47:57'),(9417,400,'POST','/api/n9e/builtin-cate-favorite','2025-06-06 14:47:57'),(9419,400,'DELETE','/api/n9e/builtin-cate-favorite/*','2025-06-06 14:47:57'),(9421,400,'GET','/api/n9e/builtin-boards','2025-06-06 14:47:57'),(9423,400,'GET','/api/n9e/builtin-board/*','2025-06-06 14:47:57'),(9425,400,'GET','/api/n9e/dashboards/builtin/list','2025-06-06 14:47:57'),(9427,400,'GET','/api/n9e/builtin-boards-cates','2025-06-06 14:47:57'),(9429,400,'POST','/api/n9e/builtin-boards-detail','2025-06-06 14:47:57'),(9431,400,'GET','/api/n9e/integrations/**','2025-06-06 14:47:57'),(9433,400,'GET','/api/n9e/integrations','2025-06-06 14:47:57'),(9435,400,NULL,'/api/n9e/integrationDialtesting/**','2025-06-06 14:47:57'),(9437,400,NULL,'/api/n9e/integration/**','2025-06-06 14:47:57'),(9439,402,NULL,'/api/n9e/alert-his-events/**','2025-06-06 14:47:57'),(9441,402,NULL,'/api/n9e/alert-cur-events/**','2025-06-06 14:47:57'),(9443,402,'DELETE','/api/n9e/alert-cur-events','2025-06-06 14:47:57'),(9445,402,NULL,'/api/n9e/busi-group/*/alert-rule/*','2025-06-06 14:47:57'),(9447,402,NULL,'/api/n9e/busi-group/*/alert-rules/*','2025-06-06 14:47:57'),(9449,402,NULL,'/api/n9e/busi-group/*/alert-rules','2025-06-06 14:47:57'),(9451,402,'PUT','/api/n9e/busi-group/*/alert-rule/*/validate','2025-06-06 14:47:57'),(9453,402,'GET','/api/n9e/alert-rule/*','2025-06-06 14:47:57'),(9455,402,'GET','/api/v1/job_task/all','2025-06-06 14:47:57'),(9457,402,NULL,'/api/n9e/alert-rules/builtin/**','2025-06-06 14:47:57'),(9459,408,'GET','/api/n9e/target/charged/list','2025-06-06 14:47:57'),(9461,408,NULL,'/api/n9e/targets/*','2025-06-06 14:47:57'),(9463,408,NULL,'/api/n9e/targets','2025-06-06 14:47:57'),(9465,408,'GET','/api/n9e/builtin-boards-cates','2025-06-06 14:47:57'),(9467,408,'POST','/api/n9e/builtin-boards-detail','2025-06-06 14:47:57'),(9469,408,'GET','/api/n9e/page/column/*/fields','2025-06-06 14:47:57'),(9471,408,NULL,'/api/n9e/host/**','2025-06-06 14:47:57'),(9473,409,'GET','/api/n9e/page/column/*/fields','2025-06-06 14:47:57'),(9475,409,NULL,'/api/n9e/container/**','2025-06-06 14:47:57'),(9477,409,NULL,'/api/n9e/targets','2025-06-06 14:47:57'),(9479,409,'GET','/api/n9e/builtin-boards-cates','2025-06-06 14:47:57'),(9481,409,'POST','/api/n9e/builtin-boards-detail','2025-06-06 14:47:57'),(9483,409,'GET','/api/n9e/host/middleware/list','2025-06-06 14:47:57'),(9485,409,'GET','/api/n9e/host/object','2025-06-06 14:47:57'),(9487,410,NULL,'/api/n9e/network-device/**','2025-06-06 14:47:57'),(9489,410,'POST','/api/n9e/network-device','2025-06-06 14:47:57'),(9491,410,NULL,'/api/n9e/targets','2025-06-06 14:47:57'),(9493,411,NULL,'/api/n9e/dialtesting-task/**','2025-06-06 14:47:57'),(9495,411,NULL,'/api/n9e/dialtesting-point/**','2025-06-06 14:47:57'),(9497,411,NULL,'/api/n9e/dialtesting-task-result/**','2025-06-06 14:47:57'),(9499,415,NULL,'/api/n9e/metrics/desc','2025-06-06 14:47:57'),(9501,415,NULL,'/api/n9e/metric-views','2025-06-06 14:47:57'),(9503,415,NULL,'/api/n9e/dialtesting-task-result/**','2025-06-06 14:47:57'),(9505,416,NULL,'/api/n9e/busi-group/*/recording-rules','2025-06-06 14:47:57'),(9507,416,'PUT','/api/n9e/busi-group/*/recording-rules/fields','2025-06-06 14:47:57'),(9509,416,'PUT','/api/n9e/busi-group/*/recording-rule/*','2025-06-06 14:47:57'),(9511,416,'GET','/api/n9e/recording-rule/*','2025-06-06 14:47:57'),(9513,419,NULL,'/api/n9e/datasource/**','2025-06-06 14:47:57'),(9515,419,NULL,'/api/n9e/datasource','2025-06-06 14:47:57'),(9517,419,NULL,'/api/n9e/integrations/**','2025-06-06 14:47:57'),(9519,419,NULL,'/api/n9e/integration/**','2025-06-06 14:47:57'),(9521,419,'GET','/api/n9e/integrations','2025-06-06 14:47:57'),(9523,419,'GET','/api/n9e/target/charged/list','2025-06-06 14:47:57'),(9525,420,NULL,'/api/n9e/dialtesting-point/**','2025-06-06 14:47:57'),(9527,420,NULL,'/api/n9e/integrations/**','2025-06-06 14:47:57'),(9529,420,'GET','/api/n9e/integrations','2025-06-06 14:47:57'),(9531,420,NULL,'/api/n9e/integrationDialtesting/**','2025-06-06 14:47:57'),(9533,420,NULL,'/api/n9e/integration/**','2025-06-06 14:47:57'),(9535,420,'GET','/api/n9e/target/charged/list','2025-06-06 14:47:57'),(9537,421,'GET','/api/n9e/page/column/*/fields','2025-06-06 14:47:57'),(9539,421,NULL,'/api/n9e/host/process/**','2025-06-06 14:47:57'),(9541,421,NULL,'/api/n9e/targets','2025-06-06 14:47:57'),(9543,421,'GET','/api/n9e/builtin-boards-cates','2025-06-06 14:47:57'),(9545,421,'POST','/api/n9e/builtin-boards-detail','2025-06-06 14:47:57'),(9547,421,'GET','/api/n9e/host/middleware/list','2025-06-06 14:47:57'),(9549,421,'GET','/api/n9e/host/object','2025-06-06 14:47:57'),(9551,423,NULL,'/api/v1/job_script/**','2025-06-06 14:47:57'),(9553,424,NULL,'/api/v1/job_script/**','2025-06-06 14:47:57'),(9555,424,NULL,'/api/v1/job_task/**','2025-06-06 14:47:57'),(9557,424,NULL,'/api/v1/job_result/**','2025-06-06 14:47:57'),(9559,424,'GET','/api/v1/node/page','2025-06-06 14:47:57'),(9561,424,'POST','/api/v1/node/list','2025-06-06 14:47:57'),(9563,425,NULL,'/api/v1/job_result/**','2025-06-06 14:47:57'),(9565,427,NULL,'/api/n9e/rum/app/**','2025-06-06 14:47:57'),(9567,428,NULL,'/api/n9e/rum/field/**','2025-06-06 14:47:57'),(9569,428,NULL,'/api/n9e/rum/detail/**','2025-06-06 14:47:57'),(9571,428,NULL,'/api/n9e/rum/trace','2025-06-06 14:47:57'),(9573,428,NULL,'/api/n9e/rum/count','2025-06-06 14:47:57'),(9575,428,NULL,'/api/n9e/rum/page','2025-06-06 14:47:57'),(9577,428,NULL,'/api/n9e/rum/export','2025-06-06 14:47:57'),(9579,428,NULL,'/api/n9e/rum/event','2025-06-06 14:47:57'),(9581,428,NULL,'/api/n9e/rum/session/events','2025-06-06 14:47:57'),(9583,428,NULL,'/api/n9e/rum/error/agg','2025-06-06 14:47:57'),(9585,429,NULL,'/api/n9e/rum/field/vals','2025-06-06 14:47:57'),(9587,429,NULL,'/api/n9e/rum/metric','2025-06-06 14:47:57'),(9589,429,NULL,'/api/n9e/rum/metric/his','2025-06-06 14:47:57');
/*!40000 ALTER TABLE `sys_menu_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `work_space_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '工作空间id',
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `issuperadmin` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否超管',
  `isdefault` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认，内置角色中超管为N',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `isvalid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_user` bigint(20) DEFAULT NULL COMMENT '更新者',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`role_id`),
  KEY `sys_role_work_space_id` (`work_space_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,0,'超管',1,0,NULL,1,0,'2023-12-18 10:22:02',0,'2023-12-18 10:22:09'),(2,0,'空间管理员',0,1,NULL,1,0,'2023-12-18 10:22:45',0,'2023-12-18 10:22:45'),(3,1,'管理员',0,0,'测试功能',1,1,'2025-06-03 14:02:30',1,'2025-06-03 14:02:30');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_role_menu_role_menu_id` (`role_id`,`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=273 DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (1,2,1,'2025-05-29 08:55:25'),(3,2,5,'2025-05-29 08:55:25'),(5,2,2,'2025-05-29 08:55:25'),(7,2,3,'2025-05-29 08:55:25'),(9,2,4,'2025-05-29 08:55:25'),(15,2,500,'2025-05-29 09:15:45'),(17,2,501,'2025-05-29 09:15:45'),(19,2,502,'2025-05-29 09:15:45'),(21,2,503,'2025-05-29 09:15:45'),(23,2,504,'2025-05-29 09:15:45'),(25,2,505,'2025-05-29 09:15:45'),(27,2,506,'2025-05-29 09:15:45'),(29,2,507,'2025-05-29 09:15:45'),(31,2,508,'2025-05-29 09:15:45'),(33,2,400,'2025-05-29 09:38:46'),(35,2,401,'2025-05-29 09:38:46'),(37,2,402,'2025-05-29 09:38:46'),(39,2,406,'2025-05-29 09:38:46'),(41,2,407,'2025-05-29 09:38:46'),(43,2,408,'2025-05-29 09:38:46'),(45,2,409,'2025-05-29 09:38:46'),(47,2,410,'2025-05-29 09:38:46'),(49,2,411,'2025-05-29 09:38:46'),(51,2,412,'2025-05-29 09:38:46'),(53,2,413,'2025-05-29 09:38:46'),(55,2,414,'2025-05-29 09:38:46'),(57,2,415,'2025-05-29 09:38:46'),(59,2,416,'2025-05-29 09:38:46'),(61,2,417,'2025-05-29 09:38:46'),(63,2,418,'2025-05-29 09:38:46'),(65,2,419,'2025-05-29 09:38:46'),(67,2,420,'2025-05-29 09:38:46'),(69,2,421,'2025-05-29 09:38:46'),(71,2,422,'2025-05-29 09:38:46'),(73,2,423,'2025-05-29 09:38:46'),(75,2,424,'2025-05-29 09:38:46'),(77,2,425,'2025-05-29 09:38:46'),(79,2,426,'2025-05-29 09:38:46'),(81,2,427,'2025-05-29 09:38:46'),(83,2,428,'2025-05-29 09:38:46'),(85,2,429,'2025-05-29 09:38:46'),(87,2,509,'2025-05-29 11:09:35'),(89,2,510,'2025-05-29 11:09:35'),(91,2,511,'2025-05-29 11:09:35'),(93,2,512,'2025-05-29 11:09:35'),(95,2,513,'2025-05-29 11:38:12'),(97,2,514,'2025-05-29 11:38:12'),(99,2,515,'2025-05-29 11:38:12'),(101,2,516,'2025-05-29 11:46:22'),(103,2,517,'2025-05-29 11:46:22'),(105,2,518,'2025-05-29 11:46:22'),(107,2,519,'2025-05-29 11:46:22'),(109,2,520,'2025-05-29 11:46:22'),(111,2,521,'2025-05-29 11:46:22'),(113,2,522,'2025-05-29 11:46:22'),(115,2,523,'2025-05-29 11:46:22'),(117,2,524,'2025-05-29 11:46:22'),(119,2,525,'2025-05-29 11:46:22'),(121,2,526,'2025-05-29 11:46:22'),(123,2,527,'2025-05-29 11:46:22'),(125,2,528,'2025-05-29 11:46:22'),(127,2,529,'2025-05-29 11:46:22'),(129,2,530,'2025-05-29 11:46:22'),(131,2,531,'2025-05-29 11:46:22'),(133,2,532,'2025-05-29 11:46:22'),(135,2,533,'2025-05-29 11:46:22'),(137,3,1,'2025-06-03 14:02:29'),(139,3,400,'2025-06-03 14:02:29'),(141,3,401,'2025-06-03 14:02:29'),(143,3,509,'2025-06-03 14:02:29'),(145,3,533,'2025-06-03 14:02:29'),(147,3,402,'2025-06-03 14:02:29'),(149,3,422,'2025-06-03 14:02:29'),(151,3,423,'2025-06-03 14:02:29'),(153,3,424,'2025-06-03 14:02:29'),(155,3,425,'2025-06-03 14:02:29'),(157,3,406,'2025-06-03 14:02:29'),(159,3,513,'2025-06-03 14:02:29'),(161,3,407,'2025-06-03 14:02:29'),(163,3,408,'2025-06-03 14:02:29'),(165,3,409,'2025-06-03 14:02:29'),(167,3,410,'2025-06-03 14:02:29'),(169,3,421,'2025-06-03 14:02:29'),(171,3,426,'2025-06-03 14:02:29'),(173,3,427,'2025-06-03 14:02:29'),(175,3,428,'2025-06-03 14:02:29'),(177,3,429,'2025-06-03 14:02:29'),(179,3,411,'2025-06-03 14:02:29'),(181,3,412,'2025-06-03 14:02:29'),(183,3,413,'2025-06-03 14:02:29'),(185,3,414,'2025-06-03 14:02:29'),(187,3,415,'2025-06-03 14:02:29'),(189,3,416,'2025-06-03 14:02:29'),(191,3,514,'2025-06-03 14:02:29'),(193,3,510,'2025-06-03 14:02:29'),(195,3,511,'2025-06-03 14:02:29'),(197,3,417,'2025-06-03 14:02:29'),(199,3,418,'2025-06-03 14:02:29'),(201,3,419,'2025-06-03 14:02:29'),(203,3,515,'2025-06-03 14:02:29'),(205,3,512,'2025-06-03 14:02:29'),(207,3,420,'2025-06-03 14:02:29'),(209,3,516,'2025-06-03 14:02:29'),(211,3,517,'2025-06-03 14:02:29'),(213,3,518,'2025-06-03 14:02:29'),(215,3,519,'2025-06-03 14:02:29'),(217,3,520,'2025-06-03 14:02:29'),(219,3,521,'2025-06-03 14:02:29'),(221,3,522,'2025-06-03 14:02:29'),(223,3,523,'2025-06-03 14:02:29'),(225,3,524,'2025-06-03 14:02:29'),(227,3,525,'2025-06-03 14:02:29'),(229,3,526,'2025-06-03 14:02:29'),(231,3,527,'2025-06-03 14:02:29'),(233,3,528,'2025-06-03 14:02:29'),(235,3,529,'2025-06-03 14:02:29'),(237,3,530,'2025-06-03 14:02:29'),(239,3,531,'2025-06-03 14:02:29'),(241,3,532,'2025-06-03 14:02:29'),(243,3,500,'2025-06-03 14:02:29'),(245,3,501,'2025-06-03 14:02:29'),(247,3,502,'2025-06-03 14:02:29'),(249,3,503,'2025-06-03 14:02:29'),(251,3,504,'2025-06-03 14:02:29'),(253,3,505,'2025-06-03 14:02:29'),(255,3,506,'2025-06-03 14:02:29'),(257,3,507,'2025-06-03 14:02:29'),(259,3,508,'2025-06-03 14:02:29'),(261,3,2,'2025-06-03 14:02:29'),(263,3,3,'2025-06-03 14:02:29'),(265,3,4,'2025-06-03 14:02:29'),(267,3,5,'2025-06-03 14:02:29'),(269,3,6,'2025-06-03 14:02:29'),(271,3,7,'2025-06-03 14:02:29');
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_shortcut_menu`
--

DROP TABLE IF EXISTS `sys_shortcut_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_shortcut_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `menu_id` bigint(20) NOT NULL,
  `menu_path` varchar(100) NOT NULL,
  `account` varchar(100) NOT NULL,
  `workspace_id` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COMMENT='快捷菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_shortcut_menu`
--

LOCK TABLES `sys_shortcut_menu` WRITE;
/*!40000 ALTER TABLE `sys_shortcut_menu` DISABLE KEYS */;
INSERT INTO `sys_shortcut_menu` VALUES (1,418,'/business-observe/observe-metric/integration','admin',3,'2025-05-30 10:14:41','2025-05-30 10:14:41'),(3,502,'/environment-config/observe-config/config/index','admin',3,'2025-05-30 13:56:38','2025-05-30 13:56:38'),(15,407,'/business-observe/observe-metric/targets','admin',1,'2025-06-03 11:34:23','2025-06-03 11:34:23'),(17,514,'/business-observe/observe-trace/trace','admin',1,'2025-06-04 16:24:16','2025-06-04 16:24:16'),(19,401,'/business-observe/observe-log/alarm-config','admin',1,'2025-06-05 19:07:41','2025-06-05 19:07:41'),(21,524,'/alarm-center/observe-alarm/assign-police','admin',1,'2025-06-05 20:01:45','2025-06-05 20:01:45'),(23,418,'/business-observe/observe-metric/integration','admin',1,'2025-06-06 13:55:11','2025-06-06 13:55:11'),(25,502,'/environment-config/observe-config/config/index','admin',1,'2025-06-06 14:05:45','2025-06-06 14:05:45');
/*!40000 ALTER TABLE `sys_shortcut_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `account` varchar(100) DEFAULT NULL COMMENT '账号',
  `passwd` varchar(100) DEFAULT NULL COMMENT '密码',
  `nick_name` varchar(100) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱地址',
  `phone` varchar(60) DEFAULT NULL COMMENT '联系方式',
  `feishu_user_id` varchar(128) DEFAULT NULL COMMENT '飞书 user_id',
  `it_workbench_user_id` varchar(128) DEFAULT NULL COMMENT '三方 IT 工作台用户 ID',
  `identity_source` varchar(20) NOT NULL DEFAULT 'LOCAL' COMMENT '身份来源：LOCAL/LDAP/SYNC',
  `ldap_account` varchar(128) DEFAULT NULL COMMENT 'LDAP 登录账号或映射账号',
  `head_img` varchar(100) DEFAULT NULL COMMENT '头像',
  `isactive` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `isloggedin` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否登录过，没有则是未激活',
  `login_time` datetime DEFAULT NULL COMMENT '用户登录时间',
  `passwd_modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '密码变更时间',
  `isvalid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_user` bigint(20) DEFAULT NULL COMMENT '更新者',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `guide_state` varchar(1000) NOT NULL DEFAULT '{}' COMMENT '新手指引状态',
  `sign` varchar(1000) DEFAULT NULL COMMENT '利川完整性校验加密字段',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `sys_user_account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','6c3a2908b4ea6d6b2e42a39b8359ee8a','超管',NULL,NULL,NULL,NULL,'LOCAL',NULL,NULL,1,1,'2025-06-06 14:22:29','2025-05-29 08:55:25',1,0,'2023-12-18 10:25:40',0,'2023-12-18 10:25:40','{\"control\":false,\"dataGroupManager\":false,\"logAnalysis\":false,\"integrationGuide\":true,\"dashboardGuide\":false,\"targetGuide\":false,\"overviewGuide\":false,\"explorerGuide\":true,\"availableGuide\":false,\"skywalkingGuide\":false,\"skywalkingTraceGuide\":false}',NULL),(2,'space','6c3a2908b4ea6d6b2e42a39b8359ee8a','空间管理员',NULL,NULL,NULL,NULL,'LOCAL',NULL,NULL,1,0,NULL,'2025-05-29 08:55:25',1,0,'2023-12-18 10:25:40',0,'2023-12-18 10:25:40','{}',NULL),(3,'test-log','6c3a2908b4ea6d6b2e42a39b8359ee8a','test-log','jjchu6@iflytek.com','18856862600',NULL,NULL,'LOCAL',NULL,NULL,1,1,'2025-06-03 14:19:18','2025-06-03 13:59:56',1,NULL,'2025-06-03 13:59:56',NULL,'2025-06-03 14:18:14','{\"control\":false,\"dataGroupManager\":false,\"logAnalysis\":false,\"integrationGuide\":true,\"targetGuide\":false,\"availableGuide\":false}',NULL),(5,'hmwang24','6c3a2908b4ea6d6b2e42a39b8359ee8a','汪虎明','hmwang24@iflytek.com','17352911273',NULL,NULL,'LOCAL',NULL,NULL,1,0,NULL,'2025-06-03 16:03:03',1,NULL,'2025-06-03 16:03:03',NULL,'2025-06-03 16:03:03','{}',NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `work_space_id` bigint(20) DEFAULT NULL COMMENT '工作空间id',
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `sign` varchar(1000) DEFAULT NULL COMMENT '利川完整性校验加密字段',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_user_role_user_space_role_id` (`user_id`,`work_space_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1,0,1,NULL,'2023-12-18 10:30:16'),(5,2,1,2,NULL,'2025-06-03 14:02:48'),(7,3,1,3,NULL,'2025-06-03 14:03:01'),(9,3,5,2,NULL,'2025-06-03 14:18:52'),(11,5,1,3,NULL,'2025-06-03 16:03:48'),(13,5,3,2,NULL,'2025-06-03 16:06:18');
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_work_space`
--

DROP TABLE IF EXISTS `sys_user_work_space`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user_work_space` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `work_space_id` bigint(20) NOT NULL COMMENT '工作空间id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_user_work_group_user_work_space_id` (`user_id`,`work_space_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COMMENT='用户工作空间关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_work_space`
--

LOCK TABLES `sys_user_work_space` WRITE;
/*!40000 ALTER TABLE `sys_user_work_space` DISABLE KEYS */;
INSERT INTO `sys_user_work_space` VALUES (3,3,3,'2025-06-03 13:59:56'),(7,2,1,'2025-06-03 14:02:48'),(9,3,1,'2025-06-03 14:03:01'),(13,3,5,'2025-06-03 14:18:52'),(17,5,1,'2025-06-03 16:03:48'),(19,5,3,'2025-06-03 16:06:18');
/*!40000 ALTER TABLE `sys_user_work_space` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_work_space`
--

DROP TABLE IF EXISTS `sys_work_space`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_work_space` (
  `work_space_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '工作空间id',
  `work_space_name` varchar(100) DEFAULT NULL COMMENT '工作空间名称',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `isdefault` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认，内置空间不可删除',
  `isvalid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_user` bigint(20) DEFAULT NULL COMMENT '更新者',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`work_space_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='工作空间';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_work_space`
--

LOCK TABLES `sys_work_space` WRITE;
/*!40000 ALTER TABLE `sys_work_space` DISABLE KEYS */;
INSERT INTO `sys_work_space` VALUES (1,'内置空间',NULL,1,1,0,'2023-12-18 10:28:05',0,'2023-12-18 10:28:05'),(3,'测试','',0,1,1,'2025-05-30 10:13:37',1,'2025-05-30 10:13:37'),(5,'日志接入','',0,1,3,'2025-06-03 14:16:43',3,'2025-06-03 14:16:43');
/*!40000 ALTER TABLE `sys_work_space` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-06 14:59:44
