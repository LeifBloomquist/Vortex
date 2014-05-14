package com.schemafactor.vortexserver.entities;

import java.util.List;

public abstract class ServerControlled extends Entity
{  
    // The entity this alien is currently chasing    
    Entity target = null;   
    
    // States
    protected enum States {IDLE, PATROLLING, CHASING, ATTACKING, RETREATING};
    protected States State = States.IDLE;
       
    /** Creates a new instance of Server Controlled */
    public ServerControlled(String name, eTypes type, int startx, int starty)
    {
       super(name, type, startx, starty);
       
       Xspeed = 0;
       Yspeed = 0;       
    }
    
    protected void limitAndMove()
    {
        // Limit
        if (Xspeed >  max_speed) Xspeed =  max_speed;
        if (Xspeed < -max_speed) Xspeed = -max_speed;
        if (Yspeed >  max_speed) Yspeed =  max_speed;
        if (Yspeed < -max_speed) Yspeed = -max_speed;        
      
        // Determine overall pointing
        double rads    = Math.atan2(-Yspeed, Xspeed);   // Negative here because our y-axis is inverted    
        double degrees = 112.5-Math.toDegrees(rads);                
        while (degrees<0)   degrees+=360d;
        while (degrees>360) degrees-=360d;       
        spriteNum = (byte)(degrees/45);

        // Move within the universe
        move();
    } 
    
    protected void navigateTo(Entity target, double visible_range, double target_attraction, double repelling, double avoidance_range, double avoiding)   
    {    
        if (target == null) return;
        
        List<Entity> allInRange = universe.getEntities(this, visible_range);
        
        // make sure target is always known.  Consider using a Set here.
        if (!allInRange.contains(target))
        {
            allInRange.add(target);
        }
        
        for (Entity e : allInRange)
        {
            double force = 0;
            
            if (target == e)
            {
                force = target_attraction;    // Attracted to target
            }
            else
            {
                force = -repelling;  // Note negative - this repels 
            } 
            
            // But don't get too close!
            if (distanceTo(e) < avoidance_range) 
            {
                force = -avoiding;
            }            
         
            double angle2 = angleTo(e); 
            double Xdelta =  force * Math.cos(angle2); 
            double Ydelta = -force * Math.sin(angle2);   // Negative here because our y-axis is inverted      

            Xspeed += 0.1*Xdelta;
            Yspeed += 0.1*Ydelta;              
        }
    }
}