package ua.kp13.mishchenko.ast;

import ua.kp13.mishchenko.Token;

public class InitializationNode extends Node {
	private final String variableName;
	private final Token variableType;
	private final Node expression;

	public InitializationNode(String variableName, Node expression, Token variableType) {
		this.variableName = variableName;

		if (expression.getClass() == VariableNode.class) {
			this.expression = null;
		} else {
			this.expression = expression;
		}

		this.variableType = variableType;
	}

	public String getVariableName() {
		return variableName;
	}

	public Token getVariableType() {
		return variableType;
	}

	public Node getExpression() {
		return expression;
	}

	@Override
	public void printAST(String indent) {
		System.out.println(indent + "Initialization: " + variableType.getValue() + " " + variableName);
		if (expression == null) {
			indent = indent + "  ";
			System.out.println(indent + "Value: null");
		} else {
			expression.printAST(indent + "  ");
		}
	}
}
