package com.citigroup.liquifi.autopilot.message;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.citigroup.get.zcc.intf.TagConstants;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.Tag;
import com.citigroup.liquifi.util.Util;

public class FIXMessage {
	public static String SEPERATOR = "\001";
	public static String DEFAULTVER = "FIX.4.4";
	private Pattern repeatingGroupTagIdPattern = Pattern.compile("([\\d]+)\\[([\\d]+)\\]");
	private LinkedHashMap<String, String> tagMap = new LinkedHashMap<String, String>();
	private AceLogger logger = AceLogger.getLogger(this.getClass().getSimpleName());

	public FIXMessage() {}
	public FIXMessage(Set<Tag> tags) {
		this.overWriteTags(tags);
	}
	
	public FIXMessage(String fixString) {
		if (fixString.length() == 0) return;
		
		
		String[] tags = fixString.trim().split(FIXMessage.SEPERATOR);
		String tagID = null;
		for (int i = 0; i < tags.length; i++) {
			try {
				if(tags[i].indexOf("=") > -1) {
					tagID = tags[i].substring(0, tags[i].indexOf("="));
					
					while(tagMap.containsKey(tagID)) {
						tagID = tagID+"#";
					}
					
					tagMap.put(tagID, tags[i].substring(tags[i].indexOf("=")+1, tags[i].length()));
				} else {
					logger.warning("Message has a tag with no value|Tag:"+tags[i]+"|Message:"+fixString);
				}
			} catch (Exception ex) {
				logger.warning("Cannot process tag " + tags[i]);
				ex.printStackTrace();
			}
			
		}
	}

	public void overWriteTags(Set<Tag> tags) {
		if (tags == null) return;		
		Iterator<Tag> it = tags.iterator();
		while (it.hasNext()) {
			Tag tagObj = it.next();
			if(isRepeatingGroupTag(tagObj)){
				overwriteRepeatingGroupTag(tagObj);
			}else{
				setValue(tagObj.getTagID(), tagObj.getTagValue());
			}
			
			//if (tagObj instanceof LFTag) {
			//	LFTag tag = (LFTag)tagObj;
			//	setValue(tag.getTagID(), tag.getTagValue());
			//}
			//else if (tagObj instanceof LFOutputTag) {
			//	LFOutputTag tag = (LFOutputTag)tagObj;
			//	setValue(tag.getTagID(), tag.getTagValue());
			//} else if(tagObj instanceof LFCommonOverwriteTag) {
			//}
			
		}
	}
	
	public void overWriteTags(List<LFTag> tags) {
		if (tags == null) return;		
		Iterator<LFTag> it = tags.iterator();
		while (it.hasNext()) {
			LFTag tagObj = it.next();
			if(isRepeatingGroupTag(tagObj)){
				overwriteRepeatingGroupTag(tagObj);
			}else{
				setValue(tagObj.getTagID(), tagObj.getTagValue());
			}
		}
	}
	
	public void overWriteTagsForOutputStep(List<LFOutputTag> tags) {
		if (tags == null) return;		
		Iterator<LFOutputTag> it = tags.iterator();
		while (it.hasNext()) {
			LFOutputTag tagObj = it.next();
			if(isRepeatingGroupTag(tagObj)){
				overwriteRepeatingGroupTag(tagObj);
			}else{
				setValue(tagObj.getTagID(), tagObj.getTagValue());
			}
		}
	}
	
	private void overwriteRepeatingGroupTag(Tag tagObj){
	
		StringBuilder targetTagId = new StringBuilder(getRepeatingGroupTagId(tagObj));
		int rgIndex = getRepeatingGroupTagIndex(tagObj); //e.g. 15678[1], index = 1
		for(int i = 1; i < rgIndex; i++){
			targetTagId.append("#");
		}
		setValue(targetTagId.toString(), tagObj.getTagValue());
	}
	
	private String getRepeatingGroupTagId(Tag t){
		String tagId = t.getTagID();
		int i = tagId.indexOf("[");
		return tagId.substring(0, i);
	}

	private int getRepeatingGroupTagIndex(Tag t){
		String tagId = t.getTagID();
		int start = tagId.indexOf("[") + 1;
		int end = tagId.indexOf("]");
		return Integer.parseInt(tagId.substring(start, end));
	}
	
	private boolean isRepeatingGroupTag(Tag t){
		return repeatingGroupTagIdPattern.matcher(t.getTagID()).matches();
	}
	
	
	/*
	public void setValue(int key, String val) {
		setValue(Integer.toString(key), val);
	}
	*/
	
	public void setValue(String key, String val) {
		tagMap.put(key, val);
	}
	
	/*
	public void unsetValue(int key) {
		unsetValue(Integer.toString(key));
	}
	*/
	
	public void unsetValue(String key) {
		tagMap.remove(key);
	}
	
	public boolean isSet(String key) {
		return tagMap.containsKey(key);
	}
	
	public String getValue(String key) {
		return (String) tagMap.get(key);
	}
	
	public String getValue(String key, String defaultVal) {
		return tagMap.containsKey(key) ? (String) tagMap.get(key) : defaultVal;
	}

	public String getClOrdID() {
		return getValue(String.valueOf(TagConstants.CLORDID));
	}
	
	public void genClOrdID() {
		setValue(String.valueOf(TagConstants.CLORDID), Util.generateSequence());
	}
	
	public String getTargetSubID() {
		return getValue(String.valueOf(TagConstants.TARGETSUBID));
	}
	
	public String getSenderSubID() {
		return getValue(String.valueOf(TagConstants.SENDERSUBID));
	}
	
	public String getTarget() {
		return getTargetSubID() == null ? getValue(String.valueOf(TagConstants.TARGETCOMPID)) : getTargetSubID();
	}
	
	private boolean isSystemTag(String key) {
		return (key.equals(String.valueOf(TagConstants.BEGINSTRING)) ) || (key.equals(String.valueOf(TagConstants.BODYLENGTH)) )
		|| (key.equals(String.valueOf(TagConstants.CHECKSUM)) );
	} 
	
	public LinkedHashMap<String, String> getTagMap() {
		return tagMap;
	}
	
	public void setTagMap(LinkedHashMap<String, String> map) {
		this.tagMap = map;
	}
	
	public String toString() {
		String res = "";
		if (!tagMap.containsKey(String.valueOf(TagConstants.BEGINSTRING)) )
			setValue(String.valueOf(TagConstants.BEGINSTRING), FIXMessage.DEFAULTVER);
		Iterator<String> it = tagMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (isSystemTag(key)) continue;
			res += key.replace("#", "") + "=" + tagMap.get(key) + FIXMessage.SEPERATOR;
		}
		res = TagConstants.BEGINSTRING + "=" + getValue(String.valueOf(TagConstants.BEGINSTRING)) + FIXMessage.SEPERATOR 
			/*
			 * generate tag 9.
			 * 
			 */
		+ TagConstants.BODYLENGTH + "=" + res.length() + FIXMessage.SEPERATOR
			+ res 
			+ TagConstants.CHECKSUM + "=000" + FIXMessage.SEPERATOR;
		return res;
	}
	
	public String getTag(int key){
		String val = getValue(String.valueOf(key));
		if (val == null)
			val = "";
		return val;
	}

}
