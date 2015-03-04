package com.lostagain.nl.me.models;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.me.newmovements.PosRotScale;

public interface hitable {
	
	
	public Vector3 getCenter();
	public int getRadius(); //hitradius
	public PosRotScale getTransform();
	public void fireTouchDown();
	public void fireTouchUp();
	
	
	

}
