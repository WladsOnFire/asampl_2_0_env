package ua.kp13.mishchenko;

import java.util.ArrayList;
import java.util.List;

import ua.kp13.mishchenko.ast.AssignmentNode;
import ua.kp13.mishchenko.ast.BinaryOperationNode;
import ua.kp13.mishchenko.ast.BooleanNode;
import ua.kp13.mishchenko.ast.ConditionNode;
import ua.kp13.mishchenko.ast.InitializationNode;
import ua.kp13.mishchenko.ast.LogicalOperationNode;
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
	
	// private int curlyOpened = 0;
	// private boolean isBool = false;

	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}

	public Node parse() throws Exception {

		// INITIALIZING CURRENT AND LOOKAHEAD TOKENS
		move();
		move();

		while (!lexer.isEndOfCode()) {
			Node statement = parseStatement();

			if (statement != null) {
				statements.add(statement);
			}
		}

		Node program = new ProgramNode(statements);
		return program;
	}

	private void move() throws Exception {

		if (lexer.isEndOfCode()) {
			// LEXER[i] == PARSER[i]
			currentToken = lookAheadToken;
			lookAheadToken = null;
			// TODO
			// throw new Exception("END OF THE TOKENS");
			return;
		}

		// LEXER[i+1] == PARSER[i]
		lexer.nextToken();
		currentToken = lexer.getPreviousToken();
		lookAheadToken = lexer.getCurrentToken();
	}

	// private Node parseInnerStatement() {

	// }
	

	private Node parseStatement() throws Exception {

		// IF CLAUSE
		if (currentToken.getType() == TokenType.IF) {
			if (lookAheadToken.getType() == TokenType.BRACKET_OPENED) {
				move();

				Node expression = parseExpression();

				List<Node> innerStatementsList = new ArrayList<Node>();
				int curlyOpened = 0;

				if (currentToken.getType() == TokenType.BRACKET_CURLY_OPENED) {
					move();
					curlyOpened += 1;
					while (curlyOpened > 0) {
						Node statement = parseStatement();
						innerStatementsList.add(statement);
						

						if (lookAheadToken.getType() == TokenType.BRACKET_CURLY_CLOSED) {
							curlyOpened -= 1;
							move();
						}
					}
					return new ConditionNode(TokenType.IF, expression, innerStatementsList);
				} else {
					// TODO ERROR
					System.out.println("ERROR. EXPECTED OPENED CURLY BRACKET");
				}
			} else {
				// TODO ERROR
				System.out.println("ERROR. EXPECTED OPENED BRACKET");
			}
		}

		if (statements.size() != 0) {
			// ELSE IF CLAUSE
			if (currentToken.getType() == TokenType.ELIF) {
				if (statements.get(statements.size() - 1).getClass() == ConditionNode.class) {
					if (((ConditionNode) statements.get(statements.size() - 1)).getType() == TokenType.IF
							|| ((ConditionNode) statements.get(statements.size() - 1)).getType() == TokenType.ELIF) {
						if (lookAheadToken.getType() == TokenType.BRACKET_OPENED) {
							move();

							Node expression = parseExpression();

							List<Node> innerStatementsList = new ArrayList<Node>();
							int curlyOpened = 0;

							if (currentToken.getType() == TokenType.BRACKET_CURLY_OPENED) {
								move();
								curlyOpened += 1;
								//parsingInner = true;
								while (curlyOpened > 0) {
									Node statement = parseStatement();
									if(statement.getClass() == ConditionNode.class && ((ConditionNode)statement).getType() != TokenType.IF) {
										if(innerStatementsList.size() != 0) {
											if(innerStatementsList.get(innerStatementsList.size()-1).getClass() == ConditionNode.class) {
												if(((ConditionNode)innerStatementsList.get(innerStatementsList.size()-1)).getType() == TokenType.IF 
														||((ConditionNode)innerStatementsList.get(innerStatementsList.size()-1)).getType() == TokenType.ELIF) {
													innerStatementsList.add(statement);
												} else {
													System.out.println("ERROR." + ((ConditionNode)statement).getType().toString() + " CLAUSE MUST BE GO AFTER IF OR ELIF CLAUSE.");
												}
											} else {
												System.out.println("ERROR." + ((ConditionNode)statement).getType().toString() + " CLAUSE MUST BE GO AFTER IF OR ELIF CLAUSE.");
											}
										}
									} else {
										innerStatementsList.add(statement);
									}

									if (lookAheadToken.getType() == TokenType.BRACKET_CURLY_CLOSED) {
										curlyOpened -= 1;
										move();
									}
								}
								return new ConditionNode(TokenType.ELIF, expression, innerStatementsList);
							} else {
								// TODO ERROR
								System.out.println("ERROR. EXPECTED OPENED CURLY BRACKET");
							}
						} else {
							// TODO ERROR
							System.out.println("ERROR. EXPECTED OPENED BRACKET");
						}
					} else {
						// TODO ERROR
						System.out.println("ERROR. ELIF CLAUSE MUST GO AFTER IF OR ELIF CLAUSE");
					}
				} else {
					// TODO ERROR
					System.out.println("ERROR. ELIF CLAUSE MUST GO AFTER IF OR ELIF CLAUSE");
				}
			}

			// ELSE CLAUSE
			if (currentToken.getType() == TokenType.ELSE) {
				if (statements.get(statements.size() - 1).getClass() == ConditionNode.class) {
					if (((ConditionNode) statements.get(statements.size() - 1)).getType() == TokenType.IF
							|| ((ConditionNode) statements.get(statements.size() - 1)).getType() == TokenType.ELIF) {
						move();
						List<Node> innerStatementsList = new ArrayList<Node>();
						int curlyOpened = 0;

						if (currentToken.getType() == TokenType.BRACKET_CURLY_OPENED) {
							move();
							curlyOpened += 1;
							//parsingInner = true;
							while (curlyOpened > 0) {
								Node statement = parseStatement();
								if(statement.getClass() == ConditionNode.class && ((ConditionNode)statement).getType() != TokenType.IF) {
									if(innerStatementsList.size() != 0) {
										if(innerStatementsList.get(innerStatementsList.size()-1).getClass() == ConditionNode.class) {
											if(((ConditionNode)innerStatementsList.get(innerStatementsList.size()-1)).getType() == TokenType.IF 
													||((ConditionNode)innerStatementsList.get(innerStatementsList.size()-1)).getType() == TokenType.ELIF) {
												innerStatementsList.add(statement);
											} else {
												System.out.println("ERROR." + ((ConditionNode)statement).getType().toString() + " CLAUSE MUST BE GO AFTER IF OR ELIF CLAUSE.");
											}
										} else {
											System.out.println("ERROR." + ((ConditionNode)statement).getType().toString() + " CLAUSE MUST BE GO AFTER IF OR ELIF CLAUSE.");
										}
									}
								} else {
									innerStatementsList.add(statement);
								}
								if (lookAheadToken.getType() == TokenType.BRACKET_CURLY_CLOSED) {
									curlyOpened -= 1;
									move();
								}
							}
							return new ConditionNode(TokenType.ELSE, null, innerStatementsList);
						} else {
							// TODO ERROR
							System.out.println("ERROR. EXPECTED OPENED CURLY BRACKET");
						}
					} else {
						// TODO ERROR
						System.out.println("ERROR. ELSE CLAUSE MUST GO AFTER IF OR ELIF CLAUSE");
					}
				} else {
					// TODO ERROR
					System.out.println("ERROR. ELSE CLAUSE MUST GO AFTER IF OR ELIF CLAUSE");
				}
			}
		}

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
		if (currentToken.getType() == TokenType.TYPE_INT || currentToken.getType() == TokenType.TYPE_STRING
				|| currentToken.getType() == TokenType.TYPE_BOOLEAN || currentToken.getType() == TokenType.TYPE_FLOAT) {

			// String varType = currentToken.getValue();

			if (lookAheadToken.getType() == TokenType.VARIABLE) {
				Token varTypeToken = currentToken;
//				move();
//				move();
				return new InitializationNode(lookAheadToken.getValue(), parseExpression(), varTypeToken);

			}
		}

		// EXPRESSION

		return parseExpression();
	}

	private Node parseExpression() throws Exception {

		Node left = parseTerm();

		// if(!isBool) {
		while (currentToken != null
				&& (currentToken.getType() == TokenType.OPER_PLUS || currentToken.getType() == TokenType.OPER_MINUS)) {

			Token operator = currentToken;

			move();

			Node right = parseTerm();

			left = new BinaryOperationNode(left, operator, right);
		}
		// } else {
		while (currentToken != null
				&& (currentToken.getType() == TokenType.OPER_AND || currentToken.getType() == TokenType.OPER_OR)) {
			Token operator = currentToken;

			move();

			Node right = parseTerm();

			left = new LogicalOperationNode(left, operator, right);
		}
		// }

		return left;
	}

	private Node parseTerm() throws Exception {
		Node left = parseRecursion();

		// if(!isBool) {
		while (currentToken != null && (currentToken.getType() == TokenType.OPER_MULTIPLY
				|| currentToken.getType() == TokenType.OPER_DIVISION
				|| currentToken.getType() == TokenType.OPER_POWER)) {

			Token operator = currentToken;
			move();

			Node right = parseRecursion();
			left = new BinaryOperationNode(left, operator, right);
		}
		// } else {
		while (currentToken != null && (currentToken.getType() == TokenType.OPER_IS
				|| currentToken.getType() == TokenType.OPER_MORE || currentToken.getType() == TokenType.OPER_LESS
				|| currentToken.getType() == TokenType.OPER_IS_NOT
				|| currentToken.getType() == TokenType.OPER_MORE_EQUALS
				|| currentToken.getType() == TokenType.OPER_LESS_EQUALS)) {

			Token operator = currentToken;
			move();

			Node right = parseRecursion();

			left = new LogicalOperationNode(left, operator, right);
		}
		// }
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
		} else if (token.getType() == TokenType.OPER_NOT) {
			move();
			Node expression = parseTerm();
			return new LogicalOperationNode(expression, token, null);
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
			if (currentToken != null) {
				return parseStatement();
			} else {
				return null;
			}
		} else {

			throw new Exception("Unexpected token: " + token.getValue());
		}
	}
}
