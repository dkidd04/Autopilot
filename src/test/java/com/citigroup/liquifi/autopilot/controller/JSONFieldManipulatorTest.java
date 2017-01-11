package com.citigroup.liquifi.autopilot.controller;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.citigroup.liquifi.entities.Tag;

public class JSONFieldManipulatorTest {

	JSONFieldManipulator jsonUpdator;
	ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setup(){
		jsonUpdator = new JSONFieldManipulator();
	}

	@Test
	public void shouldNotChangeObjectWhenNoOverwriteFieldsInput() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequest();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		assertEquals(rtcsRequestString, resultJSON);
	}
	
	@Test
	public void shouldNotChangeExistingDoubleValueWhenNewValueIsNotDouble() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequestQtyValue();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("qty", "abcd"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		assertEquals(rtcsRequestString, resultJSON);
	}
	
	@Test
	public void shouldNotAddNewFieldWhenFieldNameIsPlaceHolder() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequestQtyValue();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("@qty", "abcd"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		assertEquals(rtcsRequestString, resultJSON);
	}
	
	@Test
	public void shouldChangeExistingBooleanValueToFalseNewValueIsNotBoolean() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequest();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("checkOk", "dummy"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestCheckOkFalse();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldNotCreateObjectInListWhenNoListElementsExist() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequest();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("newList.listValue.abc", "false"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		assertEquals(rtcsRequestString, resultJSON);
	}

	@Test
	public void shouldNotCreateObjectWhenNoObjectExists() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequest();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("newObj.abc", "false"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		assertEquals(rtcsRequestString, resultJSON);
	}
	
	@Test
	public void shouldChangeBooleanValueWhenRootLevelFieldChange() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequest();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("checkOk", "false"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestCheckOkFalse();
		assertEquals(expected, resultJSON);
	}

	@Test
	public void shouldChangeDoubleValueWhenRootLevelFieldChange() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequestQtyValue();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("qty", "0.3"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestQtyValueChange();
		assertEquals(expected, resultJSON);
	}

	@Test
	public void shouldChangeStringValueWhenRootLevelFieldChange() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequest();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("sourceSystem", "ABC"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestSourceSystemABC();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldCreateNewStringFieldAndValueWhenNoRootLevelField() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequest();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("newField", "false"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestNewField();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldChangeStringValueOnList() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequest();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("facilityList.listValue.facilityId", "ABC"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestFacilityIdChange();
		assertEquals(expected, resultJSON);
	}

	@Test
	public void shouldChangeDoubleValueOnList() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequest();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("facilityList.listValue.subLimitQty", "5.1"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestSubLimitQtyChange();
		assertEquals(expected, resultJSON);
	}

	@Test
	public void shouldChangeBooleanValueOnList() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequestFacilityOk();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("facilityList.listValue.facilityOk", "false"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestFacilityOkChange();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldCreateNewStringFieldAndValueOnListWhenDoesNotExist() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequest();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("facilityList.listValue.newField", "false"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestFacilityIdNewFieldChange();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldChangeDoubleValueOnObject() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequest();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("pseDetail.requestPseUsage.w1", "60.1"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestRequestPseUsageChange();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldChangeBooleanValueOnObject() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequestWithUpdatedRequestPseUsage();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("pseDetail.requestPseUsage.isOk", "true"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestWithUpdatedRequestPseUsageIsOkChange();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldChangeStringValueOnObject() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequestWithUpdatedRequestPseUsage();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("pseDetail.requestPseUsage.type", "delta"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestWithUpdatedRequestPseUsageTypeChange();
		assertEquals(expected, resultJSON);
	}
	
	@Test
	public void shouldCreateStringFieldAndValueOnObjectWhereNoneExists() throws JsonParseException, JsonMappingException, IOException {
		String rtcsRequestString = rtcsRequestWithUpdatedRequestPseUsage();
		Map<?,?> val = convertToMap(rtcsRequestString);
		HashSet<Tag> overwriteFields = new HashSet<Tag>();
		overwriteFields.add(new Tag("pseDetail.requestPseUsage.facilityLevel", "beta"));

		jsonUpdator.overwriteJSONFields(val, overwriteFields);
		String resultJSON = jsonObjectToString(val);
		String expected = rtcsRequestWithUpdatedRequestPseUsageFacilityLevel();
		assertEquals(expected, resultJSON);
	}
	
	private String jsonObjectToString(Map<?,?> object) throws JsonGenerationException, JsonMappingException, IOException{
		return mapper.writeValueAsString(object);
	}

	private Map<?,?> convertToMap(String rawJSON) throws JsonParseException, JsonMappingException, IOException{
		return mapper.readValue(rawJSON, Map.class);
	}

	private String rtcsRequest() throws JsonParseException, JsonMappingException, IOException{
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}

	private String rtcsRequestFacilityIdChange() throws JsonParseException, JsonMappingException, IOException{
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"ABC\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}

	private String rtcsRequestQtyValue() throws JsonParseException, JsonMappingException, IOException{
		return "{\"checkOk\":true,\"qty\":0.1,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitId\":\"0\"},{\"facilityId\":\"1203941\",\"subLimitId\":\"0\"},{\"facilityId\":\"407395\",\"subLimitId\":\"0\"},{\"facilityId\":\"753405\",\"subLimitId\":\"0\"},{\"facilityId\":\"1342936\",\"subLimitId\":\"0\"},{\"facilityId\":\"359288\",\"subLimitId\":\"0\"},{\"facilityId\":\"782833\",\"subLimitId\":\"0\"},{\"facilityId\":\"842704\",\"subLimitId\":\"0\"},{\"facilityId\":\"1308058\",\"subLimitId\":\"0\"},{\"facilityId\":\"1190962\",\"subLimitId\":\"0\"},{\"facilityId\":\"1126464\",\"subLimitId\":\"0\"},{\"facilityId\":\"1277486\",\"subLimitId\":\"0\"}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}

	private String rtcsRequestQtyValueChange() throws JsonParseException, JsonMappingException, IOException{
		return "{\"checkOk\":true,\"qty\":0.3,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitId\":\"0\"},{\"facilityId\":\"1203941\",\"subLimitId\":\"0\"},{\"facilityId\":\"407395\",\"subLimitId\":\"0\"},{\"facilityId\":\"753405\",\"subLimitId\":\"0\"},{\"facilityId\":\"1342936\",\"subLimitId\":\"0\"},{\"facilityId\":\"359288\",\"subLimitId\":\"0\"},{\"facilityId\":\"782833\",\"subLimitId\":\"0\"},{\"facilityId\":\"842704\",\"subLimitId\":\"0\"},{\"facilityId\":\"1308058\",\"subLimitId\":\"0\"},{\"facilityId\":\"1190962\",\"subLimitId\":\"0\"},{\"facilityId\":\"1126464\",\"subLimitId\":\"0\"},{\"facilityId\":\"1277486\",\"subLimitId\":\"0\"}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}

	private String rtcsRequestSourceSystemABC() throws JsonParseException, JsonMappingException, IOException{
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"ABC\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}

	private String rtcsRequestCheckOkFalse() throws JsonParseException, JsonMappingException, IOException{
		return "{\"checkOk\":false,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}

	private String rtcsRequestSubLimitQtyChange() {
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":5.1},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestFacilityOk(){
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"407395\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"753405\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"359288\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"782833\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"842704\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5,\"facilityOk\":true}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestFacilityOkChange(){
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5,\"facilityOk\":false},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"407395\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"753405\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"359288\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"782833\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"842704\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5,\"facilityOk\":true},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5,\"facilityOk\":true}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}

	private String rtcsRequestFacilityIdNewFieldChange() {
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5,\"newField\":\"false\"},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}

	private String rtcsRequestNewField() {
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true,\"newField\":\"false\"}";
	}
	
	private String rtcsRequestRequestPseUsageChange(){
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"w1\":60.1,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestWithUpdatedRequestPseUsage() throws JsonParseException, JsonMappingException, IOException{
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"type\":\"alpha\",\"isOk\":false,\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}

	private String rtcsRequestWithUpdatedRequestPseUsageIsOkChange() throws JsonParseException, JsonMappingException, IOException{
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"type\":\"alpha\",\"isOk\":true,\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestWithUpdatedRequestPseUsageTypeChange() throws JsonParseException, JsonMappingException, IOException{
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"type\":\"delta\",\"isOk\":false,\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
	private String rtcsRequestWithUpdatedRequestPseUsageFacilityLevel() throws JsonParseException, JsonMappingException, IOException{
		return "{\"checkOk\":true,\"region\":\"EMEA\",\"sourceSystem\":\"CFORE\",\"facilityList\":[{\"facilityId\":\"1204034\",\"subLimitQty\":1.5},{\"facilityId\":\"1203941\",\"subLimitQty\":1.5},{\"facilityId\":\"407395\",\"subLimitQty\":1.5},{\"facilityId\":\"753405\",\"subLimitQty\":1.5},{\"facilityId\":\"1342936\",\"subLimitQty\":1.5},{\"facilityId\":\"359288\",\"subLimitQty\":1.5},{\"facilityId\":\"782833\",\"subLimitQty\":1.5},{\"facilityId\":\"842704\",\"subLimitQty\":1.5},{\"facilityId\":\"1308058\",\"subLimitQty\":1.5},{\"facilityId\":\"1190962\",\"subLimitQty\":1.5},{\"facilityId\":\"1126464\",\"subLimitQty\":1.5},{\"facilityId\":\"1277486\",\"subLimitQty\":1.5}],\"isReg23A\":false,\"isCsa\":false,\"pseDetail\":{\"limits\":{\"w1\":6.3375E7,\"m1\":6.2375E7,\"m3\":6.2375E7,\"m6\":6.2375E7,\"y1\":6.2375E7,\"y3\":6.2375E7,\"y5\":5.5E7,\"y8\":5.4E7,\"y10\":5.4E7,\"y15\":4.6875E7,\"y999\":9875000.0},\"requestPseUsage\":{\"type\":\"alpha\",\"isOk\":false,\"w1\":2635.1365059359996,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0,\"facilityLevel\":\"beta\"},\"totalPseUsage\":{\"sod\":{\"w1\":7348380.0,\"m1\":3188356.5,\"m3\":250000.0,\"m6\":625000.0,\"y1\":625000.0,\"y3\":250000.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"deals\":{\"w1\":30527.300473806838,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"orders\":{\"w1\":2353531.431289836,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0},\"pendingSod\":{\"w1\":0.0,\"m1\":0.0,\"m3\":0.0,\"m6\":0.0,\"y1\":0.0,\"y3\":0.0,\"y5\":0.0,\"y8\":0.0,\"y10\":0.0,\"y15\":0.0,\"y999\":0.0}}},\"eqty\":[{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"},{\"cef\":0.3075,\"sCef\":0.0,\"reqId\":\"1058089306:CITADEL_GVN\"}],\"earmarkOk\":true}";
	}
	
}

