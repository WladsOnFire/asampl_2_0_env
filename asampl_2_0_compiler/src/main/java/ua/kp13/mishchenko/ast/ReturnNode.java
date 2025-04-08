package ua.kp13.mishchenko.ast;

public class ReturnNode extends Node{
	
	private Node expression;
	
	public ReturnNode(Node expression) {
		this.expression = expression;
	}
	
	public Node getExpression() {
		return expression;
	}

	@Override
	public void printAST(String indent) {
		
		if(expression == null) {
			System.out.println(indent + "Return statement: void");
		} else {
			System.out.println(indent + "Return statement: ");
			expression.printAST(indent + " ");
		}
		
	}

}
