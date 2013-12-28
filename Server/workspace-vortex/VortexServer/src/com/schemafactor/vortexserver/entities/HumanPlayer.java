package com.schemafactor.vortexserver.entities;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;


public class HumanPlayer extends Entity
{		
   private InetAddress userIP;       // User IP Address
   private int timeoutCounter=0;     // Counter, in milliseconds, for timeouts for dropped connections
  
   /** Creates a new instance of Human Player */
   public HumanPlayer(DatagramPacket packet)
   {
       super("Human Player from " + packet.getAddress(), 5000, 5100, Entity.eTypes.HUMAN_PLAYER);
       
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
	   		case  Constants.CLIENT_ANNOUNCE:
	   		 // Not yet implemented
	   	    break;
	   	    
	   		case Constants.CLIENT_UPDATE:
	          //Xpos    = (0xFF & data[1]) + (0xFF & data[2])*256;  // 0xFF used to force to signed
	          //Ypos    = (0xFF & data[3]) + (0xFF & data[4])*256;
	   		  Xspeed  = (data[5] / 10d);
	   		  Yspeed  = (data[6] / 10d);
	   		  
	   		  // Cheat a little and do the position math here.  Eventually, move to client.
	   		  Xpos += Xspeed;
	   		  Ypos += Yspeed;
	   		  //wrap();
	   		  
	   		  break;
	   		
	   		default:
	   			JavaTools.printlnTime("Bad packet type " + data[0] + " from " + userIP.toString());
	   			return;
	   }
	   		 
       // Reset timeout
       timeoutCounter = 0;
   }
   

   @Override
   public boolean update(Universe universe, Vector<Entity> allEntities)
   {   	   
	   // move(universe);  // Don't call for for human players - Position is controlled by client.
	   wrap(universe);  // This is kind of odd placed here, should ultimately be in receiveUpdate() above
	   
	   // Send data packet to the client	   	   
	   byte[] message = new byte[940];  
	   message[0] = Constants.PACKET_UPDATE;
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
		   
		   // Determine distance between this player and other entities
		   long xdist = (long)(this.getXpos() - e.getXpos());
		   long ydist = (long)(this.getYpos() - e.getYpos());
		   
		   if (Math.abs(xdist) > 170) continue;
		   if (Math.abs(ydist) > 110) continue;
		   
		   
//           if (xdist < -Constants.PLAYER_XPOS)  continue;  // Too far to the left
//           if (xdist >  Constants.PLAYER_XPOS)  continue;  // Too far to the right
//           
//		              
//           if (ydist < -Constants.PLAYER_YPOS)  continue;  // Too high
//           if (ydist >  Constants.PLAYER_YPOS)  continue;  // Too low         
		   
           // Other entity is visible!
           int xrel = (int)(Constants.PLAYER_XPOS - xdist);
           int yrel = (int)(Constants.PLAYER_YPOS - ydist);                      
           
           message[offset+0] = JavaTools.getLowByte(xrel);  
           message[offset+1] = JavaTools.getHighByte(xrel);
           message[offset+2] = JavaTools.getLowByte(yrel);  
           message[offset+3] = JavaTools.getHighByte(yrel);  // Not used by C64
           message[offset+4] = e.getXspeed();
           message[offset+5] = e.getYspeed();
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
	   incTimeout();
	   return checkTimeout();
   }      
}