# 日志sdk规范和接入指南
| 修订内容                                 | **版本**      | **日期**      | **作者** |
|--------------------------------------|-------------|-------------|--------|
| 初建                                   | 1.2-general | 2023年10月01日 | 邱文轩    |
| 增加source，threadName字段                | 1.3-general | 2023年11月01日 | 邱文轩    |
| 增加endpointName,serviceInstanceName字段 | 1.4-GENERAL | 2024年01月12日 | 邱文轩    |
| 增加formatDateTime字段                   | 1.5-general | 2024年03月13日 | 邱文轩    |

## 日志规范
### 简介
日志是应用程序中非常重要的一部分，它记录了程序运行过程中的各种信息，包括错误、警告、调试信息等。一个好的日志规范可以帮助开发人员更好地理解程序的运行情况，快速定位和解决问题。本文将介绍Java后端的日志规范，包括日志级别、日志格式等方面的内容。

### 日志级别
根据打印的日志内容来确定需要生成的日志级别

* debug调试信息：在开发和调试的时候使用，正式环境不打印。包括参数信息、调试细节、返回值信息等等。

*  info：主要记录系统的关键信息，旨在保留系统正常工作期间关键运行指标。一般将初始化系统配置、业务状态变化、业务流程核心处理记录。

*  warn警告信息：主要输出警告性质的日志，这些内容是可以预知而且有规划的，即使出现程序也能正常处理。warn级别属于可以预见的错误，需要尽早处理。

*  error错误信息：主要针对一些不可预知的错误信息。error日志要把异常栈记录全，error日志最好要进行全局的收集，避免在每个错误处收集，导致有大量的try:catch:exception代码。

### 日志文件名称
日志文件放置于固定的目录中，按照一定的模板进行命名，推荐的日志文件名称：
```
当前正在写入的日志文件名：<应用名>[-<功能名>].log
eg:testSkywalking-service.log
已经滚入历史的日志文件名：<应用名>[-<功能名>].yyyy-MM-dd.[滚动号].log
eg:testSkywalking-service.2023-10-24-13.1.log
```

### 日志文件分类

|  日志名称   | 功能说明  |
|  ----  | ----  |
| application-default.log | 默认日志，存放应用程序启动日志以及其他非规范内的日志 |
| application-service.log | 服务日志，存在应用程序执行流程日志 |
| application-request.log | 外部应用程序请求本应用的相关日志 |
| application-integration.log | 本应用请求第三方应用的相关日志 |
|application-error.log|错误堆栈信息日志，主要用途是排查问题|

注意：此日志分类只是建议，如果要按照该日志分类进行打印，需要调用LoggerUtil工具类，获取特定logger进行打印

### 日志内容格式
统一日志为json 格式，方便后续的收集和分析

```json
{
    "level":"INFO",
    "dateTime":1705026308688136300,
    "formatDateTime":"2024-03-13 14:49:39",
    "ip":"10.5.166.68",
    "application":"testSkywalking|dev|",
    "methodName":"insertUserToMysql5$original$pTbr25BY",
    "logger":"testLog4j2Logger",
    "message":"{\"name\":\"test111\",\"age\":11}",
    "exception":"",
    "source":"37d0f0d8-f870-49d0-9a5e-47414397e83e",
    "threadName":"http-nio-8080-exec-4",
    "traceId":"aaba98014ee14ee2aedf61e69c765eb9.50.17050263080320001",
   "serviceInstanceName":"5b539c54b4184285ad2aa32b3079f06f@10.5.166.68",
    "endpointName":"POST:/db/save/user/mysql5"
 
}
```
*  traceId 调用链唯一标识 ，如果没有接入skywalking,则没有该字段
* level 日志级别
* datetime 日志产生时间，精确到纳秒
* formatDateTime 日志产生时间，格式化yyyy-MM-dd HH:mm:ss
* ip 应用程序所在的服务器ip地址
* application 应用名称
* logger 日志名称
* message 日志消息实体
* exception 异常信息栈
* source  sdk初始化的时候uuid生成，用来唯一标识一个启动的进程
* threadName 线程名称
* serviceInstanceName 服务实例名称，如果没有接入skywalking,则没有该字段
* endpointName 端点名称，如果没有接入skywalking,则没有该字段

