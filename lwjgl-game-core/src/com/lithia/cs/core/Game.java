package com.lithia.cs.core;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import com.lithia.cs.core.world.*;

/**
 * A simple framework for creating and utilizing an OpenGL context created by
 * LWJGL. Also contains the main game loop, and update / render processing.
 */
public class Game
{
	
	private Player player;
	private World world;
	
	/**
	 * Creates the {@code Display} to be used by the LWJGL project.
	 * 
	 * @throws LWJGLException
	 *             if creating the display fails.
	 */
	public void create() throws LWJGLException
	{
		Display.setDisplayMode(new DisplayMode(Config.WIDTH, Config.HEIGHT));
		Display.setTitle(Config.TITLE);
		Display.create();
		
		Keyboard.create();
		Mouse.create();
		Mouse.setGrabbed(true);
		
		init();
		resizeGL();
		
	}
	
	/**
	 * Configure the initial state of OpenGL and any objects present.
	 */
	private void init()
	{
		player = new Player();
		world = new World("world", Config.DEFAULT_SEED, player);
		
		glClearColor(10 / 16f, 13 / 16f, 15 / 16f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_ALPHA_TEST);
	}
	
	/**
	 * Prepare OpenGL for rendering by fitting it to our screen.
	 */
	private void resizeGL()
	{
		glViewport(0, 0, Config.WIDTH, Config.HEIGHT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		
		gluPerspective(67.0f, Config.WIDTH / (float) Config.HEIGHT, 0.1f, 1024);
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}
	
	/**
	 * Initialize the main game loop, including updating and rendering all
	 * on-screen objects.
	 */
	public void run()
	{
		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			update();
			render();
		}
		
		Display.destroy();
		System.exit(0);
	}
	
	/**
	 * Pass an update to all objects.
	 */
	private void update()
	{
		player.update();
		world.update();
	}
	
	/**
	 * Draw all objects to screen.
	 */
	private void render()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		player.render();
		world.render();
		
		Display.update();
		Display.sync(Config.FRAME_RATE);
	}
	
	/**
	 * Throw an error to the engine, dumping it to the screen and halting
	 * execution if necessary.
	 * 
	 * @param e
	 *            The exception involved in the error report.
	 * @param ignore
	 *            Whether or not to ignore the exception and continue execution.
	 */
	public static void err(Throwable e, boolean ignore)
	{
		System.out.println("Error thrown in thread " + Thread.currentThread().getName() + " : " + e.getMessage());
		if (!ignore)
		{
			System.out.println("System can no longer continue, terminating...");
			System.exit(1);
		}
	}
	
}
