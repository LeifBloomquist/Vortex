package com.schemafactor.vortexserver.entities;

import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;

public class ServerControlled extends Entity
{
	   
   /** Creates a new instance of Server Controlled */
   public ServerControlled(Universe universe, Vector<Entity> allEntities)
   {
       super("Server Controlled Alien",  Entity.eTypes.SERVER_CONTROLLED, 5000+JavaTools.generator.nextInt(200), 5000+JavaTools.generator.nextInt(200), universe,  allEntities);
       
       Xspeed = 0;
       Yspeed = 0;
   }
       
   /** Return Color */
   public byte getColor()
   {
       return Constants.COLOR_GREEN;
   }

	@Override
	public void update() 
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
	       return;
		}
		
		// Only get within so many pixels
		if (distanceTo(target) < 30)
		{
		   Xspeed = 0;
		   Yspeed = 0;
		   // Leave sprite#
		   return;
		}
		
		// Move towards the target
		double xdist = target.getXpos() - this.Xpos;
		double ydist = target.getYpos() - this.Ypos;	
		
		// TODO: Wrapping handling needed here
		
		double angle = Math.atan2(-ydist, xdist);			
		
		Xspeed =  2 * Math.cos(angle); 
		Yspeed = -2 * Math.sin(angle);		
		
		// Determine pointing
		double degrees = 112.5-Math.toDegrees(angle);				
		while (degrees<0)   degrees+=360d; 
		while (degrees>360) degrees-=360d;		
		spriteNum = (byte)(degrees/45);
	
		// Move within the world
		move();
		return;
	}   
}