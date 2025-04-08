package ua.kp13.mishchenko;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ua.kp13.mishchenko.ast.AgregateNode;
import ua.kp13.mishchenko.ast.AssignmentNode;
import ua.kp13.mishchenko.ast.BinaryOperationNode;
import ua.kp13.mishchenko.ast.BooleanNode;
import ua.kp13.mishchenko.ast.ConditionNode;
import ua.kp13.mishchenko.ast.ForLoopNode;
import ua.kp13.mishchenko.ast.FunctionCallNode;
import ua.kp13.mishchenko.ast.FunctionNode;
import ua.kp13.mishchenko.ast.InitializationNode;
import ua.kp13.mishchenko.ast.LogicalOperationNode;
import ua.kp13.mishchenko.ast.Node;
import ua.kp13.mishchenko.ast.NumberNode;
import ua.kp13.mishchenko.ast.ProgramNode;
import ua.kp13.mishchenko.ast.ReturnNode;
import ua.kp13.mishchenko.ast.StringNode;
import ua.kp13.mishchenko.ast.TimeNode;
import ua.kp13.mishchenko.ast.TupleNode;
import ua.kp13.mishchenko.ast.VariableNode;
import ua.kp13.mishchenko.ast.WhileLoopNode;



public class Interpreter {
	
	private Node program;
	private Map<String, VariableEntry> variableMap;
	private Map<String, FunctionNode> functionsMap;
	private boolean isReturnCalled = false;
	private VariableEntry lastReturnRes = null;
	private String timeRegex = "^([01]?[0-9]|2[0-3]):([0-5]?[0-9]):([0-5]?[0-9]):([0-9]{2})$";
	private List<String> defaultFunctionNames = new ArrayList<String>();

	public Interpreter(Node program) {
		this.program = program;
		variableMap = new HashMap<String, VariableEntry>();
		functionsMap = new HashMap<String, FunctionNode>();
	}

	public void run() {
		List<Node> statements = ((ProgramNode) program).getStatements();

		addDefaultASAfunctions();

		runStatements(statements, false, variableMap);

		System.out.println("###################");

		printVariablesMap(variableMap);

		System.out.println("###################");

		printFunctionsMap();

		System.out.println("END");
	}

	private boolean checkDefaultFunction(String name) {
		return defaultFunctionNames.contains(name);
	}

	private void addDefaultASAfunctions() {
		defaultFunctionNames.add("uni");
		defaultFunctionNames.add("sec");
		defaultFunctionNames.add("dif");
		defaultFunctionNames.add("sdif");
		defaultFunctionNames.add("xsec");
		defaultFunctionNames.add("ord");
		defaultFunctionNames.add("asort");
		defaultFunctionNames.add("dsort");
		defaultFunctionNames.add("singl");
		defaultFunctionNames.add("extr");
		defaultFunctionNames.add("ins");
		defaultFunctionNames.add("get");

		for (String name : defaultFunctionNames) {
			functionsMap.put(name,
					new FunctionNode(new VariableNode(new Token(TokenType.VARIABLE, name)), null, null, null));
		}

	}

	private VariableEntry runStatements(List<Node> statements, boolean isInner,
			Map<String, VariableEntry> variableMap) {
		List<String> clearList = new ArrayList<String>();

		for (int i = 0; i < statements.size(); i++) {
			Node statement = statements.get(i);
			// INITIALIZATION
			if (statement.getClass() == InitializationNode.class) {
				runInitializationOperation((InitializationNode) statement, variableMap);
				if (isInner) {
					clearList.add(((InitializationNode) statement).getVariableName());
				}
			}
			// ASSIGNMENT
			else if (statement.getClass() == AssignmentNode.class) {

				runAssignmentOperation((AssignmentNode) statement, variableMap);

			}
			// BINARY OPERATION
			else if (statement.getClass() == BinaryOperationNode.class) {
				System.out.println(runBinaryOperation((BinaryOperationNode) statement, variableMap));
				// TODO ????????

			}
			// CONDITIONS SINGLE IF OR IF-ELIF-ELSE BLOCK
			else if (statement.getClass() == ConditionNode.class) {
				List<ConditionNode> ifClauseConstruction = new ArrayList<ConditionNode>();
				while (statements.get(i).getClass() == ConditionNode.class) {
					if (((ConditionNode) statements.get(i)).getType() == TokenType.IF
							&& ifClauseConstruction.size() == 0) {
						ifClauseConstruction.add((ConditionNode) statements.get(i));
						i++;
					} else if (((ConditionNode) statements.get(i)).getType() == TokenType.ELIF) {
						ifClauseConstruction.add((ConditionNode) statements.get(i));
						i++;
					} else if (((ConditionNode) statements.get(i)).getType() == TokenType.ELSE) {
						ifClauseConstruction.add((ConditionNode) statements.get(i));
						i++;
					}
					if (i > statements.size() - 1) {
						i--;
						break;
					}
				}
				if (statements.get(i).getClass() != ConditionNode.class) {
					i--;
				}

				boolean passed = false;
				for (int j = 0; j < ifClauseConstruction.size(); j++) {
					if (passed) {
						break;
					}
					ConditionNode condition = ifClauseConstruction.get(j);
					passed = runConditionStatements(condition, variableMap);
				}

			}
			// WHILE LOOP
			else if (statement.getClass() == WhileLoopNode.class) {
				WhileLoopNode node = (WhileLoopNode) statement;
				Node expression = node.getRunCondition();
				List<Node> innerStatements = node.getStatementsList();

				while (checkConditionExpression(expression, variableMap)) {
					runStatements(innerStatements, true, variableMap);
				}

			}
			// FOR LOOP
			else if (statement.getClass() == ForLoopNode.class) {
				ForLoopNode node = (ForLoopNode) statement;
				Node initCounter = node.getCounter();
				Node expression = node.getRunCondition();
				Node step = node.getStep();
				List<Node> innerStatements = node.getStatementsList();

				if (initCounter.getClass() == InitializationNode.class) {
					runInitializationOperation((InitializationNode) initCounter, variableMap);
					clearList.add(((InitializationNode) initCounter).getVariableName());
				} else {
					// TODO ERROR
					System.out.println("1ST ARGUMENT OF 'FOR' LOOP CONSTRUCTION MUST BE INITIALIZATION");
				}

				while (checkConditionExpression(expression, variableMap)) {

					runStatements(innerStatements, true, variableMap);
					if (step.getClass() == AssignmentNode.class) {
						runAssignmentOperation((AssignmentNode) step, variableMap);
					} else {
						// TODO ERROR
						System.out.println("3RD ARGUMENT OF 'FOR' LOOP CONSTRUCTION MUST BE ASSIGNMENT");
					}
				}
			}
			// FUNCTION INITIALIZATION
			else if (statement.getClass() == FunctionNode.class) {
				FunctionNode node = (FunctionNode) statement;
				String name = node.getName().getToken().getValue();
				functionsMap.put(name, node);
			}

			// FUNCTION CALL
			else if (statement.getClass() == FunctionCallNode.class) {
				FunctionCallNode funcCall = (FunctionCallNode) statement;

				runFunctionCall(funcCall, variableMap);

			} else if (statement.getClass() == ReturnNode.class) {
				isReturnCalled = true;
				VariableEntry res = runReturn((ReturnNode) statement, variableMap);
				return res;

			}

			if (isReturnCalled) {
				isReturnCalled = false;
				break;
			}
		}

		// CLEARING MEMORY FROM LOCAL INNER VARIABLES
		for (String varName : clearList) {
			variableMap.remove(varName);
		}
		clearList.clear();
		return null;
	}

