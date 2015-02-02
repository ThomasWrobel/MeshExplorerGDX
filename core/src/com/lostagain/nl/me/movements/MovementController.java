package com.lostagain.nl.me.movements;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.lostagain.nl.me.movements.Movement.MovementTypes;

public class MovementController {

	private static String logstag="ME.MovementController";
	
	
	Movement currentMovement = new Forward(300,8500); //just a test for now
	int currentMovementNumber = 0;
	
	ArrayList<Movement> movements = new ArrayList<Movement>();
	
	ArrayList<Movement> old_movements = new ArrayList<Movement>();
	boolean resumeold = false;
	
	/**the matrix corresponding to the end of the last motion before the current one.
	 * **/
	Matrix4 lastNodesLocationMatrix = new Matrix4();
	
	//time keeping
	float currentTime = 0; //the current eclipsed time in total;
	float currentTimeWithinMovement = 0; //the current elipsed time within the specific movement
	float totalTime = 10000; //total time of all movements
	
	public MovementController(Movement... movements) {
		super();
		this.movements = new ArrayList<Movement>(Arrays.asList( movements));
		if (movements.length>0){
			currentMovement = this.movements.get(0);
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
	 * If in relative mode, this isnt needed as the returned Matrix is the displacement from where it is already.
	 * 
	 * @param delta
	 * @param source
	 * @return
	 */
	public Matrix4 update(float delta, Matrix4 objectsNativePosition){

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
		
		if (currentMovement!=null && currentTimeWithinMovement>currentMovement.durationMS){
			
			//set new position so far (this can be thought of the start of each vertext on the path being formed)
			if (currentMovement.currenttype== MovementTypes.Absolute){
				lastNodesLocationMatrix.set(currentMovement.destination);	
			} else {
				lastNodesLocationMatrix.mul(currentMovement.destination);	
			}
			//set time within movement to remainder left over from last movement
			//
			//  last movement took 5000ms
			//  our time is now 5400ms
			//  the time into the current movement should just be 400ms
			currentTimeWithinMovement = currentTimeWithinMovement-currentMovement.durationMS;
						
			//set the new movement
			currentMovementNumber=currentMovementNumber+1;
			
			if (currentMovementNumber>=movements.size()){
				Gdx.app.log(logstag, "_____________________________________________ENDING movement=");
				
				if (!resumeold){
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
					
					old_movements.clear();
					currentMovementNumber=0;
					currentTime = 0;
					currentTimeWithinMovement = 0;
					totalTime = currentMovement.durationMS;
					return lastNodesLocationMatrix;	
				}
			}
			
			
			currentMovement = movements.get(currentMovementNumber);
			
			if (currentMovement.currenttype==MovementTypes.REPEAT){
				Gdx.app.log(logstag, "_____________________________________________REPEATING=");
				currentMovementNumber=0;
				currentMovement = movements.get(0);
			}
			
			
		}
	
		Matrix4 displacement; //NOTE: Displacement means different things depending on mode.
		//in absolute mode its the displacement relative to 0,0,0 in stagespace
		//in relative mode its relative to the last movements end location (which is stored in currentConclumativeMatrix)
		
		//The value we return for absolute motion is relative to worldspace
		//for relative its relative to the objects start position/origin
		if (currentMovement.currenttype == MovementTypes.Absolute){
			//relative to last position in absolute terms
			Matrix4 lastnodesabsoluteposition = objectsNativePosition.cpy().mul(lastNodesLocationMatrix); //in order to work out the new position in abs more it needs the last position as an absolute as well
			
			 displacement = currentMovement.onUpdateAbsolute(currentTimeWithinMovement,lastnodesabsoluteposition); 

			return displacement; //displacement relative to 0,0,0 in stagespace, thus it doesnt need to be multiplied to get it relative to the last position
		
		} else {
		
			 displacement = currentMovement.onUpdateRelative(currentTimeWithinMovement); //currentTimeWithinMovement
			 return lastNodesLocationMatrix.cpy().mul(displacement); //if we are doing a relative motion we multiply current displacement by the last point (that is ConclumativeMatrix)
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
	public void setMovement(ModelInstance object,boolean resumeAfter,Movement... create) {
		if (resumeAfter && old_movements.isEmpty()){
			Gdx.app.log(logstag, "_____________________________________________setting resume after");
			old_movements.clear();
			old_movements.addAll(movements);
			resumeold=true;
		}
		
		movements.clear();
		movements.addAll(new ArrayList<Movement>(Arrays.asList(create))); //new ArrayList<Movement>(Arrays.asList( movements));
		
		if (currentMovement!=null){
			
			Matrix4 displacement;
			if (currentMovement.currenttype == MovementTypes.Absolute){
				
				 displacement = currentMovement.onUpdateAbsolute(currentTimeWithinMovement,object.transform.cpy().mul(lastNodesLocationMatrix)); 
				 
				 lastNodesLocationMatrix.set(displacement);
			} else {
			
				 displacement = currentMovement.onUpdateRelative(currentTimeWithinMovement); //currentTimeWithinMovement
				 lastNodesLocationMatrix.mul(displacement);	//burn the current movements position in at its current point
			
			}
			//Matrix4 displacement = currentMovement.onUpdate(currentTimeWithinMovement); 
			
		}
		
		currentMovementNumber=0;
		currentMovement=movements.get(0);
		
		currentTime = 0; //the current eclipsed time in total;
		currentTimeWithinMovement = 0; //the current eclipsed time within the specific movement
		
		
		
		//totalTime = currentMovement.durationMS; //total time of all movements
		
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
	public Matrix4 getUpdate(float delta, ModelInstance creaturemodel,Matrix4 origin) {
		
		if (currentMovement.currenttype == MovementTypes.Absolute){
			
			Matrix4 displacementFromOrigin = update(delta, origin);
			
			return displacementFromOrigin; //absolute motions arnt relative to where we started from, so we just return it as-is
			
		} else {
			
			Matrix4 displacementFromOrigin = update(delta, origin);
		
			return origin.cpy().mul(displacementFromOrigin);
		
		}
	}

	

	
}
