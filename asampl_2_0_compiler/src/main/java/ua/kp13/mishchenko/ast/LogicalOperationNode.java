package ua.kp13.mishchenko.ast;

import ua.kp13.mishchenko.Token;

public class LogicalOperationNode extends Node {
    private final Node left;
    private final Token operator;
    private final Node right;

    public LogicalOperationNode(Node left, Token operator, Node right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
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
        System.out.println(indent + "LogicalOp: " + operator.getValue());
        System.out.print(indent + "left : ");
        left.printAST(indent + "  ");
        if(right != null) {
            System.out.print(indent + "right: ");
            right.printAST(indent + "  ");
        }
    }
}
