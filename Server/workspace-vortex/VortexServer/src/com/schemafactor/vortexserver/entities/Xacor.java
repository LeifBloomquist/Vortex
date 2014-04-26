package com.schemafactor.vortexserver.entities;

import java.util.List;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.ServerControlled.States;

public class Xacor extends ServerControlled
{   
    int firingDelay=0;
    
    /** Creates a new instance of Server Controlled */
    public Xacor(String name, int startx, int starty, int range)
    {
       super(name, startx, starty, range);
       
       // Customize
       max_speed = 2.5;
       spriteBase = 56;
       spriteColor = Constants.COLOR_RED;
    }
    
    @Override
    public void update() 
    {
        switch (State)
        {
            case IDLE:
            {
                max_speed = 2;
                Xspeed = 0;
                Yspeed = 0;
                
                // Randomly go on patrol
                if (JavaTools.generator.nextInt(1000) <= 1)
                {
                    State = States.PATROLLING;
                    max_speed = 2.5;
                    
                    double speed = 0.5 + 0.5*JavaTools.generator.nextDouble();
                    double angle = Math.toRadians(JavaTools.generator.nextInt(360));
                    Xspeed =  max_speed * speed * Math.cos(angle); 
                    Yspeed = -max_speed * speed * Math.sin(angle);   // Negative here because our y-axis is inverted
                    break;
                } 
                
                break;
            }
               
            case PATROLLING:
            {
                max_speed = 2.5;
                
                // If we spot a hostile entity, chase it                
                for (Entity e : universe.getEntities())
                {
                    if (this == e) continue;   // Don't chase myself
                    
                    if (distanceTo(e) <= 60)   // Close by
                    {
                        target = e;
                        State = States.CHASING;  
                        break;
                    }     
                }
                
                // Randomly switch back to Idle otherwise
                if (JavaTools.generator.nextInt(10000) == 102)
                {
                    State = States.IDLE;
                    break;
                }      
                
                break;
            }
        
            case CHASING:
            {
                max_speed = 3.0;
                
                if (target == null)   // No current target
                {
                    State = States.PATROLLING;
                    break;
                }
                
                if (target.removeMe())   // Target is about to be removed
                {
                    target = null;       //  (this also releases this entity's reference to it, allowing it to be garbage collected).
                    State = States.IDLE;
                    break;
                }
                
                navigateTo(target);   
                
                if (distanceTo(target) < 50)
                {                    
                    State = States.ATTACKING;
                    break;
                }
                
                break;
            }
            
            case ATTACKING:
            {
                // Fire a torpedo!                
                if (firingDelay < 10000) firingDelay += Constants.TICK_TIME;
                
                if (firingDelay > 500)   // milliseconds 
                {   
                    double angle = Math.atan2(-Yspeed, Xspeed); // Negative here because our y-axis is inverted   
                    fireTorpedo(angle);
                    firingDelay=0; // Reset
                    
                    State = States.CHASING;   
                    break;
                }
                
                break;
            }
            
            case RETREATING:
            {
                break;
            }
        }        
       
        limitAndMove();
    }   

    // Navigation.  Seek out the target and avoid all other entities.
    private void navigateTo(Entity target)   
    {    
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
                force = -2.5;  // Note negative - this repels 
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
    }
}