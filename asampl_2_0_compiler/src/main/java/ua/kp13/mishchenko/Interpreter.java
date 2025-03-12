package ua.kp13.mishchenko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.kp13.mishchenko.ast.AssignmentNode;
import ua.kp13.mishchenko.ast.BinaryOperationNode;
import ua.kp13.mishchenko.ast.BooleanNode;
import ua.kp13.mishchenko.ast.ConditionNode;
import ua.kp13.mishchenko.ast.InitializationNode;
import ua.kp13.mishchenko.ast.LogicalOperationNode;
import ua.kp13.mishchenko.ast.Node;
import ua.kp13.mishchenko.ast.NumberNode;
import ua.kp13.mishchenko.ast.ProgramNode;
import ua.kp13.mishchenko.ast.StringNode;
import ua.kp13.mishchenko.ast.VariableNode;

public class Interpreter {
	private Node program;
	private Map<String, VariableEntry> variableMap;
	private List<String> clearList = new ArrayList<String>();
	
	public Interpreter(Node program) {
		this.program = program;
		variableMap = new HashMap<String, VariableEntry>();
	}
	
	public void run() {
		List<Node> statements = ((ProgramNode) program).getStatements();
		runStatements(statements, false);
		printVariablesMap();
	}

	private void runStatements(List<Node> statements, boolean isInner) {
		
		for(int i=0; i<statements.size(); i++) {
			Node statement = statements.get(i);
			if (statement.getClass() == InitializationNode.class) {
				
				runInitializationOperation((InitializationNode) statement);
				if(isInner) {
					clearList.add(((InitializationNode) statement).getVariableName());
				}
				
			} else if (statement.getClass() == AssignmentNode.class) {
				
				runAssignmentOperation((AssignmentNode) statement);
				//if(isInner) {
				//	clearList.add(((AssignmentNode) statement).getVariableName());
				//}
				
			} else if (statement.getClass() == BinaryOperationNode.class) {
				System.out.println(runBinaryOperation((BinaryOperationNode) statement));
				// TODO ????????
			} else if (statement.getClass() == ConditionNode.class) {
				List<ConditionNode> ifClauseConstruction = new ArrayList<ConditionNode>();
				while(statements.get(i).getClass() == ConditionNode.class) {
					if(((ConditionNode)statements.get(i)).getType() == TokenType.IF
							&& ifClauseConstruction.size() == 0) {
						ifClauseConstruction.add((ConditionNode)statements.get(i));
						i++;
					} else if(((ConditionNode)statements.get(i)).getType() == TokenType.ELIF) {
						ifClauseConstruction.add((ConditionNode)statements.get(i));
						i++;
					}  else if(((ConditionNode)statements.get(i)).getType() == TokenType.ELSE) {
						ifClauseConstruction.add((ConditionNode)statements.get(i));
						i++;
					}
					if(i>statements.size()-1) {
						i--;
						break;
					}
				}
				if(statements.get(i).getClass() != ConditionNode.class) {
					i--;
				}
				
				
				boolean passed = false;
				for(int j = 0; j<ifClauseConstruction.size(); j++) {
					if(passed) {
						break;
					}
					ConditionNode condition = ifClauseConstruction.get(j);
					passed = runConditionStatements(condition);
				}
			}
		}
		
		//CLEARING
		for(String varName : clearList) {
			variableMap.remove(varName);
		}
		clearList.clear();
	}
	
	private boolean runConditionStatements(Node statement) {
		ConditionNode node = ((ConditionNode)statement);
		Node expression = node.getExpression();
		List<Node> innerStatementsList = node.getInnerStatements();
		TokenType conditionType = node.getType(); //IF, ELSE IF, ELSE
		
		boolean expressionRes = false;
		if(conditionType != TokenType.ELSE) {
			//	LogicalOperation
			if(expression.getClass() == LogicalOperationNode.class) {
				expressionRes = runLogicalOperation((LogicalOperationNode)expression);
			}
			//True || False
			else if(expression.getClass() == BooleanNode.class) {
				expressionRes = Boolean.parseBoolean(((BooleanNode)expression).getToken().getValue());
			}
			//variable
			else if(expression.getClass() == VariableNode.class) {
				VariableEntry var = variableMap.get(((VariableNode)expression).getToken().getValue());
				if(var == null) {
					//TODO EXCEPTION
					System.out.println("VARIABLE " + ((VariableNode)expression).getToken().getValue() + " NOT FOUND");
				}
				if(var.getType() != TokenType.TYPE_BOOLEAN) {
					//TODO EXCEPTION
					System.out.println("VARIABLE " + ((VariableNode)expression).getToken().getValue() + " IS NOT " + TokenType.TYPE_BOOLEAN.toString());
				}
				expressionRes = Boolean.parseBoolean(var.getValue().toString());
			} else {
				//TODO EXCEPTION
				System.out.println("ERROR. IF CLAUSE MUST HAVE VALID CONDITION EXPRESSION");
			}
		} else {
			expressionRes = true;
		}
		
		if(expressionRes) {
			runStatements(innerStatementsList, true);
			
		}
		
		return expressionRes;
	}
	