## 接入指南
###  简介
本SDK是一款面向Java后端应用程序的日志规范打印工具，旨在提供一种简单、灵活的方式来轻松地统一日志格式。该SDK支持适配Log4j、Log4j2、Logback三大主流日志框架，用户只需引入相应的依赖和修改日志配置文件，调用LoggerUtil对应的logger对象即可实现对日志的规范打印。

注：我们提供日志sdk的接入demo，包括maven和gradle两种编译方式。如果需要的话，可以联系我们给你开通代码权限。

demo 仓库地址

https://code.iflytek.com/TC_IFLY_BigDrip/log-demo-maven.git

https://code.iflytek.com/TC_IFLY_BigDrip/log-demo-gradle.git

## logback接入
### 1. 项目引入依赖
#### 1. maven
```xml
     <dependency>
            <groupId>com.iflytek.itsc.apm</groupId>
            <artifactId>apm-toolkit-logback-1.x</artifactId>
            <version>1.5-GENERAL</version>
      </dependency>
```
#### 2. gradle
* repositories 下增加技术中心私服仓库配置
```
 maven { url 'https://depend.iflytek.com/artifactory/stc-mvn-private-repo/' }
```
* dependencies 下增加如下依赖
```
implementation 'com.iflytek.itsc.apm:apm-toolkit-logback-1.x:1.5-GENERAL'
```
注意:  该jar包底层依赖的是logback-classic-1.2.3，logstash-logback-encoder-6.1版本,如果和项目的日志框架版本冲突，需要进行依赖排除

### 2. 打印方式修改（可选，如果按照我们建议的日志文件进行分类打印，需要修改打印方式）
根据打印的日志内容调用LoggerUtil的不同logger对象打印日志
logger对象种类
SERVICE_LOG ：打印应用程序执行流程的业务日志logger对象
REQUEST_LOG：打印外部应用请求本应用的相关日志logger对象
INTEGRATION_LOG：打印本应用请求第三方应用的相关日志logger对象

```java
LoggerUtil.SERVICE_LOG.info("test");
```

### 3. 修改logback.xml 配置文件

在需要修改的appender下把layout修改成GeneralJsonStringLayout

```xml
<encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
      <layout
            class="com.iflytek.itsc.apm.logback.layout.GeneralJsonStringLayout">
      </layout>
</encoder>
```



