package com.citigroup.liquifi.autopilot.bootstrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;
import com.citigroup.liquifi.util.DBUtil;
import com.citigroup.liquifi.util.Util;

public class C4AutoPilotDuplicator extends AutoPilotBootstrap{
	private static String testCaseNameSeperator = "_";
	private static String fromLabel = "New Currency Profiles";
	private static String toLabel = "Sell";
	private static Map<String, String> inboundReplacements = new HashMap<>();
	private static Map<String, String> outboundReplacements = new HashMap<>();
	private static int casesLeft=0;
	private static int stepCount = 0;
	private static boolean contains = false;
	private static String descriptFrom = "buy";
	private static String descriptionTo = "sell";
	private static String nameFrom = "Major";
	private static String nameTo = "Traded";
	private static String filterName = "Minor";
	private static String filterDescription = "agora";
	private static String labelFilter = "CAT";
	private static String name = "pb90047";

	/**
	 * Main method launching the application.
	 */
	public static void main(String[] args) {
		System.setProperty("Mode", "massaltertags");

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
			logger.info("Launching AutoPilot in duplicate mode...");
			duplicate();
			break;
		case "massaltertags":
			logger.info("Launching AutoPilot in massAlterTags mode...");
			massAlterTags();
			break;
		case "copylabels":
			logger.info("Launching AutoPilot in copylabels mode...");
			copyFromLabels();
			break;
		case "changenumbers":
			logger.info("Launching AutoPilot in changeNumbers mode...");
			changeNumbers();
			break;
		case "removelabel":
			logger.info("Launching AutoPilot in removelabel mode...");
			removeLabel();
			break;
		case "swaptopics":
			logger.info("Launching AutoPilot in swaptopics mode...");
			swapToAdminTopics();
			break;
		case "massaltername":
			logger.info("Launching AutoPilot in massaltername mode...");
			massAlterName();
			break;
		default:
			logger.info("Launching AutoPilot in default mode...");
			massAlterTags();
			break;
		}
	}

	private static void addTagsToReplace() {
		/*
		 * The following are examples of how to 
		 * add tags to XML messages and FIX 
		 * messages for both inbound and 
		 * outbound flows
		*/
		
		outboundReplacements.put("15","ILA");
		outboundReplacements.put("44","193200");
		outboundReplacements.put("99","193300");
	}

	private static List<String> getFilteredTestCases() {
		logger.info("Getting Labels");
		return DBUtil.getInstance().getLbm().getLabels().stream()
				.filter(label -> label.getLabel().contains(labelFilter)).map(filtered -> filtered.getLabel()).collect(Collectors.toList());
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
		logger.info("Size: " + casesLeft);
		Set<LFTestCase> clones = new HashSet<LFTestCase>();
		testcases.forEach(testCase -> {
			String description = testCase.getDescription();
			LFTestCase clone = testCase.clone(Util.getTestIDSequencer(false));
			clone.setDescription(description);
			clones.add(clone);	
		});
		logger.info("Clone size:" + clones.size());
		clones.forEach(clone -> {
			filterTestCases(clone, clone.getDescription(),false);
			
		});
	}

	private static void massAlterTags() {
		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testCase -> {
			String description = testCase.getDescription();
			filterTestCasesNoDBUpdate(testCase, description);
		});
		try{
			DBUtil.getInstance().updateListtoDB(testcases);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private static void filterTestCasesNoDBUpdate(LFTestCase testCase, String description) {
		if(description.contains(filterDescription) && testCase.getName().contains(filterName)){
			updateTestCase(testCase,description);
			logger.info("Updating -> "+testCase.getName());
		}
		else {
			logger.info("Ignoring -> "+testCase.getName());
		}
		casesLeft--;
		logger.info("Cases Remaining = "+casesLeft);
		logger.info("-------------------------------------------");
	}

	private static void filterTestCases(LFTestCase testCase, String description, boolean update) {
		if(description.contains(filterDescription)){
			updateTestCase(testCase, update, description);
			
		}
		else {
			logger.info("Ignoring -> "+testCase.getName());
		}
		casesLeft--;
		logger.info("Cases Remaining = "+casesLeft);
		logger.info("-------------------------------------------");
	}
	
	private static void updateTestCase(LFTestCase testCase,String description) {
		testCase.getInputStepList().stream().filter(ipStep -> !"XML".equals(ipStep.getMsgType()))
		.forEach(filtered -> {inboundReplacements.keySet().forEach(key -> {
				replaceInboundTags(key, inboundReplacements.get(key), filtered);
			});
			outboundReplacements.keySet().forEach(key -> {
				replaceOutputTags(key, outboundReplacements.get(key), filtered);
			});
		});
		stepCount = 0;
		changeNameAndDescription(description, testCase, 18);
	}
	
	private static void updateTestCase(LFTestCase testCase, boolean update, String description) {
		testCase.getInputStepList().stream().filter(ipStep -> !"XML".equals(ipStep.getMsgType()))
		.forEach(filtered -> {inboundReplacements.keySet().forEach(key -> {
				replaceInboundTags(key, inboundReplacements.get(key), filtered);
			});
			outboundReplacements.keySet().forEach(key -> {
				replaceOutputTags(key, outboundReplacements.get(key), filtered);
			});
		});
		stepCount = 0;
		changeNameAndDescription(description, testCase, 18);
		if(update){
			logger.info("Updating -> "+testCase.getName());
			updateDB(testCase);
		} else {
			logger.info("Cloning -> "+testCase.getName());
			saveToDB(testCase);
		}
		
	}

	private static void replaceOutputTags(String tagToReplace, String newValue,
			LFTestInputSteps filtered) {
		List<LFOutputMsg> outPutMessage = filtered.getOutputStepList().stream()
				.filter(step -> step.getTopicID().contains("Arbol") && !step.getTemplate().contains("PENDING"))
				.collect(Collectors.toList());
		replaceOutboundExistingTag(tagToReplace, newValue, outPutMessage);
	}

	private static void replaceOutboundExistingTag(String tagToReplace, String newValue,List<LFOutputMsg> outPutMessages) {
		outPutMessages.stream()/*.filter(step -> step.getTopicID().contains("EUCB2")) && !step.getTopicID().contains("OES"))*/.forEach(filtered -> {
			List<LFOutputTag> contained = filtered.getOutputTagList().stream().filter(tag -> tag.getTagID().equals(tagToReplace)).collect(Collectors.toList());
			if(!contained.isEmpty()){
				contained.get(0).setTagValue(newValue);
			} else {
				//LFOutputTag newTag = new LFOutputTag(tagToReplace, newValue);
				//newTag.setOutputMsgID(filtered.getOutputMsgID());
				//newTag.setActionSequence(filtered.getActionSequence());
				//newTag.setTestID(filtered.getTestID());
				//filtered.addToOutputTagList(newTag);
			}
		});
	}

	private static void replaceInboundTags(String tagToReplace, String newValue,LFTestInputSteps inputMessage) {
		//if(inputMessage.getTopicID().contains("EUCB2") /*&& !inputMessage.getTopicID().contains("OES")*/){
			List<LFTag> contained = inputMessage.getOverwriteTags().stream().filter(tag -> tag.getTagID().equals(tagToReplace)).collect(Collectors.toList());
			if(!contained.isEmpty()){
				inputMessage.getOverwriteTags().stream().filter(tag -> tag.getTagID().equals(tagToReplace))
				.forEach(filteredTag -> filteredTag.setTagValue(newValue));
			} else {
				//inputMessage.setInputTagsValueList(new ArrayList<LFTag>());
				LFTag newTag = new LFTag(tagToReplace, newValue);
				newTag.setActionSequence(inputMessage.getActionSequence());
				inputMessage.addToInputTagValueList(newTag);
			}
		//};
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
			if(testCase.getDescription().contains(filterDescription)){
				logger.info("TC = "+testCase.getName());
				//Set<LFLabel> modifiedLabels = testCase.getLabelSet().stream().filter(label -> !"UpscaledVsUpscaled".equals(label.getLabel())).collect(Collectors.toSet());
				//modifiedLabels.forEach(label -> {
					logger.info("LABEL : "+fromLabel);
					try {
						DBUtil.getInstance().getLbm().removeLabelFromTestcase(fromLabel, testCase.getTestID());
					} catch (Exception e) {
						e.printStackTrace();
					}
				//});
	
				casesLeft--;
				logger.info("Saved, Remaining : "+casesLeft);
			}
		});
	}

	private static void copyFromLabels() {
		List<String> labels = new ArrayList<>(Arrays.asList(toLabel.split(",")));

		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testCase -> {
/*			for(LFLabel label : testCase.getLabelSet()){
				if(label.getLabel().equals(toLabel)){
					contains = true;
					break;
				}
			}
*/
			if(testCase.getDescription().contains(filterDescription)){
				for(String label : labels){
					try {
						logger.info("Adding "+testCase.getName()+" To "+label+" label...");
						DBUtil.getInstance().getLbm().addTestCaseToLabel(label,testCase.getTestID()); 
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				logger.info("Ignoring"+testCase.getName()+" To Label ...");
			}
			casesLeft--;
			logger.info("Updated, Remaining : "+casesLeft);
		});
	}

	private static void changeNameAndDescription(String description, LFTestCase testCase, int offset) {
		//testCase.setLastEditedUser(name);
		testCase.setDescription(description.replace(descriptFrom, descriptionTo));
		//changeName(testCase, offset);
	}

	private static void changeName(LFTestCase testCase, int offset) {
		String[] splitName = testCase.getName().split(testCaseNameSeperator);
		testCase.setName(testCase.getName().replace(nameFrom,nameTo));
	}

	private static void saveToDB(LFTestCase testCase) {
		DBUtil.getInstance().saveToDB(testCase);
		try {
			DBUtil.getInstance().getLbm().addTestCaseToLabel(toLabel,testCase.getTestID());
		} catch (Exception e) {
			logger.warning("Error saving to DB ");
		}
	}

	private static void updateDB(LFTestCase testCase) {
		try {
			DBUtil.getInstance().updateDB(testCase);
		} catch (Exception e) {
			logger.warning("Error saving to DB ");
		}
	}

/*	private static boolean isSecond() {
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
*/
}
