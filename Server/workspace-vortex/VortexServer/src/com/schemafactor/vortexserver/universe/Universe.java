package com.schemafactor.vortexserver.universe;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Entity;

public class Universe 
{
    // Universe is a Singleton
    private static final Universe instance = new Universe();
 
    private Universe() {}
 
    public static Universe getInstance() 
    {
        return instance;
    }
        
    private Cell[][] universeMapCells = null;   // Matrix of cells
    
    private long Xsize = -1;               // Pixels
    private long Ysize = -1;               // Pixels
    
    Vector<Point> planetoids = new Vector<Point>();
    
    List<Entity> allEntities = null;
    
    /**
     * Thread-safe queue of new entities that have been created (and added to the allEntities list)
     */
    public BlockingQueue<Entity> newEntities = new ArrayBlockingQueue<Entity>(1000);

    // Some run-time statistics for monitoring
    public double avg_ms = 0d;
    public double avg_cpu = 0d;
    
    /**
     * 
     * @param size
     * @param allEntities
     */
    public void Create(int size, List<Entity> allEntities)
    {
        // Save the entities
        this.allEntities = allEntities;
        
        // Create array        
        universeMapCells = new Cell[size*Constants.SCREEN_WIDTH][size*Constants.SCREEN_HEIGHT];        
        
        // Instantiate
        for (int x=0; x < size*Constants.SCREEN_WIDTH; x++)
        {
            for (int y=0; y < size*Constants.SCREEN_HEIGHT; y++)
            {
                universeMapCells[x][y] = new Cell();
            }
        }
        
        Xsize = size*Constants.SCREEN_WIDTH*Constants.PIXELSPERCELL;
        Ysize = size*Constants.SCREEN_HEIGHT*Constants.PIXELSPERCELL;
        
        // Stars
        
        
        int[] starchars = {2, 7, 10, 11};
        
        for (int t=1; t<100000; t++)
        {
            int randx=JavaTools.generator.nextInt(size*Constants.SCREEN_WIDTH);
            int randy=JavaTools.generator.nextInt(size*Constants.SCREEN_HEIGHT);            
            
            universeMapCells[randx][randy].setAttributes(starchars[JavaTools.generator.nextInt(starchars.length)], Constants.COLOR_WHITE, Cell.Types.Background);    
        }
        
        
        // Put some simple planetoids into the universe
        
        
        
        for (int t=1; t < 5000; t++)
        {
            int randx=10+JavaTools.generator.nextInt((size*Constants.SCREEN_WIDTH )-20);
            int randy=10+JavaTools.generator.nextInt((size*Constants.SCREEN_HEIGHT)-20);            
            
            planetoids.add(new Point(randx,randy));
            
            universeMapCells[randx-1][randy-1].setAttributes(129, Constants.COLOR_GREY2, Cell.Types.Destructable);
            universeMapCells[randx+0][randy-1].setAttributes(130, Constants.COLOR_GREY2, Cell.Types.Destructable);
            universeMapCells[randx+1][randy-1].setAttributes(136, Constants.COLOR_GREY2, Cell.Types.Destructable);
            
            universeMapCells[randx-1][randy+0].setAttributes(161, Constants.COLOR_GREY2, Cell.Types.Destructable);
            universeMapCells[randx+0][randy+0].setAttributes(14, Cell.Types.Destructable);
            universeMapCells[randx+1][randy+0].setAttributes(141, Constants.COLOR_GREY2, Cell.Types.Destructable);        
            
            universeMapCells[randx-1][randy+1].setAttributes(164, Constants.COLOR_GREY2, Cell.Types.Destructable);
            universeMapCells[randx+0][randy+1].setAttributes(165, Constants.COLOR_GREY2, Cell.Types.Destructable);
            universeMapCells[randx+1][randy+1].setAttributes(171, Constants.COLOR_GREY2, Cell.Types.Destructable);
            
            // Planets
            for (int p=1; p < JavaTools.generator.nextInt(15); p++)
            {
                int orbit=2+JavaTools.generator.nextInt(3);
                double dir=2*Math.PI*JavaTools.generator.nextDouble();
                
                int diffx=(int) Math.round(orbit*Math.sin(dir));
                int diffy=(int) Math.round(orbit*Math.cos(dir));
                
                universeMapCells[randx+diffx][randy+diffy].setAttributes(14, Constants.COLOR_BLUE, Cell.Types.Destructable);    
            }
        }
        
        
        // Experiment - huge asteroid belt
        int[] rockchars = {145, 146, 147, 148, 154, 157};
        
        for (int a=1; a < 100000; a++)
        {
            int orbit=500+JavaTools.generator.nextInt(20);
            double dir=2*Math.PI*JavaTools.generator.nextDouble();
            
            int diffx=(int) Math.round(orbit*Math.sin(dir));
            int diffy=(int) Math.round(orbit*Math.cos(dir));
            
            universeMapCells[1000+diffx][1000+diffy].setAttributes(rockchars[JavaTools.generator.nextInt(rockchars.length)], Constants.COLOR_BROWN, Cell.Types.Destructable);    
        }        
        
        
        // Put some standalone asteroids into the universe
        for (int t=1; t< 10000; t++)
        {
            int randx=JavaTools.generator.nextInt(size*Constants.SCREEN_WIDTH-1);
            int randy=JavaTools.generator.nextInt(size*Constants.SCREEN_HEIGHT-1);
            
            universeMapCells[randx+0][randy+0].setAttributes(132, Constants.COLOR_BROWN, Cell.Types.Destructable);    
            universeMapCells[randx+1][randy+0].setAttributes(137, Constants.COLOR_BROWN, Cell.Types.Destructable);
            universeMapCells[randx+0][randy+1].setAttributes(163, Constants.COLOR_BROWN, Cell.Types.Destructable);
            universeMapCells[randx+1][randy+1].setAttributes(169, Constants.COLOR_BROWN, Cell.Types.Destructable);
        }
        
        /*
        // Put some powerups into the universe
        for (int t=1; t< 1000; t++)
        {
            int randx=JavaTools.generator.nextInt(size*Constants.SCREEN_WIDTH);
            int randy=JavaTools.generator.nextInt(size*Constants.SCREEN_HEIGHT);
            
            universeMapCells[randx+0][randy+0].setAttributes(90, Constants.COLOR_YELLOW, Cell.Types.Background);
            
        }
        */
        
        // Put a special marker at the origin
        universeMapCells[0][0].setAttributes(15, Constants.COLOR_LIGHTRED, Cell.Types.Background);
    
    }
    
