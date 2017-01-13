package com.citigroup.liquifi.autopilot.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.citigroup.liquifi.autopilot.util.PlaceHolders;

public class PlaceHolderEvaluatorTest {

	private static final String APVAR_1_PLACEHOLDER = "@APVAR_1.getTag(54)";
	private static final String APVAR_2_PLACEHOLDER = "@APVAR_2.getTag(642)";
	private static final String IP_2_PLACEHOLDER = "@IP[2].getTag(54)";
	private static final String OP_1_3_PLACEHOLDER = "@OP[1][3].getTag(534)";
	private static final String NON_STANDARD_PLACEHOLDER = "@UNKNOWN.getField(abc)";
	private static final String RESULT = "result";
	private static final String OUTPUT_RESULT = "output_result";
	PlaceHolderEvaluator placeHolderEvaluator;
	ValidationObject state;
	PlaceHolders placeHolders;
	Map<String, String> placeholderMap;

	@Before
	public void setup(){
		state = mock(ValidationObject.class);
		placeHolders = mock(PlaceHolders.class);
		placeHolderEvaluator = new PlaceHolderEvaluator(placeHolders);
		placeholderMap = new HashMap<String, String>();
		placeholderMap.put("@APVAR_1","@IP[2]");
	
		when(state.getInputStepNumber()).thenReturn(3);
	}

	@Test
	public void shouldReplaceAPVARWithMappedInputStep(){
		String outputValue = placeHolderEvaluator.replaceAPVarPlaceholder(APVAR_1_PLACEHOLDER, placeholderMap);
		String expectedValue = IP_2_PLACEHOLDER;
		assertEquals(expectedValue, outputValue);
	}
	
	@Test
	public void shouldNotReplaceNonAPVARPlaceholderWhenMappingExists(){
		placeholderMap.put(NON_STANDARD_PLACEHOLDER, IP_2_PLACEHOLDER);
		String outputValue = placeHolderEvaluator.replaceAPVarPlaceholder(NON_STANDARD_PLACEHOLDER, placeholderMap);
		String expectedValue = NON_STANDARD_PLACEHOLDER;
		assertEquals(expectedValue, outputValue);
	}
	
	@Test
	public void shouldNotReplaceAPVARPlaceholderWhenNoMappingExists(){
		String outputValue = placeHolderEvaluator.replaceAPVarPlaceholder(APVAR_2_PLACEHOLDER, placeholderMap);
		String expectedValue = APVAR_2_PLACEHOLDER;
		assertEquals(expectedValue, outputValue);
	}
	
	@Test
	public void shouldCallPlaceholdersAndReturnResultForInputMessage() throws Exception {
		when(placeHolders.parsePlaceHolderInput("", 3, state, IP_2_PLACEHOLDER)).thenReturn(RESULT);
		String outputValue = placeHolderEvaluator.evaluateInputAndOutputPlaceholders(IP_2_PLACEHOLDER, state);
		String expectedValue = RESULT;
		assertEquals(expectedValue, outputValue);
	}
	
	@Test
	public void shouldReturnNullResultWhenNoInputValueFoundForPlaceholder() throws Exception {
		when(placeHolders.parsePlaceHolderInput("", 3, state, IP_2_PLACEHOLDER)).thenReturn(null);
		String outputValue = placeHolderEvaluator.evaluateInputAndOutputPlaceholders(IP_2_PLACEHOLDER, state);
		String expectedValue = null;
		assertEquals(expectedValue, outputValue);
	}

	@Test
	public void shouldReturnCallPlaceholdersAndReturnResult() throws Exception {
		when(placeHolders.parsePlaceHolderOutput("", 3, OP_1_3_PLACEHOLDER, state)).thenReturn(OUTPUT_RESULT);
		String outputValue = placeHolderEvaluator.evaluateInputAndOutputPlaceholders(OP_1_3_PLACEHOLDER, state);
		String expectedValue = OUTPUT_RESULT;
		assertEquals(expectedValue, outputValue);
	}
	
	@Test
	public void shouldReturnNullWhenOutputPlaceholderEvaluationFails() throws Exception {
		when(placeHolders.parsePlaceHolderOutput("", 3, OP_1_3_PLACEHOLDER, state)).thenReturn(null);
		String outputValue = placeHolderEvaluator.evaluateInputAndOutputPlaceholders(OP_1_3_PLACEHOLDER, state);
		String expectedValue = null;
		assertEquals(expectedValue, outputValue);
	}
	
	@Test
	public void shouldReturnPlaceholderValueWhenPlaceHolderIsNotInputOrOutput() throws Exception {
		String outputValue = placeHolderEvaluator.evaluateInputAndOutputPlaceholders(NON_STANDARD_PLACEHOLDER, state);
		String expectedValue = NON_STANDARD_PLACEHOLDER;
		assertEquals(expectedValue, outputValue);
	}
	
}
