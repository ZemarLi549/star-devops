# Snmp
## 术语

- `SNMP` (Simple network management protocol): 用于收集有关裸机网络设备信息的网络协议。
- `OID` (Object identifier): 设备上的唯一 ID 或地址，轮询时返回该值的响应代码。例如，OID 是 CPU 或设备风扇速度。
- `sysOID` (System object identifier): 定义设备类型的特定地址。所有设备都有一个定义它的唯一 ID。例如，`Meraki` 基础 sysOID 是“1.3.6.1.4.1.29671”。
- `MIB` (Managed information base): 与 MIB 相关的所有可能的 OID 及其定义的数据库或列表。例如，“IF-MIB”（接口 MIB）包含有关设备接口的描述性信息的所有 OID。

## 关于 SNMP 协议

SNMP 协议分为 3 个版本：v1/v2c/v3，其中：

- **v1 和 v2c 是兼容的**。很多 SNMP 设备只提供 v2c 和 v3 两种版本的选择。v2c 版本，兼容性最好，很多旧设备只支持这个版本；
- 如果对安全性要求高，选用 v3。安全性也是 v3 版本与之前版本的主要区别；

Datasleuth 支持以上所有版本。

### 选择 v1/v2c 版本

如果选择 v1/v2c 版本，需要提供 `community string`，中文翻译为「团体名/团体字符串/未加密的口令」，即密码，与 SNMP 设备进行交互需要提供这个进行鉴权。另外，有的设备会进一步进行细分，分为「只读团体名」和「读写团体名」。顾名思义：

- 只读团体名：设备只会向该方提供内部指标数据，不能修改内部的一些配置（Datasleuth 用这个就够了）
- 读写团体名：提供方拥有设备内部指标数据查询与部分配置修改权限

### 选择 v3 版本

如果选择 v3 版本，需要提供 「用户名」、「认证算法/密码」、「加密算法/密码」、「上下文」 等，各个设备要求不同，根据设备侧的配置进行填写。

## 配置

### exporter配置

采集Snmp数据需要用到Prometheus的snmp-exporter, 安装包在交付包中，也可以去github下载。使用文档：https://github.com/prometheus/snmp_exporter 

snmp-exporter的使用主要在于snmp.yml文件的配置，写好配置文件之后使用以下命令启动exporter

```
nohup ./snmp_exporter --config.file=snmp.yml  &
```

snmp-exporter 官方建议使用generator工具利用mib文件生成snmp.yml配置，本文档文末提供一份基础指标的配置，如果用户需要更多指标，请学习snmp-exporter 文档。

不同网络设备的mib文件不一样，其中的指标名称也不一样，可观测的设备详情页中需要一些统一名称的指标，不同接入设备在配置snmp.yml文件的时候要将指标名称改成可观测需要的，一共4个字段需要改实例如下：

| 设备               | 原始字段         | 映射字段       | 含义                                             |
| ------------------ | ---------------- | -------------- | ------------------------------------------------ |
| 华为CE6857F-48S6CQ | hwEntityMemUsage | entityMemUsage | 实体内存使用率                                   |
|                    | hwEntityCpuUsage | entityCpuUsage | 实体cpu使用率                                    |
|                    | hwEntityPwrState | entityPwrState | 电源状态（枚举值要改为supply在线，其他状态随意） |
|                    | hwEntityFanState | entityFanState | 风扇状态（枚举值要改为normal在线，其他状态随意） |

### datasleuth在线配置

在数据接入--指标接入页面，选择snmp组件，将其中的urls改为exporter的地址，device_ip地址改为网络设备的IP，如果要在同一台服务器上采集多个网络设备，可以将下面的配置多拷贝一份，分别配置不同的urls和device_ip即可。