    public void update()
    {
        for (Point p : planetoids)
        {
            universeMapCells[p.x][p.y].setAttributes(JavaTools.generator.nextInt(255), Constants.COLOR_LIGHTBLUE, Cell.Types.Infrastructure);
        }
    }
    
    /** Get a full screen's worth of cell data, with wraparound */
    public byte[] getScreen(long x, long y)
    {
        return getScreenArray(x, y, universeMapCells);
    }
    
    /** Helper function for the above */
    private byte[] getScreenArray(long x, long y, Cell[][] array)
    {
        byte[] screen = new byte[Constants.SCREEN_SIZE];
        
        int index=0;
        
        // Faster way to do this?
        for (int yy=0; yy < Constants.SCREEN_HEIGHT; yy++)
        {
            for (int xx=0; xx < Constants.SCREEN_WIDTH; xx++)
            {            
               Cell c = (Cell)JavaTools.getArrayWrap(array, x+xx, y+yy);
               screen[index] = c.getCharCode();
               index++;
            }
        }
        
        return screen;
    }    

    public long getXsize() 
    {
        return Xsize;
    }

    public long getYsize() 
    {
        return Ysize;
    }  
    
    public Color getCellColor(int x, int y)
    {
        byte cbmcolor = universeMapCells[x][y].getCharColor();
        
        Color col = Color.BLACK;
        
        switch (cbmcolor)
        {            
            // C64 colors
            case Constants.COLOR_BLACK:      col = Color.BLACK;  break;
            case Constants.COLOR_WHITE:      col = Color.WHITE;  break;     
            case Constants.COLOR_RED:        col = Color.RED;  break;       
            case Constants.COLOR_CYAN:       col = Color.CYAN;  break;      
            case Constants.COLOR_PURPLE:     col = Color.MAGENTA;  break;    
            case Constants.COLOR_GREEN:      col = Color.GREEN;  break;     
            case Constants.COLOR_BLUE:       col = Color.BLUE;  break;      
            case Constants.COLOR_YELLOW:     col = Color.YELLOW;  break;    
            case Constants.COLOR_ORANGE:     col = Color.ORANGE;  break;    
            case Constants.COLOR_BROWN:      col = new Color(150, 75, 0);  break;     
            case Constants.COLOR_LIGHTRED:   col = Color.PINK;  break;  
            case Constants.COLOR_GREY1:      col = Color.DARK_GRAY;  break;     
            case Constants.COLOR_GREY2:      col = Color.GRAY;  break;     
            case Constants.COLOR_LIGHTGREEN: col = new Color(100, 255, 100);  break;
            case Constants.COLOR_LIGHTBLUE:  col = new Color(100, 100, 255);  break; 
            case Constants.COLOR_GREY3:      col = Color.LIGHT_GRAY;  break;     
            default:                         col = Color.BLACK; break;        
        }
        
        return col;        
    }

    public List<Entity> getEntities()
    {
        return allEntities;
    }
    
    /** Get a list of all entities matching the given type, excluding the one who is doing the inquiry (who). */
    public List<Entity> getEntities(Entity who, Entity.eTypes type)
    {
        List<Entity> allOfType = new ArrayList<Entity>();
        
        for (Entity e : allEntities)
        {
            if (who == e) continue;
            
            if ((e.getType() == type) && !(e.removeMe()))
            {
                allOfType.add(e);                        
            }
        }        
        
        return allOfType;
    }
    
    /** Get a list of all entities within a certain radius, excluding the one who is doing the inquiry (who). */
    public List<Entity> getEntities(Entity who, double range)
    {
        List<Entity> allInRange = new ArrayList<Entity>();
        
        for (Entity e : allEntities)
        {
            if (who == e) continue;
            
            if ((who.distanceTo(e) <= range)  && !(e.removeMe()))
            {
                allInRange.add(e);                        
            }
        }        
        
        return allInRange;
    }
}
