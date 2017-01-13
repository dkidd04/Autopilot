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
	private static String toLabel = "New Currency Profiles";
	private static Map<String, String> inboundReplacements = new HashMap<>();
	private static Map<String, String> outboundReplacements = new HashMap<>();
	private static int casesLeft=0;
	private static int stepCount = 0;
	private static boolean contains = false;
	private static String descriptFrom = "pounds";
	private static String descriptionTo = "pence";
	private static String nameFrom = "Major";
	private static String nameTo = "Traded";
	private static String filterName = "Traded";
	private static String filterDescription = "pounds";
	private static String labelFilter = "CAT";
	private static String name = "pb90047";

	/**
	 * Main method launching the application.
	 */
	public static void main(String[] args) {
		System.setProperty("Mode", "duplicate");

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
			System.out.println("Launching AutoPilot in duplicate mode...");
			duplicate();
			break;
		case "massaltertags":
			System.out.println("Launching AutoPilot in massAlterTags mode...");
			massAlterTags();
			break;
		case "copylabels":
			System.out.println("Launching AutoPilot in copylabels mode...");
			copyFromLabels();
			break;
		case "changenumbers":
			System.out.println("Launching AutoPilot in changeNumbers mode...");
			changeNumbers();
			break;
		case "removelabel":
			System.out.println("Launching AutoPilot in removelabel mode...");
			removeLabel();
			break;
		case "swaptopics":
			System.out.println("Launching AutoPilot in swaptopics mode...");
			swapToAdminTopics();
			break;
		case "massaltername":
			System.out.println("Launching AutoPilot in massaltername mode...");
			massAlterName();
			break;
		default:
			System.out.println("Launching AutoPilot in default mode...");
			massAlterTags();
			break;
		}
	}

	private static void addTagsToReplace() {
		//inboundReplacements.put("Parameters.Customer.RemoveProfile.GBPFlag.listValue","GBPFlag");
		inboundReplacements.put("Parameters.Customer.RemoveProfile.ILSFlag.listValue","ILSFlag");
		//inboundReplacements.put("Parameters.Customer.RemoveProfile.ZARFlag.listValue","ZARFlag");
		//inboundReplacements.put("Parameters.Customer.SetProfile.GBCurrHandling.listValue","Traded");
		//inboundReplacements.put("Parameters.Customer.SetProfile.SACurrHandling.listValue","U");
		//inboundReplacements.put("Parameters.Customer.SetProfile.ILCurrHandling.listValue","U");
		/*
		inboundReplacements.put("55","CGEN.TA");
		inboundReplacements.put("15","ILA");
		inboundReplacements.put("6","193100");
		inboundReplacements.put("31","193100");
		inboundReplacements.put("44","193200");
		inboundReplacements.put("99","193300");
		inboundReplacements.put("426","193100.0");
		
		outboundReplacements.put("55","CGEN.TA");
		outboundReplacements.put("15","ILA");
		outboundReplacements.put("6","193100");
		outboundReplacements.put("31","193100");
		outboundReplacements.put("44","193200");
		outboundReplacements.put("99","193300");
		outboundReplacements.put("426","193100.0");
		*/
	}

	private static List<String> getFilteredTestCases() {
		System.out.println("Getting Labels");
		return DBUtil.getInstance().getLbm().getLabels().stream()
				.filter(label -> label.getLabel().contains(labelFilter)).map(filtered -> filtered.getLabel()).collect(Collectors.toList());
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
	
	private static void duplicate() {
		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testCase -> {
			String description = testCase.getDescription();
			LFTestCase clone = testCase.clone(Util.getTestIDSequencer());
			filterTestCases(clone, description,false);
		});
	}

	private static void massAlterTags() {
		Set<LFTestCase> testcases = getTestCases(fromLabel);
		testcases.forEach(testCase -> {
			String description = testCase.getDescription();
			filterTestCases(testCase, description,true);
		});
	}

	private static void filterTestCases(LFTestCase testCase, String description, boolean update) {
		if(description.contains(filterDescription) && testCase.getName().contains(filterName)){
			updateTestCase(testCase, update, description);
		}
		else {
			System.out.println("Ignoring -> "+testCase.getName());
		}
		casesLeft--;
		System.out.println("Cases Remaining = "+casesLeft);
		System.out.println("-------------------------------------------");
	}

	private static void updateTestCase(LFTestCase testCase, boolean update, String description) {
		testCase.getInputStepList().stream().filter(ipStep -> "XML".equals(ipStep.getMsgType()))
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
			System.out.println("Updating -> "+testCase.getName());
			updateDB(testCase);
		} else {
			System.out.println("Cloning -> "+testCase.getName());
			saveToDB(testCase);
		}
	}

	private static void replaceOutputTags(String tagToReplace, String newValue,
			LFTestInputSteps filtered) {
		List<LFOutputMsg> outPutMessage = filtered.getOutputStepList().stream()
				//.filter(step -> step.getTopicID().contains("EUCB2"))
				.collect(Collectors.toList());
		replaceOutboundExistingTag(tagToReplace, newValue, outPutMessage);
	}

	private static void replaceOutboundExistingTag(String tagToReplace, String newValue,List<LFOutputMsg> outPutMessages) {
		outPutMessages.stream().filter(step -> /*step.getTopicID().contains("EUCB2"))/* &&*/ !step.getTopicID().contains("OES")).forEach(filtered -> {
			List<LFOutputTag> contained = filtered.getOutputTagList().stream().filter(tag -> tag.getTagID().equals(tagToReplace)).collect(Collectors.toList());
			if(!contained.isEmpty()){
				contained.get(0).setTagValue(newValue);
			}/* else {
				LFOutputTag newTag = new LFOutputTag(tagToReplace, newValue);
				newTag.setOutputMsgID(step.getOutputMsgID());
				newTag.setActionSequence(step.getActionSequence());
				newTag.setTestID(step.getTestID());
				step.addToOutputTagList(newTag);
			}*/
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
		labels.forEach(label -> System.out.println(label));
		Set<LFTestCase> testcases = getTestCases(labels.toArray(new String[labels.size()]));
		testcases.forEach(testCase -> {
			System.out.println("BEFORE -> "+testCase.getName());
			changeName(testCase,0);
			System.out.println("AFTER -> "+testCase.getName());
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
					System.out.println("Error replacing step for case "+testcase.getName());
					step.setTopicID("AutoPilotToLiqFiAdmin");
					try {
						DBUtil.getInstance().updateDB(testcase);
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("-------------------------------------");
				} 
			});
			casesLeft--;
			System.out.println("Remaining :: "+casesLeft);
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
			if(testCase.getDescription().contains(filterDescription)){
				System.out.println("TC = "+testCase.getName());
				//Set<LFLabel> modifiedLabels = testCase.getLabelSet().stream().filter(label -> !"UpscaledVsUpscaled".equals(label.getLabel())).collect(Collectors.toSet());
				//modifiedLabels.forEach(label -> {
					System.out.println("LABEL : "+fromLabel);
					try {
						DBUtil.getInstance().getLbm().removeLabelFromTestcase(fromLabel, testCase.getTestID());
					} catch (Exception e) {
						e.printStackTrace();
					}
				//});
	
				casesLeft--;
				System.out.println("Saved, Remaining : "+casesLeft);
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
						System.out.println("Adding "+testCase.getName()+" To "+label+" label...");
						DBUtil.getInstance().getLbm().addTestCaseToLabel(label,testCase.getTestID()); 
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("Ignoring"+testCase.getName()+" To Label ...");
			}
			casesLeft--;
			System.out.println("Updated, Remaining : "+casesLeft);
		});
	}

	private static void changeNameAndDescription(String description, LFTestCase testCase, int offset) {
		//testCase.setLastEditedUser(name);
		//testCase.setDescription(description.replace(descriptFrom, descriptionTo));
		changeName(testCase, offset);
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
			System.out.println("Error saving to DB ");
		}
	}
	
	private static void updateDB(LFTestCase testCase) {
		try {
			DBUtil.getInstance().updateDB(testCase);
		} catch (Exception e) {
			System.out.println("Error saving to DB ");
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
