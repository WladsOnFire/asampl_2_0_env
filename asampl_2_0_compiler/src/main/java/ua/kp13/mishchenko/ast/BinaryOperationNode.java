package ua.kp13.mishchenko.ast;

import ua.kp13.mishchenko.Token;

public class BinaryOperationNode extends Node {
    private final Node left;
    private final Token operator;
    private final Node right;
    private int line;
    
	public int getLine() {
		return line;
	}
    public BinaryOperationNode(Node left, Token operator, Node right, int line) {
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.line = line;
    }

    public Node getLeft() {
        return left;
    }

    public Token getOperator() {
        return operator;
    }

    public Node getRight() {
        return right;
    }
    
    @Override
    public void printAST(String indent) {
        System.out.println(indent + "BinaryOp: " + operator.getValue());
        System.out.print(indent + " left : ");
        left.printAST(indent + " ");
        System.out.print(indent + " right: ");
        right.printAST(indent + " ");
    }
}
