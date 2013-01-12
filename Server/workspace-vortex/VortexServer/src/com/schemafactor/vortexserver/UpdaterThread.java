package com.schemafactor.vortexserver;

/*
 * UpdaterThread.java
 *
 * Updates game state for all players.
 */

import java.util.Iterator;
import java.util.Vector;

import com.schemafactor.vortexserver.common.Universe;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Entity;

/**
 * @author LBLOOMQU
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
    	
    	synchronized (universe) 
        {
    		universe.update();
        }    	
    	
        synchronized (allEntities) 
        {
            // Update each entity, removing if necessary        	
        	Iterator<Entity> i = allEntities.iterator();
        	
        	while (i.hasNext()) 
        	{
        		Entity who = i.next(); // must be called before you can call i.remove()
        	  
        		if (who.update(universe))
           	 	{
                    JavaTools.printlnTime("Removing entity " + who.getDescription() );
                    i.remove();
                }        	   
        	}
         }
        
        long estimatedTime = System.nanoTime() - startTime;    	
    	//JavaTools.printlnTime( "Update time [us]: " + estimatedTime/1000);
        
    }
}