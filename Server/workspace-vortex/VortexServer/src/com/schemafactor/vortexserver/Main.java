package com.schemafactor.vortexserver;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Asteroid;
import com.schemafactor.vortexserver.entities.Entity;
import com.schemafactor.vortexserver.entities.ServerControlled;
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
        
        // ArrayList of all users.
        //ArrayList<Entity> allEntities = new ArrayList<Entity>();
        CopyOnWriteArrayList<Entity> allEntities = new CopyOnWriteArrayList<Entity>();               
        
        // Create the universe.
        JavaTools.printlnTime("Creating game universe...");
        Universe universe = new Universe(100, allEntities);
        
        // Add some entities.
        JavaTools.printlnTime("Creating default entities...");
        for (int i=1; i<=100; i++)
        {
            allEntities.add(new ServerControlled("Alien #" + i, universe));
        }
        
        for (int i=1; i<=1000; i++)
        {
            allEntities.add(new Asteroid("Asteroid #" + i, universe));
        }        
        
        // A mini http server to show stats through a browser
        JavaTools.printlnTime("Creating debug httpd server...");
        VortexDebugServer vdbg = new VortexDebugServer(80, universe);
                
        // Start the thread that updates everything at a fixed interval
        JavaTools.printlnTime("Creating update scheduler...");
        UpdaterThread ut = new UpdaterThread(universe);
        ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(1);
        s.scheduleAtFixedRate(ut, 0, Constants.TICK_TIME, TimeUnit.MILLISECONDS );      
        
        // Instantiate a UDP listener, and let it take over.
        JavaTools.printlnTime("Creating UDP Listener...");
        UDPListener udp = new UDPListener(3005, universe);        
    }
}
