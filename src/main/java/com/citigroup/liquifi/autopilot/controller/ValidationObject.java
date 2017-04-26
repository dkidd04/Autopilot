package com.citigroup.liquifi.autopilot.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.autopilot.messaging.ConnectionManager;
import com.citigroup.liquifi.autopilot.util.ValidationResult;
import com.citigroup.liquifi.autopilot.validation.ValidationManager;
import com.citigroup.liquifi.entities.LFCommonOverwriteTag;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFTemplate;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;
import com.citigroup.liquifi.entities.Tag;
import com.citigroup.liquifi.util.AutoPilotConstants;
import com.citigroup.liquifi.util.DBUtil;

public class ValidationObject {
	private final static AceLogger logger = AceLogger.getLogger(ValidationObject.class.getSimpleName());
	private final Set<String> acceptingSymbols = new HashSet<String>();
	private final String symbol;
	private String validationResultMsg = "";
	private int failedInputStep = 0;
	private int failedOutputMsgID = 0;
	private boolean validationResultStatus = true;
	private final Iterator<LFTestInputSteps> inputStepListIterator;
	private LFTestInputSteps inputStep;
	private final LFTestCase testcase;
	private final StepMessages[] stepMessages;
	private final Map<String, Integer> topicCount = new HashMap<String, Integer>();

	public ValidationObject(LFTestCase testcase, String symbol) {
		this.testcase = testcase;
		this.symbol = symbol;
		this.inputStepListIterator = testcase.getInputStepList().iterator();
		this.inputStep = null;
		this.acceptingSymbols.add(symbol);
		if (ApplicationContext.getConfig().getSecurityManager() != null) {
			Set<String> alternateIDs = ApplicationContext.getConfig().getSecurityManager().getAlternateName(symbol);
			if (alternateIDs != null && !alternateIDs.isEmpty())
				this.acceptingSymbols.addAll(alternateIDs);
		}
		this.stepMessages = new StepMessages[testcase.getInputStep().size()+1];
	}

	public boolean hasNext() {
		return inputStepListIterator.hasNext();
	}

	public LFTestInputSteps next() {
		inputStep = inputStepListIterator.next();
		return inputStep;
	}

	public int getInputStepNumber() {
		return (inputStep == null) ? 1 : inputStep.getActionSequence();
	}

	public LFTestInputSteps getInputStep() {
		return inputStep;
	}

	public LFTestCase getTestcase() {
		return testcase;
	}

	public void setAndSaveValidationResult(boolean validationResultStatus, int failedInputStep, int failedOutputMsgID, String validationResultMsg) {
		this.validationResultStatus = validationResultStatus;
		this.failedInputStep = failedInputStep;
		//this.failedOutputMsgID = failedOutputMsgID;
		this.validationResultMsg = validationResultMsg;

		this.printValidationObject();
	}
	
	public void setupStep(int step, int expectedOutputMsgNumber) {
		if(stepMessages[step] == null) {
			stepMessages[step] = new StepMessages(expectedOutputMsgNumber);
		}
		
		topicCount.clear();
		
		for(LFOutputMsg outputMsg : this.getInputStep().getOutputStepList()) {
			String topic = TestCaseController.INSTANCE.getAcceptorTopicNameByID(outputMsg.getTopicID(), this.getSymbol());
			
			if(!ApplicationContext.getTopicManagerTableModel().isActiveTopic(outputMsg.getTopicID())){
				continue;
			}
			
			if(!topicCount.containsKey(topic)) {
				topicCount.put(topic, 1);
			} else {
				topicCount.put(topic, topicCount.get(topic)+1);
			}
		}
		
	}
	
	public synchronized void storeCurrentInbound(int step, String message, String topic) {
		stepMessages[step].setInbound(message, topic);
	}

