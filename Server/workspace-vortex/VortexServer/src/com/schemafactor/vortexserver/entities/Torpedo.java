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
  
   /** Creates a new instance */
   public Torpedo(Entity shooter, double angle)
   {
       super("Torpedo fired by " + shooter.getDescription(), Entity.eTypes.TORPEDO, (int)shooter.Xpos, (int)shooter.Ypos);
       
       // Determine overall pointing
       max_speed   = 7;
       Xspeed =  max_speed * Math.cos(angle); 
       Yspeed = -max_speed * Math.sin(angle);   // Negative here because our y-axis is inverted
       move();
       
       // Customize
       spriteBase=8;
       spriteNum=0;
       spriteColor = Constants.COLOR_YELLOW;       
   }
   
   // Increment and check the timeout
   public void checkTimeout()
   {
       if (timeoutCounter < 100000) timeoutCounter += Constants.TICK_TIME;
       
       if (timeoutCounter > 30000)   // Thirty seconds
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