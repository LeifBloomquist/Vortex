package com.schemafactor.vortexserver;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Asteroid;
import com.schemafactor.vortexserver.entities.Entity;
import com.schemafactor.vortexserver.entities.Xacor;
import com.schemafactor.vortexserver.entities.Xeeker;
import com.schemafactor.vortexserver.entities.Xlors;
import com.schemafactor.vortexserver.network.UDPListener;
import com.schemafactor.vortexserver.universe.Universe;

import fi.iki.elonen.VortexDebugServer;
import sun.security.krb5.Config;

public class Main 
{
    /**
     * @param args
     */
    public static void main(String[] args) 
    {
        JavaTools.printlnTime("-----------------------------------------------");
        JavaTools.printlnTime("Vortex Server Version " + Constants.VERSION );
        
        JavaTools.onlyOneInstance("vortexserver");
        
        // ArrayList of all entities.
        ArrayList<Entity> allEntities = new ArrayList<Entity>();               
        
        // Create the universe.
        JavaTools.printlnTime("Creating game universe...");
        Universe.getInstance().Create(Constants.UNIVERSE_SIZE, allEntities);
        
        // Add some entities.
        JavaTools.printlnTime("Creating default entities...");
        
        // Asteroids
        for (int i=1; i<=Constants.ASTEROID_COUNT; i++)
        {
            allEntities.add(new Asteroid("Asteroid #" + i, JavaTools.generator.nextInt((int)Universe.getInstance().getXsize()),
                                                           JavaTools.generator.nextInt((int)Universe.getInstance().getYsize()) ));                    
        }      
        
        // Alien factions
        for (int i=1; i<=100; i++)
        {
            allEntities.add(new Xlors("Xlors #" + i, 2000+JavaTools.generator.nextInt(2000), 
                                                     2000+JavaTools.generator.nextInt(2000) ));            
                                                    
            allEntities.add(new Xacor("Xacor #" + i, 2000+JavaTools.generator.nextInt(2000), 
                                                     2000+JavaTools.generator.nextInt(2000) ));     
        }
       
        // Xeekers
        for (int i=1; i<=5; i++)
        {
        
	        allEntities.add(new Xeeker("Xeeker #" + i, JavaTools.generator.nextInt((int)Universe.getInstance().getXsize()),
	                                                   JavaTools.generator.nextInt((int)Universe.getInstance().getYsize()) ));  
        }
      
                 
        
        // A mini http server to show stats through a browser
        JavaTools.printlnTime("Creating debug httpd server...");
        VortexDebugServer vdbg = new VortexDebugServer(80);
                
        // Start the thread that updates everything at a fixed interval
        JavaTools.printlnTime("Creating update scheduler...");
        UpdaterThread ut = new UpdaterThread();        
        ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(1);
        s.scheduleAtFixedRate(ut, 0, Constants.TICK_TIME, TimeUnit.MILLISECONDS );      
        
        // Instantiate a UDP listener, and let it take over.
        JavaTools.printlnTime("Creating UDP listener...");
        UDPListener udp = new UDPListener();
        udp.start(3005);
    }
}
