package com.lithia.cs.core.util;

import java.util.Vector;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class VectorPool
{

	private static Vector<Vector3f> vector3 = new Vector<Vector3f>(512);
	private static Vector<Vector4f> vector4 = new Vector<Vector4f>(512);

	public static Vector3f getVector3()
	{
		if (vector3.isEmpty())
		{
			Vector3f v = new Vector3f();
			return v;
		}
		else
		{
			Vector3f v = vector3.remove(0);

			v.x = 0;
			v.y = 0;
			v.z = 0;
			return v;
		}
	}
	
	public static Vector4f getVector4()
	{
		if (vector4.isEmpty())
		{
			Vector4f v = new Vector4f();
			return v;
		}
		else
		{
			Vector4f v = vector4.remove(0);

			v.x = 0;
			v.y = 0;
			v.z = 0;
			return v;
		}
	}

	public static Vector3f get(float x, float y, float z, boolean ret)
	{
		Vector3f v = get(x, y, z);
		if(ret) vector3.add(v);
		return v;
	}

	public static Vector3f get(float x, float y, float z)
	{
		Vector3f v = null;

		if (vector3.isEmpty())
		{
			v = new Vector3f(x, y, z);
			return v;
		}
		else
		{
			synchronized (vector3)
			{
				v = vector3.remove(0);
			}
			
			v.x = x;
			v.y = y;
			v.z = z;
			return v;
		}
	}
	
	public static Vector4f get(float x, float y, float z, float w, boolean ret)
	{
		Vector4f v = get(x, y, z, w);
		if(ret) vector4.add(v);
		return v;
	}
	
	public static Vector4f get(float x, float y, float z, float w)
	{
		Vector4f v = null;

		if (vector4.isEmpty())
		{
			v = new Vector4f(x, y, z, w);
			return v;
		}
		else
		{
			synchronized (vector4)
			{
				v = vector4.remove(0);
			}
			
			v.x = x;
			v.y = y;
			v.z = z;
			v.w = w;
			return v;
		}
	}

	public static Vector3f put(Vector3f v)
	{

		if (vector3.size() < 512)
			synchronized (vector3)
			{
				vector3.add(v);
			}
		
		return v;
	}

	public static Vector4f put(Vector4f v)
	{

		if (vector4.size() < 512)
			synchronized (vector4)
			{
				vector4.add(v);
			}
		
		return v;
	}

}
