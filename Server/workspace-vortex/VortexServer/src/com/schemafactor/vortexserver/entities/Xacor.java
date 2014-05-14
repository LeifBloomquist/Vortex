package com.schemafactor.vortexserver.entities;

import java.util.List;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.ServerControlled.States;

public class Xacor extends ServerControlled
{   
    int firingDelay=0;
    
    /** Creates a new instance of Server Controlled */
    public Xacor(String name, int startx, int starty)
    {
       super(name, eTypes.XACOR, startx, starty);
       
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
                max_speed = 0;
                Xspeed = 0;
                Yspeed = 0;
                
                // Randomly go on patrol
                if (JavaTools.generator.nextInt(1000) <= 1)
                {                    
                    max_speed = 2.5;
                    
                    double speed = 0.5 + 0.5*JavaTools.generator.nextDouble();
                    double angle = Math.toRadians(JavaTools.generator.nextInt(360));
                    Xspeed =  max_speed * speed * Math.cos(angle); 
                    Yspeed = -max_speed * speed * Math.sin(angle);   // Negative here because our y-axis is inverted
                    
                    State = States.PATROLLING;
                    break;
                } 
                
                break;
            }
               
            case PATROLLING:
            {
                max_speed = 2.5;
                
                // If we spot a hostile entity, chase it
                List<Entity> allInRange = universe.getEntities(this, 70);                
                
                for (Entity e : allInRange)
                {
                    // Ignore same species, asteroids, and torpedoes
                    if ((e.getType() == myType) ||
                        (e.getType() == eTypes.ASTEROID) ||
                        (e.getType() == eTypes.TORPEDO))
                    {
                        continue;
                    }                            
                        
                    // Other types are fair game.  Just pick the first one.
                    target = e;
                    State = States.CHASING;
                    break;   
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
                    target = null;       // (this also releases this entity's reference to it, allowing it to be garbage collected).
                    State = States.IDLE;
                    break;
                }
                
                if (target != null)   // Valid Target
                {                
                    navigateTo(target, 80.0, 3.0, 2.5, 30.0, 3.5);
                    
                    if (distanceTo(target) < 50)
                    {                    
                        State = States.ATTACKING;
                        break;
                    }
                }
                
                break;
            }
            
            case ATTACKING:
            {
                // Fire a torpedo!                
                if (firingDelay < 10000) firingDelay += Constants.TICK_TIME;
                
                if (firingDelay > 500)   // milliseconds 
                {
                    fireTorpedo();
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
}