	private VariableEntry runReturn(ReturnNode node, Map<String, VariableEntry> variableMap) {
		Node expression = node.getExpression();
		TokenType type = TokenType.VOID;
		Object value = null;

		if (expression.getClass() == BooleanNode.class) {
			type = ((BooleanNode) expression).getToken().getType();
			value = ((BooleanNode) expression).getToken().getValue();
		} else if (expression.getClass() == StringNode.class) {
			type = ((StringNode) expression).getToken().getType();
			value = ((StringNode) expression).getToken().getValue();
		} else if (expression.getClass() == FunctionCallNode.class) {
			type = functionsMap.get(((FunctionCallNode) expression).getName().getToken().getValue()).getReturnType()
					.getType();
			value = runFunctionCall((FunctionCallNode) expression, variableMap);
		} else if (expression.getClass() == VariableNode.class) {
			type = variableMap.get(((VariableNode) expression).getToken().getValue()).getType();
			value = variableMap.get(((VariableNode) expression).getToken().getValue()).getValue();
		} else if (expression.getClass() == LogicalOperationNode.class) {
			type = TokenType.TYPE_BOOLEAN;
			value = runLogicalOperation((LogicalOperationNode) expression, variableMap);
		} else if (expression.getClass() == BinaryOperationNode.class) {
			Object res = runBinaryOperation((BinaryOperationNode) expression, variableMap);
			if (res.getClass() == Double.class) {
				if ((Double) res % 1 == 0) {
					type = TokenType.TYPE_INT;
					value = res;
				} else {
					type = TokenType.TYPE_FLOAT;
					value = res;
				}
			} else if (res.getClass() == String.class) {
				type = TokenType.TYPE_STRING;
				value = res;
			}
		} else if (expression.getClass() == NumberNode.class) {
			Double res = Double.parseDouble(((NumberNode) expression).getToken().getValue());
			if (res % 1 == 0) {
				type = TokenType.TYPE_INT;
				value = res;
			} else {
				type = TokenType.TYPE_FLOAT;
				value = res;
			}
		} else {
			type = TokenType.VOID;
			value = null;
		}

		// TODO CLEAR LOGS
		// System.out.println("FUNCTION RETURNED: " + value.toString());
		lastReturnRes = new VariableEntry(type, null, value);
		return lastReturnRes;
	}

