# Container
采集 Container 和 Kubernetes 的指标、对象和日志数据。

## 采集器配置

### 前置条件

- 目前 container 支持 Docker、Containerd、CRI-O 容器运行时
  - 版本要求：Docker v17.04 及以上版本，Containerd v1.5.1 及以上，CRI-O 1.20.1 及以上
- 采集 Kubernetes 数据需要 datasleuth 以 DaemonSet 方式部署。

如果是纯 Docker 或 Containerd 环境，那么 datasleuth 只能安装在宿主机上。

在指标接入页面修改配置：

```
[inputs.container]
  endpoints = [
    "unix:///var/run/docker.sock",
    "unix:///var/run/containerd/containerd.sock",
    "unix:///var/run/crio/crio.sock",
  ]

  enable_container_metric = true
  enable_k8s_metric = true
  enable_pod_metric = false
  enable_k8s_event = true
  enable_k8s_node_local = true

  ## Add resource Label as Tags (container use Pod Label), need to specify Label keys.
  ## e.g. ["app", "name"]
  # extract_k8s_label_as_tags_v2 = []
  # extract_k8s_label_as_tags_v2_for_metric = []

  ## Auto-Discovery of PrometheusMonitoring Annotations/CRDs
  enable_auto_discovery_of_prometheus_pod_annotations = false
  enable_auto_discovery_of_prometheus_service_annotations = false
  enable_auto_discovery_of_prometheus_pod_monitors = false
  enable_auto_discovery_of_prometheus_service_monitors = false

  ## Containers logs to include and exclude, default collect all containers. Globs accepted.
  container_include_log = []
  container_exclude_log = ["image:*logfwd*", "image:*datasleuth*"]

  kubernetes_url = "https://kubernetes.default:443"

  ## Authorization level:
  ##   bearer_token -> bearer_token_string -> TLS
  ## Use bearer token for authorization. ('bearer_token' takes priority)
  ## linux at:   /run/secrets/kubernetes.io/serviceaccount/token
  bearer_token = "/run/secrets/kubernetes.io/serviceaccount/token"
  # bearer_token_string = "<your-token-string>"

  ## Set true to enable election for k8s metric collection
  election = true

  logging_auto_multiline_detection = true
  logging_auto_multiline_extra_patterns = []

  ## Removes ANSI escape codes from text strings.
  logging_remove_ansi_escape_codes = false

  ## Search logging interval, default "60s"
  #logging_search_interval = ""

  [inputs.container.logging_extra_source_map]
    # source_regexp = "new_source"

  [inputs.container.logging_source_multiline_map]
    # source = '''^\d{4}'''

  [inputs.container.tags]
    # some_tag = "some_value"
    # more_tag = "some_other_value"
```

> **Attention**
>
> - 对象数据采集间隔是 5 分钟，指标数据采集间隔是 60 秒，不支持配置
> - 采集到的日志，单行（包括经过 `multiline_match` 处理后）最大长度为 32MB，超出部分会被截断且丢弃

### Docker 和 Containerd sock 文件配置

如果 Docker 或 Containerd 的 sock 路径不是默认的，则需要指定一下 sock 文件路径，根据 datasleuth 不同部署方式，其方式有所差别，以 Containerd 为例：

修改 container.conf 的 `endpoints` 配置项，将其设置为对应的 sock 路径。

环境变量 `ENV_INPUT_CONTAINER_ENDPOINTS` 是追加到现有的 endpoints 配置，最终实际 endpoints 配置可能有很多项，采集器会去重然后逐一连接、采集。

默认的 endpoints 配置是：

```
  endpoints = [
    "unix:///var/run/docker.sock",
    "unix:///var/run/containerd/containerd.sock",
    "unix:///var/run/crio/crio.sock",
  ] 
```

使用环境变量 `ENV_INPUT_CONTAINER_ENDPOINTS` 为 `["unix:///path/to/new//run/containerd.sock"]`，最终 endpoints 配置如下：

