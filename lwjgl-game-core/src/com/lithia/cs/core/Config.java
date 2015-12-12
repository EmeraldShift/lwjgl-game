package com.lithia.cs.core;

import org.lwjgl.util.vector.*;

/**
 * A simple configuration file to contain a few hard-coded values used by the
 * application.
 */
public class Config
{
	
	/**
	 * The default width of the application window.
	 */
	public static int WIDTH = 640;
	
	/**
	 * The default height of the application window.
	 */
	public static int HEIGHT = 480;
	
	/**
	 * The title of the application, seen at the top of the application window.
	 */
	public static final String TITLE = "Voxel Game";
	
	/**
	 * The size of the game world, in chunks along each axis.
	 */
	public static final Vector3f WORLD_SIZE = new Vector3f(24, 1, 24);
	
	/**
	 * The number of times per second the game will attempt to redraw the
	 * screen.
	 */
	public static final int FRAME_RATE = 60;
	
	/**
	 * The default seed used to generate the world (for testing purposes, to get
	 * consistent world generation
	 */
	public static final String DEFAULT_SEED = "world";
	
}
