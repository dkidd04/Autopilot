package com.citigroup.liquifi.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;


public class XMLBuilder {
	
	public static String getMarketDataXML(String symbol, String orderID, String user, String requestID, Map<String, String> tagMap) throws IOException{
		String str = null;
		Element rootElement = new Element("AdminOrderRequest");
		Document document = new Document(rootElement);
		
		Element symbolElement = new Element("Symbol");
		symbolElement.addContent(symbol);
		
		Element orderIDElement = new Element("OrderIDs");
		orderIDElement.addContent(orderID);
		
		Element userIDElement = new Element ("UserID");
		userIDElement.addContent(user);
		
		Element requestIDElement = new Element ("AdminRequestID");
		requestIDElement.addContent(requestID);
		
		Element marketDataElement = new Element("MarketData");
		
		Element tickElement = new Element("NBBOTick");
		
		
		for(String name : tagMap.keySet()){
			Element fieldElement = new Element("Field");
			Attribute tagID = new Attribute("Name", name);
			Attribute tagValue = new Attribute("Value", tagMap.get(name));
			fieldElement.addAttribute(tagID);
			fieldElement.addAttribute(tagValue);
			tickElement.addContent(fieldElement);
		}
		
		marketDataElement.addContent(tickElement);
		
		rootElement.addContent(symbolElement);
		rootElement.addContent(orderIDElement);
		rootElement.addContent(userIDElement);
		rootElement.addContent(requestIDElement);
		rootElement.addContent(marketDataElement);
		
		
		
	    XMLOutputter outputter = new XMLOutputter(" ", true);
	    str = outputter.outputString(document);

		return str;
	}
	
	public static String getBookPropertyXML(String symbol, String orderID, String user, String requestID, Map<String, String> tagMap, Map<String, String> propertyMap) throws IOException{
		String str = null;
		Element rootElement = new Element("AdminOrderRequest");
		Document document = new Document(rootElement);
		
		Element symbolElement = new Element("Symbol");
		symbolElement.addContent(symbol);
		
		Element orderIDElement = new Element("OrderIDs");
		orderIDElement.addContent(orderID);
		
		Element userIDElement = new Element ("UserID");
		userIDElement.addContent(user);
		
		Element requestIDElement = new Element ("AdminRequestID");
		requestIDElement.addContent(requestID);
		
		Element propertiesElement = new Element("Properties");
		
		for(String name : tagMap.keySet()){
			Element propertyElement = new Element("Property");
			Attribute tagID = new Attribute("Name", name);
			Attribute tagValue = new Attribute("Value", tagMap.get(name));
			Attribute type = new Attribute("Type", propertyMap.get(name));
			propertyElement.addAttribute(tagID);
			propertyElement.addAttribute(tagValue);
			propertyElement.addAttribute(type);
			propertiesElement.addContent(propertyElement);
		}
		
		rootElement.addContent(symbolElement);
		rootElement.addContent(orderIDElement);
		rootElement.addContent(userIDElement);
		rootElement.addContent(requestIDElement);
		rootElement.addContent(propertiesElement);
		
		
		
	    XMLOutputter outputter = new XMLOutputter(" ", true);
	    str = outputter.outputString(document);

		return str;
	}
	
