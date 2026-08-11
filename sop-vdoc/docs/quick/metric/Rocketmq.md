# Rocketmq
RocketMQ 指标展示，包括生产者 TPS/消息大小、消费者 TPS/消息大小、消息堆积、topic 信息等

## 安装配置

说明：示例 Linux 版本为 CentOS Linux release 7.8.2003 (Core)，Windows 版本请修改对应的配置文件

- 操作系统支持：Linux / Windows
- 服务器安装 `rocketmq-exporter`

### 安装 exporter

发布包中提供了编译好的rocketmq-exporter-0.0.2-SNAPSHOT.jar，直接用就行。也可以从网上下载，但是要根据源码自己编译，方法如下：

- 拉取 `rocketmq-exporter`

```
git clone https://github.com/apache/rocketmq-exporter.git
```

- 进入安装目录

```
cd rocketmq-exporter/
```

- 构建安装包 (2选1即可)

（1）构建 jar 包方式

```
mvn clean install
```

构建完成，进入 target 目录

```
cd target
```

启动 jar 包 (替换命令行中 `nameserverip` 地址)

```
nohup java -jar target/rocketmq-exporter-0.0.2-SNAPSHOT.jar --rocketmq.config.namesrvAddr=nameserverip:9876 --rocketmq.config.enableACL="true" --rocketmq.config.accessKey="rocketmq2" --rocketmq.config.secretKey="FAuzUc2eM65mJVgx" &
```

（2）构建 Docker 镜像方式

```
mvn package -Dmaven.test.skip=true docker:build
```

使用镜像启动 Docker (替换命令行中 `nameserverip` 地址)

```
docker run -d --net="host" --name rocketmq-exporter -p 5557:5557 docker.io/rocketmq-exporter --rocketmq.config.namesrvAddr=nameserverip:9876 --rocketmq.config.enableACL="true" --rocketmq.config.accessKey="rocketmq2" --rocketmq.config.secretKey="FAuzUc2eM65mJVgx"
```

- 测试 `rocketmq-exporter` 是否正常

```
curl http://127.0.0.1:5557/metrics
```

### 采集配置
指标接入页面修改相关配置。
```text
# 是否启用该input
  enable_input = false

  # Exporter URLs.
  urls = ["http://127.0.0.1:5557/metrics"]
  
  # 自定义标签，修改cluster_name，将作为监控视图变量
  [inputs.prom.tags]
   cluster_name = "default_cluster_name"
```

若接入集群，则exporter启动时namesrvAddr配置多节点。如：
```
nohup java -jar target/rocketmq-exporter-0.0.2-SNAPSHOT.jar --rocketmq.config.namesrvAddr="nameserverip:9876;nameserverip:9876" --rocketmq.config.enableACL="true" --rocketmq.config.accessKey="rocketmq2" --rocketmq.config.secretKey="FAuzUc2eM65mJVgx" &
```

主要参数说明

- urls：exporter 地址，建议填写内网地址，远程采集可使用公网
- ignore_req_err：忽略对 url 的请求错误
- source：采集器别名
- metrics_types：默认只采集 counter 和 gauge 类型的指标
- interval：采集频率

```
[[inputs.prom]]
  urls = ["http://127.0.0.1:5557/metrics"]
  ignore_req_err = false
  source = "prom"
# metric_types 需要选择空，rocketmq-exporter 没有指定数据类型
  metric_types = []
  interval = "60s"
```

## 指标

| 指标                                      | 描述                               | 数据类型 |
| :---------------------------------------- | :--------------------------------- | :------- |
| `rocketmq_broker_tps`                     | broker每秒生产消息数量             | int      |
| `rocketmq_broker_qps`                     | broker每秒消费消息数量             | int      |
| `rocketmq_producer_tps`                   | 某个topic每秒生产的消息数量        | int      |
| `rocketmq_producer_put_size`              | 某个topic每秒生产的消息大小(字节)  | int      |
| `rocketmq_producer_offset`                | 某个topic的生产消息的进度          | int      |
| `rocketmq_consumer_tps`                   | 某个消费组每秒消费的消息数量       | int      |
| `rocketmq_consumer_get_size`              | 某个消费组每秒消费的消息大小(字节) | int      |
| `rocketmq_consumer_offset`                | 某个消费组的消费消息的进度         | int      |
| `rocketmq_group_get_latency_by_storetime` | 某个消费组的消费延时时间           | int      |
| `rocketmq_message_accumulati`             | 消息堆积量                         | int      |

