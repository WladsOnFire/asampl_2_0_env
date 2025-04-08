package ua.kp13.mishchenko;

import java.util.List;

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
