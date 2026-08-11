-- 鑫图平台用户外部身份字段升级脚本
-- 适用于已经存在 sys_user 表的 MySQL 环境。请在目标库执行一次。
-- 该脚本只新增字段，不修改已有用户数据。

ALTER TABLE `sys_user`
  ADD COLUMN `feishu_user_id` varchar(128) DEFAULT NULL COMMENT '飞书 user_id' AFTER `phone`,
  ADD COLUMN `it_workbench_user_id` varchar(128) DEFAULT NULL COMMENT '三方 IT 工作台用户 ID' AFTER `feishu_user_id`,
  ADD COLUMN `identity_source` varchar(20) NOT NULL DEFAULT 'LOCAL' COMMENT '身份来源：LOCAL/LDAP/SYNC' AFTER `it_workbench_user_id`,
  ADD COLUMN `ldap_account` varchar(128) DEFAULT NULL COMMENT 'LDAP 登录账号或映射账号' AFTER `identity_source`;

-- 可选索引：只有后续按外部 ID 做同步或反查时再创建，避免无需求时增加写入成本。
-- CREATE INDEX `idx_sys_user_feishu_user_id` ON `sys_user` (`feishu_user_id`);
-- CREATE INDEX `idx_sys_user_it_workbench_user_id` ON `sys_user` (`it_workbench_user_id`);
