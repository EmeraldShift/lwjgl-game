package com.lithia.cs.core.world.block;

import org.lwjgl.util.vector.*;

public abstract class Block
{
	
	public static enum SIDE
	{
		LEFT, RIGHT, TOP, BOTTOM, FRONT, BACK;
	}
	
	private static Block[] blocks = { new BlockAir(), new BlockDirt() };
	private static Block nil = new BlockNil();
	
	public static Block getBlock(int type)
	{
		Block b = null;
		
		try
		{
			b = blocks[type];
		}
		catch (Exception e)
		{
			b = nil;
		}
		
		return b;
	}
	
	/**
	 * Calculate color offset for each side. (otherwise it'd all be solid white
	 * with no shading or definition.
	 * 
	 * @param side
	 *            The side of the voxel for which to calculate
	 * @return The color offset.
	 */
	public Vector4f getColorOffsetFor(SIDE side)
	{
		if(side == SIDE.TOP) return new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);
		if(side == SIDE.LEFT || side == SIDE.RIGHT)  return new Vector4f(0.6f, 0.6f, 0.6f, 1.0f);
		if(side == SIDE.FRONT || side == SIDE.BACK)  return new Vector4f(0.4f, 0.4f, 0.4f, 1.0f);
		
		return new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);
	}
	
	public boolean isBlockInvisible()
	{
		return false;
	}
}
