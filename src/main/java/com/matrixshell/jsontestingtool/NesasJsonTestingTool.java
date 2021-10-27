package com.matrixshell.jsontestingtool;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;

import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

public class NesasJsonTestingTool {
	private JsonObject jsonObjectOrg;
	private List<String> jsonDuplicateList = new ArrayList<String>();
	private boolean completeArrDuplication;
	private Optional<Integer> levels;
	private List<String> sampleJsonTesting = new ArrayList<String>();

	public NesasJsonTestingTool() {
		System.out.println("default");
		completeArrDuplication = false;
		levels = Optional.empty();
	}

	public NesasJsonTestingTool(boolean completeArrDuplication) {
		this.completeArrDuplication = completeArrDuplication;
		levels = Optional.empty();
	}

	public NesasJsonTestingTool(Integer levels) {
		System.out.println("levels constructor");
		this.levels = Optional.of(levels - 1);
	}

	public NesasJsonTestingTool(boolean completeArrDuplication, int levels) {
		this.completeArrDuplication = completeArrDuplication;
		this.levels = Optional.of(levels - 1);
	}

	private NesasJsonTestingTool(boolean completeArrDuplication, List<String> sampleJsonTesting) {
		this.completeArrDuplication = completeArrDuplication;
		this.sampleJsonTesting = sampleJsonTesting;
	}

	private void makeRequest(JsonObject jsonObject, String jsonKey, JsonElement jsonValue, Optional<String> parentKey,
			List<String> parentKeyList, List<Entry<String, JsonElement>> entries, int start, int end) {
		String json = createDuplicateKeyValueJson(jsonObject, jsonKey, jsonValue, parentKey, parentKeyList, start, end);
		this.jsonDuplicateList.add(json);
	}

	private String createDuplicateKeyValueJson(JsonObject jsonObject, String jsonKey, JsonElement jsonValue,
			Optional<String> parentKey, List<String> parentKeyList, int start, int end) {
		JsonObject jsonObjectTestData = new Gson().fromJson(jsonObject, JsonObject.class);
		jsonObjectTestData.add(jsonKey + " ", jsonValue);
		String duplicateJsonContent = null;
		if (parentKey.isEmpty()) {
			duplicateJsonContent = jsonObjectTestData.toString().replaceAll("\\s", "");
			return duplicateJsonContent;
		} else {
			duplicateJsonContent = jsonObjectTestData.toString().replaceAll("\\s", "");
			String jsonString = jsonObjectOrg.toString();
			StringBuffer jsonStringBuffer = new StringBuffer(jsonString);
			jsonStringBuffer.replace(start, end + 1, duplicateJsonContent);
			return jsonStringBuffer.toString();
		}

	}

	private void recursiveJsonObj2(ListIterator<Entry<String, JsonElement>> listIterator, JsonObject jsonObject,
			Optional<String> parentKey, List<String> parentKeyList, List<Entry<String, JsonElement>> entries, int start,
			int end) {
		if (!listIterator.hasNext())
			return;
		Entry<String, JsonElement> entry = listIterator.next();
		String jsonKey = entry.getKey();
		JsonElement jsonValue = entry.getValue();
		if (levels.isPresent() && parentKeyList.size() > levels.get()) {
			return;
		}
		if (jsonValue.isJsonPrimitive()) {
			makeRequest(jsonObject, jsonKey, jsonValue, parentKey, parentKeyList, entries, start, end);
		} else if (jsonValue.isJsonObject()) {
			makeRequest(jsonObject, jsonKey, jsonValue, parentKey, parentKeyList, entries, start, end);
			parentKey = Optional.of(jsonKey);
			parentKeyList.add(jsonKey);

			JsonObject childJson = jsonValue.getAsJsonObject();

			List<Entry<String, JsonElement>> entriesList = new ArrayList<Entry<String, JsonElement>>(
					childJson.entrySet());
			ListIterator<Entry<String, JsonElement>> entriesIterator = entriesList.listIterator();
			int resetStart = start;
			int resetEnd = end;
			start = start + jsonObject.toString().indexOf("\"" + jsonKey + "\"") + jsonKey.length() + 3;
			end = start + childJson.toString().length() - 1;
			recursiveJsonObj2(entriesIterator, childJson, parentKey, parentKeyList, entries, start, end);
			start = resetStart;
			end = resetEnd;
			parentKeyList.remove(jsonKey);
			if (parentKeyList.size() <= 0) {
				parentKey = Optional.empty();
			} else {
				parentKey = Optional.of(parentKeyList.get(parentKeyList.size() - 1));
			}

		} else if (jsonValue.isJsonArray()) {
			makeRequest(jsonObject, jsonKey, jsonValue, parentKey, parentKeyList, entries, start, end);
			JsonArray childJsonArray = jsonValue.getAsJsonArray();
			parentKey = Optional.of(jsonKey);
			parentKeyList.add(jsonKey);
			Type listType = new TypeToken<List<JsonElement>>() {
			}.getType();
			List<JsonElement> jsonArrayList = new Gson().fromJson(childJsonArray, listType);
			ListIterator<JsonElement> jsonListIterator = jsonArrayList.listIterator();
			int resetStart = start;
			int resetEnd = end;
			start = start + jsonObject.toString().indexOf("\"" + jsonKey + "\"") + jsonKey.length() + 4;
			end = start + childJsonArray.toString().length() - 1;

			recursiveJsonArrObj2(childJsonArray, jsonListIterator, parentKey, parentKeyList, entries, start, end);
			start = resetStart;
			end = resetEnd;
			parentKeyList.remove(jsonKey);
			if (parentKeyList.size() <= 0) {
				parentKey = Optional.empty();
			} else {
				parentKey = Optional.of(parentKeyList.get(parentKeyList.size() - 1));
			}
		}
		recursiveJsonObj2(listIterator, jsonObject, parentKey, parentKeyList, entries, start, end);
	}

