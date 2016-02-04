package com.schemafactor.vortexserver.entities;

import java.awt.Color;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.universe.Universe;

public abstract class Entity 
{    
   public static enum eTypes {NONE, HUMAN_PLAYER, XACOR, XEEKER, XLORS, ASTEROID, TORPEDO}
   protected eTypes myType = eTypes.NONE;
    
   protected String description;   
   
   protected double Xpos = -1;      // These are pixels, and refers to the top-left corner of the object (sprite, etc.)
   protected double Ypos = -1;
   
   // Maximum speed
   protected double max_speed = 0.0;    
   protected double Xspeed = 0.0; 
   protected double Yspeed = 0.0;
   
   protected byte spriteBase  = 0;   
   protected byte spriteNum   = 0;         // This is the offset from SPRITE_BASE, *not* the selected memory bank.  (Client handles this)
   protected byte spriteColor = Constants.COLOR_BLACK;
   
   protected Universe universe=null;
   
   /** Flag that this entity is to be removed at the end of this update cycle.  true=remove */
   protected boolean removeMeFlag = false;
     
   /** Creates a new instance of Entity */
   public Entity(String description, eTypes type, int startX, int startY)
   {
       this.description = new String(description);
       this.myType = type;
       Xpos = startX;
       Ypos = startY;
       
       this.universe = Universe.getInstance();       
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
   
   abstract public void update(); 
   
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
   public double getXspeed()
   {
       return Xspeed;
   }
   
   public double getYspeed()
   {
       return Yspeed;
   }

   public String getDescription() 
   {
       return description;
   }

   public eTypes getType() 
   {
      return myType;
   }   
   
   /** Return Color (C64 Colors) */
   public byte getColor()
   {
       return spriteColor;
   }
   
   /** Return Color (RGB Colors) */
   public Color getRGBColor()
   {
	   switch (spriteColor)
	   {
	       case Constants.COLOR_RED:
	           return Color.RED;                  
        
	       case Constants.COLOR_GREEN:
	    	   return  Color.GREEN;                    
	           
	       case Constants.COLOR_GREY1:
	       case Constants.COLOR_GREY2:
	       case Constants.COLOR_GREY3:
	    	   return Color.LIGHT_GRAY;  
	       
	       case Constants.COLOR_YELLOW:
	           return Color.YELLOW;                    
	           
	       case Constants.COLOR_WHITE:
	           return Color.WHITE;                    
	           
	       case Constants.COLOR_BLACK:
	           return Color.DARK_GRAY;                    
	           
	       case Constants.COLOR_CYAN:
	           return Color.CYAN;                    
	           
	       case Constants.COLOR_LIGHTBLUE:
	       case Constants.COLOR_BLUE:
	    	   return Color.BLUE;                    
	           
	       default:
	    	   return Color.PINK;                    
	   }
   }
	   
   /** Return Sprite# */
   public byte getSpriteNum()
   {
      return (byte)(spriteBase+spriteNum); 
   }   
   
   // Helper function to get distance to another Entity
   public double distanceTo(Entity target)
   {   
       if (target == null)
       {
           return Double.MAX_VALUE;
       }
       
       return Math.sqrt( Math.pow((this.Xpos - target.getXpos()), 2) + Math.pow((this.Ypos - target.getYpos()), 2)); 
   }
   
   // Helper function to get angle to another Entity
   protected double angleTo(Entity target)
   {          
       double xdist = target.getXpos() - this.Xpos;
       double ydist = target.getYpos() - this.Ypos;    
       
       // TODO: Wrapping handling needed here
       
       double angle2 = Math.atan2(-ydist, xdist); // Negative here because our y-axis is inverted
       return angle2;
   }

    public boolean removeMe() 
    {        
        return removeMeFlag;
    }   
    
    /** 
     * @return Current angle in radians
     */
    protected double getAngle() 
    {
        double angle = Math.atan2(-Yspeed, Xspeed); // Negative here because our y-axis is inverted
        return angle;
    }
    
    protected void fireTorpedo() 
    {
        Torpedo t = new Torpedo(this);        
        
        try
        {
            universe.newEntities.add(t);           
        }
        catch (Exception e)
        {
            JavaTools.printlnTime( "EXCEPTION firing torpedo: " + JavaTools.getStackTrace(e) );
        }
    }
}