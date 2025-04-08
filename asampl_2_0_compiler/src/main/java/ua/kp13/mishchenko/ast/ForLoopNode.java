package ua.kp13.mishchenko.ast;

import java.util.ArrayList;
import java.util.List;

import ua.kp13.mishchenko.TokenType;

public class ForLoopNode extends Node{

	private TokenType token;
	private Node runCondition;
	private Node step;
	private Node counter;
	private List<Node> statementsList = new ArrayList<Node>();
	
	
	
	public ForLoopNode(TokenType token, Node runCondition, Node step,
			Node counter, List<Node> statementsList) {
		this.token = token;
		this.runCondition = runCondition;
		this.step = step;
		this.counter = counter;
		this.statementsList = statementsList;
	}

	
	
	public TokenType getToken() {
		return token;
	}



	public Node getRunCondition() {
		return runCondition;
	}



	public Node getStep() {
		return step;
	}



	public Node getCounter() {
		return counter;
	}



	public List<Node> getStatementsList() {
		return statementsList;
	}



	@Override
	public void printAST(String indent) {
		System.out.println(indent + token.toString() +  " loop: ");
		indent += " ";
        System.out.println(indent + "counter : ");
        counter.printAST(indent+ " ");
        System.out.println(indent + "execution condition : ");
        runCondition.printAST(indent+ " ");
        System.out.print(indent + "step : ");
        step.printAST(indent+ " ");
        System.out.println(indent + "inner statements: ");
        for(Node statement : statementsList) {
        	statement.printAST(indent+ " ");
        }
        
		
	}

}
