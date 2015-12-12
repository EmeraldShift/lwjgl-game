package com.lithia.cs.core.util;

public class FastRandom
{
	
	long seed = System.currentTimeMillis();
	
	public FastRandom(long seed)
	{
		this.seed = seed;
	}
	
	public FastRandom()
	{
	}
	
	public long randomLong()
	{
		seed ^= (seed << 21);
		seed ^= (seed >>> 35);
		seed ^= (seed << 4);
		
		return seed;
	}
	
	public double randomDouble()
	{
		return randomLong() / (Long.MAX_VALUE - 1d);
	}
	
	public int randomInt()
	{
		return (int) (randomDouble() * Integer.MAX_VALUE);
	}
	
}
