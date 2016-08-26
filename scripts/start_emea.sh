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

REGION=$1
APP=$2
ENVIRONMENT=$3
LABELS=$4
RELEASES=$5

export JAVA_HOME=/xenv/java/X/1.8.0_91l64
AUTO_HOME="/opt/liquifi/AutoPilot/currentVersion"

export LIB_HOME="$AUTO_HOME/lib"
CONFIG_HOME="$AUTO_HOME/etc/config"

AUTO_PILOT_CLASSPATH=${LIB_HOME}/*


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
-Djava.util.logging.config.file=$CONFIG_HOME/$APP/$REGION/common/logging.properties \
-Dcommon=common \
-Dmode=Servermode \
-DTestCase_QueryString="" \
-DRelease_Number="" \
-Dapplication=$APP \
-Dregion=$REGION \
-Denv=$ENVIRONMENT \
-DtestCaseLabels=$LABELS \
-Dreleases=$RELEASES "

echo "LIB = " $AUTO_PILOT_CLASSPATH
TEMPLOGDIR=/opt/loghome/autopilot
TEMPLOG=$TEMPLOGDIR/AutopilotLiquifiCore$DATE.log
$JAVA_HOME/bin/java -classpath $AUTO_PILOT_CLASSPATH $JVM_OPTS $APP_OPTS  com.citigroup.liquifi.autopilot.bootstrap.AutoPilotBootstrap > $TEMPLOG 2>&1
AutopilotLiquifiCore_result=`/bin/grep "TESTRESULTLOG|PASSED" $TEMPLOG | /bin/grep -v "FAILED:0"`
	if [ -z "$AutopilotLiquifiCore_result"  ]; then
		echo "Completed AutopilotLiquifiCore" `date`
		exit 0
	else
		echo "AutopilotLiquifiCore Failed"
		cat $TEMPLOG
		exit 1
	fi	

