package com.matrixshell.jsontestingtool;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
	/**
	 * Rigorous Test :-)
	 */
	@Test
	public void shouldAnswerWithTrue() {
		assertTrue(true);
	}

	@Test
	public void varifyJsonDuplicateTestWithCompleteArrDuplicationFalseParameter() {
		NesasJsonTestingTool jsonDuplicate = new NesasJsonTestingTool(false);
		String json = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		List<String> jsonTestDuplicates = jsonDuplicate.jsonDuplicateList(json);
		String dup0 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}],\"akey\":\"aval\"}";
		String dup1 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}],\"ckey\":\"cval\"}";
		String dup2 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}],\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"}}";
		String dup3 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\",\"akey\":\"aval\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String dup4 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\",\"bkey\":\"bval\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String dup5 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\",\"ckey\":\"cval\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String dup6 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String dup7 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}],\"dkey\":\"dkey\"}";
		String dup8 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}],\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		List<String> jsonCorrectDuplicates = new ArrayList<String>();
		jsonCorrectDuplicates.add(dup0);
		jsonCorrectDuplicates.add(dup1);
		jsonCorrectDuplicates.add(dup2);
		jsonCorrectDuplicates.add(dup3);
		jsonCorrectDuplicates.add(dup4);
		jsonCorrectDuplicates.add(dup5);
		jsonCorrectDuplicates.add(dup6);
		jsonCorrectDuplicates.add(dup7);
		jsonCorrectDuplicates.add(dup8);
		Assert.assertEquals(jsonTestDuplicates, jsonCorrectDuplicates);
	}

	@Test
	public void varifyJsonDuplicateTestWithCompleteArrDuplicationTrueParameter() {
		NesasJsonTestingTool jsonDuplicate = new NesasJsonTestingTool(true);
		String json = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		List<String> jsonTestDuplicates = jsonDuplicate.jsonDuplicateList(json);
		String dup0 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}],\"akey\":\"aval\"}";
		String dup1 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}],\"ckey\":\"cval\"}";
		String dup2 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}],\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"}}";
		String dup3 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\",\"akey\":\"aval\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String dup4 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\",\"bkey\":\"bval\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String dup5 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\",\"ckey\":\"cval\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String dup6 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String dup7 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}],\"dkey\":\"dkey\"}";
		String dup8 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}],\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String dup9 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\",\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\"}]}";
		String dup10 = "{\"akey\":\"aval\",\"ckey\":\"cval\",\"bkey\":{\"akey\":\"aval\",\"bkey\":\"bval\",\"ckey\":\"cval\",\"dkey\":\"dkey\"},\"dkey\":\"dkey\",\"ekey\":[\"\",{},[\"\",[],{\"newekarr2\":\"newevarr2\"}],{\"ekarr2\":\"evarr2\",\"ekarr2\":\"evarr2\"}]}";
		List<String> jsonCorrectDuplicates = new ArrayList<String>();
		jsonCorrectDuplicates.add(dup0);
		jsonCorrectDuplicates.add(dup1);
		jsonCorrectDuplicates.add(dup2);
		jsonCorrectDuplicates.add(dup3);
		jsonCorrectDuplicates.add(dup4);
		jsonCorrectDuplicates.add(dup5);
		jsonCorrectDuplicates.add(dup6);
		jsonCorrectDuplicates.add(dup7);
		jsonCorrectDuplicates.add(dup8);
		jsonCorrectDuplicates.add(dup9);
		jsonCorrectDuplicates.add(dup10);
		Assert.assertEquals(jsonTestDuplicates, jsonCorrectDuplicates);
	}
	
	@Test
	public void varifyJsonDuplicateTestWithCompleteArrDuplicationTrueAndLevelsParameter(){
		
	}
	
	@Test
	public void varifyJsonDuplicateTestWithCompleteArrDuplicationFalseAndLevelsParameter(){
		
	}

}
