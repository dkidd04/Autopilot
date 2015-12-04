package com.citigroup.liquifi.autopilot.helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class AdminRequestXMLParser{
	Element rootnode;
	String xmlstr="";

	public AdminRequestXMLParser(String xmlstr)
	{
		rootnode= (Element) parse(xmlstr);
		this.xmlstr= xmlstr;
	}

	public AdminRequestXMLParser(File xmlfile)
	{
		rootnode= (Element) parse(xmlfile);
	}

	private Object parse(String xmlString) {
		Document document = null;
		try {
			document = getDocument(xmlString);
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
	private static synchronized Document loadXMLFile(File file) {
		return loadXML(file);
	}

	private Object parse(Document doc) {
		return doc.getDocumentElement();
	}
	public int getAdminRequestID()
	{
		return Integer.parseInt(this.rootnode.getElementsByTagName("AdminRequestID").item(0).getTextContent().trim());
	}
	public String getOrderIDs()
	{
		return this.rootnode.getElementsByTagName("OrderIDs").item(0).getTextContent();
	}
	public String getUserID()
	{
		return this.rootnode.getElementsByTagName("UserID").item(0).getTextContent();
	}
	public String getSymbol()
	{
		return this.rootnode.getElementsByTagName("Symbol").item(0).getTextContent();
	}
	public String getSrcSystemID()
	{
		NodeList srcSystemIDList= this.rootnode.getElementsByTagName("SrcSystemID");
		if(null != srcSystemIDList && srcSystemIDList.getLength() > 0)
			return srcSystemIDList.item(0).getNodeValue();
		else
			return "";
	}
	public String getConnectMarketMethod()
	{
		NodeList connmktlist= this.rootnode.getElementsByTagName("ConnectMarket");
		String methodName="";
		if(connmktlist.getLength() > 0)
		{

			if(connmktlist.item(0).hasChildNodes())
			{
				NodeList methodlist= ((Element)connmktlist.item(0)).getElementsByTagName("MethodName");
				if(methodlist != null)
				{
					methodName= methodlist.item(0).getNodeValue();
				}

			}
		}
		return methodName;
	}
	public String getRequestSubType()
	{
		NodeList requestSubTypeList = this.rootnode.getElementsByTagName("RequestSubType");
		if(null != requestSubTypeList && requestSubTypeList.getLength() > 0)
			return requestSubTypeList.item(0).getNodeValue();
		else
			return "";
	}
	public String getConnectMarketParameter()
	{
		NodeList connmktlist= this.rootnode.getElementsByTagName("ConnectMarket");
		String attribute="";
		if(connmktlist.getLength() > 0)
		{

			if(connmktlist.item(0).hasChildNodes())
			{
				NodeList parameterlist= ((Element)connmktlist.item(0)).getElementsByTagName("Parameters");
				if(parameterlist != null && parameterlist.getLength() >0)
				{
					NodeList fieldlist= ((Element)parameterlist.item(0)).getElementsByTagName("Field");
					if(fieldlist != null && fieldlist.getLength() >0)
					{
						Element fieldEl= (Element)fieldlist.item(0);
						attribute=fieldEl.getAttribute("Value").trim();
					}
				}
			}
		}
		return attribute;
	}

	public String[] getMarketData()
	{
		NodeList mktdatalist= this.rootnode.getElementsByTagName("MarketData");
		if(mktdatalist.getLength() > 0)
		{
			if(mktdatalist.item(0).hasChildNodes())
			{
				//ArrayList<Object[]> orderlist= new ArrayList<Object[]>();
				NodeList nbbotick= ((Element)mktdatalist.item(0)).getElementsByTagName("NBBOTick");
				if(nbbotick.getLength() > 0)
				{
					String[] mktfields;
					if(nbbotick.item(0).hasChildNodes())
					{
						NodeList fieldlist= ((Element)nbbotick.item(0)).getElementsByTagName("Field");
						if(fieldlist != null)
						{
							int length= fieldlist.getLength();
							mktfields= new String[length];
							for(int i =0; i < length; i++)
							{
								Element fieldEl= (Element)fieldlist.item(i);

								String name= fieldEl.getAttribute("Name");
								if("Symbol".equals(name))
									mktfields[0]=fieldEl.getAttribute("Value");
								else if("TickType".equals(name))
									mktfields[1]=fieldEl.getAttribute("Value");
								else if("Bid".equals(name))
									mktfields[2]=fieldEl.getAttribute("Value");
								else if("BidVolume".equals(name))
									mktfields[3]=fieldEl.getAttribute("Value");
								else if("Ask".equals(name))
									mktfields[4]=fieldEl.getAttribute("Value");
								else if("AskVolume".equals(name))
									mktfields[5]=fieldEl.getAttribute("Value");
								else if("Direction".equals(name))
									mktfields[6]=fieldEl.getAttribute("Value");
								else if("Status".equals(name))
									mktfields[7]=fieldEl.getAttribute("Value");

							}
							return mktfields;

						}
					}
				}
				else
				{
					NodeList firmtick= ((Element)mktdatalist.item(0)).getElementsByTagName("FirmTick");
					if(firmtick.getLength() > 0)
					{
						String[] firmfields;
						if(firmtick.item(0).hasChildNodes())
						{
							NodeList fieldlist= ((Element)firmtick.item(0)).getElementsByTagName("Field");
							if(fieldlist != null)
							{
								int length= fieldlist.getLength();
								firmfields= new String[length];
								for(int i =0; i < length; i++)
								{
									Element fieldEl= (Element)fieldlist.item(i);

									String name= fieldEl.getAttribute("Name");
									if("Symbol".equals(name))
										firmfields[0]=fieldEl.getAttribute("Value");
									else if("TickType".equals(name))
										firmfields[1]=fieldEl.getAttribute("Value");
									else if("Bid".equals(name))
										firmfields[2]=fieldEl.getAttribute("Value");
									else if("BidVolume".equals(name))
										firmfields[3]=fieldEl.getAttribute("Value");
									else if("Ask".equals(name))
										firmfields[4]=fieldEl.getAttribute("Value");
									else if("AskVolume".equals(name))
										firmfields[5]=fieldEl.getAttribute("Value");
									else if("Direction".equals(name))
										firmfields[6]=fieldEl.getAttribute("Value");
									else if("Status".equals(name))
										firmfields[7]=fieldEl.getAttribute("Value");

								}
								return firmfields;

							}
						}
					}
				}
			}
		}
		return null;
	}

	public String getBookAttributes()
	{
		int startindex= xmlstr.indexOf("<Properties>");
		int endindex= xmlstr.indexOf("</Properties>")+13;
		String properties= this.xmlstr.substring(startindex, endindex); 
		return properties;
	}

	public String getHeartbeatAttributes()
	{
		int startindex= xmlstr.indexOf("<HeartbeatProperties>");
		int endindex= xmlstr.indexOf("</HeartbeatProperties>")+22;
		String properties= this.xmlstr.substring(startindex, endindex); 
		return properties;
	}

	public String getEnvLoaderAttributes()
	{
		int startindex= xmlstr.indexOf("<EnvLoaderProperties>");
		int endindex= xmlstr.indexOf("</EnvLoaderProperties>")+22;
		String properties= this.xmlstr.substring(startindex, endindex); 
		return properties;
	}

	public Element getRootnode() {
		return rootnode;
	}

	public void setRootnode(Element rootnode) {
		this.rootnode = rootnode;
	}

	/*
	 * only returns the first property because ControlMessage should only has one element
	 */
	public String[] getSinglePropertyForControlMessage()
	{
		String[] strArray = {"",""};
		Element ele1 = (Element)this.rootnode.getElementsByTagName("Properties").item(0);
		Element ele2 = (Element)ele1.getElementsByTagName("Property").item(0);
		strArray[0] = ele2.getAttribute("Name");
		strArray[1] = ele2.getAttribute("Value");
		
		return strArray;
	}
	
	public static void main(String [] args) {
		String str1= "<?xml version=\"1.0\" encoding=\"UTF-8\"?><AdminOrderRequest><Symbol>Test</Symbol><OrderIDs></OrderIDs><UserID>AutoPilot</UserID><AdminRequestID>7" +
				"</AdminRequestID><Properties><Property Name='Pause' Value='2000' Type='java.lang.Boolean'/></Properties></AdminOrderRequest>";
		
		AdminRequestXMLParser arParser = new AdminRequestXMLParser(str1);
		System.out.println(arParser.getSymbol());
		System.out.println(arParser.getSinglePropertyForControlMessage()[0]);
	}
	
	public String toString() {
		return xmlstr;
	}

}