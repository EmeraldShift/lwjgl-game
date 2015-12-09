package com.lithia.cs.core;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

/**
 *	Simple LWJGL class.  WIP
 */
public class Game
{
	
	public Game()
	{
		initGL();
		resizeGL();
		
		run();
	}
	
	private void initGL()
	{
	}
	
	private void resizeGL()
	{
	}
	
	private void run()
	{
		while(!Display.isCloseRequested())
		{
			// Do useful stuff here
			
			Display.update();
			Display.sync(60);
		}
		
		Display.destroy();
		System.exit(0);
	}
	
	public static void main(String[] args)
	{
		// Create display
		try
		{
			Display.setDisplayMode(Configuration.DISPLAY_MODE);
			Display.setTitle(Configuration.TITLE);
			Display.create();
		}
		catch(LWJGLException e)
		{
			Game.err(e, false);
		}
		
		new Game();
	}
	
	public static void err(Throwable e, boolean ignore)
	{
		System.out.println("Error thrown in thread " + Thread.currentThread().getName() + " : " + e.getMessage());
		if(!ignore)
		{
			System.out.println("System can no longer continue, terminating...");
			System.exit(1);
		}
	}
	
}
