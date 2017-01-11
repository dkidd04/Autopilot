package com.citigroup.liquifi.autopilot.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.entities.Tag;

public class JSONFieldManipulator {

	private static AceLogger logger =  AceLogger.getLogger(JSONFieldManipulator.class.getSimpleName());

	/**
	 * Supports overwriting of fields in objects
	 * Does not allow overwriting of anything other than the first object in a list
	 * Does not create a new element in list of no element exists
	 * Assumes lists always contain objects
	 * @param jsonMsgMap
	 * @param overwriteFields
	 */
	public Map<String, String> overwriteJSONFields(Map<String, Object> jsonMsgMap, Set<Tag> overwriteFields) {
		Map<String, String> placeholderFields = new HashMap<String, String>();
		for(Tag field : overwriteFields){
			if(isPlaceholderField(field)){
				placeholderFields.put(field.getTagID(), field.getTagValue());
				continue;
			}

			Queue<String> fieldTree = fieldTree(field.getTagID());
			overwriteField(jsonMsgMap, fieldTree, field);
		}

		return placeholderFields;
	}

	private boolean isPlaceholderField(Tag field) {
		return field.getTagID().startsWith("@");
	}

	private Queue<String> fieldTree(String tagID) {
		String[] fieldTree;
		fieldTree = tagID.split("\\.");
		Queue<String> fieldPath = new LinkedList<>(Arrays.asList(fieldTree));
		return fieldPath;
	}

	private String getFieldName(String tagId) {
		if(!tagId.contains(".")){
			return tagId;
		}
		return tagId.substring(tagId.lastIndexOf(".")+1);
	}

	private void overwriteField(Map<String, Object> jsonObject, Queue<String> fieldPath, Tag field){
		String value = field.getTagValue();
		Map<String, Object> parentObject = getParentObject(jsonObject, fieldPath);
		String fieldName = getFieldName(field.getTagID());
		if (parentObject!=null){
			overwriteField(parentObject, value, fieldName);
		}
	}
	
	public String getFieldValueForJSONObject(String message, String fieldPath){
		ObjectMapper jsonMapper = new ObjectMapper();
		try {
			@SuppressWarnings("unchecked")
			Map<String,Object> jsonMsgMap = (Map<String, Object>)jsonMapper.readValue(message, Map.class);
			Queue<String> fieldTree = fieldTree(fieldPath);
			Map<String, Object> parentObject = getParentObject(jsonMsgMap, fieldTree);
			String fieldName = getFieldName(fieldPath);
			if(parentObject!=null){
				return (String)parentObject.get(fieldName);
			} 		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private Map<String,Object> getParentObject(Map<String, Object> jsonObject, Queue<String> fieldPath) {
		try{
			String nextField = getNextField(fieldPath);
			if(fieldPath.isEmpty()){
				return (Map<String, Object>)jsonObject;
			}
			boolean isListElement = "listValue".equals(fieldPath.peek());
			Map<String, Object> nextObject;
			if(isListElement){
				fieldPath.poll();
				nextObject = getJSONObjectFromList(jsonObject, nextField);
			} else{
				nextObject = getJSONObject(jsonObject, nextField);
			}
			return getParentObject(nextObject, fieldPath);
		}catch (Exception e){
			logger.warning("Failed to set field " + fieldPath);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void overwriteField(Map jsonObject, String value, String nextField) {
		Object object = jsonObject.get(nextField);
		if(object!=null){
			try{
				if(object instanceof Boolean){
					jsonObject.put(nextField, Boolean.valueOf(value));
				} else if (object instanceof Double){
					jsonObject.put(nextField, Double.valueOf(value));
				} else {
					jsonObject.put(nextField, value);
				}
			} catch (Exception e){}
		} else {
			jsonObject.put(nextField, value);
		}
	}

	private Map<String, Object> getJSONObject(Map<String, Object> jsonObject, String fieldName){
		Map<String, Object> targetField = (Map<String, Object>) jsonObject.get(fieldName);
		return  targetField;
	}

	private Map<String,Object> getJSONObjectFromList(Map<String,Object> jsonObject, String fieldName){
		Object targetField = jsonObject.get(fieldName);
		List<?> fieldList = (List<?>)targetField;
		Map<String,Object> targetObject = (Map<String,Object>) fieldList.get(0);
		return  targetObject;
	}

	private String getNextField(Queue<String> fieldPath){
		return fieldPath.poll();
	}

}