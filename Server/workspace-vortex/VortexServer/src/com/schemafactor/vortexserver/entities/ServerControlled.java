package com.schemafactor.vortexserver.entities;

import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;

public class ServerControlled extends Entity
{
   private byte spriteBase = 32;
   private byte spriteNum  = 0;  // TODO
	   
   /** Creates a new instance of Server Controlled */
   public ServerControlled()
   {
       super("Server Controlled Alien", 5000+JavaTools.generator.nextInt(100), 5000+JavaTools.generator.nextInt(200), Entity.eTypes.SERVER_CONTROLLED);
       
       Xspeed = 0;
       Yspeed = 0;
   }
       
   /** Return Color */
   public byte getColor()
   {
       return Constants.COLOR_GREEN;
   }
   
   @Override 
   public byte getSpriteNum()
   {
       return (byte)(spriteBase+spriteNum);  // TODO, base on direction
   }   

	@Override
	public boolean update(Universe universe, Vector<Entity> allEntities) 
	{
		// Move within the world
		move(universe);
		return false;
	}   
}