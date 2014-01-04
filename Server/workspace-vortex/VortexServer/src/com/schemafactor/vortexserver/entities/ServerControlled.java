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
       super("Server Controlled Alien", 5100+JavaTools.generator.nextInt(100), 5100+JavaTools.generator.nextInt(200), Entity.eTypes.SERVER_CONTROLLED);
       
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
       return (byte)(spriteBase+spriteNum); 
   	}   

	@Override
	public boolean update(Universe universe, Vector<Entity> allEntities) 
	{
		// Find a human player to chase
		
		Entity target = null;
		
		for (Entity e : allEntities)
		{
			if (e.getType() == Entity.eTypes.HUMAN_PLAYER)
			{
				target = e;
			}
		}
		
		if (target == null)  // No human players
		{
	       Xspeed = 0;
	       Yspeed = 0;
	       //Leave sprite#
	       return false;
		}
		
		// Only get within so many pixels
		if (distanceTo(target) < 30)
		{
		   Xspeed = 0;
		   Yspeed = 0;
		   // Leave sprite#
		   return false;
		}
		
		// Move towards the target
		double xdist = target.getXpos() - this.Xpos;
		double ydist = target.getYpos() - this.Ypos;	
		
		// TODO: Wrapping handling needed here
		
		double angle = Math.atan2(-ydist, xdist);			
		
		Xspeed =  2 * Math.cos(angle); 
		Yspeed = -2 * Math.sin(angle);		
		
		// Determine pointing
		double degrees = Math.toDegrees(angle);
		double degrees1 = 112.5-degrees;		
		if (degrees1<0) degrees1+=360; 
		
		spriteNum = (byte)(degrees1/45);
		//JavaTools.printlnTime("xdist="+xdist+"\t ydist="+ydist+"\t angle="+angle + "\t degrees="+degrees + "\t sprite="+spriteNum);		
		JavaTools.printlnTime("degrees="+degrees + "\t degrees1="+degrees1 + "\t sprite="+spriteNum);
	
		// Move within the world
		move(universe);
		return false;
	}   
}