package ua.kp13.mishchenko.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import ua.kp13.mishchenko.AgregateEntry;
import ua.kp13.mishchenko.Interpreter;
import ua.kp13.mishchenko.Lexer;
import ua.kp13.mishchenko.Parser;
import ua.kp13.mishchenko.TokenType;
import ua.kp13.mishchenko.TupleEntry;
import ua.kp13.mishchenko.VariableEntry;
import ua.kp13.mishchenko.ast.Node;
import ua.kp13.mishchenko.ast.ProgramNode;
import ua.kp13.mishchenko.exceptions.InterpreterException;

public class InterpreterIntegrationTest {

	private Interpreter runCode(String code, boolean areLogsEnabled) throws Exception {
		Lexer lexer = new Lexer(code);

		Parser parser = new Parser(lexer);

		Node ast = null;

		ast = parser.parse();

		if (areLogsEnabled) {
			System.out.println("###################");
			System.out.println("Code: ");
			System.out.println(code);
			System.out.println("###################");
			System.out.println("Abstract Syntax Tree:");

			ast.printAST("");
			System.out.println("statements amount: " + ((ProgramNode) ast).getStatements().size());

			System.out.println("###################");

			System.out.println("ENDED PARSING");
		}

		Interpreter interpr = new Interpreter(ast);

		try {
			interpr.run();
		} catch (InterpreterException e) {
			e.printStackTrace();
		}
		if (areLogsEnabled) {
			interpr.printVariablesMap(interpr.getVariableMap());
			interpr.printFunctionsMap();
		}
		return interpr;
	}

