
-- 建库
CREATE DATABASE `aiops_resource` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `aiops_resource`;

-- 公用配置/字典表
CREATE TABLE `sys_config` (
  `id` bigint(20) AUTO_INCREMENT NOT NULL COMMENT '主键id',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父id',
  `property_type` varchar(60) NOT NULL COMMENT '类型 菜单按钮标识/子平台编码等',
  `property_key` varchar(100) NOT NULL COMMENT '属性名',
  `property_value` varchar(200) DEFAULT NULL COMMENT '属性值',
  `sort_num` int(11) NOT NULL DEFAULT '0' COMMENT '平级内序号',
  `isvalid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) COMMENT='公用配置/字典表';

-- -- 子平台编码
insert into `sys_config`(`id`,`parent_id`,`property_type`,`property_key`,`property_value`,`sort_num`,`isvalid`,`remark`) values (1,0,'MODULE_TYPE','WORK_BENCH','工作台',0,1,'');
insert into `sys_config`(`id`,`parent_id`,`property_type`,`property_key`,`property_value`,`sort_num`,`isvalid`,`remark`) values (2,0,'MODULE_TYPE','BUSINESS_OBSERVATION','业务观测',1,1,'');
insert into `sys_config`(`id`,`parent_id`,`property_type`,`property_key`,`property_value`,`sort_num`,`isvalid`,`remark`) values (3,0,'MODULE_TYPE','ALARM_MANAGEMENT','告警管理',2,1,'');
insert into `sys_config`(`id`,`parent_id`,`property_type`,`property_key`,`property_value`,`sort_num`,`isvalid`,`remark`) values (4,0,'MODULE_TYPE','ENVIRONMENT_CONFIGURATION','环境配置',3,1,'');
insert into `sys_config`(`id`,`parent_id`,`property_type`,`property_key`,`property_value`,`sort_num`,`isvalid`,`remark`) values (5,0,'MODULE_TYPE','RESOURCE_CONTROL','权限管理',4,1,'');
insert into `sys_config`(`id`,`parent_id`,`property_type`,`property_key`,`property_value`,`sort_num`,`isvalid`,`remark`) values (6,0,'USER_DATA','DEFAULT_PWD','star@1q2w',0,1,'');
insert into `sys_config`(`id`,`parent_id`,`property_type`,`property_key`,`property_value`,`sort_num`,`isvalid`,`remark`) values (7,0,'DEPLOY_MODULE','observe-log','日志',0,1,'100');
insert into `sys_config`(`id`,`parent_id`,`property_type`,`property_key`,`property_value`,`sort_num`,`isvalid`,`remark`) values (8,0,'DEPLOY_MODULE','observe-alarm','告警',0,1,'300');
insert into `sys_config`(`id`,`parent_id`,`property_type`,`property_key`,`property_value`,`sort_num`,`isvalid`,`remark`) values (9,0,'DEPLOY_MODULE','observe-trace','链路',0,1,'200');
insert into `sys_config`(`id`,`parent_id`,`property_type`,`property_key`,`property_value`,`sort_num`,`isvalid`,`remark`) values (10,0,'DEPLOY_MODULE','observe-metric','指标',0,1,'400');
insert into `sys_config`(`id`,`parent_id`,`property_type`,`property_key`,`property_value`,`sort_num`,`isvalid`,`remark`) values (11,0,'DEPLOY_MODULE','observe-config','配置',0,1,'500');
-- 用户表
CREATE TABLE `sys_user` (
  `user_id` bigint(20) AUTO_INCREMENT NOT NULL COMMENT '用户id',
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
  `sign` varchar(1000) NULL COMMENT '利川完整性校验加密字段',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `sys_user_account` (`account`)
) COMMENT='用户表';

-- -- 用户
INSERT INTO `sys_user` (`user_id`, `account`, `passwd`, `nick_name`, `email`, `phone`, `head_img`, `isactive`, `isloggedin`, `isvalid`, `create_user`, `create_time`, `modify_user`, `modify_time`) VALUES(1,'admin','6c3a2908b4ea6d6b2e42a39b8359ee8a','超管',NULL,NULL,NULL,1,0,1,0,'2023-12-18 10:25:40',0,'2023-12-18 10:25:40');
INSERT INTO `sys_user` (`user_id`, `account`, `passwd`, `nick_name`, `email`, `phone`, `head_img`, `isactive`, `isloggedin`, `isvalid`, `create_user`, `create_time`, `modify_user`, `modify_time`) VALUES(2,'space','6c3a2908b4ea6d6b2e42a39b8359ee8a','空间管理员',NULL,NULL,NULL,1,0,1,0,'2023-12-18 10:25:40',0,'2023-12-18 10:25:40');

-- 菜单表
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) AUTO_INCREMENT NOT NULL COMMENT '菜单id',
  `menu_name` varchar(100) DEFAULT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父id',
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
  KEY `sys_menu_parent_id` (`parent_id`),
  UNIQUE KEY `sys_menu_menu_no` (`menu_no`)
) AUTO_INCREMENT=1000
  COMMENT='菜单表';

-- -- 菜单
insert into `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `module_type`, `menu_path`, `isgroup`, `ismenu`, `isoutlink`, `icon`, `sort_num`, `isdefault`, `isvalid`, `create_user`, `modify_user`,  `menu_no`) values(1,'工作台',0,'WORK_BENCH','/control',0,1,0,'',0,1,1,1,1,'gongzuotai');
insert into `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `module_type`, `menu_path`, `isgroup`, `ismenu`, `isoutlink`, `icon`, `sort_num`, `isdefault`, `isvalid`, `create_user`, `modify_user`,  `menu_no`) values(2,'空间',0,'RESOURCE_CONTROL','/usermanage/space',0,1,0,'icon-kongjian',0,0,1,1,1,'kongjian');
insert into `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `module_type`, `menu_path`, `isgroup`, `ismenu`, `isoutlink`, `icon`, `sort_num`, `isdefault`, `isvalid`, `create_user`, `modify_user`,  `menu_no`) values(3,'业务组管理',2,'RESOURCE_CONTROL','/usermanage/space/business',0,1,0,'icon-yewuzuguanli',0,0,1,1,1,'yewuzuguanli');
insert into `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `module_type`, `menu_path`, `isgroup`, `ismenu`, `isoutlink`, `icon`, `sort_num`, `isdefault`, `isvalid`, `create_user`, `modify_user`,  `menu_no`) values(4,'用户管理',2,'RESOURCE_CONTROL','/usermanage/space/user',0,1,0,'icon-yonghuguanli',0,0,1,1,1,'yonghuguanli');
insert into `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `module_type`, `menu_path`, `isgroup`, `ismenu`, `isoutlink`, `icon`, `sort_num`, `isdefault`, `isvalid`, `create_user`, `modify_user`,  `menu_no`) values(5,'角色管理',2,'RESOURCE_CONTROL','/usermanage/space/role',0,1,0,'icon-jiaoseguanli',0,0,1,1,1,'jueseguanli');
insert into `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `module_type`, `menu_path`, `isgroup`, `ismenu`, `isoutlink`, `icon`, `sort_num`, `isdefault`, `isvalid`, `create_user`, `modify_user`,  `menu_no`) values(6,'用户池',0,'RESOURCE_CONTROL','/usermanage/pool',0,1,0,'icon-yonghuchi',0,0,1,1,1,'yonghuchi');
insert into `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `module_type`, `menu_path`, `isgroup`, `ismenu`, `isoutlink`, `icon`, `sort_num`, `isdefault`, `isvalid`, `create_user`, `modify_user`,  `menu_no`) values(7,'菜单',0,'RESOURCE_CONTROL','/usermanage/menu',0,1,0,'icon-caidan',0,0,1,1,1,'caidan');

