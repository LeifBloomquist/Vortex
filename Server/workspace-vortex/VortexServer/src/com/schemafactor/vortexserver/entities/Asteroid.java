package com.schemafactor.vortexserver.entities;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;


public class Asteroid extends Entity
{    
    private final int num_sprites=32; 
    
    /** Milliseconds */
    private int animtimer = 0;  
   
    /** Creates a new instance of Asteroid */
    public Asteroid(String name)
    {
       super(name, Entity.eTypes.ASTEROID,JavaTools.generator.nextInt((int)Universe.getInstance().getXsize()), JavaTools.generator.nextInt((int)Universe.getInstance().getYsize()));       
       
       Xspeed = -0.5 + (JavaTools.generator.nextDouble());
       Yspeed = -0.5 + (JavaTools.generator.nextDouble());
       
       spriteBase = 112;
       spriteNum  = (byte)JavaTools.generator.nextInt(num_sprites);
    }
       
    /** Return Color */
    @Override 
    public byte getColor()
    {
       return Constants.COLOR_GREY2;
    }
   
    /** Return Sprite# */
    @Override 
    public byte getSpriteNum()
    {
        return (byte)(spriteBase+spriteNum);
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