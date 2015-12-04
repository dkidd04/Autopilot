package com.citigroup.liquifi.autopilot.bootstrap;

import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import com.citigroup.liquifi.autopilot.controller.TestCaseController;
import com.citigroup.liquifi.autopilot.bootstrap.AutoPilotBenchMarkSuite;
import com.citigroup.liquifi.autopilot.controller.ValidationObject;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.util.ClassPathLoader;
import com.citigroup.liquifi.util.DBUtil;

public class AutoPilotBootstrap {
	private static AceLogger logger = AceLogger.getLogger("AutoPilotBootstrap");

	public static void initDB() {
		logger.info("Loading Template from Database ... ");
		DBUtil.getInstance().getTem().loadAllTemplateFromDB();
		ApplicationContext.getFIXFactory().setTemplate(
				DBUtil.getInstance().getTem().getAllTemplateMap());

		logger.info("Loading AppName from Database ... ");
		DBUtil.getInstance().getTcm().loadAppNameFromDB();

		logger.info("Loading Category from Database ... ");
		DBUtil.getInstance().getTcm().loadCategoryFromDB();

		logger.info("Loading CommonOverwriteTag from Database ... ");
		DBUtil.getInstance().getCom().loadAllCommonOverwriteTagFromDB();

		logger.info("Loading Label from Database ... ");
		DBUtil.getInstance().getLbm().loadLabelFromDB();
	}

