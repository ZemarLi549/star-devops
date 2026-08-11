# Activemq

## 配置

### 1. 下载JMX  Exporter

JMX Exporter 利用 Java 的 JMX 机制来读取 JVM 运行时的一些监控数据，然后将其转换为 Prometheus 所认知的 metrics 格式，以便让 Prometheus 对其进行监控采集。
 下载 [jmx_prometheus_javaagent](https://github.com/prometheus/jmx_exporter/releases)到/data/public/jmx_prometheus_javaagent/目录。

### 2.创建avtivemq.yml 配置文件
在/data/public/jmx_prometheus_javaagent/目录下创建activemq.yml文件，文件内容如下

```
wercaseOutputName: true
lowercaseOutputLabelNames: true
blacklistObjectNames:
  - "org.apache.activemq:clientId=*,*"
whitelistObjectNames:
  - "org.apache.activemq:destinationType=Queue,*"
  - "org.apache.activemq:destinationType=Topic,*"
  - "org.apache.activemq:type=Broker,brokerName=*"
  - "org.apache.activemq:type=Topic,brokerName=*"
```
### 3.activemq启动配置
activemq启动时，bin/env文件中， ACTIVEMQ_OPTS参数增加-javaagent:/data/public/jmx_prometheus_javaagent/jmx_prometheus_javaagent-0.20.0.jar=51616:/data/public/jmx_prometheus_javaagent/activemq.yml。
全值为ACTIVEMQ_OPTS="$ACTIVEMQ_OPTS_MEMORY -javaagent:/data/public/jmx_prometheus_javaagent/jmx_prometheus_javaagent-0.20.0.jar=51616:/data/public/jmx_prometheus_javaagent/activemq.yml -Djava.util.logging.config.file=logging.properties -Djava.security.auth.login.config=$ACTIVEMQ_CONF/login.config"。
### 4.验证采集
指标采集接口 curl http://127.0.0.1:51616/metrics ，检查是否可以返回metric数据。
### 5.接入采集配置
datasleuth配置文件conf.d/prom/prom-activemq.conf中，修改urls。

若接入集群，则每个节点启动均需配置jmx_prometheus_javaagent，注意端口冲突。采集配置urls配置多个地址
```
[[inputs.prom]]
  # 是否启用该input
  enable_input = true

  # Exporter URLs.
  urls = ["http://192.168.0.1:51616/metrics","http://192.168.0.2:51616/metrics"]

  # 自定义标签，修改cluster_name，将作为监控视图变量
  [inputs.prom.tags]
   cluster_name="default_cluster_name"
```

## 指标

| **名称** | **描述** | **指标类型** | **Availability** |
| --- | --- | --- | --- |
| **jvm_memory_pool_allocated_bytes_total** | 在给定JVM内存池中分配的总字节数。仅在GC之后更新，而不是连续更新。 | 度量 |  |
| **jvm_memory_objects_pending_finalization** | 在终结队列中等待的对象数量。 | 度量 |  |
| **jvm_memory_bytes_used** | 给定JVM内存区域的已使用字节数。 | 度量 |  |
| **jvm_memory_bytes_committed** | 给定JVM内存区域的已提交(字节)。 | 度量 |  |
| **jvm_memory_bytes_max** | 给定JVM内存区域的最大字节数。 | 度量 |  |
| **jvm_memory_bytes_init** | 给定JVM内存区域的初始字节。 | 度量 |  |
| **jvm_memory_pool_bytes_used** | 给定JVM内存池中已使用的字节数。 | 度量 |  |
| **jvm_memory_pool_bytes_committed** | 给定JVM内存池的已提交字节数。 | 度量 |  |
| **jvm_memory_pool_bytes_max** | 指定JVM内存池的最大字节数。 | 度量 |  |
| **jvm_memory_pool_bytes_init** | 给定JVM内存池的初始字节。 | 度量 |  |
| **jvm_memory_pool_collection_used_bytes** | 给定JVM内存池上次收集后使用的字节数。 | 度量 |  |
| **jvm_memory_pool_collection_committed_bytes** | 在给定JVM内存池的最后一个收集字节之后提交。 | 度量 |  |
| **jvm_memory_pool_collection_max_bytes** | 给定JVM内存池最后一次收集后的最大字节数。 | 度量 |  |
| **jvm_memory_pool_collection_init_bytes** | 给定JVM内存池的最后一次收集字节之后的初始值。 | 度量 |  |
| **jvm_threads_current** | 当前JVM的线程数 | 度量 |  |
| **jvm_threads_daemon** |  JVM的守护线程数 | 度量 |  |
| **jvm_threads_peak** |  JVM的峰值线程数 | 度量 |  |
| **jvm_threads_started_total** |  JVM的启动线程数 | 度量 |  |
| **jvm_threads_deadlocked** | 等待获取对象监视器或可拥有的同步器的死锁jvm线程的周期 | 度量 |  |
| **jvm_threads_deadlocked_monitor** | 处于死锁状态等待获取对象监视器的jvm线程周期 | 度量 |  |
| **jvm_threads_state** | 按状态计算的当前线程计数 | 度量 |  |
| **jvm_info** | 虚拟机版本信息 | 度量 |  |
| **org_apache_activemq_Broker_Paused** | 对消费者的分派被暂停 | 度量 |  |
| **org_apache_activemq_Broker_ExpiredCount** | 已过期的消息数。org.apache.activemq: name = null,类型=代理属性= ExpiredCount | 度量 |  |
| **org_apache_activemq_Broker_QueueSize** | 目的地上的消息数，包括已发送但未确认的消息。org.apache.activemq:name=null,type=Broker,attribute=QueueSize | 度量 |  |
| **org_apache_activemq_Broker_BlockedProducerWarningInterval** | org.apache.activemq:name=null,type=Broker,attribute=BlockedProducerWarningInterval | 度量 |  |
| **org_apache_activemq_Broker_MaxMessageSize** | 此目标上的最大消息大小org.apache.activemq:name=null,type=Broker,attribute=MaxMessageSize | 度量 |  |
| **org_apache_activemq_Broker_MemoryUsageByteCount** | org.apache.activemq:name=null,type=Broker,attribute=MemoryUsageByteCount | 度量 |  |
| **org_apache_activemq_Broker_CurrentConnectionsCount** | rg.apache.activemq:name=null,type=Broker, Attribute =CurrentConnectionsCount | 度量 |  |
| **org_apache_activemq_Broker_StatisticsEnabled** | 用了代理统计信息。org.apache.activemq: name = null,类型=代理属性= StatisticsEnabled | 度量 |  |
| **org_apache_activemq_Broker_ProducerCount** | 加到此目标的生产者数量org.apache.activemq:name=null,type=Broker,attribute=ProducerCount | 度量 |  |
| **org_apache_activemq_Broker_SendDuplicateFromStoreToDLQ** | 存储消息的副本发送到死信队列的配置设置。org.apache.activemq: name = null,类型=代理属性= SendDuplicateFromStoreToDLQ | 度量 |  |
| **org_apache_activemq_Broker_BlockedSends** | 流控制被阻塞的消息数org.apache.activemq:name=null,type=Broker,attribute= blockedsent | 度量 |  |
| **org_apache_activemq_Broker_MinMessageSize** | 此目标上的最小消息大小org.apache.activemq:name=null,type=Broker,attribute=MinMessageSize | 度量 |  |
| **org_apache_activemq_Broker_TempUsagePercentUsage** | 用的临时使用限制的百分比org.apache.activemq:name=null,type=Broker,attribute=TempUsagePercentUsage | 度量 |  |
| **org_apache_activemq_Broker_CursorFull** | 消息客户端已达到消息页面的内存限制org.apache.activemq:name=null,type=Broker,attribute=CursorFull | 度量 |  |
| **org_apache_activemq_Broker_MaxPageSize** | rg.apache.activemq中要分页的最大消息数:name=null,type=Broker,attribute=MaxPageSize | 度量 |  |
| **org_apache_activemq_Broker_StorePercentUsage** | 用存储限制的百分比。org.apache.activemq: name = null,类型=代理属性= StorePercentUsage | 度量 |  |
| **org_apache_activemq_Broker_MaxEnqueueTime** | 消息在此目的地保存的最长时间org.apache.activemq:name=null,type=Broker,attribute=MaxEnqueueTime | 度量 |  |
| **org_apache_activemq_Broker_TempUsageLimit** | 分配给此目标的临时使用限制(以字节为单位)。org.apache.activemq: name = null,类型=代理属性= TempUsageLimit | 度量 |  |
| **org_apache_activemq_Broker_StoreLimit** | 磁盘限制，以字节为单位，用于阻塞生产者之前的持久消息。org.apache.activemq: name = null,类型=代理属性= StoreLimit | 度量 |  |
| **org_apache_activemq_Broker_MemoryLimit** | 传递的消息在分页到临时存储器之前使用的内存限制(以字节为单位)。org.apache.activemq: name = null,类型=代理属性= MemoryLimit | 度量 |  |
| **org_apache_activemq_Broker_DLQ** | 信队列org.apache.activemq:name=null,type=Broker,attribute=DLQ | 度量 |  |
| **org_apache_activemq_Broker_TempPercentUsage** | 使用的温度限制的百分比。org.apache.activemq: name = null,类型=代理属性= TempPercentUsage | 度量 |  |
| **org_apache_activemq_Broker_MemoryPercentUsage** | 使用的内存限制的百分比org.apache.activemq:name=null,type=Broker,attribute=MemoryPercentUsage | 度量 |  |
| **org_apache_activemq_Broker_AverageBlockedTime** | 流量控制阻塞消息的平均时间(ms) org.apache.activemq:name=null,type=Broker,attribute=AverageBlockedTime | 度量 |  |
| **org_apache_activemq_Broker_TempLimit** | 盘限制，以字节为单位，在生产者被阻塞之前用于非持久消息和临时数据。org.apache.activemq: name = null,类型=代理属性= TempLimit | 度量 |  |
| **org_apache_activemq_Broker_TotalBlockedTime** | 流量控制阻塞消息的总时间(毫秒)org.apache.activemq:name=null,type=Broker,attribute=TotalBlockedTime | 度量 |  |
| **org_apache_activemq_Broker_AlwaysRetroactive** | rg.apache.activemq:name=null,type=Broker,attribute=AlwaysRetroactive | 度量 |  |
| **org_apache_activemq_Broker_TotalMessageCount** | 理上未确认的消息数。org.apache.activemq: name = null,类型=代理属性= TotalMessageCount | 度量 |  |
| **org_apache_activemq_Broker_ProducerFlowControl** | rg.apache.activemq:name=null,type=Broker,attribute=ProducerFlowControl | 度量 |  |
| **org_apache_activemq_Broker_DuplicateFromStoreCount** | 从存储区分页入的重复消息数。org.apache.activemq: name = null,类型=代理属性= DuplicateFromStoreCount | 度量 |  |
| **org_apache_activemq_Broker_DequeueCount** | 已从目的地确认(和删除)的消息数。org.apache.activemq: name = null,类型=代理属性= DequeueCount | 度量 |  |
| **org_apache_activemq_Broker_TotalConsumerCount** | 在代理上订阅目的地的消息使用者数量。org.apache.activemq: name = null,类型=代理属性= TotalConsumerCount | 度量 |  |
| **org_apache_activemq_Broker_MemoryUsagePortion** | 来自此目标的代理内存限制的内存部分org.apache.activemq:name=null,type= broker,attribute= memoryusagepart | 度量 |  |
| **org_apache_activemq_Broker_MinEnqueueTime** | 消息在此目的地上保存的最短时间org.apache.activemq:name=null,type=Broker,attribute=MinEnqueueTime | 度量 |  |
| **org_apache_activemq_Broker_JobSchedulerStorePercentUsage** | 用的作业存储限制的百分比。org.apache.activemq: name = null,类型=代理属性= JobSchedulerStorePercentUsage | 度量 |  |
| **org_apache_activemq_Broker_MaxProducersToAudit** | 计生产者的最大数量org.apache.activemq:name=null,type=Broker,attribute=MaxProducersToAudit | 度量 |  |
| **org_apache_activemq_Broker_UseCache** | org.apache.activemq:name=null,type=Broker,attribute=UseCache | 度量 |  |
| **org_apache_activemq_Broker_DispatchCount** | 传递给消费者的消息数，包括未确认的消息。org.apache.activemq:name=null,type=Broker,attribute=DispatchCount | 度量 |  |
| **org_apache_activemq_Broker_ConsumerCount** | 阅此目的地的消费者数量。org.apache.activemq: name = null,类型=代理属性= ConsumerCount | 度量 |  |
| **org_apache_activemq_Broker_AverageMessageSize** | 此目标上的平均消息大小org.apache.activemq:name=null,type=Broker,attribute=AverageMessageSize | 度量 |  |
| **org_apache_activemq_Broker_UptimeMillis** | 代理的正常运行时间，以毫秒为单位。org.apache.activemq: name = null,类型=代理属性= UptimeMillis | 度量 |  |
| **org_apache_activemq_Broker_PrioritizedMessages** | rg.apache.activemq:name=null,type=Broker,attribute= prefertizedmessages | 度量 |  |
| **org_apache_activemq_Broker_EnqueueCount** | 已发送到目的地的消息数。org.apache.activemq: name = null,类型=代理属性= EnqueueCount | 度量 |  |
| **org_apache_activemq_Broker_AverageEnqueueTime** | 消息在此目的地保存的平均时间。org.apache.activemq: name = null,类型=代理属性= AverageEnqueueTime | 度量 |  |
| **org_apache_activemq_Broker_Slave** | 隶代理。org.apache.activemq: name = null, type =代理属性=奴隶 | 度量 |  |
| **org_apache_activemq_Broker_TotalConnectionsCount** | 管理暴露的属性org.apache.activemq:name=null,type=Broker, Attribute =TotalConnectionsCount | 度量 |  |
| **org_apache_activemq_Broker_ForwardCount** | 已从目的地转发(到网络代理)的消息数。org.apache.activemq: name = null,类型=代理属性= ForwardCount | 度量 |  |
| **org_apache_activemq_Broker_TotalEnqueueCount** | 发送到代理的消息数。org.apache.activemq: name = null,类型=代理属性= TotalEnqueueCount | 度量 |  |
| **org_apache_activemq_Broker_TotalProducerCount** | 在代理上的目的地上活动的消息生成器的数量。org.apache.activemq: name = null,类型=代理属性= TotalProducerCount | 度量 |  |
| **org_apache_activemq_Broker_InFlightCount** | 分派给消费者但未被消费者确认的消息数。org.apache.activemq: name = null,类型=代理属性= InFlightCount | 度量 |  |
| **org_apache_activemq_Broker_CacheEnabled** | org.apache.activemq:name=null,type=Broker,attribute=CacheEnabled | 度量 |  |
| **org_apache_activemq_Broker_Persistent** | 消息被同步到磁盘。org.apache.activemq: name = null,类型=代理属性=持久 | 度量 |  |
| **org_apache_activemq_Broker_CursorMemoryUsage** | 息游标内存使用情况，以字节为单位。org.apache.activemq: name = null,类型=代理属性= CursorMemoryUsage | 度量 |  |
| **org_apache_activemq_Broker_CursorPercentUsage** | org.apache.activemq:name=null,type=Broker,attribute=CursorPercentUsage | 度量 |  |
| **org_apache_activemq_Broker_StoreMessageSize** | 此目标存储区中所有消息的内存大小。org.apache.activemq: name = null,类型=代理属性= StoreMessageSize | 度量 |  |
| **org_apache_activemq_Broker_TotalDequeueCount** | 代理上已确认或丢弃的消息数。org.apache.activemq: name = null,类型=代理属性= TotalDequeueCount | 度量 |  |
| **org_apache_activemq_Broker_MaxAuditDepth** | rg.apache.activemq:name=null,type=Broker,attribute=MaxAuditDepth | 度量 |  |
| **org_apache_activemq_Broker_JobSchedulerStoreLimit** | 磁盘限制，以字节为单位，在生产者被阻塞之前用于计划消息。org.apache.activemq: name = null,类型=代理属性= JobSchedulerStoreLimit | 度量 |  |
| **jmx_scrape_duration_seconds** |  JMX抓取所花费的时间，以秒为单位。 | 度量 |  |
| **jmx_scrape_error** | 如果刮擦失败，则非零。 | 度量 |  |
| **jmx_scrape_cached_beans** | 缓存了与其匹配规则的bean的数量 | 度量 |  |
| **jmx_exporter_build_info** | 一个带有常量“1”值的度量，标记为JMX导出器的版本。 | 度量 |  |
| **jvm_buffer_pool_used_bytes** | 给定JVM缓冲池的已使用字节数。 | 度量 |  |
| **jvm_buffer_pool_capacity_bytes** | 指定JVM缓冲池的字节容量。 | 度量 |  |
| **jvm_buffer_pool_used_buffers** | 给定JVM缓冲池的已用缓冲区。 | 度量 |  |
| **jvm_gc_collection_seconds** | 在给定JVM垃圾收集器中花费的时间(以秒为单位)。 | 度量 |  |
| **jmx_config_reload_success_total** | 重新加载配置成功的次数。 | 度量 |  |
| **process_cpu_seconds_total** | 以秒为单位的用户和系统CPU总花费时间。 | 度量 |  |
| **process_start_time_seconds** | 进程从unix纪元开始的时间，以秒为单位。 | 度量 |  |
| **process_open_fds** | 打开的文件描述符的数量。 | 度量 |  |
| **process_max_fds** | 打开的文件描述符的最大数目。 | 度量 |  |
| **process_virtual_memory_bytes** | 虚拟内存大小，单位为字节。 | 度量 |  |
| **process_resident_memory_bytes** | 常驻内存大小，单位为字节。 | 度量 |  |
| **jmx_config_reload_failure_total** | 重新加载配置失败的次数。 | 度量 |  |
| **jvm_classes_currently_loaded** | 当前在JVM中加载的类的数量 | 度量 |  |
| **jvm_classes_loadd_total** | 自JVM开始执行以来已加载的类的总数 | 度量 |  |
| **jvm_classes_unloadd_total** | 自JVM开始执行以来已卸载的类的总数 | 度量 |  |
| **jmx_config_reload_failure_created** | 重新加载配置失败的次数。 | 度量 |  |
| **jmx_config_reload_success_created** | 重新加载配置成功的次数。 | 度量 |  |
| **jvm_memory_pool_allocated_bytes_created** | 在给定JVM内存池中分配的总字节数。仅在GC之后更新，而不是连续更新。 | 度量 |  |