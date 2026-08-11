# PostgreSQL
PostgreSQL 采集器可以从 PostgreSQL 实例中采集实例运行状态指标，并将指标采集到观测云，帮助监控分析 PostgreSQL 各种异常情况。

## 配置

### 前置条件

- PostgreSQL 版本 >= 9.0
- 创建监控帐号

```
-- PostgreSQL >= 10
create user datasleuthwith password '<PASSWORD>';
grant pg_monitor to datasleuth;
grant SELECT ON pg_stat_database to datasleuth;

-- PostgreSQL < 10
create user datasleuthwith password '<PASSWORD>';
grant SELECT ON pg_stat_database to datasleuth;
```

### 采集器配置

在指标接入页面修改相关配置。

## 指标

以下所有数据采集，默认会追加名为 `host` 的全局 tag（tag 值为 datasleuth所在主机名），也可以在配置中通过 `[[inputs.postgresql.tags]]` 另择 host 来命名。

### `postgresql`

- 标签

| Tag      | Description        |
| :------- | :----------------- |
| `db`     | The database name  |
| `server` | The server address |

- 指标列表

| Metric                     | Description                                                  | Type  | Unit  |
| :------------------------- | :----------------------------------------------------------- | :---: | :---: |
| `active_time`              | Time spent executing SQL statements in this database, in milliseconds. | float | count |
| `blks_hit`                 | The number of times disk blocks were found in the buffer cache, preventing the need to read from the database. |  int  | count |
| `blks_read`                | The number of disk blocks read in this database.             |  int  | count |
| `database_size`            | The disk space used by this database.                        |  int  | count |
| `deadlocks`                | The number of deadlocks detected in this database.           |  int  | count |
| `idle_in_transaction_time` | Time spent idling while in a transaction in this database, in milliseconds. | float | count |
| `numbackends`              | The number of active connections to this database.           |  int  | count |
| `session_time`             | Time spent by database sessions in this database, in milliseconds. | float | count |
| `sessions`                 | Total number of sessions established to this database.       |  int  | count |
| `sessions_abandoned`       | Number of database sessions to this database that were terminated because connection to the client was lost. |  int  | count |
| `sessions_fatal`           | Number of database sessions to this database that were terminated by fatal errors. |  int  | count |
| `sessions_killed`          | Number of database sessions to this database that were terminated by operator intervention. |  int  | count |
| `temp_bytes`               | The amount of data written to temporary files by queries in this database. |  int  | count |
| `temp_files`               | The number of temporary files created by queries in this database. |  int  | count |
| `tup_deleted`              | The number of rows deleted by queries in this database.      |  int  | count |
| `tup_fetched`              | The number of rows fetched by queries in this database.      |  int  | count |
| `tup_inserted`             | The number of rows inserted by queries in this database.     |  int  | count |
| `tup_returned`             | The number of rows returned by queries in this database.     |  int  | count |
| `tup_updated`              | The number of rows updated by queries in this database.      |  int  | count |
| `wraparound`               | The number of transactions that can occur until a transaction wraparound. | float | count |
| `xact_commit`              | The number of transactions that have been committed in this database. |  int  | count |
| `xact_rollback`            | The number of transactions that have been rolled back in this database. |  int  | count |

### `postgresql_lock`

- 标签

| Tag        | Description        |
| :--------- | :----------------- |
| `db`       | The database name  |
| `locktype` | The lock type      |
| `mode`     | The lock mode      |
| `schema`   | The schema name    |
| `server`   | The server address |
| `table`    | The table name     |

- 指标列表

| Metric       | Description                                   | Type | Unit  |
| :----------- | :-------------------------------------------- | :--: | :---: |
| `lock_count` | The number of locks active for this database. | int  | count |

### `postgresql_index`

- 标签

| Tag      | Description        |
| :------- | :----------------- |
| `db`     | The database name  |
| `index`  | The index name     |
| `schema` | The schema name    |
| `server` | The server address |
| `table`  | The table name     |

- 指标列表

| Metric          | Description                                                  | Type | Unit  |
| :-------------- | :----------------------------------------------------------- | :--: | :---: |
| `idx_scan`      | The number of index scans initiated on this table, tagged by index. | int  | count |
| `idx_tup_fetch` | The number of live rows fetched by index scans.              | int  | count |
| `idx_tup_read`  | The number of index entries returned by scans on this index. | int  | count |

