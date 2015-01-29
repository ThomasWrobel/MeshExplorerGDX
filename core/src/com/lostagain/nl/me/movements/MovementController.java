package com.lostagain.nl.me.movements;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;

public class MovementController {

	private static String logstag="ME.MovementController";
	
	
	Movement currentMovement = new RotateLeft(90,10000); //just a test for now
	ArrayList<Movement> movements = new ArrayList<Movement>();
	
	//the matrix corisponding to the start of the last movement
	Matrix4 currentConclumativeMatrix = new Matrix4();
	
	//time keeping
	float currentTime = 0; //the current eclipsed time in total;
	float currentTimeWithinMovement = 0; //the current elipsed time within the specific movement
	float totalTime = 10000; //total time of all movements
	
	public Matrix4 update(float delta){

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
			//reset currentTimeWithinMovement
			currentTimeWithinMovement =0;
						
			//set the new movement
			
			//or set to null if at end
			currentMovement = null;			
			
		}
		if (currentTime>totalTime){
			
			return currentConclumativeMatrix;
		}
		
		
		
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
	
}
