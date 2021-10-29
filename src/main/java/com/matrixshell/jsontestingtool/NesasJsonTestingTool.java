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

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * A class for creating duplicates of JSON Key-value pair according to the requirements.
 * 
 * 
 * @author Akib Sayyed
 * @author Muheeb Shekh
 *
 */

@Slf4j
public class NesasJsonTestingTool {
	private JsonObject jsonObjectOrg;
	private List<String> jsonDuplicateList = new ArrayList<String>();
	private boolean completeArrDuplication;
	private Optional<Integer> levels;
	@SuppressWarnings("unused")
	private List<String> sampleJsonTesting = new ArrayList<String>();

	public NesasJsonTestingTool() {
		completeArrDuplication = false;
		levels = Optional.empty();
	}

	public NesasJsonTestingTool(boolean completeArrDuplication) {
		this.completeArrDuplication = completeArrDuplication;
		levels = Optional.empty();
	}

	public NesasJsonTestingTool(Integer levels) {
		this.levels = Optional.of(levels - 1);
	}

	public NesasJsonTestingTool(boolean completeArrDuplication, int levels) {
		this.completeArrDuplication = completeArrDuplication;
		this.levels = Optional.of(levels - 1);
	}

	@SuppressWarnings("unused")
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

	private void recursiveJsonObjItr(ListIterator<Entry<String, JsonElement>> listIterator, JsonObject jsonObject,
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
			recursiveJsonObjItr(entriesIterator, childJson, parentKey, parentKeyList, entries, start, end);
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

			recursiveJsonArrObjItr(childJsonArray, jsonListIterator, parentKey, parentKeyList, entries, start, end);
			start = resetStart;
			end = resetEnd;
			parentKeyList.remove(jsonKey);
			if (parentKeyList.size() <= 0) {
				parentKey = Optional.empty();
			} else {
				parentKey = Optional.of(parentKeyList.get(parentKeyList.size() - 1));
			}
		}
		recursiveJsonObjItr(listIterator, jsonObject, parentKey, parentKeyList, entries, start, end);
	}

	private void recursiveJsonArrObjItr(JsonArray jsonArray, ListIterator<JsonElement> jsonListIterator,
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
			recursiveJsonObjItr(entriesIterator, json, superParentKey, parentKeyList, entries, start, end);
			start = end + 2;
		}

		if (jsonElement.isJsonArray()) {
			JsonArray jsonArray2 = jsonElement.getAsJsonArray();
			Type listType = new TypeToken<List<JsonElement>>() {
			}.getType();
			List<JsonElement> jsonArrayList = new Gson().fromJson(jsonArray2, listType);
			end = start + jsonArray2.toString().length() - 1;
			start = start + 1;
			recursiveJsonArrObjItr(jsonArray2, jsonArrayList.listIterator(), superParentKey, parentKeyList, entries,
					start, end);
			start = end + 2;
		}
		recursiveJsonArrObjItr(jsonArray, jsonListIterator, superParentKey, parentKeyList, entries, start, end);
	}

	public List<String> jsonDuplicateList(String json) {
		jsonObjectOrg = new Gson().fromJson(json, JsonObject.class);
		String jsonString = jsonObjectOrg.toString();
		log.info("Input String: {}", jsonString);
		Optional<String> parentKey = Optional.empty();
		List<String> parentKeyList = new ArrayList<String>();
		Set<Entry<String, JsonElement>> entries = jsonObjectOrg.entrySet();
		List<Entry<String, JsonElement>> entriesList = new ArrayList<Entry<String, JsonElement>>(entries);
		ListIterator<Entry<String, JsonElement>> entriesIterator = entriesList.listIterator();
		int start = 0;
		int end = jsonString.length() - 1;
		recursiveJsonObjItr(entriesIterator, jsonObjectOrg, parentKey, parentKeyList, entriesList, start, end);
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
		String json21 = "{ \"ueContext\" : { \"gpsiList\" : [ null, null ], \"seafData\" : { \"keyAmfChangeInd\" : true, \"ngKsi\" : { \"ksi\" : 0 }, \"keyAmf\" : { \"keyVal\" : \"keyVal\" }, \"keyAmfHDerivationInd\" : true, \"nh\" : \"nh\", \"ncc\" : 4 }, \"eventSubscriptionList\" : [ { \"eventNotifyUri\" : \"eventNotifyUri\", \"nfId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"eventList\" : [ { \"subscribedDataFilterList\" : [ null, null ], \"areaList\" : [ { \"presenceInfo\" : { \"ecgiList\" : [ { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"globalRanNodeIdList\" : [ { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" } ], \"praId\" : \"praId\", \"trackingAreaList\" : [ { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"ncgiList\" : [ { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" } ] }, \"ladnInfo\" : { \"ladn\" : \"ladn\" } }, { \"presenceInfo\" : { \"ecgiList\" : [ { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"globalRanNodeIdList\" : [ { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" } ], \"praId\" : \"praId\", \"trackingAreaList\" : [ { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"ncgiList\" : [ { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" } ] }, \"ladnInfo\" : { \"ladn\" : \"ladn\" } } ], \"locationFilterList\" : [ null, null ], \"immediateFlag\" : true }, { \"subscribedDataFilterList\" : [ null, null ], \"areaList\" : [ { \"presenceInfo\" : { \"ecgiList\" : [ { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"globalRanNodeIdList\" : [ { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" } ], \"praId\" : \"praId\", \"trackingAreaList\" : [ { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"ncgiList\" : [ { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" } ] }, \"ladnInfo\" : { \"ladn\" : \"ladn\" } }, { \"presenceInfo\" : { \"ecgiList\" : [ { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"globalRanNodeIdList\" : [ { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" } ], \"praId\" : \"praId\", \"trackingAreaList\" : [ { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"ncgiList\" : [ { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" } ] }, \"ladnInfo\" : { \"ladn\" : \"ladn\" } } ], \"locationFilterList\" : [ null, null ], \"immediateFlag\" : true } ], \"subsChangeNotifyUri\" : \"subsChangeNotifyUri\", \"pei\" : \"pei\", \"anyUE\" : true, \"groupId\" : \"groupId\", \"options\" : { \"expiry\" : \"2000-01-23T04:56:07.000+00:00\", \"maxReports\" : 7 }, \"supi\" : \"supi\", \"notifyCorrelationId\" : \"notifyCorrelationId\", \"subsChangeNotifyCorrelationId\" : \"subsChangeNotifyCorrelationId\", \"gpsi\" : \"gpsi\" }, { \"eventNotifyUri\" : \"eventNotifyUri\", \"nfId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"eventList\" : [ { \"subscribedDataFilterList\" : [ null, null ], \"areaList\" : [ { \"presenceInfo\" : { \"ecgiList\" : [ { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"globalRanNodeIdList\" : [ { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" } ], \"praId\" : \"praId\", \"trackingAreaList\" : [ { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"ncgiList\" : [ { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" } ] }, \"ladnInfo\" : { \"ladn\" : \"ladn\" } }, { \"presenceInfo\" : { \"ecgiList\" : [ { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"globalRanNodeIdList\" : [ { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" } ], \"praId\" : \"praId\", \"trackingAreaList\" : [ { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"ncgiList\" : [ { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" } ] }, \"ladnInfo\" : { \"ladn\" : \"ladn\" } } ], \"locationFilterList\" : [ null, null ], \"immediateFlag\" : true }, { \"subscribedDataFilterList\" : [ null, null ], \"areaList\" : [ { \"presenceInfo\" : { \"ecgiList\" : [ { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"globalRanNodeIdList\" : [ { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" } ], \"praId\" : \"praId\", \"trackingAreaList\" : [ { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"ncgiList\" : [ { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" } ] }, \"ladnInfo\" : { \"ladn\" : \"ladn\" } }, { \"presenceInfo\" : { \"ecgiList\" : [ { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"globalRanNodeIdList\" : [ { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" } ], \"praId\" : \"praId\", \"trackingAreaList\" : [ { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } ], \"ncgiList\" : [ { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" } ] }, \"ladnInfo\" : { \"ladn\" : \"ladn\" } } ], \"locationFilterList\" : [ null, null ], \"immediateFlag\" : true } ], \"subsChangeNotifyUri\" : \"subsChangeNotifyUri\", \"pei\" : \"pei\", \"anyUE\" : true, \"groupId\" : \"groupId\", \"options\" : { \"expiry\" : \"2000-01-23T04:56:07.000+00:00\", \"maxReports\" : 7 }, \"supi\" : \"supi\", \"notifyCorrelationId\" : \"notifyCorrelationId\", \"subsChangeNotifyCorrelationId\" : \"subsChangeNotifyCorrelationId\", \"gpsi\" : \"gpsi\" } ], \"drxParameter\" : \"drxParameter\", \"5gMmCapability\" : \"5gMmCapability\", \"subUeAmbr\" : { \"uplink\" : \"uplink\", \"downlink\" : \"downlink\" }, \"forbiddenAreaList\" : [ { \"areaCodes\" : \"areaCodes\", \"tacs\" : [ null, null ] }, { \"areaCodes\" : \"areaCodes\", \"tacs\" : [ null, null ] } ], \"pei\" : \"pei\", \"pcfId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"ausfGroupId\" : \"ausfGroupId\", \"smsfId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"amPolicyReqTriggerList\" : [ null, null ], \"mmContextList\" : [ { \"nssaiMappingList\" : [ { \"hSnssai\" : { \"sd\" : \"sd\", \"sst\" : 51 }, \"mappedSnssai\" : { \"sd\" : \"sd\", \"sst\" : 51 } }, { \"hSnssai\" : { \"sd\" : \"sd\", \"sst\" : 51 }, \"mappedSnssai\" : { \"sd\" : \"sd\", \"sst\" : 51 } } ], \"nsInstanceList\" : [ null, null ], \"nasSecurityMode\" : { }, \"nasDownlinkCount\" : 0, \"allowedNssai\" : [ { \"sd\" : \"sd\", \"sst\" : 51 }, { \"sd\" : \"sd\", \"sst\" : 51 } ], \"nasUplinkCount\" : 0, \"expectedUEbehavior\" : { \"validityTime\" : \"2000-01-23T04:56:07.000+00:00\", \"expMoveTrajectory\" : [ { \"eutraLocation\" : { \"geographicalInformation\" : \"geographicalInformation\", \"globalNgenbId\" : { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, \"ageOfLocationInformation\" : 13583, \"tai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueLocationTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"geodeticInformation\" : \"geodeticInformation\", \"ecgi\" : { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } }, \"nrLocation\" : { \"geographicalInformation\" : \"geographicalInformation\", \"globalGnbId\" : { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, \"ageOfLocationInformation\" : 24202, \"ncgi\" : { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, \"tai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueLocationTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"geodeticInformation\" : \"geodeticInformation\" }, \"n3gaLocation\" : { \"ueIpv4Addr\" : \"198.51.100.1\", \"n3gppTai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueIpv6Addr\" : \"2001:db8:85a3::8a2e:370:7334\", \"n3IwfId\" : \"n3IwfId\", \"portNumber\" : 0 } }, { \"eutraLocation\" : { \"geographicalInformation\" : \"geographicalInformation\", \"globalNgenbId\" : { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, \"ageOfLocationInformation\" : 13583, \"tai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueLocationTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"geodeticInformation\" : \"geodeticInformation\", \"ecgi\" : { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } }, \"nrLocation\" : { \"geographicalInformation\" : \"geographicalInformation\", \"globalGnbId\" : { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, \"ageOfLocationInformation\" : 24202, \"ncgi\" : { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, \"tai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueLocationTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"geodeticInformation\" : \"geodeticInformation\" }, \"n3gaLocation\" : { \"ueIpv4Addr\" : \"198.51.100.1\", \"n3gppTai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueIpv6Addr\" : \"2001:db8:85a3::8a2e:370:7334\", \"n3IwfId\" : \"n3IwfId\", \"portNumber\" : 0 } } ] }, \"s1UeNetworkCapability\" : \"s1UeNetworkCapability\", \"ueSecurityCapability\" : \"ueSecurityCapability\" }, { \"nssaiMappingList\" : [ { \"hSnssai\" : { \"sd\" : \"sd\", \"sst\" : 51 }, \"mappedSnssai\" : { \"sd\" : \"sd\", \"sst\" : 51 } }, { \"hSnssai\" : { \"sd\" : \"sd\", \"sst\" : 51 }, \"mappedSnssai\" : { \"sd\" : \"sd\", \"sst\" : 51 } } ], \"nsInstanceList\" : [ null, null ], \"nasSecurityMode\" : { }, \"nasDownlinkCount\" : 0, \"allowedNssai\" : [ { \"sd\" : \"sd\", \"sst\" : 51 }, { \"sd\" : \"sd\", \"sst\" : 51 } ], \"nasUplinkCount\" : 0, \"expectedUEbehavior\" : { \"validityTime\" : \"2000-01-23T04:56:07.000+00:00\", \"expMoveTrajectory\" : [ { \"eutraLocation\" : { \"geographicalInformation\" : \"geographicalInformation\", \"globalNgenbId\" : { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, \"ageOfLocationInformation\" : 13583, \"tai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueLocationTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"geodeticInformation\" : \"geodeticInformation\", \"ecgi\" : { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } }, \"nrLocation\" : { \"geographicalInformation\" : \"geographicalInformation\", \"globalGnbId\" : { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, \"ageOfLocationInformation\" : 24202, \"ncgi\" : { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, \"tai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueLocationTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"geodeticInformation\" : \"geodeticInformation\" }, \"n3gaLocation\" : { \"ueIpv4Addr\" : \"198.51.100.1\", \"n3gppTai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueIpv6Addr\" : \"2001:db8:85a3::8a2e:370:7334\", \"n3IwfId\" : \"n3IwfId\", \"portNumber\" : 0 } }, { \"eutraLocation\" : { \"geographicalInformation\" : \"geographicalInformation\", \"globalNgenbId\" : { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, \"ageOfLocationInformation\" : 13583, \"tai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueLocationTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"geodeticInformation\" : \"geodeticInformation\", \"ecgi\" : { \"eutraCellId\" : \"eutraCellId\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } } }, \"nrLocation\" : { \"geographicalInformation\" : \"geographicalInformation\", \"globalGnbId\" : { \"gNbId\" : { \"bitLength\" : 24, \"gNBValue\" : \"gNBValue\" }, \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"n3IwfId\" : \"n3IwfId\", \"ngeNbId\" : \"ngeNbId\" }, \"ageOfLocationInformation\" : 24202, \"ncgi\" : { \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" }, \"nrCellId\" : \"nrCellId\" }, \"tai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueLocationTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"geodeticInformation\" : \"geodeticInformation\" }, \"n3gaLocation\" : { \"ueIpv4Addr\" : \"198.51.100.1\", \"n3gppTai\" : { \"tac\" : \"tac\", \"plmnId\" : { \"mnc\" : \"mnc\", \"mcc\" : \"mcc\" } }, \"ueIpv6Addr\" : \"2001:db8:85a3::8a2e:370:7334\", \"n3IwfId\" : \"n3IwfId\", \"portNumber\" : 0 } } ] }, \"s1UeNetworkCapability\" : \"s1UeNetworkCapability\", \"ueSecurityCapability\" : \"ueSecurityCapability\" } ], \"supiUnauthInd\" : true, \"routingIndicator\" : \"routingIndicator\", \"groupList\" : [ null, null ], \"pcfAmPolicyUri\" : \"pcfAmPolicyUri\", \"traceData\" : { \"eventList\" : \"eventList\", \"collectionEntityIpv6Addr\" : \"2001:db8:85a3::8a2e:370:7334\", \"collectionEntityIpv4Addr\" : \"198.51.100.1\", \"traceRef\" : \"traceRef\", \"interfaceList\" : \"interfaceList\", \"neTypeList\" : \"neTypeList\" }, \"supi\" : \"supi\", \"subRfsp\" : 21, \"sessionContextList\" : [ { \"hsmfId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"allocatedEbiList\" : [ { \"epsBearerId\" : 2, \"arp\" : { \"priorityLevel\" : 10 } }, { \"epsBearerId\" : 2, \"arp\" : { \"priorityLevel\" : 10 } } ], \"vsmfId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"dnn\" : \"dnn\", \"nsInstance\" : \"nsInstance\", \"pduSessionId\" : 26, \"sNssai\" : { \"sd\" : \"sd\", \"sst\" : 51 }, \"smContextRef\" : \"smContextRef\" }, { \"hsmfId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"allocatedEbiList\" : [ { \"epsBearerId\" : 2, \"arp\" : { \"priorityLevel\" : 10 } }, { \"epsBearerId\" : 2, \"arp\" : { \"priorityLevel\" : 10 } } ], \"vsmfId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"dnn\" : \"dnn\", \"nsInstance\" : \"nsInstance\", \"pduSessionId\" : 26, \"sNssai\" : { \"sd\" : \"sd\", \"sst\" : 51 }, \"smContextRef\" : \"smContextRef\" } ], \"hpcfId\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"restrictedRatList\" : [ null, null ], \"serviceAreaRestriction\" : { \"maxNumOfTAs\" : 0, \"areas\" : [ { \"areaCodes\" : \"areaCodes\", \"tacs\" : [ null, null ] }, { \"areaCodes\" : \"areaCodes\", \"tacs\" : [ null, null ] } ] }, \"usedRfsp\" : 154, \"restrictedCoreNwTypeList\" : [ null, null ], \"udmGroupId\" : \"udmGroupId\" }, \"pcfReselectedInd\" : true, \"targetToSourceData\" : { \"ngapData\" : { \"contentId\" : \"contentId\" }, \"ngapMessageType\" : 0 }, \"supportedFeatures\" : \"supportedFeatures\", \"pduSessionList\" : [ { \"subjectToHo\" : true, \"pduSessionId\" : 29, \"n2InfoContent\" : { \"ngapData\" : { \"contentId\" : \"contentId\" }, \"ngapMessageType\" : 0 }, \"sNssai\" : { \"sd\" : \"sd\", \"sst\" : 51 } }, { \"subjectToHo\" : true, \"pduSessionId\" : 29, \"n2InfoContent\" : { \"ngapData\" : { \"contentId\" : \"contentId\" }, \"ngapMessageType\" : 0 }, \"sNssai\" : { \"sd\" : \"sd\", \"sst\" : 51 } } ], \"ngapCause\" : { \"value\" : 0, \"group\" : 0 }, \"failedSessionList\" : [ { \"subjectToHo\" : true, \"pduSessionId\" : 29, \"n2InfoContent\" : { \"ngapData\" : { \"contentId\" : \"contentId\" }, \"ngapMessageType\" : 0 }, \"sNssai\" : { \"sd\" : \"sd\", \"sst\" : 51 } }, { \"subjectToHo\" : true, \"pduSessionId\" : 29, \"n2InfoContent\" : { \"ngapData\" : { \"contentId\" : \"contentId\" }, \"ngapMessageType\" : 0 }, \"sNssai\" : { \"sd\" : \"sd\", \"sst\" : 51 } } ] }";

		String json22 = "{\"key1\":{\"key2\":{\"key3\":{\"key4\":{\"key5\":{\"key6\":{\"key7\":{\"key8\":{\"key9\":{\"key10\":{}}}}}}}}}},\"keya\":{\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}}";

		String json23 = "{\"keya\":{\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}}";
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

		list.add(json21);

		list.add(json22);

		list.add(json23);

		return list;
	}

	public static void main(String[] args) {

		NesasJsonTestingTool tools = new NesasJsonTestingTool(false, 4);
		String json = tools.sampleJsonTesting().get(22);
		tools.jsonDuplicateList(json).forEach(str -> log.info(str));

	}
}
