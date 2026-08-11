# Nginx
## 配置

### 前置条件

- NGINX 版本 >= `1.8.0`; 已测试的版本：
  - 1.23.2
  - 1.22.1
  - 1.21.6
  - 1.18.0
  - 1.14.2
  - 1.8.0
- NGINX 默认采集 `http_stub_status_module` 模块的数据，开启 `http_stub_status_module` 模块参见[这里](http://nginx.org/en/docs/http/ngx_http_stub_status_module.html)，开启了以后会上报 NGINX 指标集的数据；
- 如果您正在使用 [VTS](https://github.com/vozlt/nginx-module-vts) 或者想监控更多数据，建议开启 VTS 相关数据采集，可在 `nginx.conf` 中将选项 `use_vts` 设置为 `true`。如何开启 VTS 参见[这里](https://github.com/vozlt/nginx-module-vts#synopsis);
- 开启 VTS 功能后，能产生如下指标集：
  - `nginx`
  - `nginx_server_zone`
  - `nginx_upstream_zone` (NGINX 需配置 [`upstream` 相关配置](http://nginx.org/en/docs/http/ngx_http_upstream_module.html))
  - `nginx_cache_zone` (NGINX 需配置 [`cache` 相关配置](https://docs.nginx.com/nginx/admin-guide/content-cache/content-caching/))
- 以产生 `nginx_upstream_zone` 指标集为例，NGINX 相关配置示例如下：

```
...
http {
   ...
   upstream your-upstreamname {
     server upstream-ip:upstream-port;
  }
   server {
   ...
   location / {
   root  html;
   index  index.html index.htm;
   proxy_pass http://yourupstreamname;
}}}
```

- 已经开启了 VTS 功能以后，不必再去采集 `http_stub_status_module` 模块的数据，因为 VTS 模块的数据会包括 `http_stub_status_module` 模块的数据

### 采集器配置

在指标接入页面修改配置。

***Attention：***

`url` 地址以 nginx 具体配置为准，一般常见的用法就是用 `/basic_status` 这个路由。

## 指标

以下所有数据采集，默认会追加名为 `host` 的全局 tag（tag 值为 datasleuth所在主机名），也可以在配置中通过 `[inputs.nginx.tags]` 指定其它标签，这里我们添加cluster_name标签作为集群分组标签：

```
[inputs.nginx.tags]
 cluster_name = "metric_cluster_name"
 # more_tag = "some_other_value"
 # ...
```

### `nginx`

- 标签

| Tag             | Description                         |
| :-------------- | :---------------------------------- |
| `host`          | Host name which installed nginx     |
| `nginx_port`    | Nginx server port                   |
| `nginx_server`  | Nginx server host                   |
| `nginx_version` | Nginx version, exist when using vts |

- 指标列表

| Metric                | Description                                                  | Type | Unit  |
| :-------------------- | :----------------------------------------------------------- | :--: | :---: |
| `connection_accepts`  | The total number of accepts client connections               | int  | count |
| `connection_active`   | The current number of active client connections              | int  | count |
| `connection_handled`  | The total number of handled client connections               | int  | count |
| `connection_reading`  | The total number of reading client connections               | int  | count |
| `connection_requests` | The total number of requests client connections              | int  | count |
| `connection_waiting`  | The total number of waiting client connections               | int  | count |
| `connection_writing`  | The total number of writing client connections               | int  | count |
| `load_timestamp`      | Nginx process load time in milliseconds, exist when using vts | int  | msec  |

### `nginx_server_zone`

- 标签

| Tag             | Description                     |
| :-------------- | :------------------------------ |
| `host`          | host name which installed nginx |
| `nginx_port`    | nginx server port               |
| `nginx_server`  | nginx server host               |
| `nginx_version` | nginx version                   |
| `server_zone`   | server zone                     |

- 指标列表

| Metric         | Description                                                | Type | Unit  |
| :------------- | :--------------------------------------------------------- | :--: | :---: |
| `received`     | The total amount of data received from clients.            | int  |   B   |
| `requests`     | The total number of client requests received from clients. | int  | count |
| `response_1xx` | The number of responses with status codes 1xx              | int  | count |
| `response_2xx` | The number of responses with status codes 2xx              | int  | count |
| `response_3xx` | The number of responses with status codes 3xx              | int  | count |
| `response_4xx` | The number of responses with status codes 4xx              | int  | count |
| `response_5xx` | The number of responses with status codes 5xx              | int  | count |
| `send`         | The total amount of data sent to clients.                  | int  |   B   |

### `nginx_upstream_zone`

- 标签

| Tag               | Description                     |
| :---------------- | :------------------------------ |
| `host`            | host name which installed nginx |
| `nginx_port`      | nginx server port               |
| `nginx_server`    | nginx server host               |
| `nginx_version`   | nginx version                   |
| `upstream_server` | upstream server                 |
| `upstream_zone`   | upstream zone                   |

- 指标列表

| Metric          | Description                                               | Type | Unit  |
| :-------------- | :-------------------------------------------------------- | :--: | :---: |
| `received`      | The total number of bytes received from this server.      | int  |   B   |
| `request_count` | The total number of client requests received from server. | int  | count |
| `response_1xx`  | The number of responses with status codes 1xx             | int  | count |
| `response_2xx`  | The number of responses with status codes 2xx             | int  | count |
| `response_3xx`  | The number of responses with status codes 3xx             | int  | count |
| `response_4xx`  | The number of responses with status codes 4xx             | int  | count |
| `response_5xx`  | The number of responses with status codes 5xx             | int  | count |
| `send`          | The total number of bytes sent to clients.                | int  |   B   |

### `nginx_cache_zone`

- 标签

| Tag             | Description                     |
| :-------------- | :------------------------------ |
| `cache_zone`    | cache zone                      |
| `host`          | host name which installed nginx |
| `nginx_port`    | nginx server port               |
| `nginx_server`  | nginx server host               |
| `nginx_version` | nginx version                   |

- 指标列表

| Metric                  | Description                                                  | Type | Unit  |
| :---------------------- | :----------------------------------------------------------- | :--: | :---: |
| `max_size`              | The limit on the maximum size of the cache specified in the configuration | int  |   B   |
| `received`              | The total number of bytes received from the cache.           | int  |   B   |
| `responses_bypass`      | The number of cache bypass                                   | int  | count |
| `responses_expired`     | The number of cache expired                                  | int  | count |
| `responses_hit`         | The number of cache hit                                      | int  | count |
| `responses_miss`        | The number of cache miss                                     | int  | count |
| `responses_revalidated` | The number of cache revalidated                              | int  | count |
| `responses_scarce`      | The number of cache scarce                                   | int  | count |
| `responses_stale`       | The number of cache stale                                    | int  | count |
| `responses_updating`    | The number of cache updating                                 | int  | count |
| `send`                  | The total number of bytes sent from the cache.               | int  |   B   |
| `used_size`             | The current size of the cache.                               | int  |   B   |

