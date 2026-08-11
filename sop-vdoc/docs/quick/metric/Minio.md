# Minio
MinIO 性能指标展示，包括 MinIO 在线时长、存储空间分布、bucket 明细、文件大小区间分布、S3 TTFB (s) 分布、S3 流量、S3 请求等。

## 配置

### 版本支持

- MinIO 版本：ALL

说明：示例 MinIO 版本为 RELEASE.2022-06-25T15-50-16Z (commit-id=bd099f5e71d0ea511846372869bfcb280a5da2f6)

### 指标采集

MinIO 默认已暴露 [metric](https://docs.min.io/minio/baremetal/monitoring/metrics-alerts/collect-minio-metrics-using-prometheus.html?ref=con#minio-metrics-collect-using-prometheus) ，可以直接通过 Prometheus 来采集相关指标。

- 使用`minio-client`（简称`mc`）创建授权信息

```
$ mc alias set myminio http://192.168.0.210:9000 minioadmin minioadmin

scrape_configs:
- job_name: minio-job
  bearer_token: eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjQ4MTAwNzIxNDQsImlzcyI6InByb21ldGhldXMiLCJzdWIiOiJtaW5pb2FkbWluIn0.tzoJ7ifMxgx4jXfUKdD_Sq5Ll2-YlbaBu6FuNTZcc88t9o9STyg4yicRAgYmezVGFwYR2VFKvBSBnOnVnb0n4w
  metrics_path: /minio/v2/metrics/cluster
  scheme: http
  static_configs:
  - targets: ['192.168.0.210:9000']
```

> **注意：**
>
> Minio 只提供了通过 `mc` 来生成`token` 信息，可用于`prometheus` 指标采集。其中并不包含生成对应 prometheus server, 输出信息包含了 `bearer_token`、`metrics_path`、`scheme`以及`targets`，通过这些信息可以进行拼装最终的 url 。

在指标接入页面修改配置，集群也只采集一个节点url，会收集所有节点指标：
```text
[[inputs.prom]]
  # 是否启用该input
  enable_input = true

  # Exporter URLs
  urls = ["http://192.168.0.210:9000/minio/v2/metrics/cluster"]

  # 自定义认证方式，目前仅支持 Bearer Token
  # token 和 token_file: 仅需配置其中一项即可
  [inputs.prom.auth]
    type = "bearer_token"
    token = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjQ4MTAwNzIxNDQsImlzcyI6InByb21ldGhldXMiLCJzdWIiOiJtaW5pb2FkbWluIn0.tzoJ7ifMxgx4jXfUKdD_Sq5Ll2-YlbaBu6FuNTZcc88t9o9STyg4yicRAgYmezVGFwYR2VFKvBSBnOnVnb0n4w"
  # token_file = "/tmp/token"
  
  # 自定义标签，修改cluster_name，将作为监控视图变量
  [inputs.prom.tags]
   cluster_name = "default_cluster_name"
```

主要参数说明 ：

- urls：`prometheus` 指标地址，这里填写 MinIO 暴露出来的指标 url
- source：采集器别名，建议写成`minio`
- interval：采集间隔
- metric_name_filter: 指标过滤，只采集需要的指标项
- tls_open：TLS 配置
- metric_types：指标类型，不填，代表采集所有指标
- tags_ignore： 忽略不需要的 tag
- [inputs.prom.auth]：配置授权信息
- token : bearer_token 值

## 指标

| 指标                              | 含义                    |
| :-------------------------------- | :---------------------- |
| node_process_uptime_seconds       | 节点在线时长            |
| node_disk_free_bytes              | 节点空间空闲大小        |
| node_disk_used_bytes              | 节点空间使用大小        |
| node_file_descriptor_open_total   | 节点文件描述打开次数    |
| node_go_routine_total             | 节点 go_routine 次数    |
| cluster_disk_online_total         | 集群磁盘在线数          |
| cluster_disk_offline_total        | 集群磁盘离线数          |
| bucket_usage_object_total         | bucket 已用对象数       |
| bucket_usage_total_bytes          | bucket 已用字节         |
| bucket_objects_size_distribution  | bucket 对象大小区间分布 |
| s3_traffic_received_bytes         | s3 接收流量             |
| s3_traffic_sent_bytes             | s3 发送流量             |
| s3_requests_total                 | s3 请求总数             |
| s3_requests_waiting_total         | s3 正在等待请求数       |
| s3_requests_errors_total          | s3 异常总数             |
| s3_requests_4xx_errors_total      | s3 4xx 异常数           |
| s3_time_ttfb_seconds_distribution | s3 TTFB                 |
| usage_last_activity_nano_seconds  | 自上使用活动以来的时间  |
