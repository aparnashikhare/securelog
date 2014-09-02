package loghandler;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AllRooms  implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final Map<Integer, Room> rooms = new HashMap<Integer, Room>();
	
	public Room getOrCreate(int id) {
		if (rooms.get(id) == null) {
			rooms.put(id, new Room(id));
		}
		
		return rooms.get(id);
	}

	public Collection<Room> getRooms() {
		return rooms.values();
	}
}
