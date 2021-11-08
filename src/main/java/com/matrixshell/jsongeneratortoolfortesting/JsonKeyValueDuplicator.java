package com.matrixshell.jsongeneratortoolfortesting;

import java.util.List;

/**
 * 
 * @author matrix
 *
 */
public interface JsonKeyValueDuplicator {
	/**
	 * Call this method to generated list of JSON with duplicate in String format
	 * 
	 * @param jsonStr A valid JSON in String format
	 * @return list of JSON with duplicate key values in String format.
	 */
	public List<String> jsonDuplicateList(String jsonStr);
}
