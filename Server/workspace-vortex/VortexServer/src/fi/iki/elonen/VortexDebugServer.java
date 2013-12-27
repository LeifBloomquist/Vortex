package fi.iki.elonen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.entities.Entity;


/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class VortexDebugServer extends NanoHTTPD 
{
	private Vector<Entity> allEntities = null;
	
    public VortexDebugServer(int port, Vector<Entity> allEntities) 
    {
    	super(port);
    	this.allEntities = allEntities;        
    }

    @Override public Response serve(IHTTPSession session) 
    {
        Method method = session.getMethod();
        String uri = session.getUri();
        // System.out.println(method + " '" + uri + "' ");
        
        if (method != Method.GET)
        {
        	return new NanoHTTPD.Response("Unsupported: " + method.toString());
        }

        String msg = "<html><head><title>Vortex Debug Server</title></head>" +        		
        		     "<body><h1>Vortex Debug Server</h1>\n" + 
        		     "<p>" +
        		     "Server version : " + Constants.VERSION + 
        		     "<hr>";
        
        switch (uri) 
        {
            case "/":
            	msg += getOptions();
            	break;
            	
            case "/memory":
	            msg += getMemory();
	            break;
	        	
	        case "/logs":
	            msg += getLogs();
	            break;
	            
	        case "/entities":
	        	msg += getEntities();
	            break;
	            
	        default:
	        	msg += "<b>Unknown: " + uri + "</b>";	        	
	        	break;
        }

        msg += "</body></html>\n";
        return new NanoHTTPD.Response(msg);
    }
    
    private String getOptions()
    { 
    	String msg = "<h2>Subpages:</h2>";
    	msg += "<a href=\"memory\">memory</a><br>" +
    		   "<a href=\"logs\">logs</a><br>" +
    		   "<a href=\"entities\">entities</a><br>";
    	         
		return msg;
    }    
    
    private String getMemory()
    {
    	double mb = 1024*1024;
 
    	String msg = "<h2>Memory Usage:</h2>";
           
    	//Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();
		
		msg += ("<p>##### Heap utilization statistics [MB] #####");
		
		//Print used memory
		msg += ("<p>Used Memory: " + (double)(runtime.totalMemory() - runtime.freeMemory()) / mb);
		
		//Print free memory
		msg += ("<p>Free Memory: " + (double)runtime.freeMemory() / mb);
		
		//Print total available memory
		msg += ("<p>Total Memory: " + (double)runtime.totalMemory() / mb);
		
		//Print Maximum available memory
		msg += ("<p>Max Memory: " + (double)runtime.maxMemory() / mb);
		
		return msg;
    }    
    
    private String getLogs()
    { 
    	String msg = "<h2>Server Logs:</h2>";
    	
    	try {
			String text = new String(Files.readAllBytes(Paths.get("out.txt")), StandardCharsets.UTF_8);
			text = text.replace("\n", "<br>");
			msg += text;
		} catch (IOException e) {			
			msg += "<b>Error</b>: Can't open log file : " + e.getMessage(); 
		}
    	
		return msg;
    }    
    
    private String getEntities()
    { 
    	String msg = "<h2>List of entities</h2>";
    	
    	msg += "<table border=\"1\">" +
    	       "<tr><th>Entity Name</th><th>Location X</th><th>Location Y</th></tr>";
    		
    	for (Entity e : allEntities)
    	{
    		msg += "<tr><td>" + e.getDescription() + "</td><td>" + e.getXpos() + "</td><td>" + e.getYpos() + "</td></tr>";
    	}
    	
    	msg += "</table>";
		return msg;
    }        
}