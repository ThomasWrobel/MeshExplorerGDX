package com.lostagain.nl.me.newmovements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class NewJerk2D extends NewMovement {
	
	Matrix4 currentDestination = new Matrix4();
	
	PosRotScale startingTransform; //origin point to jerk relative to
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
	public NewJerk2D(ModelInstance creaturemodel ,float mindistance, float maxdistance, float durationMSEachMove,float durationTotal) {
		super( getDest(mindistance,maxdistance,creaturemodel.transform) , durationMSEachMove, durationTotal); //matrix isn't important its not used
		
		
		currenttype = MovementTypes.Absolute;
		
		this.mindistance=mindistance;
		this.maxdistance=maxdistance;
		this.modelins = creaturemodel;
		//this.startingTransform=creaturemodel.transform.cpy();
		this.startingTransform=new PosRotScale(creaturemodel.transform);
		this.lastTransform = startingTransform;
		
	}
		
	/**
	 * returns the new position based on the total time eclipsed into this movement
	 * @param delta
	 * @return
	 */
	static public PosRotScale getDest(float mindistance, float maxdistance,Matrix4 origin){	
		
		//Start the destination state as a copy of the original
		PosRotScale destinationState = new PosRotScale(origin);
		
		//Pick a new location to goto within the radius of the existing one
		Vector3 newposition = new Vector3(destinationState.position);
		float angle = (float) (Math.random()*360);
		float distance =  (float) (mindistance+(Math.random()*(maxdistance-mindistance)));
		
		//work out new X/Y 
		newposition.x = (float) (newposition.x + (Math.cos(Math.toRadians(angle))*distance));
		newposition.y = (float) (newposition.y + (Math.sin(Math.toRadians(angle))*distance));
		
		//now set the destination to this new position (keeping Z the same as this is 2D movement only
		destinationState.setToPosition(new Vector3(newposition.x,newposition.y,destinationState.position.z));
		
		
		
		/*
		Matrix4 start = origin.cpy();
								
		Vector3 newposition = new Vector3();
		start.getTranslation(newposition);

		
		float angle = (float) (Math.random()*360);
		float distance =  (float) (mindistance+(Math.random()*(maxdistance-mindistance)));
		
		//work out new X/Y 
		newposition.x = (float) (newposition.x + (Math.cos(Math.toRadians(angle))*distance));
		newposition.y = (float) (newposition.y + (Math.sin(Math.toRadians(angle))*distance));
		
		//preserve original scale
		Vector3 newscale = new Vector3();
		start.getScale(newscale);
		
		// Vector3 axisVec = new Vector3();
	     //   float existingangle = (float) (rotation.getAxisAngle(axisVec) * axisVec.nor().z);
	       // existingangle = existingangle < 0 ? existingangle + 360 : existingangle; //convert <0 value

	        //rotation.setFromAxis(axisVec, 45);
			//start.set(rotation);//axisVec, existing angle+55);
			
		Gdx.app.log(logstag, "______ start scaleX ="+start.getScaleX());
		
		//	we now alter the start to the new angle, position and scale
			//start.setToRotation(new Vector3(0,0,1), angle).setTranslation(newposition).scl(newscale);
			
			Quaternion quart = new Quaternion(new Vector3(0,0,1), angle);
			PosRotScale dest = new PosRotScale(newposition, quart , newscale);
			
			
			//Gdx.app.log(logstag, "______ dest scaleX="+dest.getScaleX());
			
		//	start.mul(new Matrix4().setToRotation(new Vector3(0,0,1), angle));

		//	Gdx.app.log(logstag, "___________________________angle="+existingangle);
		//	Gdx.app.log(logstag, "___________________________old pos="+newposition.x+","+newposition.y+","+newposition.z);
		

		

/*
		
		
		//angle and translation need to be dealt with separately else you get weird issues with scaling and possibly other things
		Vector3 newposition = new Vector3();
		Vector3 oldposition = new Vector3();
		
		start.getTranslation(newposition);
		start.getTranslation(oldposition);
		
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
		
		//newposition.z = newposition.z + range;
		

	//	start.setToLookAt(oldposition, newposition, new Vector3(0,0,0));
		//new Matrix4().setToRotation(new Vector3(), 45).mul(start);
		
		Gdx.app.log(logstag, "___________________________new pos="+newposition.x+","+newposition.y+","+newposition.z);
		*/
		return destinationState;///new Matrix4(). .setToRotation(new Vector3(0,0,1), 45).mulLeft(start);//start.setTranslation(newposition); //end is just the start with the new position
		//rot
		//Quaternion newrotation = new Quaternion();
		//start.getRotation(newrotation);
	
		
		
	}

	
	//we get a new destination each repeat
	//if on final repeat we should go back to the origin to make looping easier
	@Override
	public void onRepeat(){
		super.onRepeat();
		
		
		
		Gdx.app.log(logstag, "_____________________________________________refreshSetup=");
		
		//store the last location
		lastTransform = new PosRotScale(modelins.transform);//this often has the wrong values why?
		/*
		 * ME.Movement: ______a current lerp=0.9614514
ME.Movement: ______a current scaleX=1.2145704 <------------should be this (ish(
ME.Jerk: _____________________________________________refreshSetup=
ME.Jerk: ______ start scaleX =1.2
ME.Jerk: ______ dest scaleX=1.2
ME.Jerk: ______________________new start scale on repeat=1.4867488,1.4867488,1.2) <-------wrong
ME.Jerk: ______________________new dest scale on repeat=1.2,1.2,1.2)
ME.Movement: ______a current startscale=1.4867488
ME.Movement: ______a current destscale=1.2
ME.Movement: ______a current lerp=0.0029626465
ME.Movement: ______a current scaleX=1.4858993
		 */
		//Gdx.app.log(logstag, "______________________new start scale on repeat="+lastTransform.getScaleX()+","+lastTransform.getScaleY()+","+lastTransform.getScaleZ()+")");
		
		//if we are on the last repeat, we head back to the original position
		//this allows looping without a net displacement
		if (onLastRepeat){
			destination = startingTransform;				
		} else {
			//else we calculate a new random location
			destination = getDest( mindistance,  maxdistance, startingTransform.createMatrix());
		}
	
		//break the destination down into rotation and translation (one day we might use scale too)		
		//destination.getTranslation(destposition);		

		//Gdx.app.log(logstag, "______________________position="+position.x+","+position.y+","+position.z+")");
						
		//rot			
		//destination.getRotation(destrotation);
		
		//destination.getScale(destscale);
	
		//Gdx.app.log(logstag, "______________________new dest scale on repeat="+destscale.x+","+destscale.y+","+destscale.z+")");
				
	}
	
	@Override
	public void onRestart(PosRotScale newstart){
		super.onRestart(newstart);
		
		startingTransform = newstart;
		

	//	Gdx.app.log(logstag, "______________________new scale onRestart="+startingTransform.getScaleX()+","+startingTransform.getScaleY()+","+startingTransform.getScaleZ()+")");
		
		
		
		
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
