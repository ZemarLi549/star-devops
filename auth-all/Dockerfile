FROM artifacts.iflytek.com/docker-repo/library/openjdk:8-jdk-alpine

MAINTAINER auth-all

ENV BASE_URL=/data/service \
    PROJECT_NAME=auth-all
    #CONFIG_NACOS_SERVER_ADDR="172.30.34.73:8846" \
    #CONFIG_NACOS_USERNAME="nacos" \
    #CONFIG_NACOS_PASSWORD="DiIegiS2qGCVe7Cw" \
    #AUTH_NACOS_SERVER_GROUP="auth-all" \
    #CONFIG_NACOS_NAMESPACE="st-observable"

# 更新包并安装时区数据包
RUN apk update && apk add --no-cache tzdata \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

# 设置工作目录
WORKDIR $BASE_URL


# 将项目的 JAR 文件复制到容器中
#COPY "target/$PROJECT_NAME.zip" .
COPY ./starter/target/$PROJECT_NAME.zip $BASE_URL/
# 解压 ZIP 文件到工作目录
RUN unzip "$BASE_URL/$PROJECT_NAME.zip" -d $BASE_URL

# 删除 ZIP 文件（可选，节省空间）
RUN rm -f "$BASE_URL/$PROJECT_NAME.zip"

# 暴露应用程序所需的端口
EXPOSE 9004


# 设置容器启动时运行的命令
ENTRYPOINT ["/bin/sh","-c","$BASE_URL/auth-all/run.sh start && tail -f /dev/null"]
