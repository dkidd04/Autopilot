package com.citigroup.liquifi.autopilot.message.xstream;

public class Property {
	private String name;
	private Object value;
	private String type;

	protected Property() {	// For XStream initialization via reflection in Java 7
	}

	public Property (String name, Object value, String type){
		this.name = name;
		this.value = value;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
