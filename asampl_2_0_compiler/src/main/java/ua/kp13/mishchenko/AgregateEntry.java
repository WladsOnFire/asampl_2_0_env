package ua.kp13.mishchenko;

import java.util.List;
import java.util.Objects;

public class AgregateEntry {
	private List<TupleEntry> values;
	private String name;

	public AgregateEntry(String name, List<TupleEntry> value) {
		this.values = value;
		this.name = name;
	}

	public List<TupleEntry> getValues() {
		return values;
	}

	public void setValue(List<TupleEntry> values) {
		this.values = values;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, values);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgregateEntry other = (AgregateEntry) obj;
		return Objects.equals(name, other.name) && Objects.equals(values, other.values);
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
