package ua.kp13.mishchenko;

import java.util.Objects;

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
	public int hashCode() {
		return Objects.hash(name, type, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableEntry other = (VariableEntry) obj;
		return Objects.equals(name, other.name) && type == other.type && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "VariableEntry [type=" + type + ", value=" + value + ", name=" + name + "]";
	}

}
