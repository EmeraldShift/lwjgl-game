package com.lithia.cs.core.world;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.*;
import com.lithia.cs.core.*;

/**
 * The primary division of the {@code World}. Separates blocks into
 * separately-handled render queues using an update queue to handle
 * modifications such as block addition, removal, etc.
 */
public class Chunk extends Renderable
{
	
	/**
	 * Represents the number of blocks that can be stored in each chunk along
	 * each axis. Larger values reduce GPU calls when rendering, but increase
	 * time needed to update the chunk when modified, which can be undesirable
	 * when breaking or building blocks very rapidly and / or between multiple
	 * chunks.
	 */
	public static final int CHUNK_SIZE = 16;
	
	private int displayList = -1;
	
	public boolean update = true;
	private boolean generate = true;
	
	/**
	 * The parent world is the encapsulating {@code World} instance of which
	 * this chunk is a part. This hook can be used to access other chunks,
	 * world-manipulation methods, or other necessary utilities for management
	 * of objects outside of the current chunk.
	 */
	private World parent;
	
	/**
	 * Store the data type of the block at each location. The eventual objective
	 * of this class is to store as little information as possible, to make
	 * saving the world to a file much more effficient.
	 */
	private int[][][] blocks;
	
	/**
	 * Initializes a chunk, supplying it with its position in the world as well
	 * as a hook back to the "parent" world.
	 */
	public Chunk(World parent, Vector3f position)
	{
		this.position = position;
		this.parent = parent;
		
		blocks = new int[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
	}
	
	/**
	 * Calls the chunk's display list, drawing it to the screen.
	 */
	public void render()
	{
		glCallList(displayList); // TODO Actually fill this list with data
	}
	
	/**
	 * Generates the initial state of the chunk, including world generation and
	 * other factors supplied by the chunk's {@code Generator}s.
	 *
	 * @return {@code true} if the chunk was successfully generated,
	 *         {@code false} if the chunk was already generated, or if the
	 *         generation failed.
	 */
	public boolean generate()
	{
		if (generate)
		{
			// TODO create a "chunk generator" to create (hopefully
			// realistic-ish) terrain
			return true;
		}
		
		return false;
	}
	
	/**
	 * Iterate through the chunk's blocks and generate the vertices for each.
	 */
	public void generateVertexArrays()
	{
		
	}
	
	/**
	 * Generate vertices for the block at the given location.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void generateBlockVertices(int x, int y, int z)
	{
		
	}
	
	/**
	 * Rebuild the OpenGL display list used to render the chunk.
	 */
	public void generateDisplayList()
	{
		
	}
	
	/*
	 * Alrighty, so here's the plan. There's a really elegant way to do this
	 * whole chunk updating magic, and here it is:
	 * 
	 * 1. Use a "needs update" flag to tell the renderer to rebuild chunk.
	 * 
	 * 2. Use the World's update thread to loop and rebuild vertex arrays.
	 * 
	 * 3. Use the World's game-loop update method (while synchronized so as not
	 * to get caught half-way updating when the engine calls to render) to
	 * recreate the display list (effectively pushing the chunk updates into the
	 * actual object being rendered.)
	 * 
	 * Only problem is, I haven't used VBOs and Display Lists in a long time, so
	 * I have no idea how long it'll take to get this working...
	 */
	
}
