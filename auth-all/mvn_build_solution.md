# 解决 depend.iflytek.com 内网 Maven 源访问问题

## 问题分析

从构建错误日志可以看出，主要问题是：
```
Unknown host depend.iflytek.com
```

这表明当前环境无法解析或访问讯飞内网的 Maven 源服务器。

## 解决方案

### 方法一：确保连接到讯飞内网

depend.iflytek.com 是讯飞内部 Maven 仓库，需要连接到讯飞内网才能访问。请确认：
- 您是否连接了讯飞内部网络
- 您是否通过 VPN 接入了讯飞内网
- 网络连接是否正常

### 方法二：配置 Maven 设置文件

我们已经为您创建了适用于讯飞内网的 Maven 配置文件 `maven_settings.xml`，并修改了 `run.sh` 文件以使用此配置文件。

### 方法三：检查 DNS 解析

如果您确认已连接到讯飞内网但仍无法访问，请尝试：

1. 检查 DNS 配置是否正确
2. 尝试手动添加 depend.iflytek.com 的 IP 地址到 hosts 文件

### 方法四：配置代理

如果您需要通过代理访问内网 Maven 源，可以修改 `maven_settings.xml` 文件，添加代理配置：

```xml
<proxies>
  <proxy>
    <id>iflytek-proxy</id>
    <active>true</active>
    <protocol>http</protocol>
    <host>proxy-host</host> <!-- 替换为实际代理主机 -->
    <port>proxy-port</port> <!-- 替换为实际代理端口 -->
    <username>proxy-username</username> <!-- 如果需要认证 -->
    <password>proxy-password</password> <!-- 如果需要认证 -->
    <nonProxyHosts>localhost|127.0.0.1|*.iflytek.com</nonProxyHosts>
  </proxy>
</proxies>
```

### 方法五：使用离线依赖

如果以上方法都无法解决，您可以考虑：
1. 从其他已成功构建的环境中获取本地 Maven 仓库的依赖
2. 将这些依赖复制到您的本地 `.m2/repository` 目录中
3. 使用 `-o` 参数进行离线构建：`mvn clean package -DskipTests -o -s ./maven_settings.xml`

## 构建命令

修改后的构建命令已保存到 `run.sh` 文件中：
```bash
mvn clean package -DskipTests -s ./maven_settings.xml
```

您可以直接运行此脚本，或在命令行中手动执行上述命令。

## 其他注意事项

1. pom.xml 中还存在两个依赖版本缺失的问题：
   - org.projectlombok:lombok
   - org.springframework.boot:spring-boot-configuration-processor

2. 这些问题需要在网络问题解决后，通过添加正确的版本号来修复。

3. 如果您需要进一步的帮助，请联系讯飞内部的技术支持团队。