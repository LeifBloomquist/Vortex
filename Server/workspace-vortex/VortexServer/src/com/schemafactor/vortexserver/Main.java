package com.schemafactor.vortexserver;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Asteroid;
import com.schemafactor.vortexserver.entities.Entity;
import com.schemafactor.vortexserver.network.UDPListener;
import com.schemafactor.vortexserver.universe.Universe;

import fi.iki.elonen.VortexDebugServer;

public class Main 
{
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
	    JavaTools.printlnTime("-----------------------------------------------");
	    JavaTools.printlnTime("Vortex Server Version " + Constants.VERSION );
	        
	    // Create the universe.
	    JavaTools.printlnTime("Creating game universe...");
		Universe universe = new Universe(100);
		
		 // Vector of all users.
		JavaTools.printlnTime("Creating default entities...");
        Vector<Entity> allEntities = new Vector<Entity>();
        
        // Add some entities.
        for (int i=1; i<=10; i++)
        {
        	allEntities.add(new Asteroid());
        }
        
        // Testing - a mini http server to show stats through a browser
        JavaTools.printlnTime("Creating debug http server...");
        VortexDebugServer vdbg = new VortexDebugServer(8080, allEntities);
        
        try 
        {
        	vdbg.start();
        }
        catch (IOException ioe) 
        {
        	JavaTools.printlnTime("Couldn't start httpd server:\n" + ioe);
            System.exit(-1);
        }
                
        // Start the thread that updates everything at a fixed interval
        JavaTools.printlnTime("Creating update scheduler...");
        UpdaterThread ut = new UpdaterThread(allEntities, universe);
        ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(1);
        s.scheduleAtFixedRate(ut, 0, Constants.TICK_TIME, TimeUnit.MILLISECONDS );      
        
        // Instantiate a UDP listener, and let it take over.
        JavaTools.printlnTime("Creating UDP Listener...");
        UDPListener udp = new UDPListener( 3005, allEntities );		
	}
}