完整eg: 本示例使用我们建议的日志文件分类的配置和使用的异步方式打印

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- 配置文件每隔1分钟，就检查更新 -->
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <!-- 定义参数常量，便于后面直接用${name}来获取value值 -->
    <property name="log.pattern" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%-5level] %logger{50} - %msg%n"/>
    <property name="log.filePath" value="${APPLOGS_DIR:-/data/logs/log-demo-gradle}"/>
    <property name="serviceName" value="${APPLICATION:-testLog}"/>
    <property name="log.maxHistory" value="7"/>

    <!-- ch.qos.logback.core.ConsoleAppender 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout
                    class="com.iflytek.itsc.apm.logback.layout.GeneralJsonStringLayout">
            </layout>
        </encoder>
    </appender>

    <appender name="SERVICELOGFILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${log.filePath}/${serviceName}-service.log</file>
        <!-- 设置基于时间(每天)的滚动策略，也就是将日志内容按照日期来写入到相应的文件中-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 当天之前的每天生成的日志文件的路径 -->
            <fileNamePattern>${log.filePath}/${serviceName}-service-%d{yyyy-MM-dd}.log.gz
            </fileNamePattern>
            <!-- 最大历史保存文件的数量，只保存最近30天的日志文件，超出的会被删除-->
            <maxHistory>${log.maxHistory}</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout
                    class="com.iflytek.itsc.apm.logback.layout.GeneralJsonStringLayout">
            </layout>
        </encoder>
    </appender>

    <appender name="REQUESTLOGFILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${log.filePath}/${serviceName}-request.log</file>
        <!-- 设置基于时间(每天)的滚动策略，也就是将日志内容按照日期来写入到相应的文件中-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 当天之前的每天生成的日志文件的路径 -->
            <fileNamePattern>${log.filePath}/${serviceName}-request-%d{yyyy-MM-dd}.log.gz
            </fileNamePattern>
            <!-- 最大历史保存文件的数量，只保存最近30天的日志文件，超出的会被删除-->
            <maxHistory>${log.maxHistory}</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout
                    class="com.iflytek.itsc.apm.logback.layout.GeneralJsonStringLayout">
            </layout>
        </encoder>
    </appender>
    <appender name="INTEGRATIONLOGFILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${log.filePath}/${serviceName}-integration.log</file>
        <!-- 设置基于时间(每天)的滚动策略，也就是将日志内容按照日期来写入到相应的文件中-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 当天之前的每天生成的日志文件的路径 -->
            <fileNamePattern>${log.filePath}/${serviceName}-integration-%d{yyyy-MM-dd}.log.gz
            </fileNamePattern>
            <!-- 最大历史保存文件的数量，只保存最近30天的日志文件，超出的会被删除-->
            <maxHistory>${log.maxHistory}</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout
                    class="com.iflytek.itsc.apm.logback.layout.GeneralJsonStringLayout">
            </layout>
        </encoder>
    </appender>

    <appender name="DEFAULTLOGFILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${log.filePath}/${serviceName}-default.log</file>
        <!-- 设置基于时间(每天)的滚动策略，也就是将日志内容按照日期来写入到相应的文件中-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 当天之前的每天生成的日志文件的路径 -->
            <fileNamePattern>${log.filePath}/${serviceName}-default-%d{yyyy-MM-dd}.log.gz
            </fileNamePattern>
            <!-- 最大历史保存文件的数量，只保存最近30天的日志文件，超出的会被删除-->
            <maxHistory>${log.maxHistory}</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout
                    class="com.iflytek.itsc.apm.logback.layout.GeneralJsonStringLayout">
            </layout>
        </encoder>
    </appender>

    <appender name="ERRORLOGFILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${log.filePath}/${serviceName}-error.log</file>
        <!-- 设置基于时间(每天)的滚动策略，也就是将日志内容按照日期来写入到相应的文件中-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 当天之前的每天生成的日志文件的路径 -->
            <fileNamePattern>${log.filePath}/${serviceName}-error-%d{yyyy-MM-dd}.log.gz
            </fileNamePattern>
            <!-- 最大历史保存文件的数量，只保存最近30天的日志文件，超出的会被删除-->
            <maxHistory>${log.maxHistory}</maxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout
                    class="com.iflytek.itsc.apm.logback.layout.GeneralJsonStringLayout">
            </layout>
        </encoder>
        <!-- 过滤掉非error级别的信息 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>error</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <appender name="ASYNCSERVICELOGFILE" class="ch.qos.logback.classic.AsyncAppender" includeCallerData="true">
        <appender-ref ref="SERVICELOGFILE"/>
    </appender>

    <appender name="ASYNCERRORLOGFILE" class="ch.qos.logback.classic.AsyncAppender" includeCallerData="true">
        <appender-ref ref="ERRORLOGFILE"/>
    </appender>

    <appender name="ASYNCREQUESTLOGFILE" class="ch.qos.logback.classic.AsyncAppender" includeCallerData="true">
        <appender-ref ref="REQUESTLOGFILE"/>
    </appender>

    <appender name="ASYNCINTEGRATIONLOGFILE" class="ch.qos.logback.classic.AsyncAppender" includeCallerData="true">
        <appender-ref ref="INTEGRATIONLOGFILE"/>
    </appender>

    <appender name="ASYNCDEFAULTLOGFILE" class="ch.qos.logback.classic.AsyncAppender" includeCallerData="true">
        <appender-ref ref="DEFAULTLOGFILE"/>
    </appender>


    <logger name="serviceLogger" additivity="false">
        <appender-ref ref="ASYNCSERVICELOGFILE"/>
        <appender-ref ref="ASYNCERRORLOGFILE"/>
    </logger>

    <logger name="requestLogger" additivity="false">
        <appender-ref ref="ASYNCREQUESTLOGFILE"/>
        <appender-ref ref="ASYNCERRORLOGFILE"/>
    </logger>

    <logger name="integrationLogger" additivity="false">
        <appender-ref ref="ASYNCINTEGRATIONLOGFILE"/>
        <appender-ref ref="ASYNCERRORLOGFILE"/>
    </logger>

    <root level="info">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ASYNCDEFAULTLOGFILE"/>
    </root>
