package ua.kp13.mishchenko.ast;

import ua.kp13.mishchenko.Token;

public class BooleanNode extends Node {

	private final Token token;

    public BooleanNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
    
    @Override
    public void printAST(String indent) {
        System.out.println(indent + "Boolean value: " + token.getValue());
    }

}