```
  endpoints = [
    "unix:///var/run/docker.sock",
    "unix:///var/run/containerd/containerd.sock",
    "unix:///var/run/crio/crio.sock",
    "unix:///path/to/new//run/containerd.sock",
  ] 
```

采集器会连接和采集这些容器运行时，如果 sock 文件不存在，会在第一次连接失败时输出报错日志，不影响后续采集。

## 指标

以下所有数据采集，默认会追加名为 `host` 的全局 tag（tag 值为 datasleuth 所在主机名），也可以在配置中通过 `[inputs.container.tags]` 指定其它标签：

```
 [inputs.container.tags]
  # some_tag = "some_value"
  # more_tag = "some_other_value"
  # ...
```

### `docker_containers`

The metric of containers, only supported Running status.

- 标签

| Tag                         | Description                                                  |
| :-------------------------- | :----------------------------------------------------------- |
| `aws_ecs_cluster_name`      | Cluster name of the AWS ECS.                                 |
| `cluster_name_k8s`          | K8s cluster name(default is `default`). We can rename it in datasleuth.yaml on ENV_CLUSTER_NAME_K8S. |
| `container_id`              | Container ID                                                 |
| `container_name`            | Container name from k8s (label `io.kubernetes.container.name`). If empty then use $container_runtime_name. |
| `container_runtime`         | Container runtime (this container from Docker/Containerd/cri-o). |
| `container_runtime_name`    | Container name from runtime (like 'docker ps'). If empty then use 'unknown'. |
| `container_runtime_version` | Container runtime version.                                   |
| `container_type`            | The type of the container (this container is created by Kubernetes/Docker/Containerd/cri-o). |
| `daemonset`                 | The name of the DaemonSet which the object belongs to.       |
| `deployment`                | The name of the Deployment which the object belongs to.      |
| `image`                     | The full name of the container image, example `nginx.org/nginx:1.21.0`. |
| `image_name`                | The name of the container image, example `nginx.org/nginx`.  |
| `image_short_name`          | The short name of the container image, example `nginx`.      |
| `image_tag`                 | The tag of the container image, example `1.21.0`.            |
| `namespace`                 | The namespace of the container (label `io.kubernetes.pod.namespace`). |
| `pod_name`                  | The pod name of the container (label `io.kubernetes.pod.name`). |
| `pod_uid`                   | The pod uid of the container (label `io.kubernetes.pod.uid`). |
| `state`                     | Container status (only Running).                             |
| `statefulset`               | The name of the StatefulSet which the object belongs to.     |
| `task_arn`                  | The task arn of the AWS Fargate.                             |
| `task_family`               | The task family of the AWS fargate.                          |
| `task_version`              | The task version of the AWS fargate.                         |

- 指标列表

| Metric                        | Description                                                  | Type  |  Unit   |
| :---------------------------- | :----------------------------------------------------------- | :---: | :-----: |
| `block_read_byte`             | Total number of bytes read from the container file system (only supported docker). |  int  |    B    |
| `block_write_byte`            | Total number of bytes wrote to the container file system (only supported docker). |  int  |    B    |
| `cpu_numbers`                 | The number of the CPU core.                                  |  int  |  count  |
| `cpu_usage`                   | The percentage usage of CPU on system host.                  | float | percent |
| `cpu_usage_base100`           | The normalized cpu usage, with a maximum of 100%.            | float | percent |
| `mem_capacity`                | The total memory in the host machine.                        |  int  |    B    |
| `mem_limit`                   | The limit memory in the container.                           |  int  |    B    |
| `mem_usage`                   | The usage of the memory.                                     |  int  |    B    |
| `mem_used_percent`            | The percentage usage of the memory is calculated based on the capacity of host machine. | float | percent |
| `mem_used_percent_base_limit` | The percentage usage of the memory is calculated based on the limit. | float | percent |
| `network_bytes_rcvd`          | Total number of bytes received from the network (only count the usage of the main process in the container, excluding loopback). |  int  |    B    |
| `network_bytes_sent`          | Total number of bytes send to the network (only count the usage of the main process in the container, excluding loopback). |  int  |    B    |

