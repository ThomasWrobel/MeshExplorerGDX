package com.lostagain.nl.me.newmovements;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.me.newmovements.NewMovement.MovementTypes;

public class NewMovementController {

	private static String logstag="ME.MovementController";
	
	
	public NewMovement currentMovement = new NewForward(300,8500); //just a test for now
	
	int currentMovementNumber = 0;
	
	ArrayList<NewMovement> movements = new ArrayList<NewMovement>();	
	ArrayList<NewMovement> old_movements = new ArrayList<NewMovement>();
	
	boolean resumeold = false;
		
	/**the matrix corresponding to the end of the last motion before the current one.
	 * messured in absolute co-ordinates **/
	PosRotScale lastNodesLocationMatrix = new PosRotScale();
	
	//time keeping
	float currentTime = 0; //the current eclipsed time in total;
	float currentTimeWithinMovement = 0; //the current elipsed time within the specific movement
	float totalTime = 10000; //total time of all movements
	/**
	 * 
	 *  creates a new movement controller for the object
	 *  Do not create a new controller for an existing object. Just set new movements with   the .setMovement commands
	 *  This ensures the last movement is correctly interrupted.
	 *  
	 * @param objectsOrigin
	 * @param movements
	 */
	public NewMovementController(PosRotScale objectsOrigin, NewMovement... movements) {
		super();
		
		//origin used as lastNodesLocationMatrix 
		lastNodesLocationMatrix.setTo(objectsOrigin);
		
		this.movements = new ArrayList<NewMovement>(Arrays.asList( movements));
		
		if (movements.length>0){
			currentMovement = this.movements.get(0);
			//Gdx.app.log(logstag, "_________________prenew start0 scale on repeat="+lastNodesLocationMatrix.getScaleX());
			
			
			currentMovement.onRestart(lastNodesLocationMatrix);
			
			//Gdx.app.log(logstag, "_________________prenew start0 scale on repeat="+currentMovement.lastTransform.getScaleX());
			
			//refresh if needed every time its set
			currentMovement.onRepeat();
			
		} else {
			currentMovement=null;
		}
		
		//totalTime 
	}

	

	/**
	 * Used to update a position
	 * If the movement is absolute, it needs the object passed so it can work out where it is already
	 * in order to interpolinate between its positions.
	 * 
	 * 
	 * NOTE: This returns the new world space translation in a PosRotScale
	 * (try not to convert to Matrix till as late as possible)
	 * 
	 * @param delta
	 * @param source
	 * @return
	 */
	
	public PosRotScale update(float delta){
		
		PosRotScale newstate = updatePosRotScale(delta);
		
		//Quaternion temprotation = new Quaternion(new Vector3(0,0,1),newstate.rotation.getAngle());
		
		//Matrix4 state = new Matrix4(newstate.position,newstate.rotation.nor(),newstate.scale);//new Vector3(1.2f,1f,1f)
	
		//Gdx.app.log(logstag, " new scalex: "+state.getScaleX()+ " new scaley: "+state.getScaleY());
		
		
		
		return newstate;
		
	}
	
