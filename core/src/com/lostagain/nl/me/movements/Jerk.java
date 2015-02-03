package com.lostagain.nl.me.movements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Jerk extends Movement {
	
	Matrix4 currentDestination = new Matrix4();
	
	Matrix4 startingLocation; //origin point to jerk relative to
	float mindistance = 0;
	float maxdistance = 0;
	ModelInstance modelins;
	
	final static String logstag = "ME.Jerk";
	/**
	 * Randomly moves the object about its start point in random directions by the min/max amount specified.
	 * 
	 * Not yet working.
	 * 
	 * @param mindistance
	 * @param maxdistance 
	 * @param durationTotalMS
	 */
	public Jerk(ModelInstance creaturemodel ,float mindistance, float maxdistance, float durationMSEachMove,float durationTotal) {
		super( getDest(mindistance,maxdistance,creaturemodel.transform) , durationMSEachMove, durationTotal); //matrix isn't important its not used
		
		
		currenttype = MovementTypes.Absolute;
		
		this.mindistance=mindistance;
		this.maxdistance=maxdistance;
		this.modelins = creaturemodel;
		this.startingLocation=creaturemodel.transform.cpy();
		this.lastLocation = startingLocation;
	}
	
	/**
	 * returns the new position based on the total time eclipsed into this movement
	 * @param delta
	 * @return
	 */
	static public Matrix4 getDest(float mindistance, float maxdistance,Matrix4 origin){	
		
		Matrix4 start = origin.cpy();
		
		
		//angle and translation need to be dealt with separately else you get weird issues with scaling and possibly other things
		Vector3 newposition = new Vector3();
		start.getTranslation(newposition);

		Gdx.app.log(logstag, "___________________________old pos="+newposition.x+","+newposition.y+","+newposition.z);
		
		// between 5 and 10
		// we get a value between 5 and 10
		float range = (float) (mindistance+(Math.random()*(maxdistance-mindistance)));
		//then move it to a random direction
		range=(float) ((Math.signum((Math.random()-0.5)))*range);
		
		newposition.x = newposition.x + range;
				
		
		range = (float) (mindistance+(Math.random()*(maxdistance-mindistance)));
		//then move it to a random direction
		range=(float) ((Math.signum((Math.random()-0.5)))*range);
		
		newposition.y = newposition.y + range;

		range = (float) (mindistance+(Math.random()*(maxdistance-mindistance)));
		//then move it to a random direction
		range=(float) ((Math.signum((Math.random()-0.5)))*range);
		
		newposition.z = newposition.z + range;
		

		Gdx.app.log(logstag, "___________________________new pos="+newposition.x+","+newposition.y+","+newposition.z);
		
		return start.setToTranslation(newposition); //end is just the start with the new position
		//rot
		//Quaternion newrotation = new Quaternion();
		//start.getRotation(newrotation);
	
		
		
	}

	
	//we get a new destination each repeat
	@Override
	public void onRepeat(){
		super.onRepeat();
		
		Gdx.app.log(logstag, "_____________________________________________refreshSetup=");
		
		//store the last location
		lastLocation = modelins.transform;
		
		this.destination= getDest( mindistance,  maxdistance, startingLocation);
		
	
		//break the destination down into rotation and translation (one day we might use scale too)		
		destination.getTranslation(position);		

		Gdx.app.log(logstag, "______________________position="+position.x+","+position.y+","+position.z+")");
						
		//rot			
		destination.getRotation(rotation);
		
		
				
	}
	
	@Override
	public void onRestart(Matrix4 newstart) {
		super.onRestart(newstart);
		
		startingLocation = newstart;
		
	}
	
	/*
	
	//replace overrides with a refresh destination function, which will allow a new random to be calculated triggered by the movement controller
	//this also lets absolute locations for the jerks origin be updated(?)	
	@Override
	public Matrix4 onUpdateAbsolute(float delta,Matrix4 startlocation){
		
		//if we have exceeded the time duration we create a new destinition ready for next time
		if (delta>durationMS){
			this.destination= getDest( mindistance,  maxdistance, origin);
			
			//break the destination down into rotation and translation (one day we might use scale too)		
			destination.getTranslation(position);		
			
			//rot			
			destination.getRotation(rotation);
		}
		
		return super.onUpdate(delta);
	}

	
	@Override
	public Matrix4 onUpdate(float delta){
		
		//if we have exceeded the time duration we create a new destinition ready for next time
		if (delta>durationMS){
			this.destination= getDest( mindistance,  maxdistance, origin);
			
			//break the destination down into rotation and translation (one day we might use scale too)		
			destination.getTranslation(position);		
			
			//rot			
			destination.getRotation(rotation);
		}
		
		return super.onUpdate(delta);
	}
*/
	
}
