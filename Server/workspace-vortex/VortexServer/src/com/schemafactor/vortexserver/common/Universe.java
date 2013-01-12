package com.schemafactor.vortexserver.common;

import java.awt.Point;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

public class Universe 
{
	private byte[][] universeMapCells = null;   // Matrix of character cells
	private byte[][] universeMapColor = null;   // Matrix of character colors
	
	private long Xsize = -1;               // Pixels
	private long Ysize = -1;               // Pixels
	
	Random generator = new Random();
	
	Vector<Point> planetoids = new Vector<Point>();
	
	public Universe(int size)
	{
		universeMapCells = new byte[size*Constants.SCREEN_WIDTH][size*Constants.SCREEN_HEIGHT];
		universeMapColor = new byte[size*Constants.SCREEN_WIDTH][size*Constants.SCREEN_HEIGHT];
		
		Xsize = size*Constants.SCREEN_WIDTH*Constants.PIXELSPERCELL;
		Ysize = size*Constants.SCREEN_HEIGHT*Constants.PIXELSPERCELL;
		
		for (byte[] row : universeMapCells)
		{
			Arrays.fill(row, (byte)32);   // Blank space, change this with custom char set
		}
		
		for (byte[] row : universeMapColor)
		{
			Arrays.fill(row, (byte)Constants.COLOR_BLACK);  
		}
		
		
		// Stars
		for (int t=1; t< 100000; t++)
		{
			int randx=generator.nextInt(size*Constants.SCREEN_WIDTH);
			int randy=generator.nextInt(size*Constants.SCREEN_HEIGHT);
			
			universePoke(randx, randy, 46, Constants.COLOR_WHITE);	
		}
		
		
		// Put some simple planetoids into the universe
		for (int t=1; t < 5000; t++)
		{
			int randx=10+generator.nextInt((size*Constants.SCREEN_WIDTH )-20);
			int randy=10+generator.nextInt((size*Constants.SCREEN_HEIGHT)-20);			
			
			planetoids.add(new Point(randx,randy));
			
			universePoke(randx-1, randy-1, 85, Constants.COLOR_GREY2);
			universePoke(randx+0, randy-1, 68, Constants.COLOR_GREY2);
			universePoke(randx+1, randy-1, 73, Constants.COLOR_GREY2);
			
			universePoke(randx-1, randy+0, 71, Constants.COLOR_GREY2);
			universePoke(randx+0, randy+0, (byte) (t % 255), Constants.COLOR_LIGHTBLUE);
			universePoke(randx+1, randy+0, 72, Constants.COLOR_GREY2);		
			
			universePoke(randx-1, randy+1, 74, Constants.COLOR_GREY2);
			universePoke(randx+0, randy+1, 70, Constants.COLOR_GREY2);
			universePoke(randx+1, randy+1, 75, Constants.COLOR_GREY2);
			
			// Planets
			for (int p=1; p < generator.nextInt(15); p++)
			{
				int orbit=2+generator.nextInt(3);
				double dir=2*Math.PI*generator.nextDouble();
				
				int diffx=(int) Math.round(orbit*Math.sin(dir));
				int diffy=(int) Math.round(orbit*Math.cos(dir));
				
				universePoke(randx+diffx, randy+diffy, 81, Constants.COLOR_BLUE);	
			}
		}
		
		
		// Experiment - huge asteroid belt
		for (int a=1; a < 100000; a++)
		{
			int orbit=500+generator.nextInt(20);
			double dir=2*Math.PI*generator.nextDouble();
			
			int diffx=(int) Math.round(orbit*Math.sin(dir));
			int diffy=(int) Math.round(orbit*Math.cos(dir));
			
			universePoke(1000+diffx, 1000+diffy, 42, Constants.COLOR_BROWN);	
		}		
		
		
		// Put some standalone asteroids into the universe
		for (int t=1; t< 10000; t++)
		{
			int randx=generator.nextInt(size*Constants.SCREEN_WIDTH);
			int randy=generator.nextInt(size*Constants.SCREEN_HEIGHT);
			
			universePoke(randx, randy, 87, Constants.COLOR_GREY1);	
		}
		
		// Put some powerups into the universe
		for (int t=1; t< 1000; t++)
		{
			int randx=generator.nextInt(size*Constants.SCREEN_WIDTH);
			int randy=generator.nextInt(size*Constants.SCREEN_HEIGHT);
			
			universePoke(randx, randy, 90, Constants.COLOR_YELLOW);	
		}
		
		// Put a special marker at the origin
		universePoke(0, 0, 91, Constants.COLOR_LIGHTRED);
	}
	
	public void update()
	{
		for (Point p : planetoids)
		{
			universePoke(p.x, p.y, (byte) generator.nextInt(255), Constants.COLOR_LIGHTBLUE);
		}
	}
	
	
	/** Set the character cell and color values in one shot */
	public void universePoke(int x, int y, int code, byte color)
	{
		universeMapCells[x][y]=(byte) (code  & 0xFF);
		universeMapColor[x][y]=color;		
	}
	
	/** Get a full screen's worth of cell data, with wraparound */
	public byte[] getScreen(long x, long y)
	{
		return getScreenArray(x, y, universeMapCells);
	}
	
	/** Get a full screen's worth of color data, with wraparound */
	public byte[] getScreenColor(long x, long y)
	{
		return getScreenArray(x, y, universeMapColor);
	}
	
	/** Helper function for the above */
	private byte[] getScreenArray(long x, long y, byte[][] array)
	{
		byte[] screen = new byte[Constants.SCREEN_SIZE];
		
		int index=0;
		
		// Faster way to do this?
		for (int yy=0; yy < Constants.SCREEN_HEIGHT; yy++)
		{
            for (int xx=0; xx < Constants.SCREEN_WIDTH; xx++)
			{			
			   screen[index] = JavaTools.getArrayWrap(array, x+xx, y+yy);
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
