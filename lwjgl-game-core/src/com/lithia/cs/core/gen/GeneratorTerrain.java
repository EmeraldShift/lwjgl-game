package com.lithia.cs.core.gen;

import java.util.Random;

import com.lithia.cs.core.util.PerlinNoise;
import com.lithia.cs.core.world.Chunk;
import com.lithia.cs.core.world.World;

public class GeneratorTerrain implements Generator
{
	
	private PerlinNoise noise;
	
	public GeneratorTerrain(String seed)
	{
		noise = new PerlinNoise(seed.hashCode());
	}
	
	public void generate(Chunk c, World parent)
	{
		int offsetX = (int) (c.getPosition().x * Chunk.CHUNK_SIZE.x);
		int offsetZ = (int) (c.getPosition().z * Chunk.CHUNK_SIZE.z);
		
		for(int x = 0; x < Chunk.CHUNK_SIZE.x; x++)
		{
			for(int z = 0; z < Chunk.CHUNK_SIZE.z; z++)
			{
				int height = (int) calcTerrainElevation(x + offsetX, z + offsetZ);
				
				for(int y = 0; y < height; y++)
				{
					c.setBlock(x, y, z, new Random().nextInt(2) + 1);
				}
			}
		}
	}
	
	private float calcTerrainElevation(float x, float z)
	{
		return noise.noise(0.01f * x, 0.01f, 0.01f * z) * 24 + 64;
	}
	
}
