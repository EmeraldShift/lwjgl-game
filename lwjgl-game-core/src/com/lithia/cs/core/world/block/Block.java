package com.lithia.cs.core.world.block;

import org.lwjgl.util.vector.Vector4f;

/**
 * Represents a block in the voxel world. Each block has a defined type, which
 * is one of the available types in the {@code blocks} array. Blocks may be
 * removed by setting their type to {@code 0}, or added by changing a block with
 * type {@code 0} to a positive integer corresponding with one of the
 * aforementioned types. In the case that a block type cannot be determined, the
 * {@code nil} block class will be used instead.
 *
 */
public abstract class Block
{
	
	public static enum SIDE
	{
		LEFT, RIGHT, TOP, BOTTOM, FRONT, BACK;
	}
	
	private static Block[] blocks = { new BlockAir(), new BlockDirt(), new BlockGlass() };
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
	 * Returns the color of the block type.
	 * @return A vector representing the color
	 */
	public Vector4f getColor()
	{
		return new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
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
		Vector4f color = getColor();
		
		if (side == SIDE.TOP) return new Vector4f(color.x * 0.9f, color.y * 0.9f, color.z * 0.9f, color.w * 1.0f);
		if (side == SIDE.LEFT || side == SIDE.RIGHT) return new Vector4f(color.x * 0.75f, color.y * 0.75f, color.z * 0.75f, color.w * 1.0f);
		if (side == SIDE.FRONT || side == SIDE.BACK) return new Vector4f(color.x * 0.5f, color.y * 0.5f, color.z * 0.5f, color.w * 1.0f);
		
		return new Vector4f(color.x * 0.3f, color.y * 0.3f, color.z * 0.3f, color.w * 1.0f);
	}
	
	public boolean isBlockTransparent()
	{
		return getColor().w != 1.0f;
	}
	
	public boolean isBlockInvisible()
	{
		return false;
	}
}
