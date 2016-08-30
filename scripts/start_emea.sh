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

AUTO_PILOT_CLASSPATH=${LIB_HOME}:\
${CONFIG_HOME}/$APP/$REGION:\
${CONFIG_HOME}/$APP/$REGION/common:\
${CONFIG_HOME}/$APP/$REGION/common/db:\
${LIB_HOME}/appframework-1.0.3.jar \
	${LIB_HOME}/asm-attrs.jar \
	${LIB_HOME}/asm.jar \
	${LIB_HOME}/c3p0-0.9.1.jar \
	${LIB_HOME}/ccUtil.jar \
	${LIB_HOME}/cglib-2.1.3.jar \
	${LIB_HOME}/checkstyle-all.jar \
	${LIB_HOME}/cleanimports.jar \
	${LIB_HOME}/commons-collections-3.1.jar \
	${LIB_HOME}/concurrent-1.3.2.jar \
	${LIB_HOME}/connector.jar \
	${LIB_HOME}/dom4j-1.6.1.jar \
	${LIB_HOME}/ehcache-1.2.3.jar \
	${LIB_HOME}/hibernate3.jar \
	${LIB_HOME}/jaas.jar \
	${LIB_HOME}/jacc-1_0-fr.jar \
	${LIB_HOME}/javassist-3.9.0.GA.jar \
	${LIB_HOME}/jaxen-1.1-beta-7.jar \
	${LIB_HOME}/jboss-cache.jar \
	${LIB_HOME}/jboss-common.jar \
	${LIB_HOME}/jboss-jmx.jar \
	${LIB_HOME}/jboss-system.jar \
	${LIB_HOME}/jconn3.jar \
	${LIB_HOME}/jdbc2_0-stdext.jar \
	${LIB_HOME}/jdom.jar \
	${LIB_HOME}/jgroups-2.2.8.jar \
	${LIB_HOME}/jms-1.1.jar \
	${LIB_HOME}/jms.jar \
	${LIB_HOME}/jnlp.jar \
	${LIB_HOME}/jta-1.1.jar \
	${LIB_HOME}/log4j-1.2.15.jar \
	${LIB_HOME}/mail.jar \
	${LIB_HOME}/OESIntf.jar \
	${LIB_HOME}/oscache-2.1.jar \
	${LIB_HOME}/proxool-0.8.3.jar \
	${LIB_HOME}/quantum.jar \
	${LIB_HOME}/ref.jar \
	${LIB_HOME}/slf4j-api-1.5.8.jar \
	${LIB_HOME}/slf4j-jdk14-1.5.8.jar \
	${LIB_HOME}/swarmcache-1.0rc2.jar \
	${LIB_HOME}/swing-layout-1.0.3.jar \
	${LIB_HOME}/swing-worker-1.1.jar \
	${LIB_HOME}/syndiag2.jar \
	${LIB_HOME}/tibjms.jar \
	${LIB_HOME}/tibjmsadmin.jar \
	${LIB_HOME}/versioncheck.jar \
	${LIB_HOME}/xerces-2.6.2.jar \
	${LIB_HOME}/xml-apis.jar \
	${LIB_HOME}/sqljdbc4.jar \
	${LIB_HOME}/commons-lang3-3.0.1.jar \
	${LIB_HOME}/junit-4.4.jar \
	${LIB_HOME}/gentlyweb-utils-1.5.jar \
	${LIB_HOME}/LiqFiSmtRouter.jar \
	${LIB_HOME}/org.apache.servicemix.bundles.josql-1.5_1.jar \
	${LIB_HOME}/sonic_Client.jar \
	${LIB_HOME}/sonic_Crypto.jar \
	${LIB_HOME}/quantumlogger.jar \
	${LIB_HOME}/spring-aop.jar \
	${LIB_HOME}/spring-asm.jar \
	${LIB_HOME}/spring-beans.jar \
	${LIB_HOME}/spring-context-support.jar \
	${LIB_HOME}/spring-context.jar \
	${LIB_HOME}/spring-core.jar \
	${LIB_HOME}/spring-expression.jar \
	${LIB_HOME}/spring-jdbc.jar \
	${LIB_HOME}/spring-orm.jar \
	${LIB_HOME}/spring-tx.jar \
	${LIB_HOME}/spring-web-portlet.jar \
	${LIB_HOME}/spring-web-servlet.jar \
	${LIB_HOME}/spring-web-struts.jar \
	${LIB_HOME}/spring-web.jar \
	${LIB_HOME}/tibrvj.jar \
	${LIB_HOME}/tibrvnativesd.jar \
	${LIB_HOME}/EccpressoFIPS.jar \
	${LIB_HOME}/EccpressoFIPSJca.jar \
	${LIB_HOME}/data_api-5.2_V0.jar \
	${LIB_HOME}/productAPIj-5.2_V0.jar \
	${LIB_HOME}/qpsCompatibleAPI.jar \
	${LIB_HOME}/nakedobjects_gui-1.2_C0.jar \
	${LIB_HOME}/applib-4.0.0-4.0.0.jar \
	${LIB_HOME}/xstream-1.4.2-1.4.2.jar \
	${LIB_HOME}/xpp3_min-1.1.4c-1.4.2.jar \
	${LIB_HOME}/xmlpull-1.1.3.1-1.4.2.jar \
	${LIB_HOME}/dna-3.2_C0.jar \
	${LIB_HOME}/commons-pool-1.5.5.jar "


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

TEMPLOGDIR=/opt/loghome/autopilot
TEMPLOG=$TEMPLOGDIR/AutopilotLiquifiCore$DATE.log

echo "Launching ..."
$JAVA_HOME/bin/java -classpath $AUTO_PILOT_CLASSPATH $JVM_OPTS $APP_OPTS com.citigroup.liquifi.autopilot.bootstrap.AutoPilotBootstrap > $TEMPLOG 2>&1
echo "Ran collecting results"

AutopilotLiquifiCore_result=`/bin/grep "TESTRESULTLOG|PASSED" $TEMPLOG | /bin/grep -v "FAILED:0"`
	if [ -z "$AutopilotLiquifiCore_result"  ]; then
		echo "Completed AutopilotLiquifiCore" `date`
		exit 0
	else
		echo "AutopilotLiquifiCore Failed"
		cat $TEMPLOG
		exit 1
	fi	

