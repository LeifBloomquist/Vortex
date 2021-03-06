/**
 * 
 */
package com.schemafactor.vortexserver.webserver;

import java.awt.Color;

import com.schemafactor.vortexserver.common.JavaTools;
import com.schemafactor.vortexserver.entities.Entity;

/**
 * @author Leif
 *
 * This class is a simple representation of an Entity for purposes of JSON and web display.
 */
public class SimpleEntity 
{
	public long x;      // These are pixels, and refers to the top-left corner of the object (sprite, etc.)
	public long y;
	public String name;
	public String color;
	
	public SimpleEntity(Entity original)
	{
		this.x = Math.round( original.getXpos() );
		this.y = Math.round( original.getYpos() );
		this.name = original.getDescription();
		this.color = JavaTools.toHexString( original.getRGBColor() );		 
	}
}
