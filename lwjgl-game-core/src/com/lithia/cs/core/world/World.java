package com.lithia.cs.core.world;

import java.util.*;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.*;

import com.lithia.cs.core.*;

public class World extends Renderable
{
	
	/**
	 * The display list used for drawing the world.
	 */
	private int displayList = -1;
	
	/**
	 * The (single) player to occupy the world. This hook may be used to access
	 * the player's state, such as position, rotation, etc.
	 */
	private Player player;
	
	/**
	 * The active collection of chunks being used by the World. Each chunk is a
	 * large collection of blocks, divided up for runtime efficiency.
	 */
	private Chunk[][][] chunks;
	
	/**
	 * A separately-running thread devoted to continuously updating chunks
	 * within the {@code Update} queue, instantiated directly below this object.
	 */
	private Thread updateThread;
	
	/**
	 * The {@code Update} queue holds chunks who have been flagged for an update
	 * by an outside class or method. Chunks in this queue are processed by the
	 * {@code updateThread} thread before being switched to the {@code DLUpdate}
	 * queue for display list handling.
	 */
	private List<Chunk> chunkUpdateQueue = new LinkedList<Chunk>();
	
	/**
	 * The {@code DLUpdate} queue holds chunks whose vertex arrays have been
	 * rebuild and that need their display list to be up-to-date with the new
	 * vertex data. Chunks in this queue are processed by the main thread to
	 * maintain thread safety between the updating and the rendering of chunks.
	 */
	private List<Chunk> chunkDLUpdateQueue = new LinkedList<Chunk>();
	
	public World(String name, Player player)
	{
		this.player = player;
		chunks = new Chunk[(int) Config.WORLD_SIZE.x][(int) Config.WORLD_SIZE.y][(int) Config.WORLD_SIZE.z];
		
		for (int x = 0; x < Config.WORLD_SIZE.x; x++)
		{
			for (int z = 0; z < Config.WORLD_SIZE.z; z++)
			{
				// For now, we'll only autogen the lowest chunk layer, primarily
				// because I'm unsure of whether I even want there to be 16
				// chunks stacked on top of each other. Sounds like a lot of
				// extra overhead for mostly empty chunks anyway. TBD
				Chunk c = new Chunk(this, new Vector3f(x, 0, z));
				chunks[x][0][z] = c;
				queueChunkForUpdate(c);
			}
		}
		
		updateThread = new Thread(new Runnable()
		{
			
			public void run()
			{
				while (true)
				{
					if (!chunkUpdateQueue.isEmpty())
					{
						// We'll take one chunk at a time so this loop doesn't
						// run too extensively.
						processChunk(chunkUpdateQueue.remove(0));
					}
					
					// Just so our poor CPU has time to rest
					try { Thread.sleep(50); } catch (Exception e) {}
				}
				
			}
			
		});
		
		updateThread.start();
	}
	
	/**
	 * Processes a chunk, generating and initializing its initial state if it
	 * has not already been generated, and rebuilding its vertex arrays if it is
	 * flagged for an update.
	 * 
	 * @param c
	 */
	private void processChunk(Chunk c)
	{
		c.generate();
		
		if (c.update)
		{
			c.generateVertexArrays();
			chunkDLUpdateQueue.add(c);
		}
	}
	
	/**
	 * Simple test of rendering capabilities featuring: The infinitely receding
	 * square! TODO: Implement an efficient method of rendering millions of
	 * voxels here...
	 */
	public void render()
	{
		for(int x = 0; x < Config.WORLD_SIZE.x; x++)
		{
			for(int z = 0; z < Config.WORLD_SIZE.x; z++)
			{
				Chunk c = chunks[x][0][z];
				if(c != null) c.render();
			}
		}
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3f(1, 1, 2);
		GL11.glVertex3f(0, 1, 2);
		GL11.glVertex3f(0, 0, 2);
		GL11.glVertex3f(1, 0, 2);
		GL11.glEnd();
	}
	
	/**
	 * Iterate through chunks in update queue, and rebuild their display lists.
	 */
	public void update()
	{
		if(chunkDLUpdateQueue.isEmpty()) return;
		try
		{
			Chunk c = chunkDLUpdateQueue.remove(0);
			c.generateDisplayList();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a chunk into the update queue, preparing it for an update.
	 * 
	 * @param c
	 *            The chunk to be updated
	 */
	private void queueChunkForUpdate(Chunk c)
	{
		chunkUpdateQueue.add(c);
	}
	
}
