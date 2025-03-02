package ua.kp13.mishchenko;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.kp13.mishchenko.ast.AssignmentNode;
import ua.kp13.mishchenko.ast.BinaryOperationNode;
import ua.kp13.mishchenko.ast.InitializationNode;
import ua.kp13.mishchenko.ast.Node;
import ua.kp13.mishchenko.ast.NumberNode;
import ua.kp13.mishchenko.ast.ProgramNode;
import ua.kp13.mishchenko.ast.StringNode;
import ua.kp13.mishchenko.ast.VariableNode;

public class Interpreter {
	private Node program;
	private Map<String, VariableEntry> variableMap;
	
	public Interpreter(Node program) {
		this.program = program;
		variableMap = new HashMap<String, VariableEntry>();
	}
	
	public void run() {
		List<Node> statements = ((ProgramNode)program).getStatements();
		for(Node statement : statements) {
			if(statement.getClass() == InitializationNode.class) {
				runInitializationOperation((InitializationNode)statement);
			}
			else if(statement.getClass() == AssignmentNode.class) {
				runAssignmentOperation((AssignmentNode)statement);
			}
			else if(statement.getClass() == BinaryOperationNode.class) {
				System.out.println(runBinaryOperation((BinaryOperationNode)statement));
				//TODO ????????
			}
		}
		printVariablesMap();
	}
	
	
	
	private void printVariablesMap() {
		for(Map.Entry<String, VariableEntry> entry : variableMap.entrySet()) {
			System.out.println(entry.getValue().toString());
		}
	}
	
	private void runInitializationOperation(InitializationNode node) {
		Node expression = node.getExpression();
		String name = node.getVariableName();
		TokenType type = node.getVariableType().getType();
		
		initVariable(type, name, null);
		if(expression == null) {
			//initVariable(type, name, null);
			return;
		}
		else if(expression.getClass() == AssignmentNode.class) {
			runAssignmentOperation((AssignmentNode)expression);
		}
		
	}
	
