# 生产部署-微前端Nginx配置部署

更新时间：2026-08-18

## 1. 文档目标

本文用于规范 `鑫图平台` 当前微前端生产部署方式，覆盖：

- `cpo-basic-app-front`
- `cpo-ai-productivity-front`
- `Nginx` 静态托管与反向代理
- `auth-all` 微应用 `meta` 地址联动

本文不展开后端与中间件安装：

- 后端见《生产部署-后端微服务部署》
- 中间件见《生产部署-中间件二进制部署》

## 2. 当前前端边界

### 2.1 cpo-basic-app-front

- 门户基座
- 统一登录、菜单、工作空间
- 生产态从 `auth-all` 的 `/auth/meta` 读取微应用地址

### 2.2 cpo-ai-productivity-front

- 独立微前端
- 生产构建时 `base=/ai-productivity/`
- 可以独立访问，也可以被 `qiankun` 挂载

当前 Vite 配置中的关键行为：

```ts
base: command === "serve" ? "/" : "/ai-productivity/"
```

因此生产环境必须以 `/ai-productivity/` 作为静态前缀部署，不能直接丢到根路径。

## 3. 推荐目录

```text
/data/apps/star-devops/
├─ cpo-basic-app-front/
│  └─ dist/
├─ cpo-ai-productivity-front/
│  └─ dist/
└─ nginx/
   ├─ conf/
   └─ logs/
```

## 4. 构建产物

### 4.1 构建 cpo-basic-app-front

```bash
cd /home/kali/ai_study/star-devops/cpo-basic-app-front
PATH=/home/kali/.hermes/node/bin:$PATH npm ci
PATH=/home/kali/.hermes/node/bin:$PATH npm run build
```

产物：

```text
cpo-basic-app-front/dist/
```

### 4.2 构建 cpo-ai-productivity-front

```bash
cd /home/kali/ai_study/star-devops/cpo-ai-productivity-front
PATH=/home/kali/.hermes/node/bin:$PATH npm ci
PATH=/home/kali/.hermes/node/bin:$PATH npm run build
```

产物：

```text
cpo-ai-productivity-front/dist/
```

### 4.3 发布到生产目录

```bash
mkdir -p /data/apps/star-devops/cpo-basic-app-front/dist
mkdir -p /data/apps/star-devops/cpo-ai-productivity-front/dist

rsync -av --delete /home/kali/ai_study/star-devops/cpo-basic-app-front/dist/ \
  /data/apps/star-devops/cpo-basic-app-front/dist/

rsync -av --delete /home/kali/ai_study/star-devops/cpo-ai-productivity-front/dist/ \
  /data/apps/star-devops/cpo-ai-productivity-front/dist/
```

## 5. Nginx 职责

生产态 `Nginx` 负责：

- 门户首页静态资源托管
- AI 人效微前端静态资源托管
- `/auth/` 反向代理到 `auth-all`
- `/productivity-api/` 反向代理到 `cpo-ai-productivity-service`
- 可选 `/api/` 反向代理到 `cpo-api-gateway`
- `gzip`、缓存、TLS、访问控制

## 6. 标准 Nginx 配置

仓库样例文件：

```text
docs/templates/nginx-ai-productivity-prod.conf
```

推荐配置如下：

```nginx
upstream cpo_ai_productivity_backend {
    server 127.0.0.1:9060;
    keepalive 32;
}

server {
    listen 80;
    server_name portal.example.com;

    root /data/apps/star-devops/cpo-basic-app-front/dist;
    index index.html;

    gzip on;
    gzip_comp_level 5;
    gzip_min_length 1k;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml image/svg+xml;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /ai-productivity/ {
        alias /data/apps/star-devops/cpo-ai-productivity-front/dist/;
        index index.html;
        try_files $uri $uri/ /ai-productivity/index.html;
    }

    location /productivity-api/ {
        proxy_pass http://cpo_ai_productivity_backend/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 10s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    location /auth/ {
        proxy_pass http://127.0.0.1:9004/auth/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 7. 如果启用 KrakenD

如果前端不直接代理到后端，而是统一代理到 `cpo-api-gateway`，可改成：

```nginx
location /productivity-api/ {
    proxy_pass http://127.0.0.1:9000/api/productivity/;
}

