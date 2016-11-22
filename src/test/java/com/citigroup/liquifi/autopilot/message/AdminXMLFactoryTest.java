package com.citigroup.liquifi.autopilot.message;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.citigroup.liquifi.entities.Tag;

public class AdminXMLFactoryTest {

	AdminXMLFactory xmlFactory;
	Set<Tag> replaceTagSet;

	String xmlMessage = "<QDMAEventMsg><Action>PROFILE</Action><Parameters><Instance>C4C_UKDEV_CI_1</Instance><Customer><ClearCache>Y</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>Y</Value></SetProfile></Customer></Parameters></QDMAEventMsg>";

	@Before
	public void setup(){
		xmlFactory = new AdminXMLFactory();
		replaceTagSet = new HashSet<Tag>();
	}

	@Test
	public void shouldGetListValueField() {
		String input = "<QDMAEventMsg><Action>PROFILE</Action><Parameters><Instance>C4C_UKDEV_CI_1</Instance><Customer><ClearCache>Y</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>Y</Value></SetProfile></Customer></Parameters></QDMAEventMsg>";
		String expected = "Y";
		String actual = xmlFactory.getField(input, "Parameters.Customer.SetProfile.BlockAutoAck.listValue");
	assertEquals(expected, actual);
	}

	@Test
	public void shouldReturnNullWhenNoListValueField() {
		String expected = null;
		String fieldValue = xmlFactory.getField(xmlMessage, "Parameters.Customer.SetProfile.f.listValue");
		assertEquals(expected, fieldValue);
	}

	@Test
	public void shouldReturnValueField() {
		String expected = "PROFILE";
		String fieldValue = xmlFactory.getField(xmlMessage, "Action");
		assertEquals(expected, fieldValue);
	}
	
	@Test
	public void shouldReturnNestedValueField() {
		String expected = "Y";
		String fieldValue = xmlFactory.getField(xmlMessage, "Parameters.Customer.ClearCache");
		assertEquals(expected, fieldValue);
	}

	@Test
	public void shouldReturnNullWhenNoValueField() {
		String expected = null;
		String fieldValue = xmlFactory.getField(xmlMessage, "ActionGGG");
		assertEquals(expected, fieldValue);
	}

	@Test
	public void shouldOverwriteExistingValueField() throws Exception {
		String expected = "<QDMAEventMsg><Action>newVal</Action><Parameters><Instance>C4C_UKDEV_CI_1</Instance><Customer><ClearCache>Y</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>Y</Value></SetProfile></Customer></Parameters></QDMAEventMsg>";
		Tag existingValueField = new Tag("Action", "newVal");
		replaceTagSet.add(existingValueField);
		String actual = xmlFactory.overWriteTagsGeneric(xmlMessage, replaceTagSet);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldOverwriteExistingNestedValueField() throws Exception {
		String expected = "<QDMAEventMsg><Action>PROFILE</Action><Parameters><Instance>C4C_UKDEV_CI_1</Instance><Customer><ClearCache>N</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>Y</Value></SetProfile></Customer></Parameters></QDMAEventMsg>";
		Tag existingValueField = new Tag("Parameters.Customer.ClearCache", "N");
		replaceTagSet.add(existingValueField);
		String actual = xmlFactory.overWriteTagsGeneric(xmlMessage, replaceTagSet);
		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldCreateNewFieldWhenValueFieldDoesNotExist() throws Exception {
		String input = 		"<QDMAEventMsg><Parameters><Instance>C4C_UKDEV_CI_1</Instance><Customer><ClearCache>Y</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>Y</Value></SetProfile></Customer></Parameters></QDMAEventMsg>";
		String expected = 	"<QDMAEventMsg><Parameters><Instance>C4C_UKDEV_CI_1</Instance><Customer><ClearCache>Y</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>Y</Value></SetProfile></Customer></Parameters><ActionGGG>newVal</ActionGGG></QDMAEventMsg>";
		Tag existingValueField = new Tag("ActionGGG", "newVal");
		replaceTagSet.add(existingValueField);
		String actual = xmlFactory.overWriteTagsGeneric(input, replaceTagSet);
		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldCreateNewParentTreeWhenValueFieldDoesNotExist() throws Exception {
		String input =    	"<QDMAEventMsg><Parameters><Customer><ClearCache>Y</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>Y</Value></SetProfile></Customer></Parameters></QDMAEventMsg>";
		String expected = 	"<QDMAEventMsg><Parameters><Customer><ClearCache>Y</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>Y</Value></SetProfile><Instance>newVal</Instance></Customer></Parameters></QDMAEventMsg>";
		Tag existingValueField = new Tag("Parameters.Customer.Instance", "newVal");
		replaceTagSet.add(existingValueField);
		String actual = xmlFactory.overWriteTagsGeneric(input, replaceTagSet);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldOverwriteExistingListValueField() throws Exception {
		String input = "<QDMAEventMsg><Action>PROFILE</Action><Parameters><Instance>C4C_UKDEV_CI_1</Instance><Customer><ClearCache>Y</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>Y</Value></SetProfile></Customer></Parameters></QDMAEventMsg>";
		String expected = "<QDMAEventMsg><Action>PROFILE</Action><Parameters><Instance>C4C_UKDEV_CI_1</Instance><Customer><ClearCache>Y</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>N</Value></SetProfile></Customer></Parameters></QDMAEventMsg>";
		Tag existingValueField = new Tag("Parameters.Customer.SetProfile.BlockAutoAck.listValue", "N");
		replaceTagSet.add(existingValueField);
		String actual = xmlFactory.overWriteTagsGeneric(input, replaceTagSet);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldCreateNewFieldWhenListValueFieldDoesNotExist() throws Exception {
		String input = "<QDMAEventMsg><Action>PROFILE</Action><Parameters><Instance>C4C_UKDEV_CI_1</Instance><Customer><ClearCache>Y</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>Y</Value></SetProfile></Customer></Parameters></QDMAEventMsg>";
		String expected = "<QDMAEventMsg><Action>PROFILE</Action><Parameters><Instance>C4C_UKDEV_CI_1</Instance><Customer><ClearCache>Y</ClearCache><CustomerName>C4CI40S1</CustomerName><SetProfile><Name>a</Name><Value>G</Value></SetProfile><SetProfile><Name>BlockAutoAck</Name><Value>Y</Value></SetProfile><SetProfile><Name>GBPFlag</Name><Value>N</Value></SetProfile></Customer></Parameters></QDMAEventMsg>";
		Tag existingValueField = new Tag("Parameters.Customer.SetProfile.GBPFlag.listValue", "N");
		replaceTagSet.add(existingValueField);
		String actual = xmlFactory.overWriteTagsGeneric(input, replaceTagSet);
		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldCreateNewParentTreeWhenListValueFieldDoesNotExist() throws Exception {
		String input = 		"<QDMAEventMsg><Action>PROFILE</Action><Parameters><Instance>C4C_UKDEV_CI_1</Instance></Parameters></QDMAEventMsg>";
		String expected = 	"<QDMAEventMsg><Action>PROFILE</Action><Parameters><Instance>C4C_UKDEV_CI_1</Instance><Customer><SetProfile><Name>GBPFlag</Name><Value>N</Value></SetProfile></Customer></Parameters></QDMAEventMsg>";
		Tag existingValueField = new Tag("Parameters.Customer.SetProfile.GBPFlag.listValue", "N");
		replaceTagSet.add(existingValueField);
		String actual = xmlFactory.overWriteTagsGeneric(input, replaceTagSet);
		assertEquals(expected, actual);
	}
	

}
