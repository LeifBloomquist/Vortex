package com.schemafactor.vortexserver.entities;

import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;


public class Asteroid extends Entity
{	
   private byte spriteBase = 96;
   private byte spriteNum  = 0;  // 0 to 32 
   
   /** Creates a new instance of Asteroid */
   public Asteroid()
   {
       super("Asteroid", 5000+JavaTools.generator.nextInt(100), 5080+JavaTools.generator.nextInt(1), Entity.eTypes.ASTEROID);  
       
       Xspeed = 0; //-0.05 + (JavaTools.generator.nextDouble()*0.1);
       Yspeed = -1; //-0.05 + (JavaTools.generator.nextDouble()*0.1);
   }
       
   /** Return Color */
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
	    // Animate the Asteroid
		//spriteNum++;
		//if (spriteNum > 32) spriteNum=0;	
		
		// Move within the universe
		move(universe);
		return false;
	}   
}