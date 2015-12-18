package com.lithia.cs.core.util;

import org.lwjgl.*;

public class Helper
{
	
	public static long getTime()
	{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public static int cantor(int x, int z)
	{
		return (x + z) * (x + z + 1) / 2 + z;
	}
	
}
