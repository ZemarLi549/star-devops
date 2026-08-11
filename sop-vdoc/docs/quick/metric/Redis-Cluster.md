# Redis Sentinel

Redis-sentinel 指标展示，包括 Redis 集群、Slaves、节点分布信息等。

## 安装部署

### 下载 redis-sentinel-exporter 指标采集器

下载地址 https://github.com/lrwh/redis-sentinel-exporter/releases

### 启动 redis-sentinel-exporter

```
nohup java -Xmx64m -jar redis-sentinel-exporter-0.2.jar --spring.redis.sentinel.master=mymaster --spring.redis.sentinel.nodes="127.0.0.1:26379,127.0.0.1:26380,127.0.0.1:26381" >/dev/null 2>redis-sentinel-exporter.log &
```

参数说明 spring.redis.sentinel.master ： 集群名称 spring.redis.sentinel.nodes ： 哨兵节点地址

指标暴露接口：http://localhost:6390/metrics

### 采集器配置

#### 指标采集

在指标接入页面修改相关配置。其它使用默认配置即可
```text
[[inputs.prom]]
  # 是否启用该input
  enable_input = true

  # Exporter URLs.
  urls = ["http://localhost:6390/metrics"]
```


主要参数说明

- urls：redis-sentinel-exporter 暴露出来的指标 url
- source：采集器别名
- interval：采集间隔
- measurement_prefix：指标前缀，便于指标分类查询
- tls_open：TLS 配置
- metric_types：指标类型，不填，代表采集所有指标
- [inputs.prom.tags]：额外定义的 tag


## 指标详解

| 指标                                 | 含义                     | 类型  |
| :----------------------------------- | :----------------------- | :---- |
| redis_sentinel_known_sentinels       | 哨兵实例数               | Gauge |
| redis_sentinel_known_slaves          | 集群slaves实例数         | Gauge |
| redis_sentinel_cluster_type          | 集群节点类型             | Gauge |
| redis_sentinel_link_pending_commands | 哨兵挂起命令数           | Gauge |
| redis_sentinel_odown_slaves          | slave客观宕机            | Gauge |
| redis_sentinel_sdown_slaves          | slave主观宕机            | Gauge |
| redis_sentinel_ok_slaves             | 正在运行的slave数        | Gauge |
| redis_sentinel_ping_latency          | 哨兵ping的延迟显示为毫秒 | Gauge |
| redis_sentinel_last_ok_ping_latency  | 哨兵ping成功的秒数       | Gauge |
| redis_sentinel_node_state            | redis 节点状态           | Gauge |




# Redis分片集群
## 安装部署
### 下载 redis_exporter 指标采集器
下载地址 https://github.com/oliver006/redis_exporter/releases

### 启动 redis_exporter
```text
chmod +x redis_exporter_1.62.0
# 每个节点启动一个 替换redis节点的ip、端口、密码 9121是指标采集暴露接口的端口
nohup ./redis_exporter_1.62.0 -is-cluster -redis-only-metrics -web.listen-address :9121 -redis.addr 127.0.0.1:36379 -redis.password 'password' >/dev/null 2>redis-exporter-36379.log &
```
指标暴露接口 http://localhost:9121/metrics