### `postgresql_replication`

- 标签

| Tag      | Description        |
| :------- | :----------------- |
| `db`     | The database name  |
| `server` | The server address |

- 指标列表

| Metric                    | Description                                                  | Type | Unit |
| :------------------------ | :----------------------------------------------------------- | :--: | :--: |
| `replication_delay`       | The current replication delay in seconds. Only available with `postgresql` 9.1 and newer. | int  |  s   |
| `replication_delay_bytes` | The current replication delay in bytes. Only available with `postgresql` 9.2 and newer. | int  |  B   |

### `postgresql_size`

- 标签

| Tag      | Description        |
| :------- | :----------------- |
| `db`     | The database name  |
| `schema` | The schema name    |
| `server` | The server address |
| `table`  | The table name     |

- 指标列表

| Metric       | Description                                                  | Type | Unit |
| :----------- | :----------------------------------------------------------- | :--: | :--: |
| `index_size` | The total disk space used by indexes attached to the specified table. | int  |  B   |
| `table_size` | The total disk space used by the specified table. Includes TOAST, free space map, and visibility map. Excludes indexes. | int  |  B   |
| `total_size` | The total disk space used by the table, including indexes and TOAST data. | int  |  B   |

### `postgresql_statio`

- 标签

| Tag      | Description        |
| :------- | :----------------- |
| `db`     | The database name  |
| `schema` | The schema name    |
| `server` | The server address |
| `table`  | The table name     |

- 指标列表

| Metric            | Description                                                  | Type | Unit  |
| :---------------- | :----------------------------------------------------------- | :--: | :---: |
| `heap_blks_hit`   | The number of buffer hits in this table.                     | int  | count |
| `heap_blks_read`  | The number of disk blocks read from this table.              | int  | count |
| `idx_blks_hit`    | The number of buffer hits in all indexes on this table.      | int  | count |
| `idx_blks_read`   | The number of disk blocks read from all indexes on this table. | int  | count |
| `tidx_blks_hit`   | The number of buffer hits in this table's TOAST table index. | int  | count |
| `tidx_blks_read`  | The number of disk blocks read from this table's TOAST table index. | int  | count |
| `toast_blks_hit`  | The number of buffer hits in this table's TOAST table.       | int  | count |
| `toast_blks_read` | The number of disk blocks read from this table's TOAST table. | int  | count |

### `postgresql_stat`

- 标签

| Tag      | Description        |
| :------- | :----------------- |
| `db`     | The database name  |
| `schema` | The schema name    |
| `server` | The server address |
| `table`  | The table name     |

- 指标列表

| Metric              | Description                                                  | Type | Unit  |
| :------------------ | :----------------------------------------------------------- | :--: | :---: |
| `analyze_count`     | The number of times this table has been manually analyzed.   | int  | count |
| `autoanalyze_count` | The number of times this table has been analyzed by the `autovacuum` daemon. | int  | count |
| `autovacuum_count`  | The number of times this table has been vacuumed by the `autovacuum` daemon. | int  | count |
| `idx_scan`          | The number of index scans initiated on this table, tagged by index. | int  | count |
| `idx_tup_fetch`     | The number of live rows fetched by index scans.              | int  | count |
| `n_dead_tup`        | The estimated number of dead rows.                           | int  | count |
| `n_live_tup`        | The estimated number of live rows.                           | int  | count |
| `n_tup_del`         | The number of rows deleted by queries in this database.      | int  | count |
| `n_tup_hot_upd`     | The number of rows HOT updated, meaning no separate index update was needed. | int  | count |
| `n_tup_ins`         | The number of rows inserted by queries in this database.     | int  | count |
| `n_tup_upd`         | The number of rows updated by queries in this database.      | int  | count |
| `seq_scan`          | The number of sequential scans initiated on this table.      | int  | count |
| `seq_tup_read`      | The number of live rows fetched by sequential scans.         | int  | count |
| `vacuum_count`      | The number of times this table has been manually vacuumed.   | int  | count |

### `postgresql_slru`

- 标签

| Tag      | Description            |
| :------- | :--------------------- |
| `db`     | The database name      |
| `name`   | The name of the `SLRU` |
| `server` | The server address     |

