package com.schemafactor.vortexserver.entities;

import java.util.List;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;

public class Xeeker extends ServerControlled
{      
    /** Creates a new instance of Server Controlled */
    public Xeeker(String name, int startx, int starty, int range)
    {
       super(name, startx, starty, range);
       
       // Customize
       max_speed = 7;
       spriteBase = 40;
       spriteColor = Constants.COLOR_BLUE;
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
        switch (State)
        {
            case IDLE:
            {
                Xspeed = 0;
                Yspeed = 0;

                // Staert chasing right away - Find a human player
                findNewTarget();

                if (target != null)
                {
                    State = States.CHASING;
                }                
                break;
            }
               
            case PATROLLING:
            case CHASING:
            {
                max_speed = 7.0;
                
                if (target == null)   // No current target
                {
                    State = States.IDLE;
                    break;
                }
                
                if (target.removeMe())   // Target is about to be removed
                {
                    target = null;       //  (this also releases this entity's reference to it, allowing it to be garbage collected).
                    State = States.IDLE;
                }
                
                navigateTo(target);     
                
                if (distanceTo(target) < 50)
                {
                    State = States.ATTACKING;
                }
                
                break;
            }
            
            case ATTACKING:
            {
                max_speed = 3.5;                
                navigateTo(target);
                
                if (distanceTo(target) > 100)
                {
                    State = States.CHASING;
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
                force = 4.0;    // Attracted to target
            }
            else
            {
                force = -3.5;  // Note negative - this repels 
            } 
            
            // But don't get too close!
            if (distanceTo(e) < 30) 
            {
                force = -4.0;
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