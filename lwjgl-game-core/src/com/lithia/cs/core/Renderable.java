package com.lithia.cs.core;

import org.lwjgl.util.vector.*;

/**
 * Represents an object which can be drawn in 3-dimensional space. All
 * {@code Renderable}s have a default position of (0, 0, 0}, and must implement
 * the {@code render} method.
 */
public abstract class Renderable
{
	protected Vector3f position = new Vector3f();
	
	/**
	 * Draws the {@code Renderable} to the game screen.
	 */
	public abstract void render();
	
	/**
	 * Updates the object, based on implementation.
	 */
	public void update()
	{
		
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
}
