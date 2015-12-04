package com.citigroup.liquifi.autopilot.helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class ValidationResultXMLParser {
	Element rootnode;
	String xmlstr="";

	public ValidationResultXMLParser(String xmlstr)
	{
		rootnode= (Element) parse(xmlstr);
		this.xmlstr= xmlstr;
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

	public String getRequestName()
	{
		Element eleRequest = (Element)this.rootnode.getElementsByTagName("Request").item(0); 
		return eleRequest.getAttribute("Name");
	}
	
	public String getRequestValue()
	{
		Element eleRequest = (Element)this.rootnode.getElementsByTagName("Request").item(0); 
		return eleRequest.getAttribute("Value");
	}

	
}
