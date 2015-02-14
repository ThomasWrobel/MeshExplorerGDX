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
	
	Vector3 destposition = new Vector3();
	Quaternion destrotation = new Quaternion();		
	Vector3 destscale = new Vector3(1f,1f,1f);
	
	float LastWholeDelta = 0;
	
	enum MovementTypes{
		Relative,
		Absolute,
		REPEAT
	}
	public Matrix4 lastLocation; //Absolute movements need to supply this value
	
	
	MovementTypes currenttype = MovementTypes.Relative;
	
	private static String logstag="ME.Movement";
	
	float durationTotalMS = 1000;
	float durationMSEachMove= 1000;
	/**
	 * determains if we are the on last cycle
		//That is if we are within 1 "durationMSEachMove" of "durationTotal"
	 */
	boolean onLastRepeat = false;
	
	/**
	 * durationMSEachMove - duration the movement takes
	 * durationTotal - how long this movement stays active. If this is, for example, double the duration of each move, then it will apply it twice. You can thus use durationTotal and durationEachMove together to select number of repeats, if there the same its only run once
	 * 
	 * refreshSetup(); is fired every repeat
	 * 
	 * @param destination
	 * @param durationMSEachMove
	 * @param durationTotalMS
	 */
	public Movement(Matrix4 destination,float durationMSEachMove, float durationTotalMS){
		
		this.durationTotalMS=durationTotalMS;
		this.destination=destination;
		this.durationMSEachMove=durationMSEachMove;
		
		
		//break the destination down into rotation and translation (one day we might use scale too)		
		destination.getTranslation(destposition);		
		
		//rot			
		destination.getRotation(destrotation);
		
		//scale
		destination.getScale(destscale);
		
		
	};
	
	public Movement(Matrix4 destination,float durationTotalMS){
		
		this.durationTotalMS=durationTotalMS;
		this.destination=destination;
		this.durationMSEachMove=durationTotalMS;
		
		
		//break the destination down into rotation and translation (one day we might use scale too)		
		destination.getTranslation(destposition);		
		
		//rot			
		destination.getRotation(destrotation);

		//scale
		destination.getScale(destscale);
	};
	
	
	public Matrix4 onUpdate(float totalTimePast){
		//detect if on last cycle
		//That is if we are within 1 "durationMSEachMove" of "durationTotal"
		if (totalTimePast>(durationTotalMS-durationMSEachMove)){
			onLastRepeat = true;
			//Gdx.app.log(logstag, "onLastRepeat="+onLastRepeat+" t="+totalTimePast+" out of "+durationTotalMS);
		} else {
			onLastRepeat = false;
		}
		
		//use different update function depending if we are working relative or not
		if (currenttype.equals(MovementTypes.Absolute)){
			return onUpdateAbsolute(totalTimePast);
		} else if (currenttype.equals(MovementTypes.Relative)) {
			return onUpdateRelative(totalTimePast);
		}
		
		//if we arnt relative or absolute we return null
		//this probably means this isnt a real movement, but a marker for a REPEAT of all motions in a sequence of motions
		return null;
	}
	
	/**
	 * returns the new position based on the total time elapsed into this movement
	 * if we are working in relative mode we lerp between 0,0,0 and the destination (ie, we are messuring the displacement)
	 * 
	 * @param totalTimePast
	 * @return
	 */
	public Matrix4 onUpdateRelative(float totalTimePast){	
		if (totalTimePast>durationTotalMS){
			return destination;
		}
		
		float subdelta=totalTimePast;
		
		if (totalTimePast>durationMSEachMove) {
			
			subdelta=totalTimePast%durationMSEachMove;
			
			 onRepeat();
		}
		
					
			//if we are working in relative mode we lerp between 0,0,0 and the destination (ie, we are measuring the change)
			
			float ratio = (subdelta/durationMSEachMove); //scales the time position from start to end to between 0.0 and 1.0
			
			//angle and translation need to be dealt with separately else you get weird issues with scaling and possibly other things
			Vector3 newposition = new Vector3().lerp(destposition, ratio);
			
			//rot
			Quaternion newrotation = new Quaternion().slerp(destrotation, ratio);
					
			//scale
			Vector3 newscale =  new Vector3(1f,1f,1f).lerp(destscale, ratio);
			
			return new Matrix4(newposition,newrotation,newscale);
		
		
		
		
		
	}

	
	public Matrix4 onUpdateAbsolute(float totalTimePast) { //Matrix4 startlocation
		
		if (totalTimePast>durationTotalMS){
			return destination;
		}
		
		float subdelta=totalTimePast;
		
		if (totalTimePast>durationMSEachMove) {
			
			subdelta=totalTimePast%durationMSEachMove;
			
			
			//hmz..need to fire the refresh every time we go past durationOfEachMove
			//assume the duration for each move is 1;
			//10.1
			//10.5
			//10.78
			//10.9
			//11.4 - fire refresh
			//11.6
			//11.8
			//12.1 - fire refresh
			//(etc)
			
			float WholeDelta = totalTimePast-subdelta;	//work out last "step" (that is, difference between current subdetail and the last whole duration past 		
		//	Gdx.app.log(logstag, "_____________________________________________wholeDelta="+WholeDelta+" last one was"+LastWholeDelta);
			if (WholeDelta>LastWholeDelta){ //if more then the last one
		
				
				onRepeat();
				
				LastWholeDelta = WholeDelta; //Note; the refresh set up above will clear the LastWholeDelta so we set it here to the new one
				
			}
			
			
		}
		
					
			//if we are working in absolute we lerp between current location and destination
			Matrix4 start = lastLocation;
			//loc
			Vector3 startLocation = new Vector3();
			start.getTranslation(startLocation);		
			
			//rot			
			Quaternion startRotation = new Quaternion();	
			start.getRotation(startRotation);
			
			//scale
			Vector3 startScale = new Vector3();
			start.getScale(startScale);	
			//Gdx.app.log(logstag, "_____________________________________________start scale="+start.getScaleX());
			
			float ratio = (subdelta/durationMSEachMove); //scales the time position from start to end to between 0.0 and 1.0
			
			//angle and translation need to be dealt with separately else you get weird issues with scaling and possibly other things
			Vector3 newposition = startLocation.lerp(destposition, ratio);
			
			//rot
			Quaternion newrotation = startRotation.slerp(destrotation, ratio);
			
			//scale
			Vector3 newscale = startScale.lerp(destscale, ratio);
			
			return new Matrix4(newposition,newrotation,newscale);
		
		
		
	}


	/**
	 * if needed, this will recalculate the position
	 */
	public void onRepeat() {
		// TODO Auto-generated method stub
		LastWholeDelta = 0;
	}
	
	/**
	 * this should be fired every time the animation is started.
	 * In the case of absolute movements it needs to set the current location as the new lastLocation
	 */
	public void onRestart(Matrix4 newstart) {
		lastLocation = newstart;
		//Gdx.app.log(logstag, "_____________________________________________lastLocation scale="+lastLocation.getScaleX());
	}
	

}