	private boolean initVariable(TokenType type, String name, Object value) {
		if(variableMap.containsKey(name)) {
			//TODO error
			System.out.println("Variable is already initialized");
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
	
	
	private Object runBinaryOperation(BinaryOperationNode node) {
		Node leftNode = node.getLeft();
		Node rightNode = node.getRight();
		Token operator = node.getOperator();
		boolean isConcat = false;
		
		double right = 0;
		double left = 0;
		
		String leftString = "";
		String rightString = "";
		
		
		if(leftNode.getClass() == StringNode.class) {
			leftString = ((StringNode)leftNode).getToken().getValue();
			isConcat = true;
		}
		else if(leftNode.getClass() == BinaryOperationNode.class) {
			Object leftObject = runBinaryOperation((BinaryOperationNode)leftNode);
			if(leftObject.getClass() == String.class) {
				leftString = leftObject.toString();
				isConcat = true;
			} 
			else {
				left = (double)leftObject;
			}
		}
		else if(leftNode.getClass() == NumberNode.class) {
			left = Double.valueOf(((NumberNode)leftNode).getToken().getValue());
		}
		else if(leftNode.getClass() == VariableNode.class) {
			//TODO error
			String varName = ((VariableNode)leftNode).getToken().getValue();
			
			if(variableMap.get(varName).getType() == TokenType.TYPE_STRING) {
				isConcat = true;
				leftString = variableMap.get(varName).getValue().toString();
			} else {
				try{
					left = ((Number)variableMap.get(varName).getValue()).doubleValue();
				} catch(Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		}
		
		//
		
		if(rightNode.getClass() == StringNode.class) {
			rightString = ((StringNode)rightNode).getToken().getValue();
			isConcat = true;
		}
		else if(rightNode.getClass() == BinaryOperationNode.class) {
			Object rightObject = runBinaryOperation((BinaryOperationNode)rightNode);
			if(rightObject.getClass() == String.class) {
				rightString = rightObject.toString();
				isConcat = true;
			} else {
				right = (double)rightObject;
			}
		}
		else if(rightNode.getClass() == NumberNode.class) {
			right = Double.valueOf(((NumberNode)rightNode).getToken().getValue());
		}
		else if(rightNode.getClass() == VariableNode.class) {
			
			String varName = ((VariableNode)rightNode).getToken().getValue();

			if(variableMap.get(varName).getType() == TokenType.TYPE_STRING) {
				isConcat = true;
				rightString = variableMap.get(varName).getValue().toString();
			} else {
				try{
					right = ((Number)variableMap.get(varName).getValue()).doubleValue();
				} catch(Exception ex) {
					//TODO error
					System.out.println(ex.getMessage());
				}
			}
		}
		
		TokenType opType = operator.getType();
		
		
		if(opType == TokenType.OPER_PLUS 
				&& isConcat) {
			return leftString + rightString;
		} 
//		else if(opType == TokenType.OPER_PLUS 
//				&& .getClass() == StringNode.class 
//				&& .getClass() == StringNode.class) {
//			return leftString + rightString;
//		}
		else if (opType == TokenType.OPER_PLUS) {
			return left + right;
		}
		else if (opType == TokenType.OPER_MINUS) {
			return left - right;
		}
		else if (opType == TokenType.OPER_DIVISION) {
			if((double)right == 0) {
				System.out.println("DIVISION BY ZERO ERROR");
				//TODO error
			}
			return left / right;
			
		}
		else if (opType == TokenType.OPER_MULTIPLY) {
			return left * right;
		}
		else if (opType == TokenType.OPER_POWER) {
			return Math.pow(left, right);
		}
//		TODO else if (opType = TokenType.OPER_EQUALS) {
//	
//		}
		return 0;
	}
	
	private void runAssignmentOperation(AssignmentNode node) {
		String name = node.getVariableName();
		
		if(!variableMap.containsKey(name)) {
			System.out.println("VARIABLE " + name + " WAS NEVER INITIALIZED");
			//TODO error
			return;
		}
		
		VariableEntry variable = variableMap.get(name);
		
		TokenType type = variable.getType();
		
		
		//type a = b;
		
		Node assignmentInnerExpression = node.getExpression();
		
		if(assignmentInnerExpression.getClass() == VariableNode.class) {
			
			String varName = ((VariableNode)assignmentInnerExpression).getToken().getValue();
			
			VariableEntry var = variableMap.get(varName);
			
			if(var == null) {
				System.out.println("VARIABLE " + varName + " WAS NEVER INITIALIZED");
				//TODO error
				return;
			}
			if(type == var.getType()) {
				assignVariable(name, var.getValue());
			} else {
				System.out.println("ERROR. CAN NOT ASSIGN " + var.getType() + " TO " + type);
				//TODO error
			}
		}
		
		// type a = 123.4; type a = 123;
		else if(assignmentInnerExpression.getClass() == NumberNode.class) {
			String number = ((NumberNode)assignmentInnerExpression).getToken().getValue();
			
			boolean isInt = false;
			boolean isDouble = false;
			int integerNum = 0;
			double doubleNum = 0;
			try {
				integerNum = Integer.parseInt(number);
				isInt = true;
			} catch(Exception exInt) {
				try {
					doubleNum = Double.parseDouble(number);
					isDouble = true;
				} catch(Exception exDouble) {
					System.out.println("Error: " + exDouble.getLocalizedMessage());
				}
			}
			
			TokenType numberType = null;
			
			if(isInt) {
				numberType = TokenType.TYPE_INT;
			} else if (isDouble){
				numberType = TokenType.TYPE_FLOAT;
			}
			
			if(numberType == type) {
				if(isInt) {
					assignVariable(name, integerNum);
				}
				else if(isDouble) {
					assignVariable(name, doubleNum);
				}
			} else {
				System.out.println("INITIALIZATION ERROR. VARIABLE TYPE: " + type.toString() + "; NUMBER TYPE: " + numberType.toString());
			}
		}
		
		// type a = 213 + 4213;
		else if(assignmentInnerExpression.getClass() == BinaryOperationNode.class) {
			Object resObject = runBinaryOperation((BinaryOperationNode)assignmentInnerExpression);
			

			double resNumber = 0;
			//System.out.println(resObject.getClass());
			if(resObject.getClass() != String.class) {
				resNumber = (double)resObject;
			} else if (resObject.getClass() == String.class && type != TokenType.TYPE_STRING){
				System.out.println("TYPE MATCHING ERROR. CAN NOT ASSIGN STRING TO THE " + type.toString());
				return;
			} else {
				assignVariable(name, resObject);
				return;
			}
			 
			
			if(type == TokenType.TYPE_FLOAT) {

				assignVariable(name, resNumber);
				return;
			}
			
			boolean isInt = resNumber % 1 == 0;
			TokenType resNumberType = null;
			if(isInt) {
				resNumberType = TokenType.TYPE_INT;
			} else {
				resNumberType = TokenType.TYPE_FLOAT;
			}
			
			if(resNumberType == type) {
				if(isInt) {
					assignVariable(name, Math.round(resNumber));
				} else {
					assignVariable(name, resNumber);
				}
			} else {
				System.out.println("TYPE MATCHING ERROR");
				//TODO ERROR
			}
			
			
		}
		
		//String a = "text";
		else if(assignmentInnerExpression.getClass() == StringNode.class) {
			if(type == TokenType.TYPE_STRING) {
				String value = ((StringNode)assignmentInnerExpression).getToken().getValue();
				assignVariable(name, value);
			} else {
				System.out.println("TYPE MATCHING ERROR. ASSIGNING " + TokenType.TYPE_STRING + " TO " + type);
			}
		}
	}
	
	
	
}
