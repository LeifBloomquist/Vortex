package com.schemafactor.vortexserver;

import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.Universe;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Entity;
import com.schemafactor.vortexserver.network.UDPListener;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
	    JavaTools.printlnTime("-----------------------------------------------");
	    JavaTools.printlnTime("Vortex Server Version 0.001");
	        
		Universe world = new Universe(100);
		
		 // Vector of all users.
        Vector<Entity> allEntities = new Vector<Entity> ();
        
        // Start the thread that updates everything at a fixed interval
        UpdaterThread ut = new UpdaterThread(allEntities, world);
        ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(1);
        s.scheduleAtFixedRate(ut, 0, Constants.TICK_TIME, TimeUnit.MILLISECONDS );
        
        // Instantiate a UDP listener, and let it take over.
        UDPListener udp = new UDPListener( 3005, allEntities, world );
		
		//NetworkTests.networkTests();
		
	}
}
