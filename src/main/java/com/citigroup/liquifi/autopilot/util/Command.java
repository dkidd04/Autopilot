package com.citigroup.liquifi.autopilot.util;

import java.util.HashMap;
import java.util.Map;

public class Command {
	private final CommandName name;
	private final String value;
	
	public enum CommandName {
		Pause,
		Accept;
		
		private static Map<String, CommandName> stringToCommandName = new HashMap<String, CommandName>();
		
		static {
			for(CommandName commandName : CommandName.values()) {
				stringToCommandName.put(commandName.name(), commandName);
			}
		}
		
		public static CommandName getEnum(String value) {
			String result = Character.toUpperCase(value.charAt(0)) + value.substring(1);
			return stringToCommandName.get(result);
		}
	}
	
	public Command(CommandName name, String value) {
		this.name = name;
		this.value = value;
	}

	public CommandName getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Name:").append(name);
		buffer.append("|Value:").append(value);
		
		return buffer.toString();
	}
}
