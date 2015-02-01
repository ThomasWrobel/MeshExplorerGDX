package com.lostagain.nl.me.movements;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.lostagain.nl.me.movements.Movement.MovementTypes;

public class MovementController {

	private static String logstag="ME.MovementController";
	
	
	Movement currentMovement = new Forward(300,8500); //just a test for now
	int currentMovementNumber = 0;
	
	ArrayList<Movement> movements = new ArrayList<Movement>();
	
	ArrayList<Movement> old_movements = new ArrayList<Movement>();
	boolean resumeold = false;
	
	//the matrix corresponding to the start of the last movement
	Matrix4 currentConclumativeMatrix = new Matrix4();
	
	//time keeping
	float currentTime = 0; //the current eclipsed time in total;
	float currentTimeWithinMovement = 0; //the current elipsed time within the specific movement
	float totalTime = 10000; //total time of all movements
	
	public MovementController(Movement... movements) {
		super();
		this.movements = new ArrayList<Movement>(Arrays.asList( movements));
		
		currentMovement = this.movements.get(0);
		
		//totalTime 
	}

	public Matrix4 update(float delta){

		if (currentMovement==null){
			return currentConclumativeMatrix;
		}
		
		//convert to ms
		delta = delta*1000.0f;
		
		
		//Gdx.app.log(logstag, "______________________________________________delta="+delta);
		currentTime = currentTime+delta;
		currentTimeWithinMovement = currentTimeWithinMovement + delta;

	//	Gdx.app.log(logstag, "______________________________________________currentTime="+currentTime);
	//	Gdx.app.log(logstag, "______________________________________________currentTimeWithinMovement="+currentTimeWithinMovement);
		
		if (currentMovement!=null && currentTimeWithinMovement>currentMovement.durationMS){
			
			//get new position so far (this can be thought of the start of each vertext on the path being formed)
			currentConclumativeMatrix.mul(currentMovement.destination);	
			
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
					return currentConclumativeMatrix;	
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
					return currentConclumativeMatrix;	
				}
			}
			
			
			currentMovement = movements.get(currentMovementNumber);
			
			if (currentMovement.currenttype==MovementTypes.REPEAT){
				Gdx.app.log(logstag, "_____________________________________________REPEATING=");
				currentMovementNumber=0;
				currentMovement = movements.get(0);
			}
			
			
		}
	//	if (currentTime>totalTime){
			
			//return currentConclumativeMatrix;
		//}
		
		
		
		Matrix4 displacement = currentMovement.onUpdate(currentTimeWithinMovement); //currentTimeWithinMovement
		
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
		
		return currentConclumativeMatrix.cpy().mul(displacement);//currentMovement.onUpdate(currentTimeWithinMovement));
		
	}

	/**clears all movements and
	 * sets the current movement to the specified one
	 * @param movement
	 */
	public void setMovement(boolean resumeAfter,Movement movement) {
		
		setMovement(resumeAfter,movement);
		
		/*
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
		totalTime = movement.durationMS; //total time of all movements*/
	}
	
	/**clears all movements and
	 * sets the current movements to the specified ones
	 * @param movement
	 */
	public void setMovement(boolean resumeAfter,Movement... create) {
		if (resumeAfter){
			Gdx.app.log(logstag, "_____________________________________________setting resume after");
			old_movements.clear();
			old_movements.addAll(movements);
			resumeold=true;
		}
		
		movements.clear();
		movements.addAll(new ArrayList<Movement>(Arrays.asList(create))); //new ArrayList<Movement>(Arrays.asList( movements));
		
		if (currentMovement!=null){
			Matrix4 displacement = currentMovement.onUpdate(currentTimeWithinMovement); 
			currentConclumativeMatrix.mul(displacement);	//burn the current movements position in at its current point
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

	

	
}