	private VariableEntry runFunctionCall(FunctionCallNode funcCall, Map<String, VariableEntry> variableMap) {

		String funcName = funcCall.getName().getToken().getValue();
		if (checkDefaultFunction(funcName)) {

			List<VariableEntry> entries = new ArrayList<VariableEntry>();

			for (Node node : funcCall.getArgs()) {
				//if (checkNodeReturnType(node, TokenType.TYPE_TUPLE, variableMap)) {
					if (node.getClass() == VariableNode.class) {
						VariableEntry entry = variableMap.get(((VariableNode) node).getToken().getValue());
						entries.add(entry);
					} else {
						// TODO ERROR
						System.out.println("EXPECTED VARIABLE IN " + funcName + " call args");
					}
				//}
			}

			if (funcName.equals("uni") || funcName.equals("sec") || funcName.equals("dif") || funcName.equals("sdif")
					|| funcName.equals("xsec")) {

				if (entries.size() != 2) {
					// TODO ERROR
					System.out.println("ERROR. " + funcName + "ACCEPTS ONLY 2 ARGUMENTS");
				} else if (((TupleEntry) entries.get(0).getValue())
						.getType() != ((TupleEntry) entries.get(1).getValue()).getType()) {
					// TODO ERROR
					System.out.println("ERROR. " + funcName + "TUPLE TYPES MISMATCH");
				}

				TupleEntry tupleLeft = (TupleEntry) (entries.get(0).getValue());
				TupleEntry tupleRight = (TupleEntry) (entries.get(1).getValue());
				List<Object> resultList = new ArrayList<Object>();

				List<Object> tupleLeftValues = tupleLeft.getValues();
				List<Object> tupleRightValues = tupleRight.getValues();

				if (funcName.equals("uni")) {
					for (Object object : tupleLeftValues) {
						resultList.add(object);
					}

					for (Object object : tupleRightValues) {

						resultList.add(object);

					}
					// SEC OPERATION
				} else if (funcName.equals("sec")) {
					List<Object> bufferList = new ArrayList<Object>();
					for (Object object : tupleLeftValues) {
						bufferList.add(object);
					}

					for (Object object : tupleRightValues) {
						if (bufferList.contains(object)) {
							bufferList.remove(object);
							resultList.add(object);
						}
					}
					bufferList.clear();

					for (Object object : tupleRightValues) {
						bufferList.add(object);
					}
					for (Object object : tupleLeftValues) {
						if (bufferList.contains(object)) {
							bufferList.remove(object);
							resultList.add(object);
						}
					}
					// DIF OPERATION
				} else if (funcName.equals("dif")) {
					List<Object> bufferList = new ArrayList<Object>();

					for (Object object : tupleRightValues) {
						bufferList.add(object);
					}

					for (Object object : tupleLeftValues) {
						if (!bufferList.contains(object)) {
							resultList.add(object);
						} else {
							bufferList.remove(object);
						}
					}
					// SDIF OPERATION
				} else if (funcName.equals("sdif")) {

					List<Object> bufferList = new ArrayList<Object>();

					for (Object object : tupleRightValues) {
						bufferList.add(object);
					}

					for (Object object : tupleLeftValues) {
						if (!bufferList.contains(object)) {
							resultList.add(object);
						} else {
							bufferList.remove(object);
						}
					}
					for (Object object : bufferList) {
						resultList.add(object);
					}
					// XSEC OPERATION
				} else if (funcName.equals("xsec")) {
					List<Object> bufferList = new ArrayList<Object>();
					for (Object object : tupleLeftValues) {
						bufferList.add(object);
					}

					for (Object object : tupleRightValues) {
						if (bufferList.contains(object)) {
							bufferList.remove(object);
							resultList.add(object);
						}
					}
				}
				TupleEntry tupleResult = new TupleEntry(tupleLeft.getType(), null, resultList);
				lastReturnRes = new VariableEntry(TokenType.TYPE_TUPLE, null, tupleResult);
				return new VariableEntry(TokenType.TYPE_TUPLE, null, tupleResult);
			} else if (funcName.equals("ord") || funcName.equals("asort") || funcName.equals("dsort")
					|| funcName.equals("singl")) {

				if (entries.size() != 1) {
					// TODO ERROR
					System.out.println("ERROR. " + funcName + "ACCEPTS ONLY 1 ARGUMENT");
				}

				Object object = entries.get(0).getValue();

				if (object.getClass() != TupleEntry.class) {
					// TODO ERROR
					System.out.println("ERROR. INVALID Tuple variable.");
				}

				List<Object> values = ((TupleEntry) object).getValues();
				List<Object> resultValues = new ArrayList<Object>();

				if (funcName.equals("ord")) {

					Collections.sort(values, new Comparator<Object>() {
						public int compare(Object o1, Object o2) {
							return o1.toString().compareTo(o2.toString());
						}
					});

					for (Object value : values) {
						resultValues.add(value);
					}

				} else if (funcName.equals("asort")) {

					double[] resultArray = new double[values.size()];
					for (int i = 0; i < values.size(); i++) {
						resultArray[i] = Double.parseDouble((values.get(i)).toString());
					}

					boolean flag = true;

					while (flag) {
						flag = false;
						for (int i = 0; i < values.size() - 1; i++) {
							if (resultArray[i] > resultArray[i + 1]) {
								double buf = resultArray[i] + 0;
								resultArray[i] = resultArray[i + 1] + 0;
								resultArray[i + 1] = buf + 0;
								flag = true;
							}
						}
					}

					for (double value : resultArray) {
						resultValues.add(value);
					}

				} else if (funcName.equals("dsort")) {

					double[] resultArray = new double[values.size()];
					for (int i = 0; i < values.size(); i++) {
						resultArray[i] = Double.parseDouble((values.get(i)).toString());
					}

					boolean flag = true;

					while (flag) {
						flag = false;
						for (int i = 0; i < values.size() - 1; i++) {
							if (resultArray[i] < resultArray[i + 1]) {
								double buf = resultArray[i] + 0;
								resultArray[i] = resultArray[i + 1] + 0;
								resultArray[i + 1] = buf + 0;
								flag = true;
							}
						}
					}

					for (double value : resultArray) {
						resultValues.add(value);
					}

				} else if (funcName.equals("singl")) {
					Set<Object> set = new HashSet<Object>();
					for (Object value : values) {
						set.add(value);
					}

					for (Object value : set) {
						resultValues.add(value);
					}
				}

				TupleEntry tupleResult = new TupleEntry(entries.get(0).getType(), null, resultValues);
				lastReturnRes = new VariableEntry(TokenType.TYPE_TUPLE, null, tupleResult);
				return new VariableEntry(TokenType.TYPE_TUPLE, null, tupleResult);
			} else if (funcName.equals("extr") || funcName.equals("ins") || funcName.equals("get") ) {
				
				List<Object> resultValues = new ArrayList<Object>();
				
				if (funcName.equals("extr")) {
					if (entries.size() != 2) {
						// TODO ERROR
						System.out.println("ERROR. " + funcName + " REQUIRES 2 ARGUMENTS");
					}
					if(entries.get(1).getType() != TokenType.TYPE_INT) {
						//TODO ERROR
						System.out.println("ERROR. SECOND ARGUMENT MUST BE INTEGER");
					}
					Object object = entries.get(0).getValue();
					
					if (object.getClass() != TupleEntry.class) {
						// TODO ERROR
						System.out.println("ERROR. INVALID Tuple variable.");
					}
					int index = Integer.parseInt(entries.get(1).getValue().toString());
					
					resultValues = ((TupleEntry)object).getValues();
					if(index >= resultValues.size()) {
						//TODO ERROR
						System.out.println("ERROR. INDEX OUT OF BOUNDS EXCEPTION");
					}
					Object result = resultValues.remove(index);
					//TupleEntry tupleResult = new TupleEntry(entries.get(0).getType(), null, resultValues);
					lastReturnRes = new VariableEntry(((TupleEntry)object).getType(), null, result);
					return new VariableEntry(TokenType.TYPE_TUPLE, null, result);
					
				} else if (funcName.equals("get")) {
					if (entries.size() != 2) {
						// TODO ERROR
						System.out.println("ERROR. " + funcName + " REQUIRES 2 ARGUMENTS");
					}
					if(entries.get(1).getType() != TokenType.TYPE_INT) {
						//TODO ERROR
						System.out.println("ERROR. SECOND ARGUMENT MUST BE INTEGER");
					}
					Object object = entries.get(0).getValue();
					
					if (object.getClass() != TupleEntry.class) {
						// TODO ERROR
						System.out.println("ERROR. INVALID Tuple variable.");
					}
					int index = Integer.parseInt(entries.get(1).getValue().toString());
					
					resultValues = ((TupleEntry)object).getValues();
					if(index >= resultValues.size()) {
						//TODO ERROR
						System.out.println("ERROR. INDEX OUT OF BOUNDS EXCEPTION");
					}
					Object result = resultValues.get(index);
					//TupleEntry tupleResult = new TupleEntry(entries.get(0).getType(), null, resultValues);
					lastReturnRes = new VariableEntry(((TupleEntry)object).getType(), null, result);
					return new VariableEntry(TokenType.TYPE_TUPLE, null, result);
					
				} else if (funcName.equals("ins")) {
					
					if (entries.size() != 3) {
						// TODO ERROR
						System.out.println("ERROR. " + funcName + " REQUIRES 3 ARGUMENTS");
					}
					
					if(entries.get(1).getType() != TokenType.TYPE_INT) {
						//TODO ERROR
						System.out.println("ERROR. SECOND ARGUMENT MUST BE INTEGER");
					}
					
					int index = Integer.parseInt(entries.get(1).getValue().toString());
					Object entry = entries.get(0).getValue();
					
					if (entry.getClass() != TupleEntry.class) {
						// TODO ERROR
						System.out.println("ERROR. INVALID Tuple variable.");
					}
					
					if(((TupleEntry)entry).getType() != entries.get(2).getType()) {
						// TODO ERROR
						System.out.println("ERROR. INVALID insert variable type.");
					}
					
					Object insValue = entries.get(2).getValue();
					
					((TupleEntry)entry).getValues().add(index, insValue);
					
					lastReturnRes = entries.get(2);
					return entries.get(2);
				}
				
				
				TupleEntry tupleResult = new TupleEntry(entries.get(0).getType(), null, resultValues);
				lastReturnRes = new VariableEntry(TokenType.TYPE_TUPLE, null, tupleResult);
				return new VariableEntry(TokenType.TYPE_TUPLE, null, tupleResult);
			}

		} // DEFAULT FUNCTIONS END

		FunctionNode func = functionsMap.get(funcCall.getName().getToken().getValue());
		if (func == null) {
			// TODO ERROR
			System.out.println("ERROR. FUNCTION '" + funcCall.getName().getToken().getValue() + "' NOT FOUND.");
		}
		if (funcCall.getArgs().size() != func.getArgs().size()) {
			// TODO ERROR
			System.out.println("ERROR. FUNCTION '" + funcCall.getName().getToken().getValue()
					+ "' EXPECTED AMOUNT OF ARGS: " + func.getArgs().size() + ". GOT: " + funcCall.getArgs().size());

		}

		List<Node> gotArgs = funcCall.getArgs();
		List<VariableEntry> expectedArgs = func.getArgs();

		for (int k = 0; k < func.getArgs().size(); k++) {
			if (!checkNodeReturnType(gotArgs.get(k), expectedArgs.get(k).getType(), variableMap)) {
				// TODO ERROR
				System.out.println("ERROR. FUNCTION '" + funcCall.getName().getToken().getValue()
						+ "'. EXPECTED ARG TYPE: " + expectedArgs.get(k).getType() + " at index " + k);
			} else {

			}
		}

		Map<String, VariableEntry> bufferVariableMap = new HashMap<String, VariableEntry>();

		for (int k = 0; k < gotArgs.size(); k++) {

			if (gotArgs.get(k).getClass() == VariableNode.class) {
				String actualName = ((VariableNode) gotArgs.get(k)).getToken().getValue();
				VariableEntry entry = variableMap.get(actualName);

				bufferVariableMap.put(expectedArgs.get(k).getName(),
						new VariableEntry(entry.getType(), expectedArgs.get(k).getName(), entry.getValue()));
			} else {
				runInitializationOperation(new InitializationNode(expectedArgs.get(k).getName(),
						new AssignmentNode(expectedArgs.get(k).getName(), gotArgs.get(k)),
						new Token(expectedArgs.get(k).getType())), bufferVariableMap);
			}
		}

		runStatements(func.getInnerStatements(), false, bufferVariableMap);

		VariableEntry result = new VariableEntry(lastReturnRes.getType(), lastReturnRes.getName(),
				lastReturnRes.getValue());

		bufferVariableMap.clear();
		return result;
	}

