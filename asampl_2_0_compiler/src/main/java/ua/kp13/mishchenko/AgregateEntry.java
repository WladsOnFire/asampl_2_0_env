package ua.kp13.mishchenko;

import java.util.List;

public class AgregateEntry {
	private List<Object> values;
	private String name;

	public AgregateEntry(String name, List<Object> value) {
		this.values = value;
		this.name = name;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValue(List<Object> values) {
		this.values = values;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		String result = "[";
		
		for(int i = 0; i<values.size(); i++) {
			result += values.get(i).toString();
			if(i != values.size()-1) {
				result += ",";
			}
		}
		
		return result + "]";
	}
	
}
