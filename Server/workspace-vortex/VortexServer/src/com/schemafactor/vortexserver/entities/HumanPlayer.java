package com.schemafactor.vortexserver.entities;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;


public class HumanPlayer extends Entity
{        
   private InetAddress userIP;       // User IP Address
   private int timeoutCounter=0;     // Counter, in milliseconds, for timeouts for dropped connections
   
   private boolean announceReceived = false;
   
   private boolean fireButton = false;
   private boolean lastFireState = false;
   
   private double speedscaling=5d;
  
   /** Creates a new instance of Human Player */
   public HumanPlayer(DatagramPacket packet)
   {
       // Random starting positions for multiple players
       super("Human Player [" + JavaTools.packetAddress(packet)+"]", eTypes.HUMAN_PLAYER, 1000+JavaTools.generator.nextInt(1000), 1000+JavaTools.generator.nextInt(1000));
       
       // Customize       
       spriteBase=48;
       spriteNum=0;
       spriteColor = Constants.COLOR_BLUE;   // Overridden by announce packet
       
       userIP = packet.getAddress();
       receiveUpdate(packet);
   }
   
   public void sendUpdate(byte[] data)
   {       
       try
       {            
           // Initialize a datagram packet with data and address
           DatagramPacket packet = new DatagramPacket(data, data.length, userIP, 3000); 

           // Create a datagram socket, send the packet through it, close it
           DatagramSocket dsocket = new DatagramSocket();
           dsocket.send(packet);
           dsocket.close();
       }
       catch (Exception e)
       {
           JavaTools.printlnTime("EXCEPTION sending update: " + JavaTools.getStackTrace(e));
       }
   }
      
   /** Return the InetAddress, for comparisons */
   public InetAddress getAddress()
   {
       return userIP;
   }
   
   // Increment and check the timeout
   public void checkTimeout()
   {
       if (timeoutCounter < 10000) timeoutCounter += Constants.TICK_TIME;
       
       if (timeoutCounter > 2000)   // Two seconds 
       {
           if (!removeMeFlag)
           {
               removeMeFlag = true;
               JavaTools.printlnTime( "Player Timed Out: " + description );
           }               
       }       
   }
   
   /** Update me with new data from client */
   public void receiveUpdate(DatagramPacket packet)
   {
       byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());    
       
       switch (data[0])   // Packet type
       {
           case Constants.CLIENT_ANNOUNCE:
           {      
               spriteColor = data[1];
               description = JavaTools.fromPETSCII(Arrays.copyOfRange(data, 2, data.length)) + " [" +JavaTools.packetAddress(packet) + "]";
               
               if (!announceReceived)
               {
                   JavaTools.printlnTime( "Player Joined: " + description );                   
               }
               
               announceReceived = true;
           }
           break;
           
           case Constants.CLIENT_UPDATE:
           {
               //Xpos       = (0xFF & data[1]) + (0xFF & data[2])*256;  // 0xFF used to force to signed           
               //Ypos       = (0xFF & data[3]) + (0xFF & data[4])*256;
               
             Xspeed     = data[5]/speedscaling;
             Yspeed     = data[6]/speedscaling;
             spriteNum  = data[7];
             fireButton =(data[8] == 1);             
             
             // Cheat a little and do the position math here.  Eventually move to client?
             move();
             
             // Handle fire button
             handleFireButton();           
           }
           break;
           
           default:
           {
               JavaTools.printlnTime("Bad packet type " + data[0] + " from " + userIP.toString());
               return;
           }
       }
                
       // Reset timeout
       timeoutCounter = 0;
   }
   

   @Override
   public void update()
   {              
       // move();   // Don't call for for human players - Position will be controlled by client.       
       
       // Send data packet to the client              
       byte[] message = new byte[940];  
       message[0] = Constants.PACKET_UPDATE;
       message[1] = 0; // Unused
       message[2] = getScroll(Xpos);  // Fine X
       message[3] = getScroll(Ypos);  // Fine Y           
       message[4] = 0; // Unused - Screen # if needed
       message[5] = 0; // Unused - Spare
              
       int offset = 6;
              
       // Determine which entities would be visible on screen to this player.
       List<Entity> visibleEntities = new ArrayList<Entity>();
       
       for (Entity e : universe.getEntities())
       {           
            if (this == e) continue;  // Don't update with myself
           
            if (isOnScreen(e))
            {
                visibleEntities.add(e);   
            }
       }

       // The C64 can't display more than 7 entities at a time due to sprite limits (and our client can't multiplex)
       if (visibleEntities.size() > 7)       
       {   
           // In future, could be fancier with round-robin, or only showing 7 closest entities.  For now, just limit to the first 7.           
           try
           {
               visibleEntities = visibleEntities.subList(0, 6); 
           }
           catch (Exception e)
           {              
               JavaTools.printlnTime( "EXCEPTION determining visible entities: " + JavaTools.getStackTrace(e));
           }
       }
              
       for (Entity e : visibleEntities)
       {   
           // TODO, this needs serious wrapping handling.  Make this a function.
           
           // Determine distance between this player and other entities
           long xdist = (long)(this.getXpos() - e.getXpos());
           long ydist = (long)(this.getYpos() - e.getYpos());
          
           // Relative distances onscreen
           int xrel = (int)(Constants.PLAYER_XPOS - xdist);  
           int yrel = (int)(Constants.PLAYER_YPOS - ydist);                      
           
           message[offset+0] = JavaTools.getLowByte(xrel);  
           message[offset+1] = JavaTools.getHighByte(xrel);
           message[offset+2] = JavaTools.getLowByte(yrel);  
           message[offset+3] = JavaTools.getHighByte(yrel);  // Not used by C64
           message[offset+4] = (byte)(e.getXspeed()*100d);
           message[offset+5] = (byte)(e.getYspeed()*100d);
           message[offset+6] = e.getColor();
           message[offset+7] = e.getSpriteNum();
           message[offset+8] = 0;  // Spare 1
           message[offset+9] = 0;  // Spare 2
           
           offset += 10;   
       }
       
       // Sound effects
       
       
       // Messages
       
       // And now, the first 20 lines (=800 bytes) of the screen.       
       System.arraycopy(universe.getScreen(getXcell(), getYcell()), 0, message, 140, 20*40);
       
       // Send the packet.
       sendUpdate(message);           
           
       // Increment and Timeout.  This is reset in receiveUpdate() above.     
       checkTimeout();
       return;
   }     
   
   private boolean isOnScreen(Entity e)
   {
       long xdist = (long)(this.getXpos() - e.getXpos());
       long ydist = (long)(this.getYpos() - e.getYpos());
       
       if (Math.abs(xdist) > 170) return false;
       if (Math.abs(ydist) > 110) return false;
       
       return true;
   }
   
   public void handleFireButton()
   {
       // Look for a positive edge       
       if ((fireButton == true) && (lastFireState == false))
       {           
           fireTorpedo();
       }      
      
       lastFireState = fireButton;              
   }   
  
   /** 
    * Return angle for human players.
    * @return Current angle in radians
    */
   @Override
   protected double getAngle() 
   {
       // TODO, this needs to be made cleaner.  For now, derive direction from sprite number.
       double angle = (Math.PI/2d) - (spriteNum/8d)*2d*Math.PI;
       return angle;
   }
   
}