package com.schemafactor.vortexserver.entities;

import java.util.List;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;

public class ServerControlled extends Entity
{  
    // The entity this alien is currently chasing    
    Entity target = null;
       
    /** Creates a new instance of Server Controlled */
    public ServerControlled(String name, Universe universe)
    {
       super(name,  Entity.eTypes.SERVER_CONTROLLED, 1000+JavaTools.generator.nextInt(15000), 1000+JavaTools.generator.nextInt(15000), universe);
       
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
        try
        {
            target = null;
            
            // Get a list of all human players. 
            List<Entity> allHumans = universe.getEntities(this, eTypes.HUMAN_PLAYER);           
            
            // Pick one
            int size=allHumans.size();           
            
            if (size>0)
            {
                int num = JavaTools.generator.nextInt(size);
                target = allHumans.get(num);  
                JavaTools.printlnTime(this.description + " chose new target: " + target.getDescription() );
            }
        }
        catch (Exception e)
        {               
            JavaTools.printlnTime( "EXCEPTION finding target: " + JavaTools.getStackTrace(e) );
        }
    }

    @Override
    public void update() 
    {
        try
        {   
            if (target == null)   // No current target
            {
                findNewTarget();
            }
            
            if (target == null) 
            {
               Xspeed = 0;
               Yspeed = 0;
               //Leave sprite#
               return;
            }        
            
            if (target.removeMe())  // Target is about to be removed (this also releases this entity's reference to it, allowing it to be garbage collected).
            {
                findNewTarget();
            }
                
            
            // Randomly change targets
            if (JavaTools.generator.nextInt(1000) == 742)
            {
                findNewTarget();
            }        
            
            
            if (target == null)    // No human players
            {
               Xspeed = 0;
               Yspeed = 0;
               //Leave sprite#
               return;
            }
        }
        catch (Exception e)
        {              
            JavaTools.printlnTime( "EXCEPTION determining target: " + JavaTools.getStackTrace(e));
        }
        
        // Navigation.  Seek out the target and avoid all other entities.
        
        for (Entity e : universe.getEntities())
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