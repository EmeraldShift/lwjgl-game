package com.lithia.cs.core.util;

public class PerlinNoise
{
	
	private FastRandom rand;
	private int[] perm = new int[512]; // Permutation for gradient vectors
	
	/**
	 * Create a new noise generator with the given seed.
	 * 
	 * @param seed
	 */
	public PerlinNoise(long seed)
	{
		rand = new FastRandom(seed);
		
		// Randomly generate the permutation
		for (int i = 0; i < perm.length; i++)
		{
			perm[i] = Math.abs(rand.randomInt() % (perm.length / 2));
		}
	}
	
	/**
	 * Generates a float value between 0.0 and 1.0 for the given coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return the noise value
	 */
	public float noise(float x, float y, float z)
	{
		int ix = (int) x;
		int iy = (int) y;
		int iz = (int) z;
		
		float dx = x - ix;
		float dy = y - iy;
		float dz = z - iz;
		
		ix &= perm.length - 1;
		iy &= perm.length - 1;
		iz &= perm.length - 1;
		
		// Trilinear interolation: Take 8 corners, lerp into 4 , lerp into 2,
		// lerp into one, and return it.
		
		float w000 = grad(ix, iy, iz, dx, dy, dz);
		float w100 = grad(ix + 1, iy, iz, dx - 1, dy, dz);
		float w010 = grad(ix, iy + 1, iz, dx, dy - 1, dz);
		float w110 = grad(ix + 1, iy + 1, iz, dx - 1, dy - 1, dz);
		float w001 = grad(ix, iy, iz + 1, dx, dy, dz - 1);
		float w101 = grad(ix + 1, iy, iz + 1, dx - 1, dy, dz - 1);
		float w011 = grad(ix, iy + 1, iz + 1, dx, dy - 1, dz - 1);
		float w111 = grad(ix + 1, iy + 1, iz + 1, dx - 1, dy - 1, dz - 1);
		
		float wx = noiseWeight(dx), wy = noiseWeight(dy), wz = noiseWeight(dz);
		
		float x00 = lerp(wx, w000, w100);
		float x10 = lerp(wx, w010, w110);
		float x01 = lerp(wx, w001, w101);
		float x11 = lerp(wx, w011, w111);
		
		float y0 = lerp(wy, x00, x10);
		float y1 = lerp(wy, x01, x11);
		
		return lerp(wz, y0, y1);
	}
	
	private float lerp(float t, float v1, float v2)
	{
		return (1.0f - t) * v1 + t * v2;
	}
	
	private float grad(int x, int y, int z, float dx, float dy, float dz)
	{
		int h = perm[(perm[(perm[x % perm.length] + y) % perm.length] + z) % perm.length] & 15;
		float u = h < 8 || h == 12 || h == 13 ? dx : dy;
		float v = h < 4 || h == 12 || h == 13 ? dy : dz;
		
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}
	
	private float noiseWeight(float t)
	{
		float t3 = t * t * t;
		float t4 = t3 * t;
		return 6.0f * t4 * t - 15.0f * t4 + 10.0f * t3;
	}
	
}
