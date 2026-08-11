# NPU

## 安装配置

#### 1、驱动安装

参考华为文档

https://support.huawei.com/enterprise/zh/doc/EDOC1100318080/c1fd8818

安装完成后执行命令

```shell
npu-smi info
```

显示如下内容，则说明驱动安装成功

```
[root@master-192.168.1.1 ~]# npu-smi info
+------------------------------------------------------------------------------------------------+
| npu-smi 24.1.rc1                 Version: 24.1.rc1                                             |
+---------------------------+---------------+----------------------------------------------------+
| NPU   Name                | Health        | Power(W)    Temp(C)           Hugepages-Usage(page)|
| Chip                      | Bus-Id        | AICore(%)   Memory-Usage(MB)  HBM-Usage(MB)        |
+===========================+===============+====================================================+
| 0     910B3               | OK            | 99.5        37                0    / 0             |
| 0                         | 0000:C1:00.0  | 0           0    / 0          33724/ 65536         |
+===========================+===============+====================================================+
| 1     910B3               | OK            | 92.9        35                0    / 0             |
| 0                         | 0000:C2:00.0  | 0           0    / 0          33723/ 65536         |
+===========================+===============+====================================================+
| 2     910B3               | OK            | 89.4        37                0    / 0             |
| 0                         | 0000:81:00.0  | 0           0    / 0          33723/ 65536         |
+===========================+===============+====================================================+
| 3     910B3               | OK            | 94.0        39                0    / 0             |
| 0                         | 0000:82:00.0  | 0           0    / 0          33723/ 65536         |
+===========================+===============+====================================================+
| 4     910B3               | OK            | 100.2       44                0    / 0             |
| 0                         | 0000:01:00.0  | 0           0    / 0          33723/ 65536         |
+===========================+===============+====================================================+
| 5     910B3               | OK            | 92.6        41                0    / 0             |
| 0                         | 0000:02:00.0  | 0           0    / 0          33723/ 65536         |
+===========================+===============+====================================================+
| 6     910B3               | OK            | 95.2        43                0    / 0             |
| 0                         | 0000:41:00.0  | 0           0    / 0          33723/ 65536         |
+===========================+===============+====================================================+
| 7     910B3               | OK            | 103.7       45                0    / 0             |
| 0                         | 0000:42:00.0  | 0           0    / 0          33723/ 65536         |
+===========================+===============+====================================================+
+---------------------------+---------------+----------------------------------------------------+
| NPU     Chip              | Process id    | Process name             | Process memory(MB)      |
+===========================+===============+====================================================+
| 0       0                 | 1067991       | java                     | 30454                   |
+===========================+===============+====================================================+
| 1       0                 | 1067991       | java                     | 30454                   |
+===========================+===============+====================================================+
| 2       0                 | 1067991       | java                     | 30454                   |
+===========================+===============+====================================================+
| 3       0                 | 1067991       | java                     | 30454                   |
+===========================+===============+====================================================+
| 4       0                 | 1067991       | java                     | 30453                   |
+===========================+===============+====================================================+
| 5       0                 | 1067991       | java                     | 30454                   |
+===========================+===============+====================================================+
| 6       0                 | 1067991       | java                     | 30454                   |
+===========================+===============+====================================================+
| 7       0                 | 1067991       | java                     | 30454                   |
+===========================+===============+====================================================+
```



#### 2、部署arm64架构的npu-exporter

（1）发布包中提供了Ascend-mindxdl-npu-exporter_5.0.0.5_linux-aarch64.zip，可直接使用。

如果需要别的版本的部署包，可以到网站 https://www.hiascend.com/zh/developer/download/community/result?module=dl+cann 中选择相应的版本下载npu-exporter部署包。

（2）解压部署包Ascend-mindxdl-npu-exporter_5.0.0.5_linux-aarch64.zip，解压后的文件夹如下所示

