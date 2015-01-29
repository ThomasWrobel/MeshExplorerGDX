package com.lostagain.nl.me.movements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;

/**
 * defines a simple movement in 3d space
 * @author Tom
 *
 */
public class Movement {

	Matrix4 destination;
	Matrix4 start = new Matrix4();
	
	private static String logstag="ME.Movement";
	
	float durationMS = 1000;
	/**
	 *  sets the destination and duration
	 * @param destination
	 * @param durationMS
	 */
	public Movement(Matrix4 destination, float durationMS){
		this.durationMS=durationMS;
		this.destination=destination;
	};
	
	
	/**
	 * returns the new position based on the total time eclipsed into this movement
	 * @param delta
	 * @return
	 */
	public Matrix4 onUpdate(float delta){	
		if (delta>durationMS){
			return destination;
		} else 
		{
			
			float ratio = (delta/durationMS); //scales the time position from start to end to between 0.0 and 1.0

			return new Matrix4().lerp(destination, ratio);
		}
	
		
		
	}
	
	

}