	/**
	 * Called by OutputMsgNotifier when storing an arriving output message - Msg placed by topicID and msgID set appropiatly
	 * @param strOutputMsg
	 * @param strTopic
	 * @return
	 */
	public synchronized boolean storeCurrentOutput(int step, String strOutputMsg, String topic) {
		// new logic
		stepMessages[step].addActual(strOutputMsg, topic);
		
		if(topicCount.containsKey(topic)) {
			int count = topicCount.get(topic);
			if(count == 1) {
				topicCount.remove(topic);
			} else {
				topicCount.put(topic, count-1);
			}
			
			return true;
		}
		
		
		return false;
	}

	private Map<String, List<LFCommonOverwriteTag>> commonOverwriteTagMap = DBUtil.getInstance().getCom().getCommonOverwriteTagMap();
	private Map<String, LFTemplate> templateMap = DBUtil.getInstance().getTem().getAllTemplateMap();
	
	public synchronized ValidationResult validate(int step, List<LFOutputMsg> expectedOutputSteps, boolean isBenchMarkModeOn) throws Exception {
		
		if(stepMessages[step] == null) {
			return new ValidationResult(false, "Can not validate a null step");
		}
		
		for(Entry<String, Integer> entry : topicCount.entrySet()) {
			return new ValidationResult(false, "Failed to receive "+entry.getValue()+" messages on topic "+entry.getKey());
		}

		
		Set<Tag> overwrite = new HashSet<Tag>();
		LinkedList<LFOutputMsg> expectedQueue = new LinkedList<LFOutputMsg>();
		
		// expected output steps could be empty (maybe even null?)
		if (expectedOutputSteps != null) {
			expectedQueue.addAll(expectedOutputSteps);
		}
		
		while(!expectedQueue.isEmpty()) {
			LFOutputMsg expectedOutputMsg = expectedQueue.poll();
			
			String topic = TestCaseController.INSTANCE.getAcceptorTopicNameByID(expectedOutputMsg.getTopicID(), this.getSymbol());
			
			if (topic == null) {
				return new ValidationResult(false, "Topic ID "+expectedOutputMsg.getTopicID()+" is not setup in broker.xml");
			}
			
			String exceptedOutputMsg = null;
			//String strCustValidationClassName = expectedOutputMsg.getCustValidationClass();
			overwrite.clear();

			// Add any overwrite tags
			overwrite.addAll(expectedOutputMsg.getOutputTagList());

			// Add any step common tags to the overwrite set.
			if (expectedOutputMsg.getCommonTags() != null && !expectedOutputMsg.getCommonTags().equals(" ")) {
				overwrite.addAll(commonOverwriteTagMap.get(expectedOutputMsg.getCommonTags()));
			}

			if (expectedOutputMsg.getTemplate() != null && expectedOutputMsg.getTemplate().trim().length() != 0) {
				LFTemplate template = templateMap.get(expectedOutputMsg.getTemplate());
				exceptedOutputMsg = template.getMsgTemplate();
				if (template.getCommonOverwriteTagListName() != null && !template.getCommonOverwriteTagListName().equals(AutoPilotConstants.EMPTY_COMBO_STRING)) {
					overwrite.addAll(commonOverwriteTagMap.get(template.getCommonOverwriteTagListName()));
				}
			} else if (expectedOutputMsg.getOutputMsg() != null && expectedOutputMsg.getOutputMsg().trim().length() != 0) {
				exceptedOutputMsg = expectedOutputMsg.getOutputMsg();
			} else {
				logger.warning("TestCaseID:" + this.getTestcase().getTestID() + "|Symbol:" + this.getSymbol() + "|InputStep:" + this.getInputStep().getActionSequence() + "Current step does not expected output. Validate next step.");
				continue;
			}

			//if (!exceptedOutputMsg.equals(AutoPilotConstants.NO_MESSAGE)) {
				exceptedOutputMsg = ApplicationContext.getFIXFactory().genFIXMessage(exceptedOutputMsg, overwrite).toString();
			//}

			// parse the reference place holder like (11=@INPUT[2].getTag(11)) at run time
			exceptedOutputMsg = ApplicationContext.getPlaceHolders().parsePlaceholders(exceptedOutputMsg, true, this.getTestcase(), this.getInputStep().getActionSequence(), this.getSymbol(), this);
			
			// store expected step
			stepMessages[step].addExpected(exceptedOutputMsg, topic);
			
			
			//String currentResultedOutputMsg = this.retrieveCurrentOutputMsg(this.getActualOutputStepList(), this.getInputStep().getActionSequence(), strTopicNameExpectedOutputMsg, expectedOutputMsg.getOutputMsgID());

			
			// get all actual messages for the topic
			List<Message> messagesOnTopic = stepMessages[step].getActualMessages(topic);
			

			if(messagesOnTopic == null && ApplicationContext.getTopicManagerTableModel().isActiveTopic(topic)) {
				this.failedOutputMsgID = expectedOutputMsg.getOutputMsgID();
				return new ValidationResult(false, "No msg from topic " + topic);
			}
			// skip disabled topic
			boolean success = false;
			if(!ApplicationContext.getTopicManagerTableModel().isActiveTopic(expectedOutputMsg.getTopicID()) || !ApplicationContext.getTopicManagerTableModel().isActiveTopic(this.getInputStep().getTopicID())){
				success = true;
			}
						
			// find any easy success
			if(!success){
				for(Message message : messagesOnTopic) {
					// Check if topic is active
					if(message.assigned == Message.UNASSIGNED && isBenchMarkModeOn == false) {
						ValidationResult validationResult = ValidationManager.INSTANCE.validate(exceptedOutputMsg, message.message);
						message.state[expectedOutputMsg.getOutputMsgID()-1] = validationResult;

						if(validationResult.isSuccess()) {
							message.assigned = expectedOutputMsg.getOutputMsgID()-1;
							success = true;
							break;
						}
					}
					else if (message.assigned == Message.UNASSIGNED && isBenchMarkModeOn == true){
						message.assigned = expectedOutputMsg.getOutputMsgID()-1;
						success = true;
						break;

					}
				}
			}
	
			// if no easy success then see if any match can be found
	
			if(!success) {
				for(Message message : messagesOnTopic) {
					if(message.assigned != Message.UNASSIGNED && isBenchMarkModeOn == false) {
						if(message.state[expectedOutputMsg.getOutputMsgID()-1] == null) {
							ValidationResult validationResult = ValidationManager.INSTANCE.validate(exceptedOutputMsg, message.message);
							message.state[expectedOutputMsg.getOutputMsgID()-1] = validationResult;
							if(validationResult.isSuccess()) {
								// kick out previous assigned expected output
								LFOutputMsg kickedOut = expectedOutputSteps.get(message.assigned);
								expectedQueue.addFirst(kickedOut);
								
								// now reassign
								message.assigned = expectedOutputMsg.getOutputMsgID()-1;
								success = true;
								break;
							}
						}
					}
					else if(message.assigned != Message.UNASSIGNED && isBenchMarkModeOn == true)
					{
						LFOutputMsg kickedOut = expectedOutputSteps.get(message.assigned);
						expectedQueue.addFirst(kickedOut);

						// now reassign
						message.assigned = expectedOutputMsg.getOutputMsgID()-1;
						success = true;
						break;

					}
				}
			}
	
			// if still no success return the result created for the first unassigned failure
	
			if(!success && isBenchMarkModeOn == false) {
				for(Message message : messagesOnTopic) {
					if(message.assigned == Message.UNASSIGNED) {
						this.failedOutputMsgID = expectedOutputMsg.getOutputMsgID();
						return message.state[expectedOutputMsg.getOutputMsgID()-1];
					}
				}
			}
	
		}
	

		if(ApplicationContext.getConfig().isCompletenessCheck() && isBenchMarkModeOn == false) {
			// check that messages have all been assigned
			for(Message message : stepMessages[step].getActualSortedMessages()) {
				if(message.assigned == Message.UNASSIGNED) {
					return new ValidationResult(false, "Received messages not specified in the testcase");
				}
			}
		}
		
		return new ValidationResult(true, "");
	}


