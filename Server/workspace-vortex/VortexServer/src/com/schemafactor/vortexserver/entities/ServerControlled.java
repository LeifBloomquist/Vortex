package com.schemafactor.vortexserver.entities;

import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.Universe;

public class ServerControlled extends Entity
{	
   private byte spriteNum; 
   
   /** Creates a new instance of Server Controlled */
   public ServerControlled()
   {
       super("Server Controlled Enemy", 180, 100, Entity.eTypes.SERVER_CONTROLLED );  
   }
       
   /** Return Color */
   public byte getColor()
   {
       return Constants.COLOR_GREY1;
   }
   
   /** Return Sprite# */
   public byte getSprite()
   {
       return spriteNum;
   }

@Override
public boolean update(Universe universe, Vector<Entity> allEntities) {
	   // Move within the world
	   move(universe);
	   return false;
}   
}