location /api/astrbot/ {
    proxy_pass http://127.0.0.1:9000/api/astrbot/;
}
```

是否启用网关是部署选择，不影响微前端静态托管方式。

## 8. auth-all 元数据联动

### 8.1 为什么要联动

`cpo-basic-app-front` 生产态并不是写死微应用地址，而是通过 `auth-all` 的 `/auth/meta` 获取：

```json
{
  "moduleName": "ai-productivity",
  "moduleUrl": "https://portal.example.com/ai-productivity/"
}
```

### 8.2 必须修改的配置

在 `auth-all` 的 `Nacos` 配置 `application.yaml` 中，必须把：

```yaml
nginx:
  url: https://portal.example.com
```

改成真实门户域名或外网地址。

### 8.3 刷新微应用元数据

当门户域名、端口或微应用路径变化后，重新触发一次：

```bash
curl -X POST http://127.0.0.1:9004/auth/metaAndMenu/ai-productivity
```

再验证：

```bash
curl -s http://127.0.0.1:9004/auth/meta
```

返回中应能看到：

- `moduleName=ai-productivity`
- `moduleUrl` 指向 `/ai-productivity/`

## 9. 发布流程建议

### 9.1 首次发布

1. 构建两个前端产物
2. 发布到 `/data/apps/star-devops/.../dist`
3. 发布 `Nginx` 配置
4. 修改 `auth-all` 的 `nginx.url`
5. 触发 `/auth/metaAndMenu/ai-productivity`
6. 重载 `Nginx`

### 9.2 例行发版

```bash
PATH=/home/kali/.hermes/node/bin:$PATH npm run build
rsync -av --delete dist/ /data/apps/star-devops/<project>/dist/
nginx -t
nginx -s reload
```

## 10. Nginx 生产建议

### 10.1 访问控制

- `server_name` 只允许真实域名
- 关闭目录浏览
- 禁止上传类接口走静态站点
- 为 `Nginx` 管理端和状态页设置内网访问控制

### 10.2 缓存建议

- `index.html` 不做长缓存
- 哈希静态资源可做长缓存
- 微前端入口页变更后优先刷新 `index.html`

示例：

```nginx
location = /index.html {
    add_header Cache-Control "no-cache";
}

location /assets/ {
    add_header Cache-Control "public,max-age=31536000,immutable";
}
```

### 10.3 TLS 建议

生产建议统一启用 `HTTPS`：

```nginx
listen 443 ssl http2;
```

并同时把 `auth-all` 中的 `nginx.url` 改为 `https://` 前缀。

## 11. 常见问题

### 11.1 打开 /ai-productivity 空白

优先检查：

- `cpo-ai-productivity-front` 是否按生产模式构建
- 产物是否部署在 `/ai-productivity/`
- `try_files` 是否回退到 `/ai-productivity/index.html`

### 11.2 微前端能独立打开，但基座中打不开

优先检查：

- `auth-all /auth/meta` 返回的 `moduleUrl` 是否正确
- `moduleName` 是否仍然是 `ai-productivity`
- `nginx.url` 是否已经更新

### 11.3 登录后菜单存在，但跳转 404

优先检查：

- `Nginx` 是否已经发布 `/ai-productivity/`
- 宿主和微前端产物是否都更新
- 是否误把微前端部署到了根路径

## 12. 最终验收清单

- 门户首页可访问
- 登录页可正常打开
- 登录后菜单可见
- `ai-productivity` 页面可正常加载
- `?tab=collector` 可正常打开
- `/productivity-api/*` 请求正常返回
- `/auth/meta` 中微前端地址正确
- `Nginx -t` 通过且 reload 成功
