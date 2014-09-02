package loghandler;

import java.io.Serializable;

public class VisitorLogEntry  implements Serializable {
	
	private static final long serialVersionUID = 1L;

	
	private final int time;
	private final AllRooms rooms;
	private final AllVisitors visitors;
	
	VisitorLogEntry(int time, AllRooms rooms, AllVisitors visitors) {
		this.time = time;
		this.rooms = rooms;
		this.visitors = visitors;
		
		Util.debug("created entry with " + this.rooms.hashCode() + " and " + this.visitors.hashCode());
		Util.debug("created entry with " + this.rooms);
		Util.debug("created entry with " + this.visitors);
	}
	
	public int getTime() {
		return time;
	}
	
	public AllRooms getRooms() {
		return rooms;
	}
	
	public AllVisitors getVisitors() {
		return visitors;
	}
}
