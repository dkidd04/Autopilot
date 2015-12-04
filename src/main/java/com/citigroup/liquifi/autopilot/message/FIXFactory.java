package com.citigroup.liquifi.autopilot.message;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTemplate;
import com.citigroup.liquifi.entities.Tag;

public class FIXFactory {
	private LinkedHashMap<String, String> template = new LinkedHashMap<String, String>();

	public FIXMessage getFixMessage(String templateName, Set<Tag> specTags) {
		FIXMessage msg = null;
		if (template.containsKey(templateName)) {
			msg = new FIXMessage(template.get(templateName));
		} else {
			msg = new FIXMessage();
		}
		msg.overWriteTags(specTags);
		return msg;
	}

	public FIXMessage getFixMessage(String MsgType) {
		return getFixMessage(MsgType, null);
	}

	public void initMessage(String MsgType, String Standard) {
		template.put(MsgType, Standard.replace("^A", FIXMessage.SEPERATOR));
	}

	public FIXMessage genFIXMessage(String fixString, Set<Tag> specTags) {
		FIXMessage msg = new FIXMessage(fixString.trim().replace("^A",
				FIXMessage.SEPERATOR));
		msg.overWriteTags(specTags);
		return msg;
	}

	public FIXMessage genFIXMessage(String fixString, List<LFTag> specTags) {
		FIXMessage msg = new FIXMessage(fixString.trim().replace("^A",
				FIXMessage.SEPERATOR));
		msg.overWriteTags(specTags);
		return msg;
	}
	
	public FIXMessage genFIXMessageForOutputStep(String fixString, List<LFOutputTag> specTags) {
		FIXMessage msg = new FIXMessage(fixString.trim().replace("^A",
				FIXMessage.SEPERATOR));
		msg.overWriteTagsForOutputStep(specTags);
		return msg;
	}

	public void setTemplate(Map<String, LFTemplate> templateMap) {
		for (String templateName : templateMap.keySet()) {
			initMessage(templateName, templateMap.get(templateName)
					.getMsgTemplate());
		}
	}

	public LinkedHashMap<String, String> getTemplate() {
		return template;
	}

	public String getTagValue(String fixString, String tagID) {
		FIXMessage msg = new FIXMessage(fixString);
		return msg.getValue(tagID);
	}



}
