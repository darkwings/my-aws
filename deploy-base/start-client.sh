#!/bin/sh

APP_NAME="my-aws"
APP_MAIN_CLASS="com.frank.myaws.App"
SHUTDOWN_WAIT=10

$JAVA_HOME/bin/java -cp $APP_NAME"-${project.version}.jar:conf/:lib/*" -Xms1G -Xmx2G $APP_MAIN_CLASS client ${aws.client.id} ${aws.endpoint} ./certificates/${aws.cert.pem} ./certificates/${aws.private.key} ${bcm.pin} > /dev/null &