采集器启动参数说明，可通过 `./redis_exporter_1.62.0 -h` 查看帮助说明
```text
- `check-key-groups`：逗号分隔的lua正则表达式列表，用于对键进行分组。
- `check-keys`：逗号分隔的键模式列表，用于导出值和长度/大小，使用SCAN搜索。
- `check-keys-batch-size`：大致处理的键数量，每执行一次增加速度。警告：Redis仍然是单线程应用程序，大计数可能会影响生产环境。（默认值为1000）
- `check-single-keys`：逗号分隔的单个键列表，用于导出值和长度/大小。
- `check-single-streams`：逗号分隔的单个流列表，用于导出有关流、组和消费者的信息。
- `check-streams`：逗号分隔的流模式列表，用于导出有关流、组和消费者的信息，使用SCAN搜索。
- `config-command`：要用于CONFIG命令的内容，设置为"-"以跳过配置指标提取（默认为"CONFIG"）。
- `connection-timeout`：连接到Redis实例的超时时间（默认为"15s"）。
- `count-keys`：逗号分隔的模式列表，用于计数（例如：'db0=production_*,db3=sessions:*'），使用SCAN搜索。
- `debug`：输出详细的调试信息。
- `disable-exporting-key-values`：在使用check-keys/check-single-key时，是否禁用存储在redis中的键的值作为标签。
- `exclude-latency-histogram-metrics`：不要尝试收集延迟直方图指标。
- `export-client-list`：是否抓取客户端列表特定指标。
- `export-client-port`：在导出客户端列表时，是否包括客户端的端口。警告：包括端口会增加生成的指标数量，并会使Prometheus服务器占用更多内存。
- `include-config-metrics`：是否包含所有配置设置作为指标。
- `include-system-metrics`：是否包含系统指标，例如e.g. redis_total_system_memory_bytes。
- `is-cluster`：这是否是Redis集群（如果需要获取Redis集群上的键级别数据，请启用此选项）。
- `is-tile38`：是否抓取Tile38特定指标。
- `log-format`：日志格式，有效选项为txt和json（默认为"txt"）。
- `max-distinct-key-groups`：具有最大内存利用率的不同键组的数量，将按数据库显示为不同的度量指标，剩余的键组将聚合在“溢出”桶中（默认值为100）。
- `namespace`：指标的命名空间（默认为"redis"）。
- `ping-on-connect`：连接后是否向Redis实例发送ping。
- `redact-config-metrics`：是否省略包含可能包含敏感信息的配置设置（如密码）（默认为true）。
- `redis-only-metrics`：是否还导出go运行时指标。
- `redis.addr`：要抓取的Redis实例的地址（默认为"redis://localhost:6379"）。
- `redis.password`：要抓取的Redis实例的密码。
- `redis.password-file`：要抓取的Redis实例的密码文件。
- `redis.user`：用于身份验证的Redis ACL的用户名称（仅适用于Redis 6.0及更高版本）。
- `script`：逗号分隔的路径列表，用于Redis Lua脚本以获取额外指标。
- `set-client-name`：是否将客户端名称设置为redis_exporter（默认为true）。
- `skip-tls-verification`：是否跳过TLS验证。
- `streams-exclude-consumer-metrics`：不要为流收集消费者指标（减少基数）。
- `tls-ca-cert-file`：如果服务器要求TLS客户端身份验证，则CA证书文件的名称（包括完整路径）。
- `tls-client-cert-file`：如果服务器要求TLS客户端身份验证，则客户端证书文件的名称（包括完整路径）。
- `tls-client-key-file`：如果服务器要求TLS客户端身份验证，则客户端密钥文件的名称（包括完整路径）。
- `tls-server-ca-cert-file`：如果Web界面和遥测应要求TLS客户端身份验证，则CA证书文件的名称（包括完整路径）。
- `tls-server-cert-file`：如果Web界面和遥测应使用TLS，则服务器证书文件的名称（包括完整路径）。
- `tls-server-key-file`：如果Web界面和遥测应使用TLS，则服务器密钥文件的名称（包括完整路径）。
- `tls-server-min-version`：在使用TLS时，可接受的Web界面和遥测的最小TLS版本（默认为"TLS1.2"）。
- `version`：显示版本信息并退出。
- `web.listen-address`：Web界面和遥测要监听的地址（默认为":9121"）。
- `web.telemetry-path`：暴露指标的路径下。（默认为"/metrics"）
```

### 采集器配置

#### 指标采集

在指标接入页面修改相关配置。其它使用默认配置即可
```text
[[inputs.prom]]
  # 是否启用该input
  enable_input = true

  # Exporter URLs.
  urls = ["http://127.0.0.1:9121/metrics","http://127.0.0.1:9121/metrics","http://127.0.0.1:9121/metrics","http://127.0.0.1:9121/metrics"]
  
  # 自定义标签，修改cluster_name，将作为监控视图变量
  [inputs.prom.tags]
   cluster_name = "default_cluster_name"
```

#### 常用采集指标说明
```text
- redis_connected_clients: 当前与Redis服务器建立连接的客户端数量。
- redis_used_memory: Redis服务器当前使用的内存量。
- redis_total_commands_processed: Redis服务器处理的命令总数。
- redis_keyspace_hits: Redis服务器命中缓存的键的总数。
- redis_keyspace_misses: Redis服务器未命中缓存的键的总数。
```