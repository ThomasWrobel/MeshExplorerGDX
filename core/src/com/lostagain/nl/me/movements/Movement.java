package com.lostagain.nl.me.movements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * defines a simple movement in 3d space
 * @author Tom
 *
 */
public class Movement {

	Matrix4 destination;
	
	Vector3 position = new Vector3();
	Quaternion rotation = new Quaternion();		
	
	enum MovementTypes{
		Forward,Rotate,REPEAT
	}
	MovementTypes currenttype = null;
	
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
		
		//break the destination down into rotation and translation (one day we might use scale too)
		
		destination.getTranslation(position);		
		
		//rot
			
		destination.getRotation(rotation);
		
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
			
			//angle and translation need to be dealt with separately else you get weird issues with scaling and possibly other things
			Vector3 newposition = new Vector3().lerp(position, ratio);
			
			//rot
			Quaternion newrotation = new Quaternion().slerp(rotation, ratio);
					
			return new Matrix4(newposition,newrotation,new Vector3(1f, 1f, 1f));
		}
		
	
		
		
	}
	
	

}
