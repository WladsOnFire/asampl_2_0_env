package ua.kp13.mishchenko;

import java.util.Arrays;

public class Lexer {

	private final String code;
	private final int codeLength;

	private int currentIndex;

	private Token currentToken;
	private Token previousToken;

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

	public boolean nextToken() throws Exception {


		while (!isEndOfCode()) {

			previousToken = currentToken;

			char currentChar = code.charAt(currentIndex);

			if (Arrays.asList(' ', '\r', '\t', '\n').contains(currentChar)) {
				skipWhiteSpace();
				continue;
			}

			else if (currentChar == ';') {
				currentToken = new Token(TokenType.NEXT_LINE, ";");
				currentIndex++;
			}

			else if (currentChar == '=') {
				if(code.charAt(currentIndex + 1) != '=') {
					currentToken = new Token(TokenType.OPER_EQUALS, "=");
					currentIndex++;
				} else { //////////BOOL
					currentToken = new Token(TokenType.OPER_IS, "==");
					currentIndex +=2;
				}
			}

			else if (currentChar == '+') {
				currentToken = new Token(TokenType.OPER_PLUS, "+");
				currentIndex++;
			}

			else if (currentChar == '-') {
				currentToken = new Token(TokenType.OPER_MINUS, "-");
				currentIndex++;
			}

			else if (currentChar == '*') {
				currentToken = new Token(TokenType.OPER_MULTIPLY, "*");
				currentIndex++;
			}

			else if (currentChar == '/') {
				currentToken = new Token(TokenType.OPER_DIVISION, "/");
				currentIndex++;
			}

			else if (currentChar == '^') {
				currentToken = new Token(TokenType.OPER_POWER, "^");
				currentIndex++;
			}

			else if (currentChar == '.') {

				currentToken = new Token(TokenType.FLOATING_POINT, ".");
				currentIndex++;
			}

			else if (currentChar == '(') {
				currentToken = new Token(TokenType.BRACKET_OPENED, "(");
				currentIndex++;
			}

			else if (currentChar == ')') {
				currentToken = new Token(TokenType.BRACKET_CLOSED, ")");
				currentIndex++;
			}
			
			else if (currentChar == ',') {
				currentToken = new Token(TokenType.COMMA, ",");
				currentIndex++;
			}

			else if (currentChar == '[') {
				currentToken = new Token(TokenType.BRACKET_SQUARE_OPENED, "[");
				currentIndex++;
				currentChar = code.charAt(currentIndex);
				if(currentChar == ']') {
					currentToken = new Token(TokenType.TYPE_TUPLE, "tuple []");
					currentIndex++;
				}
			}
			else if (currentChar == ']') {
				currentToken = new Token(TokenType.BRACKET_SQUARE_CLOSED, "]");
				currentIndex++;
			}
			
			else if (currentChar == '{') {
				currentToken = new Token(TokenType.BRACKET_CURLY_OPENED, "{");
				currentIndex++;
			}

			else if (currentChar == '}') {
				currentToken = new Token(TokenType.BRACKET_CURLY_CLOSED, "}");
				currentIndex++;
			}
			//////////BOOL
			else if (currentChar == '!') {
				if(code.charAt(currentIndex + 1) == '=') {
					currentToken = new Token(TokenType.OPER_IS_NOT, "!=");
					currentIndex +=2;
				} else {
					currentToken = new Token(TokenType.OPER_NOT, "!");
					currentIndex++;
				}
			}
			else if (currentChar == '>') {
				if(code.charAt(currentIndex + 1) == '=') {
					currentToken = new Token(TokenType.OPER_MORE_EQUALS, ">=");
					currentIndex +=2;
				} else {
					currentToken = new Token(TokenType.OPER_MORE, ">");
					currentIndex++;
				}
			}
			else if (currentChar == '<') {
				if(code.charAt(currentIndex + 1) == '=') {
					currentToken = new Token(TokenType.OPER_LESS_EQUALS, "<=");
					currentIndex +=2;
				} else {
					currentToken = new Token(TokenType.OPER_LESS, "<");
					currentIndex++;
				}
			}
			else if (currentChar == '|') {
				if(code.charAt(currentIndex + 1) == '|') {
					currentToken = new Token(TokenType.OPER_OR, "||");
					currentIndex +=2;
				} else {
					throw new Exception("Token not defined.");
				}
			}
			else if (currentChar == '&') {
				if(code.charAt(currentIndex + 1) == '&') {
					currentToken = new Token(TokenType.OPER_AND, "&&");
					currentIndex +=2;
				} else {
					throw new Exception("Token not defined.");
				}
			}
			else if (currentChar == '}') {
				currentToken = new Token(TokenType.BRACKET_CURLY_CLOSED, "}");
				currentIndex++;
			}
			////////// READ STRING TEXT
			else if (currentChar == '\"') {
				currentIndex++;
				
				boolean anotherQuotesDetected = false;
				for (int i = (currentIndex); i < code.length(); i++) {
					if (code.charAt(i) == '\"') {
						anotherQuotesDetected = true;
					}
				}

				if (!anotherQuotesDetected) {
					throw new Exception("CLOSING \" REQUIRED");
				}
				anotherQuotesDetected = false;
				StringBuilder sb = new StringBuilder();
				currentChar = code.charAt(currentIndex);
				while (currentChar != '\"' && !isEndOfCode()) {

					sb.append(currentChar);

					currentChar = code.charAt(currentIndex);

					currentIndex++;

					if (isEndOfCode())
						break;

					currentChar = code.charAt(currentIndex);
				}
				
				currentToken = new Token(TokenType.STRING, sb.toString());
				
				currentIndex++;
			}

			////////// NUMBERS, VARIABLES AND KEY-WORDS
			else if (Character.isDigit(currentChar)) {
				String number = readNumber();
				String time = "";
				currentChar = code.charAt(currentIndex);
				if(number.length() == 2 && currentChar == ':') {
					time += number;
					currentIndex++;
					currentChar = code.charAt(currentIndex);
					for(int i = 0; i<3; i++) {
						String result = readNumber();
						if(result.length() == 2) {
							time += ":" + result;
						} else {
							//TODO ERROR
							System.out.println("LEXER ERROR OCCURED WHILE PARSING TIME CONSTRUCTION");
						}
						currentIndex++;
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
				throw new Exception("Token not defined.");
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
			currentIndex++;
			if (isEndOfCode())
				break;
			previousChar = currentChar;
			currentChar = code.charAt(currentIndex);

			if (previousChar != 0 && currentChar == '.') {
				sb.append(currentChar);
				currentIndex++;
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
			currentIndex++;
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
				currentIndex++;
			} else {
				break;
			}
		}
	}

}
