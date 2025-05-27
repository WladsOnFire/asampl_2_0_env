package ua.kp13.mishchenko.ast;

import java.util.List;

import ua.kp13.mishchenko.TokenType;

public class TupleNode extends Node{

	private TokenType type;
	private List<Node> args;
	private int line;
    
	public int getLine() {
		return line;
	}
	public TupleNode(TokenType type, List<Node> args, int line) {
		this.type = type;
		this.args = args;
		this.line = line;
	}

	public TokenType getType() {
		return type;
	}

	public List<Node> getArgs() {
		return args;
	}

	//@Override
	//public String toString() {
	//	String tuple = "[";
	//	for(Node args)
	//	return "TupleNode [type=" + type + ", args=" + args + "]";
	//}

	@Override
	public void printAST(String indent) {
		System.out.println(indent +type.toString() + " TUPLE");
		System.out.println(indent + "elements("+args.size()+"):");
		
		if(args.size() != 0) {
			for(int i =0; i<args.size(); i++) {
				args.get(i).printAST(" " + indent + "["  + i + "] ");
			}
		}
	}
	
}
