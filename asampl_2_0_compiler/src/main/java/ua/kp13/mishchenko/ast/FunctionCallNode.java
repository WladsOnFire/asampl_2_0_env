package ua.kp13.mishchenko.ast;

import java.util.ArrayList;
import java.util.List;


public class FunctionCallNode extends Node {

	private VariableNode name;
	private List<Node> args = new ArrayList<Node>();
	private int line;
    
	public int getLine() {
		return line;
	}
	
	public FunctionCallNode(VariableNode name, List<Node> args, int line) {
		this.name = name;
		this.line = line;
		this.args = args;
	}

	public VariableNode getName() {
		return name;
	}

	public List<Node> getArgs() {
		return args;
	}

	@Override
	public void printAST(String indent) {
		System.out.println(indent + "Function call: " + name.getToken().getValue());
		for(Node argument : args) {
			argument.printAST(indent + " ");
		}
	}

}