	// used to get the actual output message at a certain step, msgID
	public String getOutputMsgByStepAndOutputMsgID(int step, int outputMsgID) {
		return stepMessages[step].getActualMessage(outputMsgID);
	}

	/**
	 * Method is synchronized as it is possible to be still receiving messages for testcase whilst result is being printed out
	 */
	public synchronized void printValidationObject() {
		for (int i = 1; i < stepMessages.length; i++) {
			
			// end
			if(stepMessages[i] == null) {
				break;
			}
			
			logger.info("InputStep:" + i);
			
			if(stepMessages[i].getInbound() != null) {
				logger.info("    Input.topic:" + stepMessages[i].getInbound().topic);
				logger.info("    Input.msg:" + stepMessages[i].getInbound().message.replaceAll("\r\n", ""));
			}
	
			for (Message oMsg : stepMessages[i].getActualSortedMessages()) {
				logger.info("    Inbound.topic:"+oMsg.topic);
				logger.info("    Inbound.msg:" + oMsg.message);
				logger.info("    Inbound.status:"+(oMsg.assigned == Message.UNASSIGNED ? "FAIL" : "SUCCESS"));
			}
	
			int count = 1;
			List<Message> actual = stepMessages[i].getActualSortedMessages();
			for (Message oMsg : stepMessages[i].getExpectedMessages()) {
				String topicID = ConnectionManager.INSTANCE.getAcceptorTopicId(oMsg.topic);
				logger.info("    OutputMsg.msgID:" + count);
				if(ApplicationContext.getTopicManagerTableModel().isActiveTopic(topicID)){
					logger.info("        OutputMsg.topic (Enabled):" + oMsg.topic);
					logger.info("        ExpectedOutputMsg.msg:" + oMsg.message);
					logger.info("        ActualdOutputMsg.msg:" + (actual.size() >= count ? actual.get(count-1).message : ""));
					count++;
				}else{
					logger.info("        OutputMsg.topic (Disabled):" + oMsg.topic);
					logger.info("        	OutputMsg topic is 'Diabled', skipped output step");
				}
			}
		}
		
		logger.info("*******************************************************************************");
		if(this.validationResultStatus) {
			logger.info("VALIDATIONPASSED|TestCaseID:" + testcase.getTestID() + "|TestCase Name:"+ testcase.getName() + "|Symbol:" + this.symbol+"| :-) ");
		} else {
			logger.info("VALIDATIONFAILED|TestCaseID:" + testcase.getTestID() + "|TestCase Name:"+ testcase.getName() +"|Symbol:" + this.symbol+"|" + this.validationResultMsg+ "|InputStep:" + this.failedInputStep + "|OutputStep:" + this.failedOutputMsgID);
		}
		logger.info("*******************************************************************************");
	}


