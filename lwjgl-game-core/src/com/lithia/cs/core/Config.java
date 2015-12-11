package com.lithia.cs.core;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.*;

/**
 * A simple configuration file to contain a few hard-coded values used by the
 * application.
 */
public class Config
{
	
	/**
	 * The width of the application window.
	 */
	public static int WIDTH = 640;
	
	/**
	 * The height of the application window.
	 */
	public static int HEIGHT = 480;
	
	/**
	 * The {@code DisplayMode} used by LWJGL to create the {@code Display}.
	 */
	public static final DisplayMode DISPLAY_MODE = new DisplayMode(WIDTH, HEIGHT);
	
	/**
	 * The title of the application, seen at the top of the application window.
	 */
	public static final String TITLE = "Voxel Game";
	
	/**
	 * The size of the game world, in chunks along each axis.
	 */
	public static final Vector3f WORLD_SIZE = new Vector3f(16, 1, 16);
	
}