	/**
	 * Main method launching the application.
	 */
	public static void main(String[] args) {
		try {

			if (System.getProperty("mode", "Servermode").equalsIgnoreCase(
					"Guimode")) {
				logger.info("Launch AutoPilot in Guimode ...");
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
				new AutoPilotGuiStarter(args);

			} else if (System.getProperty("mode", "Servermode")
					.equalsIgnoreCase("Benchmarkmode")) {
				logger.info("Launch AutoPilot in Benchmarkmode ...");
				initSpring();
				initDB();

				String benchmarkCriteria = ApplicationContext
						.getBenchmarkConfig().getBenchmarkCriteria();
				String warmupCriteria = ApplicationContext.getBenchmarkConfig()
						.getWarmupCriteria();
				String initBookCriteria = ApplicationContext
						.getBenchmarkConfig().getInitBookCriteria();
				int messageRate = ApplicationContext.getBenchmarkConfig()
						.getMessageRate();
				int warmUpMessages = ApplicationContext.getBenchmarkConfig()
						.getWarmUpMessages();
				int warmUpPause = ApplicationContext.getBenchmarkConfig()
						.getWarmUpPause();
				int benchmarkSymbolList = ApplicationContext
						.getBenchmarkConfig().getBenchmarkSymbolList();
				int securityClass = ApplicationContext.getBenchmarkConfig()
						.getSecurityClass();

				List<String> benchmarkTestIDList = DBUtil.getInstance()
						.getTcm().getTestCaseList(benchmarkCriteria);
				List<String> warmupTestIDList = DBUtil.getInstance().getTcm()
						.getTestCaseList(warmupCriteria);
				List<String> initBookTestIDList = DBUtil.getInstance().getTcm()
						.getTestCaseList(initBookCriteria);

				String symbolPrefix = ApplicationContext.getConfig()
						.getDefaultSymbolMap().get(securityClass);

				AutoPilotBenchMarkSuite.initBookPhase(benchmarkSymbolList,
						symbolPrefix, initBookTestIDList);

				AutoPilotBenchMarkSuite.warmupPhase(benchmarkSymbolList,
						warmUpMessages, symbolPrefix, warmupTestIDList);

				Thread.sleep(warmUpPause);

				AutoPilotBenchMarkSuite.benchmarkPhase(benchmarkSymbolList,
						messageRate, symbolPrefix, benchmarkTestIDList);

				shutdownAutoPilot();
			} else {
				logger.info("Launch AutoPilot at Servermode...");

				initSpring();
				initDB();
				
				// to add a testcase list based on a query
				String strLabel = ApplicationContext.getConfig()
						.getServerModeTestCaseQuery();
				
				String strReleaseNum = ApplicationContext.getConfig()
						.getReleaseNum();
				
				strLabel = checkNullStrValue(strLabel);
				strReleaseNum = checkNullStrValue(strReleaseNum);
				
				List<String> tcIDList = null;
				
				//if only release number given 
				if((!strReleaseNum.equals("")) && (strLabel.equals(""))){
					tcIDList = DBUtil.getInstance().getTcm()
							.getTestCaseList(strReleaseNum);
					
					// if only label given 
				}else if ((strReleaseNum.equals("")) && (!strLabel.equals(""))){
					tcIDList = DBUtil.getInstance().getTcm()
							.getTestCaseListByLabel(strLabel);
										
					// both release and label given populate 2 lists and retain common elements  
				}else{
					tcIDList = DBUtil.getInstance().getTcm()
							.getTestCaseList(strReleaseNum);
					
					List<String> labelList = DBUtil.getInstance().getTcm()
							.getTestCaseListByLabel(strLabel);
					
					tcIDList.retainAll(labelList);
				}
				
				int intPassed = 0, intFailed = 0;
				List<String> failedTestcases = new ArrayList<String>();
				List<String> passedTestcases = new ArrayList<String>();
				logger.info("***************************AutoPilot ServerMode Run Started. Total TestCase:"
						+ tcIDList.size());
				logger.info("TESTRESULTLOG|AutoPilot ServerMode Run Started. ");
				logger.info("TESTRESULTLOG|Total # of TestCases: "
						+ tcIDList.size());
				logger.info("Criteria:: Label: " + strLabel+", Release No: "+strReleaseNum);
				for (String strTcID : tcIDList) {
					try {
						/*
						 * forcely turn off the debug for the server mode run
						 */
						ApplicationContext.getConfig().setDebug(false);
						ValidationObject validationObject = TestCaseController.INSTANCE
								.runPersistedTestCase(strTcID, "", false);

						/*
						 * ValidationObject voTemp =
						 * ResultedOutputController.getInstance
						 * ().getResultedOutputObj(strTcID);
						 * 
						 * String strXml =voTemp.toXmlString();
						 * logger.info(strXml);
						 * 
						 * AutoPilotValidationResultParser aParser = new
						 * AutoPilotValidationResultParser(strXml);
						 * ValidationObject voTempBack = aParser.getVObject();
						 * 
						 * voTempBack.printValidationObject();
						 */

						if (validationObject.isSuccess()) {
							intPassed++;
							passedTestcases.add(strTcID);
							logger.info("****************TestCase " + strTcID
									+ ": PASSED.");
						}

						else {
							intFailed++;
							failedTestcases.add(strTcID);
							logger.info("****************TestCase " + strTcID
									+ ": FAILED.");
						}

						Thread.sleep(1000);

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				logger.info("TESTRESULTLOG|AutoPilot ServerMode Run Finished.");
				logger.info("TESTRESULTLOG|PASSED:" + intPassed + " FAILED:"
						+ intFailed);
				logger.info("TESTRESULTLOG|List of passed test cases:  ");
				logger.info("TESTRESULTLOG|" + passedTestcases.toString());
				logger.info("TESTRESULTLOG|List of failed test cases:  ");
				logger.info("TESTRESULTLOG|" + failedTestcases.toString());

				// cleanUpAutoPilot();
				shutdownAutoPilot();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	private static String checkNullStrValue(String strValue){
		
		if (strValue == null || strValue.trim().isEmpty()){
			strValue = "";
		}
		return strValue;
	}

	public static void initSpring() throws Exception {
		ApplicationContext.init();
	}

	public static void shutdownAutoPilot() {
		logger.info("shutdownAutoPilot()");
		// to be updated
		try {
			System.exit(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void loadClassPath(String strClassPath) throws Exception {
		logger.info("loadClassPath()");
		try {
			ClassPathLoader.addToClasspath(strClassPath);
		} catch (Exception ex) {
			throw ex;
		}

	}

}
