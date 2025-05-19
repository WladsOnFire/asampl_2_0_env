package ua.kp13.mishchenko;

import java.util.List;
import java.util.Objects;

public class TupleEntry {
	
	private TokenType type;
	private List<Object> values;
	private String name;

	public TupleEntry(TokenType type, String name, List<Object> value) {
		this.type = type;
		this.values = value;
		this.name = name;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValue(List<Object> values) {
		this.values = values;
	}

	public TokenType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type, values);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TupleEntry other = (TupleEntry) obj;
		return Objects.equals(name, other.name) && type == other.type && values.equals(other.values);
	}

	@Override
	public String toString() {
		String result = type+"[";
		
		for(int i = 0; i<values.size(); i++) {
			result += values.get(i);
			if(i != values.size()-1) {
				result += ",";
			}
		}
		
		return result + "]";
	}
}
