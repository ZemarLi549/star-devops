# Tomcat
可以使用 DDTrace可以采集 Tomcat 指标。采集数据流向如下：Tomcat -> DDTrace -> datasleuth(StatsD)。

可以看到 datasleuth 已经集成了 [StatsD](https://github.com/statsd/statsd) 的服务端，DDTrace 采集 Tomcat 的数据后使用 StatsD 的协议报告给了 datasleuth。

## 配置

### 前置条件

- 已测试的 Tomcat 版本：
  - 11.0.0
  - 10.1.10
  - 9.0.76
  - 8.5.90

### 配置

- 下载 `dd-java-agent.jar` 包，参见提供的发布包，也可以从网上Maven仓库下载。
- datasleuth 侧：参见 StatsD的配置。
- Tomcat 侧：

在 */usr/local/tomcat/bin* 下创建文件 *setenv.sh* 并赋予执行权限，再写入以下内容：

```
export CATALINA_OPTS="-javaagent:dd-java-agent.jar \
                      -Ddd.tags=tomcat_name:default_tomcat_name \
                      -Ddd.jmxfetch.enabled=true \
                      -Ddd.jmxfetch.statsd.host=${datasleuth_HOST} \
                      -Ddd.jmxfetch.statsd.port=${datasleuth_STATSD_HOST} \
                      -Ddd.jmxfetch.tomcat.enabled=true"
```

参数说明如下：

- `javaagent`: 这个填写 `dd-java-agent.jar` 的完整路径；
- `Ddd.tags`: 自定义标签，需填写tomcat_name的值，作为监控视图变量；
- `Ddd.jmxfetch.enabled`: 填 `true`, 表示开启 DDTrace 的采集功能；
- `Ddd.jmxfetch.statsd.host`: 填写 datasleuth 监听的网络地址。不含端口号；
- `Ddd.jmxfetch.statsd.port`: 填写 datasleuth 监听的端口号。一般为 `8125`，由 datasleuth 侧的配置决定；
- `Ddd.jmxfetch.tomcat.enabled`: 填 `true`, 表示开启 DDTrace 的 Tomcat 采集功能。开启后会多出名为 `tomcat` 的指标集；

重启 Tomcat 使配置生效。

## 指标

会产生两个指标集，一个是 JVM 的指标集，另一个是 `tomcat` 的指标集，以下只展示 `tomcat` 的指标集。

以下所有数据采集，默认会追加名为 `host` 的全局 tag（tag 值为 datasleuth 所在主机名），也可以在配置中通过 `[inputs.statsd.tags]` 指定其它标签：

```
 [inputs.statsd.tags]
  # some_tag = "some_value"
  # more_tag = "some_other_value"
  # ...
```

### `tomcat`

- 标签

| Tag           | Description   |
| :------------ | :------------ |
| `host`        | Hostname.     |
| `instance`    | Instance.     |
| `jmx_domain`  | JMX domain.   |
| `metric_type` | Metric type.  |
| `name`        | Name.         |
| `runtime-id`  | Runtime ID.   |
| `service`     | Service name. |
| `type`        | Type.         |

- 指标列表

| Metric                      | Description                                                  | Type  | Unit  |
| :-------------------------- | :----------------------------------------------------------- | :---: | :---: |
| `bytes_rcvd`                | Bytes per second received by all request processors.         | float | count |
| `bytes_sent`                | Bytes per second sent by all the request processors.         | float | count |
| `cache_access_count`        | The number of accesses to the cache per second.              | float | count |
| `cache_hits_count`          | The number of cache hits per second.                         | float | count |
| `error_count`               | The number of errors per second on all request processors.   | float | count |
| `jsp_count`                 | The number of JSPs per second that have been loaded in the web module. | float | count |
| `jsp_reload_count`          | The number of JSPs per second that have been reloaded in the web module. | float | count |
| `max_time`                  | The longest request processing time (in milliseconds).       | float | count |
| `processing_time`           | The sum of request processing times across all requests handled by the request processors (in milliseconds) per second. | float | count |
| `request_count`             | The number of requests per second across all request processors. | float | count |
| `servlet_error_count`       | The number of erroneous requests received by the Servlet per second. | float | count |
| `servlet_processing_time`   | The sum of request processing times across all requests to the Servlet (in milliseconds) per second. | float | count |
| `servlet_request_count`     | The number of requests received by the Servlet per second.   | float | count |
| `string_cache_access_count` | The number of accesses to the string cache per second.       | float | count |
| `string_cache_hit_count`    | The number of string cache hits per second.                  | float | count |
| `threads_busy`              | The number of threads that are in use.                       | float | count |
| `threads_count`             | The number of threads managed by the thread pool.            | float | count |
| `threads_max`               | The maximum number of allowed worker threads.                | float | count |
| `web_cache_hit_count`       | The number of web resource cache hits per second.            | float | count |
| `web_cache_lookup_count`    | The number of lookups to the web resource cache per second.  | float | count |
