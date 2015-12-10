package com.lithia.cs.core;

import org.lwjgl.util.vector.*;

public abstract class Renderable
{
	private Vector3f position = new Vector3f();
	
	public abstract void update();
	public abstract void render();
	
	public Vector3f getPosition()
	{
		return position;
	}
}
