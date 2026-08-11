# DM8-EXPORTER
## 配置

***注意：***  DM数据库本身提供监控管理工具DEM，但是商业版收费，此文档介绍使用dmdb-exporter采集指标数据，是免费的，DEM的接入方法见另一篇文档DM8。

### exporter安装

将目录下生成的二进制文件`dmdb_exporter`和`default-metrics.toml`都上传到达梦所在服务器进行启动

```
# 授权可执行
chmod +x dmdb_exporter

# 启动前先设置环境变量
export DATA_SOURCE_NAME=dm://SYSDBA:SYSDBA@localhost:5236?autoCommit=true

#执行exporter, 端口为9161
nohup ./dmdb_exporter --log.level=info --default.metrics=default-metrics.toml --web.listen-address=:9161 &
```

通过浏览器查看指标相关信息：`http://127.0.0.1:9161/metrics`。端口可能有差异，以实际端口为准即可。



### datasleuth 采集器配置

在可观测平台的业务观测--数据接入--指标接入页面配置达梦。

调整内容如下：

```
enable_input = true
urls = ["http://clientIP:9161/metrics"]
source = "dm"
interval = "10s"
```

*其他配置按需调整*

，调整参数说明 ：

- urls：`prometheus`指标地址，这里填写对应组件暴露出来的指标 url
- source：采集器别名，建议做区分
- interval：采集间隔

