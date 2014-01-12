package com.schemafactor.vortexserver;

/*
 * UpdaterThread.java
 *
 * Updates game state for all players.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        try
        {
            List<Entity> toBeRemoved = new ArrayList<Entity>();
            for (Entity e : universe.getEntities())
            { 
               if (e.removeMe())
               {
                   toBeRemoved.add(e);
                   JavaTools.printlnTime("Removing entity: " + e.getDescription() );
               }
            }
            
            universe.getEntities().removeAll(toBeRemoved);
        }
        catch (Exception e)
        {               
            JavaTools.printlnTime("EXCEPTION Removing entities:" + JavaTools.getStackTrace(e) );
        }        
        
        long estimatedTime = System.nanoTime() - startTime;        
        JavaTools.printlnTime( "Update time [ms]: " + estimatedTime/1000000d);       
    }
}