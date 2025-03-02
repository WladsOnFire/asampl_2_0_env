package ua.kp13.mishchenko.ast;

import java.util.List;

public class ProgramNode extends Node {

	private List<Node> statements;
	
	public ProgramNode(List<Node> statements) {
		this.statements = statements;
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
