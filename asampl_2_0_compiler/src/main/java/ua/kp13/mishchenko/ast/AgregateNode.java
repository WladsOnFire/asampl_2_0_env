package ua.kp13.mishchenko.ast;

import java.util.List;

import ua.kp13.mishchenko.TokenType;

public class AgregateNode extends Node{

	private TokenType type;
	private List<Node> values;
	private int line;
	
	public AgregateNode(TokenType type, List<Node> values, int line) {
		this.type = type;
		this.values = values;
		this.line = line;
	}
	
	public int getLine() {
		return line;
	}

	public TokenType getType() {
		return type;
	}

	public List<Node> getValues() {
		return values;
	}

	@Override
	public void printAST(String indent) {
		System.out.println(indent + "Agregate: ");
		indent += " ";
		System.out.println(indent + "elements("+values.size()+"): ");
		indent += " ";
		for(Node node : values) {
			node.printAST(indent);
		}
		
	}
	
}
