package com.schemafactor.vortexserver.universe;

import com.schemafactor.vortexserver.common.Constants;

public class Cell 
{
    public enum Types { Background, Destructable, Infrastructure, Animated };
        
    private byte charCode = 0;   // Character code shown on client screen
    private byte charColor = Constants.COLOR_BLACK;   // Foreground color code shown to the client  (though will likely use a lookup table)    
    
    public Types type = Types.Background;
    
    public byte getCharCode() 
    {
        return charCode;
    }

    @Deprecated
    public byte getCharColor() 
    {
        return charColor;
    }
    
    /** Set the character cell and color values in one shot */
    public void setAttributes(int charCode, byte color, Types type)
    {
        this.charCode = (byte)(charCode & 0xFF);
        this.charColor = color;
        this.type = type;
    }
    
    /** Set the character cell and color values in one shot */
    public void setAttributes(int charCode, Types type)
    {
        this.charCode = (byte)(charCode & 0xFF);        
        this.type = type;
    }
}
