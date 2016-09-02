package com.citigroup.liquifi.autopilot.bootstrap;

import static com.citigroup.liquifi.autopilot.bootstrap.OrdType.MODACK;
import static com.citigroup.liquifi.autopilot.bootstrap.OrdType.NEW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.entities.LFLabel;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTemplate;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;
import com.citigroup.liquifi.util.DBUtil;
import com.citigroup.liquifi.util.Util;

public class AutoPilotDuplicateTestCases {
	private static AceLogger logger = AceLogger.getLogger("AutoPilotDuplicateTestCases");
	private static Map<String, LFTemplate> templateMap;
	private static String fromLabel = "(CAT)EMM Vs EMM";
	private static String toLabel = "UpscaledVsUpscaled";
	private static Map<String, String[]> inboundReplacements = new HashMap<>();
	private static Map<String, String[]> outboundReplacements = new HashMap<>();
	private static int casesLeft=0;
	private static int stepCount = 0;
	private static boolean contains = false;

	/**
	 * Main method launching the application.
	 */
	public static void main(String[] args) {
		try {
			//			addTagsToReplace();
			initDuplicationMode();

			//			duplicate();
			//			copyFromLabels();
			//			changeNumbers();
			//			removeLabel();
			swapTopics();
			//			delete();
			shutdownAutoPilot();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void delete() {
		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testcase -> {
			System.out.println("DELETING :: "+testcase.getName());
			try {
				List<LFLabel> labelsInTestcase = DBUtil.getInstance().getLbm()
						.getLabelsForTestcase(testcase.getTestID());
				for (LFLabel lb : labelsInTestcase) {
					DBUtil.getInstance().getLbm()
					.removeLabelFromTestcase(lb.getLabel(), testcase.getTestID());

					DBUtil.getInstance().getTcm().deleteTestCase(testcase);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("DELETED :: "+testcase.getName());
		});
	}

	private static void swapTopics() {
		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testcase -> {
			testcase.getInputStepList().forEach(step -> {
				if("XML".equals(step.getMsgType()) && "AutoPilotToLiqFi".equals(step.getTopicID())){
					System.out.println("Error replacing step for case "+testcase.getName());
					step.setTopicID("AutoPilotToLiqFiAdmin");
					try {
						DBUtil.getInstance().updateDB(testcase);
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("-------------------------------------");
				} else {
					System.out.println("IGNORING :: "+testcase.getName());
				}
			});
		});
	}

	private static void changeNumbers() {
		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testCase -> {
			changeName(testCase,(-432));
			try {
				DBUtil.getInstance().updateDB(testCase);
			} catch(Exception e) {
				e.printStackTrace();
			}
			casesLeft--;
			System.out.println("Remaining "+casesLeft);
		});
	}

	private static void removeLabel() {
		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testCase -> {
			System.out.println("TC = "+testCase.getName());
			Set<LFLabel> modifiedLabels = testCase.getLabelSet().stream().filter(label -> !"UpscaledVsUpscaled".equals(label.getLabel())).collect(Collectors.toSet());
			modifiedLabels.forEach(label -> {
				System.out.println("LABEL : "+label.getLabel());
				try {
					DBUtil.getInstance().getLbm().removeLabelFromTestcase(label.getLabel(), testCase.getTestID());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			casesLeft--;
			System.out.println("Saved, Remaining : "+casesLeft);
		});
	}

	private static void copyFromLabels() {
		List<String> labels = new ArrayList<>(Arrays.asList(new String[]{
				"DS_AlgoApAggVsApAgg"
				,"DS_AlgoApAggVsApPassive"
				,"DS_AlgoFlowPassiveVsFlowAgg"
				,"DS_AlgoFlowMidVsFlowAgg"
				,"DS_AlgoFlowAggVsFlowAgg"
				,"DS_AlgoApAggVsApMid"
				,"DS_AlgoFlowMidVsFlowMid"
				,"DS_AlgoFlowAggVsFlowMid"
				,"DS_AlgoFlowAggVsFlowPassive"
				,"DS_AlgoFlowPassiveVsFlowPassive"
				,"DS_AlgoFlowPassiveVsFlowMid"
				,"DS_AlgoFlowMidVsFlowPassive"
		}));

		Set<LFTestCase> testcases = getTestCases(labels.toArray(new String[labels.size()]));
		testcases.forEach(testCase -> {
			for(LFLabel label : testCase.getLabelSet()){
				if(label.getLabel().equals(toLabel)){
					contains = true;
					break;
				}
			}

			if(!contains){
				System.out.println("Adding"+testCase.getName()+" To Label ...");
				try {
					DBUtil.getInstance().getLbm().addTestCaseToLabel(toLabel,testCase.getTestID()); 
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("Added");
			} else {
				System.out.println("Ignoring"+testCase.getName()+" To Label ...");
			}
		});
	}

	private static Set<LFTestCase> getTestCases(String ... labels) {
		System.out.println("Getting TestCases");
		Set<LFTestCase> tmp = new HashSet<>();
		for(String label : labels) {
			tmp.addAll(DBUtil.getInstance().getTestCasesForLabel(label));
		}
		casesLeft = tmp.size();
		System.out.println("TotalCases = "+tmp.size());
		return tmp;
	}

	private static void addTagsToReplace() {
		inboundReplacements.put("7451", new String[]{"M","A"});
		outboundReplacements.put("31", new String[]{"100","106"});
	}

	private static void duplicate() throws Exception{
		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testCase -> {
			System.out.println("Cloning -> "+testCase.getName());
			String description = testCase.getDescription();

			LFTestCase clone = testCase.clone(Util.getTestIDSequencer());
			clone.getInputStepList().stream()
			.filter(ipStep -> !"XML".equals(ipStep.getMsgType()) && (isOfOrderType(NEW,ipStep) && isSecond()) || isOfOrderType(MODACK,ipStep)).forEach(filtered -> {
				inboundReplacements.keySet().forEach(key -> {
					replaceInboundTags(key, inboundReplacements.get(key)[1], filtered);
				});
				outboundReplacements.keySet().forEach(key -> {
					replaceOutputTags(key, outboundReplacements.get(key)[1], filtered);
				});
			});
			stepCount = 0;
			changeNameAndDescription(description, clone, 18);
			System.out.println("Cloned -> "+clone.getName());
			saveToDB(clone);
			casesLeft--;
			System.out.println("Saved -> "+clone.getName());
			System.out.println("Cases Remaining = "+casesLeft);
			System.out.println("-------------------------------------------");
		});
	}

	private static void changeNameAndDescription(String description, LFTestCase clone, int offset) {
		clone.setLastEditedUser("dr45414");
		clone.setDescription(description.replace("SELL: Upscaled Mid", "SELL: Upscaled Passive"));
		changeName(clone, offset);
	}

	private static void changeName(LFTestCase clone, int offset) {
		String[] splitName = clone.getName().split("\\|");
		int testCaseNum = Integer.parseInt(splitName[0].split("C")[1].trim())+offset;
		clone.setName("TC"+testCaseNum+"|"+splitName[1]);
	}

	private static void saveToDB(LFTestCase clone) {
		DBUtil.getInstance().saveToDB(clone);
		try {
			DBUtil.getInstance().getLbm().addTestCaseToLabel(toLabel,clone.getTestID());
		} catch (Exception e) {
			logger.info("Error saving to DB ");
		}
	}

	private static void replaceOutputTags(String tagToReplace, String newValue,
			LFTestInputSteps filtered) {
		List<LFOutputMsg> outPutMessage = filtered.getOutputStepList().stream()
				.filter(step -> 
				step.getTemplate().contains("TO_SOR") 
				|| step.getTemplate().contains("FILL_TO_BOF")
				|| step.getTemplate().contains("FILL_TO_CLIENT")
						).collect(Collectors.toList());
		outPutMessage.stream().forEach(step -> {
			List<LFOutputTag> contained = step.getOutputTagList().stream().filter(tag -> tag.getTagID().equals(tagToReplace)).collect(Collectors.toList());
			if(!contained.isEmpty()){
				contained.get(0).setTagValue(newValue);
			} else {
				LFOutputTag newTag = new LFOutputTag(tagToReplace, newValue);
				newTag.setOutputMsgID(step.getOutputMsgID());
				newTag.setActionSequence(step.getActionSequence());
				newTag.setTestID(step.getTestID());
				step.addToOutputTagList(newTag);
			}
		});
	}

	private static void replaceInboundTags(String tagToReplace, String newValue,
			LFTestInputSteps filtered) {
		List<LFTag> contained = filtered.getOverwriteTags().stream().filter(tag -> tag.getTagID().equals(tagToReplace)).collect(Collectors.toList());
		if(!contained.isEmpty()){
			replaceExistingTag(tagToReplace, newValue, filtered);
		} else {
			LFTag newTag = new LFTag(tagToReplace, newValue);
			newTag.setActionSequence(filtered.getInputTagsValueList().get(0).getActionSequence());
			filtered.addToInputTagValueList(newTag);
		}
	}

	private static boolean isSecond() {
		stepCount++;
		return stepCount == 2;
	}

	private static boolean isOfOrderType(OrdType ordType, LFTestInputSteps ipStep) {
		String compareText = "";
		switch(ordType) {
		case NEW:
			compareText = "35=D";
			break;
		case  MODACK:
			compareText = "58=Amend accepted";
			break;
		}
		if(ipStep.getTemplate() != null) {
			return templateMap.get(ipStep.getTemplate()).getMsgTemplate().contains(compareText);
		} else {
			return ipStep.getMessage().contains(compareText);
		}

	}

	private static void replaceExistingTag(String tagToReplace, String newValue,
			LFTestInputSteps filtered) {
		filtered.getOverwriteTags().stream()
		.filter(tag -> tag.getTagID().equals(tagToReplace))
		.forEach(filteredTag -> filteredTag.setTagValue(newValue));
	}

	private static void shutdownAutoPilot() {
		logger.info("shutdownAutoPilot()");
		try {
			System.exit(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void initDuplicationMode() throws Exception{
		logger.info("Launch AutoPilot at Servermode...");
		String strApplication = System.getProperty("application");
		String strRegion = System.getProperty("region");
		String strEnv = System.getProperty("env");
		String strConfighome = System.getProperty("config.home");

		System.setProperty("title", "AutoPilot_"+strApplication+"_"+strRegion+"_"+strEnv);

		String strClassPathRegion = strConfighome + "/" + strApplication+"/" + strRegion;
		String strClassPathCommon = strClassPathRegion+"/" + "common";
		String strClassPathDb = strClassPathCommon + "/" + "db";

		AutoPilotBootstrap.loadClassPath(strClassPathRegion);
		AutoPilotBootstrap.loadClassPath(strClassPathCommon);
		AutoPilotBootstrap.loadClassPath(strClassPathDb);
		AutoPilotBootstrap.initSpring();
//		AutoPilotBootstrap.initDB();
		ApplicationContext.init();
	}
}
