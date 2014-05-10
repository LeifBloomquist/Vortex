package fi.iki.elonen;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Entity;
import com.schemafactor.vortexserver.entities.Entity.eTypes;
import com.schemafactor.vortexserver.universe.Universe;

import fi.iki.elonen.NanoHTTPD.Response.Status;


/**
 * Custom HTTP server with some debug information.
 */
public class VortexDebugServer extends NanoHTTPD 
{
    private Font C64font = null;
    private Universe universe = null;
    
    public VortexDebugServer(int port) 
    {
        super(port);
        universe = Universe.getInstance();
        
        try 
        {
            loadResources();
            this.start();
        }
        catch (IOException | FontFormatException ioe) 
        {
            JavaTools.printlnTime("Couldn't start httpd server:\n" + ioe);
            System.exit(-1);
        }
    }

    @Override public Response serve(IHTTPSession session) 
    {
        Method method = session.getMethod();
        String uri = session.getUri();        
        
        if (method != Method.GET)
        {
            return new NanoHTTPD.Response("Unsupported: " + method.toString());
        }

        String msg = "<html><head><title>Vortex Debug Server</title></head>" +                
                     "<body><h1>Vortex Debug Server</h1>\n" + 
                     "<p>Server version : " + Constants.VERSION +
                     "<p> " + getOptions() +                     
                     "<hr>";
        
        switch (uri) 
        {
            case "/":
                return new NanoHTTPD.Response("");  // Fake out the hax0rs
            
            case "/menu":
                msg += "Please choose a sub-page";
                break;
                
            case "/memory":
                msg += getMemory();
                break;
                
            case "/logs":
                msg += getLogs();
                break;
                
            case "/players":
                msg += getPlayers();
                break;
                
            case "/entities":
                msg += getEntities();
                break;
            
            case "/map.png":
                InputStream mbuffer = generateMap();        
                return new NanoHTTPD.Response(Status.OK, "image/png", mbuffer);               
                
            default:
                return new NanoHTTPD.Response("");  
        }

        msg += "</body></html>\n";
        return new NanoHTTPD.Response(msg);
    }
    
    private String getOptions()
    { 
        String msg = "<h2>Subpages:</h2>";
        msg += "<a href=\"memory\">memory+cpu</a> | " +
               "<a href=\"logs\">logs</a> | " +        
               "<a href=\"map.png\">map</a> | " +  
               "<a href=\"players\">players</a> | " +
               "<a href=\"entities\">all entities</a>";
                 
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
        
        // CPU usage stats
        msg += ("<p><p>Average update time [ms]: " + universe.avg_ms);
        msg += ("<p>Average CPU Usage [%]: " + universe.avg_cpu*100 );        		
        
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
        String msg = "<h2>List of all entities (" + universe.getEntities().size() + " total)</h2>";
        
        msg += "<table border=\"1\">" +
               "<tr><th>Entity Name</th><th>Location X</th><th>Location Y</th></tr>";
        
     // Copy array to get around the dreaded Concurrent modification exception
        Entity[] entitiesCopy = (Entity[]) universe.getEntities().toArray();
        
        for (Entity e : entitiesCopy)
        {
            msg += "<tr><td>" + e.getDescription() + "</td><td>" + e.getXpos() + "</td><td>" + e.getYpos() + "</td></tr>";
        }
        
        msg += "</table>";
        return msg;
    }
    
    private String getPlayers()
    { 
        List<Entity> allPlayers = universe.getEntities(null, eTypes.HUMAN_PLAYER);
        
        String msg = "<h2>List of all players (" + allPlayers.size() + " total)</h2>";
        
        msg += "<table border=\"1\">" +
               "<tr><th>Player Name</th><th>Location X</th><th>Location Y</th></tr>";
        
        for (Entity e : allPlayers)
        {
            msg += "<tr><td>" + e.getDescription() + "</td><td>" + e.getXpos() + "</td><td>" + e.getYpos() + "</td></tr>";
        }
        
        msg += "</table>";
        return msg;
    }
   
    
    private void loadResources() throws FontFormatException, IOException  
    {
        InputStream is = this.getClass().getResourceAsStream("/fonts/C64_User_Mono_v1.0-STYLE.ttf");
        C64font=Font.createFont(Font.TRUETYPE_FONT,is);        
    }
    
    private InputStream generateMap()
    {
        BufferedImage map_Image = new BufferedImage(100*40, 100*25, BufferedImage.TYPE_INT_RGB);        
        Graphics2D gO = map_Image.createGraphics();        
        
        // Plot the main map        
        for (int x=0; x<universe.getXsize()/Constants.PIXELSPERCELL; x++)
        {
            for (int y=0; y<universe.getYsize()/Constants.PIXELSPERCELL; y++)
            {
                map_Image.setRGB( x, y, universe.getCellColor(x, y).getRGB() );                         
            }
        }
        
        // Title block
        gO.setColor(Color.WHITE);
        gO.setFont(C64font.deriveFont(60f));
        gO.drawString("Vortex Universe Map Generated " + JavaTools.Now(), 20, 100);       
        
        // Add entities
        Color c;
        gO.setFont(C64font.deriveFont(12f));
        
        // Copy array to get around the dreaded Concurrent modification exception
        Entity[] entitiesCopy = (Entity[]) universe.getEntities().toArray();     
        
        for (Entity e : entitiesCopy)
        {
            switch (e.getColor())
            {
                case Constants.COLOR_RED:
                    c = Color.RED;                    
                    break;
                    
                case Constants.COLOR_GREEN:
                    c = Color.GREEN;                    
                    break;
                    
                case Constants.COLOR_GREY1:
                case Constants.COLOR_GREY2:
                case Constants.COLOR_GREY3:
                    c = Color.LIGHT_GRAY;                    
                    break;
                
                case Constants.COLOR_YELLOW:
                    c = Color.YELLOW;                    
                    break;
                    
                case Constants.COLOR_WHITE:
                    c = Color.WHITE;                    
                    break;
                    
                case Constants.COLOR_BLACK:
                    c = Color.DARK_GRAY;                    
                    break;
                    
                case Constants.COLOR_CYAN:
                    c = Color.CYAN;                    
                    break;
                    
                case Constants.COLOR_LIGHTBLUE:
                case Constants.COLOR_BLUE:
                    c = Color.BLUE;                    
                    break;
                    
                default:
                    c = Color.PINK;                    
                    break;                
            }
        
            // Override human players to red so easier to see on map
            if (e.getType() == eTypes.HUMAN_PLAYER)
            {
                c = Color.RED;                    
            }
        
            gO.setColor(c);
            gO.fillOval((int)e.getXcell(), (int) e.getYcell(), 10, 10);
            gO.drawString(e.getDescription(), (int) e.getXcell() + 15, (int) e.getYcell());                   
        }
        
        // Clean up graphics
        gO.dispose();        
        
        // Convert
    
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(map_Image, "png", baos);
        } catch (IOException e) {           
            e.printStackTrace();
        }
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }
}