	public String getSymbol() {
		return symbol;
	}

	public String getValidationResultMsg() {
		return validationResultMsg;
	}

	public void setValidationResultMsg(String validationResultMsg) {
		this.validationResultMsg = validationResultMsg;
	}

	public int getFailedInputStep() {
		return failedInputStep;
	}

	public void setFailedInputStep(int failedInputStep) {
		this.failedInputStep = failedInputStep;
	}

	public int getFailedOutputMsgID() {
		return failedOutputMsgID;
	}

	public void setFailedOutputMsgID(int failedOutputMsgID) {
		this.failedOutputMsgID = failedOutputMsgID;
	}

	public boolean isSuccess() {
		return validationResultStatus;
	}

	public void setValidationResultStatus(boolean validationResultStatus) {
		this.validationResultStatus = validationResultStatus;
	}
	
	public Message getInboundStep(int step) {
		if(step > stepMessages.length) {
			return null;
		}
		
		return (stepMessages[step] == null) ? null : stepMessages[step].getInbound();
	}
	
	public void addAcceptingSymbol(String symbol) {
		acceptingSymbols.add(symbol);
	}
	
	public Set<String> getAcceptingSymbols() {
		return acceptingSymbols;
	}
	
	public StepMessages getStepMessages(int step) {
		return stepMessages[step];
	}
}
