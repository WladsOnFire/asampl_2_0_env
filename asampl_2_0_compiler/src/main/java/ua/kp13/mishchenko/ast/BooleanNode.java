package ua.kp13.mishchenko.ast;

import ua.kp13.mishchenko.Token;

public class BooleanNode extends Node {

	private final Token token;
	private int line;
    
	public int getLine() {
		return line;
	}
	
    public BooleanNode(Token token, int line) {
        this.token = token;
        this.line = line;
    }

    public Token getToken() {
        return token;
    }
    
    @Override
    public void printAST(String indent) {
        System.out.println(indent + "Boolean value: " + token.getValue());
    }

}
