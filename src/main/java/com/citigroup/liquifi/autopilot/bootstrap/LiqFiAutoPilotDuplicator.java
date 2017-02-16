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

import com.citigroup.liquifi.entities.LFLabel;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;
import com.citigroup.liquifi.util.DBUtil;
import com.citigroup.liquifi.util.Util;
import com.sonicsw.blackbird.sys.SysObject;

public class LiqFiAutoPilotDuplicator extends AutoPilotBootstrap{
	private static String testCaseNameSeperator = "_";
	private static String fromLabel = "(CAT)WholeSaleVsInstitution";
	private static String toLabel = "(CAT)WholeSaleVsProp";
	private static Map<String, String> inboundReplacements = new HashMap<>();
	private static Map<String, String> outboundReplacements = new HashMap<>();
	private static int casesLeft=0;
	private static int stepCount = 0;
	private static boolean contains = false;
	private static String descriptFrom = "Institution";
	private static String descriptionTo = "Proprietary";
	private static String labelFilter = "CAT";

	/**
	 * Main method launching the application.
	 */
	public static void main(String[] args) {
		logger.info("Launch AutoPilot at Duplication...");
		System.setProperty("Mode", "massaltername");

		addTagsToReplace();
		try {
			AutoPilotBootstrap.appInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		chooseMode();
		shutdownAutoPilot();
	}

	protected static void chooseMode() {
		switch(System.getProperty("Mode","null").toLowerCase()){
		case "duplicate":
			duplicate();
			break;
		case "copylabels":
			copyFromLabels();
			break;
		case "changeNumbers":
			changeNumbers();
			break;
		case "removelabel":
			removeLabel();
			break;
		case "swaptopics":
			swapToAdminTopics();
			break;
		case "massaltername":
			massAlterName();
			break;
		default: 
			getTestCases(fromLabel);
			break;
		}
	}

	private static void massAlterName() {
		List<String> labels = getFilteredTestCases();
		labels.forEach(label -> logger.info(label));
		Set<LFTestCase> testcases = getTestCases(labels.toArray(new String[labels.size()]));
		testcases.forEach(testCase -> {
			logger.info("BEFORE -> "+testCase.getName());
			changeName(testCase,0);
			logger.info("AFTER -> "+testCase.getName());
			try {
				DBUtil.getInstance().updateDB(testCase);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static List<String> getFilteredTestCases() {
		logger.info("Getting Labels");
		return DBUtil.getInstance().getLbm().getLabels().stream()
				.filter(label -> label.getLabel().contains(labelFilter)).map(filtered -> filtered.getLabel()).collect(Collectors.toList());
	}

	private static void addTagsToReplace() {
		inboundReplacements.put("11042","5");
	}

	private static void swapToAdminTopics() {
		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testcase -> {
			testcase.getInputStepList().forEach(step -> {
				if("XML".equals(step.getMsgType()) && "AutoPilotToLiqFi".equals(step.getTopicID())){
					logger.info("Error replacing step for case "+testcase.getName());
					step.setTopicID("AutoPilotToLiqFiAdmin");
					try {
						DBUtil.getInstance().updateDB(testcase);
					} catch (Exception e) {
						e.printStackTrace();
					}
					logger.info("-------------------------------------");
				} 
			});
			casesLeft--;
			logger.info("Remaining :: "+casesLeft);
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
			logger.info("Remaining "+casesLeft);
		});
	}

	private static void removeLabel() {
		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testCase -> {
			logger.info("TC = "+testCase.getName());
			Set<LFLabel> modifiedLabels = testCase.getLabelSet().stream().filter(label -> !"UpscaledVsUpscaled".equals(label.getLabel())).collect(Collectors.toSet());
			modifiedLabels.forEach(label -> {
				logger.info("LABEL : "+label.getLabel());
				try {
					DBUtil.getInstance().getLbm().removeLabelFromTestcase(label.getLabel(), testCase.getTestID());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			casesLeft--;
			logger.info("Saved, Remaining : "+casesLeft);
		});
	}

	private static void copyFromLabels() {
		List<String> labels = new ArrayList<>(Arrays.asList(fromLabel.split(",")));

		Set<LFTestCase> testcases = getTestCases(labels.toArray(new String[labels.size()]));
		testcases.forEach(testCase -> {
			for(LFLabel label : testCase.getLabelSet()){
				if(label.getLabel().equals(toLabel)){
					contains = true;
					break;
				}
			}

			if(!contains){
				logger.info("Adding"+testCase.getName()+" To Label ...");
				try {
					DBUtil.getInstance().getLbm().addTestCaseToLabel(toLabel,testCase.getTestID()); 
				} catch (Exception e) {
					e.printStackTrace();
				}
				logger.info("Added");
			} else {
				logger.info("Ignoring"+testCase.getName()+" To Label ...");
			}
		});
	}

	private static Set<LFTestCase> getTestCases(String ... labels) {
		logger.info("Getting TestCases");
		Set<LFTestCase> tmp = new HashSet<>();
		for(String label : labels) {
			tmp.addAll(DBUtil.getInstance().getTestCasesForLabel(label));
		}
		casesLeft = tmp.size();
		logger.info("TotalCases = "+tmp.size());
		return tmp;
	}

	private static void duplicate() {
		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testCase -> {
			logger.info("Cloning -> "+testCase.getName());
			String description = testCase.getDescription();

			LFTestCase clone = testCase.clone(Util.getTestIDSequencer(true));
			clone.getInputStepList().stream()
			.filter(ipStep -> !"XML".equals(ipStep.getMsgType()) && (isOfOrderType(NEW,ipStep) && isSecond()) || isOfOrderType(MODACK,ipStep)).forEach(filtered -> {
				inboundReplacements.keySet().forEach(key -> {
					replaceInboundTags(key, inboundReplacements.get(key), filtered);
				});
				outboundReplacements.keySet().forEach(key -> {
					replaceOutputTags(key, outboundReplacements.get(key), filtered);
				});
			});
			stepCount = 0;
			changeNameAndDescription(description, clone, 18);
			logger.info("Cloned -> "+clone.getName());
			saveToDB(clone);
			casesLeft--;
			logger.info("Saved -> "+clone.getName());
			logger.info("Cases Remaining = "+casesLeft);
			logger.info("-------------------------------------------");
		});
	}

	private static void changeNameAndDescription(String description, LFTestCase clone, int offset) {
		clone.setLastEditedUser("dr45414");
		clone.setDescription(description.replace(descriptFrom, descriptionTo));
		changeName(clone, offset);
	}

	private static void changeName(LFTestCase clone, int offset) {
		String[] splitName = clone.getName().split(testCaseNameSeperator);
		int testCaseNum = Integer.parseInt(splitName[0].split("K")[1].trim())+offset;
		clone.setName("TC"+testCaseNum+"|"+splitName[1]);
		clone.setName(clone.getName().replace("VsInst", "VsProp"));
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
			return ipStep.getTemplate().contains(compareText);
		} else {
			return ipStep.getMessage().contains(compareText);
		}

	}

	private static void replaceExistingTag(String tagToReplace, String newValue,
			LFTestInputSteps filtered) {
		filtered.getOverwriteTags().stream().filter(tag -> tag.getTagID().equals(tagToReplace))
		.forEach(filteredTag -> filteredTag.setTagValue(newValue));
	}
}
