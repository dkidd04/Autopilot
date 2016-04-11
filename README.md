#AutoPilot
##More About AutoPilot
AutoPilot is a testing framework for the Crossing stack.

##Running AutoPilot
To run Autopilot on your local machine download the latest develop branch from BitBucket on the [AutoPilot project](https://cedt-icg-bitbucket.nam.nsroot.net/bitbucket/projects/CROS/repos/autopilot/browse)



###Create a Run configuration in Eclipse with the following details
####Main Class
`com.citigroup.liquifi.autopilot.bootstrap.AutoPilotBootstrap`

####VM Arguments
`-Dcommon=common`
`-Dregion=emea`
`-Dapplication=aee`
`-Dmode=GuiMode`
`-Dsun.java2d.noddraw=true`
`-Dconfig.home=src/main/resources/config`
`-DTestCase_QueryString="where ACTIVE='true'"`

####Classpath
![Classpath](https://teamforge.nam.nsroot.net/sf/wiki/do/viewAttachment/projects.163527_globalcrossing/wiki/EMEAAutopilotTestCases/Autopilot_Liquifi_TotalTouch_1_9.JPG)

##Creating TestCases

##Running TestCases

##Editing TestCases