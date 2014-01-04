package com.schemafactor.vortexserver.universe;

import java.awt.Point;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import com.schemafactor.vortexserver.common.Constants;
import com.schemafactor.vortexserver.common.JavaTools;

public class Universe 
{
    private Cell[][] universeMapCells = null;   // Matrix of cells
    
    private long Xsize = -1;               // Pixels
    private long Ysize = -1;               // Pixels
    
    Vector<Point> planetoids = new Vector<Point>();
    
    public Universe(int size)
    {
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
            
            universeMapCells[randx][randy].setAttributes(starchars[JavaTools.generator.nextInt(starchars.length)], Cell.Types.Background);    
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
            
            universeMapCells[randx+0][randy+0].setAttributes(132, Constants.COLOR_GREY1, Cell.Types.Destructable);    
            universeMapCells[randx+1][randy+0].setAttributes(137, Constants.COLOR_GREY1, Cell.Types.Destructable);
            universeMapCells[randx+0][randy+1].setAttributes(163, Constants.COLOR_GREY1, Cell.Types.Destructable);
            universeMapCells[randx+1][randy+1].setAttributes(169, Constants.COLOR_GREY1, Cell.Types.Destructable);
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
}
