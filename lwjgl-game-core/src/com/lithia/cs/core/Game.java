package com.lithia.cs.core;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

/**
 *	A simple framework for creating and utilizing an OpenGL context created by
 *	LWJGL.  Also contains the main game loop, and update / render processing.
 */
public class Game
{
	
	/**
	 * 	Creates the {@code Display} to be used by the LWJGL project.
	 * 	@throws LWJGLException if creating the display fails.
	 */
	public void create() throws LWJGLException
	{
		Display.setDisplayMode(Config.DISPLAY_MODE);
		Display.setTitle(Config.TITLE);
		Display.create();
		
		Keyboard.create();
		Mouse.create();
		Mouse.setGrabbed(true);
		
		init();
		resizeGL();
		
	}
	
	/**
	 * 	Configure the initial state of OpenGL and any objects present.
	 */
	private void init()
	{
	}
	
	/**
	 * 	Prepare OpenGL for rendering by fitting it to our screen.
	 */
	private void resizeGL()
	{
		glViewport(0, 0, Config.WIDTH, Config.HEIGHT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		
		gluPerspective(67.0f, Config.WIDTH / Config.HEIGHT, 0.1f, 1024);
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}
	
	/**
	 * 	Initialize the main game loop, including updating and rendering all on-screen objects.
	 */
	public void run()
	{
		while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			update();
			render();
		}
		
		Display.destroy();
		System.exit(0);
	}
	
	/**
	 * 	Pass an update to all objects.
	 */
	private void update()
	{
		
	}
	
	/**
	 * 	Draw all objects to screen.
	 */
	private void render()
	{
		// TODO something
		
		Display.update();
		Display.sync(60);
	}
	
	/**
	 * 	Throw an error to the engine, dumping it to the screen and halting execution if necessary.
	 * 	@param e The exception involved in the error report.
	 * 	@param ignore Whether or not to ignore the exception and continue execution.
	 */
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
