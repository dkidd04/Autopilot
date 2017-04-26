package com.citigroup.liquifi.autopilot.validation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.citigroup.liquifi.autopilot.message.FIXMessage;
import com.citigroup.liquifi.autopilot.util.ValidationResult;
import com.citigroup.liquifi.util.AutoPilotConstants;

public enum ValidationManager {
	INSTANCE;

	private final ValidationResult validationSuccess = new ValidationResult(true, "Success");
	private final Pattern matchPattern = Pattern.compile("@.*?MATCH\\((.*?)\\)");
	private static final int[] NUMERIC_TAG_LIST = {14,32,38,151,31,44,6,99,12,211,132,133,134,135,111,110};
	private static final int[] ROUNDABLE_TAG_LIST = {14,32,38,151,31,44,6};
	
	
	public ValidationResult validate(String expectedMsgStr, String actualMsgStr) {
		if (expectedMsgStr == null || expectedMsgStr.equals("")) {
			return validationSuccess;
		} else if (actualMsgStr == null || actualMsgStr.equals("")) {
			return new ValidationResult(false, "Did not receieve output message");
		} else {
			return validate(new FIXMessage(expectedMsgStr), new FIXMessage(actualMsgStr));
		}
	}

	private ValidationResult validate(FIXMessage expectedMsg, FIXMessage actualMsg) {
		Set<String> keySet = expectedMsg.getTagMap().keySet();

		for (String tagID : keySet) {
			String tagVal1 = expectedMsg.getValue(tagID);
			String tagVal2 = actualMsg.getValue(tagID);
			
			// should not validate Tag9(the lenght of msg might change) and Tag8 (Fix version could be 4.4 or 4.2)
			if (tagID != null && (tagID.trim().equals("9") || tagID.trim().equals("8") || tagID.trim().equals("10"))) {
				continue;
			}

			boolean valid = true;
			
			if (isNumbericTag(tagID)) {
				valid = assertNumbericTagValue(tagVal1, tagVal2, isRounableTag(tagID));
			} else {
				valid = assertTagValue(tagVal1, tagVal2);
			}
			
			if (!valid) {
				return new ValidationResult(false, "Tag: " + tagID + ", Expected value: " + tagVal1 + ", Actual value: " + tagVal2);
			}
		}

		return validationSuccess;
	}

	private boolean assertTagValue(String expected, String receieved) {
		try {
			if (expected == null) {
				return false;
			}
			
			expected = expected.trim();
			
//			if(expected.length() == 0) {
//				return false;
//			}

			if (expected.equals(AutoPilotConstants.PLACEHOLDER_ISSET)) {
				if (receieved != null && receieved.trim().length() != 0) {
					return true;
				}
			} else if (expected.equals(AutoPilotConstants.PLACEHOLDER_ISNOTSET)) {
				if (receieved == null || receieved.trim().length() == 0) {
					return true;
				}
			} else if (expected.startsWith(AutoPilotConstants.PLACEHOLDER_NOTEQUALTO)) {
				String strVal = expected.replace(AutoPilotConstants.PLACEHOLDER_NOTEQUALTO, "");
				if (!expected.equalsIgnoreCase(strVal.trim())) {
					return true;
				}
			} else if (expected.startsWith(AutoPilotConstants.PLACEHOLDER_MATCH) || expected.startsWith(AutoPilotConstants.PLACEHOLDER_NOTMATCH)) {
				Matcher m = matchPattern.matcher(expected);
				
				if (m.find() && m.groupCount() == 1) {
					String pattern = m.group(1);
					Pattern userDefinedPattern = Pattern.compile(pattern);
					
					Matcher m2 = userDefinedPattern.matcher(receieved);
					if(m2.find()) {
						if(expected.startsWith(AutoPilotConstants.PLACEHOLDER_MATCH)) {
							return true;
						} else {
							return false;
						}
					} else {
						if(expected.startsWith(AutoPilotConstants.PLACEHOLDER_NOTMATCH)) {
							return true;
						} else {
							return false;
						}
					}
				}
			} else if (receieved != null && expected.equalsIgnoreCase(receieved.trim())) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return false;
	}

	private boolean assertNumbericTagValue(String expected, String receieved, boolean round) {
		try {
			if (expected == null) {
				return false;
			}
			
			expected = expected.trim();
			
			if(expected.length() == 0) {
				return false;
			}

			if (expected.equals(AutoPilotConstants.PLACEHOLDER_ISSET)) {
				if (receieved != null && receieved.trim().length() != 0) {
					return true;
				}
			} else if (expected.trim().equals(AutoPilotConstants.PLACEHOLDER_ISNOTSET)) {
				if (receieved == null || receieved.trim().length() == 0) {
					return true;
				}
			} else if (expected.startsWith(AutoPilotConstants.PLACEHOLDER_NOTEQUALTO)) {
				String strVal = expected.replace(AutoPilotConstants.PLACEHOLDER_NOTEQUALTO, "");
				Double double1 = Double.valueOf(strVal);
				Double double2 = Double.valueOf(receieved.trim());
				if (!double1.equals(double2)) {
					return true;
				}
			} else if (receieved != null) {
				Double double1 = Double.valueOf(expected);
				Double double2 = Double.valueOf(receieved.trim());

				if (round) {
					BigDecimal rounded = new BigDecimal(expected, new MathContext(8, RoundingMode.HALF_UP));
					double1 = rounded.doubleValue();

					rounded = new BigDecimal(receieved.trim(), new MathContext(8, RoundingMode.HALF_UP));
					double2 = rounded.doubleValue();
				}

				if (double1.equals(double2)) {
					return true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return false;
	}

	private boolean isNumbericTag(String strTagID) {
		int[] NumericTags = NUMERIC_TAG_LIST;
		for (int i = 0; i < NumericTags.length; i++) {
			if (strTagID.equals(String.valueOf(NumericTags[i])))
				return true;
		}

		return false;
	}

	public boolean isRounableTag(String strTagID) {
		int[] NumericTags = ROUNDABLE_TAG_LIST;
		for (int i = 0; i < NumericTags.length; i++) {
			if (strTagID.equals(String.valueOf(NumericTags[i])))
				return true;
		}

		return false;
	}

}
