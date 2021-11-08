/**
 * This library is created to generate JSON for testing purposes.
 */
package com.matrixshell.jsongeneratortoolfortesting;

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
 * A class for creating list of JSON with duplicate Key-value pair according to
 * the requirements for testing purpose.
 * 
 * @author Akib Sayyed
 * @author Muheeb Shekh
 *
 */

@Slf4j
public class JsonKeyValueDuplicatorImpl implements JsonKeyValueDuplicator {

	/**
	 * This will hold the original JSON Object
	 */
	private JsonObject jsonObjectOrg;

	/*
	 * This list object will contain list of JSON with duplicates of Key and Value
	 */
	private List<String> jsonDuplicateList = new ArrayList<String>();

	/**
	 * If duplication is not required for all the elements of JSON array this flag
	 * can be set to false else true
	 */
	private boolean completeArrDuplication;

	/**
	 * If JSON is big in size with respect to where value of a key has another pair
	 * of key value and that value further have another pair, in this case we can
	 * put a certain level up to which duplicates should be generated.
	 */
	private Optional<Integer> levels;

	/**
	 * Constructs a JsonKeyValueDuplicator object with default configuration. The
	 * default configuration has the following settings:
	 * <ul>
	 * <li>completeArrDuplication : <b>false</b>. Only one element in array of JSON
	 * Object will be accessed</li>
	 * <li>levels : <b>empty</b>. All the nested JSON object key and values will be
	 * accessed</li>
	 * </ul>
	 */
	public JsonKeyValueDuplicatorImpl() {
		completeArrDuplication = false;
		levels = Optional.empty();
	}

	/**
	 * Constructs a JsonKeyValueDuplicator object with one custom configuration. The
	 * configuration has the following settings:
	 * 
	 * @param completeArrDuplication : Set true to create duplicates of all elements
	 *                               in an array
	 */
	public JsonKeyValueDuplicatorImpl(boolean completeArrDuplication) {
		this.completeArrDuplication = completeArrDuplication;
		levels = Optional.empty();
	}

	/**
	 * Constructs a JsonKeyValueDuplicator object with one custom configuration. The
	 * configuration has the following settings:
	 * 
	 * @param levels : This parameter to create duplicate keys values till a limit
	 *               for nested JSON Key Value.
	 */
	public JsonKeyValueDuplicatorImpl(int levels) {
		this.levels = Optional.of(levels - 1);
	}

	/**
	 * Constructs a JsonKeyValueDuplicator object with two custom configuration. The
	 * configuration has the following settings:
	 * 
	 * @param completeArrDuplication : Set true to create duplicates of all elements
	 *                               in an array
	 * @param levels                 : This parameter if for nested array
	 *                               duplication.
	 */
	public JsonKeyValueDuplicatorImpl(boolean completeArrDuplication, int levels) {
		this.completeArrDuplication = completeArrDuplication;
		this.levels = Optional.of(levels - 1);
	}

	/**
	 * Json method will call
	 * {@link #createDuplicateKeyValueJson(JsonObject, String, JsonElement, Optional, List, int, int)
	 * createDuplicateKeyValueJson} method and return the <b>String</b> with
	 * duplicate of key value which is passed in the parameter. The <b>String</b>
	 * will be added in {@link #jsonDuplicateList} List type of variable
	 */
	private void createDuplicate(JsonObject jsonObject, String jsonKey, JsonElement jsonValue,
			Optional<String> parentKey, List<String> parentKeyList, List<Entry<String, JsonElement>> entries, int start,
			int end) {
		String json = createDuplicateKeyValueJson(jsonObject, jsonKey, jsonValue, parentKey, parentKeyList, start, end);
		this.jsonDuplicateList.add(json);
	}

	/**
	 * In this method JSON Key and JSON Value is passed along with JSON object, then
	 * this key value duplicate is created and added in JsonObject and later
	 * returned in String format because {@link JsonObject} cannot contain duplicate
	 * key value
	 */
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

	/**
	 * This method will iterate over {@linkplain JsonObject}
	 * {@linkplain ListIterator} recursively, if value of any key is a JSON object,
	 * then this method will be called again with a new iterator, if the value of
	 * any key is JSON Array then
	 * {@link #recursiveJsonArrObjItr(JsonArray, ListIterator, Optional, List, List, int, int)
	 * recursiveJsonArrObjItr} will be called. For every key value pair that is
	 * passed in this method their duplicates are created and added in
	 * {@linkplain #jsonDuplicateList}
	 */
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
			createDuplicate(jsonObject, jsonKey, jsonValue, parentKey, parentKeyList, entries, start, end);
		} else if (jsonValue.isJsonObject()) {
			createDuplicate(jsonObject, jsonKey, jsonValue, parentKey, parentKeyList, entries, start, end);
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
			createDuplicate(jsonObject, jsonKey, jsonValue, parentKey, parentKeyList, entries, start, end);
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

	/**
	 * This method will iterate over {@linkplain JsonArray}
	 * {@linkplain ListIterator} recursively, if value of any key is a JSON object,
	 * then this method will be called again with a new iterator, if the value of
	 * any key is JSON Array then
	 * {@link #recursiveJsonArrObjItr(JsonArray, ListIterator, Optional, List, List, int, int)
	 * recursiveJsonArrObjItr} will be called. And for every key value pair that is
	 * passed in this method their duplicates are created and added in
	 * {@link #jsonDuplicateList} list.
	 */
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

	@Override
	public List<String> jsonDuplicateList(String jsonStr) {
		jsonObjectOrg = new Gson().fromJson(jsonStr, JsonObject.class);
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

}
