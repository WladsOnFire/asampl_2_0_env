package ua.kp13.mishchenko;

import java.util.ArrayList;
import java.util.List;

import ua.kp13.mishchenko.ast.AssignmentNode;
import ua.kp13.mishchenko.ast.BinaryOperationNode;
import ua.kp13.mishchenko.ast.BooleanNode;
import ua.kp13.mishchenko.ast.InitializationNode;
import ua.kp13.mishchenko.ast.Node;
import ua.kp13.mishchenko.ast.NumberNode;
import ua.kp13.mishchenko.ast.ProgramNode;
import ua.kp13.mishchenko.ast.StringNode;
import ua.kp13.mishchenko.ast.VariableNode;

public class Parser {

	private final Lexer lexer;
	private Token currentToken;
	private Token lookAheadToken;
	private List<Node> statements = new ArrayList<Node>();

	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}

	public Node parse() throws Exception {

		// INITIALIZING CURRENT AND LOOKAHEAD TOKENS
		move();
		move();

		while (!lexer.isEndOfCode()) {
			Node statement = parseStatement();
			
			if(statement != null) {
				statements.add(statement);
			}
		}

		Node program = new ProgramNode(statements);
		return program;
	}

	private void move() throws Exception {
		
		if(lexer.isEndOfCode()) {
			// LEXER[i] == PARSER[i]
			
			currentToken = lookAheadToken;
			lookAheadToken = null;
			
			//throw new Exception("END OF THE TOKENS");
			//System.out.println("current: null\tnext: null");
			return;
		}
		
		// LEXER[i+1] == PARSER[i]
		lexer.nextToken();
		currentToken = lexer.getPreviousToken();
		lookAheadToken = lexer.getCurrentToken();

		/*
		if (currentToken != null) {
			System.out.println("current: " + currentToken.getValue() + "\t next: " + lookAheadToken.getValue());
		} else {
			System.out.println("current: null\tnext: " + lookAheadToken.getValue());
		}*/
	}

	private Node parseStatement() throws Exception {
		
		
		// ASSIGNMENT v = ... ;
		if (currentToken.getType() == TokenType.VARIABLE) {
			String varName = currentToken.getValue();

			if (lookAheadToken.getType() == TokenType.OPER_EQUALS) {
				move();
				move(); // SKIP "="
				Node expression = parseExpression();
				return new AssignmentNode(varName, expression);
			}
		}
		
		// INITIALIZATION [INT||FLOAT||STRING] v = ... || [INT||FLOAT||STRING] v;
		if (currentToken.getType() == TokenType.TYPE_INT || 
				currentToken.getType() == TokenType.TYPE_STRING || 
				currentToken.getType() == TokenType.TYPE_BOOLEAN|| 
				currentToken.getType() == TokenType.TYPE_FLOAT) {
			
			//String varType = currentToken.getValue();
			
			if (lookAheadToken.getType() == TokenType.VARIABLE) {
				Token varTypeToken = currentToken;
//				move();
//				move();
				statements.add(new InitializationNode(
						lookAheadToken.getValue(),
					    parseExpression(),
					    varTypeToken));
				
			}
		}
		

		// EXPRESSION
		return parseExpression();
	}

	private Node parseExpression() throws Exception {
		
		Node left = parseTerm();

		while (currentToken != null
				&& (currentToken.getType() == TokenType.OPER_PLUS || currentToken.getType() == TokenType.OPER_MINUS)) {

			Token operator = currentToken;

			move();

			Node right = parseTerm();

			left = new BinaryOperationNode(left, operator, right);
		}

		return left;
	}
	
	

	private Node parseTerm() throws Exception {
		Node left = parseRecursion();

		while (currentToken != null && (currentToken.getType() == TokenType.OPER_MULTIPLY
				|| currentToken.getType() == TokenType.OPER_DIVISION
				|| currentToken.getType() == TokenType.OPER_POWER)) {

			Token operator = currentToken;
			move();

			Node right = parseRecursion();
			left = new BinaryOperationNode(left, operator, right);
		}

		return left;
	}

	private Node parseRecursion() throws Exception {
		Token token = currentToken;

		if (token.getType() == TokenType.STRING) {
			move();
			return new StringNode(token);
		} else if (token.getType() == TokenType.NUMBER) {
			move();
			return new NumberNode(token);
		} else if (token.getType() == TokenType.FALSE || token.getType() == TokenType.TRUE) {
			move();
			return new BooleanNode(token);
		} else if (token.getType() == TokenType.VARIABLE) {
			move();
			return new VariableNode(token);
		} else if (token.getType() == TokenType.BRACKET_OPENED) {
			move();
			Node expression = parseExpression();
			if (currentToken.getType() != TokenType.BRACKET_CLOSED) {
				throw new Exception("Expected closing bracket");
			}
			move();
			return expression;
		} else if (token.getType() == TokenType.NEXT_LINE || lookAheadToken != null) {
			move();
			if( currentToken != null) {
				return parseStatement();
			} else {
				return null;
			}
		} else {

			throw new Exception("Unexpected token: " + token.getValue());
		}
	}
}