</configuration>
```
 ### 4.启动脚本修改
 1. 增加日志打印路径APPLOGS_DIR和应用程序名称APPLICATION系统参数
 eg:
```
-DAPPLOGS_DIR=D:/logs
-DAPPLICATION=testLog4j
```
注：具体的值根据项目修改

### 5. 项目接入skywalking  agent

注：skywalking agent 可选接入，接入后会打印traceId,serviceInstanceName,endpointName字段

1. 在项目所在服务器上上传 apache-skywalking-java-agent-8.16.0.zip

```shell
rz apache-skywalking-java-agent-8.16.0.zip
```
2. 解压zip
```shell
unzip apache-skywalking-java-agent-8.16.0.zip
```

3. 项目启动脚本修改

  * 启动脚本增加javaagent配置

```
-javaagent:/data/apache-skywalking-java-agent-8.16.0/skywalking-agent/skywalking-agent.jar=agent.service_name=your_applicationName,collector.backend_service=oap_server_url,skywalking_config=/data/apache-skywalking-java-agent-8.16.0/skywalking-agent/config/agent.config
```
your_applicationName: 根据应用程序名称进行替换
oap_server_url:根据后端oap服务地址进行替换

## log4j2接入

### 1. 项目引入依赖
#### 1. maven
```xml
     <dependency>
            <groupId>com.iflytek.itsc.apm</groupId>
            <artifactId>apm-toolkit-log4j-2.x</artifactId>
            <version>1.5-GENERAL</version>
     </dependency>
```

#### 2. gradle
* repositories 下增加技术中心私服仓库配置
```
 maven { url 'https://depend.iflytek.com/artifactory/stc-mvn-private-repo/' }
```
* dependencies 下增加如下依赖
```
implementation 'com.iflytek.itsc.apm:apm-toolkit-log4j-2.x:1.5-GENERAL'
```
注意:  该jar包底层依赖的是log4j-2.20.0，log4j-layout-template-json-2.20.0版本,如果和项目的日志版本冲突，需要进行依赖排除

### 2. 打印方式修改（可选，如果按照我们建议的日志文件进行分类打印，需要修改打印方式）
根据打印的日志内容调用LoggerUtil的不同logger对象打印日志
logger对象种类
SERVICE_LOG ：打印应用程序执行流程的业务日志logger对象
REQUEST_LOG：打印外部应用请求本应用的相关日志logger对象
INTEGRATION_LOG：打印本应用请求第三方应用的相关日志logger对象

```java
LoggerUtil.SERVICE_LOG.info("test");
```

### 3. 修改log4j2.xml 配置文件
在需要修改的appender下把layout修改成GeneralJsonStringLayout
```xml
  <GeneralJsonStringLayout/>