```
[root@master-172-20-18-12 data]# cd Ascend-mindxdl-npu-exporter_5.0.0.5_linux-aarch64/
[root@master-172-20-18-12 Ascend-mindxdl-npu-exporter_5.0.0.5_linux-aarch64]# ll
total 26692
-r-------- 1 root root      740 Feb 29 17:06 Dockerfile
-r-------- 1 root root      964 Feb 29 17:06 Dockerfile-310P-1usoc
-rw-r--r-- 1 root root  6451904 May 30 15:06 exporter.log
-r-x------ 1 root root 16750720 Feb 29 17:06 npu-exporter
-r-------- 1 root root     4609 Feb 29 17:06 npu-exporter-310P-1usoc-v5.0.0.5.yaml
-r-------- 1 root root     4127 Feb 29 17:06 npu-exporter-v5.0.0.5.yaml
-r-x------ 1 root root     2579 Feb 29 17:06 run_for_310P_1usoc.sh
[root@master-172-20-18-12 Ascend-mindxdl-npu-exporter_5.0.0.5_linux-aarch64]#
```

（3）启动npu-exporter

使用二进制文件npu-exporter启动，需要指定ip，ip为部署npu-exporter的服务器对外ip地址，默认启动端口为8082

```
cd Ascend-mindxdl-npu-exporter_5.0.0.5_linux-aarch64
nohup ./npu-exporter -ip=127.0.0.1 > exporter.log 2>&1 &
```

启动后出现下图日志，则说明npu-exporter正常运行

```
[INFO]     2024/05/29 08:44:37.646867 1       hwlog/api.go:108    npu-exporter.log's logger init success
[INFO]     2024/05/29 08:44:37.647168 1       npu-exporter/main.go:205    listen on: 172.20.18.12
[INFO]     2024/05/29 08:44:37.647220 1       npu-exporter/main.go:325    npu exporter starting and the version is v5.0.0.5_linux-aarch64
[WARN]     2024/05/29 08:44:37.912953 1       npu-exporter/main.go:339    enable unsafe http server
[WARN]     2024/05/29 08:44:42.912845 295     container/runtime_ops.go:127    connecting to CRI server failed: context deadline exceeded
[WARN]     2024/05/29 08:44:42.912905 295     container/runtime_ops.go:129    use cri-dockerd address to try again
[ERROR]    2024/05/29 08:44:42.912940 295     collector/npu_collector.go:414    failed to init devices parser: init CRI client failed, connecting to CRI server failed: context deadline exceeded->co
nnecting to container runtime failed
[INFO]     2024/05/29 08:44:42.912963 295     collector/npu_collector.go:418    Starting update cache every 5 seconds
[WARN]     2024/05/29 08:44:42.914220 315     collector/npu_collector.go:463    get info of npu-exporter-network-info failed: no value found, so use initial net info
[INFO]     2024/05/29 08:44:42.914268 315     collector/npu_collector.go:476    update cache,key is npu-exporter-network-info
[INFO]     2024/05/29 08:44:47.101473 306     collector/npu_collector.go:442    update cache,key is npu-exporter-npu-list
[INFO]     2024/05/29 08:44:47.913407 315     collector/npu_collector.go:476    update cache,key is npu-exporter-network-info
[INFO]     2024/05/29 08:44:51.883723 306     collector/npu_collector.go:442    update cache,key is npu-exporter-npu-list
[INFO]     2024/05/29 08:44:52.913813 315     collector/npu_collector.go:476    update cache,key is npu-exporter-network-info
[INFO]     2024/05/29 08:44:56.269588 306     collector/npu_collector.go:442    update cache,key is npu-exporter-npu-list
[INFO]     2024/05/29 08:44:57.913505 315     collector/npu_collector.go:476    update cache,key is npu-exporter-network-info
[INFO]     2024/05/29 08:45:02.157426 306     collector/npu_collector.go:442    update cache,key is npu-exporter-npu-list
[INFO]     2024/05/29 08:45:02.914260 315     collector/npu_collector.go:476    update cache,key is npu-exporter-network-info
[WARN]     2024/05/29 08:45:05.701912 678     collector/npu_collector.go:697    containers' devices info not found in cache, rebuilding
[INFO]     2024/05/29 08:45:06.181746 306     collector/npu_collector.go:442    update cache,key is npu-exporter-npu-list
[WARN]     2024/05/29 08:45:06.702264 678     collector/npu_collector.go:703    rebuild container info cache timeout
[ERROR]    2024/05/29 08:45:06.702297 678     collector/npu_collector.go:711    Error container npu info cache and convert failed
[INFO]     2024/05/29 08:45:06.704894 675     limiter/limit_handler.go:128    HTTP/1.1 GET: /metrics <200> (1003ms) |   172.20.18.12 |Go-http-client/1.1 |0
[INFO]     2024/05/29 08:45:07.913397 315     collector/npu_collector.go:476    update cache,key is npu-exporter-network-info
[INFO]     2024/05/29 08:45:11.281388 306     collector/npu_collector.go:442    update cache,key is npu-exporter-npu-list
```

