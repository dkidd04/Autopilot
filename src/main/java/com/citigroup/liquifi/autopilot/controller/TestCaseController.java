package com.citigroup.liquifi.autopilot.controller;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.citigroup.get.quantum.messaging.Initiator;
import com.citigroup.get.quantum.messaging.MessagingException;
import com.citigroup.get.quantum.messaging.QuantumTransportHeaderProperties;
import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.bootstrap.AutoPilotBootstrap;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.autopilot.message.FIXMessage;
import com.citigroup.liquifi.autopilot.messaging.AutoPilotBrokerInfoFactory;
import com.citigroup.liquifi.autopilot.messaging.ConnectionManager;
import com.citigroup.liquifi.autopilot.messaging.QueueInitiator;
import com.citigroup.liquifi.autopilot.socket.ClientSocket;
import com.citigroup.liquifi.autopilot.util.Command;
import com.citigroup.liquifi.autopilot.util.ValidationResult;
import com.citigroup.liquifi.autopilot.validation.AutoPilotValidator;
import com.citigroup.liquifi.entities.LFCommonOverwriteTag;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFTemplate;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;
import com.citigroup.liquifi.entities.Tag;
import com.citigroup.liquifi.util.AutoPilotConstants;
import com.citigroup.liquifi.util.DBUtil;
import com.citigroup.liquifi.util.Util;

public enum TestCaseController {
	INSTANCE;

	private static AceLogger logger = AceLogger.getLogger(TestCaseController.class.getSimpleName());
	private Map<String, LFTemplate> templateMap = DBUtil.getInstance().getTem().getAllTemplateMap();
	private Map<String, List<LFCommonOverwriteTag>> commonOverwriteTagMap = DBUtil.getInstance().getCom().getCommonOverwriteTagMap();
	private AutoPilotBrokerInfoFactory brokerFactory = ApplicationContext.getBrokerFactory();
	public static final boolean OMIT_XML_DECLARATION = Boolean.getBoolean("OMIT_XML_DECLARATION");

	public ValidationObject loadState(LFTestCase testcase, String symbolStr) {
		String symbolToUse = "";
		try {
			// If the user does not provide Symbol, get a default value from config

			if (symbolStr != null && symbolStr.trim().length() > 0) {
				symbolToUse = symbolStr;
			} else {
				int intSecurityClass = testcase.getSecurityClass();
				symbolToUse = ApplicationContext.getConfig().getDefaultSymbolMap().get(intSecurityClass);

				if (symbolToUse == null) {
					throw new Exception("No symbol definned for security class " + intSecurityClass + " - please use different security class or add one to config.xml");
				}
			}

			logger.info("Symbol:" + symbolToUse);

			// construct validation object
			ValidationObject validationObject = new ValidationObject(testcase, symbolToUse);

			// setup notifiers
			AdminMsgNotifier.INSTANCE.setup(validationObject);
			OutputMsgNotifier.INSTANCE.setup(validationObject);

			// start up all acceptors and sockets associated with the symbol
			for(String autoPilotTopic : ApplicationContext.getBrokerFactory().getAcceptorBrokerMapping().keySet()) {
				ConnectionManager.INSTANCE.getAcceptor(symbolToUse, autoPilotTopic);
			}


			// setup the sockets by find out what is relevant for the testcase - sockets and TIBCO topics should be merged into one concept and handled the same way
			if (ApplicationContext.getSocketFactory() != null) {
				for (LFTestInputSteps inputStep : testcase.getInputStepList()) {
					for (LFOutputMsg outputStep : inputStep.getOutputStepList()) {
						if (ApplicationContext.getSocketFactory().isSocket(outputStep.getTopicID())) {
							ApplicationContext.getSocketFactory().setupSocket(outputStep.getTopicID());
						}
					}

					ApplicationContext.getSocketFactory().setupSocket(inputStep.getTopicID());
				}
			}

			return validationObject;
		} catch (Exception ex) {
			AutoPilotBootstrap.getFailedTestcases().add(testcase.getTestID()+"NULLED");
			logger.severe("TestCaseID:" + testcase.getTestID() + "|Symbol:" + symbolToUse + "|" + AutoPilotConstants.ValidationFailed_CannotLoadTestCase);
			logger.severe(Util.getStackTrace(ex));
		}

		return null;
	}

