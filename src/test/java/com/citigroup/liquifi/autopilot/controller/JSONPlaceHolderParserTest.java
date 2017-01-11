package com.citigroup.liquifi.autopilot.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.citigroup.liquifi.autopilot.util.PlaceHolders;

public class JSONPlaceHolderParserTest {

	private static final String APVAR_1_PLACEHOLDER = "@APVAR_1.getTag(54)";
	private static final String IP_1_PLACEHOLDER = "@IP[3].getTag(872)";
	JSONPlaceHolderParser jsonPlaceHolderParse;
	ObjectMapper mapper = new ObjectMapper();
	ValidationObject state;
	PlaceHolders placeHolders;
	PlaceHolderEvaluator placeHolderEvaluator;
	Map<String, String> placeholderMap;
	Message inputMessage;
	String inputMessageString;
	String outputMessageString;

	@Before
	public void setup(){
		state = mock(ValidationObject.class);
		placeHolderEvaluator = mock(PlaceHolderEvaluator.class);
		jsonPlaceHolderParse = new JSONPlaceHolderParser(placeHolderEvaluator);
		placeholderMap = new HashMap<String, String>();
		when(placeHolderEvaluator.evaluatePlaceholders(anyString(), eq(state), eq(placeholderMap))).thenReturn(null);
	
//		placeHolders = mock(PlaceHolders.class);
//		inputMessage = mock(Message.class);
//		when(state.getInputStepNumber()).thenReturn(3);
//		when(state.getInboundStep(1)).thenReturn(inputMessage);
//		when(inputMessage.getMessage()).thenReturn(fixMessageInput());
//		when(state.getOutputMsgByStepAndOutputMsgID(2, 1)).thenReturn(outputMessageString);
	}

	@Test
	public void shouldNotChangeObjectWhenNoPlaceholdersPresent() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequestNoPlaceholders();
		Map<String, Object> val = convertToMap(rtcsRequestString);