	/**
	 * Used to update a position
	 * If the movement is absolute, it needs the object passed so it can work out where it is already
	 * in order to interpolinate between its positions.
	 * 
	 * If in relative mode, this isnt needed as the returned Matrix is the displacement from where it is already.
	 * 
	 * NOTE: This returns the new world space translation
	 * 
	 * @param delta
	 * @param source
	 * @return
	 */
	public PosRotScale updatePosRotScale(float delta){ //, Matrix4 objectsNativePosition
		

		if (currentMovement==null){
			return lastNodesLocationMatrix;
		}
		
		//convert to ms
		delta = delta*1000.0f;
		
		
		//Gdx.app.log(logstag, "______________________________________________delta="+delta);
		currentTime = currentTime+delta;
		currentTimeWithinMovement = currentTimeWithinMovement + delta;

	//	Gdx.app.log(logstag, "______________________________________________currentTime="+currentTime);
	//	Gdx.app.log(logstag, "______________________________________________currentTimeWithinMovement="+currentTimeWithinMovement);
		
		if (currentMovement!=null && currentTimeWithinMovement>currentMovement.durationTotalMS){
			
			//set new position so far (this can be thought of the start of each vertext on the path being formed)
			if (currentMovement.currenttype== MovementTypes.Absolute){
				lastNodesLocationMatrix = currentMovement.destination.copy();
			} else {
				lastNodesLocationMatrix.displaceBy(currentMovement.destination);	
			}
			
			//set time within movement to remainder left over from last movement
			//
			//  last movement took 5000ms
			//  our time is now 5400ms
			//  the time into the current movement should just be 400ms
			currentTimeWithinMovement = currentTimeWithinMovement-currentMovement.durationTotalMS;
						
			//set the new movement
			currentMovementNumber=currentMovementNumber+1;
			
			if (currentMovementNumber>=movements.size()){
				Gdx.app.log(logstag, "_____________________________________________ENDING movement=");
				
				if (!resumeold || old_movements.size()==0){
					
					currentMovement=null;
					currentMovementNumber=0;
					currentTime = 0;
					currentTimeWithinMovement = 0;
					
					return lastNodesLocationMatrix;	
					
				} else {
					
					resumeold=false;
					movements.clear();					
					
					movements.addAll(old_movements);
					
					currentMovement = movements.get(0);	
					currentMovement.onRestart(lastNodesLocationMatrix);
					//refresh if needed every time its set
				//	Gdx.app.log(logstag, "__mc_____________prenew start0 scale on repeat="+lastNodesLocationMatrix.getScaleX());
					
					
					currentMovement.onRepeat();
					
					old_movements.clear();
					currentMovementNumber=0;
					currentTime = 0;
					currentTimeWithinMovement = 0;
					totalTime = currentMovement.durationTotalMS;
					
					return lastNodesLocationMatrix;	
					
					
				}
			}
			
			
			currentMovement = movements.get(currentMovementNumber);
			
		
			//should also refresh lastposition if we are on a absolute motion at this point as its blank by default? Should be set to last location 
			
			
			if (currentMovement.currenttype==MovementTypes.REPEAT){
				//Gdx.app.log(logstag, "_____________________________________________REPEATING=");
				currentMovementNumber=0;
				currentMovement = movements.get(0);
				
				currentMovement.onRepeat();
				currentMovement.onRestart(lastNodesLocationMatrix);
			} else {
				
				//refresh if needed every time its set
				currentMovement.onRestart(lastNodesLocationMatrix);
				
			}
			
			
		}
	
		PosRotScale displacement; //NOTE: Displacement means different things depending on mode.
		//in absolute mode its the displacement relative to 0,0,0 in stagespace
		//in relative mode its relative to the last movements end location (which is stored in currentConclumativeMatrix)
		//because of this we need to deal with the return type differently
		
		 displacement = currentMovement.onUpdate(currentTimeWithinMovement);
				 
		//The value we return  is relative to worldspace
		if (currentMovement.currenttype == MovementTypes.Absolute){
			//relative to last position in absolute terms
			//Matrix4 lastnodesabsoluteposition = lastNodesLocationMatrix;// objectsNativePosition.cpy().mul(lastNodesLocationMatrix); //in order to work out the new position in abs more it needs the last position as an absolute as well
			
			// displacement = currentMovement.onUpdateAbsolute(currentTimeWithinMovement);//,lastnodesabsoluteposition); 

			return displacement; //displacement relative to 0,0,0 in stagespace, thus it doesnt need to be multiplied to get it relative to the last position
		
		} else {
		
			// displacement = currentMovement.onUpdateRelative(currentTimeWithinMovement); //currentTimeWithinMovement
			Gdx.app.log(logstag, "______________________________updating by pos:"+displacement.position);
			Gdx.app.log(logstag, "______________________________updating by scale:"+displacement.scale);
			
			 return lastNodesLocationMatrix.copy().displaceBy(displacement); //if we are doing a relative motion we multiply current displacement by the last point (that is ConclumativeMatrix)
		}
		/*
		Matrix4 test = new Matrix4().setToTranslation(100, 0, 0);
		Gdx.app.log(logstag, "______________________________________________dis=");
		Gdx.app.log(logstag, "MovementController="+tes2t.getValues()[0]+","+tes2t.getValues()[1]+","+tes2t.getValues()[2]+","+tes2t.getValues()[3]);
		Gdx.app.log(logstag, "MovementController="+tes2t.getValues()[4]+","+tes2t.getValues()[5]+","+tes2t.getValues()[6]+","+tes2t.getValues()[7]);
		Gdx.app.log(logstag, "MovementController="+tes2t.getValues()[8]+","+tes2t.getValues()[9]+","+tes2t.getValues()[10]+","+tes2t.getValues()[11]);
		Gdx.app.log(logstag, "MovementController="+tes2t.getValues()[12]+","+tes2t.getValues()[13]+","+tes2t.getValues()[14]+","+tes2t.getValues()[15]);
		
		Gdx.app.log(logstag, "______________________________________________ref end=");
		Gdx.app.log(logstag, "MovementController="+test.getValues()[0]+","+test.getValues()[1]+","+test.getValues()[2]+","+test.getValues()[3]);
		Gdx.app.log(logstag, "MovementController="+test.getValues()[4]+","+test.getValues()[5]+","+test.getValues()[6]+","+test.getValues()[7]);
		Gdx.app.log(logstag, "MovementController="+test.getValues()[8]+","+test.getValues()[9]+","+test.getValues()[10]+","+test.getValues()[11]);
		Gdx.app.log(logstag, "MovementController="+test.getValues()[12]+","+test.getValues()[13]+","+test.getValues()[14]+","+test.getValues()[15]);
		*/
		
		//if its absolute movement we dont use the conclumative matrix and instead just apply it

//currentMovement.onUpdate(currentTimeWithinMovement));
		
	}