（4）验证

访问npu-exporter指标接口 http://127.0.0.1:8082/metrics

```
[root@master-172-20-18-12 Ascend-mindxdl-npu-exporter_5.0.0.5_linux-aarch64]# curl http://127.0.0.1:8082/metrics
# HELP machine_npu_nums Amount of npu installed on the machine.
# TYPE machine_npu_nums gauge
machine_npu_nums 8
# HELP npu_chip_info_aicore_current_freq the npu ai core current frequency, unit is 'MHz'
# TYPE npu_chip_info_aicore_current_freq gauge
npu_chip_info_aicore_current_freq{id="0",model_name="910B3-Ascend-V1",pcie_bus_info="0000:C1:00.0",vdie_id="64036E64-00A07559-5C350CF2-B9D00485-104301E2"} 1800 1717053078245
npu_chip_info_aicore_current_freq{id="1",model_name="910B3-Ascend-V1",pcie_bus_info="0000:C2:00.0",vdie_id="7EF86E64-0060D3F3-0A7A88F2-B9D00485-104301E2"} 1800 1717053078678
npu_chip_info_aicore_current_freq{id="2",model_name="910B3-Ascend-V1",pcie_bus_info="0000:81:00.0",vdie_id="C4036E64-00A0EA1E-398A4CF2-B9D00485-104301E2"} 1800 1717053079426
npu_chip_info_aicore_current_freq{id="3",model_name="910B3-Ascend-V1",pcie_bus_info="0000:82:00.0",vdie_id="7EF86E64-00C00C5B-0F155CF2-B9D00485-104301E2"} 1800 1717053079859
npu_chip_info_aicore_current_freq{id="4",model_name="910B3-Ascend-V1",pcie_bus_info="0000:01:00.0",vdie_id="6A87A664-0080561D-1090DEF2-B9D00485-104301E2"} 800 1717053080292
npu_chip_info_aicore_current_freq{id="5",model_name="910B3-Ascend-V1",pcie_bus_info="0000:02:00.0",vdie_id="C4036E64-0140691E-76E00EF2-B9D00485-104301E2"} 800 1717053080736
npu_chip_info_aicore_current_freq{id="6",model_name="910B3-Ascend-V1",pcie_bus_info="0000:41:00.0",vdie_id="7EC1A664-0020AFF3-0F28E4F2-98500485-104301E2"} 800 1717053081170
npu_chip_info_aicore_current_freq{id="7",model_name="910B3-Ascend-V1",pcie_bus_info="0000:42:00.0",vdie_id="7EC1A664-00604AF3-21AA4CF2-B9D00485-104301E2"} 800 1717053081601
```



#### 3、datasleuth配置npu-exporter接口，采集数据

（1）创建配置文件 conf.d/prom/prom-npu.conf，内容如下，需修改urls地址为npu-exporter接口地址