	public boolean runTestCaseNextStep(ValidationObject state, boolean canTakeItSlow, boolean isBenchMarkModeOn) {
		try {
			if (!state.hasNext()) {
				return false;
			}

			String inputMsg = "";
			Set<Tag> overwrite = new HashSet<Tag>();
			String strMsgType = "";

			state.next();

			logger.info("Running Testcase|Step:" + state.getInputStepNumber());

			if (state.getInputStep() == null) {
				throw new Exception("current input step can not be null");
			} else {
				List<LFOutputMsg> outputSteps = state.getInputStep().getOutputStepList();
				strMsgType = state.getInputStep().getMsgType();

				int expectedOutputMsgNumber = getActiveOutputStepsNum(outputSteps);

				boolean success = OutputMsgNotifier.INSTANCE.reset(state.getInputStepNumber(), outputSteps == null ? 0 : expectedOutputMsgNumber);

				// fails if the output msg notifier receives messages between steps
				if(!success) {
					state.setAndSaveValidationResult(false, state.getInputStep().getActionSequence(), /*expectedOutputMsg.getOutputMsgID()*/ -1, "Received messages after completing previous step, but before starting the next");
					return false;
				}

				if (AutoPilotConstants.MSG_TYPE_XML.equalsIgnoreCase(strMsgType)) {
					List<Command> commands = new ArrayList<Command>();

					// Add any overwrite tags
					overwrite.addAll(state.getInputStep().getInputTagsValueList());

					// Add any step common tags to the overwrite set.
					if (state.getInputStep().getCommonTags() != null) {
						overwrite.addAll(commonOverwriteTagMap.get(state.getInputStep().getCommonTags()));
					}

					// Get either the base message or template (possible comes with common tags)
					if (state.getInputStep().getTemplate() != null && state.getInputStep().getTemplate().trim().length() > 0) {
						LFTemplate template = templateMap.get(state.getInputStep().getTemplate());
						inputMsg = template.getMsgTemplate();
						if (template.getCommonOverwriteTagListName() != null && !template.getCommonOverwriteTagListName().equals(AutoPilotConstants.ComboBoxEmptyItem)) {
							overwrite.addAll(commonOverwriteTagMap.get(template.getCommonOverwriteTagListName()));
						}
					} else if (state.getInputStep().getMessage() != null && state.getInputStep().getMessage().trim().length() > 0) {
						inputMsg = state.getInputStep().getMessage();
					} else {
						throw new Exception("input step can not be empty");
					}

					// TODO: merge all these parsing methods

					if (inputMsg.contains("<AdminOrderRequest>") || inputMsg.contains("<AdminMessage>")) {
						inputMsg = ApplicationContext.getXmlFactory().overWriteTags(inputMsg, overwrite);
					} else if(!inputMsg.contains("<AdminOrderRequest2>")) {
						inputMsg = ApplicationContext.getXmlFactory().overWriteTagsGeneric(inputMsg, overwrite);
					}

					inputMsg = ApplicationContext.getXmlFactory().overwriteAdminOrderRequest2XMLMessage(inputMsg, overwrite);
					inputMsg = ApplicationContext.getPlaceHolders().parseAPVarPlaceholdersString(inputMsg, overwrite);
					inputMsg = ApplicationContext.getPlaceHolders().parsePlaceholders(inputMsg, false, state.getTestcase(), state.getInputStep().getActionSequence(), state.getSymbol(), state);
					inputMsg = ApplicationContext.getXmlFactory().extractCommands(inputMsg, commands);


					// if the initiator has a reply topic then setup listener
					String replyAcceptor = brokerFactory.getInitiatorBrokerMapping().get(state.getInputStep().getTopicID()).getReplyAcceptor();
					if (replyAcceptor != null) {
						AdminMsgNotifier.INSTANCE.reset(state);
						// temp hack, make the assumption that there is only one reply topic
						ConnectionManager.INSTANCE.getAcceptor(state.getSymbol(), replyAcceptor);
					}

					// Get initiator and send msg
					boolean found = false;
					if (ApplicationContext.getSocketFactory() != null) {
						for (ClientSocket socket : ApplicationContext.getSocketFactory().getSockets()) {
							if (state.getInputStep().getTopicID().equals(socket.getInbound())) {
								// if it disconnected then reconnect
								if (!socket.isConnected()) {
									ApplicationContext.getSocketFactory().setupSocket(socket.getInbound());
								}
								socket.send(inputMsg);
								found = true;
							}
						}
					}

					if (!found) {
						sendMessageEMS(state, inputMsg);
					}

					// wait for reply on reply topic
					if (replyAcceptor != null) {
						AdminMsgNotifier.INSTANCE.waitForAdminMsg();

						switch(AdminMsgNotifier.INSTANCE.getStatus()) {
						case NO_REPLY:
							logger.severe("pausing limit passed - allowing user to continue");
							break;
						case FAIL:
							state.storeCurrentInbound(state.getInputStep().getActionSequence(), inputMsg, ConnectionManager.INSTANCE.getInitiatorTopic(state.getSymbol(), state.getInputStep().getTopicID()));
							state.setAndSaveValidationResult(false, state.getInputStep().getActionSequence(), 0, AutoPilotConstants.ValidationFailed_AdminCommandFailed);
							return false;
						case SUCCESS:
							break;
						default:
							break;
						}
					}

					if(state.getInputStep().getTemplate() != null){
						// TotalTouch uses the adjustable clock object to manage time - lets reset time to 11:30 which is the same in TT
						if (state.getInputStep().getTemplate().equals("A_SETUP")) {
							ApplicationContext.getClock().setTimeWithinCurrentDay("11:30:00");
						} else if (state.getInputStep().getTemplate().equals("A_TIME")) {
							int start = inputMsg.indexOf("now");
							ApplicationContext.getClock().setTimeWithinCurrentDay(inputMsg.substring(start+12, start+20));
						}
					}

					// Examine autopilot commands sent on AdminOrderRequest2 message
					if (commands != null) {
						for (Command command : commands) {
							switch(command.getName()) {
							case Pause:
								try {
									int intPause = Integer.valueOf(command.getValue());
									Thread.sleep(intPause);
								} catch (Exception e) {
									logger.severe(Util.getStackTrace(e));
								}
								break;
							case Accept:
								for(String symbol : command.getValue().split(";")) {
									logger.info("Adding to accepting symbols|Symbol:"+symbol);
									state.addAcceptingSymbol(symbol);
								}
								break;
							}
						}
					}
				} else if (AutoPilotConstants.MSG_TYPE_CONTROL.equalsIgnoreCase(strMsgType)) {
					logger.info("Current InputStep is an AutoPilot Control message.");

					if (state.getInputStep().getMessage() != null && state.getInputStep().getMessage().trim().length() > 0 && state.getInputStep().getTemplate() == null) {
						inputMsg = state.getInputStep().getMessage();
					} else if (state.getInputStep().getTemplate() != null && state.getInputStep().getTemplate().trim().length() > 0) {
						inputMsg = DBUtil.getInstance().getTem().getAllTemplateMap().get(state.getInputStep().getTemplate()).getMsgTemplate();
						// If the any of the tag needs to be override, other than the place holders
						if (state.getInputStep().getInputTagsValueList().size() > 0) {
							overwrite.addAll(state.getInputStep().getInputTagsValueList());
							inputMsg = ApplicationContext.getXmlFactory().overWriteTags(inputMsg, overwrite);
						}

						AutoPilotControlMessageHandler.getInstance().processControlMessage(inputMsg);
					} else {
						logger.warning(AutoPilotConstants.AutoPilotWarning_TestCaseDesign_InputStepIsEmpty + " InputStep=" + state.getInputStep().getActionSequence());
					}
				} else if ("Others".equalsIgnoreCase(strMsgType)) {
					if (state.getInputStep().getMessage() != null && state.getInputStep().getMessage().trim().length() > 0 && state.getInputStep().getTemplate() == null) {
						inputMsg = state.getInputStep().getMessage();
					} else if (state.getInputStep().getTemplate() != null && state.getInputStep().getTemplate().trim().length() > 0) {
						inputMsg = DBUtil.getInstance().getTem().getAllTemplateMap().get(state.getInputStep().getTemplate()).getMsgTemplate();

					} else {
						logger.warning(AutoPilotConstants.AutoPilotWarning_TestCaseDesign_InputStepIsEmpty + " InputStep=" + state.getInputStep().getActionSequence());
					}

					sendMessageEMS(state, inputMsg);

				} else {
					// Add any overwrite tags
					overwrite.addAll(state.getInputStep().getInputTagsValueList());

					// Add any step common tags to the overwrite set.
					if (state.getInputStep().getCommonTags() != null) {
						overwrite.addAll(commonOverwriteTagMap.get(state.getInputStep().getCommonTags()));
					}

					// Get either the base message or template (possible comes with common tags)
					if (state.getInputStep().getTemplate() != null && state.getInputStep().getTemplate().trim().length() > 0) {
						LFTemplate template = templateMap.get(state.getInputStep().getTemplate());
						inputMsg = template.getMsgTemplate();
						if (template.getCommonOverwriteTagListName() != null && !template.getCommonOverwriteTagListName().equals(AutoPilotConstants.ComboBoxEmptyItem)) {
							overwrite.addAll(commonOverwriteTagMap.get(template.getCommonOverwriteTagListName()));
						}
					} else if (state.getInputStep().getMessage() != null && state.getInputStep().getMessage().trim().length() > 0) {
						inputMsg = state.getInputStep().getMessage();
					} else {
						// to add CustValidationClass logic for control msg type
						logger.warning(AutoPilotConstants.AutoPilotWarning_TestCaseDesign_InputStepIsEmpty + " InputStep=" + state.getInputStep().getActionSequence());
					}

					if (ApplicationContext.getConfig().isDebug()) {
						logger.info("strBeforeParsePlaceHolder=" + inputMsg);
					}

					inputMsg = ApplicationContext.getFIXFactory().genFIXMessage(inputMsg, overwrite).toString();
					inputMsg = parseInputMessage(inputMsg, state.getSymbol(), state.getTestcase(), state.getInputStep().getActionSequence(), state);

					// To fix tag 9 problems
					inputMsg = new FIXMessage(inputMsg).toString();

					// process repeating group tags 
					inputMsg =  ApplicationContext.getPlaceHolders().parseRepeatingGroup(inputMsg);


					// Get initiator and send msg
					boolean found = false;
					String topic = "";
					if (ApplicationContext.getSocketFactory() != null) {
						for (ClientSocket socket : ApplicationContext.getSocketFactory().getSockets()) {
							if (state.getInputStep().getTopicID().equals(socket.getInbound())) {
								socket.send(inputMsg);
								topic = "out:" + socket.getOutbound() + "|in:" + socket.getInbound();
								found = true;
							}
						}
					}

					if (!found) {
						Initiator initiatorLocal = ConnectionManager.INSTANCE.getInitiator(state.getSymbol(), state.getInputStep().getTopicID());
						topic = initiatorLocal.getTransportSender().getUrl() + "/" + ConnectionManager.INSTANCE.getInitiatorTopic(state.getSymbol(), state.getInputStep().getTopicID());
						initiatorLocal.send(inputMsg);
					}

					logger.info("OUTBOUND|FIXMSG|" + topic + "|" + inputMsg);

				}

				// store the actually generated InputMsg into the actualInputMsgList
				ValidationInputStep iStep = new ValidationInputStep(state.getInputStep().getActionSequence(), inputMsg, ConnectionManager.INSTANCE.getInitiatorTopic(state.getSymbol(), state.getInputStep().getTopicID()), strMsgType);
				state.storeCurrentInbound(state.getInputStep().getActionSequence(), iStep.getMsg(), iStep.getTopic());


				// if running the testcase individually or step-by-step then we can go slow and try to capture bad messages
				// if last step then do a slight pause to attempt to catch any messages that haven't been definned in the testcase
				boolean lastStep = !state.hasNext();
				if (lastStep || canTakeItSlow) {
					Thread.sleep(50);
				}

				if(ApplicationContext.getTopicManagerTableModel().isActiveTopic(state.getInputStep().getTopicID())){
					OutputMsgNotifier.INSTANCE.waitForAllOutputMsg();
				}
				// Check if InputStep topic is active

				// validate output stages even if none are expected
				ValidationResult result = state.validate(state.getInputStepNumber(), outputSteps, isBenchMarkModeOn);

				if(!ApplicationContext.getTopicManagerTableModel().isActiveTopic(state.getInputStep().getTopicID())){
					return true;
				}

				if (!result.isSuccess()) {
					state.setAndSaveValidationResult(false, state.getInputStep().getActionSequence(), /*expectedOutputMsg.getOutputMsgID()*/ -1, result.getReason());
					return false;
				} else {
					// don't save until all steps are complete or future step fails
					return true;
				}
			}

		} catch (Throwable ex) {
			state.setAndSaveValidationResult(false, state.getInputStep().getActionSequence(), 0, ex.getMessage());

			logger.severe(Util.getStackTrace(ex));
			logger.severe("TestCaseID:" + state.getTestcase().getTestID() + "|Symbol:" + state.getSymbol() + "|InputStep:" + state.getInputStep().getActionSequence() + "|" + AutoPilotConstants.ValidationFailed_CannotProcessInputMsg);
			return false;
		}
	}