	/**clears all movements and
	 * sets the current movement to the specified one
	 * @param movement
	
	public void setMovement(boolean resumeAfter,Movement movement) {
		
		setMovement(resumeAfter,movement);
		
		
		movements.clear();
		movements.add(movement);
		
		if (currentMovement!=null){
			Matrix4 displacement = currentMovement.onUpdate(currentTimeWithinMovement); 
			currentConclumativeMatrix.mul(displacement);	//burn the current movements position in at its current point
		}
		
		currentMovementNumber=0;
		currentMovement=movement;
		currentTime = 0; //the current eclipsed time in total;
		currentTimeWithinMovement = 0; //the current eclipsed time within the specific movement
		totalTime = movement.durationMS; //total time of all movements
	} */
	
	/**clears all movements and
	 * sets the current movements to the specified ones
	 * 
	 * Setting resume after means it will resume the current motions after - but only if no resumeAfter is already pending
	 * @param movement
	 */
	public void setMovement(PosRotScale lastLocation,boolean resumeAfter,NewMovement... create) {
		if (resumeAfter && old_movements.isEmpty()){
			Gdx.app.log(logstag, "_____________________________________________setting resume after");
			old_movements.clear();
			old_movements.addAll(movements);
			resumeold=true;
		} else {
			resumeold=false;
		}
		
		movements.clear();
		movements.addAll(new ArrayList<NewMovement>(Arrays.asList(create))); //new ArrayList<Movement>(Arrays.asList( movements));
		
		if (currentMovement!=null){
			
			PosRotScale displacement;
			if (currentMovement.currenttype == MovementTypes.Absolute){
				
				Gdx.app.log(logstag, "_____________________________________________lastTransform");
				currentMovement.lastTransform.setTo(lastLocation);//.cpy().mul(lastNodesLocationMatrix);
				
				// displacement = currentMovement.onUpdateAbsolute(currentTimeWithinMovement);//,object.transform.cpy().mul(lastNodesLocationMatrix)); 
				 lastNodesLocationMatrix.setTo(lastLocation);
				 
			} else {
			
				 displacement = currentMovement.onUpdateRelative(currentTimeWithinMovement); //currentTimeWithinMovement
				 lastNodesLocationMatrix.displaceBy(displacement);	//burn the current movements position in at its current point
			
			}
			//Matrix4 displacement = currentMovement.onUpdate(currentTimeWithinMovement); 
			
		}
		
		currentMovementNumber=0;
		currentMovement=movements.get(0);
		
		currentMovement.onRestart(lastLocation);
		
		
		//refresh if needed every time its set
		//Gdx.app.log(logstag, "_____________prenew start0 scale on repeat_="+currentMovement.lastTransform.getScaleX());
		
		
		currentMovement.onRepeat();
		
		currentTime = 0; //the current eclipsed time in total;
		currentTimeWithinMovement = 0; //the current eclipsed time within the specific movement
		
		
		
		//totalTime = currentMovement.durationMS; //total time of all movements
		
	}
	public boolean isGoingToResumeAfter() {
		
			return resumeold;
		
		
	}

	
	public boolean isMoving() {
		if (currentMovement!=null){
			return true;
		}
		
		return false;
	}

	public void clearMovement() {
		movements.clear();
		old_movements.clear();
		currentMovementNumber=0;
		currentMovement=null;
		resumeold=false;
		currentTime = 0; //the current eclipsed time in total;
		currentTimeWithinMovement = 0; 
		
	}

	/**
	 * gets the new transform for the object 
	 * @param delta - time since last frame 
	 * @param creaturemodel - the model being moved
	 * @param origin - the objects "native" position (relative motions start of relative to this point).
	 * 
	 * @return absolute worldspace transform for new location
	 */
	public PosRotScale getUpdate(float delta) {
		
		
		
		return update(delta);
		
		/*
		if (currentMovement.currenttype == MovementTypes.Absolute){
			
			Matrix4 displacementFromOrigin = update(delta, origin);
			
			return displacementFromOrigin; //absolute motions arnt relative to where we started from, so we just return it as-is
			
		} else {
			
			Matrix4 displacementFromOrigin = update(delta, origin);
		
			return origin.cpy().mul(displacementFromOrigin);
		
		}*/
	}

	

	
}