	private boolean checkNodeReturnType(Node node, TokenType type, Map<String, VariableEntry> variableMap) {

		if (node.getClass() == BooleanNode.class) {
			return ((BooleanNode) node).getToken().getType() == type;
		} else if (node.getClass() == StringNode.class) {
			if (((StringNode) node).getToken().getType() == TokenType.STRING && type == TokenType.TYPE_STRING) {
				return true;
			}
			return ((StringNode) node).getToken().getType() == type;
		} else if (node.getClass() == FunctionCallNode.class) {
			return functionsMap.get(((FunctionCallNode) node).getName().getToken().getValue()).getReturnType()
					.getType() == type;
		} else if (node.getClass() == VariableNode.class) {
			return variableMap.get(((VariableNode) node).getToken().getValue()).getType() == type;
		} else if (node.getClass() == LogicalOperationNode.class) {
			return TokenType.TYPE_BOOLEAN == type;
		} else if (node.getClass() == BinaryOperationNode.class) {
			Object res = runBinaryOperation((BinaryOperationNode) node, variableMap);
			if (res.getClass() == Double.class) {
				if ((Double) res % 1 == 0) {
					return (type == TokenType.TYPE_INT) || (type == TokenType.TYPE_FLOAT);
				} else {
					return type == TokenType.TYPE_FLOAT;
				}
			} else if (res.getClass() == String.class) {
				return type == TokenType.TYPE_STRING;
			}
		} else if (node.getClass() == NumberNode.class) {
			Double res = Double.parseDouble(((NumberNode) node).getToken().getValue());
			if (res % 1 == 0) {
				return (type == TokenType.TYPE_INT) || (type == TokenType.TYPE_FLOAT);
			} else {
				return type == TokenType.TYPE_FLOAT;
			}
		} else {
			return false;
		}
		return false;
	}

	private boolean checkConditionExpression(Node expression, Map<String, VariableEntry> variableMap) {
		boolean expressionRes = false;

		// LogicalOperation
		if (expression.getClass() == LogicalOperationNode.class) {
			expressionRes = runLogicalOperation((LogicalOperationNode) expression, variableMap);
		}
		// True || False
		else if (expression.getClass() == BooleanNode.class) {
			expressionRes = Boolean.parseBoolean(((BooleanNode) expression).getToken().getValue());
		}
		// variable
		else if (expression.getClass() == VariableNode.class) {
			VariableEntry var = variableMap.get(((VariableNode) expression).getToken().getValue());
			if (var == null) {
				// TODO EXCEPTION
				System.out.println("VARIABLE " + ((VariableNode) expression).getToken().getValue() + " NOT FOUND");
			}
			if (var.getType() != TokenType.TYPE_BOOLEAN) {
				// TODO EXCEPTION
				System.out.println("VARIABLE " + ((VariableNode) expression).getToken().getValue() + " IS NOT "
						+ TokenType.TYPE_BOOLEAN.toString());
			}
			expressionRes = Boolean.parseBoolean(var.getValue().toString());
		} else {
			// TODO EXCEPTION
			System.out.println("ERROR. IF CLAUSE MUST HAVE VALID CONDITION EXPRESSION");
		}

		return expressionRes;
	}

