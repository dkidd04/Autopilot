package com.citigroup.liquifi.autopilot.message;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.citigroup.liquifi.autopilot.helper.AdminRequestXMLParser;
import com.citigroup.liquifi.entities.Tag;
import com.citigroup.liquifi.util.AdminRequestID;
import com.citigroup.liquifi.util.XMLBuilder;

public class AdminXMLMessage extends XMLMessage {

    private String symbol;
    private String orderID;
    private String userid;
    private AdminRequestID requestID;
    private AdminRequestXMLParser adminParser;
	
	public AdminXMLMessage(){
	}
	
	
	public AdminXMLMessage(String template){
		adminParser = new AdminRequestXMLParser(template);
		symbol = adminParser.getSymbol();
		orderID = adminParser.getOrderIDs();
		userid = adminParser.getUserID();
		requestID = AdminRequestID.valueOf(adminParser.getAdminRequestID());
		

		switch(requestID){

			case SETMKTDATA://SETMKTDATA		
				addMarketDatatoTagMap(adminParser);
				break;
				
			case SETPROPERTIES:
				addBookPropertiesToTagMap(adminParser);
				break;
			case SETOFFSET:
				addPriceOffSetToTagMap(adminParser);
				break;
	
		}
	}


	public AdminXMLMessage(Set<? extends Tag> tags) {
		this.overWriteTags(tags);
	}
	
	
	public void overWriteTags(Set<? extends Tag> tags) {
		if (tags == null) return;
		Iterator<? extends Tag> it = tags.iterator();
		while (it.hasNext()) {
			Tag tag = it.next();
			setValue(tag.getTagID(), tag.getTagValue());
		}
	}
	
	public void setValue(int key, String val) {
		setValue(Integer.toString(key), val);
	}
	
	public void setValue(String key, String val) {
		getTagMap().put(key, val);
	}
	
	
	public void addMarketDatatoTagMap(AdminRequestXMLParser adminParser){
		NodeList mktdatalist = adminParser.getRootnode().getElementsByTagName("MarketData");
		if(mktdatalist.getLength() > 0)
		{
			if(mktdatalist.item(0).hasChildNodes())
			{
				NodeList nbbotick= ((Element)mktdatalist.item(0)).getElementsByTagName("NBBOTick");
				if(nbbotick.getLength() > 0)
				{
					if(nbbotick.item(0).hasChildNodes())
					{
						NodeList fieldlist= ((Element)nbbotick.item(0)).getElementsByTagName("Field");
						if(fieldlist != null)
						{
							int length= fieldlist.getLength();
							for(int i =0; i < length; i++)
							{
								Element fieldEl= (Element)fieldlist.item(i);

								String name	= fieldEl.getAttribute("Name");
								String value = fieldEl.getAttribute("Value");

								getTagMap().put(name, value);

							}
						}
					}
				}
			}
		}
	}
	
	
	private void addBookPropertiesToTagMap(AdminRequestXMLParser adminParser) {
		NodeList propertiesList = adminParser.getRootnode().getElementsByTagName("Properties");
		if(propertiesList.getLength() > 0){
			Element properties = (Element) propertiesList.item(0);
			if (properties.hasChildNodes()){
				NodeList propertyList = properties.getElementsByTagName("Property");
				if(propertyList !=null){
					int length = propertyList.getLength();
					for(int i =0; i < length; i++){
						Element property = (Element) propertyList.item(i);
						String name	= property.getAttribute("Name");
						String value = property.getAttribute("Value");
						String type = property.getAttribute("Type");
						getTagMap().put(name, value);
						getPropertyTypeMap().put(name, type);
					}
				}
			}
		}
		
	}
	
	private void addPriceOffSetToTagMap(AdminRequestXMLParser adminParser) {
		NodeList propertiesList = adminParser.getRootnode().getElementsByTagName("PriceOffset");
		if(propertiesList.getLength() > 0){
			Element properties = (Element) propertiesList.item(0);
			if (properties.hasChildNodes()){
				NodeList propertyList = properties.getElementsByTagName("Field");
				if(propertyList !=null){
					int length = propertyList.getLength();
					for(int i =0; i < length; i++){
						Element property = (Element) propertyList.item(i);
						String name	= property.getAttribute("Name");
						String value = property.getAttribute("Value");
						getTagMap().put(name.trim(), value.trim());
					}
				}
			}
		}
		
	}
	
	
	public String toString(){
		String xmlString = null;
		try {
			switch(requestID){

			case SETMKTDATA://SETMKTDATA		
				xmlString = XMLBuilder.getMarketDataXML(symbol,orderID,userid, requestID.stringValue(), getTagMap());
				break;
			case SETPROPERTIES:
				xmlString = XMLBuilder.getBookPropertyXML(symbol,orderID,userid, requestID.stringValue(), getTagMap(), getPropertyTypeMap());
				break;
			case SETOFFSET:
				xmlString = XMLBuilder.getPriceOffsetXML(symbol,orderID,userid, requestID.stringValue(), getTagMap());
				break;
			default:
				xmlString = adminParser.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlString;
	}
	
}
