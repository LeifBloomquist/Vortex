package com.schemafactor.vortexserver.entities;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.Universe;
import com.schemafactor.vortexserver.common.JavaTools;


public class HumanPlayer extends Entity
{	
	
   private InetAddress myIP;       // User IP Address
   
   private byte shipColor;
   private byte spriteNum; 
   
   private int timeoutCounter=0;    
   
   private boolean toggle = false;  // Used to alternate color/screen update packets
   
   private int screen = 0;   // Which screen the client should show
   private long lastX=-1;
   private long lastY=-1;
   
  
   /** Creates a new instance of RaceCar */
   public HumanPlayer(DatagramPacket packet)
   {
       super("Human Player from " + packet.getAddress(), 100, 100, Entity.eTypes.HUMAN_PLAYER);
       
       myIP = packet.getAddress();
       receiveUpdate(packet.getData());
   }
   
   public void sendUpdate(byte[] message)
   {       
       try
       {            
           // Initialize a datagram packet with data and address
           DatagramPacket packet = new DatagramPacket(message, message.length, myIP, 3000); 

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
       return myIP;
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
	   if (timeoutCounter < 1000000) timeoutCounter += Constants.TICK_TIME;
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
	   Xspeed = data[0];   // Signed
	   Yspeed = data[1];
	   
//       playerNum  = data[0];  
//       Xpos       = (0xFF & data[1]) + (0xFF & data[2])*256;  // 0xFF used to force to signed
//       Ypos       = (0xFF & data[3]) + (0xFF & data[4])*256;
//       XspeedLow  = data[5];
//       XspeedHigh = data[6];
//       YspeedLow  = data[7];
//       YspeedHigh = data[8];
//       carColor   = data[9];
//       spriteNum  = data[10];        
//       
       // Reset timeout
       timeoutCounter = 0;
//       
//      // System.out.println("Player " + myIP.toString() + " location: " + Xpos + " " + Ypos);     
   } 
   

   @Override
   public boolean update(Universe universe)
   {   
	   // Move within the world
	   move(universe);
	   
	   if ((lastX != getXcell()) || (lastY != getYcell()))   // We have moved to a new cell, C64 should switch screens to avoid flickering  
	   {
	      if (screen==0)
	      {
	    	  screen=1;
	      }
	      else
	      {
	    	  screen=0;
	      }
	   }
	   
		   
	   lastX = getXcell();
	   lastY = getYcell();
	   
	   byte[] message = null;
	   
	   // Update Client	   
	  // if (toggle)
	  // {
		   // Screen
		   message = new byte[4 + Constants.SCREEN_SIZE];  
		   message[0] = Constants.PACKET_SCREEN;
		   message[1] = getScroll(Xpos);  // Fine X
		   message[2] = getScroll(Ypos);  // Fine Y 		  
		   message[3] = (byte) screen;
		   System.arraycopy(universe.getScreen(getXcell(), getYcell()), 0, message, 4,  Constants.SCREEN_SIZE);
		   
		   sendUpdate(message);
		   
		   JavaTools.printlnTime("X pos=" + Math.floor(Xpos) + " X Scroll="+ message[1] +"   X Cell="+getXcell());
//	   }
//	   else
//	   {
//		   // Color
//		   message = new byte[1 + Constants.SCREEN_SIZE];
//		   message[0] = Constants.PACKET_COLOR;
//		   System.arraycopy(universe.getScreenColor(getXcell(), getYcell()), 0, message, 1,  Constants.SCREEN_SIZE);		   
//		   sendUpdate(message);
//	   }
	   
	   toggle = !toggle;
	   
	   // Increment and Timeout
	   incTimeout();
	   return checkTimeout();
   }   
   
   @Override
   public eTypes getType() 
   {
      return myType;
   }   
   
}