package loghandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogAppender {
	
	public static void main(String ...a) throws Exception {
		if (a == null || a.length == 0)
			throw new Exception ("Invalid Input");
			
		String input = a[0];
		
		if (input.indexOf(",") < 0) {
			throw new Exception ("Invalid Input");
		}
		
		String[] inParts = input.split(",");
		Map<String,String> inputs = new HashMap<String,String>();
		
		for (String part : inParts) {
			if (part.indexOf("=")<=0) {
				throw new Exception ("Invalid Input");
			}
			
			inputs.put(part.split("=")[0], part.split("=")[1]);
		}
		
		Util.debug("LogHandlermain.main() input : " + inputs);
		handleData(inputs);
		
	}

	private static void handleData(Map<String, String> inputs) {
		LogData data = Util.getLogData(inputs);
		
		if (data != null && validate(inputs, data)) {
			handleAction(data, inputs);
			Serializer.write(inputs.get("file"), data);
		}
	}
	
	private static boolean validate(Map<String, String> inputs, LogData data) {
		if (inputs.get("token") == null || !data.getToken().equals(inputs.get("token")) || ! Util.isAlphaNum(inputs.get("token"))  ) {
			Util.showOutput("Invalid token");
			return false;
		}
		List<VisitorLogEntry> logs= data.getLog().getLogs();
		if(inputs.get("time") == null || (logs.size() >0 && logs.get(logs.size()-1).getTime() > Integer.parseInt(inputs.get("time"))))
		{
			Util.showOutput("Invalid timestamp");
			return false;
		}
		if(inputs.get("visitor")== null || !Util.isAlpha(inputs.get("visitor")))
		{
			Util.showOutput("Invalid visitor name");
			return false;
		}
		if(inputs.get("room")!= null && !Util.isNumeric(inputs.get("room")) )
		{
            Util.showOutput("Invalid room id");
            return false;
		}
		if(inputs.get("file")== null || !Util.isAlphaNum(inputs.get("file")))
		{
			Util.showOutput("Invalid log path");
			return false;
		}
		if(inputs.get("action")== null || inputs.get("type")== null)
		{
			Util.showOutput("Should specify action(L/A) , type(E,G) ");
			return false;
		}
		if( !"A".equals(inputs.get("action")) && !"L".equals(inputs.get("action")) && !"E".equals(inputs.get("type")) && !"G".equals(inputs.get("type")))
		{
			Util.showOutput("Should specify action(L/A) , type(E,G) ");
			return false;
		}
		if ("A".equals(inputs.get("action")) && inputs.get("room") != null)  //Should enter a gallery before entering a room
		{
		    
		    if (data.getVisitors().getVisitor(inputs.get("visitor")) == null 
		            && Integer.parseInt(inputs.get("room")) != -1) {
                Util.showOutput("Invalid. Should enter the gallery before entering room ");
		        return false;
		    }
		}
		
		if("A".equals(inputs.get("action")) ) //should leav a room before entering a room
		{
			AllVisitors allVisitors=data.getVisitors();
			if (allVisitors !=null) {
				Visitor visitor=allVisitors.getVisitor(inputs.get("visitor"));
				if(visitor!=null)
				{
					if(visitor.getState().getCurrentPlace().getId() !=-1)
					{
						if(!"L".equals(visitor.getState().getState() ))
						{
							Util.showOutput("Invalid. Should leave a room before entering room ");
							return false;
						}
					}
				}
			}
		}
		
		if ("L".equals(inputs.get("action")) && inputs.get("room") == null) //should leav a room before leaving gallery
		{
		    String name = inputs.get("name");
		    Visitor visitor = data.getVisitors().getVisitor(name);
		    if (visitor != null) {
		        if (visitor.getState().getCurrentPlace().getId() != -1) {
	                Util.showOutput("Invalid. Should leave a room before leaving the gallery ");
		            return false;
		        }
		    }
		}
		if("L".equals(inputs.get("action")))
		{
			
			AllVisitors allVisitors=data.getVisitors();
			if (allVisitors !=null) {
				Visitor visitor=allVisitors.getVisitor(inputs.get("visitor"));
				if(visitor!=null)
				{
					if(!"A".equals(visitor.getState().getState() ))
					{
						Util.showOutput("Invalid. Should enter a room before leaving room ");
						return false;
					}
					else
					{
						VisitorState visitorState=visitor.getState();
						int presRoomId=visitorState.getCurrentPlace().getId();
						if(presRoomId != Integer.parseInt(inputs.get("room")))
						{
							Util.showOutput("Invalid. Should enter a room before leaving room ");
							return false;
						}
					}
				}
			}
			
		}
		return true;
	}

	private static void handleAction(LogData data, Map<String, String> inputs) {
		String action = inputs.get("action");
		String name = inputs.get("visitor");
		String room = inputs.get("room");
		String type = inputs.get("type");
		String timeString = inputs.get("time");
		
		int roomId = room != null ? Integer.parseInt(room) : -1;
		int time = Integer.parseInt(timeString);

		Visitor visitor = data.getVisitors().getOrCreate(name, type);
		Room roomRef = data.getRooms().getOrCreate(roomId);
		
		if ("A".equals(action)) {
			roomRef.addVisitor(visitor);
			visitor.updateState(roomRef, VisitorState.Arrived);
		} else if ("L".equals(action)) {
			roomRef.removeVisitor(visitor);
			visitor.updateState(roomRef, VisitorState.Left);
		}
		
		data.getLog().addEntry(time, data.getRooms(), data.getVisitors());
		Util.printRooms(data.getRooms());
	}
}