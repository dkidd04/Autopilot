package com.citigroup.liquifi.autopilot.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.citigroup.get.util.date.DateUtil;
import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.controller.JSONFieldManipulator;
import com.citigroup.liquifi.autopilot.controller.ValidationObject;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.autopilot.message.FIXMessage;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.Tag;
import com.citigroup.liquifi.util.AutoPilotConstants;
import com.citigroup.liquifi.util.UniqueId;

public class PlaceHolders {
	private AceLogger logger = AceLogger.getLogger(this.getClass().getSimpleName());
	private UniqueId ID = null;
	private Pattern inputPattern = Pattern.compile("@IP\\[([\\d]+)\\]\\.(get.*?)\\(([-?\\w., ]+)\\)");
	private Pattern outputPattern = Pattern.compile("@OP\\[([\\d]+)\\]\\[([\\d]+)\\]\\.(get.*?)\\(([-?\\w., ]+)\\)");
	private Pattern replacePattern = Pattern.compile("@.*?\\.replace\\('(.*?)',.*?'(.*?)'\\)");
	private static final String ENV_FUNCTION = AutoPilotConstants.PLACEHOLDER_ENV + ".value(";
	private static Map<String,String> symfiiMap = new HashMap<String,String>();
	private PlaceHolders(UniqueId ID) {
		this.ID = ID;
	}

	public static void addSymFiiMap(String symbol){
		symfiiMap.put(symbol, ApplicationContext.getProductApiUtil().getFiiStr(symbol));
	}

	public String parseAPVarPlaceholdersString(String str, Set<Tag> overwriteTags) {
		Map<String, String> apvarMapping = getAPVARMapping(overwriteTags);
		try {
			int start = -1;
			while ((start = str.indexOf(AutoPilotConstants.PLACEHOLDER_APVAR)) > -1) {
				int end = str.indexOf(")", start) + 1;
				String val = str.substring(start, end);
				
				String strAPVarKey = getAPVARPlaceholderName(val);
				String strAPVarVal = apvarMapping.get(strAPVarKey);

				if (strAPVarVal == null || strAPVarVal.trim().length() < 1) {
					logger.warning(AutoPilotConstants.WARN_CANNOT_PARSE_APVAR + " strAPVarKey:" + strAPVarKey + " strAPVarVal:" + strAPVarVal);
					throw new Exception("No linking found " + val);
				} else {
					val = val.replace(strAPVarKey, strAPVarVal);
					str = str.substring(0, start) + val + str.substring(end);
				}

			}

		} catch (Exception ex) {
			logger.warning(AutoPilotConstants.WARN_CANNOT_PARSE_APVAR);
			return str;
		}

		updateAPVARPlaceholderValuesOnOverwriteTagsList(overwriteTags, apvarMapping);

		return str;
	}

	private String getAPVARPlaceholderName(String val) {
		String strAPVarKey;
		if (val.contains(AutoPilotConstants.SEPERATOR_APVAR)) {
			strAPVarKey = val.substring(0, val.indexOf(AutoPilotConstants.SEPERATOR_APVAR));
		} else {
			strAPVarKey = val;
		}
		return strAPVarKey;
	}

	private void updateAPVARPlaceholderValuesOnOverwriteTagsList(Set<Tag> overwriteTags, Map<String, String> apvarMapping) {
		for(Tag overwriteTag : overwriteTags){
			String value = overwriteTag.getTagValue();
			if(value.startsWith(AutoPilotConstants.PLACEHOLDER_APVAR)){
				String apVarPlaceholderName = getAPVARPlaceholderName(value);
				if(apvarMapping.containsKey(apVarPlaceholderName)){
					overwriteTag.setTagValue(value.replace(apVarPlaceholderName, apvarMapping.get(apVarPlaceholderName)));
				}
			}
		}
	}

	private Map<String, String> getAPVARMapping(Set<Tag> overwriteTags) {
		Map<String, String> apvarMapping = new HashMap<>();
		Iterator<Tag> overwriteTagIterator = overwriteTags.iterator();
		while(overwriteTagIterator.hasNext()){
			Tag overwriteTag = overwriteTagIterator.next();
			String value = overwriteTag.getTagValue();
			String tagName = overwriteTag.getTagID();
			if (tagName.startsWith(AutoPilotConstants.PLACEHOLDER_APVAR)){
				apvarMapping.put(tagName, value);
				overwriteTagIterator.remove();
			}
		}
		return apvarMapping;
	}