- 指标列表

| Metric         | Description                                                  | Type | Unit  |
| :------------- | :----------------------------------------------------------- | :--: | :---: |
| `blks_exists`  | Number of blocks checked for existence for this `SLRU` (simple least-recently-used) cache. | int  | count |
| `blks_hit`     | Number of times disk blocks were found already in the `SLRU` (simple least-recently-used.) | int  | count |
| `blks_read`    | Number of disk blocks read for this `SLRU` (simple least-recently-used) cache. `SLRU` caches are created with a fixed number of pages. | int  | count |
| `blks_written` | Number of disk blocks written for this `SLRU` (simple least-recently-used) cache. | int  | count |
| `blks_zeroed`  | Number of blocks zeroed during initializations of `SLRU` (simple least-recently-used) cache. | int  | count |
| `flushes`      | Number of flush of dirty data for this `SLRU` (simple least-recently-used) cache. | int  | count |
| `truncates`    | Number of truncates for this `SLRU` (simple least-recently-used) cache. | int  | count |

### `postgresql_bgwriter`

- 标签

| Tag      | Description        |
| :------- | :----------------- |
| `db`     | The database name  |
| `server` | The server address |

- 指标列表

| Metric                  | Description                                                  | Type  | Unit  |
| :---------------------- | :----------------------------------------------------------- | :---: | :---: |
| `buffers_alloc`         | The number of buffers allocated                              |  int  | count |
| `buffers_backend`       | The number of buffers written directly by a backend.         |  int  | count |
| `buffers_backend_fsync` | The of times a backend had to execute its own fsync call instead of the background writer. |  int  | count |
| `buffers_checkpoint`    | The number of buffers written during checkpoints.            |  int  | count |
| `buffers_clean`         | The number of buffers written by the background writer.      |  int  | count |
| `checkpoint_sync_time`  | The total amount of checkpoint processing time spent synchronizing files to disk. | float |  ms   |
| `checkpoint_write_time` | The total amount of checkpoint processing time spent writing files to disk. | float |  ms   |
| `checkpoints_req`       | The number of requested checkpoints that were performed.     |  int  | count |
| `checkpoints_timed`     | The number of scheduled checkpoints that were performed.     |  int  | count |
| `maxwritten_clean`      | The number of times the background writer stopped a cleaning scan due to writing too many buffers. |  int  | count |

### `postgresql_connection`

- 标签

| Tag      | Description        |
| :------- | :----------------- |
| `db`     | The database name  |
| `server` | The server address |

- 指标列表

| Metric                      | Description                                                  | Type  | Unit  |
| :-------------------------- | :----------------------------------------------------------- | :---: | :---: |
| `max_connections`           | The maximum number of client connections allowed to this database. | float | count |
| `percent_usage_connections` | The number of connections to this database as a fraction of the maximum number of allowed connections. | float | count |

### `postgresql_conflict`

- 标签

| Tag      | Description        |
| :------- | :----------------- |
| `db`     | The database name  |
| `server` | The server address |

- 指标列表

| Metric             | Description                                                  | Type | Unit  |
| :----------------- | :----------------------------------------------------------- | :--: | :---: |
| `confl_bufferpin`  | Number of queries in this database that have been canceled due to pinned buffers. | int  | count |
| `confl_deadlock`   | Number of queries in this database that have been canceled due to deadlocks. | int  | count |
| `confl_lock`       | Number of queries in this database that have been canceled due to dropped tablespaces. This will occur when a `temp_tablespace` is dropped while being used on a standby. | int  | count |
| `confl_snapshot`   | Number of queries in this database that have been canceled due to old snapshots. | int  | count |
| `confl_tablespace` | Number of queries in this database that have been canceled due to dropped tablespaces. This will occur when a `temp_tablespace` is dropped while being used on a standby. | int  | count |

### `postgresql_archiver`

- 标签

| Tag      | Description        |
| :------- | :----------------- |
| `db`     | The database name  |
| `server` | The server address |

- 指标列表

| Metric                  | Description                                               | Type | Unit  |
| :---------------------- | :-------------------------------------------------------- | :--: | :---: |
| `archived_count`        | Number of WAL files that have been successfully archived. | int  | count |
| `archived_failed_count` | Number of failed attempts for archiving WAL files.        | int  | count |
