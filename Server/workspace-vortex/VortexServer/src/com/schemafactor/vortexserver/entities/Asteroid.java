package com.schemafactor.vortexserver.entities;

import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;


public class Asteroid extends Entity
{	
    private byte spriteBase = 96;
    private byte spriteNum  = (byte)JavaTools.generator.nextInt(32);
   
    /** Milliseconds */
    private int animtimer = 0;  
   
    /** Creates a new instance of Asteroid */
    public Asteroid()
    {
       super("Asteroid", 5100+JavaTools.generator.nextInt(100), 5080+JavaTools.generator.nextInt(100), Entity.eTypes.ASTEROID);  
       
       Xspeed = -0.5 + (JavaTools.generator.nextDouble());
       Yspeed = -0.5 + (JavaTools.generator.nextDouble());
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
	public boolean update(Universe universe, Vector<Entity> allEntities) 
	{
		animtimer += Constants.TICK_TIME;
		
		// Animate the Asteroid
		if (animtimer >= 100)   // milliseconds
		{
			animtimer=0;
			spriteNum++;
			if (spriteNum > 31) spriteNum=0;
		}
		
		// Move within the universe
		move(universe);
		return false;
	}   
}