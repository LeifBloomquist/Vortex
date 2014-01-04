package com.schemafactor.vortexserver.entities;

import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.universe.Universe;

public abstract class Entity 
{    
   public static enum eTypes {NONE, HUMAN_PLAYER, SERVER_CONTROLLED, ASTEROID}
   protected eTypes myType = eTypes.NONE;
    
   protected String description;   
   
   protected double Xpos = -1;      // These are pixels, and refers to the top-left corner of the object (sprite, etc.)
   protected double Ypos = -1;
   
   protected double Xspeed = 0; 
   protected double Yspeed = 0;
   
   protected byte spriteBase  = 32;   
   protected byte spriteNum   = 0;  // This is the offset from SPRITE_BASE, *not* the selected memory bank.  (Client handles this)
   protected byte spriteColor = Constants.COLOR_BLACK;
   
   protected Universe universe=null;
   protected Vector<Entity> allEntities=null;
   
   /** Flag that this entity is to be removed at the end of this update cycle.  true=remove */
   protected boolean removeMeFlag = false;
     
   /** Creates a new instance of Entity */
   public Entity(String description, eTypes type, double startX, double startY,  Universe universe, Vector<Entity> allEntities)
   {
       this.description = new String(description);
       this.myType = type;
       Xpos = startX;
       Ypos = startY;
       this.universe = universe;
       this.allEntities = allEntities;       
   }
   
   // Handle wraparound
   protected void wrap()
   {
       // Wrap around, repeatedly if necessary
       while (Xpos < 0)                   Xpos += universe.getXsize();
       while (Ypos < 0)                   Ypos += universe.getYsize();       
       while (Xpos > universe.getXsize()) Xpos -= universe.getXsize();
       while (Ypos > universe.getYsize()) Ypos -= universe.getYsize();       
   }
   
   public void move()
   {
       Xpos += Xspeed;  
       Ypos += Yspeed;
       
       wrap();
   }
   
   abstract public void update();   // True means the player should be removed (timeout, destroyed, etc)
   
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

   public eTypes getType() 
   {
      return myType;
   }   
   
   /** Return Color */
   public byte getColor()
   {
       return spriteColor;
   }
   
    /** Return Sprite# */
      public byte getSpriteNum()
      {
      return (byte)(spriteBase+spriteNum); 
      }   
   
   // Helper function to get distance to another Entity
   protected double distanceTo(Entity target)
   {       
       return Math.sqrt( Math.pow((this.Xpos - target.getXpos()), 2) + Math.pow((this.Ypos - target.getYpos()), 2)); 
   }

    public boolean removeMe() 
    {        
        return removeMeFlag;
    }   
}