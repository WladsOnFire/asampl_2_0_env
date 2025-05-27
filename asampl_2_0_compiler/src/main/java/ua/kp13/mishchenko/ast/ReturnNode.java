package ua.kp13.mishchenko.ast;

public class ReturnNode extends Node{
	
	private Node expression;
	private int line;
    
	public int getLine() {
		return line;
	}
	public ReturnNode(Node expression, int line) {
		this.expression = expression;
		this.line = line;
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
