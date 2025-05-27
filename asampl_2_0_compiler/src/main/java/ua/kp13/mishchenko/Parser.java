package ua.kp13.mishchenko;

import java.util.ArrayList;
import java.util.List;

import ua.kp13.mishchenko.ast.AgregateNode;
import ua.kp13.mishchenko.ast.AssignmentNode;
import ua.kp13.mishchenko.ast.BinaryOperationNode;
import ua.kp13.mishchenko.ast.BooleanNode;
import ua.kp13.mishchenko.ast.ConditionNode;
import ua.kp13.mishchenko.ast.ForLoopNode;
import ua.kp13.mishchenko.ast.FunctionCallNode;
import ua.kp13.mishchenko.ast.FunctionNode;
import ua.kp13.mishchenko.ast.InitializationNode;
import ua.kp13.mishchenko.ast.LogicalOperationNode;
import ua.kp13.mishchenko.ast.Node;
import ua.kp13.mishchenko.ast.NumberNode;
import ua.kp13.mishchenko.ast.ProgramNode;
import ua.kp13.mishchenko.ast.ReturnNode;
import ua.kp13.mishchenko.ast.StringNode;
import ua.kp13.mishchenko.ast.TimeNode;
import ua.kp13.mishchenko.ast.TupleNode;
import ua.kp13.mishchenko.ast.VariableNode;
import ua.kp13.mishchenko.ast.WhileLoopNode;
import ua.kp13.mishchenko.exceptions.LexerException;
import ua.kp13.mishchenko.exceptions.ParserException;

public class Parser {

	private final Lexer lexer;
	private Token currentToken;
	private Token lookAheadToken;
	private List<Node> statements = new ArrayList<Node>();
	private boolean parsingForExpressions = false;

