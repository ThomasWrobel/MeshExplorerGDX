package com.lostagain.nl.me.models;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public interface hitable {
	
	
	public Vector3 getCenter();
	public int getRadius(); //hitradius
	public Matrix4 getTransform();
	public void fireTouchDown();
	public void fireTouchUp();
	
	
	

}
