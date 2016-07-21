#!/bin/sh
#ident  "%W%"

DATE=`/bin/date +%m%d%y_%H.%M.%S`

echo "${DATE}: Starting up...."
if [ $# -lt 3 ]
then
        echo "Usage:    start_AutoPilot.sh region application environment (optional)TestCase_Query_String"
        echo "Sample:   start_AutoPilot.sh emea aee dev \"where Active = 'Y' AND AppName = 'LIQUIFI'\""
        echo "Exiting..."
        exit 1
fi

VERSION=$1
REGION=$2
APP=$3
ENVIRONMENT=$4
TESTS_TO_RUN=$5

if [ $# -lt 5 ]; then
        TESTS_TO_RUN="where Active = 'Y' AND AppName = 'LIQUIFI'"
        echo "defaulting the test cases to run"
fi

if [ $REGION == "emea" ]
then
        export JAVA_HOME=/xenv/java/X/1.6.0_31l64
        AUTO_HOME="/opt/liquifi/AutoPilot/$VERSION"
else
        export JAVA_HOME=/usr/java/jdk1.6.0_26
        AUTO_HOME="/opt/aee/LiqFiAuto/VERSION"
fi

export LIB_HOME="$AUTO_HOME/lib"
CONFIG_HOME="$AUTO_HOME/etc/config"

AUTO_PILOT_CLASSPATH=${LIB_HOME}:\
${CONFIG_HOME}/$APP/$REGION:\
${CONFIG_HOME}/$APP/$REGION/common:\
${CONFIG_HOME}/$APP/$REGION/common/db:\
${LIB_HOME}/*

JVM_OPTS="-Xms2000m \
-Xmx4000m \
-XX:PermSize=256m \
-XX:MaxPermSize=256m \
-XX:+UseLargePages \
-verbose:gc \
-XX:+PrintGCDateStamps \
-XX:+UseConcMarkSweepGC \
-XX:MaxGCPauseMillis=1000 \
-XX:+CMSIncrementalMode \
-XX:+PrintGCDetails"

APP_OPTS="-Dconfig.home=$CONFIG_HOME \
-Dcommon=common \
-Dmode=Servermode \
-Dregion=$REGION \
-Denv=$ENVIRONMENT \
-Dapplication=$APP \
-Djava.util.logging.config.file=$CONFIG_HOME/$APP/$REGION/common/logging.properties"

if [ $APP == "cx" ]
then
        APP_OPTS="$APP_OPTS \
-Dinstance=6 \
-Duse.xstream=true"
fi

if [ $APP == "rates" ]
then
        APP_OPTS="$APP_OPTS \
-Dinstance=3 \
-Duse.xstream=true"
fi

if [ $REGION == "emea" ]
then
	echo "$JAVA_HOME/bin/java -classpath $AUTO_PILOT_CLASSPATH $JVM_OPTS $APP_OPTS -DTestCase_QueryString=\"$TESTS_TO_RUN\" com.citigroup.liquifi.autopilot.bootstrap.AutoPilotBootstrap > $TEMPLOG"
	TEMPLOGDIR=/opt/loghome/autopilot
	TEMPLOG=$TEMPLOGDIR/AutopilotLiquifiCore*d.log
	$JAVA_HOME/bin/java -classpath $AUTO_PILOT_CLASSPATH $JVM_OPTS $APP_OPTS -DTestCase_QueryString="$TESTS_TO_RUN" com.citigroup.liquifi.autopilot.bootstrap.AutoPilotBootstrap > $TEMPLOG 2>&1
	AutopilotLiquifiCore_result=`/bin/grep "TESTRESULTLOG|PASSED" $TEMPLOG | /bin/grep -v "FAILED:0"`
	if [ -z "$AutopilotLiquifiCore_result"  ]; then
		echo "Completed AutopilotLiquifiCore" `date`
		exit 0
	else
		echo "AutopilotLiquifiCore Failed"
		cat $TEMPLOG
		exit 1
	fi	
else
	echo "$JAVA_HOME/bin/java -classpath $AUTO_PILOT_CLASSPATH $JVM_OPTS $APP_OPTS -DTestCase_QueryString=\"$TESTS_TO_RUN\" com.citigroup.liquifi.autopilot.bootstrap.AutoPilotBootstrap"
	$JAVA_HOME/bin/java -classpath $AUTO_PILOT_CLASSPATH $JVM_OPTS $APP_OPTS -DTestCase_QueryString="$TESTS_TO_RUN" com.citigroup.liquifi.autopilot.bootstrap.AutoPilotBootstrap
fi
