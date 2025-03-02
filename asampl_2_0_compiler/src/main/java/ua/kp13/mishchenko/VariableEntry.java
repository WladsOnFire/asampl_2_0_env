package ua.kp13.mishchenko;

public class VariableEntry {

	private TokenType type;
	private Object value;
	private String name;

	public VariableEntry(TokenType type, String name, Object value) {
		super();
		this.type = type;
		this.value = value;
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public TokenType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "VariableEntry [type=" + type + ", value=" + value + ", name=" + name + "]";
	}

}
