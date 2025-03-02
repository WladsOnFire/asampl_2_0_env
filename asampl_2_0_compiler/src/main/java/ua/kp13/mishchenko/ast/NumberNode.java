package ua.kp13.mishchenko.ast;

import ua.kp13.mishchenko.Token;

public class NumberNode extends Node {
	private final Token token;

    public NumberNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
    
    @Override
    public void printAST(String indent) {
        System.out.println(indent + "Number: " + token.getValue());
    }
}
