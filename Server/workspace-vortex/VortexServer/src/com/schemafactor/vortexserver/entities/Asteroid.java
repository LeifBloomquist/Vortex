package com.schemafactor.vortexserver.entities;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;


public class Asteroid extends Entity
{    
    private int num_sprites=0; 
    
    /** Milliseconds */
    private int animtimer = 0;  
   
    /** Creates a new instance of Asteroid */
    public Asteroid(String name)
    {
       super(name, Entity.eTypes.ASTEROID,JavaTools.generator.nextInt(12000), JavaTools.generator.nextInt((int)Universe.getInstance().getYsize()));       
       
       Xspeed = -0.5 + (JavaTools.generator.nextDouble());
       Yspeed = -0.5 + (JavaTools.generator.nextDouble());
  
       // Customize       
       if (JavaTools.generator.nextInt(10) < 3)
       {
           // Large Asteroid
           spriteBase  = 96;
           num_sprites = 8;
       }
       else
       {
           // Small Asteroid
           spriteBase  = 112;
           num_sprites = 32;
       }
       
       spriteNum   = (byte)JavaTools.generator.nextInt(num_sprites);
       spriteColor = Constants.COLOR_GREY2;       
    }           

    @Override
    public void update() 
    {
        animtimer += Constants.TICK_TIME;
        
        // Animate the Asteroid
        if (animtimer >= 200)   // milliseconds
        {
            animtimer=0;
            spriteNum++;
            if (spriteNum >= num_sprites) spriteNum=0;
        }
        
        // Move within the universe
        move();
        return;
    }   
}