package com.schemafactor.vortexserver.entities;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.Universe;

public abstract class Entity 
{	
   public enum eTypes {NONE, HUMAN_PLAYER, SERVER_CONTROLLED};
   protected eTypes myType = eTypes.NONE;
	
   protected String description;   
   
   protected double Xpos;      // These are pixels, and refers to the top-left corner of the object (sprite, etc.)
   protected double Ypos;
   
   protected byte Xspeed = 0;  // These are signed bytes, +/- 100.
   protected byte Yspeed = 0;
     
   /** Creates a new instance of Entity */
   public Entity(String description, double startX, double startY, eTypes type)
   {
	   this.description = new String(description);
	   Xpos = startX;
	   Ypos = startY;
	   this.myType = type;
   }
   
   public void move()
   {
       Xpos += (Xspeed / 100.0);  
       Ypos += (Yspeed / 100.0);  
   }
   
   /** Return X,Y positions */
   public double getXpos()
   {
       return Xpos;
   }
   
   public double getYpos()
   {
       return Ypos;
   }
   
   /** Return X,Y position as cell counts */
   public long getXcell()
   {
       return (long) (Math.floor(Xpos / Constants.PIXELSPERCELL));
   }
   
   public long getYcell()
   {
	   return (long) (Math.floor(Ypos / Constants.PIXELSPERCELL));
   }
   
   /** Return the scroll remainder for either X,Y (for smooth scrolling) */
   public byte getScroll(double xory) 
   {
	  return (byte) (Constants.PIXELSPERCELL - (Math.floor(xory) % Constants.PIXELSPERCELL) -1);
   }
   
    /** Return X,Y speeds */
   public byte getXspeed()
   {
       return Xspeed;
   }
   
   public byte getYspeed()
   {
       return Yspeed;
   }

   public String getDescription() 
   {
       return description;
   }

   abstract public boolean update(Universe world);   // True means the player should be removed (timeout, destroyed, etc)   
   abstract  public eTypes getType();
}