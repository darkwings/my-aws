#!/bin/sh

APP_NAME="my-aws"
APP_MAIN_CLASS="com.frank.myaws.App"
SHUTDOWN_WAIT=10

$JAVA_HOME/bin/java -cp $APP_NAME"-${project.version}.jar:conf/:lib/*" ${jvm.xms} ${jvm.xmx} $APP_MAIN_CLASS listener ${aws.client.id} ${aws.endpoint} ./certificates/${aws.cert.pem} ./certificates/${aws.private.key} ${gpio.pin} > /dev/null &

