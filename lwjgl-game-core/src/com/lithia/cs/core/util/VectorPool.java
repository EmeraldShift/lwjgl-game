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
		if (VectorPool.vector3.isEmpty())
		{
			Vector3f v = new Vector3f();
			VectorPool.vector3.add(v);
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

	public static Vector3f getVector3(float x, float y, float z)
	{
		Vector3f v = null;

		if (VectorPool.vector3.isEmpty())
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

	public static void putVector3(Vector3f v)
	{

		if (vector3.size() < 512)
			synchronized (vector3)
			{
				vector3.add(v);
			}
		}
	}

}
