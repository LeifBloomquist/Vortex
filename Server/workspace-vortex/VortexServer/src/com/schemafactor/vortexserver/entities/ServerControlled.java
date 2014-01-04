package com.schemafactor.vortexserver.entities;

import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;

public class ServerControlled extends Entity
{  
    // The entity this alien is currently chasing    
    Entity target = null;
       
    /** Creates a new instance of Server Controlled */
    public ServerControlled(Universe universe, Vector<Entity> allEntities)
    {
       super("Server Controlled Alien",  Entity.eTypes.SERVER_CONTROLLED, 4700+JavaTools.generator.nextInt(1000), 4700+JavaTools.generator.nextInt(1000), universe,  allEntities);
       
       Xspeed = 0;
       Yspeed = 0;
    }
       
    /** Return Color */
    public byte getColor()
    {
       return Constants.COLOR_GREEN;
    }
    
    private void findNewTarget()
    {
        target = null;
        
        for (Entity e : allEntities)
        {
            if (this == e) continue;  // Don't worry about myself
            
            if ((e.getType() == Entity.eTypes.HUMAN_PLAYER) && !(e.removeMe()))
            {
                target = e;
            }
        }        
    }

    @Override
    public void update() 
    {   
        if (target == null)   // No current target
        {
            findNewTarget();
        }
        
     /* bad code causing deadlock, why?  
      * if (target.removeMe())  // Target is about to be removed (this also releases this entity's reference to it, allowing it to be garbage collected).
        {
            findNewTarget();
        }
        */       
        
        if (target == null)    // No human players
        {
           Xspeed = 0;
           Yspeed = 0;
           //Leave sprite#
           return;
        }        
        
        // Navigation.  Seek out the target and avoid all other entities.
        
        for (Entity e : allEntities)
        {
            if (this == e) continue;   // Don't worry about myself
            
            if (distanceTo(e) >= 60)   // Don't worry about entities too far away
            {
                if (target != e)       // Except the target
                {
                    continue;
                }
            }            
                        
            double force = 0;
            
            if (target == e)
            {
                force = 3.0;    // Attracted to target
            }
            else
            {
                force = -2.5; // Note negative - this repels 
            } 
            
            // But don't get too close!
            if (distanceTo(e) < 30) 
            {
                force = -3.5;
            }
            
         
            double xdist = e.getXpos() - this.Xpos;
            double ydist = e.getYpos() - this.Ypos;    
            
            // TODO: Wrapping handling needed here
            
            double angle  =  Math.atan2(-ydist, xdist); // Negative here because our y-axis is inverted    
            double Xdelta =  force * Math.cos(angle); 
            double Ydelta = -force * Math.sin(angle);   // Negative here because our y-axis is inverted      

            Xspeed += 0.1*Xdelta;
            Yspeed += 0.1*Ydelta;              
        }

        // Limit
        if (Xspeed >  3.0) Xspeed =  3.0;
        if (Xspeed < -3.0) Xspeed = -3.0;
        if (Yspeed >  3.0) Yspeed =  3.0;
        if (Yspeed < -3.0) Yspeed = -3.0;
        
      
        // Determine overall pointing
        double rads    = Math.atan2(-Yspeed, Xspeed);   // Negative here because our y-axis is inverted    
        double degrees = 112.5-Math.toDegrees(rads);                
        if (degrees<0)   degrees+=360d;   // while
        if (degrees>360) degrees-=360d;   // while     
        spriteNum = (byte)(degrees/45);

        // Move within the universe
        move();
        return;
    }   
}