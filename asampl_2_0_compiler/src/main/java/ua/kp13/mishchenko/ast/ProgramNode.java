package ua.kp13.mishchenko.ast;

import java.util.List;

public class ProgramNode extends Node {

	private List<Node> statements;
	private int line;
    
	public int getLine() {
		return line;
	}
	
	public ProgramNode(List<Node> statements, int line) {
		this.statements = statements;
		this.line = line;
	}
	
	public List<Node> getStatements() {
		return statements;
	}
	
	@Override
	public void printAST(String indent) {
		for(Node statement : statements) {
			if(statement != null) statement.printAST("");
		}
		
	}

}
