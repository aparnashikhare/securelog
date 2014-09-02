package loghandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class LogReader {
	private static final String FourSpaces = "    ";
	private static final String NewLine = "\n";
	
	public static void main(String... a) throws Exception {
		if (a == null || a.length == 0)
			throw new Exception("Invalid Input");

		String input = a[0];

		if (input.indexOf(",") < 0) {
			throw new Exception("Invalid Input");
		}

		String[] inParts = input.split(",");
		Map<String, String> inputs = new HashMap<String, String>();

		for (String part : inParts) {
			if (part.indexOf("=") <= 0) {
				throw new Exception("Invalid Input");
			}

			inputs.put(part.split("=")[0], part.split("=")[1]);
		}

		Util.debug("LogHandlermain.main() input : " + inputs);
		handleRequest(inputs);

	}

	private static void handleRequest(Map<String, String> inputs) {
		LogData data = Util.getLogData(inputs);
		if (data != null && validate(inputs, data)) {
			showResult(inputs, data);
		}
	}

	private static void showResult(Map<String, String> inputs, LogData data) {
		String show = inputs.get("show");
		String showRooms = inputs.get("showrooms");
		String showHtml = inputs.get("html");
		String name = inputs.get("name");
		String type = inputs.get("type");
		String file = inputs.get("file");
		String token = inputs.get("token");

		if (Util.isTrue(show)) {
		    
		    if (Util.isTrue(showHtml))
		        showAllData(inputs, data);
		    else 
		        showTextData(inputs, data);
		    
		} else if (Util.isTrue(showRooms)) {
			showRoomHistory(inputs, data);
		}
	}

	private static void showRoomHistory(Map<String, String> inputs, LogData data) {

		String name = inputs.get("name");
		VisitorLog log = data.getLog();
		Set<String> rooms = new LinkedHashSet<String>();
		for (VisitorLogEntry entry : log.getLogs()) {
			Visitor visitor = entry.getVisitors().getVisitor(name);
			rooms.add("" + visitor.getState().getCurrentPlace().getId());
		}

		for (String r : rooms) {
			System.out.println(r + ",");
		}
	}
	
	private static void showTextData(Map<String, String> inputs, LogData data) {
		AllVisitors visitors = data.getVisitors();
		Map<String, Visitor> employees = visitors.getEmployees();
		Map<String, Visitor> guests =  visitors.getGuests(); 
        Collection<Room> roomList = data.getRooms().getRooms();
        
		StringBuilder textOutput = new StringBuilder();
		
		int i = 0, size = employees.values().size();
		for (Visitor e : employees.values()) {
		    i++;
		    textOutput.append(e.getName());
		    textOutput.append((i < size) ? "," : "\n");
		}
		
		i = 0; size = guests.values().size();
		for(Visitor g : guests.values()) {
		    i++;
		    textOutput.append(g.getName());
            textOutput.append((i < size) ? "," : "\n");
		}
		
        for (Room room : roomList) {
            if (room.getId() == -1)
                continue;
            
            textOutput.append(room.getId() + " : ");
            
            i = 0; size = room.getVisitors().size();
            for (Visitor v : room.getVisitors()) {
                i++;
                textOutput.append(v.getName());

                if (i<size)
                    textOutput.append(",");
            }
            
            textOutput.append("\n");
        }
        
        Util.showOutput(textOutput.toString());
	}
	
	private static void showAllData(Map<String, String> inputs, LogData data) {
		AllVisitors visitors = data.getVisitors();
		Map<String, Visitor> employees = visitors.getEmployees();
		Map<String, Visitor> guests =  visitors.getGuests(); 
		boolean done = false;
		
		StringBuilder htmlOutput = new StringBuilder();
		htmlOutput.append("<html>").append(NewLine);
		htmlOutput.append("<body>").append(NewLine);
		htmlOutput.append("<table>").append(NewLine);
		htmlOutput.append("<tr>").append(NewLine);
		htmlOutput.append(FourSpaces).append("<th>Employee</th>").append(NewLine);
		htmlOutput.append(FourSpaces).append("<th>Guest</th>").append(NewLine);
		htmlOutput.append("</tr>").append(NewLine);
		
		Iterator<Visitor> eIter = employees.values().iterator();
		Iterator<Visitor> gIter = guests.values().iterator();
		
		while (!done) {
            htmlOutput.append("<tr>").append(NewLine);
            
            if (eIter.hasNext())
                htmlOutput.append(FourSpaces)
                          .append("<td>").append(eIter.next().getName())
                          .append("</td>").append(NewLine);
            if (gIter.hasNext())
                htmlOutput.append(FourSpaces)
                          .append("<td>").append(eIter.next().getName())
                          .append("</td>").append(NewLine);
            
            htmlOutput.append("</tr>").append(NewLine);
            
            done = !eIter.hasNext() && !gIter.hasNext(); 
		}
		
        htmlOutput.append("</table>").append(NewLine);
        htmlOutput.append("<table>").append(NewLine);
        htmlOutput.append("<tr>").append(NewLine);
        htmlOutput.append(FourSpaces).append("<th>Room Id</th>").append(NewLine);
        htmlOutput.append(FourSpaces).append("<th>Occupants</th>").append(NewLine);
        htmlOutput.append("</tr>").append(NewLine);

		AllRooms rooms = data.getRooms();
		Collection<Room> roomList = rooms.getRooms();

		for (Room room : roomList) {
			if (room.getId() == -1)
				continue;
			
            htmlOutput.append("<tr>").append(NewLine);
            htmlOutput.append(FourSpaces).append("<td>").append(room.getId()).append("</td>").append(NewLine);
            htmlOutput.append(FourSpaces).append("<td>");
            
            int i=0, size = room.getVisitors().size();
			for (Visitor v : room.getVisitors()) {
			    i++;
                htmlOutput.append(v.getName());

                if (i < size)
                    htmlOutput.append(",");
			}
			
            htmlOutput.append("</td>").append(NewLine);
            htmlOutput.append("</tr>").append(NewLine);
		}
        htmlOutput.append("</table> ").append(NewLine);
        htmlOutput.append("</body>").append(NewLine);
        htmlOutput.append("</html>").append(NewLine);
        Util.showOutput(htmlOutput.toString());
	}

	private static boolean validate(Map<String, String> inputs, LogData data) {
	    
	    if (!data.getToken().equals(inputs.get("token"))) {
            Util.showOutput("Invalid token");
	        System.exit(-1);
	    }
	    if (Util.isTrue(inputs.get("show")))
	    {
	    	if(Util.isTrue(inputs.get("showrooms")))
	    	{
	    		 Util.showOutput("Invalid ");
	 	        return false;
	    	}
	    }
	    if (Util.isTrue(inputs.get("showrooms")))
	    {
	    	if(Util.isTrue(inputs.get("show")))
	    	{
	    		 Util.showOutput("Invalid ");
	 	        return false;
	    	}
	    	if(inputs.get("type") !=null)
	    	{
	    		
	    		if(!"E".equals(inputs.get("type")) &&  !"G".equals(inputs.get("type")))
		    	{
		    		 Util.showOutput("Invalid ");
		 	        return false;
		    	}
	    	}
	    	else
	    	{
	    		 Util.showOutput("Invalid ");
		 	        return false;
	    	}
	    	if(inputs.get("name")== null)
	    	{
	    		 Util.showOutput("Invalid ");
		 	        return false;
	    	}
	    
	    }
	    if(inputs.get("file")==null)
	    {
	    	 Util.showOutput("Invalid ");
	 	        return false;
	    }
	    
	    
		return true;
	}
}
