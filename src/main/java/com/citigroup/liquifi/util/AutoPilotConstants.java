package com.citigroup.liquifi.util;

public interface AutoPilotConstants {
	
	public static final String MSG_TYPE_FIXMSG = "FixMsg";
	public static final String MSG_TYPE_XML = "XML";
	public static final String MSG_TYPE_CONTROL = "CONTROL"; // Same as XML, but for internal control
	public static final String MSG_TYPE_CONFIG = "CONFIG"; // Similar to XML

	public static final String CONTROL_REQUEST_TYPE_PAUSE = "Pause";
	
	public static final String FIX_SEPERATOR = "\001";

	public static final String PLACEHOLDER_CURRENTTIME = "@CURRENTTIME";
	public static final String PLACEHOLDER_TIMEPLUS = "@TIMEPLUS";
	public static final String PLACEHOLDER_TIME2PLUS = "@TIME2PLUS";
	public static final String PLACEHOLDER_DELIMITER = ";";
	public static final String PLACEHOLDER_ORDERID_DELIMITER = "#";

	
	public static final String PLACEHOLDER_currentDay = "@CURRENTDAY";
	public static final String PLACEHOLDER_sendingTime = "@SENDINGTIME";
	public static final String PLACEHOLDER_transactTime = "@TRANSACTTIME";
	public static final String PLACEHOLDER_quoteTime = "@QUOTETIME";
	public static final String PLACEHOLDER_CLORDID = "@CLORDID";
	public static final String PLACEHOLDER_ORDID = "@ORDID";
	public static final String PLACEHOLDER_SYMBOL = "@SYMBOL";
	public static final String PLACEHOLDER_UTI = "@UTI";
	public static final String PLACEHOLDER_TESTCASE ="@TESTCASE";
	public static final String PLACEHOLDER_MATCH = "@MATCH";
	public static final String PLACEHOLDER_NOTMATCH = "@NOTMATCH";
	public static final String PLACEHOLDER_ISSET = "@ISSET";
	public static final String PLACEHOLDER_ISNOTSET = "@ISNOTSET";
	public static final String PLACEHOLDER_UNSET = "@UNSET";
	public static final String PLACEHOLDER_NOTEQUALTO = "!=";
	public static final String PLACEHOLDER_REMOVE = "@REMOVE";
	public static final String PLACEHOLDER_FII = "@FII";
	
	
	public static final String PLACEHOLDER_OUTPUT = "@OP";
	public static final String PLACEHOLDER_INPUT = "@IP";
	public static final String PLACEHOLDER_APVAR = "@APVAR";
	
	public static final String PLACEHOLDER_REPEATING_GROUP = "@INJECT";
	public static final String PLACEHOLDER_ENV = "@ENV";

	
	public static final String PLACEHOLDER_APLIST = "-9999";
	public static final String PLACEHOLDER_SEPERATOR = "@";
	
	public static final String SEPERATOR_ARRAY_START = "[";
	public static final String SEPERATOR_ARRAY_END = "]";
	public static final String SEPERATOR_TAG_START = "(";
	public static final String SEPERATOR_TAG_END = ")";
	public static final String SEPERATOR_TAGLIST = ",";
	public static final String SEPERATOR_APVAR = ".";
	
	public static final String XMLSEPERATOR_START = "<";
	
	public static final String AutoPilotWarning_TestCaseDesign_InputStepIsEmpty = "AutoPilotWarning_TestCaseDesign_InputStepIsEmpty";
	public static final String AutoPilotWarning_TestCaseDesign_InvalidPlaceholder = "AutoPilotWarning_TestCaseDesign_InvalidPlaceholder";
	public static final String AutoPilotWarning_TestCaseDesign_CannotParseAPVariable = "AutoPilotWarning_TestCaseDesign_CannotParseAPVariable";
	
	public static final String AutoPilotWarning_TestCaseRuntime_CannoFindSpecifiedTagInReferedMsg = "AutoPilotWarning_TestCaseRuntime_CannoFindSpecifiedTagInReferedMsg";

	public static final String AutoPilotWarning_TestCaseDesign_PlaceholderNotFound = "AutoPilotWarning_TestCaseDesign_PlaceholderNotFound";
	public static final String AutoPilotWarning_PlaceholderParsingError = "AutoPilotWarning_PlaceholderParsingError";
	public static final String AutoPilotWarning_TestCaseDesign_InputstepCannotBeReferencedYet = "AutoPilotWarning_TestCaseDesign_InputstepCannotBeReferencedYet";

	public static final String AutoPilotError_CannotLoadTopicAcceptor = "AutoPilotConfigError_CannotLoadTopicAcceptor";
	public static final String AutoPilotError_CannotLoadTopicInitiator = "AutoPilotError_CannotLoadTopicInitiator";	

	public static final String ValidationFailed_UserTerminatedTestCase = "User terminated testcase";
	public static final String ValidationFailed_CannotProcessActualOutputMsg = "Cannot process received output msg";	
	public static final String ValidationFailed_CannotProcessInputMsg = "Cannot process input msg";	
	public static final String ValidationFailed_CannotLoadTestCase = "Cannot load testcase";	
	public static final String ValidationFailed_CannotLoadCustomizedValidationClass = "ValidationFailed_CannotLoadCustomizedValidationClass";
	public static final String ValidationFailed_CompletnessCheckFailed = "ValidationFailed_CompletnessCheckFailed";
	public static final String ValidationFailed_AdminCommandFailed = "Admin Command Failed";
	
	public static final String Error_CannotProcessControlMessage = "Error_CannotProcessControlMessage";	

	public static final String AUTOPILOT_PREFIX = "AP";
	public static final String USERID_AutoPilotServerMode = "AutoPilotServerMode";
	
	public static final int[] NumbericTagList = {14,32,38,151,31,44,6,99,12,211,132,133,134,135,111,110};
	public static final int[] RoundbaleTagList = {14,32,38,151,31,44,6};
	
	public static final String ComboBoxEmptyItem = " ";
	
	public static final String NO_MESSAGE = "NO_MESSAGE";
}
