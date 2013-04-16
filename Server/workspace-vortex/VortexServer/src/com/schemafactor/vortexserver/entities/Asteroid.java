package com.schemafactor.vortexserver.entities;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.Universe;
import com.schemafactor.vortexserver.common.JavaTools;


public class Asteroid extends Entity
{	
   private byte spriteBase;
   private byte spriteNum;   // 0 to 32 
   
   /** Creates a new instance of Asteroid */
   public Asteroid()
   {
       super("Asteroid", 1000, 1000, Entity.eTypes.ASTEROID);  
       
       Xspeed = -5 + JavaTools.generator.nextInt(11);
       Yspeed = -5 + JavaTools.generator.nextInt(11);
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
   public boolean update(Universe universe)
   {   
	   // Move within the world
	   move(universe);
	   return false;
   }   
}