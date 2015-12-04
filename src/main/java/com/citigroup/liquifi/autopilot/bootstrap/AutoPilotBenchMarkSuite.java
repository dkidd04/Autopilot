package com.citigroup.liquifi.autopilot.bootstrap;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.citigroup.liquifi.autopilot.controller.TestCaseController;
import com.citigroup.liquifi.autopilot.controller.ValidationObject;
import com.citigroup.liquifi.autopilot.logger.AceLogger;

import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;

import com.citigroup.liquifi.util.DBUtil;


public class AutoPilotBenchMarkSuite  {
	private static AceLogger logger = AceLogger.getLogger("AutoPilotBenchMarkSuite");
		
	public static void initBookPhase(int benchmarkSymbolList, String symbolPrefix,List<String> initBookTestIDList ){
		logger.info("starting initbook phase");
		for(int initBookiter = 1 ; initBookiter < benchmarkSymbolList + 1 ; initBookiter = initBookiter + 1 ){
			String initBookSymbol = symbolPrefix + Integer.toString(initBookiter);
			for (String initBookStrTcID : initBookTestIDList) {
				ValidationObject validationObject = TestCaseController.INSTANCE.runPersistedTestCase(initBookStrTcID,initBookSymbol, true);
			}
		}
	}
	
	public static void warmupPhase(int benchmarkSymbolList, int warmUpMessages, String symbolPrefix, List<String> warmupTestIDList ){
		logger.info("starting warmup phase");
		List<Integer> warmUpEachSymbolIteration = new ArrayList<Integer>();
		// sep function from config.
		for (int i = 1 ; i <= benchmarkSymbolList; i++){
			int iter = warmUpMessages/benchmarkSymbolList;
			warmUpEachSymbolIteration.add(iter);
		}
		/////////////////
		int warmUpBuckets = benchmarkSymbolList;

		while(warmUpBuckets > 0 )
		{	
			int warmUpIterationRuns = warmUpEachSymbolIteration.get(warmUpBuckets - 1);
			String warmUpBucketSymbol = symbolPrefix + Integer.toString(warmUpBuckets);
			while (  warmUpIterationRuns > 0 ){
				for (String warmupStrTcID : warmupTestIDList) {
					ApplicationContext.getConfig().setDebug(false);
					ValidationObject validationObject = TestCaseController.INSTANCE.runPersistedTestCase(warmupStrTcID,warmUpBucketSymbol, true);
				}
				warmUpIterationRuns -- ;
			}
			warmUpBuckets --;

		}
		logger.info("warmup phase finished");
	}
	
	public static void benchmarkPhase(int benchmarkSymbolList, int messageRate, String symbolPrefix, List<String> benchmarkTestIDList){
		logger.info("starting benchmark phase");
		int totalBenchmarkMessages = 0;
		int intPassed = 0, intFailed = 0;
		List<String> failedTestcases = new ArrayList<String>();
		List<LFTestCase> benchmarkLFTestCaseList = new ArrayList<LFTestCase>();
		for (String strTcID : benchmarkTestIDList) {
			totalBenchmarkMessages = totalBenchmarkMessages + DBUtil.getInstance().getInputStepSize("testID",strTcID);
			
			LFTestCase tcLocal = DBUtil.getInstance().getTcm().getTestcaseMap().get(strTcID);
			if (tcLocal == null) {
				//logger.info("tclocal from benchmark is null");
				tcLocal = DBUtil.getInstance().getTestCase(strTcID);
			}
			benchmarkLFTestCaseList.add(tcLocal);
			loadBenchmarkConnections(tcLocal);
			
			
		}
		logger.info("totalBenchmarkMessages =  " + totalBenchmarkMessages);
		int benchmarkRun = messageRate/totalBenchmarkMessages;
		int benchmarkPause = 1000/benchmarkRun;  // mililsecond run
		logger.info("benchmarkPause =  " + benchmarkPause);

		Random randomSymbolSuffixGenerator = new Random();	
		List<String> randomSymbolList = new ArrayList<String>();
		for(int j = 0 ; j < messageRate ; j++){
			randomSymbolList.add(symbolPrefix + (randomSymbolSuffixGenerator.nextInt(benchmarkSymbolList) + 1));
		}
		
		int indexOfRandomSymbolList = 0;
		logger.info("all init done.Now running benchmark phase");
		while( benchmarkRun != 0 ){
			for (LFTestCase benchmarkTestToRun : benchmarkLFTestCaseList) {
				try {
					String symbolForBenchmarkRun = randomSymbolList.get(indexOfRandomSymbolList);
					
					/*
					 * forcely turn off the debug for the server mode run
					 */
					ApplicationContext.getConfig().setDebug(false);
					logger.info("validation started ----");
					ValidationObject validationObject = TestCaseController.INSTANCE.runTestCase(benchmarkTestToRun, symbolForBenchmarkRun, false,true);
					logger.info("validation ended ----");
					
					if (validationObject.isSuccess()) {
						intPassed ++;
						logger.info("****************TestCase " +benchmarkTestToRun.getTestID() + ": PASSED.");
					}

					else {
						intFailed ++;
						failedTestcases.add(benchmarkTestToRun.getTestID());
						logger.info("****************TestCase " +benchmarkTestToRun.getTestID() + ": FAILED.");
					}


				} catch (Exception ex) {
					ex.printStackTrace();
				}
				indexOfRandomSymbolList++;
			}
			benchmarkRun = benchmarkRun - 1;
			logger.info("TESTRESULTLOG|AutoPilot BenchmarkMode Run number " + benchmarkRun +" Finished.");
			logger.info("TESTRESULTLOG|PASSED:" + intPassed + " FAILED:" + intFailed);
			logger.info("TESTRESULTLOG|List of failed test cases:  ");
			logger.info("TESTRESULTLOG|" + failedTestcases.toString());

			try {
				logger.info("benchmark sleeping");
				Thread.sleep(benchmarkPause);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}

	}
	public static void loadBenchmarkConnections(LFTestCase testcase){
		if (ApplicationContext.getSocketFactory() != null) {
			for (LFTestInputSteps inputStep : testcase.getInputStepList()) {
				for (LFOutputMsg outputStep : inputStep.getOutputStepList()) {
					if (ApplicationContext.getSocketFactory().isSocket(outputStep.getTopicID())) {
						logger.info("setting up benchmark output socket -  " + outputStep.getTopicID() );
						try {
							ApplicationContext.getSocketFactory().setupSocket(outputStep.getTopicID());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				logger.info("setting up benchmark input socket -  " + inputStep.getTopicID() );
				try {
					ApplicationContext.getSocketFactory().setupSocket(inputStep.getTopicID());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