	private void recursiveJsonArrObj2(JsonArray jsonArray, ListIterator<JsonElement> jsonListIterator,
			Optional<String> superParentKey, List<String> parentKeyList, List<Entry<String, JsonElement>> entries,
			int start, int end) {
		if (levels.isPresent() && parentKeyList.size() > levels.get()) {
			return;
		}
		if (!jsonListIterator.hasNext())
			return;
		if (!completeArrDuplication && jsonListIterator.nextIndex() > 0) {
			return;
		}
		JsonElement jsonElement = jsonListIterator.next();
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			start = start + jsonPrimitive.toString().length() + 1;
		}

		if (jsonElement.isJsonObject()) {
			JsonObject json = jsonElement.getAsJsonObject();
			List<Entry<String, JsonElement>> entriesList = new ArrayList<Entry<String, JsonElement>>(json.entrySet());
			ListIterator<Entry<String, JsonElement>> entriesIterator = entriesList.listIterator();
			end = start + json.toString().length() - 1;
			recursiveJsonObj2(entriesIterator, json, superParentKey, parentKeyList, entries, start, end);
			start = end + 2;
		}

		if (jsonElement.isJsonArray()) {
			JsonArray jsonArray2 = jsonElement.getAsJsonArray();
			Type listType = new TypeToken<List<JsonElement>>() {
			}.getType();
			List<JsonElement> jsonArrayList = new Gson().fromJson(jsonArray2, listType);
			end = start + jsonArray2.toString().length() - 1;
			start = start + 1;
			recursiveJsonArrObj2(jsonArray2, jsonArrayList.listIterator(), superParentKey, parentKeyList, entries,
					start, end);
			start = end + 2;
		}
		recursiveJsonArrObj2(jsonArray, jsonListIterator, superParentKey, parentKeyList, entries, start, end);
	}

	public List<String> jsonDuplicateList(String json) {
		jsonObjectOrg = new Gson().fromJson(json, JsonObject.class);
		String jsonString = jsonObjectOrg.toString();
		System.out.println("Input String: " + jsonString);
		Optional<String> parentKey = Optional.empty();
		List<String> parentKeyList = new ArrayList<String>();
		Set<Entry<String, JsonElement>> entries = jsonObjectOrg.entrySet();
		List<Entry<String, JsonElement>> entriesList = new ArrayList<Entry<String, JsonElement>>(entries);
		ListIterator<Entry<String, JsonElement>> entriesIterator = entriesList.listIterator();
		int start = 0;
		int end = jsonString.length() - 1;
		recursiveJsonObj2(entriesIterator, jsonObjectOrg, parentKey, parentKeyList, entriesList, start, end);
		return jsonDuplicateList;
	}

	private List<String> sampleJsonTesting() {
		List<String> list = new ArrayList<String>();

		String json0 = "{\"amfStatusUri\":\"amfStatusUri\",\"amfStatusUri\":\"amfStatusUri\",\"guamiList\":[{\"plmnId\":{\"mnc\":\"mnc\",\"mcc\":\"mcc\"},\"amfId\":\"amfId\"},{\"plmnId\":{\"mnc\":\"mnc\",\"mcc\":\"mcc\"},\"amfId\":\"amfId\"}]}";
		String json1 = "{\"test\":[\"a\",{\"a Key\":\"a val\"},\"c\"]}";
		String json2 = "{\"test\":[\"a\",{\"a Key\":\"a val\"},\"c\",{\"d\":[\"a\",{\"akey\":\"aval\"}]}]}";
		String json3 = "{\"test\":[\"a\",{\"a Key\":\"a val\"},\"c\",{\"d\":[\"a\",{\"akey\":\"aval\"},{\"objkey\":{\"bkey\":\"bkey\",\"ckey\":\"ckey\"}}]}]}";
		String json4 = "{\"amfStatusUri\":\"1234\",\"guamiList\":[{\"plmnId\":{\"mcc\":\"123\",\"mnc\":\"12\"},\"amfId\":\"123\"},{\"plmnId\":{\"mcc\":\"123\",\"mnc\":\"12\"},\"amfId\":\"123\"}]}";
		String json5 = "{\"amfStatusUri\":\"1234\",\"guamiList\":{\"testk1\":\"testv1\",\"amfStatusUri\":\"1234\",\"testobj2\":{\"testk2\":\"testv2\"}}}";
		String json6 = "{\"amfStatusUri\":\"1234\",\"guamiList\":{\"plmnId\":{\"mcc\":\"123\",\"mnc\":\"12\"},\"amfid\":\"amfid\"}}";
		String json7 = "{\"k1\":\"v1\",\"k2\":{\"k21\":{\"k211\":\"v211\",\"k212\":\"v212\"},\"k22\":\"v22\"}}";
		String json8 = "{\"k1\":\"v1\",\"k2\":{\"k21\":\"v21\",\"k22\":\"v22\"},\"k3\":\"v3\"}";// cross checking logic
		String json9 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\"}}";// cross
																																// checking
																																// logic
		String json10 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\"}";// cross
																																										// checking
																																										// logic
		String json11 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"earr1\",{\"ekarr2\":\"evarr2\"}]}";
		String json12 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"hello\",\"earr1\",{\"ekarr2\":\"evarr2\"}]}";
		String json13 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"hello\",\"earr1\",{\"ekarr2\":\"evarr2\"},[\"hello\",\"earr1\",{\"ekarr2\":\"evarr2\"}]]}";
		String json14 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"hello\",\"earr1\",{\"ekarr2\":\"evarr2\"},[\"hello\",\"earr1\",{\"ekarr2\":\"evarr2\"}],\"hello\",{\"ekarr2\":\"evarr2\"}]}";
		String json15 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"earr1\",{\"ekarr2\":\"evarr2\"},[\"newearr1\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String json16 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String json17 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"ekey\":[{\"akeyarr\":\"avalarr\"},{\"bkeyarr\":\"bvalarr\"},{\"ckeyarr\":\"cvalarr\"}]}";
		String json18 = "{\"amfStatusUri\":\"1234\",\"guamiList\":[{\"plmnId\":{\"mcc\":\"123\",\"mnc\":\"12\"},\"amfId\":\"123\"},{\"plmnId\":{\"mcc\":\"321\",\"mnc\":\"21\"},\"amfId\":\"321\"}]}";

		String json19 = "{\"k1\":\"v1\",\"k2\":{\"k21\":{\"k211\":\"v211\",\"k212\":{\"k2121\":{\"k21211\":\"v21211\",\"k21212\":{\"k212121\":\"v212121\",\"k212122\":{}}}}},\"k22\":\"v22\",\"k23\":{\"k231\":\"v211\",\"k232\":\"v212\"},\"k24\":{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}}}";
		String json20 = "{\"k1\":\"v1\",\"k2\":{\"k21\":{\"k211\":\"v211\",\"k212\":{\"k2121\":{\"k21211\":\"v21211\",\"k21212\":{\"k212121\":\"v212121\",\"k212122\":{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}}}}},\"k22\":\"v22\",\"k23\":{\"k231\":\"v211\",\"k232\":\"v212\"},\"k24\":{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}}}";

		list.add(json0);
		list.add(json1);
		list.add(json2);
		list.add(json3);
		list.add(json4);
		list.add(json5);
		list.add(json6);
		list.add(json7);
		list.add(json8);
		list.add(json9);
		list.add(json10);
		list.add(json11);
		list.add(json12);
		list.add(json13);
		list.add(json14);
		list.add(json15);
		list.add(json16);
		list.add(json17);
		list.add(json18);

		list.add(json19);

		list.add(json20);

		return list;
	}

	public static void main(String[] args) {

//		NesasJsonTestingTool tools = new NesasJsonTestingTool(true);
		NesasJsonTestingTool tools = new NesasJsonTestingTool(2);
		String json = tools.sampleJsonTesting().get(19);
		tools.jsonDuplicateList(json).forEach(System.out::println);
	}
}
