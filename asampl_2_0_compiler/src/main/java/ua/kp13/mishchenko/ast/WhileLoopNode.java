package ua.kp13.mishchenko.ast;

import java.util.ArrayList;
import java.util.List;

import ua.kp13.mishchenko.TokenType;

public class WhileLoopNode extends Node{

	private TokenType token;
	private Node runCondition;
	private List<Node> statementsList = new ArrayList<Node>();
	
	
	
	public WhileLoopNode(TokenType token, Node runCondition, List<Node> statementsList) {
		this.token = token;
		this.runCondition = runCondition;
		this.statementsList = statementsList;
	}

	
	
	public TokenType getToken() {
		return token;
	}



	public Node getRunCondition() {
		return runCondition;
	}



	public List<Node> getStatementsList() {
		return statementsList;
	}



	@Override
	public void printAST(String indent) {
		System.out.println(indent + token.toString() +  " loop: ");
		indent += " ";
        System.out.println(indent + "execution condition : ");
        runCondition.printAST(indent + " ");
        System.out.println(indent + "inner statements("+statementsList.size()+"): ");
        for(Node statement : statementsList) {
        	statement.printAST(indent + " ");
        }
        
		
	}

}