		jsonPlaceHolderParse.parsePlaceholders(val, placeholderMap, state);
		String resultJSON = jsonObjectToString(val);
		assertEquals(rtcsRequestString, resultJSON);
	}
	
	@Test
	public void shouldNotChangeObjectWhenPlaceholderFieldDoesNotHaveMapping() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequestWithPlaceholderAtRoot();
		Map<String, Object> val = convertToMap(rtcsRequestString);

		jsonPlaceHolderParse.parsePlaceholders(val, placeholderMap, state);
		String resultJSON = jsonObjectToString(val);
		assertEquals(rtcsRequestString, resultJSON);
	}
	
	@Test
	public void shouldNotChangeObjectWhenNotAnAPVAROrInputOutputPlaceholder() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequestWithNonStandardPlaceholderAtRoot();
		Map<String, Object> val = convertToMap(rtcsRequestString);

		jsonPlaceHolderParse.parsePlaceholders(val, placeholderMap, state);
		String resultJSON = jsonObjectToString(val);
		assertEquals(rtcsRequestString, resultJSON);
	}
	
	@Test
	public void shouldChangeRootFieldWhenPlaceholderFieldHasMapping() throws JsonParseException, JsonMappingException, IOException {
		when(placeHolderEvaluator.evaluatePlaceholders(APVAR_1_PLACEHOLDER, state, placeholderMap)).thenReturn("342");
		String rtcsRequestString = rtcsRequestWithPlaceholderAtRoot();
		Map<String, Object> val = convertToMap(rtcsRequestString);
		
		jsonPlaceHolderParse.parsePlaceholders(val, placeholderMap, state);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestWithAPVarReplacedAtRoot();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldChangeListWhenPlaceholderFieldHasMapping() throws JsonParseException, JsonMappingException, IOException {
		when(placeHolderEvaluator.evaluatePlaceholders(APVAR_1_PLACEHOLDER, state, placeholderMap)).thenReturn("342");
		String rtcsRequestString = rtcsRequestWithAPVARPlaceholderInList();
		Map<String, Object> val = convertToMap(rtcsRequestString);
		
		jsonPlaceHolderParse.parsePlaceholders(val, placeholderMap, state);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestWithAPVarReplacedInList();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldChangeObjectWhenPlaceholderFieldHasMapping() throws JsonParseException, JsonMappingException, IOException {
		when(placeHolderEvaluator.evaluatePlaceholders(APVAR_1_PLACEHOLDER, state, placeholderMap)).thenReturn("342");
		String rtcsRequestString = rtcsRequestWithAPVARPlaceholderInObject();
		Map<String, Object> val = convertToMap(rtcsRequestString);
		
		jsonPlaceHolderParse.parsePlaceholders(val, placeholderMap, state);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestWithAPVARPlaceholderReplacedInObject();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldChangeMultipleFieldsWhenMultiplePlaceholderFields() throws JsonParseException, JsonMappingException, IOException {
		when(placeHolderEvaluator.evaluatePlaceholders(APVAR_1_PLACEHOLDER, state, placeholderMap)).thenReturn("342");
		String rtcsRequestString = rtcsRequestWithMultipleAPVARPlaceholders();
		Map<String, Object> val = convertToMap(rtcsRequestString);
		
		jsonPlaceHolderParse.parsePlaceholders(val, placeholderMap, state);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestWithMultipleAPVARPlaceholderReplaced();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldChangeObjectWhenTwoDifferentPlaceholderFields() throws JsonParseException, JsonMappingException, IOException {
		when(placeHolderEvaluator.evaluatePlaceholders(APVAR_1_PLACEHOLDER, state, placeholderMap)).thenReturn("sjbw");
		when(placeHolderEvaluator.evaluatePlaceholders(IP_1_PLACEHOLDER, state, placeholderMap)).thenReturn("fehfej");
		String rtcsRequestString = rtcsRequestMultipleDifferentPlaceholders();
		Map<String, Object> val = convertToMap(rtcsRequestString);
		
		jsonPlaceHolderParse.parsePlaceholders(val, placeholderMap, state);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestMultipleDifferentPlaceholdersReplaced();
		assertEquals(expected, resultJSON);
	}

	private String jsonObjectToString(Map<String, Object> object) throws JsonGenerationException, JsonMappingException, IOException{
		return mapper.writeValueAsString(object);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> convertToMap(String rawJSON) throws JsonParseException, JsonMappingException, IOException{
		return mapper.readValue(rawJSON, Map.class);
	}
	
	private String rtcsRequestNoPlaceholders() {
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestWithPlaceholderAtRoot() {
		return "{\"checkOk\":true,\"region\":\"@APVAR_1.getTag(54)\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestWithNonStandardPlaceholderAtRoot() {
		return "{\"checkOk\":true,\"region\":\"@FUNC.getTag(54)\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}

	private String rtcsRequestWithAPVarReplacedAtRoot() {
		return "{\"checkOk\":true,\"region\":\"342\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestWithAPVARPlaceholderInList() {
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"@APVAR_1.getTag(54)\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
		}

	private String rtcsRequestWithAPVarReplacedInList() {
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"342\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
		}
	
	private String rtcsRequestWithAPVARPlaceholderInObject() {
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":\"@APVAR_1.getTag(54)\",\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestWithAPVARPlaceholderReplacedInObject() {
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":\"342\",\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestWithMultipleAPVARPlaceholders() {
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"@APVAR_1.getTag(54)\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"@APVAR_1.getTag(54)\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":\"@APVAR_1.getTag(54)\",\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":\"@APVAR_1.getTag(54)\",\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"@APVAR_1.getTag(54)\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestWithMultipleAPVARPlaceholderReplaced() {
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"342\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"342\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":\"342\",\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":\"342\",\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"342\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestMultipleDifferentPlaceholders() {
		return "{\"checkOk\":true,\"region\":\""+APVAR_1_PLACEHOLDER+"\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\""+IP_1_PLACEHOLDER+"\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestMultipleDifferentPlaceholdersReplaced() {
		return "{\"checkOk\":true,\"region\":\"sjbw\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"fehfej\"}],\"earmarkOk\":true}";
	}

	
}
