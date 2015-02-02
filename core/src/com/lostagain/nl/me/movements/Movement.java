package com.lostagain.nl.me.movements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
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
		Relative,
		Absolute,
		REPEAT
	}
	MovementTypes currenttype = MovementTypes.Relative;
	
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
	 * returns the new position based on the total time elapsed into this movement
	 * if we are working in relative mode we lerp between 0,0,0 and the destination (ie, we are messuring the displacement)
	 * 
	 * @param delta
	 * @return
	 */
	public Matrix4 onUpdateRelative(float delta){	
		if (delta>durationMS){
			return destination;
		} else 
		{
					
			//if we are working in relative mode we lerp between 0,0,0 and the destination (ie, we are messuring the displacement)
			
			float ratio = (delta/durationMS); //scales the time position from start to end to between 0.0 and 1.0
			
			//angle and translation need to be dealt with separately else you get weird issues with scaling and possibly other things
			Vector3 newposition = new Vector3().lerp(position, ratio);
			
			//rot
			Quaternion newrotation = new Quaternion().slerp(rotation, ratio);
					
			return new Matrix4(newposition,newrotation,new Vector3(1f, 1f, 1f));
		}
		
	
		
		
	}


	public Matrix4 onUpdateAbsolute(float delta,
			Matrix4 startlocation) {
		
		if (delta>durationMS){
			return destination;
		} else 
		{
					
			//if we are working in absolute we lerp between current location and destinition
			Matrix4 start = startlocation;
			
			Vector3 startLocation = new Vector3();
			start.getTranslation(startLocation);		
			Quaternion startRotation = new Quaternion();		
			//rot				
			start.getRotation(startRotation);
			
			
			float ratio = (delta/durationMS); //scales the time position from start to end to between 0.0 and 1.0
			
			//angle and translation need to be dealt with separately else you get weird issues with scaling and possibly other things
			Vector3 newposition = startLocation.lerp(position, ratio);
			
			//rot
			Quaternion newrotation = startRotation.slerp(rotation, ratio);
					
			return new Matrix4(newposition,newrotation,new Vector3(1f, 1f, 1f));
		}
		
		
	}
	
	

}