```

完整eg：此示例是使用我们建议的日志文件分类配置和log4j2全异步无锁方案配置
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration status="warn" monitorInterval="5">
    <properties>
        <property name="LOG_HOME">${sys:APPLOGS_DIR:-/data/logs/log-demo-gradle}</property>
        <property name="serviceName">${sys:APPLICATION:-testLog4j2}</property>
        <property name="maxRolloverStrategy">7</property>
        <property name="log4j2.contextSelector">org.apache.logging.log4j.core.async.AsyncLoggerContextSelector</property>
    </properties>

    <Appenders>
        <Console name="CONSOLE" target="SYSTEM_OUT">
            <GeneralJsonStringLayout/>
        </Console>

        <RollingFile name="SERVICELOGFILE" fileName="${LOG_HOME}/${serviceName}-service.log"
                     filePattern="${LOG_HOME}/$${date:yyyy-MM-dd}/${serviceName}-service-%d{yyyy-MM-dd-HH-mm}-%i.log.gz">
            <GeneralJsonStringLayout/>
            <Policies>
                <OnStartupTriggeringPolicy />
                <SizeBasedTriggeringPolicy size="250 MB" />
                <TimeBasedTriggeringPolicy />
            </Policies>
            <DefaultRolloverStrategy max="${maxRolloverStrategy}" />
        </RollingFile>

        <RollingFile name="REQUESTLOGFILE" fileName="${LOG_HOME}/${serviceName}-request.log"
                     filePattern="${LOG_HOME}/$${date:yyyy-MM-dd}/{serviceName}-request-%d{yyyy-MM-dd-HH-mm}-%i.log.gz">
            <GeneralJsonStringLayout/>
            <Policies>
                <OnStartupTriggeringPolicy />
                <SizeBasedTriggeringPolicy size="250 MB" />
                <TimeBasedTriggeringPolicy />
            </Policies>
            <DefaultRolloverStrategy max="${maxRolloverStrategy}" />
        </RollingFile>
        <RollingFile name="INTEGRATIONLOGFILE" fileName="${LOG_HOME}/${serviceName}-integration.log"
                     filePattern="${LOG_HOME}/$${date:yyyy-MM-dd}/{serviceName}-integration-%d{yyyy-MM-dd-HH-mm}-%i.log.gz">
            <GeneralJsonStringLayout/>
            <Policies>
                <OnStartupTriggeringPolicy />
                <SizeBasedTriggeringPolicy size="250 MB" />
                <TimeBasedTriggeringPolicy />
            </Policies>
            <DefaultRolloverStrategy max="${maxRolloverStrategy}" />
        </RollingFile>

        <RollingFile name="DEFAULTLOGFILE" fileName="${LOG_HOME}/${serviceName}-default.log"
                     filePattern="${LOG_HOME}/$${date:yyyy-MM-dd}/${serviceName}-default-%d{yyyy-MM-dd-HH-mm}-%i.log.gz">
            <GeneralJsonStringLayout/>
            <Policies>
                <OnStartupTriggeringPolicy />
                <SizeBasedTriggeringPolicy size="250 MB" />
                <TimeBasedTriggeringPolicy />
            </Policies>
            <DefaultRolloverStrategy max="${maxRolloverStrategy}" />
        </RollingFile>

        <RollingFile name="ERRORLOGFILE" fileName="${LOG_HOME}/${serviceName}-error.log"
                     filePattern="${LOG_HOME}/$${date:yyyy-MM-dd}/${serviceName}-error-%d{yyyy-MM-dd-HH-mm}-%i.log.gz">

            <ThresholdFilter level="error" onMatch="ACCEPT" onMismatch="DENY" />
            <GeneralJsonStringLayout/>
            <Policies>
                <OnStartupTriggeringPolicy />
                <SizeBasedTriggeringPolicy size="250 MB" />
                <TimeBasedTriggeringPolicy />
            </Policies>
            <DefaultRolloverStrategy max="${maxRolloverStrategy}" />
        </RollingFile>
    </Appenders>

    <Loggers>
        <Root level="info">
            <AppenderRef ref="CONSOLE" />
            <AppenderRef ref="DEFAULTLOGFILE" />
        </Root>
        <logger name="serviceLogger" additivity="false" >
            <AppenderRef ref="SERVICELOGFILE" />
            <AppenderRef ref="ERRORLOGFILE" />
        </logger>
        <logger name="integrationLogger" additivity="false" >
            <AppenderRef ref="INTEGRATIONLOGFILE" />
            <AppenderRef ref="ERRORLOGFILE" />
        </logger>

        <logger name="requestLogger" additivity="false" >
            <AppenderRef ref="REQUESTLOGFILE" />
            <AppenderRef ref="ERRORLOGFILE" />
        </logger>
    </Loggers>
</configuration>
```

 ### 4.启动脚本修改
 1. 增加日志打印路径APPLOGS_DIR和应用程序名称APPLICATION系统参数
 eg:
