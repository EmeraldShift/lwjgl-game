package com.lithia.cs.core.world.block;

import org.lwjgl.util.vector.*;

import com.lithia.cs.core.util.*;

public class BlockDirt extends Block
{
	
	public Vector4f getColor()
	{
		return VectorPool.get(0.6f, 0.46f, 0.33f, 1.0f, true);
	}
	
}
