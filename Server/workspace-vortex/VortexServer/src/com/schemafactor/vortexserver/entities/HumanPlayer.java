package com.schemafactor.vortexserver.entities;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;


public class HumanPlayer extends Entity
{		
   private InetAddress userIP;       // User IP Address
   
   private byte shipColor;
   private byte spriteNum; 
   
   private int timeoutCounter=0;  
  
   /** Creates a new instance of Human Player */
   public HumanPlayer(DatagramPacket packet)
   {
       super("Human Player from " + packet.getAddress(), 100, 100, Entity.eTypes.HUMAN_PLAYER);
       
       userIP = packet.getAddress();
       receiveUpdate(packet.getData());
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
 //          JavaTools.printlnTime("Packet sent to " + myIP);
       }
       catch (Exception e)
       {
           JavaTools.printlnTime(e.toString());
       }
   }
      
   /** Return the InetAddress, for comparisons */
   public InetAddress getAddress()
   {
       return userIP;
   } 

   /** Return Color */
   public byte getColor()
   {
       return shipColor;
   }
   
   /** Return Sprite# */
   public byte getSprite()
   {
       return spriteNum;
   }
   
   // Increment the timeout - called by UpdaterThread
   public void incTimeout()
   {
	   if (timeoutCounter < 10000) timeoutCounter += Constants.TICK_TIME;
   }
   
   // Check the timeout
   // true if timed out
   public boolean checkTimeout()
   {
      return (timeoutCounter > 2000);   // Two seconds         
   }
   
   /** Update me with new data from client */
   public void receiveUpdate(byte[] data)
   {
	   switch (data[0])   // Packet type
	   {
	   		case 0:  // Player name and Announce
	   			
	   	    break;
	   	    
	   		case 2:  // Client ready
	   			
	   		break;
	   		
	   		case 3:  // Client update
	          Xpos    = (0xFF & data[1]) + (0xFF & data[2])*256;  // 0xFF used to force to signed
	          Ypos    = (0xFF & data[3]) + (0xFF & data[4])*256;
	   		  Xspeed  = (data[5] / 100d);
	   		  Yspeed  = (data[6] / 100d);	   		   
	   		break;
	   		
	   		default:
	   			JavaTools.printlnTime("Bad packet type " + data[0] + " from " + userIP.toString());
	   			return;
	   		//break;
	   }
	   		 
       // Reset timeout
       timeoutCounter = 0;
       
       JavaTools.printlnTime("Player " + userIP.toString() + " location: " + Xpos + " " + Ypos);     
   } 
   

   @Override
   public boolean update(Universe universe, Vector<Entity> allEntities)
   {   
	   // Don't call for for human players - Position is controlled by client.
	   // move(universe);
	   
	   // Send data packet to the client	   	   
	   byte[] message = new byte[940];  
	   message[0] = (byte) 140;  // Packet type, game update.
	   message[1] = 0; // Unused
	   message[2] = getScroll(Xpos);  // Fine X
	   message[3] = getScroll(Ypos);  // Fine Y 		  
	   message[4] = 0; // Unused - Screen # if needed
	   message[5] = 0; // Unused - Spare
	   	   
	   int offset = 6;
	   
	   // !!! Quick assumption for testing, that there will never be more than 7 entities.
	   
	   for (Entity e : allEntities)
	   {		   
		   if (this == e) continue;  // Don't update with myself
		   
		   // TODO, this needs serious wrapping handling.  Make this a function.
		   
		   // Determine distance between this player and other entities    These assume player is centered.
		   long ydist = (long)(this.getYpos() - e.getYpos());           
           if (ydist < -Constants.CLIENT_YPOS)  continue;  // Too high
           if (ydist >  Constants.CLIENT_YPOS)  continue;  // Too low
		   
           long xdist = (long)(this.getXpos() - e.getXpos());           
           if (xdist < -Constants.CLIENT_XPOS)  continue;  // Too far to the left
           if (xdist >  Constants.CLIENT_XPOS)  continue;  // Too far to the right
		   
           // Other entity is visible!
           int yrel = (int)(ydist + Constants.CLIENT_YPOS);
           int xrel = (int)(xdist + Constants.CLIENT_XPOS);           
           
           message[offset+0] = JavaTools.getLowByte(xrel);  
           message[offset+1] = JavaTools.getHighByte(xrel);
           message[offset+2] = JavaTools.getLowByte(yrel);  
           message[offset+3] = JavaTools.getHighByte(yrel);  // Not used by C64
           message[offset+4] = e.getXspeed();
           message[offset+5] = e.getYspeed();
           message[offset+6] = 0;  // Sprite color
           message[offset+7] = 0;  // Sprite number
           message[offset+8] = 0;  // Spare 1
           message[offset+9] = 0;  // Spare 2
           
           offset += 10;   
	   }
	   
	   // And now, the first 20 lines (=800 bytes) of the screen.	   
	   System.arraycopy(universe.getScreen(getXcell(), getYcell()), 0, message, 140, 20*40);
	   
	   // Send the packet.
	   sendUpdate(message);		   
		   
	   // Increment and Timeout.  This is reset in receiveUpdate() above.
	   incTimeout();
	   return checkTimeout();
   }      
}