package com.schemafactor.vortexserver.entities;

import java.util.List;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Entity.eTypes;
import com.schemafactor.vortexserver.universe.Universe;

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
}