	private boolean runConditionStatements(Node statement, Map<String, VariableEntry> variableMap) {
		ConditionNode node = ((ConditionNode) statement);
		Node expression = node.getExpression();
		List<Node> innerStatementsList = node.getInnerStatements();
		TokenType conditionType = node.getType(); // IF, ELSE IF, ELSE

		boolean expressionRes = false;

		if (conditionType != TokenType.ELSE) {
			expressionRes = checkConditionExpression(expression, variableMap);
		} else {
			expressionRes = true;
		}

		VariableEntry res = null;
		if (expressionRes) {
			res = runStatements(innerStatementsList, true, variableMap);
		}

		if (res != null) {
			// System.out.println("returned from if");
		}

		return expressionRes;
	}

	private void printVariablesMap(Map<String, VariableEntry> variableMap) {
		System.out.println("VARIABLES MEMORY: ");
		for (Map.Entry<String, VariableEntry> entry : variableMap.entrySet()) {
			System.out.println(entry.getValue().toString());
		}
	}

	private void printFunctionsMap() {

		System.out.println("FUNCTIONS MEMORY: ");
		for (Map.Entry<String, FunctionNode> entry : functionsMap.entrySet()) {
			entry.getValue().printAST("");
		}
	}

	private void runInitializationOperation(InitializationNode node, Map<String, VariableEntry> variableMap) {
		Node expression = node.getExpression();
		String name = node.getVariableName();
		TokenType type = node.getVariableType().getType();

		initVariable(type, name, null, variableMap);
		if (expression == null) {
			// initVariable(type, name, null);
			return;
		} else if (expression.getClass() == AssignmentNode.class) {
			runAssignmentOperation((AssignmentNode) expression, variableMap);
		} else if (expression.getClass() == TupleNode.class) {
			runAssignmentOperation(new AssignmentNode(name, (TupleNode) expression), variableMap);
		} else if (expression.getClass() == AgregateNode.class) {
			runAssignmentOperation(new AssignmentNode(name, (AgregateNode) expression), variableMap);
		}
	}

	private boolean initVariable(TokenType type, String name, Object value, Map<String, VariableEntry> variableMap) {
		if (variableMap.containsKey(name)) {
			// TODO error
			System.out.println("Variable " + name + " is already initialized");
			//
			return false;
		} else {
			variableMap.put(name, new VariableEntry(type, name, value));
			return true;
		}
	}

	private boolean assignVariable(String name, Object value, Map<String, VariableEntry> variableMap) {
		variableMap.get(name).setValue(value);
		return true;
	}

	private boolean runLogicalOperation(LogicalOperationNode node, Map<String, VariableEntry> variableMap) {
		Node leftNode = node.getLeft();
		Node rightNode = node.getRight();
		Token operator = node.getOperator();

		boolean isBool = false;
		boolean isString = false;
		boolean isNumber = false;
		boolean isTime = false;

		String leftString = "";
		String rightString = "";

		double leftNumber = 0;
		double rightNumber = 0;

		boolean leftValue = false;
		boolean rightValue = false;

		Pattern pattern = Pattern.compile(timeRegex);

		// variable, value, operation

		if (leftNode.getClass() == VariableNode.class) {
			String varName = ((VariableNode) leftNode).getToken().getValue();

			if (variableMap.get(varName).getValue().getClass() == TupleEntry.class) {
				// TODO ERROR
				System.out.println("ERROR. NO CASUAL BINARY OPERATIONS ALLOWED WITH THE TUPLES");
			} else if (variableMap.get(varName).getType() == TokenType.TYPE_BOOLEAN) {
				leftValue = Boolean.parseBoolean(variableMap.get(varName).getValue().toString());
				isBool = true;
			} else if (variableMap.get(varName).getType() == TokenType.TYPE_STRING) {
				leftString = variableMap.get(varName).getValue().toString();
				isString = true;
			} else if (variableMap.get(varName).getType() == TokenType.TYPE_TIME) {
				leftString = variableMap.get(varName).getValue().toString();
				isTime = true;
			} else {
				try {
					leftNumber = ((Number) variableMap.get(varName).getValue()).doubleValue();
					isNumber = true;
				} catch (Exception ex) {
					// TODO ERROR
					System.out.println(ex.getMessage());
					System.out.println("ERROR. CAN NOT ASSIGN "
							+ variableMap.get(((VariableNode) leftNode).getToken().getValue()).getType().toString()
							+ " value to boolean variable");
				}
			}
		} else if (leftNode.getClass() == BooleanNode.class) {
			leftValue = Boolean.parseBoolean(((BooleanNode) leftNode).getToken().getValue());
			isBool = true;
		} else if (leftNode.getClass() == LogicalOperationNode.class) {
			leftValue = runLogicalOperation((LogicalOperationNode) leftNode, variableMap);
			isBool = true;
		} else if (leftNode.getClass() == NumberNode.class) {
			leftNumber = Double.valueOf(((NumberNode) leftNode).getToken().getValue());
			isNumber = true;
		} else if (leftNode.getClass() == StringNode.class) {
			leftString = ((StringNode) leftNode).getToken().getValue();
			isString = true;
		} else if (leftNode.getClass() == TimeNode.class) {
			leftString = ((TimeNode) leftNode).getToken().getValue();
			isTime = true;
		} else if (leftNode.getClass() == BinaryOperationNode.class) {
			Object object = runBinaryOperation((BinaryOperationNode) leftNode, variableMap);
			Matcher matcher = pattern.matcher(object.toString());
			if (matcher.matches()) {
				leftString = object.toString();
				isTime = true;
			} else if (object.getClass() == String.class) {
				leftString = object.toString();
				isString = true;
			} else {
				leftNumber = (double) object;
				isNumber = true;
			}
		}

		// oper == ! => rightNode == null
		if (operator.getType() != TokenType.OPER_NOT) {
			if (rightNode.getClass() == VariableNode.class) {
				String varName = ((VariableNode) rightNode).getToken().getValue();

				if (variableMap.get(varName).getValue().getClass() == TupleEntry.class) {
					System.out.println("ERROR. NO CASUAL BINARY OPERATIONS ALLOWED WITH THE TUPLES");
				} else if (variableMap.get(varName).getType() == TokenType.TYPE_BOOLEAN) {
					rightValue = Boolean.parseBoolean(variableMap.get(varName).getValue().toString());
					isBool = true;
				} else if (variableMap.get(varName).getType() == TokenType.TYPE_STRING) {
					rightString = variableMap.get(varName).getValue().toString();
					isString = true;
				} else if (variableMap.get(varName).getType() == TokenType.TYPE_TIME) {
					rightString = variableMap.get(varName).getValue().toString();
					isTime = true;
				} else {
					try {
						rightNumber = ((Number) variableMap.get(varName).getValue()).doubleValue();
						isNumber = true;
					} catch (Exception ex) {
						// TODO ERROR
						System.out.println(ex.getMessage());
						System.out.println("ERROR. CAN NOT ASSIGN "
								+ variableMap.get(((VariableNode) rightNode).getToken().getValue()).getType().toString()
								+ " value to boolean variable");
					}
				}
			} else if (rightNode.getClass() == BooleanNode.class) {
				rightValue = Boolean.parseBoolean(((BooleanNode) rightNode).getToken().getValue());
				isBool = true;
			} else if (rightNode.getClass() == LogicalOperationNode.class) {
				rightValue = runLogicalOperation((LogicalOperationNode) rightNode, variableMap);
				isBool = true;
			} else if (rightNode.getClass() == NumberNode.class) {
				rightNumber = Double.valueOf(((NumberNode) rightNode).getToken().getValue());
				isNumber = true;
			} else if (rightNode.getClass() == StringNode.class) {
				rightString = ((StringNode) rightNode).getToken().getValue();
				isString = true;
			} else if (rightNode.getClass() == TimeNode.class) {
				rightString = ((TimeNode) rightNode).getToken().getValue();
				isTime = true;
			} else if (rightNode.getClass() == BinaryOperationNode.class) {
				Object object = runBinaryOperation((BinaryOperationNode) rightNode, variableMap);
				Matcher matcher = pattern.matcher(object.toString());
				if (matcher.matches()) {
					rightString = object.toString();
					isTime = true;
				} else if (object.getClass() == String.class) {
					rightString = object.toString();
					isString = true;
				} else {
					rightNumber = (double) object;
					isNumber = true;
				}
			}
		} else {
			if (!isBool) {
				System.out.println("ERROR. WRONG OPERATOR '!' USAGE");
			}
			// TODO ERROR
			return !leftValue;
		}

		// OPERATORS
		if (operator.getType() == TokenType.OPER_IS) {
			if (isBool) {
				return leftValue == rightValue;
			} else if (isNumber) {
				return leftNumber == rightNumber;
			} else if (isString || isTime) {
				return leftString.equals(rightString);
			}
		} else if (operator.getType() == TokenType.OPER_IS_NOT) {
			if (isBool) {
				return leftValue != rightValue;
			} else if (isNumber) {
				return leftNumber != rightNumber;
			} else if (isString || isTime) {
				return !(leftString.equals(rightString));
			}
		} else if (operator.getType() == TokenType.OPER_LESS) {
			if (isNumber) {
				return leftNumber < rightNumber;
			} else if (isTime) {
				return TimeNode.less(leftString, rightString);
			}
		} else if (operator.getType() == TokenType.OPER_LESS_EQUALS) {
			if (isNumber) {
				return leftNumber <= rightNumber;
			} else if (isTime) {
				return TimeNode.less(leftString, rightString) || (leftString.equals(rightString));
			}
		} else if (operator.getType() == TokenType.OPER_MORE) {
			if (isNumber) {
				return leftNumber > rightNumber;
			} else if (isTime) {
				return TimeNode.more(leftString, rightString);
			}
		} else if (operator.getType() == TokenType.OPER_MORE_EQUALS) {
			if (isNumber) {
				return leftNumber >= rightNumber;
			} else if (isTime) {
				return TimeNode.more(leftString, rightString) || (leftString.equals(rightString));
			}
		} else if (operator.getType() == TokenType.OPER_AND) {
			if (isBool) {
				return leftValue && rightValue;
			}
		} else if (operator.getType() == TokenType.OPER_OR) {
			if (isBool) {
				return leftValue || rightValue;
			}
			;
		}
		return false;
	}

