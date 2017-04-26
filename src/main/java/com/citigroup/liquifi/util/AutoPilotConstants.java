package com.citigroup.liquifi.util;

public class AutoPilotConstants {
	
	public static final String MSG_TYPE_FIXMSG = "FixMsg";
	public static final String MSG_TYPE_XML = "XML";
	public static final String MSG_TYPE_CONTROL = "CONTROL"; // Same as XML, but for internal control
	public static final String MSG_TYPE_CONFIG = "CONFIG"; // Similar to XML

	public static final String CONTROL_REQUEST_TYPE_PAUSE = "Pause";
	
	public static final String FIX_SEPERATOR = "\001";

	public static final String PLACEHOLDER_CURRENTTIME = "@CURRENTTIME";
	public static final String PLACEHOLDER_TIMEPLUS = "@TIMEPLUS";
	public static final String PLACEHOLDER_TIME2PLUS = "@TIME2PLUS";
	public static final String PLACEHOLDER_TIMESTAMP_HHMMSS = "@TIMESTAMP_HHmmss";
	public static final String PLACEHOLDER_ORDERID_DELIMITER = "#";

	
	public static final String PLACEHOLDER_CURRENT_DAY = "@CURRENTDAY";
	public static final String PLACEHOLDER_SENDING_TIME = "@SENDINGTIME";
	public static final String PLACEHOLDER_TRANSACT_TIME = "@TRANSACTTIME";
	public static final String PLACEHOLDER_QUOTE_TIME = "@QUOTETIME";
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
	public static final String PLACEHOLDER_VALID_UNTIL = "@VALIDUNTIL";
	public static final String PLACEHOLDER_REPEATING_GROUP = "@INJECT";
	public static final String PLACEHOLDER_ENV = "@ENV";

	
	public static final String PLACEHOLDER_APLIST = "-9999";
	public static final String PLACEHOLDER_SEPERATOR = "@";
	
	public static final String SEPERATOR_ARRAY_START = "[";
	public static final String SEPERATOR_ARRAY_END = "]";
	public static final String SEPERATOR_TAGLIST = ",";
	public static final String SEPERATOR_APVAR = ".";
	
	public static final String WARN_INPUT_EMPTY = "AutoPilotWarning_TestCaseDesign_InputStepIsEmpty";
	public static final String WARN_INVALID_PLACEHOLDER = "AutoPilotWarning_TestCaseDesign_InvalidPlaceholder";
	public static final String WARN_CANNOT_PARSE_APVAR = "AutoPilotWarning_TestCaseDesign_CannotParseAPVariable";
	
	public static final String WARN_CANNOT_FIND_TAG = "AutoPilotWarning_TestCaseRuntime_CannoFindSpecifiedTagInReferedMsg";

	public static final String WARN_CANNOT_BE_REFERENCED = "AutoPilotWarning_TestCaseDesign_InputstepCannotBeReferencedYet";

	public static final String ERROR_CANNOT_CREATE_TOPIC = "AutoPilotError_CannotLoadTopicInitiator";	

	public static final String FAIL_USER_TERMINATE = "User terminated testcase";
	public static final String FAIL_CANNOT_PROCESS_INPUT = "Cannot process input msg";	
	public static final String FAIL_CANNOT_LOAD_TESTCASE = "Cannot load testcase";	
	public static final String FAIL_CANNOT_LOAD_CUSTOM_VALIDATION = "ValidationFailed_CannotLoadCustomizedValidationClass";
	public static final String FAIL_ADMIN_COMMAND = "Admin Command Failed";
	
	public static final String ERROR_CANNOT_PROCESS_CONTROL = "Error_CannotProcessControlMessage";	

	public static final String AUTOPILOT_PREFIX = "AP";
	
	public static final String EMPTY_COMBO_STRING = " ";

	
	private AutoPilotConstants(){}
	
}
