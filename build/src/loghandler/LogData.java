package loghandler;

import java.io.Serializable;

public class LogData  implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String token;
	private AllVisitors visitors;
	private AllRooms rooms;
	private VisitorLog log;	

	public LogData() {
		visitors = new AllVisitors();
		rooms = new AllRooms();
		log = new VisitorLog();
	}
	
	public AllVisitors getVisitors() {
		return visitors;
	}

	public void setVisitors(AllVisitors visitors) {
		this.visitors = visitors;
	}

	public AllRooms getRooms() {
		return rooms;
	}

	public void setRooms(AllRooms rooms) {
		this.rooms = rooms;
	}

	public VisitorLog getLog() {
		return log;
	}

	public void setLog(VisitorLog log) {
		this.log = log;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