	private void sendMessageEMS(ValidationObject state, String inputMsg) throws MessagingException {
		Initiator initiatorLocal = ConnectionManager.INSTANCE.getInitiator(state.getSymbol(), state.getInputStep().getTopicID());
		String topic = ConnectionManager.INSTANCE.getInitiatorTopic(state.getSymbol(), state.getInputStep().getTopicID());
		if(initiatorLocal instanceof QueueInitiator){
			QueueInitiator queue = (QueueInitiator) initiatorLocal;
			QuantumTransportHeaderProperties createTransportHeaderProperties = initiatorLocal.createTransportHeaderProperties();
			createTransportHeaderProperties.setTransportHeaderStringProperty("JMSCorrelationID", queue.getSelector());
			initiatorLocal.send(inputMsg, createTransportHeaderProperties);
		} else {
			initiatorLocal.send(inputMsg);
		}

		logger.info("sending message to " + initiatorLocal.getTransportSender().getUrl() + "/" + topic);
	}

	private int getActiveOutputStepsNum(List<LFOutputMsg> outputSteps) {
		int activeOutputStepsNum = 0;

		if(outputSteps != null){
			for(LFOutputMsg outStep : outputSteps){
				if(ApplicationContext.getTopicManagerTableModel().isActiveTopic(outStep.getTopicID())){
					activeOutputStepsNum++;
				}
			}
		}

		return activeOutputStepsNum;
	}

