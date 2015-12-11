package com.lithia.cs.core;

import org.lwjgl.util.vector.*;

public abstract class Renderable
{
	protected Vector3f position = new Vector3f();
	
	/**
	 * Draws the {@code Renderable} to the game screen.
	 */
	public abstract void render();
	
	/**
	 * Implement this {@code update} method to add functionality for updating the instance between frames.
	 */
	public void update()
	{
		
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
}
