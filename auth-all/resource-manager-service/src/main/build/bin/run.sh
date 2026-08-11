#!/bin/sh
BASE_DIR=`cd $(dirname $0); pwd`

if [ -f "$BASE_DIR/VERSION" ]; then
  SERVER_JAR=`cat $BASE_DIR/VERSION`
else
  echo "no VERSION file found"
  exit 1
fi

GC_LOG_PATH=${BASE_DIR}/logs
JAVA_OPT="-server -Xms256m -Xmx256m -Xmn128m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${GC_LOG_PATH}/heapdump.hprof"
JAVA_OPT="${JAVA_OPT} -Xloggc:${GC_LOG_PATH}/gc.log -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=3 -XX:GCLogFileSize=1M"

pid=`ps -ef | grep ${SERVER_JAR} | grep java | grep -v grep | awk '{print $2}'`

cd ${BASE_DIR}
if [ "$1" = "start" ]; then
  if [ -z "$pid" ];then
    if [ ! -d "$GC_LOG_PATH" ];then
      mkdir -p "$GC_LOG_PATH"
    fi

    java ${JAVA_OPT} -jar ${BASE_DIR}/${SERVER_JAR} 2>&1 &
    echo " resource-service server is starting"
  else
    echo " resource-service server is running"
  fi
elif [ "$1" = "stop" ]; then
  if [ -z "$pid" ] ; then
    echo "no resource-service server running."
    exit 1;
  fi
  kill ${pid}
  echo "shut down resource-service server(${pid}) "
else
  echo "please use (sh run.sh start) or (sh run.sh stop)"
fi