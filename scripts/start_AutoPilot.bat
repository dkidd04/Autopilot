rem set region and app specific values
set APP=aee
set REGION=emea

set CLASSPATH=..\src\;..\etc\config\%APP%\%REGION%\;..\etc\config\%APP%\%REGION%\common\;..\etc\config\%APP%\%REGION%\common\db\;..\lib\*;

java -Djava.util.logging.config.file=..\etc\config\%APP%\%REGION%\common\logging.properties -Dconfig.home=..\etc\config\ -Dcommon=common -Dmode=GuiMode -Dsun.java2d.noddraw com.citigroup.liquifi.autopilot.bootstrap.AutoPilotBootstrap