	private void printVariablesMap() {
		for (Map.Entry<String, VariableEntry> entry : variableMap.entrySet()) {
			System.out.println(entry.getValue().toString());
		}
	}

	private void runInitializationOperation(InitializationNode node) {
		Node expression = node.getExpression();
		String name = node.getVariableName();
		TokenType type = node.getVariableType().getType();

		initVariable(type, name, null);
		if (expression == null) {
			// initVariable(type, name, null);
			return;
		} else if (expression.getClass() == AssignmentNode.class) {
			runAssignmentOperation((AssignmentNode) expression);
		}

	}

	private boolean initVariable(TokenType type, String name, Object value) {
		if (variableMap.containsKey(name)) {
			// TODO error
			System.out.println("Variable " + name +" is already initialized");
			//
			return false;
		} else {
			variableMap.put(name, new VariableEntry(type, name, value));
			return true;
		}
	}

	private boolean assignVariable(String name, Object value) {
		variableMap.get(name).setValue(value);
		return true;
	}

	private boolean runLogicalOperation(LogicalOperationNode node) {
		Node leftNode = node.getLeft();
		Node rightNode = node.getRight();
		Token operator = node.getOperator();

		boolean isBool = false;
		boolean isString = false;
		boolean isNumber = false;

		String leftString = "";
		String rightString = "";

		double leftNumber = 0;
		double rightNumber = 0;

		boolean leftValue = false;
		boolean rightValue = false;

		// variable, value, operation

		if (leftNode.getClass() == VariableNode.class) {
			String varName = ((VariableNode) leftNode).getToken().getValue();

			if (variableMap.get(varName).getType() == TokenType.TYPE_BOOLEAN) {
				leftValue = Boolean.parseBoolean(variableMap.get(varName).getValue().toString());
				isBool = true;
			} else if (variableMap.get(varName).getType() == TokenType.TYPE_STRING) {
				leftString = variableMap.get(varName).getValue().toString();
				isString = true;
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
			leftValue = runLogicalOperation((LogicalOperationNode) leftNode);
			isBool = true;
		} else if (leftNode.getClass() == NumberNode.class) {
			leftNumber = Double.valueOf(((NumberNode) leftNode).getToken().getValue());
			isNumber = true;
		} else if (leftNode.getClass() == BinaryOperationNode.class) {
			Object object = runBinaryOperation((BinaryOperationNode) leftNode);
			if(object.getClass() == String.class) {
				leftString = object.toString();
				isString = true;
			} else {
				leftNumber = (double)object;
				isNumber = true;
			}
		}

		// oper == ! => rightNode == null
		if (operator.getType() != TokenType.OPER_NOT) {
			if (rightNode.getClass() == VariableNode.class) {
				String varName = ((VariableNode) rightNode).getToken().getValue();

				if (variableMap.get(varName).getType() == TokenType.TYPE_BOOLEAN) {
					rightValue = Boolean.parseBoolean(variableMap.get(varName).getValue().toString());
					isBool = true;
				} else if (variableMap.get(varName).getType() == TokenType.TYPE_STRING) {
					rightString = variableMap.get(varName).getValue().toString();
					isString = true;
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
				rightValue = runLogicalOperation((LogicalOperationNode) rightNode);
				isBool = true;
			} else if (rightNode.getClass() == NumberNode.class) {
				rightNumber = Double.valueOf(((NumberNode) rightNode).getToken().getValue());
				isNumber = true;
			} else if (rightNode.getClass() == BinaryOperationNode.class) {
				Object object = runBinaryOperation((BinaryOperationNode) rightNode);
				if(object.getClass() == String.class) {
					rightString = object.toString();
					isString = true;
				} else {
					rightNumber = (double)object;
					isNumber = true;
				}
			}
		} else {
			if(!isBool) {
				System.out.println("ERROR. WRONG OPERATOR '!' USAGE");
			}
			//TODO ERROR
			return !leftValue;
		}
		

		

		// OPERATORS		
		if (operator.getType() == TokenType.OPER_IS) {
			if(isBool) {
				return leftValue == rightValue;
			} else if(isNumber) {
				return leftNumber == rightNumber;
			} else if(isString) {
				return leftString.equals(rightString);
			}
		} else if (operator.getType() == TokenType.OPER_IS_NOT) {
			if(isBool) {
				return leftValue != rightValue;
			} else if(isNumber) {
				return leftNumber != rightNumber;
			} else if(isString) {
				return !(leftString.equals(rightString));
			}
		} else if (operator.getType() == TokenType.OPER_LESS) {
			if(isNumber) {
				return leftNumber < rightNumber;
			}
		} else if (operator.getType() == TokenType.OPER_LESS_EQUALS) {
			if(isNumber) {
				return leftNumber <= rightNumber;
			}
		} else if (operator.getType() == TokenType.OPER_MORE) {
			if(isNumber) {
				return leftNumber > rightNumber;
			}
		} else if (operator.getType() == TokenType.OPER_MORE_EQUALS) {
			if(isNumber) {
				return leftNumber >= rightNumber;
			}
		} else if (operator.getType() == TokenType.OPER_AND) {
			if(isBool) {
				return leftValue && rightValue;
			}
		} else if (operator.getType() == TokenType.OPER_OR) {
			if(isBool) {
				return leftValue || rightValue;
			};
		}
		return false;
	}

	private Object runBinaryOperation(BinaryOperationNode node) {
		Node leftNode = node.getLeft();
		Node rightNode = node.getRight();
		Token operator = node.getOperator();
		boolean isConcat = false;

		double right = 0;
		double left = 0;

		String leftString = "";
		String rightString = "";

		if (leftNode.getClass() == StringNode.class) {
			leftString = ((StringNode) leftNode).getToken().getValue();
			isConcat = true;
		} else if (leftNode.getClass() == BinaryOperationNode.class) {
			Object leftObject = runBinaryOperation((BinaryOperationNode) leftNode);
			if (leftObject.getClass() == String.class) {
				leftString = leftObject.toString();
				isConcat = true;
			} else {
				left = (double) leftObject;
			}
		} else if (leftNode.getClass() == NumberNode.class) {
			left = Double.valueOf(((NumberNode) leftNode).getToken().getValue());
		} else if (leftNode.getClass() == VariableNode.class) {
			// TODO error
			String varName = ((VariableNode) leftNode).getToken().getValue();

			if (variableMap.get(varName).getType() == TokenType.TYPE_STRING) {
				isConcat = true;
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

		if (rightNode.getClass() == StringNode.class) {
			rightString = ((StringNode) rightNode).getToken().getValue();
			isConcat = true;
		} else if (rightNode.getClass() == BinaryOperationNode.class) {
			Object rightObject = runBinaryOperation((BinaryOperationNode) rightNode);
			if (rightObject.getClass() == String.class) {
				rightString = rightObject.toString();
				isConcat = true;
			} else {
				right = (double) rightObject;
			}
		} else if (rightNode.getClass() == NumberNode.class) {
			right = Double.valueOf(((NumberNode) rightNode).getToken().getValue());
		} else if (rightNode.getClass() == VariableNode.class) {

			String varName = ((VariableNode) rightNode).getToken().getValue();

			if (variableMap.get(varName).getType() == TokenType.TYPE_STRING) {
				isConcat = true;
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

	private void runAssignmentOperation(AssignmentNode node) {
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

		if (assignmentInnerExpression.getClass() == VariableNode.class) {

			String varName = ((VariableNode) assignmentInnerExpression).getToken().getValue();

			VariableEntry var = variableMap.get(varName);

			if (var == null) {
				System.out.println("VARIABLE " + varName + " WAS NEVER INITIALIZED");
				// TODO error
				return;
			}
			if (type == var.getType()) {
				assignVariable(name, var.getValue());
			} else {
				System.out.println("ERROR. CAN NOT ASSIGN " + var.getType() + " TO " + type);
				// TODO error
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
					assignVariable(name, integerNum);
				} else if (isDouble) {
					assignVariable(name, doubleNum);
				}
			} else {
				System.out.println("INITIALIZATION ERROR. VARIABLE TYPE: " + type.toString() + "; NUMBER TYPE: "
						+ numberType.toString());
			}
		}
		// [boolean] x = false || true ...;
		else if (assignmentInnerExpression.getClass() == LogicalOperationNode.class) {
			boolean result = runLogicalOperation((LogicalOperationNode) assignmentInnerExpression);
			assignVariable(name, result);
		}

		// [int/float/string] a = 213 + 4213;
		else if (assignmentInnerExpression.getClass() == BinaryOperationNode.class) {
			Object resObject = runBinaryOperation((BinaryOperationNode) assignmentInnerExpression);

			double resNumber = 0;
			// System.out.println(resObject.getClass());
			if (resObject.getClass() != String.class) {
				resNumber = (double) resObject;
			} else if (resObject.getClass() == String.class && type != TokenType.TYPE_STRING) {
				System.out.println("TYPE MATCHING ERROR. CAN NOT ASSIGN STRING TO THE " + type.toString());
				return;
			} else {
				assignVariable(name, resObject);
				return;
			}

			if (type == TokenType.TYPE_FLOAT) {

				assignVariable(name, resNumber);
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
					assignVariable(name, Math.round(resNumber));
				} else {
					assignVariable(name, resNumber);
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
				assignVariable(name, value);
			} else {
				System.out.println("TYPE MATCHING ERROR. ASSIGNING " + TokenType.TYPE_BOOLEAN + " TO " + type);
			}
		}
		// String a = "text";
		else if (assignmentInnerExpression.getClass() == StringNode.class) {
			if (type == TokenType.TYPE_STRING) {
				String value = ((StringNode) assignmentInnerExpression).getToken().getValue();
				assignVariable(name, value);
			} else {
				System.out.println("TYPE MATCHING ERROR. ASSIGNING " + TokenType.TYPE_STRING + " TO " + type);
			}
		}
	}

}