	public String parseRepeatingGroup(String strFixMessage){

		List<String> repeatingGroupList = new ArrayList<String>();

		FIXMessage fixMsgToReturn = null;

		FIXMessage fixMsgToParse = new FIXMessage(strFixMessage);
		fixMsgToReturn = new FIXMessage();

		LinkedHashMap<String, String> tagMapTemp = fixMsgToParse.getTagMap();
		Iterator<String> it = tagMapTemp.keySet().iterator();

		while (it.hasNext()) {
			String key = it.next();
			String val = tagMapTemp.get(key);

			if (key.startsWith(AutoPilotConstants.PLACEHOLDER_REPEATING_GROUP)) {
				repeatingGroupList.add(val);
			}else{
				fixMsgToReturn.setValue(key, val);
			}
		}

		String result = fixMsgToReturn.toString();
		for(String repeatingGroupStr : repeatingGroupList){
			int i = fixMsgToReturn.toString().lastIndexOf("10=000");
			result = result.substring(0, i) + repeatingGroupStr + AutoPilotConstants.FIX_SEPERATOR + result.substring(i);
		}

		return result;
	}

	public String parseAPVarPlaceholders(String strFixMessage) {

		FIXMessage fixMsgToReturn = null;

		try {
			FIXMessage fixMsgToParse = new FIXMessage(strFixMessage);
			fixMsgToReturn = new FIXMessage(strFixMessage);

			LinkedHashMap<String, String> tagMapTemp = fixMsgToParse.getTagMap();
			Iterator<String> it = tagMapTemp.keySet().iterator();

			/*
			 * find the APVariable and replace with the value set in overwrite tag list
			 */
			while (it.hasNext()) {
				try {

					String key = it.next();
					String val = tagMapTemp.get(key);


					val = parseAPVarPlaceholderValue(tagMapTemp, fixMsgToReturn, key, val);

					if(val != null) {
						fixMsgToReturn.setValue(key, val);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					logger.warning(AutoPilotConstants.WARN_CANNOT_PARSE_APVAR);
					continue;
					// return strToReturn;
				}

			}

			/*
			 * remove the APVariable tag in the overwrite tag list
			 */
			Iterator<String> it2 = tagMapTemp.keySet().iterator();

			while (it2.hasNext()) {

				try {
					String key = it2.next();
					if (key.startsWith(AutoPilotConstants.PLACEHOLDER_APVAR)) {
						fixMsgToReturn.unsetValue(key);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					logger.warning(AutoPilotConstants.WARN_CANNOT_PARSE_APVAR);
					continue;
				}

			}

		} catch (Exception ex) {
			logger.warning(AutoPilotConstants.WARN_CANNOT_PARSE_APVAR);
			return strFixMessage;
		}

		return fixMsgToReturn.toString();
	}


	/**
	 * 
	 * @param tagMapTemp
	 * @param fixMsgToReturn
	 * @param key
	 * @param val
	 */
	private String parseAPVarPlaceholderValue(final LinkedHashMap<String, String> tagMapTemp, FIXMessage fixMsgToReturn, final String key, String val) {


		if (val.startsWith(AutoPilotConstants.PLACEHOLDER_APVAR)) {

			StringBuilder b = new StringBuilder();

			String [] parts = val.split("\\+");

			for(String part : parts) {

				part = part.replaceAll("\"", "");
				part = part.trim();				

				String v = parseAPVarPlaceholderSingleValue(tagMapTemp, fixMsgToReturn, key, part);

				if(v != null) {

					b.append( v );
					b.append( "+" );
				}

			}


			if(b.length() > 0 && b.charAt( b.length() -1 ) == '+') {
				b.setLength( b.length() - 1 );
			}

			String s = b.toString();  

			return s;

		}

		return null;
	}

	/**
	 * 
	 * @param tagMapTemp
	 * @param fixMsgToReturn
	 * @param key
	 * @param val
	 * @return
	 */
	private String parseAPVarPlaceholderSingleValue(final LinkedHashMap<String, String> tagMapTemp, FIXMessage fixMsgToReturn, final String key, String val) {

		String strAPVarKey = "";
		String strAPVarVal = "";		


		if (val.startsWith(AutoPilotConstants.PLACEHOLDER_APVAR)) {

			if (ApplicationContext.getConfig().isDebug()) {
				logger.info("Found APVarible: " + val);
			}

			strAPVarKey = getAPVARPlaceholderName(val);

			strAPVarVal = tagMapTemp.get(strAPVarKey);

			if ((strAPVarVal == null) || strAPVarVal.trim().length() < 1) {
				logger.warning(AutoPilotConstants.WARN_CANNOT_PARSE_APVAR + " strAPVarKey:" + strAPVarKey + " strAPVarVal:" + strAPVarVal);
				return null;
			} 

			val = val.replace(strAPVarKey, strAPVarVal);

			if (ApplicationContext.getConfig().isDebug()) {
				logger.info("Parsed APVariable. strAPVarKey:" + strAPVarKey + " strAPVarVal:" + strAPVarVal);
			}

		}

		return val;
	}	

	/**
	 * 
	 * @param strFixMessage
	 * @param isFIX
	 * @param tCase
	 * @param intCurrentInputStep
	 * @param strSymbol
	 * @param rOutputLocal
	 * @return
	 * @throws Exception
	 */
	public String parsePlaceholders(String strFixMessage, boolean isFIX, LFTestCase tCase, int intCurrentInputStep, String strSymbol, ValidationObject rOutputLocal) throws Exception {
		HashMap<String, String> cache = new HashMap<>();

		if (isFIX) {
			strFixMessage = parseAPVarPlaceholders(strFixMessage);
		}

		Pattern placeholderPattern = Pattern.compile("(@.*?)[\"<;]");
		Matcher m = placeholderPattern.matcher(strFixMessage);

		while (m.find()) {
			String strPlaceholderpattern = m.group(1);
			String replacementStr = "";

			if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_ORDID)) {
				/*
				 * support the sequencial orderID and clientOrderID. e.g.: FIXNEW: 37=AP-123, 11=AP-123#0; FIXMOD.
				 * 37=AP-123, 11=AP-123#1, 41=AP-123#0 on the same inputstep, if there is a @ORDID already parsed and
				 * stored in the cache, use the id in the cache instead of regenerate
				 */
				int intOrderIDDelimiter = strPlaceholderpattern.indexOf(AutoPilotConstants.PLACEHOLDER_ORDERID_DELIMITER);
				if (intOrderIDDelimiter != -1) {
					strPlaceholderpattern = strPlaceholderpattern.substring(0, intOrderIDDelimiter);
				}

				if (cache.containsKey(AutoPilotConstants.PLACEHOLDER_ORDID)) {
					replacementStr = cache.get(AutoPilotConstants.PLACEHOLDER_ORDID);
				} else {
					replacementStr = AutoPilotConstants.AUTOPILOT_PREFIX + "-" + ID.generate(9);
					cache.put(AutoPilotConstants.PLACEHOLDER_ORDID, replacementStr);
				}
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_CLORDID)) {
				// on the same inputstep, if there is a @CLORDID already parsed and stored in the cache, use the
				// id in the cache instead of regenerate
				if (cache.containsKey(AutoPilotConstants.PLACEHOLDER_CLORDID)) {
					replacementStr = cache.get(AutoPilotConstants.PLACEHOLDER_CLORDID);
				} else {
					replacementStr = AutoPilotConstants.AUTOPILOT_PREFIX + "-" + ID.generate(9);
					cache.put(AutoPilotConstants.PLACEHOLDER_CLORDID, replacementStr);
				}

			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_SYMBOL)) {
				Matcher m2 = replacePattern.matcher(strPlaceholderpattern);

				if(m2.find()) {
					String what = m2.group(1);
					String with = m2.group(2);

					replacementStr = strPlaceholderpattern.replace(strPlaceholderpattern, strSymbol.replace(what, with));

				} else {
					replacementStr = strPlaceholderpattern.replace(AutoPilotConstants.PLACEHOLDER_SYMBOL, strSymbol);
				}
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_UTI)) {
				if (cache.containsKey(AutoPilotConstants.PLACEHOLDER_UTI)) {
					replacementStr = cache.get(AutoPilotConstants.PLACEHOLDER_UTI);
				} else {
					replacementStr = AutoPilotConstants.AUTOPILOT_PREFIX + "UTI-" + ID.generate(10);
					cache.put(AutoPilotConstants.PLACEHOLDER_UTI, replacementStr);
				}
			}else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_FII)) {
				if(symfiiMap.containsKey(strSymbol))
					replacementStr = symfiiMap.get(strSymbol);
				else{	
					symfiiMap.put(strSymbol, ApplicationContext.getProductApiUtil().getFiiStr(strSymbol));
					replacementStr = symfiiMap.get(strSymbol);
				}
			}else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_SENDING_TIME)) {
				replacementStr = printCurrentTimePlus(0);
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_TRANSACT_TIME)) {
				replacementStr = printCurrentTimePlus(0);
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_QUOTE_TIME)) {
				replacementStr = printCurrentTimePlus(120000000);
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_CURRENTTIME)) {
				replacementStr = printCurrentTimePlus(0);
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_CURRENT_DAY)) {
				replacementStr = printCurrentDay();
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_VALID_UNTIL)) {
				replacementStr = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS").format(new Timestamp(System.currentTimeMillis()+7200000));
			}else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_TIMEPLUS)) {
				int futureMillis = 500;
				int timeToAddMillis = getTimeToAddMillis(strPlaceholderpattern, futureMillis);

				replacementStr = printCurrentTimePlus(timeToAddMillis);
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_TIME2PLUS)) {
				int futureMillis = 500;
				int timeToAddMillis = getTimeToAddMillis(strPlaceholderpattern, futureMillis);

				replacementStr = printCurrentTime2Plus(timeToAddMillis);
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_TIMESTAMP_HHMMSS)) {
				int futureMillis = 0;
				int timeToAddMillis = getTimeToAddMillis(strPlaceholderpattern, futureMillis);

				replacementStr = currentTimeHHmmssPlus(timeToAddMillis);
			}	else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_TESTCASE)) {
				replacementStr = tCase.getName();
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_REMOVE)) {
				// find the end of the previous fix tag's value
				int placeholderStart = strFixMessage.indexOf(strPlaceholderpattern);
				int placeholderEnd = placeholderStart + strPlaceholderpattern.length();

				String reverseFix = new StringBuffer(strFixMessage).reverse().toString();
				String reversePlaceHolder = new StringBuffer(AutoPilotConstants.PLACEHOLDER_REMOVE).reverse().toString();
				int reverseStart = reverseFix.indexOf(reversePlaceHolder, strFixMessage.length() - placeholderEnd);
				int reverseEnd = reverseFix.indexOf(AutoPilotConstants.FIX_SEPERATOR, reverseStart);
				int fixTagStart = strFixMessage.length() - reverseEnd;

				String fixTag = strFixMessage.substring(fixTagStart, placeholderStart - 1);
				strFixMessage = strFixMessage.replace(AutoPilotConstants.FIX_SEPERATOR + fixTag + "=" + strPlaceholderpattern, "");
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_OUTPUT)) {

				replacementStr = parsePlaceHolderOutput(strFixMessage, intCurrentInputStep, strPlaceholderpattern, rOutputLocal);

			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_INPUT)) {

				replacementStr = parsePlaceHolderInput(strFixMessage, intCurrentInputStep, rOutputLocal, strPlaceholderpattern);

			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_ENV)){
				replacementStr = handleEnvPlaceholder(strFixMessage, strPlaceholderpattern);
			} else if (strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_REPEATING_GROUP)){
				// do nothing, will be handled later
			}else {
				logger.warning("unable to parse placeholder|" + strPlaceholderpattern);
			}

			// replace the strToReturn based the pattern found in strToBeParsed and update the strToBeParsed
			if (replacementStr != null && replacementStr.length() > 0) {
				strFixMessage = StringUtils.replaceOnce(strFixMessage, strPlaceholderpattern, replacementStr);
			} else {
				if (!strPlaceholderpattern.equals(AutoPilotConstants.PLACEHOLDER_ISSET) && !strPlaceholderpattern.equals(AutoPilotConstants.PLACEHOLDER_ISNOTSET) && !strPlaceholderpattern.equals(AutoPilotConstants.PLACEHOLDER_NOTEQUALTO) && !strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_MATCH) && !strPlaceholderpattern.startsWith(AutoPilotConstants.PLACEHOLDER_NOTMATCH)) {
					logger.warning(AutoPilotConstants.WARN_INVALID_PLACEHOLDER + " : " + strPlaceholderpattern);
				}
			}
		}

		// remove the AutoPilot special tag -9999
		if (strFixMessage.indexOf(AutoPilotConstants.PLACEHOLDER_APLIST) != -1) {
			strFixMessage = strFixMessage.replaceAll(AutoPilotConstants.PLACEHOLDER_APLIST + "=", "");
		}

		// unset tag that is value tagID=@UNSET
		int i = -1;
		while ((i = strFixMessage.indexOf(AutoPilotConstants.PLACEHOLDER_UNSET)) > -1) {
			;
			int start = strFixMessage.lastIndexOf("", i);
			strFixMessage = strFixMessage.substring(0, start) + strFixMessage.substring(i + 6);
		}

		return strFixMessage;
	}

	public String parsePlaceHolderInput(String strFixMessage, int intCurrentInputStep, ValidationObject rOutputLocal, String strPlaceholderpattern) {
		// @IP[{8}].{getTags}({55,76}) => three matches: 
		// 1: 8, 2: getTags, 3: 55,76
		// @IP[{8}].{getXMLFieldText}({clordId}) => three matches: 
		// 1: 8, 2: getXMLFieldText, 3: clordId
		String replacementStr=null;
		Matcher m2 = inputPattern.matcher(strPlaceholderpattern);
		InputFunctionType input = new InputFunctionType(m2);

		if (input.isValid()) {

			int inputStep = input.getInputStep();
			if (intCurrentInputStep < inputStep) {
				logger.warning(AutoPilotConstants.WARN_CANNOT_BE_REFERENCED + ". CurrentInputStep/InputStepReferenced:" + intCurrentInputStep + "/" + inputStep);
			}

			String strActualInputMsg = rOutputLocal.getInboundStep(inputStep).message;
			if(isFixMessageFunction(input.getFunctionName()) ){
				replacementStr = handleFixMessageFunctions(strFixMessage, strActualInputMsg, input);
			} else {
				replacementStr = handleNonFixMessageFunctions(strActualInputMsg, input);
			}
		}
		return replacementStr;
	}

	private int getTimeToAddMillis(String strPlaceholderpattern, int defaultTimeToAddMillis) {
		int futureMillis=0;
		int start = -1;
		if (strPlaceholderpattern.contains("+")) {
			start = strPlaceholderpattern.indexOf("+") + 1;
		} else if (strPlaceholderpattern.contains("-")) {
			start = strPlaceholderpattern.indexOf("-");
		}

		if (start > -1) {
			futureMillis = Integer.parseInt(strPlaceholderpattern.substring(start, strPlaceholderpattern.length()));
		}

		return futureMillis==0 ? defaultTimeToAddMillis : futureMillis;
	}

	private String handleEnvPlaceholder(String strFixMessage, String strPlaceholderpattern) {
		//retrieve env variable to replace
		if(!isValidEnvFunction(strPlaceholderpattern)){
			return null;
		}

		String envVariable = strPlaceholderpattern.substring(ENV_FUNCTION.length(), strPlaceholderpattern.lastIndexOf(")"));
		Map<String, String> envMap = new HashMap<>();
		if(envMap.containsKey(envVariable)){
			return envMap.get(envVariable);
		}
		return System.getenv(envVariable);
	}

	private boolean isValidEnvFunction(String strPlaceholderpattern) {
		return strPlaceholderpattern.startsWith(ENV_FUNCTION) && strPlaceholderpattern.endsWith(")");
	}

	private boolean isFixMessageFunction(String getType) {
		return getType.matches("getTag(s|List)?");
	}

	/**
	 * 
	 * @param strFixMessage
	 * @param intCurrentInputStep
	 * @param strPlaceholderpattern
	 * @param rOutputLocal
	 * @return
	 * @throws Exception
	 */
	public String parsePlaceHolderOutput (final String strFixMessage, final int intCurrentInputStep, final String strPlaceholderpattern, final ValidationObject rOutputLocal) {


		String [] parts = strPlaceholderpattern.split("\\+");

		StringBuilder b = new StringBuilder();

		for(String part : parts) {

			part = part.replaceAll("\"", "");
			part = part.trim();

			String s = parsePlaceHolderOutputA(strFixMessage, intCurrentInputStep, part, rOutputLocal);

			if(s != null) {
				b.append( s );
			}

		}

		return b.toString();

	}


	/**
	 * 
	 * @param strFixMessage
	 * @param intCurrentInputStep
	 * @param strPlaceholderpattern
	 * @param rOutputLocal
	 * @return
	 * @throws Exception
	 */
	private String parsePlaceHolderOutputA (final String strFixMessage, final int intCurrentInputStep, final String strPlaceholderpattern, final ValidationObject rOutputLocal) {		


		// @OP[{8}][{3}].{getTags}({55,76}) => four matches: 
		// 1: 8, 2: 3, 3: getTags, 4: 55,76
		// @OP[{8}][{3}].{getXMLFieldText}({clordId}) => four matches: 
		// 1: 8, 2: 3, 3: getXMLFieldText, 4: clordId
		Matcher m2 = outputPattern.matcher(strPlaceholderpattern);
		OutputFunctionType outputStep = new OutputFunctionType(m2);

		if (!outputStep.isValid()) {
			logger.warning("unable to parse placeholder ("+strPlaceholderpattern+")");
			return strPlaceholderpattern;
		}

		int intInputStep = outputStep.getInputStep();
		int intOutputMsgID = outputStep.getOutputStep();

		if (intCurrentInputStep < intInputStep) {
			logger.warning(AutoPilotConstants.WARN_CANNOT_BE_REFERENCED + ". CurrentInputStep/InputStepReferenced:" + intCurrentInputStep + "/" + intInputStep);
			return null;
		}

		String strResultedOutputMsg = rOutputLocal.getOutputMsgByStepAndOutputMsgID(intInputStep, intOutputMsgID);

		if(strResultedOutputMsg == null) {
			throw new IllegalArgumentException("linkage is wrong ("+strPlaceholderpattern+")");
		}

		String getType = outputStep.getFunctionName();
		if(isFixMessageFunction(getType)){
			return handleFixMessageFunctions(strFixMessage, strResultedOutputMsg, outputStep); 
		} else {
			return handleNonFixMessageFunctions(strResultedOutputMsg, outputStep);
		}
	}

	private String handleNonFixMessageFunctions(String referenceMessage, FunctionType function) {
		String functionName = function.getFunctionName();
		String tag = function.getFunctionValue();
		if (functionName.equals("getXMLFieldText")){
			return ApplicationContext.getXmlFactory().getField(referenceMessage, tag);
		} else if (functionName.equals("getJSONFieldText")){
			return new JSONFieldManipulator().getFieldValueForJSONObject(referenceMessage, tag);
		}
		return null;
	}

	private String handleFixMessageFunctions(final String currentFixMessage, String referenceMessage, FunctionType function) {
		String functionName = function.getFunctionName();
		String tag = function.getFunctionValue();
		if (functionName.equals("getTag")) {
			return ApplicationContext.getFIXFactory().getTagValue(referenceMessage, tag);
		} else if (functionName.equals("getTags") || functionName.equals("getTagList")) {
			String[] strTagIDArray = tag.split(AutoPilotConstants.SEPERATOR_TAGLIST);
			StringBuffer sBuffReplaceLocal = new StringBuffer("");
			String strTagIDLocal = "";
			String strTagValueLocal = "";

			boolean shouldAppendSeperator = false;

			for (int arrayIndex = 0; arrayIndex < strTagIDArray.length; arrayIndex++) {

				strTagIDLocal = strTagIDArray[arrayIndex];
				strTagValueLocal = ApplicationContext.getFIXFactory().getTagValue(referenceMessage, strTagIDLocal);

				/*
				 * if strTagIDLocal is already in the message, do not overwrite using reference message
				 */
				String strCheckExistingTag = ApplicationContext.getFIXFactory().getTagValue(currentFixMessage, strTagIDLocal);
				if (strCheckExistingTag != null && strCheckExistingTag.trim().length() != 0) {
					logger.info("The tag is already in the message, do not overwrite using reference message. Tag:" + strTagIDLocal);
					continue;
				}

				if (strTagValueLocal != null && strTagValueLocal.length() > 0) {
					// remove the FIX_SEPERATOR '' in the end of replaced string
					if (shouldAppendSeperator)
						sBuffReplaceLocal.append(AutoPilotConstants.FIX_SEPERATOR);

					sBuffReplaceLocal.append(strTagIDLocal);
					sBuffReplaceLocal.append("=");
					sBuffReplaceLocal.append(strTagValueLocal);
					shouldAppendSeperator = true;
				} else {
					logger.warning(AutoPilotConstants.WARN_CANNOT_FIND_TAG + " Tag:" + strTagIDLocal);
					continue;
				}
			}

			return sBuffReplaceLocal.toString();
		}
		return null;
	}

	ThreadLocal<SimpleDateFormat> hourMinuteSecondDateFormat = new ThreadLocal<SimpleDateFormat>(){
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm:ss");
		}
	};
	private String currentTimeHHmmssPlus(int millisToAdd) {
		return timestampPlus(hourMinuteSecondDateFormat.get(), millisToAdd);
	}

	private String timestampPlus(SimpleDateFormat dateFormat, int millisToAdd){
		return dateFormat.format(ApplicationContext.getClock().currentTimeMillis() + millisToAdd);
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd:HH:mm:ss.SSS");
	private String printCurrentTimePlus(int plus) {
		synchronized(sdf) {
			return sdf.format(DateUtil.convertToGMTDate(ApplicationContext.getClock().currentTimeMillis() + plus));
		}
	}

	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss.SSS");
	private String printCurrentTime2Plus(int plus) {
		synchronized(sdf2) {
			return sdf2.format(ApplicationContext.getClock().currentTimeMillis() + plus);
		}
	}

	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
	private String printCurrentDay() {
		synchronized(sdf3) {
			return sdf3.format(ApplicationContext.getClock().currentTimeMillis());
		}
	}
}

