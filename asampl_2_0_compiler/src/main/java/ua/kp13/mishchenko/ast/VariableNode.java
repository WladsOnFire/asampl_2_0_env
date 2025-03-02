package ua.kp13.mishchenko.ast;

import ua.kp13.mishchenko.Token;

public class VariableNode extends Node {
    private final Token token;

    public VariableNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
    
    @Override
    public void printAST(String indent) {
        System.out.println(indent + "Variable: " + token.getValue());
    }
}
