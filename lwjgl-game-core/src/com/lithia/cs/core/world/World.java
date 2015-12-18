package com.lithia.cs.core.world;

import javolution.util.*;

import org.lwjgl.util.vector.*;

import com.lithia.cs.core.*;
import com.lithia.cs.core.gen.*;
import com.lithia.cs.core.util.*;

public class World extends Renderable
{
	
	/**
	 * The (single) player to occupy the world. This hook may be used to access
	 * the player's state, such as position, rotation, etc.
	 */
	private Player player;
	
	/**
	 * The active cache of chunks being used by the World. Each chunk is a large
	 * collection of blocks, divided up for runtime efficiency.
	 */
	private FastMap<Integer, Chunk> chunkCache = new FastMap<Integer, Chunk>();
	
	/**
	 * A collection of chunks currently visible to the player, whether by being
	 * within a reasonable distance or otherwise.
	 */
	private FastTable<Chunk> visibleChunks = new FastTable<Chunk>();
	
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
	private FastTable<Chunk> chunkUpdateQueue = new FastTable<Chunk>();
	
	/**
	 * The {@code DLUpdate} queue holds chunks whose vertex arrays have been
	 * rebuild and that need their display list to be up-to-date with the new
	 * vertex data. Chunks in this queue are processed by the main thread to
	 * maintain thread safety between the updating and the rendering of chunks.
	 */
	private FastTable<Chunk> chunkDLUpdateQueue = new FastTable<Chunk>();
	
	private GeneratorTerrain terrainGen;
	private long lastChunkUpdate = Helper.getTime();
	
	public World(String name, String seed, Player player)
	{
		this.player = player;
		player.resetPosition();
		
		terrainGen = new GeneratorTerrain(seed);
		
		updateThread = new Thread(new Runnable()
		{
			
			public void run()
			{
				while (true)
				{
					if (!chunkUpdateQueue.isEmpty())
					{
						double distance = Float.MAX_VALUE;
						int index = -1;
						
						int i = 0;
						Chunk c;
						for (Chunk last = chunkUpdateQueue.getLast(); (c = chunkUpdateQueue.get(i)) != last; i++)
						{
							double dist = c.calcDistanceSquaredToPlayer();
							
							if (dist < distance)
							{
								distance = dist;
								index = i;
							}
						}
						
						if (index != -1)
						{
							c = chunkUpdateQueue.remove(index);
							processChunk(c);
						}
					}
					
					if (Helper.getTime() - lastChunkUpdate >= 1000)
					{
						updateVisibleChunks();
						lastChunkUpdate = Helper.getTime();
					}
					
					try
					{
						Thread.sleep(0);
					}
					catch (Exception e)
					{
					}
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
		if (c == null) return;
		
		c.generate();
		Chunk[] neighbors = c.loadOrCreateNeighbors();
		for (Chunk n : neighbors)
		{
			if (n == null) continue;
			
			n.generate();
			
			if (n.update)
			{
				n.generateVertexArrays();
				chunkDLUpdateQueue.add(n);
			}
		}
		
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
		if(visibleChunks.isEmpty()) return;
		
		int i = 0;
		Chunk c;
		for (Chunk last = visibleChunks.getLast(); (c = visibleChunks.get(i)) != last; i++)
		{
			if (c != null) c.render();
		}
	}
	
	/**
	 * Iterate through chunks in update queue, and rebuild their display lists.
	 */
	public void update()
	{
		try
		{
			Chunk c = chunkDLUpdateQueue.poll();
			c.generateDisplayList();
		}
		catch (Exception e)
		{
		}
	}
	
	public int getBlock(int x, int y, int z)
	{
		int chunkPosX = calcChunkPosX(x);
		int chunkPosZ = calcChunkPosZ(z);
		
		int blockPosX = calcBlockPosX(x, chunkPosX);
		int blockPosZ = calcBlockPosZ(z, chunkPosZ);
		
		Chunk c = null;
		
		try
		{
			c = loadOrCreateChunk(chunkPosX, chunkPosZ);
			return c.getBlock(blockPosX, y, blockPosZ);
		}
		catch (Exception e)
		{
		}
		
		return 1;
	}
	
	/**
	 * Loads the chunk at the speficied location from the chunk cache, or
	 * creates a new one if it doesn't exist.
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public Chunk loadOrCreateChunk(int x, int z)
	{
		if(x < 0 || z < 0) return null;
		
		Chunk c = chunkCache.get(Helper.cantor(x, z));
		if (c != null) return c;
		
		c = prepareNewChunk(x, z);
		chunkCache.put(Helper.cantor(x, z), c);
		return c;
	}
	
	private Chunk prepareNewChunk(int x, int z)
	{
		FastTable<Generator> gen = new FastTable<Generator>();
		gen.add(terrainGen);
		return new Chunk(this, new Vector3f(x, 0, z), gen);
	}
	
	private void updateVisibleChunks()
	{
		visibleChunks = fetchVisibleChunks();
		
		// Cancel updating of non-visible chunks
		FastTable<Chunk> del = new FastTable<Chunk>();
		int i = 0;
		Chunk c;
		for(Chunk last = chunkUpdateQueue.getLast(); (c = chunkUpdateQueue.get(i)) != last; i++)
		{
			if(!visibleChunks.contains(c)) del.add(c);
		}
		
		visibleChunks.removeAll(del);
		del.clear();
	}
	
	private FastTable<Chunk> fetchVisibleChunks()
	{
		FastTable<Chunk> visibleChunks = new FastTable<Chunk>();
		for(int x = -8; x < 8; x++)
		{
			for(int z = -8; z < 8; z++)
			{
				Chunk c = loadOrCreateChunk(calcPlayerChunkOffsetX() + x, calcPlayerChunkOffsetZ() + z);

				if(c != null)
				{
					if(c.generate || c.update) queueChunkForUpdate(c);
					visibleChunks.add(c);
				}
				
			}
		}
		
		return visibleChunks;
	}

	private int calcPlayerChunkOffsetX()
	{
		return (int) (player.getPosition().x / Chunk.CHUNK_SIZE.x);
	}
	
	private int calcPlayerChunkOffsetZ()
	{
		return (int) (player.getPosition().z / Chunk.CHUNK_SIZE.z);
	}

	private int calcBlockPosX(int x1, int x2)
	{
		return (x1 - x2 * (int) Chunk.CHUNK_SIZE.x);
	}
	
	private int calcBlockPosZ(int z1, int z2)
	{
		return (z1 - z2 * (int) Chunk.CHUNK_SIZE.z);
	}
	
	private int calcChunkPosX(int x)
	{
		return x / (int) Chunk.CHUNK_SIZE.x;
	}
	
	private int calcChunkPosZ(int z)
	{
		return z / (int) Chunk.CHUNK_SIZE.z;
	}
	
	/**
	 * Send a chunk into the update queue, preparing it for an update.
	 * 
	 * @param c
	 *            The chunk to be updated
	 */
	private void queueChunkForUpdate(Chunk c)
	{
		if (c != null) chunkUpdateQueue.add(c);
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
}