```
[[inputs.prom]]
  # 是否启用该input
  enable_input =false

  # Exporter URLs.
  urls = ["http://127.0.0.1:9116/snmp?target=10.254.17.13&auth=public_v2&module=if_mib%2Chuawei-entity-extent-mib"]
  timeout="30s"
  interval="1m"
  # Unix Domain Socket URL. Using socket to request data when not empty.
  uds_path = ""

  # Ignore URL request errors.
  ignore_req_err = false

  ## Collector alias.
  source = "snmp"

  ## Collect data output.
  # Fill this when want to collect the data to local file nor center.
  # After filling, could use 'datasleuth --prom-conf /path/to/this/conf' to debug local storage measurement set.
  # Using '--prom-conf' when priority debugging data in 'output' path.
  # output = "/abs/path/to/file"

  ## Collect data upper limit as bytes.
  # Only available when set output to local file.
  # If collect data exceeded the limit, the data would be dropped.
  # Default is 32MB.
  # max_file_size = 0

  ## Metrics type whitelist. Optional: counter, gauge, histogram, summary
  # Default only collect 'counter' and 'gauge'.
  # Collect all if empty.
  metric_types = ["counter", "gauge"]

  ## Metrics name whitelist.
  # Regex supported. Multi supported, conditions met when one matched.
  # Collect all if empty.
  # metric_name_filter = ["cpu"]

  ## Measurement prefix.
  # Add prefix to measurement set name.
  measurement_prefix = ""

  ## Measurement name.
  # If measurement_name is empty, split metric name by '_', the first field after split as measurement set name, the rest as current metric name.
  # If measurement_name is not empty, using this as measurement set name.
  # Always add 'measurement_prefix' prefix at last.
   measurement_name = "snmp"

  ## TLS configuration.
  tls_open = false
  # tls_ca = "/tmp/ca.crt"
  # tls_cert = "/tmp/peer.crt"
  # tls_key = "/tmp/peer.key"

  ## Set to 'true' to enable election.
  election = true

  # disable setting host tag for this input
  disable_host_tag = false

  # disable setting instance tag for this input
  disable_instance_tag = false

  # Ignore tags. Multi supported.
  # The matched tags would be dropped, but the item would still be sent.
  # tags_ignore = ["xxxx"]

  ## Customize authentification. For now support Bearer Token only.
  # Filling in 'token' or 'token_file' is acceptable.
  # [inputs.prom.auth]
  # type = "bearer_token"
  # token = "xxxxxxxx"
  # token_file = "/tmp/token"

  ## Customize measurement set name.
  # Treat those metrics with prefix as one set.
  # Prioritier over 'measurement_name' configuration.
  #[[inputs.prom.measurements]]
  #  prefix = "cpu_"
  #  name = "cpu"

  # [[inputs.prom.measurements]]
  # prefix = "mem_"
  # name = "mem"

  # Not collecting those data when tag matched.
  [inputs.prom.ignore_tag_kv_match]
  # key1 = [ "val1.*", "val2.*"]
  # key2 = [ "val1.*", "val2.*"]

  # Add HTTP headers to data pulling.
  [inputs.prom.http_headers]
  # Root = "passwd"
  # Michael = "1234"

  # Rename tag key in prom data.
  [inputs.prom.tags_rename]
    overwrite_exist_tags = false
    [inputs.prom.tags_rename.mapping]
    # tag1 = "new-name-1"
    # tag2 = "new-name-2"
    # tag3 = "new-name-3"

  # Send collected metrics to center as log.
  # When 'service' field is empty, using 'service tag' as measurement set name.
  [inputs.prom.as_logging]
    enable = false
    service = "service_name"

  ## Customize tags.
  [inputs.prom.tags]
  # some_tag = "some_value"
  # more_tag = "some_other_value"
  # 网络设备的IP
  device_ip= "127.0.0.1"
```



### 配置被采集 SNMP 设备

SNMP 设备在默认情况下，一般 SNMP 协议处于关闭状态，需要进入管理界面手动打开。同时，需要根据实际情况选择协议版本和填写相应信息。

有些设备为了安全需要额外配置放行 SNMP，具体因设备而异。比如华为系防火墙，需要在 "启用访问管理" 中勾选 SNMP 以放行。 可以使用 `snmpwalk` 命令来测试采集侧与设备侧是否配置连通成功（在 Datasleuth 运行的主机上运行以下命令）：

```
# 适用 v2c 版本
snmpwalk -O bentU -v 2c -c [community string] [SNMP_DEVICE_IP] 1.3.6
# 适用 v3 版本
snmpwalk -v 3 -u user -l authPriv -a sha -A [认证密码] -x aes -X [加密密码] [SNMP_DEVICE_IP] 1.3.6
```

