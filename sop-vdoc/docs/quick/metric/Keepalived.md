# Keepalived
## 安装配置
1. [下载exporter](https://github.com/mehdy/keepalived-exporter/releases/download/v1.3.2/keepalived-exporter-1.3.2.linux-amd64.tar.gz)到安装了keepalived的主机上
2. 将exporter解压，解压后是三个文件，不是一个文件夹
3. 启动：./keepalived-exporter &
4. 查询指标值：curl http://127.0.0.1:9165/metrics
5. 去可观测网页--数据接入--指标接入页面修改keepalived的配置，修改指标采集地址

```
[[inputs.prom]]
  # 是否启用该input
  enable_input = true

  # Exporter URLs.
  urls = ["http://172.31.65.35:9165/metrics"]

 ......
```



## 指标

| Metric                                          | 说明                             |
| ----------------------------------------------- | -------------------------------- |
| keepalived_exporter_build_info                  | Exporter build info              |
| keepalived_up                                   | Status of Keepalived service     |
| keepalived_vrrp_state                           | State of vrrp                    |
| keepalived_vrrp_excluded_state                  | State of vrrp with excluded VIP  |
| keepalived_exporter_check_script_status         | Check Script status for each VIP |
| keepalived_gratuitous_arp_delay_total           | Gratuitous ARP delay             |
| keepalived_advertisements_received_total        | Advertisements received          |
| keepalived_advertisements_sent_total            | Advertisements sent              |
| keepalived_become_master_total                  | Became master                    |
| keepalived_release_master_total                 | Released master                  |
| keepalived_packet_length_errors_total           | Packet length errors             |
| keepalived_advertisements_interval_errors_total | Advertisement interval errors    |
| keepalived_ip_ttl_errors_total                  | TTL errors                       |
| keepalived_invalid_type_received_total          | Invalid type errors              |
| keepalived_address_list_errors_total            | Address list errors              |
| keepalived_authentication_invalid_total         | Authentication invalid           |
| keepalived_authentication_mismatch_total        | Authentication mismatch          |
| keepalived_authentication_failure_total         | Authentication failure           |
| keepalived_priority_zero_received_total         | Priority zero received           |
| keepalived_priority_zero_sent_total             | Priority zero sent               |
| keepalived_script_status                        | Tracker Script Status            |
| keepalived_script_state                         | Tracker Script State             |