```
-DAPPLOGS_DIR=D:/logs
-DAPPLICATION=testLog4j
```
注：具体的值根据项目修改

### 5. 项目接入skywalking  agent

注：skywalking agent 可选接入，接入后会打印traceId,serviceInstanceName,endpointName字段
1. 在项目所在服务器上上传 apache-skywalking-java-agent-8.16.0.zip

```shell
rz apache-skywalking-java-agent-8.16.0.zip
```
2. 解压zip
```shell
unzip apache-skywalking-java-agent-8.16.0.zip
```

3. 项目启动脚本修改

  * 启动脚本增加javaagent配置

```
-javaagent:/data/apache-skywalking-java-agent-8.16.0/skywalking-agent/skywalking-agent.jar=agent.service_name=your_applicationName,collector.backend_service=oap_server_url,skywalking_config=/data/apache-skywalking-java-agent-8.16.0/skywalking-agent/config/agent.config
```
your_applicationName: 根据应用程序名称进行替换
oap_server_url:根据后端oap服务地址进行替换


## log4j接入

注：此种日志框架高并发场景下不推荐，性能差

### 1. 项目引入依赖

#### 1. maven
```xml
    <dependency>
            <groupId>com.iflytek.itsc.apm</groupId>
            <artifactId>apm-toolkit-log4j-1.x</artifactId>
            <version>1.5-GENERAL</version>
    </dependency>
```
#### 2. gradle
* repositories 下增加技术中心私服仓库配置
```
 maven { url 'https://depend.iflytek.com/artifactory/stc-mvn-private-repo/' }
```
* dependencies 下增加如下依赖
```
implementation 'com.iflytek.itsc.apm:apm-toolkit-log4j-1.x:1.5-GENERAL'
```
注意:  该jar包底层依赖的是log4j-1.2.17版本,如果和项目的log4j版本冲突，需要进行依赖排除

### 2. 打印方式修改（可选，如果按照我们建议的日志文件进行分类打印，需要修改打印方式）
根据打印的日志内容调用LoggerUtil的不同logger对象打印日志
logger对象种类
SERVICE_LOG ：打印应用程序执行流程的业务日志logger对象
REQUEST_LOG：打印外部应用请求本应用的相关日志logger对象
INTEGRATION_LOG：打印本应用请求第三方应用的相关日志logger对象

```java
LoggerUtil.SERVICE_LOG.info("test");
```
### 3. 修改log4j.properties 配置文件

在需要修改的appender下把layout修改成GeneralJsonStringLayout
```xml
log4j.appender.CONSOLE.layout=com.iflytek.itsc.apm.log4j.layout.GeneralJsonStringLayout
```

