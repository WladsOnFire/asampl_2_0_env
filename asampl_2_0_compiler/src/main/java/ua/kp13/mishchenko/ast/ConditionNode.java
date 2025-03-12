package ua.kp13.mishchenko.ast;

import java.util.List;

import ua.kp13.mishchenko.TokenType;

public class ConditionNode extends Node {
	private TokenType type;
	private Node expression;
	private List<Node> innerStatements;

	public TokenType getType() {
		return type;
	}

	public Node getExpression() {
		return expression;
	}

	public List<Node> getInnerStatements() {
		return innerStatements;
	}

	public ConditionNode(TokenType type, Node expression, List<Node> innerStatements) {
		this.type = type;
		this.expression = expression;
		this.innerStatements = innerStatements;
	}

	@Override
	public void printAST(String indent) {
		System.out.println(indent + type.toString() + " clause: ");
		indent += " ";
		
		if(type != TokenType.ELSE) {

			System.out.println(indent + "expression: ");
			expression.printAST(indent + " ");
		}
		
		System.out.println(indent + "inner statements("+innerStatements.size()+"): ");
		for(Node statement : innerStatements) {
			if(statement != null) statement.printAST(indent + " ");
		}

	}

}
