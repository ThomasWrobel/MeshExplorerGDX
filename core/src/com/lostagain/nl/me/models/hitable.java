package com.lostagain.nl.me.models;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.lostagain.nl.me.newmovements.PosRotScale;

public interface hitable {
	
	
	public Vector3 getCenter();
	//public int getRadius(); //hitradius
	public PosRotScale getTransform();
	public void fireTouchDown();
	public void fireTouchUp();
	
	public void setLastHitsRange(float range); //set the distance squared from the shot origin to this object 
	public float getLastHitsRange(); //returns the above
	
	public boolean isBlocker(); //if this blocks hits below it
	public boolean rayHits(Ray ray);
	
}
