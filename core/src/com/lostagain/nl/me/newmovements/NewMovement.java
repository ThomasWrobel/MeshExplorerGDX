package com.lostagain.nl.me.newmovements;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
//import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.PosRotScale;

/**
 * NEW VERSION
 * 
 * defines a simple movement in 3d space
 * @author Tom
 *
 */
public class NewMovement {

	private static String logstag="ME.NewMovement";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag
	
	PosRotScale destination;
	
	float LastWholeDelta = 0;
	
	enum MovementTypes{
		Relative,
		Absolute,
		REPEAT
	}
	
	public PosRotScale lastTransform; //Absolute movements need to supply this value
	
	
	MovementTypes currenttype = MovementTypes.Relative;
	
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
	public NewMovement(PosRotScale destination,float durationMSEachMove, float durationTotalMS){
		
		this.durationTotalMS=durationTotalMS;
		this.destination=destination;
		this.durationMSEachMove=durationMSEachMove;
				
		Log.info("destination.position set to: "+destination.position); //Z not updating??!?
		
	};
	
	public NewMovement(PosRotScale destination,float durationTotalMS){
		
		this.durationTotalMS=durationTotalMS;
		this.destination=destination;
		this.durationMSEachMove=durationTotalMS;

		Log.info("destination.position set to: "+destination.position); //Z not updating??!?
		
	};
	
	
	public PosRotScale onUpdate(float totalTimePast){
		
		//detect if on last cycle
		//That is if we are within 1 "durationMSEachMove" of "durationTotal"
		if (totalTimePast>(durationTotalMS-durationMSEachMove)){
			onLastRepeat = true;
			//Log.info( "onLastRepeat="+onLastRepeat+" t="+totalTimePast+" out of "+durationTotalMS);
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
		//this probably means this isn't a real movement, but a marker for a REPEAT of all motions in a sequence of motions
		return null;
	}
	
	/**
	 * returns the new PosRotScale based on the total time elapsed into this movement
	 * if we are working in relative mode we lerp between 0,0,0 and the destination (ie, we are measuring the displacement)
	 * 
	 * @param totalTimePast
	 * @return
	 */
	public PosRotScale onUpdateRelative(float totalTimePast){
		
		if (totalTimePast>durationTotalMS){
			return destination;
		}
		
		float subdelta=totalTimePast;
		
		if (totalTimePast>durationMSEachMove) {
			
			subdelta=totalTimePast%durationMSEachMove;
		//	Log.info( "_____________________prenew startRR scale on repeat="+lastTransform.getScaleX()+","+lastTransform.getScaleY()+","+lastTransform.getScaleZ()+")");
			
			 onRepeat();
		}
		
					
			//if we are working in relative mode we lerp between 0,0,0 and the destination (ie, we are measuring the change)
			
			float ratio = (subdelta/durationMSEachMove); //scales the time position from start to end to between 0.0 and 1.0
			
			//angle and translation need to be dealt with separately else you get weird issues with scaling and possibly other things
			Vector3 newposition = new Vector3().lerp(destination.position, ratio);
			
			//rot
			Quaternion newrotation = new Quaternion().slerp(destination.rotation, ratio);
					
			//scale
			Vector3 newscale =  new Vector3(1f,1f,1f).lerp(destination.scale, ratio);
			
			Log.info( "______ current rel position "+newposition.x+","+newposition.y+","+newposition.z);

			Log.info( "___ current rel scale "+newscale.toString() );	
			
			return new PosRotScale(newposition,newrotation,newscale);
		
		
		
		
		
	}

	
	
	public PosRotScale onUpdateAbsolute(float totalTimePast) { //Matrix4 startlocation
		
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
		//	Log.info( "_____________________________________________wholeDelta="+WholeDelta+" last one was"+LastWholeDelta);
			if (WholeDelta>LastWholeDelta){ //if more then the last one
			//	Log.info( "_______________prenew start scale on repeat="+lastTransform.getScaleX()+","+lastTransform.getScaleY()+","+lastTransform.getScaleZ()+")");
				
				
				onRepeat();
				
				LastWholeDelta = WholeDelta; //Note; the refresh set up above will clear the LastWholeDelta so we set it here to the new one
				
			}
			
			
		}
		
					
			//if we are working in absolute we lerp between current location and destination
			PosRotScale start = lastTransform.copy(); //copy redundant give 
		
			//loc
			Vector3 startLocation = start.position.cpy();	
			
			//rot			
			Quaternion startRotation = start.rotation.cpy();
			
			//scale
			Vector3 startScale = start.scale.cpy();
			
			//Log.info( "_____________________________________________start scale="+start.getScaleX());
			
			float ratio = (subdelta/durationMSEachMove); //scales the time position from start to end to between 0.0 and 1.0
			
			//angle and translation need to be dealt with separately else you get weird issues with scaling and possibly other things
			Vector3 newposition = startLocation.lerp(destination.position, ratio);
			

		//	Log.info("destination.position is: "+destination.position); //Z set right but doesn't update later
			Vector3 startAxis = new Vector3();
			startRotation.getAxisAngle(startAxis);
			
			//Log.info( "______a startrotation="+startRotation.getAngle()+" axis="+startAxis);
			
			//rot
			Quaternion newrotation = startRotation.slerp(destination.rotation, ratio);
			
			//newrotation = destination.rotation.cpy(); //temp while testing problem with rotation setting on jerk2d
					
			//scale
		//	Log.info( "______a current startscale="+startScale.x);
		//	Log.info( "______a current destscale="+destscale.x);
		//	Log.info( "______a current slerp="+ratio);
			
			//Log.info( "________a current dest rotation ="+destination.rotation.getAngle());	
			//newrotation.getAxisAngle(startAxis);
			//Log.info( "____________________a current new  rotation="+newrotation.getAngle()+" axis="+startAxis);	
			
			Vector3 newscale = startScale.lerp(destination.scale, ratio);

		//	Log.info( "______a current scaleX="+newscale.x);
			
			return new PosRotScale(newposition,newrotation,newscale);
		
		
		
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
	public void onRestart(PosRotScale newstart) {
		lastTransform = newstart.copy();
		//Log.info( "_____________________________________________lastLocation scale="+lastLocation.getScaleX());
	}
	

}
