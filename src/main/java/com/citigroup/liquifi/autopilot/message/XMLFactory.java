package com.citigroup.liquifi.autopilot.message;

import java.util.List;
import java.util.Set;

import com.citigroup.liquifi.autopilot.util.Command;
import com.citigroup.liquifi.entities.Tag;

public interface XMLFactory {

	public String overWriteTags(String msg, Set<Tag> tags);
	
	public String overwriteAdminOrderRequest2XMLMessage(String msgStr, Set<Tag> specTags) throws Exception;
	
	public String extractCommands(String msgStr, List<Command> commands) throws Exception;

	public String getField(String msg, String strTagID);

	public String overWriteTagsGeneric(String inputMsg, Set<Tag> overwrite);
}
