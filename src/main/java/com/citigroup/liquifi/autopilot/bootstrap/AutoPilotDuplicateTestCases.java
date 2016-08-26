package com.citigroup.liquifi.autopilot.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.citigroup.liquifi.autopilot.logger.AceLogger;
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
	private static int count;
	private static String fromLabel = "Darren23";
	private static String toLabel = "Darren22";
	private static Map<String, String[]> inboundReplacements = new HashMap<>();
	private static Map<String, String[]> outboundReplacements = new HashMap<>();
	private static int casesLeft=0;
	/**
	 * Main method launching the application.
	 */
	public static void main(String[] args) {
		try {
			addTagsToReplace();
			initDuplicationMode();
			duplicate(fromLabel);
			shutdownAutoPilot();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void addTagsToReplace() {
		inboundReplacements.put("7451", new String[]{"M","A"});
		outboundReplacements.put("31", new String[]{"100","106"});
	}

	private static void duplicate(String label) throws Exception{

		Set<LFTestCase> testcases = DBUtil.getInstance().getTestCasesForLabel(label);


		System.out.println("TotalCases = "+testcases.size());
		casesLeft = testcases.size();
		testcases.forEach(testCase -> {
//			if(testCase.getName().contains("TC397")){
				System.out.println("Cloning -> "+testCase.getName());
				String description = testCase.getDescription();

				LFTestCase clone = testCase.clone(Util.getTestIDSequencer());
				clone.getInputStepList().stream()
				.filter(ipStep -> !"XML".equals(ipStep.getMsgType()) && (isNewOrder(ipStep) && isSecond()) || isModAck(ipStep)).forEach(filtered -> {
					inboundReplacements.keySet().forEach(key -> {
						replaceInboundTags(key, inboundReplacements.get(key)[1], filtered);
					});
					outboundReplacements.keySet().forEach(key -> {
						replaceOutputTags(key, outboundReplacements.get(key)[1], filtered);
					});
				});
				count=0;
				changeNameAndDescription(description, clone);
				System.out.println("Cloned -> "+clone.getName());
				saveToDB(clone);
				casesLeft--;
				System.out.println("Saved -> "+clone.getName());
				System.out.println("Cases Remaining = "+casesLeft);
				System.out.println("-------------------------------------------");
//			}
		});
	}

	private static void changeNameAndDescription(String description, LFTestCase clone) {
		clone.setLastEditedUser("dr45414");
		clone.setDescription(description.replace("SELL: Upscaled Mid", "SELL: Upscaled Passive"));
		String[] splitName = clone.getName().split("\\|");
		int testCaseNum = Integer.parseInt(splitName[0].split("C")[1].trim())-18;
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
//				|| step.getTemplate().contains("TO_MKT")
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
		count++;
		return count == 2;
	}

	private static boolean isNewOrder(LFTestInputSteps ipStep) {
		if(ipStep.getTemplate() != null && ipStep.getMessage().length() != 0) {
			return templateMap.get(ipStep.getTemplate()).getMsgTemplate().contains("35=D");
		} else {
			return ipStep.getMessage().contains("35=D");
		}
	}

	private static boolean isModAck(LFTestInputSteps ipStep) {
		if(ipStep.getTemplate() != null) {
			return templateMap.get(ipStep.getTemplate()).getMsgTemplate().contains("58=Amend accepted");
		} else {
			return ipStep.getMessage().contains("58=Amend accepted");
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
		ApplicationContext.init();
		templateMap = DBUtil.getInstance().getTem().getAllTemplateMap();

	}
}