```
[[inputs.prom]]
  # 是否启用该input
  enable_input = true

  # Exporter URLs.
  urls = ["http://127.0.0.1:8082/metrics"]

  # Unix Domain Socket URL. Using socket to request data when not empty.
  uds_path = ""

  # 采集间隔 "ns", "us" (or "µs"), "ms", "s", "m", "h"
  interval = "1m"
  ignore_req_err = false

  source = "npu"
  metric_types = ["counter", "gauge"]
  measurement_prefix = ""
  tls_open = false
  election = true
  disable_host_tag = false
  disable_instance_tag = false

  [inputs.prom.ignore_tag_kv_match]

  [inputs.prom.http_headers]

  [inputs.prom.tags_rename]
    overwrite_exist_tags = false
    [inputs.prom.tags_rename.mapping]

  [inputs.prom.as_logging]
    enable = false
    service = "service_name"

[inputs.prom.tags]
```



## 指标

| Metric                            | 说明                                                       |                                         |
| --------------------------------- | ---------------------------------------------------------- | --------------------------------------- |
| machine_npu_nums                  | 昇腾AI处理器数目。                                         | 个                                      |
| npu_chip_info_bandwidth_rx        | 昇腾AI处理器网口实时接收速率（仅支持Atlas 训练系列产品）。 | MB/s                                    |
| npu_chip_info_bandwidth_tx        | 昇腾AI处理器网口实时发送速率（仅支持Atlas 训练系列产品）。 | MB/s                                    |
| npu_chip_info_link_status         | 昇腾AI处理器网口Link状态（仅支持Atlas 训练系列产品）。     | 1:UP   0:DOWN                           |
| npu_chip_info_network_status      | 昇腾AI处理器网络健康状态（仅支持Atlas 训练系列产品）。     | 1：健康，可以连通   0：不健康，无法连通 |
| npu_chip_info_error_code          | 昇腾AI处理器错误码。标签包含以下字段：                     |                                         |
| npu_chip_info_name                | 昇腾AI处理器名称和ID。标签包含以下字段：                   | -                                       |
| npu_chip_info_health_status       | 昇腾AI处理器健康状态。标签包含以下字段：                   | 1：健康   0：不健康                     |
| npu_chip_info_power               | 昇腾AI处理器功耗（910和310为处理器功耗，310P为板卡功耗）。 | 瓦特（W）                               |
| npu_chip_info_temperature         | 昇腾AI处理器温度。                                         | 摄氏度（℃）                             |
| npu_chip_info_used_memory         | 昇腾AI处理器DDR内存已使用量。                              | MB                                      |
| npu_chip_info_total_memory        | 昇腾AI处理器DDR内存总量。                                  | MB                                      |
| npu_chip_info_hbm_used_memory     | 昇腾AI处理器HBM内存已使用量（Atlas 训练系列产品专属）。    | MB                                      |
| npu_chip_info_hbm_total_memory    | 昇腾AI处理器HBM总内存（Atlas 训练系列产品专属）。          | MB                                      |
| npu_chip_info_utilization         | 昇腾AI处理器AI Core利用率。                                | %                                       |
| npu_chip_info_aicore_current_freq | 昇腾AI处理器的AI Core当前频率。                            | MHz                                     |
| npu_chip_info_process_info        | 占用昇腾AI处理器的进程的信息，取值为进程使用的内存。       | MB                                      |
| npu_chip_info_voltage             | 昇腾AI处理器电压。                                         | 伏特（V）                               |
| npu_exporter_version_info         | NPU-Exporter版本信息。                                     | -                                       |



## 附录

1.npu-exporter源码
https://github.com/Ascend/ascend-npu-exporter

2.NPU-Exporter Prometheus Metrics接口

https://www.hiascend.com/document/detail/zh/mindx-dl/50rc2/clusterscheduling/clusterscheduling/dlug_guide_03_000138.html

3.Ascend主页

https://www.hiascend.com/zh/software/mindx-dl