注：此样例是按照我们的日志文件分类进行打印的配置文件
完整样例:
```properties
# Global logging configuration
# log4j.rootLogger = [level],appenderName1,appenderName2

log4j.rootLogger=INFO,CONSOLE,DEFAULTLOGFILE
log4j.logger.serviceLogger=INFO,SERVICELOGFILE,ERRORLOGFILE
log4j.logger.requestLogger=INFO,REQUESTLOGFILE,ERRORLOGFILE
log4j.logger.integrationLogger=INFO,INTEGRATIONLOGFILE,ERRORLOGFILE
log4j.additivity.serviceLogger=false
log4j.additivity.requestLogger=false
log4j.additivity.integrationLogger=false

log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.Target = System.out
log4j.appender.CONSOLE.Threshold = DEBUG
log4j.appender.CONSOLE.ImmediateFlush = true
log4j.appender.CONSOLE.Encoding = UTF-8
log4j.appender.CONSOLE.layout=com.iflytek.itsc.apm.log4j.layout.GeneralJsonStringLayout

## SERVICE_LOG目的地输出设置
log4j.appender.SERVICELOGFILE=org.apache.log4j.DailyRollingFileAppender
log4j.appender.SERVICELOGFILE.File=${APPLOGS_DIR}/${APPLICATION}-service.log
log4j.appender.SERVICELOGFILE.DatePattern='.'yyyy-MM-dd
log4j.appender.SERVICELOGFILE.Threshold=INFO
log4j.appender.SERVICELOGFILE.layout=com.iflytek.itsc.apm.log4j.layout.GeneralJsonStringLayout

## REQUEST_LOG目的地输出设置
log4j.appender.REQUESTLOGFILE=org.apache.log4j.DailyRollingFileAppender
log4j.appender.REQUESTLOGFILE.File=${APPLOGS_DIR}/${APPLICATION}-request.log
log4j.appender.REQUESTLOGFILE.DatePattern='.'yyyy-MM-dd
log4j.appender.REQUESTLOGFILE.Threshold=INFO
log4j.appender.REQUESTLOGFILE.layout=com.iflytek.itsc.apm.log4j.layout.GeneralJsonStringLayout

## REQUEST_LOG目的地输出设置
log4j.appender.INTEGRATIONLOGFILE=org.apache.log4j.DailyRollingFileAppender
log4j.appender.INTEGRATIONLOGFILE.File=${APPLOGS_DIR}/${APPLICATION}-integration.log
log4j.appender.INTEGRATIONLOGFILE.DatePattern='.'yyyy-MM-dd
log4j.appender.INTEGRATIONLOGFILE.Threshold=INFO
log4j.appender.INTEGRATIONLOGFILE.layout=com.iflytek.itsc.apm.log4j.layout.GeneralJsonStringLayout

## DEFAULT_LOG 目的地输出设置
log4j.appender.DEFAULTLOGFILE=org.apache.log4j.DailyRollingFileAppender
log4j.appender.DEFAULTLOGFILE.File=${APPLOGS_DIR}/${APPLICATION}-default.log
log4j.appender.DEFAULTLOGFILE.DatePattern='.'yyyy-MM-dd
log4j.appender.DEFAULTLOGFILE.Threshold=INFO
log4j.appender.DEFAULTLOGFILE.layout=com.iflytek.itsc.apm.log4j.layout.GeneralJsonStringLayout

## DEFAULT_LOG 目的地输出设置
log4j.appender.ERRORLOGFILE=org.apache.log4j.DailyRollingFileAppender
log4j.appender.ERRORLOGFILE.File=${APPLOGS_DIR}/${APPLICATION}-error.log
log4j.appender.ERRORLOGFILE.DatePattern='.'yyyy-MM-dd
log4j.appender.ERRORLOGFILE.Threshold=ERROR
log4j.appender.ERRORLOGFILE.layout=com.iflytek.itsc.apm.log4j.layout.GeneralJsonStringLayout
```

 ### 4.启动脚本修改

 1. 增加日志打印路径APPLOGS_DIR和应用程序名称APPLICATION系统参数
 eg:
```
-DAPPLOGS_DIR=D:/logs
-DAPPLICATION=testLog4j
```
注：具体的值根据项目修改
### 5. 项目接入skywalking  agent

注：skywalking agent 可选接入，接入后会打印traceId,serviceInstanceName,endpointName字段

1. 在项目所在服务器上上传 apache-skywalking-java-agent-8.16.0.zip

```shell
rz apache-skywalking-java-agent-8.16.0.zip
```
2. 解压zip
```shell
unzip apache-skywalking-java-agent-8.16.0.zip
```

3. 项目启动脚本修改

  * 启动脚本增加javaagent配置

```
-javaagent:/data/apache-skywalking-java-agent-8.16.0/skywalking-agent/skywalking-agent.jar=agent.service_name=your_applicationName,collector.backend_service=oap_server_url,skywalking_config=/data/apache-skywalking-java-agent-8.16.0/skywalking-agent/config/agent.config
```
your_applicationName: 根据应用程序名称进行替换
oap_server_url:根据后端oap服务地址进行替换

## 调试
1. 根据自己项目原有的日志框架选择合适的接入方式后，启动项目到日志打印的对应路径下看是否有正确格式的日志生成