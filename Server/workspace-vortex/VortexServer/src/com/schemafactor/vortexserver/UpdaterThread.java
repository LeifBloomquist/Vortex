package com.schemafactor.vortexserver;

/*
 * UpdaterThread.java
 *
 * Updates game state for all players.
 */

import java.util.ArrayList;
import java.util.List;

import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.entities.Entity;
import com.schemafactor.vortexserver.universe.Universe;

/**
 * @author Leif Bloomquist
 */
public class UpdaterThread implements Runnable
{   
    private Universe universe = null;    
    
    JavaTools.MovingAverage sma_ms = new JavaTools.MovingAverage(100);
    JavaTools.MovingAverage sma_cpu = new JavaTools.MovingAverage(100);
     
    /** Creates a new instance of UpdaterThread */
    public UpdaterThread()
    {
        // Save references       
        universe = Universe.getInstance();        
    }                   
    
    /** Main updating thread (called from ScheduledThreadPoolExecutor in main(). */
    public void run()                       
    {
        Thread.currentThread().setName("Vortex Updater Thread");
        
        long startTime = System.nanoTime(); 
        
        // 1. Update the universe.
        try
        {
            universe.update();
        }
        catch (Exception e)
        {               
            JavaTools.printlnTime("EXCEPTION Updating Universe: " + JavaTools.getStackTrace(e) );
        }   
        
        
        // 2. Update each entity                  
        try
        {
            for (Entity e : universe.getEntities())
            {             
                e.update(); 
            } 
        }
        catch (Exception ex)
        {               
            JavaTools.printlnTime("EXCEPTION Updating Entities: " + JavaTools.getStackTrace(ex) );
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
               }
            }            
            universe.getEntities().removeAll(toBeRemoved);
        }
        catch (Exception ex)
        {               
            JavaTools.printlnTime("EXCEPTION removing entities: " + JavaTools.getStackTrace(ex) );
        }                          
        
        // 4. Add any new entities
        try
        {
            universe.newEntities.drainTo( universe.getEntities() );
        }
        catch (Exception ex)
        {               
            JavaTools.printlnTime("EXCEPTION adding new entities:" + JavaTools.getStackTrace(ex) );
        }        

        // 5. Gather some stats (read out in httpd server)
        long estimatedTime = System.nanoTime() - startTime;  
        double estimatedMilliseconds = estimatedTime/1000000d;
        sma_ms.newNum(estimatedMilliseconds);
        sma_cpu.newNum(estimatedMilliseconds / Constants.TICK_TIME);
        universe.avg_ms = sma_ms.getAvg();
        universe.avg_cpu = sma_cpu.getAvg();
    }
}