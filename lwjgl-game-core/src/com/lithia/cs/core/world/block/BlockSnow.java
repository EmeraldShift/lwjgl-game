package com.lithia.cs.core.world.block;

import org.lwjgl.util.vector.Vector4f;

import com.lithia.cs.core.util.*;

public class BlockSnow extends Block
{
	
	public Vector4f getColor()
	{
		return VectorPool.get(1.0f, 1.0f, 1.0f, 0.9f, true);
	}
	
}
