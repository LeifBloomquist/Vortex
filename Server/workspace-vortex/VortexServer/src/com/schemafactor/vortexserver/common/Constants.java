package com.schemafactor.vortexserver.common;


public class Constants 
{
    // Server version
    public static final double VERSION        = 0.013;
    
    // Game-specific constants
    public static final int TICK_TIME         = 50; // milliseconds
    
    // Screen constants
    public static final int SCREEN_WIDTH      = 40;
    public static final int SCREEN_HEIGHT     = 25;
    public static final int SCREEN_SIZE       = SCREEN_WIDTH*SCREEN_HEIGHT;
    public static final int PIXELSPERCELL     = 8;
    public static final int UNIVERSE_SIZE     = 50;  // In screens x/y.  Was 100.  
    
    // Position on screen
    public static final int PLAYER_XPOS       = 172;  // 24+(320/2)-(24/2)
    public static final int PLAYER_YPOS       = 140;  // 50+(200/2)-(21/2);
    
    // C64 colors
    public static final byte COLOR_BLACK      = 0;
    public static final byte COLOR_WHITE      = 1;
    public static final byte COLOR_RED        = 2;
    public static final byte COLOR_CYAN       = 3;
    public static final byte COLOR_PURPLE     = 4;
    public static final byte COLOR_GREEN      = 5;
    public static final byte COLOR_BLUE       = 6;
    public static final byte COLOR_YELLOW     = 7;
    public static final byte COLOR_ORANGE     = 8;
    public static final byte COLOR_BROWN      = 9;
    public static final byte COLOR_LIGHTRED   = 10;
    public static final byte COLOR_GREY1      = 11;
    public static final byte COLOR_GREY2      = 12;
    public static final byte COLOR_LIGHTGREEN = 13;
    public static final byte COLOR_LIGHTBLUE  = 14;
    public static final byte COLOR_GREY3      = 15;
    
    // Packet types    
    public static final byte CLIENT_ANNOUNCE  = 1;
    public static final byte CLIENT_UPDATE    = 2;
    
    public static final byte PACKET_ANN_REPLY = (byte) 128;
    public static final byte PACKET_UPDATE    = (byte) 129;

    // Entity Counts and other game parameters
	public static final int ASTEROID_COUNT = 200;    
}