	// private int curlyOpened = 0;
	// private boolean isBool = false;

	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}

	public Node parse() throws ParserException {

		// INITIALIZING CURRENT AND LOOKAHEAD TOKENS
		move();
		move();

		while (!lexer.isEndOfCode() && !lexer.getIsExceptionThrown()) {
			Node statement = null;
			
			statement = parseStatement();
			

			if (statement != null) {
				statements.add(statement);
			}
			
		}

		Node program = new ProgramNode(statements, lexer.getLine());
		return program;
	}

	private void move() throws ParserException {

		if (lexer.isEndOfCode()) {
			// LEXER[i] == PARSER[i]
			currentToken = lookAheadToken;
			lookAheadToken = null;
			return;
			//throw new ParserException("end of the tokens");
		}

		// LEXER[i+1] == PARSER[i]
		try {
		lexer.nextToken();
		} catch (LexerException e) {
			throw new ParserException("error. can not parse code. inner lexer exception: " + e.getMessage());
		}
		currentToken = lexer.getPreviousToken();
		lookAheadToken = lexer.getCurrentToken();

		//TODO CLEAR
		
		/*
		if (currentToken != null) {
			System.out.print("current token: " + currentToken.getValue().toString() + "\t");
		} else {
			System.out.print("current token: null\t");
		}
		if (lookAheadToken != null) {
			System.out.println("next token: " + lookAheadToken.getValue().toString());
		} else {
			System.out.println("next token: null");
		}*/

	}

	private Node parseStatement() throws ParserException {

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
					return new ConditionNode(TokenType.IF, expression, innerStatementsList, lexer.getLine());
				} else {
					throw new ParserException("error. expected opened curly bracket." + " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
				}
			} else {
				throw new ParserException("error. expected opened bracket." + " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
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
								// parsingInner = true;
								while (curlyOpened > 0) {
									Node statement = parseStatement();
									if (statement.getClass() == ConditionNode.class
											&& ((ConditionNode) statement).getType() != TokenType.IF) {
										if (innerStatementsList.size() != 0) {
											if (innerStatementsList.get(innerStatementsList.size() - 1)
													.getClass() == ConditionNode.class) {
												if (((ConditionNode) innerStatementsList
														.get(innerStatementsList.size() - 1)).getType() == TokenType.IF
														|| ((ConditionNode) innerStatementsList
																.get(innerStatementsList.size() - 1))
																.getType() == TokenType.ELIF) {
													innerStatementsList.add(statement);
												} else {
													System.out.println(
															"ERROR." + ((ConditionNode) statement).getType().toString()
																	+ " CLAUSE MUST BE GO AFTER IF OR ELIF CLAUSE.");
												}
											} else {
												System.out.println(
														"ERROR." + ((ConditionNode) statement).getType().toString()
																+ " CLAUSE MUST BE GO AFTER IF OR ELIF CLAUSE.");
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
								return new ConditionNode(TokenType.ELIF, expression, innerStatementsList, lexer.getLine());
							} else {
								throw new ParserException("error. expected opened curly bracket."+ " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
							}
						} else {
							throw new ParserException("error. expected opened bracket."+ " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
						}
					} else {
						throw new ParserException("error. elif clause must go after if or elif clause." + " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
					}
				} else {
					throw new ParserException("error. elif clause must go after if or elif clause." + " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
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
							// parsingInner = true;
							while (curlyOpened > 0) {
								Node statement = parseStatement();
								if (statement.getClass() == ConditionNode.class
										&& ((ConditionNode) statement).getType() != TokenType.IF) {
									if (innerStatementsList.size() != 0) {
										if (innerStatementsList.get(innerStatementsList.size() - 1)
												.getClass() == ConditionNode.class) {
											if (((ConditionNode) innerStatementsList
													.get(innerStatementsList.size() - 1)).getType() == TokenType.IF
													|| ((ConditionNode) innerStatementsList
															.get(innerStatementsList.size() - 1))
															.getType() == TokenType.ELIF) {
												innerStatementsList.add(statement);
											} else {
												throw new ParserException(
														"error." + ((ConditionNode) statement).getType().toString()
																+ " clause must be go after if or elif clause."+ " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
											}
										} else {
											throw new ParserException("error." + ((ConditionNode) statement).getType().toString()
															+ " clause must be go after if or elif clause."+ " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
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
							return new ConditionNode(TokenType.ELSE, null, innerStatementsList, lexer.getLine());
						} else {
							throw new ParserException("error. expected opened curly bracket." + " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
						}
					} else {
						throw new ParserException("error. else clause must go after if or elif clause." + " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
					}
				} else {
					throw new ParserException("error. else clause must go after if or elif clause." + " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
				}
			}
		}

		// FOR LOOP
		if (currentToken.getType() == TokenType.FOR) {
			if (lookAheadToken.getType() == TokenType.BRACKET_OPENED) {
				move(); // skip for
				move(); // skip "("
				parsingForExpressions = true;
				Node counter = parseStatement();
				Node expression = parseExpression();
				parsingForExpressions = false;
				Node step = parseExpression();

				List<Node> innerStatementsList = new ArrayList<Node>();
				move();
				move(); // skip ";)"
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
					return new ForLoopNode(TokenType.FOR, expression, step, counter, innerStatementsList, lexer.getLine());
				} else {
					throw new ParserException("error. expected opened curly bracket." + " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
				}
			} else {
				throw new ParserException("error. expected opened bracket."+ " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
			}
		}

		
		
		// WHILE LOOP
		if (currentToken.getType() == TokenType.WHILE) {
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
					return new WhileLoopNode(TokenType.WHILE, expression, innerStatementsList, lexer.getLine());
				} else {
					throw new ParserException("error. expected opened curly bracket." + " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
				}
			} else {
				throw new ParserException("error. expected opened bracket." + " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
			}
		}

		if (currentToken.getType() == TokenType.VARIABLE) {
			String varName = currentToken.getValue().toString();
			VariableNode funcName = new VariableNode(currentToken, lexer.getLine());

			// FUNCTION CALL
			List<Node> arguments = new ArrayList<Node>();
			if (lookAheadToken.getType() == TokenType.BRACKET_OPENED) {
				move();
				move();

				while (currentToken.getType() != TokenType.BRACKET_CLOSED) {

					Node arg = parseExpression();

					if (lookAheadToken.getType() == TokenType.COMMA) {
						move();
					}
					arguments.add(arg);
				}
				return new FunctionCallNode(funcName, arguments, lexer.getLine());
			}
			
			// ASSIGNMENT v = ... ;
			if (lookAheadToken.getType() == TokenType.OPER_EQUALS) {
				move();
				move(); // SKIP "="
				Node expression = parseStatement();
				return new AssignmentNode(varName, expression, lexer.getLine());
			}
		}

		// RETURN
		if (currentToken.getType() == TokenType.RETURN) {
			move();
			if (currentToken.getType() == TokenType.NEXT_LINE) {
				return new ReturnNode(null, lexer.getLine());
			} else {
				Node expression = parseStatement();
				return new ReturnNode(expression, lexer.getLine());
			}
		}

		// INITIALIZATION [INT||FLOAT||STRING] v = ... || [INT||FLOAT||STRING] v;
		if (currentToken.getType() == TokenType.TYPE_INT || currentToken.getType() == TokenType.TYPE_STRING
				|| currentToken.getType() == TokenType.TYPE_BOOLEAN || currentToken.getType() == TokenType.TYPE_FLOAT
				|| currentToken.getType() == TokenType.VOID
				|| currentToken.getType() == TokenType.TYPE_TIME || currentToken.getType() == TokenType.TYPE_AGREGATE) {

			Token typeToken = currentToken;
			
			
			//AGREGATE INITIALIZATION
			if(typeToken.getType() == TokenType.TYPE_AGREGATE) {
				if(lookAheadToken.getType() == TokenType.VARIABLE) {
					Token variable = lookAheadToken;
					move();
					
					List<Node> args = new ArrayList<Node>();
					
					if(lookAheadToken.getType() == TokenType.OPER_EQUALS) {
							move();
						if(lookAheadToken.getType() == TokenType.BRACKET_SQUARE_OPENED) {
							move();
							move();
							// on args now;
							while (currentToken.getType() != TokenType.BRACKET_SQUARE_CLOSED) {
								Node arg = parseExpression();
								
								if (lookAheadToken.getType() == TokenType.COMMA) {
									move();
								}
								if(arg.getClass() == VariableNode.class) {
									args.add(arg);
								}
							}
						}
					}
					move();
					Node node = new AgregateNode(typeToken.getType(), args, lexer.getLine());
					return new InitializationNode(variable.getValue(), node, typeToken, lexer.getLine());
				}
			}
			
			
			
			//TUPLE INITIALIZATION
			if (lookAheadToken.getType() == TokenType.TYPE_TUPLE) {
				move();
				if(lookAheadToken.getType() == TokenType.VARIABLE) {
					Token variable = lookAheadToken;
					Token tupleToken = currentToken;
					
					List<Node> args = new ArrayList<Node>();
					Node node = new TupleNode(typeToken.getType(),args, lexer.getLine());
					
					move();
					
					if(lookAheadToken.getType() == TokenType.OPER_EQUALS) {
						move();
						
						if(lookAheadToken.getType() == TokenType.BRACKET_SQUARE_OPENED) {
							move();
							move();
							// on args now;
							while (currentToken.getType() != TokenType.BRACKET_SQUARE_CLOSED) {
								Node arg = parseExpression();

								if (lookAheadToken.getType() == TokenType.COMMA) {
									move();
								}
								
								args.add(arg);

							}
						}
					}
					move();
					return new InitializationNode(variable.getValue(), node, tupleToken, lexer.getLine());
				}
			}
			
			
			// VARIABLE INITIALIZATION
			if (lookAheadToken.getType() == TokenType.VARIABLE) {
				Token varTypeToken = currentToken;
				return new InitializationNode(lookAheadToken.getValue(), parseExpression(), varTypeToken, lexer.getLine());

			}

			// FUNCTION INITIALIZATION
			if (lookAheadToken.getType() == TokenType.FUNCTION) {
				Token returnType = currentToken;
				move();
				if (lookAheadToken.getType() == TokenType.VARIABLE) {
					move();
					VariableNode funcName = new VariableNode(currentToken, lexer.getLine());
					List<VariableEntry> args = new ArrayList<VariableEntry>();
					
					if (lookAheadToken.getType() == TokenType.BRACKET_OPENED) {
						move();
						move();
						// on args now;
						while (currentToken.getType() != TokenType.BRACKET_CLOSED) {
							Node arg = parseStatement();

							if (lookAheadToken.getType() == TokenType.COMMA) {
								move();
							}

							if (arg.getClass() == InitializationNode.class) {
								args.add(new VariableEntry(((InitializationNode) arg).getVariableType().getType(),
										((InitializationNode) arg).getVariableName(), null));
							} else {
								throw new ParserException("error. invalid function arguments declaration."+ " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
							}
						}
					}
					move();

					int curlyOpened = 0;
					List<Node> innerStatementsList = new ArrayList<Node>();
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
					}
					return new FunctionNode(funcName, returnType, args, innerStatementsList, lexer.getLine());

				}
			}
		}

		// EXPRESSION

		return parseExpression();
	}

	private Node parseExpression() throws ParserException {

		Node left = parsePriorOper();

		while (currentToken != null
				&& (currentToken.getType() == TokenType.OPER_PLUS || currentToken.getType() == TokenType.OPER_MINUS)) {

			Token operator = currentToken;

			move();

			Node right = parsePriorOper();

			left = new BinaryOperationNode(left, operator, right, lexer.getLine());
		}

		while (currentToken != null
				&& (currentToken.getType() == TokenType.OPER_AND || currentToken.getType() == TokenType.OPER_OR)) {
			Token operator = currentToken;

			move();

			Node right = parsePriorOper();

			left = new LogicalOperationNode(left, operator, right, lexer.getLine());
		}
		// }

		return left;
	}

	private Node parsePriorOper() throws ParserException {
		Node left = parseArgs();

		while (currentToken != null && (currentToken.getType() == TokenType.OPER_MULTIPLY
				|| currentToken.getType() == TokenType.OPER_DIVISION
				|| currentToken.getType() == TokenType.OPER_POWER)) {

			Token operator = currentToken;
			move();

			Node right = parseArgs();
			left = new BinaryOperationNode(left, operator, right, lexer.getLine());
		}

		while (currentToken != null && (currentToken.getType() == TokenType.OPER_IS
				|| currentToken.getType() == TokenType.OPER_MORE || currentToken.getType() == TokenType.OPER_LESS
				|| currentToken.getType() == TokenType.OPER_IS_NOT
				|| currentToken.getType() == TokenType.OPER_MORE_EQUALS
				|| currentToken.getType() == TokenType.OPER_LESS_EQUALS)) {

			Token operator = currentToken;
			move();

			Node right = parseArgs();

			left = new LogicalOperationNode(left, operator, right, lexer.getLine());
		}
		return left;
	}

	private Node parseArgs() throws ParserException {
		Token token = currentToken;

		if (token.getType() == TokenType.STRING) {
			move();
			return new StringNode(token, lexer.getLine());
		} else if (token.getType() == TokenType.NUMBER) {
			move();
			return new NumberNode(token, lexer.getLine());
		} else if (token.getType() == TokenType.TIME_VALUE) {
			move();
			return new TimeNode(token, lexer.getLine());
		} else if (token.getType() == TokenType.FALSE || token.getType() == TokenType.TRUE) {
			move();
			return new BooleanNode(token, lexer.getLine());
		} else if (token.getType() == TokenType.OPER_NOT) {
			move();
			Node expression = parsePriorOper();
			return new LogicalOperationNode(expression, token, null, lexer.getLine());
		} else if (token.getType() == TokenType.VARIABLE) {
			move();
			return new VariableNode(token, lexer.getLine());
		} else if (token.getType() == TokenType.BRACKET_OPENED) {
			move();
			Node expression = parseExpression();
			if (currentToken.getType() != TokenType.BRACKET_CLOSED && !parsingForExpressions) {
				throw new ParserException("Expected closing bracket."+ " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
			}
			move();
			return expression;
		} else if (token.getType() == TokenType.NEXT_LINE || token.getType() == TokenType.COMMA
				|| lookAheadToken != null) {
			
			move();
			if (lookAheadToken != null) {
				return parseStatement();
			} else {
				return null;
			}
			
		} else {
			throw new ParserException("Unexpected token: " + token.getValue() + "." + " line: " + lexer.getLine() + " , pos: " + lexer.getPos());
		}
	}
}
