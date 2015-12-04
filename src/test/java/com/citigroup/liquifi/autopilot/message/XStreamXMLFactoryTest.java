package com.citigroup.liquifi.autopilot.message;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.citigroup.liquifi.autopilot.message.XStreamXMLFactory;
import com.citigroup.liquifi.autopilot.message.xstream.MarketPeriod;
import com.citigroup.liquifi.autopilot.message.xstream.Property;
import com.citigroup.liquifi.entities.Tag;

/**
 * @author Bobby Goodrich
 *
 */
public class XStreamXMLFactoryTest {

	XStreamXMLFactory xstreamFactory = new XStreamXMLFactory();
	
	/**
	 * Test method for {@link com.citigroup.liquifi.autopilot.message.XStreamXMLFactory#overwriteSystemPropeties(java.util.List, java.util.Set)}.
	 * Scenario: Overwrite an element in the list of {@link Property} which is not of a String type
	 * Expected Result: The overridden {@link Property} element has the correct type
	 */
	@Test
	public void testOverwriteSystemPropeties() {
		List<Property> properties = new ArrayList<Property>();
		Property property = new Property("java.lang.Boolean", false, "crossEnabled");

		//This will be modified after the method call
		Property overwrittenProperty = getCopy(property);
		properties.add(overwrittenProperty);

		//Use the same expected value but changing the form of it to a string
		Set<Tag> overwriteTags = new HashSet<Tag>();
		Tag overwriteTag = new Tag("crossEnabled", "false");
		overwriteTags.add(overwriteTag);

		xstreamFactory.overwriteSystemProperties(properties, overwriteTags);
		assertEquals(property.getValue(), overwrittenProperty.getValue());
	}

	/**
	 * Test method for {@link com.citigroup.liquifi.autopilot.message.XStreamXMLFactory#overwriteSystemPropeties(java.util.List, java.util.Set)}.
	 * Scenario: Overwrite an element in the list of {@link Property} which is a MarketPeriod enumeration
	 * Expected Result: The overridden {@link Property} element has the correct type
	 */
	@Test
	public void testOverwriteSystemPropertiesMarketPeriod() {
		List<Property> properties = new ArrayList<Property>();
		Property property = new Property("MarketPeriod", MarketPeriod.CONTINUOUS_TRADING, "marketPeriod");
		property.setType("MarketPeriod");
		property.setValue(MarketPeriod.CONTINUOUS_TRADING);
		property.setName("marketPeriod");

		//This will be modified after the method call
		Property overwrittenProperty = getCopy(property);
		properties.add(overwrittenProperty);

		//Use the same expected value but changing the form of it to a {@link MarketPeriod}
		Set<Tag> overwriteTags = new HashSet<Tag>();
		Tag overwriteTag = new Tag("marketPeriod", "CONTINUOUS_TRADING");
		overwriteTags.add(overwriteTag);

		xstreamFactory.overwriteSystemProperties(properties, overwriteTags);
		assertEquals(property.getValue(), overwrittenProperty.getValue());
	}

	/**
	 * Not a test, but a method more than one test benefits from
	 * @param property
	 * @return
	 */
	private Property getCopy(Property property) {
		Property newProperty = new Property(property.getType(), property.getValue(), property.getName());
		newProperty.setType(property.getType());
		newProperty.setValue(property.getValue());
		newProperty.setName(property.getName());
		return property;
	}
}
