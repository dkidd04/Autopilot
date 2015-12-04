package com.citigroup.liquifi.autopilot.validation;

import com.citigroup.liquifi.autopilot.controller.ValidationObject;

public interface AutoPilotValidator {
	public void validate (ValidationObject vObject, int intCurrentInputStep, String strCurrentInputMsg, String strCurrentOutputMsg);
}

