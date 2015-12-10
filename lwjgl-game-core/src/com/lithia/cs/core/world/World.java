package com.lithia.cs.core.world;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

import com.lithia.cs.core.*;

public class World extends Renderable
{

	private int displayList = -1;
	private long time = Sys.getTime();
	
	/**
	 * 	Simple test of rendering capabilities featuring: The infinitely receding square!
	 * 	TODO: Implement an efficient method of rendering millions of voxels here...
	 */
	public void render()
	{
		GL11.glTranslatef(0, 0, -(Sys.getTime() - time) / 10000.0f);
		
		if(displayList == -1)
		{
			displayList = glGenLists(1);
			glNewList(displayList, GL_COMPILE);

			GL11.glBegin(GL11.GL_QUADS);
			
			GL11.glColor3f(1.0f, 0.0f, 0.0f);
			GL11.glVertex3f(-0.5f, -0.5f, -2);
			GL11.glColor3f(0.0f, 0.0f, 1.0f);
			GL11.glVertex3f(0.5f, -0.5f, -2);
			GL11.glColor3f(0.0f, 1.0f, 1.0f);
			GL11.glVertex3f(0.5f, 0.5f, -2);
			GL11.glColor3f(1.0f, 1.0f, 0.0f);
			GL11.glVertex3f(-0.5f, 0.5f, -2);
			
			GL11.glEnd();
			glEndList();
		}
		
		GL11.glCallList(displayList);
	}

	public void update()
	{
	}
	
}
