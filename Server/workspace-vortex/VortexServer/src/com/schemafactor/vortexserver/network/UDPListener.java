package com.schemafactor.vortexserver.network;

/*
 * UDPListener.java
 *
 * Created on December 14, 2012, 2:56 PM
 *
 * Listens for UDP packets from clients and updates the internal model of the playing field.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Entity;
import com.schemafactor.vortexserver.entities.HumanPlayer;
import com.schemafactor.vortexserver.universe.Universe;

/**
 * @author LBLOOMQU
 */
public class UDPListener
{    
    /** Creates a new instance of UDPListener */
    public UDPListener(int port, Universe universe)
    {
        try
        {
            Thread.currentThread().setName("Vortex UDP Listener Thread");
            
            byte[] buf = new byte[50];
            DatagramSocket socket = new DatagramSocket(port);
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
                
            JavaTools.printlnTime( "Waiting for packets on port " + port );
            
            /* Loop Forever, waiting for packets. */      
            while (true) 
            {                            
                socket.receive(packet);  // This blocks!                
                handlePacket(packet, universe);    // Handle it              
            }
            
            //socket.close();
        }
        catch (SocketException ex)
        {
            JavaTools.printlnTime( "Socket Exception: " + JavaTools.getStackTrace(ex));
        }
        catch (IOException ex)
        {
            JavaTools.printlnTime( "IO Exception: " + JavaTools.getStackTrace(ex));
        }   
    }
    
    /**
     *  Handle a received packet.
     */    
    private void handlePacket(DatagramPacket packet, Universe universe)
    {
        byte[] packetBytes = Arrays.copyOf(packet.getData(), packet.getLength());
     
        // Check Checksum - Future
                
        // Determine player
        for (Entity e : universe.getEntities())
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
        
        // No match, create new user and add to vector
        JavaTools.printlnTime( "Creating player from " + packet.getAddress() );
        HumanPlayer who = new HumanPlayer(packet, universe);
        
        try
        {
            universe.newEntities.put(who);
        }
        catch (InterruptedException e)
        {
            JavaTools.printlnTime( "EXCEPTION adding new player: " + JavaTools.getStackTrace(e) );
        }
        
        return;  
   }
}