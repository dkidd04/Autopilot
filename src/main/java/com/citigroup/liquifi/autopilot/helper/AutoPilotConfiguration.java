package com.citigroup.liquifi.autopilot.helper;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.citigroup.liquifi.autopilot.util.SercurityManager;
import com.citigroup.liquifi.entities.LFTestInputSteps;

public class AutoPilotConfiguration {
	private List<LFTestInputSteps> defaultInputStep = new ArrayList<LFTestInputSteps>();
	private String[] queryCriteria = new String[20];
	private boolean debug;
	private int validateTimeout = 2000;
	private boolean enablePopup;
	private boolean completenessCheck = true;
	private String serverModeTestCaseQuery;
	private String releaseNum;
	private String orderTestcases = "name";
	private String highlightedTags;
	private boolean overwriteSendCompSubIDBasedOnConfig;
	private int defaultSecurityClass;
	private Map<Integer, String> defaultSymbolMap = new HashMap<Integer, String>();
	private SercurityManager securityManager = null;
	
	public List<LFTestInputSteps> getDefaultInputStep() {
		// Want to send a clone
		List<LFTestInputSteps> clone = new ArrayList<LFTestInputSteps>();
		for (LFTestInputSteps inputStep : defaultInputStep) {
			LFTestInputSteps inputClone = new LFTestInputSteps();
			inputClone.setActionSequence(inputStep.getActionSequence());
			inputClone.setMessage(inputStep.getMessage());
			inputClone.setMsgType(inputStep.getMsgType());
			inputClone.setTemplate(inputStep.getTemplate());
			inputClone.setTopicID(inputStep.getTopicID());
			inputClone.setComments(inputStep.getComments());
			clone.add(inputClone);
		}

		return clone;
	}

	public void setDefaultInputStep(List<LFTestInputSteps> defaultInputStep) {
		this.defaultInputStep = defaultInputStep;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Map<Integer, String> getDefaultSymbolMap() {
		return defaultSymbolMap;
	}

	public void setDefaultSymbolMap(Map<String, String> map) {
		defaultSymbolMap.clear();
		
		Pattern pattern = Pattern.compile("((\\d+)=(\\w+))");
		
		for(Entry<String, String> entry : map.entrySet()) {
			String symbol = entry.getValue();
			
			if(symbol.contains("[")) {
				Matcher m = pattern.matcher(symbol);
				String replacement = "";
				String instance = System.getProperty("instance", "1");
				
				while (m.find()) {
					if(instance.equals(m.group(2))) {
						replacement = m.group(3);
						break;
					}
				}
				symbol = symbol.replaceAll("\\[.*\\]", replacement);
			}
			int intValue = Integer.parseInt(entry.getKey());
			defaultSymbolMap.put(intValue, symbol);
		}
	}

	public int getValidateTimeout() {
		return validateTimeout;
	}

	public void setValidateTimeout(int validateTimeout) {
		this.validateTimeout = validateTimeout;
	}

	public String getServerModeTestCaseQuery() {
		return serverModeTestCaseQuery;
	}

	public void setServerModeTestCaseQuery(String serverModeTestCaseQuery) {
		this.serverModeTestCaseQuery = serverModeTestCaseQuery;
	}

	public String[] getQueryCriteria() {
		return queryCriteria;
	}

	public void setQueryCriteria(String[] queryCriteria) {
		this.queryCriteria = queryCriteria;
	}

	public boolean isEnablePopup() {
		return enablePopup;
	}

	public void setEnablePopup(boolean enablePopup) {
		this.enablePopup = enablePopup;
	}

	public void setOverwriteSendCompSubIDBasedOnConfig(boolean overwriteSendCompSubIDBasedOnConfig) {
		this.overwriteSendCompSubIDBasedOnConfig = overwriteSendCompSubIDBasedOnConfig;
	}

	public boolean isOverwriteSendCompSubIDBasedOnConfig() {
		return overwriteSendCompSubIDBasedOnConfig;
	}

	public boolean isCompletenessCheck() {
		return completenessCheck;
	}

	public void setCompletenessCheck(boolean completenessCheck) {
		this.completenessCheck = completenessCheck;
	}

	public String getOrderTestcases() {
		return orderTestcases;
	}

	public void setOrderTestcases(String orderTestcases) {
		this.orderTestcases = orderTestcases;
	}

	public String getHighlightedTags() {
		return highlightedTags;
	}

	public void setHighlightedTags(String highlightedTags) {
		this.highlightedTags = highlightedTags;
	}

	public int getDefaultSecurityClass() {
		return defaultSecurityClass;
	}

	public void setDefaultSecurityClass(int defaultSecurityClass) {
		this.defaultSecurityClass = defaultSecurityClass;
	}

	public Map<String, String> getContents() {
		Map<String, String> contents = new HashMap<String, String>();

		try {
			for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(this.getClass()).getPropertyDescriptors()) {
				if (propertyDescriptor.getWriteMethod() != null) {
					String name = propertyDescriptor.getWriteMethod().getName();
					
					Class<?> parameter = propertyDescriptor.getWriteMethod().getParameterTypes()[0];
					if(parameter == List.class || parameter == Map.class || parameter == String[].class || parameter == SercurityManager.class) {
						continue;
					}

					// cut off "set" from method name
					if (name.startsWith("set")) {
						try {
							contents.put(name.substring(3), propertyDescriptor.getReadMethod().invoke(this).toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}
	
	public void setContents(String property, String value) throws Exception {
		for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(this.getClass()).getPropertyDescriptors()) {
			if (propertyDescriptor.getWriteMethod() != null) {
				if(propertyDescriptor.getWriteMethod().getName().equals("set"+property)) {
					
					Class<?> parameter = propertyDescriptor.getWriteMethod().getParameterTypes()[0];

					if(parameter == String.class) {
						propertyDescriptor.getWriteMethod().invoke(this, value);
					} else if(parameter == boolean.class || parameter == Boolean.class) {
						if(!value.toLowerCase().equals("true") && !value.toLowerCase().equals("false")) {
							throw new Exception("Can not set value not true or false. Value = "+value);
						}
						propertyDescriptor.getWriteMethod().invoke(this, Boolean.valueOf(value));
					} else if(parameter == int.class || parameter == Integer.class) {
						propertyDescriptor.getWriteMethod().invoke(this, Integer.valueOf(value));
					} else if(parameter == double.class || parameter == Double.class) {
						propertyDescriptor.getWriteMethod().invoke(this, Double.valueOf(value));
					} else if(parameter == long.class || parameter == Long.class) {
						propertyDescriptor.getWriteMethod().invoke(this, Long.valueOf(value));
					} else if(parameter == short.class || parameter == Short.class) {
						propertyDescriptor.getWriteMethod().invoke(this, Short.valueOf(value));
					} else if(parameter == float.class || parameter == Float.class) {
						propertyDescriptor.getWriteMethod().invoke(this, Float.valueOf(value));
					} else if(parameter == byte.class || parameter == Byte.class) {
						propertyDescriptor.getWriteMethod().invoke(this, Byte.valueOf(value));
					}
				}
				
			}
		}
	}
	
	public void setSecurityManager(SercurityManager manager) {
		this.securityManager = manager;
	}
	
	public SercurityManager getSecurityManager() {
		return this.securityManager;
	}

	public String getReleaseNum() {
		return releaseNum;
	}

	public void setReleaseNum(String releaseNum) {
		this.releaseNum = releaseNum;
	}
}
