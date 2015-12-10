package com.lithia.cs.core.world;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.*;
import org.lwjgl.util.vector.*;
import com.lithia.cs.core.*;

public class Player extends Renderable
{
	
	private float yaw, pitch;
	
	public void update()
	{
		updatePosition();
	}
	
	/**
	 * "Renders" the player by adjusting the perspective based on Player's
	 * position.
	 */
	public void render()
	{
		glRotatef(pitch, 1, 0, 0);
		glRotatef(yaw, 0, 1, 0);
		glTranslatef(-position.x, -position.y, -position.z);
	}
	
	private void updatePosition()
	{
		Vector3f dPos = new Vector3f();
		
		yaw += Mouse.getDX() / 10.0f;
		pitch -= Mouse.getDY() / 10.0f;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			dPos.z -= Math.cos(Math.toRadians(yaw)) * 0.25;
			dPos.x += Math.sin(Math.toRadians(yaw)) * 0.25;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			dPos.z += Math.cos(Math.toRadians(yaw)) * 0.25;
			dPos.x -= Math.sin(Math.toRadians(yaw)) * 0.25;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			dPos.z += Math.cos(Math.toRadians(yaw + 90)) * 0.25;
			dPos.x -= Math.sin(Math.toRadians(yaw + 90)) * 0.25;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			dPos.z += Math.cos(Math.toRadians(yaw - 90)) * 0.25;
			dPos.x -= Math.sin(Math.toRadians(yaw - 90)) * 0.25;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			dPos.y += 0.25;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			dPos.y -= 0.25;
		}
		
		position = Vector3f.add(dPos, position, null);
	}
	
}