	public static String getPriceOffsetXML(String symbol, String orderID, String user, String requestID, Map<String, String> tagMap) throws IOException{
		String str = null;
		Element rootElement = new Element("AdminOrderRequest");
		Document document = new Document(rootElement);
		
		Element symbolElement = new Element("Symbol");
		symbolElement.addContent(symbol);
		
		Element orderIDElement = new Element("OrderIDs");
		orderIDElement.addContent(orderID);
		
		Element userIDElement = new Element ("UserID");
		userIDElement.addContent(user);
		
		Element requestIDElement = new Element ("AdminRequestID");
		requestIDElement.addContent(requestID);
		
		Element propertiesElement = new Element("PriceOffset");
		
		for(String name : tagMap.keySet()){
			Element propertyElement = new Element("Field");
			Attribute tagID = new Attribute("Name", name);
			Attribute tagValue = new Attribute("Value", tagMap.get(name));
			propertyElement.addAttribute(tagID);
			propertyElement.addAttribute(tagValue);
			propertiesElement.addContent(propertyElement);
		}
		
		rootElement.addContent(symbolElement);
		rootElement.addContent(orderIDElement);
		rootElement.addContent(userIDElement);
		rootElement.addContent(requestIDElement);
		rootElement.addContent(propertiesElement);
		
		
		
	    XMLOutputter outputter = new XMLOutputter(" ", true);
	    str = outputter.outputString(document);

		return str;
	}
	
	
	public static String getXML(LFTestCase testcase) throws IOException{
		String str = null;
	    Element rootElement = new Element("TestCase");
		Document document = new Document(rootElement);
		
		Element appNameElement = new Element("AppName");
		appNameElement.addContent(testcase.getAppName());
		
		Element testIDElement = new Element("TestID");
		testIDElement.addContent(String.valueOf(testcase.getTestID()));
		
		Element descriptionElement = new Element ("Description");
		descriptionElement.addContent(testcase.getDescription());
		
		
		Element inputStepsListElement = new Element ("InputSteps");
		
		for(LFTestInputSteps inputStep : testcase.getInputStep()){
			
			Element stepNumElement = new Element("StepNumber");
			stepNumElement.addContent(String.valueOf(inputStep.getActionSequence()));
			Element templateElement = new Element("Template");
			templateElement.addContent(inputStep.getTemplate());
			Element messageElement = new Element("Message");
			messageElement.addContent(inputStep.getMessage());
			Element msgTypeElement = new Element("MsgType");
			msgTypeElement.addContent(inputStep.getMsgType());
			Element topicIDElement = new Element ("TopicID");
			topicIDElement.addContent(inputStep.getTopicID());
			
			Element inputTagListElement = new Element("InputTagList");
			
			for(LFTag inputTag : inputStep.getInputTagsValueList()){
				Element inputTagElement = new Element("InputTag");
				Attribute tagID = new Attribute("TagID", inputTag.getTagID());
				Attribute tagValue = new Attribute("TagValue", inputTag.getTagValue());
				inputTagElement.addAttribute(tagID);
				inputTagElement.addAttribute(tagValue);
				inputTagListElement.addContent(inputTagElement);
			}
			Element outputStepsListElement = new Element ("OutputSteps");
			for(LFOutputMsg outputStep : inputStep.getOutputStepList()){
				 
				Element outputStepNumElement = new Element("StepNumber");
				outputStepNumElement.addContent(String.valueOf(outputStep.getActionSequence()));
				Element outputTemplateElement = new Element("Template");
				outputTemplateElement.addContent(outputStep.getOutputMsg());
				Element outputMsgIDElement = new Element("OutputMsgID");
				outputMsgIDElement.addContent(String.valueOf(outputStep.getOutputMsgID()));
				Element outputTopicIDElement = new Element ("TopicID");
				outputTopicIDElement.addContent(outputStep.getTestID());
				
				Element outputTagListElement = new Element("OutputTagList");
				
				for(LFOutputTag outputTag : outputStep.getOutputTagList()){
					Element outputTagElement = new Element("OutputTag");
					Attribute tagID = new Attribute("TagID", outputTag.getTagID());
					Attribute tagValue = new Attribute("TagValue", outputTag.getTagValue());
					outputTagElement.addAttribute(tagID);
					outputTagElement.addAttribute(tagValue);
					outputTagListElement.addContent(outputTagElement);
				}
				
				Element outputStepElement = new Element ("OutputStep");
				outputStepElement.addContent(outputStepNumElement);
				outputStepElement.addContent(outputTemplateElement);
				outputStepElement.addContent(outputMsgIDElement);
				outputStepElement.addContent(outputTopicIDElement);
				outputStepElement.addContent(outputTagListElement);
				
				outputStepsListElement.addContent(outputStepElement);
				
			}
			Element inputStepElement = new Element ("InputStep");
			inputStepElement.addContent(stepNumElement);
			inputStepElement.addContent(templateElement);
			inputStepElement.addContent(messageElement);
			inputStepElement.addContent(msgTypeElement);
			inputStepElement.addContent(topicIDElement);
			inputStepElement.addContent(inputTagListElement);
			inputStepElement.addContent(outputStepsListElement);
			
			inputStepsListElement.addContent(inputStepElement);
			
		}
		
		rootElement.addContent(appNameElement);
		rootElement.addContent(testIDElement);
		rootElement.addContent(descriptionElement);
		rootElement.addContent(inputStepsListElement);
		
        XMLOutputter outputter = new XMLOutputter(" ", true);
        str = outputter.outputString(document);

		return str;
	}
	
	
	public static LFTestCase getTestCase(String xml){
		LFTestCase testcase = new LFTestCase();
		return testcase;
	}
	
	public static void saveToFile(LFTestCase testcase){
		try {
			String xmlStr = getXML(testcase);
			String fileName = "Local.xml";
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName)); 
			out.write(xmlStr);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void openFile(String fileName){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
	
			String line;
			while(( line = reader.readLine())!=null){
				System.out.println(line);
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main (String[] args){
//		Properties prop = new Properties();
//		prop.put(CfgMgrConstants.DPM_POS_POSITION, "10");
		LFTestCase sampleTestCase = new LFTestCase();
		sampleTestCase.setAppName("SampeTestCase");
		sampleTestCase.setTestID("123");
		sampleTestCase.setDescription("Sample Test Case XML File");
		
		LFTestInputSteps inputStep = new LFTestInputSteps();
		inputStep.setActionSequence(1);
		inputStep.setTemplate("NEW_DAY_ORDER");
		inputStep.setMessage(null);
		inputStep.setMsgType("FixMsg");
		inputStep.setUseOutputMsg('N');
		inputStep.setOutputMsgID("NEWACK");
		inputStep.setTopicID("ANY_TO_LIQUIFI");
		
		LFTestInputSteps inputStep2 = new LFTestInputSteps();
		inputStep2.setActionSequence(2);
		inputStep2.setTemplate("NEW_DAY_ORDER");
		inputStep2.setMessage(null);
		inputStep2.setMsgType("FixMsg");
		inputStep2.setUseOutputMsg('N');
		inputStep2.setOutputMsgID("NEWACK");
		inputStep2.setTopicID("ANY_TO_LIQUIFI");
		
		LFTag inputTag = new LFTag();
		inputTag.setTagID("37");
		inputTag.setTagValue("${getOrderID}");
		
		LFTag inputTag2 = new LFTag();
		inputTag2.setTagID("11");
		inputTag2.setTagValue("${getClOrderID}");
		
		inputStep.addToInputTagValueList(inputTag);
		inputStep.addToInputTagValueList(inputTag2);
		
		inputStep2.addToInputTagValueList(inputTag);
		
	
		sampleTestCase.addToInputStep(inputStep);
		sampleTestCase.addToInputStep(inputStep2);
		
		
		String xml;
		try {
			xml = XMLBuilder.getXML(sampleTestCase);
			System.out.println(xml);
			
			saveToFile(sampleTestCase);
			
			openFile("Local.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
