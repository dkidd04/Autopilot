package com.citigroup.liquifi.autopilot.bootstrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.UIManager;

import com.citigroup.liquifi.autopilot.controller.TestCaseController;
import com.citigroup.liquifi.autopilot.controller.ValidationObject;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.util.ClassPathLoader;
import com.citigroup.liquifi.util.DBUtil;

class AutoPilotBootstrap {
	private final static AceLogger logger = AceLogger.getLogger("AutoPilotBootstrap");

	static void initDB() {
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
			String mode = System.getProperty("mode", "Servermode").toLowerCase();
			logger.info("Launch AutoPilot in "+mode+" ...");
			switch(mode){
			case "guimode":
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				new AutoPilotGuiStarter(args);
				break;
			case "benchmarkmode":
				launchBenchmarkMode();
				break;
			default:
				launchServerMode();
				break;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void launchServerMode() throws Exception {
		appInit();
		List<String> labels = new ArrayList<>();
		List<String> releases = new ArrayList<>();

		if(null != System.getProperty("testCaseLabels") && 0 != System.getProperty("testCaseLabels").trim().length()){
			logger.info("Label String : "+System.getProperty("testCaseLabels"));
			labels = Arrays.asList(System.getProperty("testCaseLabels").split(","));
		}

		if(null != System.getProperty("releases") && 0 != System.getProperty("releases").trim().length()){
			logger.info("Releases String : "+System.getProperty("releases"));
			releases = Arrays.asList(System.getProperty("releases").split(","));
		}

		List<String> tcIDList = new ArrayList<>();


		if((!releases.isEmpty()) && (labels.isEmpty())){
			//if only release number given 
			releases.forEach(addReleaseToList(tcIDList));
		}else if ((releases.isEmpty()) && (!labels.isEmpty())){
			// if only label given 
			labels.forEach(addLabelToList(tcIDList));
		}else{
			// both release and label given populate 2 lists and retain common elements  
			releases.forEach(addReleaseToList(tcIDList));
			List<String> labelList = new ArrayList<>();
			labels.forEach(label -> labelList.addAll(DBUtil.getInstance().getTcm().getTestCaseListByLabel(label)));

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
		logger.info("Criteria:: Labels: " + labels.toString()+", Releases: "+releases.toString());
		for (String strTcID : tcIDList) {
			try {
				/*
				 * forcely turn off the debug for the server mode run
				 */
				ApplicationContext.getConfig().setDebug(false);
				ValidationObject validationObject = TestCaseController.INSTANCE.runPersistedTestCase(strTcID, "", false);

				if (validationObject.isSuccess()) {
					intPassed++;
					passedTestcases.add(strTcID);
					logger.info("****************TestCase " + strTcID+ ": PASSED.");
				}else {
					intFailed++;
					failedTestcases.add(strTcID);
					logger.info("****************TestCase " + strTcID+ ": FAILED.");
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

	private static Consumer<? super String> addLabelToList(List<String> tcIDList) {
		return label -> tcIDList.addAll(DBUtil.getInstance().getTcm().getTestCaseListByLabel(label));
	}

	private static Consumer<? super String> addReleaseToList(List<String> tcIDList) {
		return release -> tcIDList.addAll(DBUtil.getInstance().getTcm().getTestCaseList(release));
	}

	private static void launchBenchmarkMode() throws Exception, InterruptedException {
		appInit();

		String benchmarkCriteria = ApplicationContext.getBenchmarkConfig().getBenchmarkCriteria();
		String warmupCriteria = ApplicationContext.getBenchmarkConfig().getWarmupCriteria();
		String initBookCriteria = ApplicationContext.getBenchmarkConfig().getInitBookCriteria();
		int messageRate = ApplicationContext.getBenchmarkConfig().getMessageRate();
		int warmUpMessages = ApplicationContext.getBenchmarkConfig().getWarmUpMessages();
		int warmUpPause = ApplicationContext.getBenchmarkConfig().getWarmUpPause();
		int benchmarkSymbolList = ApplicationContext.getBenchmarkConfig().getBenchmarkSymbolList();
		int securityClass = ApplicationContext.getBenchmarkConfig().getSecurityClass();

		List<String> benchmarkTestIDList = DBUtil.getInstance().getTcm().getTestCaseList(benchmarkCriteria);
		List<String> warmupTestIDList = DBUtil.getInstance().getTcm().getTestCaseList(warmupCriteria);
		List<String> initBookTestIDList = DBUtil.getInstance().getTcm().getTestCaseList(initBookCriteria);

		String symbolPrefix = ApplicationContext.getConfig().getDefaultSymbolMap().get(securityClass);

		AutoPilotBenchMarkSuite.initBookPhase(benchmarkSymbolList,symbolPrefix, initBookTestIDList);

		AutoPilotBenchMarkSuite.warmupPhase(benchmarkSymbolList,warmUpMessages, symbolPrefix, warmupTestIDList);

		Thread.sleep(warmUpPause);

		AutoPilotBenchMarkSuite.benchmarkPhase(benchmarkSymbolList,messageRate, symbolPrefix, benchmarkTestIDList);

		shutdownAutoPilot();
	}

	/**
	 * This Is needed to load the app when not launched from the GUI mode to:
	 * 1. Load ClassPatch
	 * 2. Initialise Spring
	 * 3. InitDB Connection
	 * @throws Exception
	 */
	protected static void appInit() throws Exception {
		loadClasspath();
		initSpring();
		initDB();
	}

	private static void loadClasspath() throws Exception {
		String strConfighome =  System.getProperty("config.home");
		String strCommon =  System.getProperty("common");

		String strClassPathRegion = strConfighome + "/" + System.getProperty("application")+"/" + System.getProperty("region");
		String strClassPathCommon = strClassPathRegion+"/" + strCommon;
		String strClassPathDb = strClassPathCommon + "/" + "db";
		loadClassPath(strClassPathRegion);
		loadClassPath(strClassPathCommon);
		loadClassPath(strClassPathDb);
	}

	static void initSpring() throws Exception {
		ApplicationContext.init();
	}

	protected static void shutdownAutoPilot() {
		logger.info("shutdownAutoPilot()");
		// to be updated
		try {
			System.exit(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	static void loadClassPath(String strClassPath) throws Exception {
		logger.info("loadClassPath()");
		try {
			ClassPathLoader.addToClasspath(strClassPath);
		} catch (Exception ex) {
			throw ex;
		}

	}

}
