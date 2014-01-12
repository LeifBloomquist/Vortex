package com.schemafactor.vortexserver;

/*
 * UpdaterThread.java
 *
 * Updates game state for all players.
 */

import java.util.ArrayList;
import java.util.Iterator;

import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Entity;
import com.schemafactor.vortexserver.universe.Universe;

/**
 * @author Leif Bloomquist
 */
public class UpdaterThread implements Runnable
{   
    private Universe universe = null;
    
    private double averagecount=0.0;
    private double averagetime=0.0;
    
     
    /** Creates a new instance of UpdaterThread */
    public UpdaterThread(Universe universe)
    {
        // Save references       
        this.universe = universe; 
    }                   
    
    /** Main updating thread (called from ScheduledThreadPoolExecutor in main(). */
    public void run()                       
    {
        long startTime = System.nanoTime(); 
        
        // 1. Update the universe.
        universe.update();        
        
        // 2. Update each entity                  
        for (Entity e : universe.getEntities())
        { 
            e.update(); 
        }
        
        // 3. Remove any entities that are flagged to be removed            
        Iterator<Entity> i = universe.getEntities().iterator();
        
        synchronized (universe.getEntities())  // Synchronize where entity list is modified 
        {
            while (i.hasNext()) 
            {
                Entity who = i.next(); // must be called before you can call i.remove()
              
                if (who.removeMe())
                {
                    JavaTools.printlnTime("Removing entity: " + who.getDescription() );
                    i.remove();
                }               
            }
        }
        
        long estimatedTime = System.nanoTime() - startTime;        
        JavaTools.printlnTime( "Update time [ms]: " + estimatedTime/1000000d);       
    }
}