package com.schemafactor.vortexserver.common;


public class Constants 
{
	// Game-specific constants
	public static int TICK_TIME         = 50; // milliseconds
	
	// Screen constants
	public static int SCREEN_WIDTH      = 40;
	public static int SCREEN_HEIGHT     = 25;
	public static int SCREEN_SIZE       = SCREEN_WIDTH*SCREEN_HEIGHT;
	public static int PIXELSPERCELL     = 8;   
	
	// C64 colors
	public static byte COLOR_BLACK      = 0;
	public static byte COLOR_WHITE      = 1;
	public static byte COLOR_RED        = 2;
	public static byte COLOR_CYAN       = 3;
	public static byte COLOR_PURPLE     = 4;
	public static byte COLOR_GREEN      = 5;
	public static byte COLOR_BLUE       = 6;
	public static byte COLOR_YELLOW     = 7;
	public static byte COLOR_ORANGE     = 8;
	public static byte COLOR_BROWN      = 9;
	public static byte COLOR_LIGHTRED   = 10;
	public static byte COLOR_GREY1      = 11;
	public static byte COLOR_GREY2      = 12;
	public static byte COLOR_LIGHTGREEN = 13;
	public static byte COLOR_LIGHTBLUE  = 14;
	public static byte COLOR_GREY3      = 15;
	
	// packet types
	public static byte PACKET_SCREEN    = 100;
	public static byte PACKET_COLOR     = 101;
	
	// Position on screen
	public static int CLIENT_YPOS       = 100;
	public static int CLIENT_XPOS       = 160;
	
}
