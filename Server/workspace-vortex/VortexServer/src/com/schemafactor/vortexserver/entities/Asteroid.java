package com.schemafactor.vortexserver.entities;

import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;


public class Asteroid extends Entity
{	
	private final int num_sprites=32; 
	
    /** Milliseconds */
    private int animtimer = 0;  
   
    /** Creates a new instance of Asteroid */
    public Asteroid(Universe universe, Vector<Entity> allEntities)
    {	
       super("Asteroid", Entity.eTypes.ASTEROID, universe.getXsize()*JavaTools.generator.nextDouble(), universe.getYsize()*JavaTools.generator.nextDouble(), universe, allEntities);       
       
       Xspeed = -0.5 + (JavaTools.generator.nextDouble());
       Yspeed = -0.5 + (JavaTools.generator.nextDouble());
       
       spriteBase = 96;
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