-- 菜单接口资源关系
CREATE TABLE `sys_menu_resource` (
    `id` bigint(20) AUTO_INCREMENT NOT NULL,
    `menu_id` bigint(20) NOT NULL,
    `method` VARCHAR(255),
    `path` VARCHAR(255) NOT NULL ,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`)
)COMMENT='菜单接口资源关系';
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/auth/updatePassword');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/auth/updateUser');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'GET', '/auth/userBaseInfo');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'GET', '/auth/menu');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'GET', '/auth/workspace');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'GET', '/auth/meta');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'GET', '/auth/validateIntegrality');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/auth/deleteShortcutMenu/*');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'GET', '/auth/shortcutMenu');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/auth/saveShortcutMenu');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/alarm/analysis/level');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/alarm/statistics/task');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/alarm/statistics/level');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/alarm/alarm/overview');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/graphql/listServicesNew');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/graphql/getErrorPm');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/auth/guideState');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/graphql/readMetricsValues');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'POST', '/log-platform/log/getApplicationErrorLogCount');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'GET', '/log-platform/application/list');
insert into `sys_menu_resource` (`menu_id`, `method`, `path`) values(1, 'GET', '/log-platform/analysis/getErrorLogDistribution');


insert into `sys_menu_resource`  (`menu_id`, `method`, `path`) values(2, 'GET', '/auth/menu');
insert into `sys_menu_resource`  (`menu_id`, `method`, `path`) values(2, 'GET', '/auth/workspace');
insert into `sys_menu_resource`  (`menu_id`, `method`, `path`) values(2, 'GET', '/auth/meta');
insert into `sys_menu_resource`  (`menu_id`, `method`, `path`) values(2, 'POST', '/auth/sys/workSpace/list');
insert into `sys_menu_resource`  (`menu_id`, `method`, `path`) values(2, 'POST', '/auth/sys/workSpace/update');
insert into `sys_menu_resource`  (`menu_id`, `method`, `path`) values(2, 'POST', '/auth/sys/workSpace/insert');
insert into `sys_menu_resource`  (`menu_id`, `method`, `path`) values(2, 'POST', '/auth/sys/workSpace/list');
insert into `sys_menu_resource`  (`menu_id`, `method`, `path`) values(2, 'POST', '/auth/sys/dataGroup/bizTree');
insert into `sys_menu_resource`  (`menu_id`, `method`, `path`) values(2, 'POST', '/auth/sys/workSpace/delete');
insert into `sys_menu_resource`  (`menu_id`, `method`, `path`) values(2, 'POST', '/auth/sys/workSpace/user/insertUser');
insert into  `sys_menu_resource` (`menu_id`, `method`, `path`) values(2, 'GET', '/auth/sys/user/getDefaultPwd');
insert into  `sys_menu_resource` (`menu_id`, `method`, `path`) values(2, 'GET', '/auth/sys/workSpace/listTotalDrown');



insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/dataGroup/dataGroupTree');
insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/dataGroup/dataGroupTree');
insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/dataGroup/moveDataGroup');
insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/application/delete');
insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/application/update');
insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/application/insert');
insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/dataGroup/update');
insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/dataGroup/insert');
insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/dataGroup/delete');
insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/dataGroup/update');
insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/application/page');
insert into  `sys_menu_resource`  (`menu_id`, `method`, `path`) values(3, 'POST', '/auth/sys/dataGroup/insert');

insert into  `sys_menu_resource` (`menu_id`, `method`, `path`) values(4, 'POST', '/auth/sys/workSpace/user/save');
insert into  `sys_menu_resource` (`menu_id`, `method`, `path`) values(4, 'GET', '/auth/sys/role/listSpaceTotal');
insert into  `sys_menu_resource` (`menu_id`, `method`, `path`) values(4, 'GET', '/auth/sys/workSpace/user/getDetail');
insert into  `sys_menu_resource` (`menu_id`, `method`, `path`) values(4, 'POST', '/auth/sys/workSpace/user/batchRemove');
insert into  `sys_menu_resource` (`menu_id`, `method`, `path`) values(4, 'POST', '/auth/sys/workSpace/user/save');
insert into  `sys_menu_resource` (`menu_id`, `method`, `path`) values(4, 'GET', '/auth/sys/role/detail');
insert into  `sys_menu_resource` (`menu_id`, `method`, `path`) values(4, 'GET', '/auth/sys/workSpace/user/listUserDrown');
insert into  `sys_menu_resource` (`menu_id`, `method`, `path`) values(4, 'POST', '/auth/sys/workSpace/user/page');


insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(5, 'POST', '/auth/sys/role/page');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(5, 'GET', '/auth/sys/role/listTemplate');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(5, 'GET', '/auth/sys/role/getAuthMenuTree');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(5, 'POST', '/auth/sys/role/delete');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(5, 'POST', '/auth/sys/role/delete');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(5, 'POST', '/auth/sys/role/update');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(5, 'GET', '/auth/sys/role/detail');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(5, 'POST', '/auth/sys/role/insert');

insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(6, 'POST', '/auth/sys/user/page');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(6, 'GET', '/auth/sys/user/getDefaultPwd');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(6, 'POST', '/auth/sys/user/delete');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(6, 'POST', '/auth/sys/user/resetPassword');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(6, 'POST', '/auth/sys/user/update');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(6, 'POST', '/auth/sys/user/changeActive');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(6, 'POST', '/auth/sys/user/insert');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(6, 'GET', '/auth/sys/workSpace/listTotalDrown');

insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(7, 'GET', '/auth/sys/menu/getModuleSubTree');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(7, 'POST', '/auth/sys/menu/delete');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(7, 'POST', '/auth/sys/menu/update');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(7, 'GET', '/auth/sys/menu/detail');
insert into   `sys_menu_resource` (`menu_id`, `method`, `path`) values(7, 'POST', '/auth/sys/menu/insert');


-- 工作空间
CREATE TABLE `sys_work_space` (
  `work_space_id` bigint(20) AUTO_INCREMENT NOT NULL COMMENT '工作空间id',
  `work_space_name` varchar(100) DEFAULT NULL COMMENT '工作空间名称',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `isdefault` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认，内置空间不可删除',
  `isvalid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_user` bigint(20) DEFAULT NULL COMMENT '更新者',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`work_space_id`)
) COMMENT='工作空间';

-- -- 内置空间
insert into `sys_work_space` (`work_space_id`, `work_space_name`, `remark`, `isdefault`,`isvalid`, `create_user`, `create_time`, `modify_user`, `modify_time`) values(1,'内置空间',NULL,1,1,0,'2023-12-18 10:28:05',0,'2023-12-18 10:28:05');

-- 业务组/数据单元-按照序号排序
CREATE TABLE `sys_data_group` (
  `data_group_id` bigint(20) AUTO_INCREMENT NOT NULL COMMENT 'id',
  `work_space_id` bigint(20) NOT NULL COMMENT '工作空间id',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父id',
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
) COMMENT='业务组';

-- -- 内置业务组，数据单元
insert into `sys_data_group` (`data_group_id`, `work_space_id`, `parent_id`, `data_group_name`, `data_group_token`, `iselement`, `remark`, `sort_num`, `isvalid`, `create_user`, `create_time`, `modify_user`, `modify_time`) values('1','1','0','内置业务组',NULL,'0',NULL,'0','1','0','2023-12-20 14:42:11','0','2023-12-20 14:42:11');
insert into `sys_data_group` (`data_group_id`, `work_space_id`, `parent_id`, `data_group_name`, `data_group_token`, `iselement`, `remark`, `sort_num`, `isvalid`, `create_user`, `create_time`, `modify_user`, `modify_time`) values('2','1','1','内置数据单元','tok_294c7bd237c34500b25c862c833fd236','1',NULL,'0','1','0','2023-12-20 14:43:54','0','2023-12-20 14:43:54');

-- 应用
CREATE TABLE `sys_application` (
  `application_id` bigint(20) AUTO_INCREMENT NOT NULL COMMENT '应用id',
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
) COMMENT='应用';

-- 角色表
CREATE TABLE `sys_role` (
  `role_id` bigint(20) AUTO_INCREMENT NOT NULL COMMENT '角色id',
  `work_space_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '工作空间id',
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
) COMMENT='角色表';