	private Object runBinaryOperation(BinaryOperationNode node, Map<String, VariableEntry> variableMap) {
		Node leftNode = node.getLeft();
		Node rightNode = node.getRight();
		Token operator = node.getOperator();
		boolean isConcat = false;
		boolean isTimeOp = false;
		double right = 0;
		double left = 0;

		Pattern pattern = Pattern.compile(timeRegex);

		String leftString = "";
		String rightString = "";

		if (leftNode.getClass() == TimeNode.class) {
			leftString = ((TimeNode) leftNode).getToken().getValue();
			isTimeOp = true;
		} else if (leftNode.getClass() == StringNode.class) {
			leftString = ((StringNode) leftNode).getToken().getValue();
			isConcat = true;
		} else if (leftNode.getClass() == BinaryOperationNode.class) {
			Object leftObject = runBinaryOperation((BinaryOperationNode) leftNode, variableMap);
			if (leftObject.getClass() == String.class) {
				Matcher matcher = pattern.matcher(leftObject.toString());
				if (matcher.matches()) {
					isTimeOp = true;
				} else {
					isConcat = true;
				}
				leftString = leftObject.toString();

			} else {
				left = (double) leftObject;
			}
		} else if (leftNode.getClass() == NumberNode.class) {
			left = Double.valueOf(((NumberNode) leftNode).getToken().getValue());
		} else if (leftNode.getClass() == VariableNode.class) {
			// TODO error
			String varName = ((VariableNode) leftNode).getToken().getValue();

			if (variableMap.get(varName).getValue().getClass() == TupleEntry.class) {
				// TODO ERROR
				System.out.println("ERROR. NO CASUAL BINARY OPERATIONS ALLOWED WITH THE TUPLES");
			} else if (variableMap.get(varName).getType() == TokenType.TYPE_STRING) {
				isConcat = true;
				leftString = variableMap.get(varName).getValue().toString();
			} else if (variableMap.get(varName).getType() == TokenType.TYPE_TIME) {
				isTimeOp = true;
				leftString = variableMap.get(varName).getValue().toString();
			} else {
				try {
					left = ((Number) variableMap.get(varName).getValue()).doubleValue();
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		}

		//

		if (rightNode.getClass() == TimeNode.class) {
			rightString = ((TimeNode) rightNode).getToken().getValue();
			isTimeOp = true;
		} else if (rightNode.getClass() == StringNode.class) {
			rightString = ((StringNode) rightNode).getToken().getValue();
			isConcat = true;
		} else if (rightNode.getClass() == BinaryOperationNode.class) {
			Object rightObject = runBinaryOperation((BinaryOperationNode) rightNode, variableMap);
			if (rightObject.getClass() == String.class) {
				Matcher matcher = pattern.matcher(rightObject.toString());
				if (matcher.matches()) {
					isTimeOp = true;
				} else {
					isConcat = true;
				}
				rightString = rightObject.toString();

			} else {
				right = (double) rightObject;
			}
		} else if (rightNode.getClass() == NumberNode.class) {
			right = Double.valueOf(((NumberNode) rightNode).getToken().getValue());
		} else if (rightNode.getClass() == VariableNode.class) {

			String varName = ((VariableNode) rightNode).getToken().getValue();

			if (variableMap.get(varName).getValue().getClass() == TupleEntry.class) {
				// TODO ERROR
				System.out.println("ERROR. NO CASUAL BINARY OPERATIONS ALLOWED WITH THE TUPLES");
			} else if (variableMap.get(varName).getType() == TokenType.TYPE_STRING) {
				isConcat = true;
				rightString = variableMap.get(varName).getValue().toString();
			} else if (variableMap.get(varName).getType() == TokenType.TYPE_TIME) {
				isTimeOp = true;
				rightString = variableMap.get(varName).getValue().toString();
			} else {
				try {
					right = ((Number) variableMap.get(varName).getValue()).doubleValue();
				} catch (Exception ex) {
					// TODO error
					System.out.println(ex.getMessage());
				}
			}
		}

		TokenType opType = operator.getType();

		if (isConcat) {
			if (opType == TokenType.OPER_PLUS) {
				return leftString + rightString;
			} else {
				// TODO ERROR
				System.out.println("ERROR. OPERATION " + opType.toString() + " IS NOT ALLOWED BETWEEN STRING VALUES");
			}
		} else if (isTimeOp) {
			if (opType == TokenType.OPER_PLUS) {
				return TimeNode.sum(leftString, rightString);
			} else if (opType == TokenType.OPER_MINUS) {
				return TimeNode.diff(leftString, rightString);
			} else {
				// TODO ERROR
				System.out.println("ERROR. OPERATION " + opType.toString() + " IS NOT ALLOWED BETWEEN STRING VALUES");
			}
		} else if (opType == TokenType.OPER_PLUS) {
			return left + right;
		} else if (opType == TokenType.OPER_MINUS) {
			return left - right;
		} else if (opType == TokenType.OPER_DIVISION) {
			if ((double) right == 0) {
				System.out.println("DIVISION BY ZERO ERROR");
				// TODO error
			}
			return left / right;

		} else if (opType == TokenType.OPER_MULTIPLY) {
			return left * right;
		} else if (opType == TokenType.OPER_POWER) {
			return Math.pow(left, right);
		}
//		TODO else if (opType = TokenType.OPER_EQUALS) {
//	
//		}
		return 0;
	}

	private void runAssignmentOperation(AssignmentNode node, Map<String, VariableEntry> variableMap) {
		String name = node.getVariableName();

		if (!variableMap.containsKey(name)) {
			System.out.println("VARIABLE " + name + " WAS NEVER INITIALIZED");
			// TODO error
			return;
		}

		VariableEntry variable = variableMap.get(name);

		TokenType type = variable.getType();

		// type a = b;

		Node assignmentInnerExpression = node.getExpression();
		
		//AGREGATE INITIALIZATION => INNER ASSIGNMENT
		if(type == TokenType.TYPE_AGREGATE) {
			List<TupleEntry> entries = new ArrayList<TupleEntry>();
			if(assignmentInnerExpression.getClass() == AgregateNode.class) {
				
				for(Node innerEntry : ((AgregateNode)assignmentInnerExpression).getValues()) {
					if(innerEntry.getClass() != VariableNode.class) {
						System.out.println("ERROR. AGREGATE ASSIGNMENT FAILURE. INNER VALUES MUST BE VARIABLES");
					}
					if(checkNodeReturnType(innerEntry, TokenType.TYPE_TUPLE, variableMap)) {
						VariableEntry var = variableMap.get(((VariableNode)innerEntry).getToken().getValue());
						entries.add(((TupleEntry)var.getValue()));
						
					} else {
						//TODO ERROR
						System.out.println("ERROR. AGREGATE ASSIGNMENT FAILURE. VARIABLE TYPE MUST BE TUPLE");
					}
				}
				assignVariable(name, entries, variableMap);
			} else {
				//TODO ERROR
				System.out.println("ERROR. AGREGATE ASSIGNMENT FAILURE.");
			}
		}
		//TUPLE INITIALIZATION => INNER ASSIGNMENT
		if (type == TokenType.TYPE_TUPLE) {
			if (assignmentInnerExpression.getClass() == TupleNode.class) {
				TupleNode tuple = (TupleNode) assignmentInnerExpression;
				List<Node> elements = tuple.getArgs();
				TokenType tupleType = tuple.getType();
				List<Object> objectElements = new ArrayList<Object>();
				for (Node element : elements) {
					if (checkNodeReturnType(element, tupleType, variableMap)) {
						objectElements.add(runReturn(new ReturnNode(element), variableMap).getValue());
						lastReturnRes = null;
						isReturnCalled = false;
					} else {
						// TODO ERROR
						System.out.println("ERROR! TUPLE ENTRIES TYPE MISSMATCH.");
					}
				}
				TupleEntry entry = new TupleEntry(tupleType, name, objectElements);
				assignVariable(name, entry, variableMap);

			} else if (assignmentInnerExpression.getClass() == VariableNode.class) {
				VariableEntry variable2 = variableMap
						.get(((VariableNode) assignmentInnerExpression).getToken().getValue());

				if (variable2.getType() == TokenType.TYPE_TUPLE) {
					if (variable2.getValue().getClass() == TupleEntry.class) {
						TokenType tupleType = ((TupleEntry) variableMap.get(name).getValue()).getType();

						if (((TupleEntry) variable2.getValue()).getType() == tupleType) {
							assignVariable(name, variable2.getValue(), variableMap);
						} else {
							System.out.println("ERROR. CAN NOT ASSIGN TUPLE"
									+ ((TupleEntry) variable2.getValue()).getType() + " VALUE TO TUPLE" + tupleType);
						}

					} else {
						System.out.println("ERROR. VARIABLE IS NULL");
					}
				} else {
					System.out.println("ERROR. CAN NOT ASSIGN " + variable2.getType() + " VALUE TO TUPLE");
				}
				// TODO ERRORS

			} else if (assignmentInnerExpression.getClass() == FunctionCallNode.class) {
				FunctionNode func = functionsMap
						.get(((FunctionCallNode) assignmentInnerExpression).getName().getToken().getValue());
				if (func == null) {
					// TODO ERROR
					System.out.println("ERROR. FUNCTION NOT FOUND");
				}

				if (checkDefaultFunction(func.getName().getToken().getValue())
						|| func.getReturnType().getType() == type) {
					runFunctionCall((FunctionCallNode) assignmentInnerExpression, variableMap);
					if (lastReturnRes != null) {
						assignVariable(name, lastReturnRes.getValue(), variableMap);
						//System.out.println(name);
						//System.out.println(lastReturnRes.toString());
						//System.out.println(((TupleEntry) lastReturnRes.getValue()).getValues().toString());
						// CURRRRRR
					}
					lastReturnRes = null;
					isReturnCalled = false;
				} else {
					System.out.println(
							"TYPE MATCHING ERROR. ASSIGNING " + func.getReturnType().getType() + " TO " + type);
				}
			}

		}

		else if (assignmentInnerExpression.getClass() == VariableNode.class) {

			String varName = ((VariableNode) assignmentInnerExpression).getToken().getValue();

			VariableEntry var = variableMap.get(varName);

			if (var == null) {
				System.out.println("VARIABLE " + varName + " WAS NEVER INITIALIZED");
				// TODO error
				return;
			}
			if (type == var.getType()) {

				assignVariable(name, var.getValue(), variableMap);
			} else {
				System.out.println("ERROR. CAN NOT ASSIGN " + var.getType() + " TO " + type);
				// TODO error
			}
		}

		else if (assignmentInnerExpression.getClass() == TimeNode.class) {
			TimeNode expression = (TimeNode) assignmentInnerExpression;
			String timeString = expression.getToken().getValue();
			if (type == TokenType.TYPE_TIME) {
				assignVariable(name, timeString, variableMap);
			} else {
				System.out.println("ERROR. CAN NOT ASSIGN " + TokenType.TYPE_TIME + " TO " + type);
			}

		}
		// type a = 123.4; type a = 123;
		else if (assignmentInnerExpression.getClass() == NumberNode.class) {
			String number = ((NumberNode) assignmentInnerExpression).getToken().getValue();

			boolean isInt = false;
			boolean isDouble = false;
			int integerNum = 0;
			double doubleNum = 0;
			try {
				integerNum = Integer.parseInt(number);
				isInt = true;
			} catch (Exception exInt) {
				try {
					doubleNum = Double.parseDouble(number);
					isDouble = true;
				} catch (Exception exDouble) {
					System.out.println("Error: " + exDouble.getLocalizedMessage());
				}
			}

			TokenType numberType = null;

			if (isInt) {
				numberType = TokenType.TYPE_INT;
			} else if (isDouble) {
				numberType = TokenType.TYPE_FLOAT;
			}

			if (numberType == type) {
				if (isInt) {
					assignVariable(name, integerNum, variableMap);
				} else if (isDouble) {
					assignVariable(name, doubleNum, variableMap);
				}
			} else {
				System.out.println("INITIALIZATION ERROR. VARIABLE TYPE: " + type.toString() + "; NUMBER TYPE: "
						+ numberType.toString());
			}
		}
		// [boolean] x = false || true ...;
		else if (assignmentInnerExpression.getClass() == LogicalOperationNode.class) {
			boolean result = runLogicalOperation((LogicalOperationNode) assignmentInnerExpression, variableMap);
			assignVariable(name, result, variableMap);
		}

		// [int/float/string/time] a = c [op] b;
		else if (assignmentInnerExpression.getClass() == BinaryOperationNode.class) {
			Object resObject = runBinaryOperation((BinaryOperationNode) assignmentInnerExpression, variableMap);

			double resNumber = 0;
			// System.out.println(resObject.getClass());
			if (resObject.getClass() != String.class) {
				resNumber = (double) resObject;
			} else if (resObject.getClass() == String.class) {
				if (type == TokenType.TYPE_STRING) {
					assignVariable(name, resObject.toString(), variableMap);
					return;
				} else if (type == TokenType.TYPE_TIME) {
					// TODO CHECK TIME
					assignVariable(name, resObject.toString(), variableMap);
					return;
				} else {
					System.out.println("TYPE MATCHING ERROR. CAN NOT ASSIGN STRING TO THE " + type.toString());
					return;
				}
			} /*
				 * else { assignVariable(name, resObject, variableMap); return; }
				 */

			if (type == TokenType.TYPE_FLOAT) {

				assignVariable(name, resNumber, variableMap);
				return;
			}

			boolean isInt = resNumber % 1 == 0;
			TokenType resNumberType = null;
			if (isInt) {
				resNumberType = TokenType.TYPE_INT;
			} else {
				resNumberType = TokenType.TYPE_FLOAT;
			}

			if (resNumberType == type) {
				if (isInt) {
					assignVariable(name, Math.round(resNumber), variableMap);
				} else {
					assignVariable(name, resNumber, variableMap);
				}
			} else {
				System.out.println("TYPE MATCHING ERROR");
				// TODO ERROR
			}

		}

		// boolean a = [false/true];
		else if (assignmentInnerExpression.getClass() == BooleanNode.class) {
			if (type == TokenType.TYPE_BOOLEAN) {
				boolean value = Boolean.parseBoolean(((BooleanNode) assignmentInnerExpression).getToken().getValue());
				assignVariable(name, value, variableMap);
			} else {
				System.out.println("TYPE MATCHING ERROR. ASSIGNING " + TokenType.TYPE_BOOLEAN + " TO " + type);
			}
		}
		// String a = "text";
		else if (assignmentInnerExpression.getClass() == StringNode.class) {
			if (type == TokenType.TYPE_STRING) {
				String value = ((StringNode) assignmentInnerExpression).getToken().getValue();
				assignVariable(name, value, variableMap);
			} else {
				System.out.println("TYPE MATCHING ERROR. ASSIGNING " + TokenType.TYPE_STRING + " TO " + type);
			}

		} else if (assignmentInnerExpression.getClass() == FunctionCallNode.class) {
			FunctionNode func = functionsMap
					.get(((FunctionCallNode) assignmentInnerExpression).getName().getToken().getValue());
			if (func == null) {
				// TODO ERROR
				System.out.println("ERROR. FUNCTION NOT FOUND");
			}
			if(defaultFunctionNames.contains(func.getName().getToken().getValue())) {
				runFunctionCall((FunctionCallNode) assignmentInnerExpression, variableMap);

				assignVariable(name, lastReturnRes.getValue(), variableMap);
				lastReturnRes = null;
				isReturnCalled = false;
			}
			else if (func.getReturnType().getType() == type) {
				runFunctionCall((FunctionCallNode) assignmentInnerExpression, variableMap);

				assignVariable(name, lastReturnRes.getValue(), variableMap);
				lastReturnRes = null;
				isReturnCalled = false;
			} else {
				System.out.println("TYPE MATCHING ERROR. ASSIGNING " + func.getReturnType().getType() + " TO " + type);
			}
		}
	}
}
