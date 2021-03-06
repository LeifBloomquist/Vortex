package com.schemafactor.vortexserver.entities;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;


public class Torpedo extends Entity
{        
   private int timeoutCounter=0;     // Counter, in milliseconds, for life of this object      
  
   /** Creates a new instance when torpedo is fired from another entity*/
   public Torpedo(Entity shooter)
   {
       super("Torpedo fired by " + shooter.getDescription(), eTypes.TORPEDO, (int)shooter.Xpos, (int)shooter.Ypos);
       
       // Match shooter's speed
       Xspeed = shooter.getXspeed();
       Yspeed = shooter.getYspeed();
       
       // Add in velocity.  This doesn't seem quite right
       max_speed   = 7d;
       double angle = shooter.getAngle();       
       Xspeed +=  max_speed * Math.cos(angle); 
       Yspeed += -max_speed * Math.sin(angle);   // Negative here because our y-axis is inverted
       move();
       
       // Customize
       spriteBase=8;
       spriteNum=0;
       spriteColor = Constants.COLOR_WHITE;       
   }
   
   // Increment and check the timeout
   public void checkTimeout()
   {
       if (timeoutCounter < 100000) timeoutCounter += Constants.TICK_TIME;
       
       if (timeoutCounter > 5000)   // Five seconds
       {
           removeMeFlag = true;
       }       
   }   

   @Override
   public void update()
   {          
       move();       
       checkTimeout();
       return;
   }     
}