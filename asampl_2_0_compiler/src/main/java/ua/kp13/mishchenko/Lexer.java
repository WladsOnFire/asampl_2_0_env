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

		boolean quotesDetected = false;

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
				currentToken = new Token(TokenType.OPER_EQUALS, "=");
				currentIndex++;
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

			else if (currentChar == '{') {
				currentToken = new Token(TokenType.BRACKET_CURLY_OPENED, "{");
				currentIndex++;
			}

			else if (currentChar == '}') {
				currentToken = new Token(TokenType.BRACKET_CURLY_CLOSED, "}");
				currentIndex++;
			} 
			////////// READ STRING TEXT
			else if (currentChar == '\"') {
				quotesDetected = true;
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
				quotesDetected = false;
				
			}

			////////// NUMBERS, VARIABLES AND KEY-WORDS
			else if (Character.isDigit(currentChar)) {
				currentToken = new Token(TokenType.NUMBER, readNumber());
			}

			else if (Character.isLetter(currentChar)) {
				String variableName = readVariable();

				if (variableName.equalsIgnoreCase("int")) {
					currentToken = new Token(TokenType.TYPE_INT, "int");
				} else if (variableName.equalsIgnoreCase("string")) {
					currentToken = new Token(TokenType.TYPE_STRING, "string");
				} else if (variableName.equalsIgnoreCase("float")) {
					currentToken = new Token(TokenType.TYPE_FLOAT, "float");
				} else if (variableName.equalsIgnoreCase("boolean")) {
					currentToken = new Token(TokenType.TYPE_BOOLEAN, "boolean");
				} else if (variableName.equalsIgnoreCase("true")) {
					currentToken = new Token(TokenType.TRUE, "true");
				} else if (variableName.equalsIgnoreCase("false")) {
					currentToken = new Token(TokenType.FALSE, "false");
				}
				 else {
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
