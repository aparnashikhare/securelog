package loghandler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AllVisitors  implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final Map<String, Visitor> visitors = new HashMap<String, Visitor>();

	public void addVisitor(Visitor visitor) {
		visitors.put(visitor.getName(), visitor);
	}
	
	public Visitor getOrCreate(String name, String type) {
		if (visitors.get(name) == null) {
			visitors.put(name, new Visitor(name,type));
		}
		
		return visitors.get(name);
	}
	public Visitor getVisitor(String name) {
		return visitors.get(name);
	}

	public Map<String, Visitor> getVisitors() {
		return visitors;
	}
	
	public Map<String, Visitor> getEmployees() {
		Map<String, Visitor> employees = new HashMap<String, Visitor>();
		
		for (Visitor v : visitors.values()) {
			if (v.isEmployee()) {
				employees.put(v.getName(), v);
			}
		}
		
		return employees;
	}

	public Map<String, Visitor> getGuests() {
		Map<String, Visitor> guests = new HashMap<String, Visitor>();
		
		for (Visitor v : visitors.values()) {
			if (v.isGuest()) {
				guests.put(v.getName(), v);
			}
		}
		
		return guests;
	}
}
