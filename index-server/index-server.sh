#!/bin/bash

APP_NAME=index-server
APP_VERSION=0.0.1-SNAPSHOT
APP_JAR=$APP_NAME-$APP_VERSION.jar
APP_PROFILE=test

#JVM参数
JVM_OPTS="-Dname=$APP_JAR  -Duser.timezone=Asia/Shanghai -Xms512M -Xmx512M -XX:PermSize=256M -XX:MaxPermSize=512M -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDateStamps  -XX:+PrintGCDetails -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC -XX:+UseParallelOldGC"
APP_HOME=`pwd`

if [ "$2" != "" ];
then
    APP_JAR=$2
fi

if [ "$3" != "" ];
then
    APP_PROFILE=$3
fi

if [ "$APP_JAR" = "" ];
then
    echo -e "\033[0;31m 未输入应用名 \033[0m"
    exit 1
fi

function start()
{
    PID=""
	query(){
		PID=`jps -l |grep  $APP_HOME/$APP_JAR|awk '{print $1}'`
	}
    query
	if [ x"$PID" != x"" ]; then
	    echo "$APP_HOME/$APP_JAR (pid:$PID) is running..."
	else
	    nohup java -jar  $JVM_OPTS $APP_HOME/$APP_JAR --spring.profiles.active=$APP_PROFILE >/dev/null 2>&1 &
	    echo "Start $APP_HOME/$APP_JAR success..."
	    sleep 2s
	    query
	    if [ x"$PID" != x"" ]; then
	        echo "Start $APP_HOME/$APP_JAR (pid:$PID) succeeded !"
        else
            echo "Start $APP_HOME/$APP_JAR failed !"
	    fi
	fi
}

function startnew()
{
    PID=""
	query(){
		PID=`jps -l |grep  $APP_HOME/$APP_JAR|awk '{print $1}'`
	}
    query
	if [ x"$PID" != x"" ]; then
	    echo "$APP_HOME/$APP_JAR is running..."
	else
	    mv -f $APP_JAR.new $APP_JAR #文件名称更改
	    nohup java -jar  $JVM_OPTS $APP_HOME/$APP_JAR --spring.profiles.active=$APP_PROFILE >/dev/null 2>&1 &
	    echo "Start $APP_HOME/$APP_JAR success..."
	    query
	    if [ x"$PID" != x"" ]; then
	        echo "Start $APP_HOME/$APP_JAR failed !"
        else
            echo "Start $APP_HOME/$APP_JAR (pid:$PID) succeeded !"
	    fi
	fi
}

function stop()
{
    echo "Stop $APP_HOME/$APP_JAR"

	PID=""
	query(){
		PID=`jps -l |grep  $APP_HOME/$APP_JAR|awk '{print $1}'`
	}

	query
	if [ x"$PID" != x"" ]; then
		kill -TERM $PID
		echo "$APP_HOME/$APP_JAR (pid:$PID) exiting..."
		while [ x"$PID" != x"" ]
		do
			sleep 1
			query
		done
		echo "$APP_HOME/$APP_JAR exited."
	else
		echo "$APP_HOME/$APP_JAR already stopped."
	fi
}

function restart()
{
    stop
    sleep 2
    start
}

function status()
{
    PID=`jps -l |grep  $APP_HOME/$APP_JAR|awk '{print $1}'`
    if [ $PID != 0 ];then
        echo "$APP_HOME/$APP_JAR (pid:$PID) is running..."
    else
        echo "$APP_HOME/$APP_JAR is not running..."
    fi
}

function deploy()
{
    scp $APP_JAR root@120.79.243.95:$APP_NAME/$APP_JAR.new
    echo "Deploy $APP_HOME/$APP_JAR succeeded !"
}

if [ "$1" = "" ];
then
#    echo -e "\033[0;31m 未输入操作名 \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
    echo "execute default operate: deploy"
    deploy
fi

case $1 in
    start)
    start;;
    startnew)
    startnew;;
    stop)
    stop;;
    restart)
    restart;;
    status)
    status;;
    deploy)
    deploy;;
    *)

esac