-- -- 内置角色
INSERT INTO `sys_role` (`role_id`, `work_space_id`, `role_name`, `issuperadmin`, `isdefault`, `remark`, `isvalid`, `create_user`, `create_time`, `modify_user`, `modify_time`) VALUES(1,0,'超管',1,0,NULL,1,0,'2023-12-18 10:22:02',0,'2023-12-18 10:22:09');
INSERT INTO `sys_role` (`role_id`, `work_space_id`, `role_name`, `issuperadmin`, `isdefault`, `remark`, `isvalid`, `create_user`, `create_time`, `modify_user`, `modify_time`) VALUES(2,0,'空间管理员',0,1,NULL,1,0,'2023-12-18 10:22:45',0,'2023-12-18 10:22:45');

-- 用户工作空间关系
CREATE TABLE `sys_user_work_space` (
  `id` bigint(20) AUTO_INCREMENT NOT NULL COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `work_space_id` bigint(20) NOT NULL COMMENT '工作空间id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_user_work_group_user_work_space_id` (`user_id`,`work_space_id`)
) COMMENT='用户工作空间关系';

-- -- 内置空间管理员用户
insert into `sys_user_work_space` (`id`, `user_id`, `work_space_id`, `create_time`) values(1,2,1,'2023-12-18 10:29:05');

