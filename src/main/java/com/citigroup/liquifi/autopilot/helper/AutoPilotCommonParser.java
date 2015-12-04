package com.citigroup.liquifi.autopilot.helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class AutoPilotCommonParser {
	Element rootnode;
	String xmlstr="";

	public AutoPilotCommonParser(String xmlstr)
	{
		rootnode= (Element) parse(xmlstr);
		this.xmlstr= xmlstr;
	}

	public AutoPilotCommonParser(File xmlfile)
	{
		rootnode= (Element) parse(xmlfile);
	}
	
	private Object parse(String xmlString) {
		Document document = null;
		try {
			document = loadXML(xmlString);
			return parse(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parse(document);
	}

	private Object parse(File file)
	{
		Document document = null;
		try {
			document = getDocument(file);
			return parse(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parse(document);
	}

	private Document getDocument(Object object){		
		Document document = null;
		try {
			if(object instanceof String){
				document = loadXML((String)object);
			}
			else if(object instanceof File){
				document = loadXMLFile((File)object);
			}
			else{
				throw new Exception("Cannot parse "+object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

	private static synchronized Document loadXMLFile(File file) {
		return loadXML(file);
	}
	
	private Object parse(Document doc) {
		return doc.getDocumentElement();
	}

	private static synchronized Document loadXML(Object xml) {
		Document document = null;
		try {			
			DOMParser parser = new DOMParser();
			if(xml instanceof String){				
				ByteArrayInputStream bs = new ByteArrayInputStream(((String)xml).getBytes());
				InputSource source = new InputSource(bs);
				parser.parse(source);	
			}else{
				Reader reader = new FileReader((File)xml); 
				InputSource source = new InputSource(reader);
				parser.parse(source);				
			}
			document = parser.getDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{}
		return document;
	}

	public Element getRootnode() {
		return rootnode;
	}

	public void setRootnode(Element rootnode) {
		this.rootnode = rootnode;
	}

	public ArrayList<String> getCommonVariables(String strPropertyName)
	{
		ArrayList<String> list = new ArrayList<String>();
		Element elePropertyList = (Element) this.rootnode.getElementsByTagName(strPropertyName).item(0);
		NodeList propertyList =  elePropertyList.getElementsByTagName("Property");
		if(propertyList != null && propertyList.getLength() >0)
		{	
			for(int i =0; i < propertyList.getLength(); i++) {

				try {
					Element eleProperty= ((Element)propertyList.item(i));
					String strName = eleProperty.getAttribute("Name");
					list.add(strName);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return list;
	}

	public String getProperty(String strPropertyName) {
		String strVal = "";
		try {
			NodeList propertyList =  this.rootnode.getElementsByTagName("Property");
			
			if(propertyList != null && propertyList.getLength() >0)
			{	
				for(int i =0; i < propertyList.getLength(); i++) {

					try {
						Element eleProperty= ((Element)propertyList.item(i));
						String strName = eleProperty.getAttribute("Name");
						if (strPropertyName.equals(strName)) {
							strVal = eleProperty.getAttribute("Value");
						}
						
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return strVal;
	}
	
	public static void main(String [] args) {
		File file = new File("C:/_platform_c/perforce/liquifi/us/1.4/mainline/AutoPilot/resource/AutoPilotCommonEnv.xml");
//		String str1="<?xml version='1.0' encoding='UTF-8'?><AutoPilotValidationResult> <Property Name='TestCaseID' Value='TC-001' /> <Property Name='Symbol' Value='DECC' /> <Property Name='ValidationResultStatus' Value='true' /> <Property Name='ValidationResultMsg' Value='' /> <Property Name='FailedInputStep' Value='0' /> <Property Name='FailedOutputMsgID' Value='0' /> <ActualInputMsgList>  <InputStep Step='1' Msg='AdminOrderRequest&gt;' Topic='AEE.ADMIN.INBOUND.P3' Msgtype='XML' /> </ActualInputMsgList>" +
//				" <OriginalInboundMsgList>  <OutputStep Step='1'> <OutputMsg Msg='tq' Topic='AEEToWind' MsgID='0' />  <OutputMsg Msg='t2' Topic='AEEToXSVCLAVACust' MsgID='0' />  </OutputStep> </OriginalInboundMsgList> " +
//				"<ExpectedOutputStepList>  <OutputStep Step='1'> <OutputMsg Msg='tq' Topic='AEEToWind' MsgID='0' /> <OutputMsg Msg='t2' Topic='AEEToXSVCLAVACust' MsgID='0' /> </OutputStep>  </ExpectedOutputStepList> " +
//				"<ActualOutputStepList>  <OutputStep Step='1' > <OutputMsg Msg='tq' Topic='AEEToWind' MsgID='0' /> <OutputMsg Msg='t2' Topic='AEEToXSVCLAVACust' MsgID='0' /> </OutputStep> </ActualOutputStepList>" +
//				"</AutoPilotValidationResult>";

		AutoPilotCommonParser vParser = new AutoPilotCommonParser(file); 
		ArrayList<String> vo1 = vParser.getCommonVariables("ApplictionList");
		
		System.out.println("0"+vo1.get(0));
		
		ArrayList<String> vo2 = vParser.getCommonVariables("EnvList");
		System.out.println("vo2"+vo2);
		
		System.out.println(vParser.getProperty("CustomizedValidationClasspath"));
	}

}
