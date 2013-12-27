package com.schemafactor.vortexserver.entities;

import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.universe.Universe;

public abstract class Entity 
{	
   public enum eTypes {NONE, HUMAN_PLAYER, SERVER_CONTROLLED, ASTEROID};
   protected eTypes myType = eTypes.NONE;
	
   protected String description;   
   
   protected double Xpos;      // These are pixels, and refers to the top-left corner of the object (sprite, etc.)
   protected double Ypos;
   
   protected double Xspeed = 0; 
   protected double Yspeed = 0;
     
   /** Creates a new instance of Entity */
   public Entity(String description, double startX, double startY, eTypes type)
   {
	   this.description = new String(description);
	   Xpos = startX;
	   Ypos = startY;
	   this.myType = type;
   }
   
   public void move(Universe universe)
   {
       Xpos += Xspeed;  
       Ypos += Yspeed;
       
       // Wrap around
       if (Xpos < 0) Xpos += universe.getXsize();
       if (Ypos < 0) Ypos += universe.getYsize();
       
       if (Xpos > universe.getXsize()) Xpos -= universe.getXsize();
       if (Ypos > universe.getYsize()) Ypos -= universe.getYsize();
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
   
    /** Return X,Y speeds as bytes */
   public byte getXspeed()
   {
       return (byte)(Xspeed*100d);
   }
   
   public byte getYspeed()
   {
       return (byte)(Yspeed*100d);
   }

   public String getDescription() 
   {
       return description;
   }

   abstract public boolean update(Universe universe, Vector<Entity> allEntities);   // True means the player should be removed (timeout, destroyed, etc)   
   

   public eTypes getType() 
   {
      return myType;
   }   
   
}