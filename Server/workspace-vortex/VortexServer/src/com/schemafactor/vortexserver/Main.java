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
        
        // ArrayList of all users.
        ArrayList<Entity> allEntities = new ArrayList<Entity>();               
        
        // Create the universe.
        JavaTools.printlnTime("Creating game universe...");
        Universe.getInstance().Create(100, allEntities);
        
        // Add some entities.
        JavaTools.printlnTime("Creating default entities...");
        for (int i=1; i<=50; i++)
        {
            allEntities.add(new Xlors("Xlors #" + i, 9000+JavaTools.generator.nextInt(2000), 
                                                     9000+JavaTools.generator.nextInt(2000) ));            
                                                    
            allEntities.add(new Xacor("Xacor #" + i, 9000+JavaTools.generator.nextInt(2000), 
                                                     9000+JavaTools.generator.nextInt(2000) ));     
        }
        
        allEntities.add(new Xeeker("The Xeeker", JavaTools.generator.nextInt((int)Universe.getInstance().getXsize()),
                                                 JavaTools.generator.nextInt((int)Universe.getInstance().getYsize()) ));    
        
        for (int i=1; i<=1000; i++)
        {
            allEntities.add(new Asteroid("Asteroid #" + i, JavaTools.generator.nextInt((int)Universe.getInstance().getXsize()),
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
