package ua.kp13.mishchenko;

import java.util.Arrays;

import ua.kp13.mishchenko.exceptions.LexerException;

public class Lexer {

	private final String code;
	private final int codeLength;

	private int currentIndex;

	private Token currentToken;
	private Token previousToken;
	private boolean isExceptionThrown = false;
	private int line = 1;
	private int pos = 0;
	
	
	
	public int getLine() {
		return line;
	}

	public int getPos() {
		return pos;
	}

	public Lexer(String code) {
		this.code = code;
		this.currentIndex = 0;
		this.codeLength = code.length();
	}

	public Token getPreviousToken() {
		return previousToken;
	}

	public Token getCurrentToken() {
		return currentToken;
	}

	public boolean getIsExceptionThrown() {
		return isExceptionThrown;
	}
	
	private void moveIndex() {
		currentIndex += 1;
		pos++;
	};
	
	public boolean nextToken() throws LexerException {


		while (!isEndOfCode() && !isExceptionThrown) {

			previousToken = currentToken;

			char currentChar = code.charAt(currentIndex);

			if (Arrays.asList(' ', '\r', '\t', '\n').contains(currentChar)) {
				skipWhiteSpace();
				continue;
			}

			else if (currentChar == ';') {
				currentToken = new Token(TokenType.NEXT_LINE, ";");
				moveIndex();
			}

			else if (currentChar == '=') {
				if(code.charAt(currentIndex + 1) != '=') {
					currentToken = new Token(TokenType.OPER_EQUALS, "=");
					moveIndex();
				} else { //////////BOOL
					currentToken = new Token(TokenType.OPER_IS, "==");
					currentIndex +=2;
				}
			}

			else if (currentChar == '+') {
				currentToken = new Token(TokenType.OPER_PLUS, "+");
				moveIndex();
			}

			else if (currentChar == '-') {
				currentToken = new Token(TokenType.OPER_MINUS, "-");
				moveIndex();
			}

			else if (currentChar == '*') {
				currentToken = new Token(TokenType.OPER_MULTIPLY, "*");
				moveIndex();
			}

			else if (currentChar == '/') {
				currentToken = new Token(TokenType.OPER_DIVISION, "/");
				moveIndex();
			}

			else if (currentChar == '^') {
				currentToken = new Token(TokenType.OPER_POWER, "^");
				moveIndex();
			}

			else if (currentChar == '.') {

				currentToken = new Token(TokenType.FLOATING_POINT, ".");
				moveIndex();
			}

			else if (currentChar == '(') {
				currentToken = new Token(TokenType.BRACKET_OPENED, "(");
				moveIndex();
			}

			else if (currentChar == ')') {
				currentToken = new Token(TokenType.BRACKET_CLOSED, ")");
				moveIndex();
			}
			
			else if (currentChar == ',') {
				currentToken = new Token(TokenType.COMMA, ",");
				moveIndex();
			}

			else if (currentChar == '[') {
				currentToken = new Token(TokenType.BRACKET_SQUARE_OPENED, "[");
				moveIndex();
				currentChar = code.charAt(currentIndex);
				if(currentChar == ']') {
					currentToken = new Token(TokenType.TYPE_TUPLE, "tuple []");
					moveIndex();
				}
			}
			else if (currentChar == ']') {
				currentToken = new Token(TokenType.BRACKET_SQUARE_CLOSED, "]");
				moveIndex();
			}
			
			else if (currentChar == '{') {
				currentToken = new Token(TokenType.BRACKET_CURLY_OPENED, "{");
				moveIndex();
			}

			else if (currentChar == '}') {
				currentToken = new Token(TokenType.BRACKET_CURLY_CLOSED, "}");
				moveIndex();
			}
			//////////BOOL
			else if (currentChar == '!') {
				if(code.charAt(currentIndex + 1) == '=') {
					currentToken = new Token(TokenType.OPER_IS_NOT, "!=");
					currentIndex +=2;
				} else {
					currentToken = new Token(TokenType.OPER_NOT, "!");
					moveIndex();
				}
			}
			else if (currentChar == '>') {
				if(code.charAt(currentIndex + 1) == '=') {
					currentToken = new Token(TokenType.OPER_MORE_EQUALS, ">=");
					currentIndex +=2;
				} else {
					currentToken = new Token(TokenType.OPER_MORE, ">");
					moveIndex();
				}
			}
			else if (currentChar == '<') {
				if(code.charAt(currentIndex + 1) == '=') {
					currentToken = new Token(TokenType.OPER_LESS_EQUALS, "<=");
					currentIndex +=2;
				} else {
					currentToken = new Token(TokenType.OPER_LESS, "<");
					moveIndex();
				}
			}
			else if (currentChar == '|') {
				if(code.charAt(currentIndex + 1) == '|') {
					currentToken = new Token(TokenType.OPER_OR, "||");
					currentIndex +=2;
				} else {
					throw new LexerException("Token not defined.");
				}
			}
			else if (currentChar == '&') {
				if(code.charAt(currentIndex + 1) == '&') {
					currentToken = new Token(TokenType.OPER_AND, "&&");
					currentIndex +=2;
				} else {
					throw new LexerException("Token not defined.");
				}
			}
			else if (currentChar == '}') {
				currentToken = new Token(TokenType.BRACKET_CURLY_CLOSED, "}");
				moveIndex();
			}
			////////// READ STRING TEXT
			else if (currentChar == '\"') {
				moveIndex();
				
				boolean anotherQuotesDetected = false;
				for (int i = (currentIndex); i < code.length(); i++) {
					if (code.charAt(i) == '\"') {
						anotherQuotesDetected = true;
					}
				}

				if (!anotherQuotesDetected) {
					throw new LexerException("closing \" required");
				}
				anotherQuotesDetected = false;
				StringBuilder sb = new StringBuilder();
				currentChar = code.charAt(currentIndex);
				while (currentChar != '\"' && !isEndOfCode()) {

					sb.append(currentChar);

					currentChar = code.charAt(currentIndex);

					moveIndex();

					if (isEndOfCode())
						break;

					currentChar = code.charAt(currentIndex);
				}
				
				currentToken = new Token(TokenType.STRING, sb.toString());
				
				moveIndex();
			}

			////////// NUMBERS, VARIABLES AND KEY-WORDS
			else if (Character.isDigit(currentChar)) {
				String number = readNumber();
				String time = "";
				currentChar = code.charAt(currentIndex);
				if(number.length() == 2 && currentChar == ':') {
					time += number;
					moveIndex();
					currentChar = code.charAt(currentIndex);
					for(int i = 0; i<3; i++) {
						String result = readNumber();
						if(result.length() == 2) {
							time += ":" + result;
						} else {
							throw new LexerException("lexer error occured while parsing time construction");
						}
						moveIndex();
						currentChar = code.charAt(currentIndex);
					}
					currentToken = new Token(TokenType.TIME_VALUE, time);
				} else {
					currentToken = new Token(TokenType.NUMBER, number);
				}
			}

			else if (Character.isLetter(currentChar)) {
				String variableName = readVariable();

				if (variableName.equalsIgnoreCase("int")) {
					currentToken = new Token(TokenType.TYPE_INT, "int");
				} else if (variableName.equalsIgnoreCase("string")) {
					currentToken = new Token(TokenType.TYPE_STRING, "string");
				} else if (variableName.equalsIgnoreCase("agregate")) {
					currentToken = new Token(TokenType.TYPE_AGREGATE, "agregate");
				} else if (variableName.equalsIgnoreCase("float")) {
					currentToken = new Token(TokenType.TYPE_FLOAT, "float");
				} else if (variableName.equalsIgnoreCase("boolean")) {
					currentToken = new Token(TokenType.TYPE_BOOLEAN, "boolean");
				} else if (variableName.equalsIgnoreCase("time")) {
					currentToken = new Token(TokenType.TYPE_TIME, "time");
				} else if (variableName.equalsIgnoreCase("true")) {
					currentToken = new Token(TokenType.TRUE, "true");
				} else if (variableName.equalsIgnoreCase("false")) {
					currentToken = new Token(TokenType.FALSE, "false");
				} else if (variableName.equalsIgnoreCase("if")) {
					currentToken = new Token(TokenType.IF, "if");
				} else if (variableName.equalsIgnoreCase("elif")) {
					currentToken = new Token(TokenType.ELIF, "else if");
				} else if (variableName.equalsIgnoreCase("else")) {
					currentToken = new Token(TokenType.ELSE, "else");
				} else if (variableName.equalsIgnoreCase("while")) {
					currentToken = new Token(TokenType.WHILE, "while");
				} else if (variableName.equalsIgnoreCase("for")) {
					currentToken = new Token(TokenType.FOR, "for");
				} else if (variableName.equalsIgnoreCase("void")) {
					currentToken = new Token(TokenType.VOID, "void");
				} else if (variableName.equalsIgnoreCase("function")) {
					currentToken = new Token(TokenType.FUNCTION, "function");
				} else if (variableName.equalsIgnoreCase("return")) {
					currentToken = new Token(TokenType.RETURN, "return");
				} else {
					currentToken = new Token(TokenType.VARIABLE, variableName);
				}
			}
			else {
				isExceptionThrown = true;
				throw new LexerException("Token not defined. Got: \"" + currentChar + "\" line: " + line + " , pos: " + pos);
			}

			return true;
		}
		return false;
	}