	public void passTestCase(ValidationObject state) {
		state.setAndSaveValidationResult(true, 0, 0, "");
	}

	public void finishTestCase(ValidationObject state) {
		// Close down any ports
		if (ApplicationContext.getSocketFactory() != null) {
			for (LFTestInputSteps inputStep : state.getTestcase().getInputStepList()) {
				for (LFOutputMsg outputStep : inputStep.getOutputStepList()) {
					ApplicationContext.getSocketFactory().closeSocket(outputStep.getTopicID());
				}
				ApplicationContext.getSocketFactory().closeSocket(inputStep.getTopicID());
			}
		}
	}

	public void terminateTestCase(ValidationObject state) {
		state.setAndSaveValidationResult(false, (state.getInputStep() == null) ? 0 : state.getInputStep().getActionSequence(), 0, AutoPilotConstants.ValidationFailed_UserTerminatedTestCase);
		finishTestCase(state);
	}

	public ValidationObject runPersistedTestCase(String strTestCaseID, String symbolStr, boolean isBenchMarkModeOn) {
		LFTestCase tcLocal = DBUtil.getInstance().getTcm().getTestcaseMap().get(strTestCaseID);
		if (tcLocal == null) {
			tcLocal = DBUtil.getInstance().getTestCase(strTestCaseID);
		}

		// this method is called when running batch testcases, therefore, don't take it slow
		return runTestCase(tcLocal, symbolStr, false, isBenchMarkModeOn);
	}

