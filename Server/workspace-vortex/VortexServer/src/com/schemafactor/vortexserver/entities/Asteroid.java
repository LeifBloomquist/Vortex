package com.schemafactor.vortexserver.entities;

import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;


public class Asteroid extends Entity
{	
   private byte spriteBase = 0; // TODO
   private byte spriteNum  = 0;   // 0 to 32 
   
   /** Creates a new instance of Asteroid */
   public Asteroid()
   {
       super("Asteroid", 1000+JavaTools.generator.nextInt(1000), 1000+JavaTools.generator.nextInt(1000), Entity.eTypes.ASTEROID);  
       
       Xspeed = 0; //-0.05 + JavaTools.generator.nextInt(11);
       Yspeed = 0; //-5 + JavaTools.generator.nextInt(11);
   }
       
   /** Return Color */
   public byte getColor()
   {
       return Constants.COLOR_GREY1;
   }
   
   /** Return Sprite# */
   public byte getSprite()
   {
       return (byte)(spriteBase+spriteNum);
   }      

	@Override
	public boolean update(Universe universe, Vector<Entity> allEntities) 
	{
	   // Move within the universe
	   move(universe);
	   return false;
	}   
}