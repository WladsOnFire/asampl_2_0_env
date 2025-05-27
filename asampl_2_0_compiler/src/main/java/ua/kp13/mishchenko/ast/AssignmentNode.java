package ua.kp13.mishchenko.ast;

public class AssignmentNode extends Node{
	private final String variableName;
    private final Node expression;
    private int line;
    
	public int getLine() {
		return line;
	}
    

    public AssignmentNode(String variableName, Node expression, int line) {
        this.variableName = variableName;
        this.expression = expression;
        this.line = line;
    }
    
    

    public String getVariableName() {
		return variableName;
	}



	public Node getExpression() {
		return expression;
	}



	@Override
    public void printAST(String indent) {
        System.out.println(indent + "Assignment: " + variableName);
        expression.printAST(indent + "  "); // Отображаем выражение с отступом
    }
}
