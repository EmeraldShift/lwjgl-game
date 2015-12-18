package com.lithia.cs.core.world;

import static org.lwjgl.opengl.GL11.*;

import java.nio.*;
import java.util.*;

import javolution.util.*;

import org.lwjgl.*;
import org.lwjgl.util.vector.*;

import com.lithia.cs.core.*;
import com.lithia.cs.core.gen.*;
import com.lithia.cs.core.util.*;
import com.lithia.cs.core.world.block.*;

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
	public static final Vector3f CHUNK_SIZE = VectorPool.get(16, 256, 16);
	
	private int displayList = -1;
	
	public boolean update = true;
	public boolean generate = true;
	
	/**
	 * Holds all the data for the positions of the vertices.
	 */
	private FastTable<Float> quads;
	
	/**
	 * Holds all the data for the colors of the vertices.
	 */
	private FastTable<Float> color;
	
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
	 * Holds a list of this chunk's neighboring chunks, useful for cross-chunk
	 * updating of lighting and vertex arrays.
	 */
	private Chunk[] neighbors;
	
	/**
	 * Maintains a list of the generators to act upon this chunk when it is
	 * created. May contain any amount of generators, including but not limited
	 * to terrain, flora, vegetation, etc.
	 */
	private FastTable<Generator> generators = new FastTable<Generator>();
	
	/**
	 * Initializes a chunk, supplying it with its position in the world as well
	 * as a hook back to the "parent" world.
	 */
	public Chunk(World parent, Vector3f position, Collection<Generator> gen)
	{
		this.position = position;
		this.parent = parent;
		
		blocks = new int[(int) CHUNK_SIZE.x][(int) CHUNK_SIZE.y][(int) CHUNK_SIZE.z];
		generators.addAll(gen);
	}
	
	/**
	 * Calls the chunk's display list, drawing it to the screen.
	 */
	public void render()
	{
		glCallList(displayList);
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
			for (Generator g : generators)
			{
				g.generate(this, parent);
			}
			
			generate = false;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Iterate through the chunk's blocks and generate the vertices for each.
	 */
	public void generateVertexArrays()
	{
		quads = new FastTable<Float>();
		color = new FastTable<Float>();
		
		for (int x = 0; x < CHUNK_SIZE.z; x++)
		{
			for (int y = 0; y < CHUNK_SIZE.y; y++)
			{
				for (int z = 0; z < CHUNK_SIZE.z; z++)
				{
					generateBlockVertices(x, y, z);
				}
			}
		}
		update = false;
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
		int type = blocks[x][y][z];
		boolean drawTop, drawFront, drawBack, drawLeft, drawRight, drawBottom;
		
		if (Block.getBlock(type).isBlockInvisible()) return;
		
		// Calculate the block offset from the World's origin
		float offsetX = position.x * CHUNK_SIZE.x;
		float offsetY = position.y * CHUNK_SIZE.y;
		float offsetZ = position.z * CHUNK_SIZE.z;
		
		// Create lists to hold the quad and color data)
		FastTable<Float> q = new FastTable<Float>();
		FastTable<Float> c = new FastTable<Float>();
		
		Vector4f colorOffset;
		drawTop = isSideVisibleForBlockType(parent.getBlock(getBlockWorldPosX(x), getBlockWorldPosY(y + 1), getBlockWorldPosZ(z)), type);
		if (drawTop)
		{
			colorOffset = Block.getBlock(type).getColorOffsetFor(Block.SIDE.TOP);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ + 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ + 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ - 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ - 0.5f);
		}
		
		drawFront = isSideVisibleForBlockType(parent.getBlock(getBlockWorldPosX(x), getBlockWorldPosY(y), getBlockWorldPosZ(z - 1)), type);
		if (drawFront)
		{
			colorOffset = Block.getBlock(type).getColorOffsetFor(Block.SIDE.FRONT);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ - 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ - 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ - 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ - 0.5f);
		}
		
		drawBack = isSideVisibleForBlockType(parent.getBlock(getBlockWorldPosX(x), getBlockWorldPosY(y), getBlockWorldPosZ(z + 1)), type);
		if (drawBack)
		{
			colorOffset = Block.getBlock(type).getColorOffsetFor(Block.SIDE.BACK);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ + 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ + 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ + 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ + 0.5f);
		}
		
		drawLeft = isSideVisibleForBlockType(parent.getBlock(getBlockWorldPosX(x - 1), getBlockWorldPosY(y), getBlockWorldPosZ(z)), type);
		if (drawLeft)
		{
			colorOffset = Block.getBlock(type).getColorOffsetFor(Block.SIDE.LEFT);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ - 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ + 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ + 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ - 0.5f);
		}
		
		drawRight = isSideVisibleForBlockType(parent.getBlock(getBlockWorldPosX(x + 1), getBlockWorldPosY(y), getBlockWorldPosZ(z)), type);
		if (drawRight)
		{
			colorOffset = Block.getBlock(type).getColorOffsetFor(Block.SIDE.RIGHT);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ - 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY + 0.5f);
			q.add(z + offsetZ + 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ + 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ - 0.5f);
		}
		
		drawBottom = isSideVisibleForBlockType(parent.getBlock(getBlockWorldPosX(x), getBlockWorldPosY(y - 1), getBlockWorldPosZ(z)), type);
		if (drawBottom)
		{
			colorOffset = Block.getBlock(type).getColorOffsetFor(Block.SIDE.BOTTOM);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ - 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ - 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX + 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ + 0.5f);
			
			c.add(colorOffset.x);
			c.add(colorOffset.y);
			c.add(colorOffset.z);
			c.add(colorOffset.w);
			q.add(x + offsetX - 0.5f);
			q.add(y + offsetY - 0.5f);
			q.add(z + offsetZ + 0.5f);
		}
		
		this.quads.addAll(q);
		this.color.addAll(c);
	}
	
	/**
	 * Rebuild the OpenGL display list used to render the chunk.
	 */
	public void generateDisplayList()
	{
		// Skip the build process if there's no data
		if (quads == null) return;
		// Reset the previous display list and create a new one!
		if (glIsList(displayList)) glDeleteLists(displayList, 1);
		displayList = glGenLists(1);
		
		// Create the final float buffer for use in the display list
		FloatBuffer q = BufferUtils.createFloatBuffer(quads.size());
		FloatBuffer c = BufferUtils.createFloatBuffer(color.size());
		
		for (int i = 0; i < quads.size(); i++)
		{
			q.put(quads.get(i));
		}
		for (int i = 0; i < color.size(); i++)
		{
			c.put(color.get(i));
		}
		
		q.flip();
		c.flip();
		
		glNewList(displayList, GL_COMPILE);
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		glColorPointer(4, 0, c);
		glVertexPointer(3, 0, q);
		glDrawArrays(GL_QUADS, 0, quads.size() / 3);
		glDisableClientState(GL_COLOR_ARRAY);
		glDisableClientState(GL_VERTEX_ARRAY);
		glEndList();
		
		quads = null;
		color = null;
	}
	
	/**
	 * Determine whether a face should be rendered based on surroundings.
	 * 
	 * @param check
	 *            The block to check off of
	 * @param block
	 *            The block with the face in question
	 * @return Whether or not the block face should be rendered.
	 */
	private boolean isSideVisibleForBlockType(int check, int block)
	{
		return check < 1 || Block.getBlock(check).isBlockInvisible() || Block.getBlock(check).isBlockTransparent();
	}
	
	private int getChunkWorldPosX()
	{
		return (int) (position.x * CHUNK_SIZE.x);
	}
	
	private int getChunkWorldPosY()
	{
		return (int) (position.y * CHUNK_SIZE.y);
	}
	
	private int getChunkWorldPosZ()
	{
		return (int) (position.z * CHUNK_SIZE.z);
	}
	
	private int getBlockWorldPosX(int x)
	{
		return x + getChunkWorldPosX();
	}
	
	private int getBlockWorldPosY(int y)
	{
		return y + getChunkWorldPosY();
	}
	
	private int getBlockWorldPosZ(int z)
	{
		return z + getChunkWorldPosZ();
	}
	
	public double calcDistanceSquaredToPlayer()
	{
		double x = parent.getPlayer().getPosition().x - getChunkWorldPosX() - Chunk.CHUNK_SIZE.x / 2;
		double z = parent.getPlayer().getPosition().z - getChunkWorldPosZ() - Chunk.CHUNK_SIZE.z / 2;
		
		return x * x + z * z;
	}
	
	public int getBlock(int x, int y, int z)
	{
		try
		{
			return blocks[x][y][z];
		}
		catch (Exception e)
		{
		}
		return -1;
	}
	
	public void setBlock(int x, int y, int z, int type)
	{
		try
		{
			blocks[x][y][z] = type;
			update = true;
			
			updateNeighbors(x, z);
		}
		catch (Exception e)
		{
		}
	}
	
	private void updateNeighbors(int x, int z)
	{
		Chunk[] neighbors = loadOrCreateNeighbors();
		
		if (x == 0) neighbors[1].update = true;
		if (x == Chunk.CHUNK_SIZE.x - 1) neighbors[0].update = true;
		if (z == 0) neighbors[3].update = true;
		if (z == Chunk.CHUNK_SIZE.z - 1) neighbors[2].update = true;
	}
	
	public Chunk[] loadOrCreateNeighbors()
	{
		if (neighbors == null)
		{
			neighbors = new Chunk[4];
			neighbors[0] = parent.loadOrCreateChunk((int) position.x + 1, (int) position.z);
			neighbors[1] = parent.loadOrCreateChunk((int) position.x - 1, (int) position.z);
			neighbors[2] = parent.loadOrCreateChunk((int) position.x, (int) position.z + 1);
			neighbors[3] = parent.loadOrCreateChunk((int) position.x, (int) position.z - 1);
		}
		return neighbors;
	}
	
}
