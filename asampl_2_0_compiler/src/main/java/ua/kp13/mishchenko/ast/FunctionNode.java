package ua.kp13.mishchenko.ast;

import java.util.ArrayList;
import java.util.List;

import ua.kp13.mishchenko.Token;
import ua.kp13.mishchenko.VariableEntry;

public class FunctionNode extends Node{
	
	private VariableNode name;
	private Token returnType;
	private List<VariableEntry> args = new ArrayList<VariableEntry>();
	private List<Node> innerStatements = new ArrayList<Node>();
	
	public VariableNode getName() {
		return name;
	}
	public Token getReturnType() {
		return returnType;
	}
	public List<VariableEntry> getArgs() {
		return args;
	}
	public List<Node> getInnerStatements() {
		return innerStatements;
	}
	
	
	
	public FunctionNode(VariableNode name, Token returnType, List<VariableEntry> args, List<Node> innerStatements) {
		this.name = name;
		this.returnType = returnType;
		this.args = args;
		this.innerStatements = innerStatements;
	}
	@Override
	public void printAST(String indent) {
		
		
		if(returnType != null) {
			System.out.println(indent + "Function : " + name.getToken().getValue());
			indent += " ";
			System.out.println(indent + "Return type: " + returnType.getValue().toString());
			System.out.println(indent + "Arguments: ");
			for(VariableEntry entry : args) {
				System.out.println(indent + " " + entry.toString());
			}
			System.out.println(indent + "Inner statements ("+innerStatements.size()+"): ");
			for(Node statement : innerStatements) {
				statement.printAST(indent + "  ");
			}
		} else {
			System.out.println(indent + "Function : " + name.getToken().getValue());
		}
		
	}
}