如果配置没有问题的话，该命令会输出大量数据。`snmpwalk` 是运行在采集侧的一个测试工具，MacOS 下自带，Linux 安装方法：

```
sudo yum install net-snmp net-snmp-utils # CentOS
sudo apt–get install snmp                # Ubuntu
```

### snmp-exporter配置文件

```
auths:
  public_v1:
    community: read
    security_level: noAuthNoPriv
    auth_protocol: MD5
    priv_protocol: DES
    version: 1
  public_v2:
    community: read
    security_level: noAuthNoPriv
    auth_protocol: MD5
    priv_protocol: DES
    version: 2
modules:
  if_mib:
    walk:
    - 1.3.6.1.2.1.2
    - 1.3.6.1.2.1.31.1.1
    get:
    - 1.3.6.1.2.1.1.3.0
    metrics:
    - name: sysUpTime
      oid: 1.3.6.1.2.1.1.3
      type: gauge
      help: The time (in hundredths of a second) since the network management portion
        of the system was last re-initialized. - 1.3.6.1.2.1.1.3
    - name: ifSpeed
      oid: 1.3.6.1.2.1.2.2.1.5
      type: gauge
      help: An estimate of the interface's current bandwidth in bits per second -
        1.3.6.1.2.1.2.2.1.5
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifOperStatus
      oid: 1.3.6.1.2.1.2.2.1.8
      type: EnumAsInfo
      help: The current operational state of the interface - 1.3.6.1.2.1.2.2.1.8
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
      enum_values:
        1: up
        2: down
        3: testing
        4: unknown
        5: dormant
        6: notPresent
        7: lowerLayerDown
    - name: ifInOctets
      oid: 1.3.6.1.2.1.2.2.1.10
      type: counter
      help: The total number of octets received on the interface, including framing
        characters - 1.3.6.1.2.1.2.2.1.10
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifInUcastPkts
      oid: 1.3.6.1.2.1.2.2.1.11
      type: counter
      help: The number of packets, delivered by this sub-layer to a higher (sub-)layer,
        which were not addressed to a multicast or broadcast address at this sub-layer
        - 1.3.6.1.2.1.2.2.1.11
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifInNUcastPkts
      oid: 1.3.6.1.2.1.2.2.1.12
      type: counter
      help: The number of packets, delivered by this sub-layer to a higher (sub-)layer,
        which were addressed to a multicast or broadcast address at this sub-layer
        - 1.3.6.1.2.1.2.2.1.12
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifInDiscards
      oid: 1.3.6.1.2.1.2.2.1.13
      type: counter
      help: The number of inbound packets which were chosen to be discarded even though
        no errors had been detected to prevent their being deliverable to a higher-layer
        protocol - 1.3.6.1.2.1.2.2.1.13
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifInErrors
      oid: 1.3.6.1.2.1.2.2.1.14
      type: counter
      help: For packet-oriented interfaces, the number of inbound packets that contained
        errors preventing them from being deliverable to a higher-layer protocol -
        1.3.6.1.2.1.2.2.1.14
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifInUnknownProtos
      oid: 1.3.6.1.2.1.2.2.1.15
      type: counter
      help: For packet-oriented interfaces, the number of packets received via the
        interface which were discarded because of an unknown or unsupported protocol
        - 1.3.6.1.2.1.2.2.1.15
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifOutOctets
      oid: 1.3.6.1.2.1.2.2.1.16
      type: counter
      help: The total number of octets transmitted out of the interface, including
        framing characters - 1.3.6.1.2.1.2.2.1.16
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifOutUcastPkts
      oid: 1.3.6.1.2.1.2.2.1.17
      type: counter
      help: The total number of packets that higher-level protocols requested be transmitted,
        and which were not addressed to a multicast or broadcast address at this sub-layer,
        including those that were discarded or not sent - 1.3.6.1.2.1.2.2.1.17
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifOutNUcastPkts
      oid: 1.3.6.1.2.1.2.2.1.18
      type: counter
      help: The total number of packets that higher-level protocols requested be transmitted,
        and which were addressed to a multicast or broadcast address at this sub-layer,
        including those that were discarded or not sent - 1.3.6.1.2.1.2.2.1.18
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifOutDiscards
      oid: 1.3.6.1.2.1.2.2.1.19
      type: counter
      help: The number of outbound packets which were chosen to be discarded even
        though no errors had been detected to prevent their being transmitted - 1.3.6.1.2.1.2.2.1.19
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifOutErrors
      oid: 1.3.6.1.2.1.2.2.1.20
      type: counter
      help: For packet-oriented interfaces, the number of outbound packets that could
        not be transmitted because of errors - 1.3.6.1.2.1.2.2.1.20
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifInMulticastPkts
      oid: 1.3.6.1.2.1.31.1.1.1.2
      type: counter
      help: The number of packets, delivered by this sub-layer to a higher (sub-)layer,
        which were addressed to a multicast address at this sub-layer - 1.3.6.1.2.1.31.1.1.1.2
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifInBroadcastPkts
      oid: 1.3.6.1.2.1.31.1.1.1.3
      type: counter
      help: The number of packets, delivered by this sub-layer to a higher (sub-)layer,
        which were addressed to a broadcast address at this sub-layer - 1.3.6.1.2.1.31.1.1.1.3
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifOutMulticastPkts
      oid: 1.3.6.1.2.1.31.1.1.1.4
      type: counter
      help: The total number of packets that higher-level protocols requested be transmitted,
        and which were addressed to a multicast address at this sub-layer, including
        those that were discarded or not sent - 1.3.6.1.2.1.31.1.1.1.4
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifOutBroadcastPkts
      oid: 1.3.6.1.2.1.31.1.1.1.5
      type: counter
      help: The total number of packets that higher-level protocols requested be transmitted,
        and which were addressed to a broadcast address at this sub-layer, including
        those that were discarded or not sent - 1.3.6.1.2.1.31.1.1.1.5
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifHCInOctets
      oid: 1.3.6.1.2.1.31.1.1.1.6
      type: counter
      help: The total number of octets received on the interface, including framing
        characters - 1.3.6.1.2.1.31.1.1.1.6
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifHCInUcastPkts
      oid: 1.3.6.1.2.1.31.1.1.1.7
      type: counter
      help: The number of packets, delivered by this sub-layer to a higher (sub-)layer,
        which were not addressed to a multicast or broadcast address at this sub-layer
        - 1.3.6.1.2.1.31.1.1.1.7
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifHCInMulticastPkts
      oid: 1.3.6.1.2.1.31.1.1.1.8
      type: counter
      help: The number of packets, delivered by this sub-layer to a higher (sub-)layer,
        which were addressed to a multicast address at this sub-layer - 1.3.6.1.2.1.31.1.1.1.8
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifHCInBroadcastPkts
      oid: 1.3.6.1.2.1.31.1.1.1.9
      type: counter
      help: The number of packets, delivered by this sub-layer to a higher (sub-)layer,
        which were addressed to a broadcast address at this sub-layer - 1.3.6.1.2.1.31.1.1.1.9
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifHCOutOctets
      oid: 1.3.6.1.2.1.31.1.1.1.10
      type: counter
      help: The total number of octets transmitted out of the interface, including
        framing characters - 1.3.6.1.2.1.31.1.1.1.10
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifHCOutUcastPkts
      oid: 1.3.6.1.2.1.31.1.1.1.11
      type: counter
      help: The total number of packets that higher-level protocols requested be transmitted,
        and which were not addressed to a multicast or broadcast address at this sub-layer,
        including those that were discarded or not sent - 1.3.6.1.2.1.31.1.1.1.11
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifHCOutMulticastPkts
      oid: 1.3.6.1.2.1.31.1.1.1.12
      type: counter
      help: The total number of packets that higher-level protocols requested be transmitted,
        and which were addressed to a multicast address at this sub-layer, including
        those that were discarded or not sent - 1.3.6.1.2.1.31.1.1.1.12
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
    - name: ifHCOutBroadcastPkts
      oid: 1.3.6.1.2.1.31.1.1.1.13
      type: counter
      help: The total number of packets that higher-level protocols requested be transmitted,
        and which were addressed to a broadcast address at this sub-layer, including
        those that were discarded or not sent - 1.3.6.1.2.1.31.1.1.1.13
      indexes:
      - labelname: ifIndex
        type: gauge
      lookups:
      - labels:
        - ifIndex
        labelname: ifAlias
        oid: 1.3.6.1.2.1.31.1.1.1.18
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifDescr
        oid: 1.3.6.1.2.1.2.2.1.2
        type: DisplayString
      - labels:
        - ifIndex
        labelname: ifName
        oid: 1.3.6.1.2.1.31.1.1.1.1
        type: DisplayString
  huawei-entity-extent-mib:
    walk:
    - 1.3.6.1.4.1.2011.5.25.31.1.1.1
    - 1.3.6.1.4.1.2011.5.25.31.1.1.10
    - 1.3.6.1.4.1.2011.5.25.31.1.1.18
    metrics:
    - name: entityCpuUsage
      oid: 1.3.6.1.4.1.2011.5.25.31.1.1.1.1.5
      type: gauge
      help: This object indicates the CPU usage of an entity - 1.3.6.1.4.1.2011.5.25.31.1.1.1.1.5
      indexes:
      - labelname: entPhysicalIndex
        type: gauge
    - name: entityMemUsage
      oid: 1.3.6.1.4.1.2011.5.25.31.1.1.1.1.7
      type: gauge
      help: This object indicates the memory usage of an entity, that is, the percentage
        of the memory that has been used. - 1.3.6.1.4.1.2011.5.25.31.1.1.1.1.7
      indexes:
      - labelname: entPhysicalIndex
        type: gauge
    - name: entityFanState
      oid: 1.3.6.1.4.1.2011.5.25.31.1.1.10.1.7
      type: EnumAsInfo
      help: This object indicates the fan status. - 1.3.6.1.4.1.2011.5.25.31.1.1.10.1.7
      indexes:
      - labelname: hwEntityFanSlot
        type: gauge
      - labelname: hwEntityFanSn
        type: gauge
      enum_values:
        1: normal
        2: abnormal
    - name: entityPwrState
      oid: 1.3.6.1.4.1.2011.5.25.31.1.1.18.1.6
      type: EnumAsInfo
      help: This object indicates the state of the power. - 1.3.6.1.4.1.2011.5.25.31.1.1.18.1.6
      indexes:
      - labelname: hwEntityPwrSlot
        type: gauge
      - labelname: hwEntityPwrSn
        type: gauge
      enum_values:
        1: supply
        2: notSupply
        3: sleep
        4: unknown

  hh3c-entity-ext-mib:
    walk:
    - 1.3.6.1.4.1.25506.2.6.1.1.1.1
    metrics:
    - name: entityCpuUsage
      oid: 1.3.6.1.4.1.25506.2.6.1.1.1.1.6
      type: gauge
      help: 实体CPU实时利用率,统计周期为5秒钟
      indexes:
      - labelname: hh3cEntityExtPhysicalIndex
        type: gauge
    - name: entityMemUsage
      oid: 1.3.6.1.4.1.25506.2.6.1.1.1.1.8
      type: gauge
      help: 实体内存实时利用率百分比
      indexes:
      - labelname: hh3cEntityExtPhysicalIndex
        type: gauge
  hh3c-lswdevm-mib:
    walk:
    - 1.3.6.1.4.1.25506.8.35.9.1.1.1
    - 1.3.6.1.4.1.25506.8.35.9.1.2.1
    metrics:
    - name: entityFanState
      oid: 1.3.6.1.4.1.25506.8.35.9.1.1.1.2
      type: EnumAsInfo
      help: 风扇的状态信息
      indexes:
      - labelname: hh3cDevMFanNum
        type: gauge
      enum_values:
        1: normal
        2: deactive
        3: not-install
        4: unsupport
    - name: entityPwrState
      oid: 1.3.6.1.4.1.25506.8.35.9.1.2.1.2
      type: EnumAsInfo
      help: 电源的状态信息
      indexes:
      - labelname: hh3cDevMPowerNum
        type: gauge
      enum_values:
        1: supply
        2: deactive
        3: not-install
        4: unsupport

```

