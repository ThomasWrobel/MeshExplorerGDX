package com.lostagain.nl.me.movements;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Jitter extends Movement {
	
	Matrix4 currentDestination = new Matrix4();
	
	/**
	 * Randomly moves the object about its start point in random directions by the min/max amount specified.
	 * 
	 * Not yet working.
	 * 
	 * @param mindistance
	 * @param maxdistance 
	 * @param durationMS
	 */
	public Jitter(float mindistance, float maxdistance, float durationMSEachMove) {
		super(new Matrix4(), durationMSEachMove); //matrix isnt important its not used
		
		currenttype = MovementTypes.Absolute;
	}
	
	/**
	 * returns the new position based on the total time eclipsed into this movement
	 * @param delta
	 * @return
	 */
	public Matrix4 onUpdateRelative(float delta){	
		
		if (delta>durationMS){
			
			return destination;
			
			
		} else 
		{
					
			
			float ratio = (delta/durationMS); //scales the time position from start to end to between 0.0 and 1.0
			
			//angle and translation need to be dealt with separately else you get weird issues with scaling and possibly other things
			Vector3 newposition = new Vector3().lerp(position, ratio);
			
			//rot
			Quaternion newrotation = new Quaternion().slerp(rotation, ratio);
					
			return new Matrix4(newposition,newrotation,new Vector3(1f, 1f, 1f));
		}
		
	
		
		
	}
	

}