-- 用户角色关系
CREATE TABLE `sys_user_role` (
  `id` bigint(20) AUTO_INCREMENT NOT NULL COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `work_space_id` bigint(20) DEFAULT NULL COMMENT '工作空间id',
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `sign` varchar(1000) NULL COMMENT '利川完整性校验加密字段',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_user_role_user_space_role_id` (`user_id`,`work_space_id`,`role_id`)
) COMMENT='用户角色关系';

-- -- 角色关系
insert into `sys_user_role` (`id`, `user_id`, `work_space_id`, `role_id`, `create_time`) values(1,1,0,1,'2023-12-18 10:30:16');
insert into `sys_user_role` (`id`, `user_id`, `work_space_id`, `role_id`, `create_time`) values(2,2,1,2,'2023-12-18 10:30:16');

-- 角色菜单关系
CREATE TABLE `sys_role_menu` (
  `id` bigint(20) AUTO_INCREMENT NOT NULL COMMENT '主键',
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_role_menu_role_menu_id` (`role_id`,`menu_id`)
) COMMENT='角色菜单关系';

-- -- 空间管理员角色菜单
DELETE FROM `sys_role_menu` WHERE `role_id` = 2;
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`,`create_time`) SELECT 2,`menu_id`,NOW() FROM `sys_menu` WHERE `menu_no` NOT IN ('yonghuchi','caidan');

create table sys_shortcut_menu
(
    id           bigint auto_increment primary key,
    menu_id      bigint                             not null,
    menu_path    varchar(100)                       not null,
    account      varchar(100)                       not null,
    workspace_id bigint                             not null,
    create_time  datetime default CURRENT_TIMESTAMP not null,
    modify_time  datetime default CURRENT_TIMESTAMP not null
)COMMENT='快捷菜单表';

CREATE TABLE IF NOT EXISTS `sys_deploy_meta_info` (
  `id` bigint auto_increment primary key  COMMENT '主键id',
  `module_name` varchar(100) NOT NULL COMMENT '模块名称',
  `module_url` varchar(100) NOT NULL COMMENT '前端用于微服务之间的跳转',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` datetime not null  default CURRENT_TIMESTAMP COMMENT '更新时间'
)COMMENT='部署信息表';
