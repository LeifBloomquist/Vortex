package com.schemafactor.vortexserver.entities;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;

// Named by Andreas
public class Xlors extends ServerControlled
{  
    /** Creates a new instance of the Xlors */
    public Xlors(String name, int startx, int starty, int range)
    {
       super(name, startx, starty, range);
       
       // Customize
       max_speed   = 3.5;
       spriteBase  = 32;
       spriteColor = Constants.COLOR_GREEN;
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
                
                // Always go on patrol
                State = States.PATROLLING;
                double angle  =  Math.toRadians(JavaTools.generator.nextInt(360));
                Xspeed =  max_speed * Math.cos(angle); 
                Yspeed = -max_speed * Math.sin(angle);   // Negative here because our y-axis is inverted      
            }
               
            case PATROLLING:
            {               
                // Randomly switch directions.  Do this by switching back to Idle momentarily (next loop). 
                if (JavaTools.generator.nextInt(3000) == 200)
                {
                    State = States.IDLE;
                } 
                break;
            }
        
            case CHASING:
            {
                break;
            }
            
            case ATTACKING:
            {
            }
            
            case RETREATING:
            {
                break;
            }
        }        

        limitAndMove();
    }   
}