interface FunctionType {

	public String getFunctionName();
	public String getFunctionValue();

}

class InputFunctionType implements FunctionType {

	private final int inputStep;
	private final String type;
	private final String tagID;
	private final boolean isValid;

	public InputFunctionType(Matcher matchResult){
		isValid = (matchResult.find() && matchResult.groupCount() == 3);
		if(isValid){
			inputStep = Integer.valueOf(matchResult.group(1));
			type = matchResult.group(2);
			tagID = matchResult.group(3);
		} else {
			inputStep = 0;
			type = null;
			tagID = null;
		}
	}

	public int getInputStep() {
		return inputStep;
	}

	public String getFunctionName() {
		return type;
	}

	public String getFunctionValue() {
		return tagID;
	}

	public boolean isValid() {
		return isValid;
	}

}

class OutputFunctionType implements FunctionType {

	private final int inputStep;
	private final int outputStep;
	private final String functionName;
	private final String functionValue;
	private final boolean isValid;

	public OutputFunctionType(Matcher matchResult){
		isValid = (matchResult.find() && matchResult.groupCount() == 4);
		if(isValid){
			inputStep = Integer.valueOf(matchResult.group(1));
			outputStep = Integer.valueOf(matchResult.group(2));
			functionName = matchResult.group(3);
			functionValue = matchResult.group(4);
		} else {
			inputStep = 0;
			outputStep = 0;
			functionName = null;
			functionValue = null;
		}
	}

	public int getInputStep() {
		return inputStep;
	}

	public int getOutputStep() {
		return outputStep;
	}

	public String getFunctionName() {
		return functionName;
	}

	public String getFunctionValue() {
		return functionValue;
	}

	public boolean isValid() {
		return isValid;
	}

}
