package ua.kp13.mishchenko;

public class Token {
	private final TokenType type;
	
	private final String value;

	public Token(TokenType type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public Token(TokenType type) {
		super();
		this.type = type;
		this.value = null;
	}

	public TokenType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}
	
	
	
}
