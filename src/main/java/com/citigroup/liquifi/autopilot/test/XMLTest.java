package com.citigroup.liquifi.autopilot.test;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XMLTest {
	public static void main(String args[]){
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			
			String str = "<AdminOrderRequest><Symbol>DELLL</Symbol><OrderIDs ></OrderIDs ><UserID>AutoPilot</UserID><AdminRequestID>5</AdminRequestID></AdminOrderRequest>";
			StreamSource source = new StreamSource(new StringReader(str));
			transformer.transform(source, result);
		

			String xmlString = result.getWriter().toString();
			System.out.println(xmlString);
			
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
