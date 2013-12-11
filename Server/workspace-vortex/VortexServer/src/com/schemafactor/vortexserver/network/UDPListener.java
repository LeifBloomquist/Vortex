package com.schemafactor.vortexserver.network;

/*
 * UDPListener.java
 *
 * Created on December 14, 2012, 2:56 PM
 *
 * Listens for UDP packets from clients and updates the internal model of the playing field.
 */

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Vector;

import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Entity;
import com.schemafactor.vortexserver.entities.HumanPlayer;
import com.schemafactor.vortexserver.universe.Universe;

/**
 * @author LBLOOMQU
 */
public class UDPListener
{
    private Vector<Entity> allEntities = null;
    
    /** Creates a new instance of UDPListener */
    public UDPListener(int port, Vector<Entity> allEntities)
    {
        this.allEntities = allEntities;  // Keep a reference to the users list

        try
        {
        	byte[] buf = new byte[50];
            DatagramSocket socket = new DatagramSocket(port);
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
                
            JavaTools.printlnTime( "Waiting for packets on port " + port );
            
            /* Loop Forever, waiting for packets. */      
            while (true) 
            {                            
                socket.receive(packet);  // This blocks!                
                handlePacket(packet);    // Handle it              
            }
            
            //socket.close();
        }
        catch (SocketException ex)
        {
            JavaTools.printlnTime( "Socket Exception: " + ex.toString());
        }
        catch (IOException ex)
        {
            JavaTools.printlnTime( "IO Exception: " + ex.toString());
        }   
    }
    
    /**
     *  Handle a received packet.
     */    
    private void handlePacket(DatagramPacket packet)
    {
        byte[] packetBytes = Arrays.copyOf(packet.getData(), packet.getLength());
     
        // Check Checksum - Future
        
//        String s = "Packet received!  Length:" + packet.getLength() + "  Data [ ";
//        
//        for (Byte b : packetBytes)
//        {
//        	s += b.toString()+ " ";
//        }
//        s+="]";
//         
//        JavaTools.printlnTime(s); 
                
        // Determine player
        
        synchronized (allEntities)
        {
            for (Entity e : allEntities)
            {
            	if ( e.getType().equals(Entity.eTypes.HUMAN_PLAYER) )
            	{
            		HumanPlayer hp = (HumanPlayer)e;
            		
            		if ( hp.getAddress().equals( packet.getAddress()) )   // Match found.  There's probably a faster way to do this, hashtable etc.
                    {
            			hp.receiveUpdate(packetBytes);
                        return;
                    }            		
            	}                
            }
        }
        
        // No match, create new user and add to vector
        JavaTools.printlnTime( "Creating player from " + packet.getAddress() );
        HumanPlayer who = new HumanPlayer(packet);
            
        synchronized (allEntities) 
        {
            allEntities.add(who);
        }
        return;  
   }
}