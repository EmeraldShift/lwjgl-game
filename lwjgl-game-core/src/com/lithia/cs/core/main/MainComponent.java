package com.lithia.cs.core.main;

import org.lwjgl.LWJGLException;
import com.lithia.cs.core.Game;

/**
 *	Entry point of the application.  Contains static {@code main} method.
 */
public class MainComponent
{
	
	/**
	 * 	Main entry point.  Creates a {@code Game} instance and creates the OpenGL context.
	 * 	@param args Command-line arguments.  Unused.
	 */
	public static void main(String[] args)
	{
		try
		{
			Game game = new Game();
			
			game.create();
			game.run();
		}
		catch(LWJGLException e)
		{
			Game.err(e, false);
		}
	}
	
}