	public boolean isEndOfCode() {
		return currentIndex >= codeLength;
	}

	private String readNumber() {
		StringBuilder sb = new StringBuilder();
		char previousChar = 0;
		char currentChar = code.charAt(currentIndex);

		while (!isEndOfCode() && Character.isDigit(currentChar)) {
			sb.append(currentChar);
			moveIndex();
			if (isEndOfCode())
				break;
			previousChar = currentChar;
			currentChar = code.charAt(currentIndex);

			if (previousChar != 0 && currentChar == '.') {
				sb.append(currentChar);
				moveIndex();
				previousChar = currentChar;
				currentChar = code.charAt(currentIndex);
			}
		}
		return sb.toString();
	}

	private String readVariable() {
		StringBuilder sb = new StringBuilder();
		char currentChar = code.charAt(currentIndex);
		while (!isEndOfCode() && (Character.isLetter(currentChar) || Character.isDigit(currentChar))) {
			sb.append(currentChar);
			moveIndex();
			if (isEndOfCode())
				break;
			currentChar = code.charAt(currentIndex);
		}
		return sb.toString();
	}

	private void skipWhiteSpace() {
		while (!isEndOfCode()) {
			if (Arrays.asList(' ', '\r', '\t', '\n').contains(code.charAt(currentIndex))) { // whitespace, carriage
																							// return,
																							// tab, new line																
				moveIndex();
				if(!isEndOfCode()) {
					if (Arrays.asList('\n').contains(code.charAt(currentIndex))) {
						line++; //FOR EXCEPTIONS
						pos = 0;
					}
				}
			} else {
				break;
			}
		}
	}

}