	@Test
	public void testTupleOperationUni() throws Exception {
		String code = "int[] a = [1,2,3,4,5]; int[] b = [5,6,7,8,9]; int[] c; c = b; c = uni(a,b);";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(1);
		expectedA.add(2);
		expectedA.add(3);
		expectedA.add(4);
		expectedA.add(5);

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		List<Object> expectedС = new ArrayList<Object>();
		expectedС.add(1);
		expectedС.add(2);
		expectedС.add(3);
		expectedС.add(4);
		expectedС.add(5);
		expectedС.add(5);
		expectedС.add(6);
		expectedС.add(7);
		expectedС.add(8);
		expectedС.add(9);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		expectedMap.put("c",
				new VariableEntry(TokenType.TYPE_TUPLE, "c", new TupleEntry(TokenType.TYPE_INT, null, expectedС)));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));

	}

	@Test
	public void testTupleOperationSec() throws Exception {
		String code = "int[] a = [1,2,3,4,5,5]; int[] b = [5,5,5,6,7,8,9]; int[] c; c = sec(a,b);";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(1);
		expectedA.add(2);
		expectedA.add(3);
		expectedA.add(4);
		expectedA.add(5);
		expectedA.add(5);

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		List<Object> expectedC = new ArrayList<Object>();
		expectedC.add(5);
		expectedC.add(5);
		expectedC.add(5);
		expectedC.add(5);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		expectedMap.put("c",
				new VariableEntry(TokenType.TYPE_TUPLE, "c", new TupleEntry(TokenType.TYPE_INT, null, expectedC)));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testTupleOperationDif() throws Exception {

		String code = "int[] a = [1,2,3,4,5,5,5]; int[] b = [5,6,7,8,9]; int[] c; c = b; c = dif(a,b);";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(1);
		expectedA.add(2);
		expectedA.add(3);
		expectedA.add(4);
		expectedA.add(5);
		expectedA.add(5);
		expectedA.add(5);

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		List<Object> expectedС = new ArrayList<Object>();
		expectedС.add(1);
		expectedС.add(2);
		expectedС.add(3);
		expectedС.add(4);
		expectedС.add(5);
		expectedС.add(5);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		expectedMap.put("c",
				new VariableEntry(TokenType.TYPE_TUPLE, "c", new TupleEntry(TokenType.TYPE_INT, null, expectedС)));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));

	}

	@Test
	public void testTupleOperationSDif() throws Exception {
		String code = "int[] a = [1,2,3,4,5]; int[] b = [5,6,7,8,9]; int[] c; c = b; c = sdif(a,b);";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(1);
		expectedA.add(2);
		expectedA.add(3);
		expectedA.add(4);
		expectedA.add(5);

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		List<Object> expectedC = new ArrayList<Object>();
		expectedC.add(1);
		expectedC.add(2);
		expectedC.add(3);
		expectedC.add(4);
		expectedC.add(6);
		expectedC.add(7);
		expectedC.add(8);
		expectedC.add(9);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		expectedMap.put("c",
				new VariableEntry(TokenType.TYPE_TUPLE, "c", new TupleEntry(TokenType.TYPE_INT, null, expectedC)));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testTupleOperationXSec() throws Exception {
		String code = "int[] a = [1,2,3,4,5,5]; int[] b = [5,5,5,6,7,8,9]; int[] c; c = xsec(a,b);";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(1);
		expectedA.add(2);
		expectedA.add(3);
		expectedA.add(4);
		expectedA.add(5);
		expectedA.add(5);

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		List<Object> expectedC = new ArrayList<Object>();
		expectedC.add(5);
		expectedC.add(5);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		expectedMap.put("c",
				new VariableEntry(TokenType.TYPE_TUPLE, "c", new TupleEntry(TokenType.TYPE_INT, null, expectedC)));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testTupleOperationOrd() throws Exception {
		String code = "String[] a = [\"b\",\"x\",\"x\",\"c\",\"y\",\"a\"]; int[] b = [5,5,5,6,7,8,9]; String[] c; c = ord(a);";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add("a");
		expectedA.add("b");
		expectedA.add("c");
		expectedA.add("x");
		expectedA.add("x");
		expectedA.add("y");

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		List<Object> expectedC = new ArrayList<Object>();
		expectedC.add("a");
		expectedC.add("b");
		expectedC.add("c");
		expectedC.add("x");
		expectedC.add("x");
		expectedC.add("y");

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_STRING, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		expectedMap.put("c",
				new VariableEntry(TokenType.TYPE_TUPLE, "c", new TupleEntry(TokenType.TYPE_STRING, null, expectedC)));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testTupleOperationASort() throws Exception {
		String code = "int[] a = [9,8,7,6,5,4,3,2,10]; int[] b = [5,5,5,6,7,8,9]; int[] c; c = asort(a);";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(9);
		expectedA.add(8);
		expectedA.add(7);
		expectedA.add(6);
		expectedA.add(5);
		expectedA.add(4);
		expectedA.add(3);
		expectedA.add(2);
		expectedA.add(10);

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		List<Object> expectedC = new ArrayList<Object>();
		expectedC.add(2);
		expectedC.add(3);
		expectedC.add(4);
		expectedC.add(5);
		expectedC.add(6);
		expectedC.add(7);
		expectedC.add(8);
		expectedC.add(9);
		expectedC.add(10);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		expectedMap.put("c",
				new VariableEntry(TokenType.TYPE_TUPLE, "c", new TupleEntry(TokenType.TYPE_INT, null, expectedC)));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testTupleOperationDSort() throws Exception {
		String code = "int[] a = [9,8,7,6,5,4,3,2,10]; int[] b = [5,5,5,6,7,8,9]; int[] c; c = dsort(a);";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(9);
		expectedA.add(8);
		expectedA.add(7);
		expectedA.add(6);
		expectedA.add(5);
		expectedA.add(4);
		expectedA.add(3);
		expectedA.add(2);
		expectedA.add(10);

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		List<Object> expectedC = new ArrayList<Object>();
		expectedC.add(10);
		expectedC.add(9);
		expectedC.add(8);
		expectedC.add(7);
		expectedC.add(6);
		expectedC.add(5);
		expectedC.add(4);
		expectedC.add(3);
		expectedC.add(2);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		expectedMap.put("c",
				new VariableEntry(TokenType.TYPE_TUPLE, "c", new TupleEntry(TokenType.TYPE_INT, null, expectedC)));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testTupleOperationSingl() throws Exception {
		String code = "String[] a = [\"x\",\"x\",\"x\",\"y\",\"y\",\"z\"]; int[] b = [5,5,5,6,7,8,9]; String[] c; c = singl(a);";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add("x");
		expectedA.add("x");
		expectedA.add("x");
		expectedA.add("y");
		expectedA.add("y");
		expectedA.add("z");

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		List<Object> expectedC = new ArrayList<Object>();
		expectedC.add("x");
		expectedC.add("y");
		expectedC.add("z");

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_STRING, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		expectedMap.put("c",
				new VariableEntry(TokenType.TYPE_TUPLE, "c", new TupleEntry(TokenType.TYPE_STRING, null, expectedC)));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testTupleOperationExtr() throws Exception {
		String code = "int[] a = [1,2,3,4,5,5]; int[] b = [5,5,5,6,7,8,9]; int c; int i = 3; c = extr(a, i);";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(1);
		expectedA.add(2);
		expectedA.add(3);
		expectedA.add(5);
		expectedA.add(5);

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		expectedMap.put("i", new VariableEntry(TokenType.TYPE_INT, "i", 3));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_INT, "c", 4));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testTupleOperationIns() throws Exception {
		String code = "int[] a = [1,2,3,4,5,5]; int b = 114; int i = 2; int c = ins(a, i, b);";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(1);
		expectedA.add(2);
		expectedA.add(114);
		expectedA.add(3);
		expectedA.add(4);
		expectedA.add(5);
		expectedA.add(5);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("i", new VariableEntry(TokenType.TYPE_INT, "i", 2));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_INT, "c", 114));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_INT, "b", 114));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testTupleOperationGet() throws Exception {
		String code = "int[] a = [1,2,3,4,5,5]; int[] b = [5,5,5,6,7,8,9]; int c; int i = 3; c = get(a, i);";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(1);
		expectedA.add(2);
		expectedA.add(3);
		expectedA.add(4);
		expectedA.add(5);
		expectedA.add(5);

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		expectedMap.put("i", new VariableEntry(TokenType.TYPE_INT, "i", 3));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_INT, "c", 4));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testTuplesInitWithVariables() throws Exception {
		String code = "int b = 5; int[] a = [1,2,3,3+1,b]; int[] c; c = a ;";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_INT, "b", 5));

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(1);
		expectedA.add(2);
		expectedA.add(3);
		expectedA.add(4);
		expectedA.add(5);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("c",
				new VariableEntry(TokenType.TYPE_TUPLE, "c", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testTime() throws Exception {
		String code = "time a = 12:47:00:00; time b = 13:13:13:13; time c = a + b; time d = b - a; time e = 24:00:00:00 - 23:00:00:10; boolean x = 24:00:00:00 == 23:00:00:00; ";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_TIME, "a", "12:47:00:00"));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_TIME, "b", "13:13:13:13"));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_TIME, "c", "02:00:13:13"));
		expectedMap.put("d", new VariableEntry(TokenType.TYPE_TIME, "d", "00:26:13:13"));
		expectedMap.put("e", new VariableEntry(TokenType.TYPE_TIME, "e", "00:59:59:90"));
		expectedMap.put("x", new VariableEntry(TokenType.TYPE_BOOLEAN, "x", false));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testFunction() throws Exception {
		String code = "int a=5; int b=6; int function sum(int a, int b){return a+b;}; int c = sum(a,b);";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", 5));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_INT, "b", 6));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_INT, "c", 11));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testRecursiveFunction() throws Exception {
		String code = "int function factorial(int n){ if(n==0){return 1; }  int b = n - 1; int mul = factorial(b); int res = n * mul;  return res;} int r = factorial(5);";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("r", new VariableEntry(TokenType.TYPE_INT, "r", 120));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testInnerFor() throws Exception {
		String code = "int c = 0; int a = 0; for( int i = 0; i<10; i = i+1;) {a = a + 2; if(a==14){a = a+10;} for(int j = 0; j<10; j = j +1;){c = c + 1;}}";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", 30));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_INT, "c", 100));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testFor() throws Exception {
		String code = "int a = 0; for( int i = 0; i<10; i = i+1;) {a = a + 1;}";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", 10));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testInnerWhile() throws Exception {
		String code = "int c = 0; int i = 0; int a = 0; while(i<10){a = a + 2; i = i + 1; if( a == 14 ){ a = a+10; } int j = 0; while( j < 10 ){c = c + 1; j = j + 1; }} ";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", 30));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_INT, "c", 100));
		expectedMap.put("i", new VariableEntry(TokenType.TYPE_INT, "i", 10));
		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testWhile() throws Exception {
		String code = "int i = 0; int a = 0; while(i<10) { int b = 1; a = a + 2; i = i + 1;} ";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		expectedMap.put("i", new VariableEntry(TokenType.TYPE_INT, "i", 10));
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", 20));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testMathOperations() throws Exception {
		String code = "float a = 7+5+8*4/12*(12+3)/0.05; float b = a^2; ";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		expectedMap.put("a", new VariableEntry(TokenType.TYPE_FLOAT, "a", 812.0));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_FLOAT, "b", 659344.0));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testIfClauseEndsOnElif() throws Exception {
		String code = "int a = 5; int b = 6; if(a>b){a = a + b;} elif(a<b){a = a-b;} else {int a = 3;} ;";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", -1));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_INT, "b", 6));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testIfClauseDifferentCombinations() throws Exception {
		String code = "int a = 5; int b = 6; if(a>b){a = a + b; } else {a = 1;} if( b == 6) {b = 1; } ;";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", 1));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_INT, "b", 1));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testIfClause() throws Exception {
		String code = "int a = 5; int b = 6; if( a<b ){a = a + b;} ";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", 11));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_INT, "b", 6));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testIfClauseEndsOnIf() throws Exception {
		String code = "int a = 5; int b = 6; if(a<b){a = a + b;} elif(a>b){a = a-b;} else {int a = 3;} ;";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", 11));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_INT, "b", 6));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testIfClauseEndsOnElse() throws Exception {
		String code = "int a = 5; int b = 5; if(a<b){a = a + b;} elif(a>b){a = a-b;} else {a = 3;} ;";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", 3));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_INT, "b", 5));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testBooleanFuncs() throws Exception {
		String code = "boolean a = true ; boolean b = !false ; boolean c = ((a == b) && false) || true ;";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_BOOLEAN, "a", true));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_BOOLEAN, "b", true));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_BOOLEAN, "c", true));
		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testConcatanation() throws Exception {
		String code = "string a = \"test\"; string b = \" passed\"; string c = a + b + \" finally\"; ";
		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_STRING, "a", "test"));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_STRING, "b", " passed"));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_STRING, "c", "test passed finally"));
		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testSimpleVariablesInitializationAndAssignment() throws Exception {
		String code = "int a; float b; string c; boolean d; time e; "
				+ "a = 1; b = 1.0; c = \"test1\"; d = false; e = 11:11:11:11; ";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", 1));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_FLOAT, "b", 1.0));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_STRING, "c", "test1"));
		expectedMap.put("d", new VariableEntry(TokenType.TYPE_BOOLEAN, "d", false));
		expectedMap.put("e", new VariableEntry(TokenType.TYPE_TIME, "e", "11:11:11:11"));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));
	}

	@Test
	public void testSimpleVariablesInitialization() throws Exception {
		String code = "int a; float b; string c; boolean d; time e;";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", null));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_FLOAT, "b", null));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_STRING, "c", null));
		expectedMap.put("d", new VariableEntry(TokenType.TYPE_BOOLEAN, "d", null));
		expectedMap.put("e", new VariableEntry(TokenType.TYPE_TIME, "e", null));

		Interpreter interpr = runCode(code, false);
		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));

	}

	@Test
	public void testSimpleVariablesInitializationAssignment() throws Exception {
		String code = "int a = 5; float b = 5.5; string c = \"test\"; boolean d = true; time e = 11:11:11:11 ; ";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();
		expectedMap.put("a", new VariableEntry(TokenType.TYPE_INT, "a", 5));
		expectedMap.put("b", new VariableEntry(TokenType.TYPE_FLOAT, "b", 5.5));
		expectedMap.put("c", new VariableEntry(TokenType.TYPE_STRING, "c", "test"));
		expectedMap.put("d", new VariableEntry(TokenType.TYPE_BOOLEAN, "d", true));
		expectedMap.put("e", new VariableEntry(TokenType.TYPE_TIME, "e", "11:11:11:11"));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));

	}

	@Test
	public void testTupleAndAgregateVariablesInitialization() throws Exception {
		String code = "int[] a = [9,8,7,6,5,4,3,2,10]; int[] b = [5,5,5,6,7,8,9];  Agregate c = [a,b];";

		Map<String, VariableEntry> expectedMap = new HashMap<String, VariableEntry>();

		List<Object> expectedA = new ArrayList<Object>();
		expectedA.add(9);
		expectedA.add(8);
		expectedA.add(7);
		expectedA.add(6);
		expectedA.add(5);
		expectedA.add(4);
		expectedA.add(3);
		expectedA.add(2);
		expectedA.add(10);

		List<Object> expectedB = new ArrayList<Object>();
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(5);
		expectedB.add(6);
		expectedB.add(7);
		expectedB.add(8);
		expectedB.add(9);

		expectedMap.put("a",
				new VariableEntry(TokenType.TYPE_TUPLE, "a", new TupleEntry(TokenType.TYPE_INT, "a", expectedA)));

		expectedMap.put("b",
				new VariableEntry(TokenType.TYPE_TUPLE, "b", new TupleEntry(TokenType.TYPE_INT, "b", expectedB)));

		List<TupleEntry> agregateValue = new ArrayList<TupleEntry>();
		agregateValue.add(new TupleEntry(TokenType.TYPE_INT, "a", expectedA));
		agregateValue.add(new TupleEntry(TokenType.TYPE_INT, "b", expectedB));

		expectedMap.put("c", new VariableEntry(TokenType.TYPE_AGREGATE, "c", new AgregateEntry("c", agregateValue)));

		Interpreter interpr = runCode(code, false);

		Assert.assertTrue(interpr.getVariableMap().equals(expectedMap));

	}

}