	public ValidationObject runTestCase(LFTestCase testCase, String symbolStr, boolean canTakeItSlow , boolean isBenchMarkModeOn) {
		ValidationObject state = loadState(testCase, symbolStr);
		while (state.hasNext()) {
			if (!runTestCaseNextStep(state, canTakeItSlow, isBenchMarkModeOn)) {
				finishTestCase(state);
				return state;
			}
		}

		passTestCase(state);
		finishTestCase(state);
		return state;
	}

	private String parseInputMessage(String strFix, String Symbol, LFTestCase tCase, int intCurrentInputStep, ValidationObject state) throws Exception {
		StringBuffer sBuff = new StringBuffer(ApplicationContext.getPlaceHolders().parsePlaceholders(strFix, true, tCase, intCurrentInputStep, Symbol, state));
		String strTemp = sBuff.toString();
		/*
		 * to enhance fix message
		 */
		if (ApplicationContext.getFIXFactory().getTagValue(strTemp, "8") != null) {
			/*
			 * if (ApplicationContext.getFIXFactory().getTagValue(strTemp, "55")==null) {
			 * sBuff.append("55="+Symbol+"\001"); } if (ApplicationContext.getFIXFactory().getTagValue(strTemp,
			 * "49")==null) { try {
			 * sBuff.append("49="+brokerFactory.getSenderCompID()+AutoPilotConstants.FIX_SEPERATOR);
			 * sBuff.append("50="+brokerFactory.getSenderSubID()+AutoPilotConstants.FIX_SEPERATOR); } catch (Exception
			 * ex) { ex.printStackTrace(); return (sBuff.toString()); } }
			 */

			// Overwrite the SenderCompID/SubID and symbol based on AutoPilot gui or config
			if (ApplicationContext.getFIXFactory().getTagValue(strTemp, "55") == null) {
				sBuff.append("55=" + Symbol + AutoPilotConstants.FIX_SEPERATOR);
			}

			if ((ApplicationContext.getFIXFactory().getTagValue(strTemp, "49") == null && !"AJ".equals(ApplicationContext.getFIXFactory().getTagValue(strTemp, "35"))) || ApplicationContext.getConfig().isOverwriteSendCompSubIDBasedOnConfig()) {
				try {
					AutoPilotBrokerInfoFactory brokerFactory = ApplicationContext.getBrokerFactory();
					sBuff.append("49=" + brokerFactory.getSenderCompID() + AutoPilotConstants.FIX_SEPERATOR);
					sBuff.append("50=" + brokerFactory.getSenderSubID() + AutoPilotConstants.FIX_SEPERATOR);
				} catch (Exception ex) {
					ex.printStackTrace();
					return (sBuff.toString());
				}
			} else {
				// If the message contains 49, but not 50 then generate 50 from the senderSubIDMapping
				if (ApplicationContext.getFIXFactory().getTagValue(strTemp, "50") == null) {
					sBuff.append("50=" + brokerFactory.getSenderSubID(ApplicationContext.getFIXFactory().getTagValue(strTemp, "49"), Symbol) + "");
				}
			}
		}

		return (sBuff.toString());
	}

