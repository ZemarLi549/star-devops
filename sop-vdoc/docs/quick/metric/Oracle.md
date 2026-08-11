# Oracle
## 配置

Oracle 监控指标采集，具有以下数据收集功能

- Process 相关
- Table Space 相关数据
- System 数据采集
- 自定义查询数据采集

已测试的版本：

- Oracle 19c
- Oracle 12c
- Oracle 11g

### 前置条件

- 创建监控账号

如果是使用单 PDB 或者非 CDB 实例，一个本地用户(local user)就足够了：

```
-- Create the datasleuthuser. Replace the password placeholder with a secure password.
CREATE USER datasleuthIDENTIFIED BY <PASSWORD>;

-- Grant access to the datasleuthuser.
GRANT CONNECT, CREATE SESSION TO datasleuth;
GRANT SELECT_CATALOG_ROLE to datasleuth;
GRANT SELECT ON DBA_TABLESPACE_USAGE_METRICS TO datasleuth;
GRANT SELECT ON DBA_TABLESPACES TO datasleuth;
GRANT SELECT ON DBA_USERS TO datasleuth;
GRANT SELECT ON SYS.DBA_DATA_FILES TO datasleuth;
GRANT SELECT ON V_$ACTIVE_SESSION_HISTORY TO datasleuth;
GRANT SELECT ON V_$ARCHIVE_DEST TO datasleuth;
GRANT SELECT ON V_$ASM_DISKGROUP TO datasleuth;
GRANT SELECT ON V_$DATABASE TO datasleuth;
GRANT SELECT ON V_$DATAFILE TO datasleuth;
GRANT SELECT ON V_$INSTANCE TO datasleuth;
GRANT SELECT ON V_$LOG TO datasleuth;
GRANT SELECT ON V_$OSSTAT TO datasleuth;
GRANT SELECT ON V_$PGASTAT TO datasleuth;
GRANT SELECT ON V_$PROCESS TO datasleuth;
GRANT SELECT ON V_$RECOVERY_FILE_DEST TO datasleuth;
GRANT SELECT ON V_$RESTORE_POINT TO datasleuth;
GRANT SELECT ON V_$SESSION TO datasleuth;
GRANT SELECT ON V_$SGASTAT TO datasleuth;
GRANT SELECT ON V_$SYSMETRIC TO datasleuth;
GRANT SELECT ON V_$SYSTEM_PARAMETER TO datasleuth;
```

如果想监控来自 CDB 和所有 PDB 中的表空间(Table Spaces)，需要一个有合适权限的公共用户(common user):

```
-- Create the datasleuthuser. Replace the password placeholder with a secure password.
CREATE USER datasleuthIDENTIFIED BY <PASSWORD>;

-- Grant access to the datasleuthuser.
ALTER USER datasleuthSET CONTAINER_DATA=ALL CONTAINER=CURRENT;
GRANT CONNECT, CREATE SESSION TO datasleuth;
GRANT SELECT_CATALOG_ROLE to datasleuth;
GRANT SELECT ON v_$instance TO datasleuth;
GRANT SELECT ON v_$database TO datasleuth;
GRANT SELECT ON v_$sysmetric TO datasleuth;
GRANT SELECT ON v_$system_parameter TO datasleuth;
GRANT SELECT ON v_$session TO datasleuth;
GRANT SELECT ON v_$recovery_file_dest TO datasleuth;
GRANT SELECT ON v_$active_session_history TO datasleuth;
GRANT SELECT ON v_$osstat TO datasleuth;
GRANT SELECT ON v_$restore_point TO datasleuth;
GRANT SELECT ON v_$process TO datasleuth;
GRANT SELECT ON v_$datafile TO datasleuth;
GRANT SELECT ON v_$pgastat TO datasleuth;
GRANT SELECT ON v_$sgastat TO datasleuth;
GRANT SELECT ON v_$log TO datasleuth;
GRANT SELECT ON v_$archive_dest TO datasleuth;
GRANT SELECT ON v_$asm_diskgroup TO datasleuth;
GRANT SELECT ON sys.dba_data_files TO datasleuth;
GRANT SELECT ON DBA_TABLESPACES TO datasleuth;
GRANT SELECT ON DBA_TABLESPACE_USAGE_METRICS TO datasleuth;
GRANT SELECT ON DBA_USERS TO datasleuth;
```

> 注意：上述的 SQL 语句由于 Oracle 版本的原因部分可能会出现 "表不存在" 等错误，忽略即可。

- 安装依赖包

