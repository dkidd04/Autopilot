package com.citigroup.liquifi.autopilot.message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.autopilot.message.xstream.AdminMarketData;
import com.citigroup.liquifi.autopilot.message.xstream.AdminMessage;
import com.citigroup.liquifi.autopilot.message.xstream.MarketPeriod;
import com.citigroup.liquifi.autopilot.message.xstream.Property;
import com.citigroup.liquifi.autopilot.util.Command;
import com.citigroup.liquifi.entities.Tag;
import com.citigroup.liquifi.util.Util;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class XStreamXMLFactory implements XMLFactory{
	private static AceLogger logger = AceLogger.getLogger(XStreamXMLFactory.class.getSimpleName());
	private static XStream xstream = new XStream(new DomDriver());

	@Override
	public String overWriteTags(String msg, Set<Tag> tags){
		String returnMsg = msg;
		xstream.alias("AdminMessage", AdminMessage.class);
		xstream.alias("Property", Property.class);
		xstream.alias("MarketPeriod", MarketPeriod.class);
		AdminMessage adminMsg = (AdminMessage) xstream.fromXML(msg);

		switch (adminMsg.getCommand()){
		case SETMARKETDATA:
			overwriteMarketData(adminMsg.getMarketData(), tags);
			returnMsg = xstream.toXML(adminMsg);
			break;
		case SETBOOKATTRIBUTES:
		case SETSYSTEMPROPERTIES:
			overwriteSystemProperties(adminMsg.getProperties(), tags);
			returnMsg = xstream.toXML(adminMsg);
			break;
		case DELETEBOOK:
			break;
		case DELETEORDER:
			break;
		case GETBOOKATTRIBUTES:
			break;
		case GETMARKETDATA:
			break;
		case GETSYSTEMPROPERTIES:
			break;
		case UNSOLCITEDCXLORDER:
			break;
		default:
			break;
		}

		return returnMsg;
	}

	protected void overwriteSystemProperties(List<Property> properties, Set<Tag> tags) {
		for (Tag tag : tags){
			String id = tag.getTagID();
			String stringValue = tag.getTagValue();
			for(Property prop : properties){
				if (prop.getName().equals(id)){
					//Since {@link Tag#getTagValue()} is of type {@link String}, we need to convert to the property type
					boolean successfulConversion = false;
					try {
						Object trueValue = null;
						Class<?> trueType = Class.forName(prop.getType());
						//MarketPeriod is a special case since it is an enumeration in both AutoPilot and LiqFiFramework
						//Xstream aliases don't help when trying to load the class to do the conversion from String to MarketPeriod
						//This special check can go away if LiqFiFramework becomes a dependency of AutoPilot
						if(trueType.getSimpleName().equals("MarketPeriod")) {
							trueValue = MarketPeriod.valueOf(stringValue);
						} else if(!trueType.equals(String.class)){
							Method method = trueType.getDeclaredMethod("valueOf", String.class);
							trueValue = method.invoke(null, stringValue);
						}
						if(null != trueValue) {
							prop.setValue(trueValue);
							successfulConversion = true;
						}
					} catch (ClassNotFoundException e) {
						logger.severe(String.format("Unable to find class for type %s while setting property %s to %s, writing as java.lang.String", prop.getType(), prop.getName(), prop.getValue()));
					} catch (SecurityException e) {
						logger.severe(Util.getStackTrace(e));
					} catch (NoSuchMethodException e) {
						logger.severe(Util.getStackTrace(e));
					} catch (IllegalArgumentException e) {
						logger.severe(Util.getStackTrace(e));
					} catch (IllegalAccessException e) {
						logger.severe(Util.getStackTrace(e));
					} catch (InvocationTargetException e) {
						logger.severe(Util.getStackTrace(e));
					}
					if(!successfulConversion) {
						prop.setValue(stringValue);
					}
				}
			}
		}
	}

	private void overwriteMarketData(AdminMarketData marketData, Set<Tag> tags) {
		for (Tag tag : tags){
			String id = tag.getTagID();
			String value = tag.getTagValue();
			if (id.equalsIgnoreCase("bid")){
				marketData.setBid(Double.parseDouble(value));
			}else if (id.equalsIgnoreCase("offer")){
				marketData.setOffer(Double.parseDouble(value));
			}
		}
	}

	/**
	 * Added public behavior not exposed to interface, this should be a separate class but I'm borrowing the Xstream instance
	 */
	public String[] getSinglePropertyForControlMessage(String msg)
	{
		String[] returnArray = new String[2];
		xstream.alias("AdminMessage", AdminMessage.class);
		xstream.alias("Property", Property.class);
		AdminMessage adminMsg = (AdminMessage) xstream.fromXML(msg);
		List<Property> properties = adminMsg.getProperties();
		returnArray[0] = properties.get(0).getName();
		returnArray[1] = (String) properties.get(0).getValue();
		return returnArray;
	}

	@Override
	public String overwriteAdminOrderRequest2XMLMessage(String msgStr, Set<Tag> specTags) throws Exception {
		return msgStr;
	}

	@Override
	public String extractCommands(String msgStr, List<Command> commands) throws Exception {
		return msgStr;
	}

	@Override
	public String getField(String msg, String strTagID) {
		throw new RuntimeException("Not yet implemented!");
	}

	@Override
	public String overWriteTagsGeneric(String inputMsg, Set<Tag> overwrite) {
		throw new RuntimeException("Not yet implemented!");
	}

}