	public String getAcceptorTopicNameByID(String strTopicID, String symbol) {
		String strTopicName = ConnectionManager.INSTANCE.getAcceptorTopic(symbol, strTopicID);

		if (strTopicName == null || (strTopicName.trim().length() < 1)) {
			logger.warning("cannot find the topic name for topicID:" + strTopicID);

			//TODO: DR45414 should be removed once TOB is retired
			// WARNING this is a terrible hack to make sockets work in EMEA - the socket logic should be rewritten!!!!!!!!
			if (strTopicID.equals("TOBtoAutoPilot")) {
				// returning for topicID if topicName is not found. For socket ID
				strTopicName = strTopicID;
			}
		}

		return strTopicName;
	}

	public void invokeCustValidation(String strCustValidationClassName, ValidationObject vObject, int intCurrentInputStep, String strCurrentInputMsg, String strCurrentOutputMsg) {
		try {
			logger.info("invokeCustValidation started|CustValidationClassName:" + strCustValidationClassName);
			logger.info(strCustValidationClassName + "|strCurrentInputMsg:" + strCurrentInputMsg);
			logger.info(strCustValidationClassName + "|strCurrentOutputMsg:" + strCurrentOutputMsg);

			if (strCustValidationClassName != null && strCustValidationClassName.trim().length() > 0) {

				Class<?> classValidate = Class.forName(strCustValidationClassName);
				Class<?> partypesCt[] = new Class[0];
				Constructor<?> ct = classValidate.getConstructor(partypesCt);
				Object arglist[] = new Object[0];
				AutoPilotValidator apValidator = (AutoPilotValidator) ct.newInstance(arglist);

				apValidator.validate(vObject, intCurrentInputStep, strCurrentInputMsg, strCurrentOutputMsg);

				if (!vObject.isSuccess()) {
					logger.info("\n\n\n");
					logger.info("*******************************************************************************");
					logger.warning("VALIDATIONFAILED|CUSTOMIZEDVALIDATION|CustValidationClassName:" + strCustValidationClassName + " | ResultMsg: " + vObject.getValidationResultMsg());
					logger.info("*******************************************************************************\n\n\n");
				} else {
					logger.warning("VALIDATIONSUCCEEDED|" + strCustValidationClassName);
				}

			}
			logger.info("invokeCustValidation finished.");

		} catch (ClassNotFoundException cnfFx) {
			// cnfFx.printStackTrace();
			vObject.setValidationResultStatus(false);
			vObject.setValidationResultMsg(AutoPilotConstants.ValidationFailed_CannotLoadCustomizedValidationClass + ":" + strCustValidationClassName);
			logger.info("\n\n\n");
			logger.info("*******************************************************************************");
			logger.warning("VALIDATIONFAILED|CUSTOMIZEDVALIDATION|CustValidationClassName:" + strCustValidationClassName + " | ResultMsg: " + vObject.getValidationResultMsg());
			logger.info("*******************************************************************************\n\n\n");
		} catch (Exception ex) {
			ex.printStackTrace();
			vObject.setValidationResultStatus(false);
			vObject.setValidationResultMsg(ex.getMessage());
			logger.info("\n\n\n");
			logger.info("*******************************************************************************");
			logger.warning("VALIDATIONFAILED|CUSTOMIZEDVALIDATION|CustValidationClassName:" + strCustValidationClassName + " | ResultMsg: " + vObject.getValidationResultMsg());
			logger.info("*******************************************************************************\n\n\n");
		}
	}

}
