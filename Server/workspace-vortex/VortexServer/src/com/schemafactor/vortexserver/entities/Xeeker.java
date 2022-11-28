package com.schemafactor.vortexserver.entities;

import java.util.List;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.ServerControlled.States;

public class Xeeker extends ServerControlled
{      
    /** Creates a new instance of Xeeker */
    public Xeeker(String name, int startx, int starty)
    {
       super(name, eTypes.XEEKER, startx, starty);
       
       // Customize
       max_speed = 7;
       spriteBase = 40;
       spriteColor = Constants.COLOR_RED;
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

                // Start chasing right away - Find a human player
                findNewTarget();

                if (target != null)
                {
                    State = States.CHASING;
                    break;
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
                    break;
                }                
                
                if (target != null)   // Valid Target
                {
                    navigateTo(target, 100.0, 4.0, 3.5, 30.0, 5.0);
                
                    if (distanceTo(target) < 70)
                    {
                        State = States.ATTACKING;
                        break;
                    }
                }
                
                // Randomly chase other players
                if (JavaTools.generator.nextInt(10000) == 7788)
                {
                    State = States.IDLE;
                    break;
                }
                
                break;
            }
            
            case ATTACKING:
            {
            	// Fire a torpedo!                
                if (firingDelay < 10000) firingDelay += Constants.TICK_TIME;
                
                if (firingDelay > 200)   // milliseconds 
                {
                    fireTorpedo();
                    firingDelay=0; // Reset
                    // Note no change in state!  It will fire like crazy until out of range! (see below)
                    break;   
                }
                
                if (target == null)   // No current target
                {
                    State = States.IDLE;
                    break;
                }
                
                max_speed = 3.5;                
                navigateTo(target, 60.0, 4.0, 3.5, 50.0, 4.0);
                
                if (distanceTo(target) > 100)
                {
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
    	
    	if (lastState != State)
    	{
    		// JavaTools.printlnTime(this.description + " entered state: " + State.toString() );
    	}
    	
    	lastState = State;
        
        limitAndMove();
    }   
}