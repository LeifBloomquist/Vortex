package com.schemafactor.vortexserver;

/*
 * UpdaterThread.java
 *
 * Updates game state for all players.
 */

import java.util.Iterator;
import java.util.Vector;

import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Entity;
import com.schemafactor.vortexserver.universe.Universe;

/**
 * @author Leif Bloomquist
 */
public class UpdaterThread implements Runnable
{   
    private Vector<Entity> allEntities = null;
    private Universe universe = null;
     
    /** Creates a new instance of UpdaterThread */
    public UpdaterThread(Vector<Entity> allEntities, Universe universe)
    {
        // Save references
        this.allEntities = allEntities;
        this.universe = universe;
    }                   
    
    /** Main updating thread (called from ScheduledThreadPoolExecutor in main(). */
    public void run()                       
    {         
        long startTime = System.nanoTime(); 
        
        // 1. Update the universe.
        
        synchronized (universe) 
        {
            universe.update();
        }        
        
        synchronized (allEntities) 
        {
            // 2. Update each entity
            for (Entity e : allEntities)
            {
                e.update();
            }
            
            // 3. Remove any entities that are flagged to be removed            
            Iterator<Entity> i = allEntities.iterator();
            
            while (i.hasNext()) 
            {
                Entity who = i.next(); // must be called before you can call i.remove()
              
                if (who.removeMe())
                {
                    JavaTools.printlnTime("Removing entity " + who.getDescription() );
                    i.remove();
                }               
            }
        }
        
        long estimatedTime = System.nanoTime() - startTime;        
        JavaTools.printlnTime( "Update time [ms]: " + estimatedTime/1000000d);       
    }
}