根据操作系统和 Oracle 版本选择安装对应的安装包，参考[这里](https://oracle.github.io/odpi/doc/installation.html)

```
wget https://download.oracle.com/otn_software/linux/instantclient/2110000/instantclient-basiclite-linux.x64-21.10.0.0.0dbru.zip
unzip instantclient-basiclite-linux.x64-21.10.0.0.0dbru.zip
```

将解压后的目录文件路径添加到以下配置信息中的 `LD_LIBRARY_PATH` 环境变量路径中。

> 也可以直接下载我们预先准备好的依赖包：

```
wget https://static.guance.com/otn_software/instantclient/instantclient-basiclite-linux.x64-21.10.0.0.0dbru.zip \
    -O /usr/local/datasleuth/externals/instantclient-basiclite-linux.zip \
    && unzip /usr/local/datasleuth/externals/instantclient-basiclite-linux.zip -d /opt/oracle \
    && mv /opt/oracle/instantclient_21_10 /opt/oracle/instantclient;
```

- 部分系统需要安装额外的依赖库：

```
apt-get install -y libaio-dev libaio1
```

### 采集器配置

在指标接入页面修改相关配置。

>***tips：***
>
>上述配置会以命令行形式展示在进程列表中（包括密码），如果想隐藏密码，可以通过将密码写进环境变量 `ENV_INPUT_ORACLE_PASSWORD` 形式实现，示例：
>
>```
>envs = [
>  "ENV_INPUT_ORACLE_PASSWORD=<YOUR-SAFE-PASSWORD>"
>] 
>```

该环境变量在读取密码时有最高优先级，即只要出现该环境变量，那密码就以该环境变量中的值为准。

## 指标

以下所有数据采集，默认会追加名为 `host` 的全局 tag（tag 值为 datasleuth所在主机名），也可以在配置中通过 `[inputs.oracle.tags]` 指定其它标签：

```
 [inputs.oracle.tags]
  # some_tag = "some_value"
  # more_tag = "some_other_value"
  # ...
```

### `oracle_process`

- 标签

| Tag              | Description         |
| :--------------- | :------------------ |
| `host`           | Host name           |
| `oracle_server`  | Server addr         |
| `oracle_service` | Server service      |
| `pdb_name`       | PDB name            |
| `program`        | Program in progress |

- 指标列表

| Metric             | Description                                  | Type  | Unit |
| :----------------- | :------------------------------------------- | :---: | :--: |
| `pga_alloc_mem`    | PGA memory allocated by process              | float |  B   |
| `pga_freeable_mem` | PGA memory freeable by process               | float |  B   |
| `pga_max_mem`      | PGA maximum memory ever allocated by process | float |  B   |
| `pga_used_mem`     | PGA memory used by process                   | float |  B   |
| `pid`              | Oracle process identifier                    |  int  |  -   |

### `oracle_tablespace`

- 标签

| Tag               | Description      |
| :---------------- | :--------------- |
| `host`            | Host name        |
| `oracle_server`   | Server addr      |
| `oracle_service`  | Server service   |
| `pdb_name`        | PDB name         |
| `tablespace_name` | Table space name |

- 指标列表

| Metric       | Description         | Type  | Unit  |
| :----------- | :------------------ | :---: | :---: |
| `in_use`     | Table space in-use  | float | count |
| `off_use`    | Table space offline | float | count |
| `ts_size`    | Table space size    | float |   B   |
| `used_space` | Used space          | float | count |

### `oracle_system`

- 标签

| Tag              | Description    |
| :--------------- | :------------- |
| `host`           | Host name      |
| `oracle_server`  | Server addr    |
| `oracle_service` | Server service |
| `pdb_name`       | PDB name       |

- 指标列表

| Metric                      | Description                        | Type  |  Unit   |
| :-------------------------- | :--------------------------------- | :---: | :-----: |
| `active_sessions`           | Number of active sessions          | float |  count  |
| `buffer_cachehit_ratio`     | Ratio of buffer cache hits         | float | percent |
| `cache_blocks_corrupt`      | Corrupt cache blocks               | float |  count  |
| `cache_blocks_lost`         | Lost cache blocks                  | float |  count  |
| `consistent_read_changes`   | Consistent read changes per second | float |  count  |
| `consistent_read_gets`      | Consistent read gets per second    | float |  count  |
| `cursor_cachehit_ratio`     | Ratio of cursor cache hits         | float | percent |
| `database_cpu_time_ratio`   | Database CPU time ratio            | float | percent |
| `database_wait_time_ratio`  | Memory sorts per second            | float | percent |
| `db_block_changes`          | DB block changes per second        | float |  count  |
| `db_block_gets`             | DB block gets per second           | float |  count  |
| `disk_sorts`                | Disk sorts per second              | float |  count  |
| `enqueue_timeouts`          | Enqueue timeouts per second        | float |  count  |
| `execute_without_parse`     | Execute without parse ratio        | float |  count  |
| `gc_cr_block_received`      | GC CR block received               | float |  count  |
| `host_cpu_utilization`      | Host CPU utilization (%)           | float | percent |
| `library_cachehit_ratio`    | Ratio of library cache hits        | float | percent |
| `logical_reads`             | Logical reads per second           | float |  count  |
| `logons`                    | Number of logon attempts           | float |  count  |
| `memory_sorts_ratio`        | Memory sorts ratio                 | float | percent |
| `pga_over_allocation_count` | Over-allocating PGA memory count   | float |  count  |
| `physical_reads`            | Physical reads per second          | float |  count  |
| `physical_reads_direct`     | Physical reads direct per second   | float |  count  |
| `physical_writes`           | Physical writes per second         | float |  count  |
| `redo_generated`            | Redo generated per second          | float |  count  |
| `redo_writes`               | Redo writes per second             | float |  count  |
| `rows_per_sort`             | Rows per sort                      | float |  count  |
| `service_response_time`     | Service response time              | float |   sec   |
| `session_count`             | Session count                      | float |  count  |
| `session_limit_usage`       | Session limit usage                | float | percent |
| `shared_pool_free`          | Shared pool free memory %          | float | percent |
| `soft_parse_ratio`          | Soft parse ratio                   | float | percent |
| `sorts_per_user_call`       | Sorts per user call                | float |  count  |
| `temp_space_used`           | Temp space used                    | float |    B    |
| `user_rollbacks`            | Number of user rollbacks           | float |  count  |
