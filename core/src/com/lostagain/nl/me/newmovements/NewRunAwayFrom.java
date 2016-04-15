package com.lostagain.nl.me.newmovements;

import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;

//untested!
public class NewRunAwayFrom  {


	final static String logstag = "ME.NewRunAwayFrom";
	
	
	/**
	 *  Will quickly fade an object, then back away a bit. Then, finally, turn quickly and run off.
	 *  
	 * @param originObject
	 * @param runawayfrom
	 * @param duration
	 * @return
	 */
	
	static public NewMovement[] create(AnimatableModelInstance originObject, AnimatableModelInstance runawayfrom, int duration) {
						
		//return the array		
		return create(originObject, runawayfrom.transState.position.x, runawayfrom.transState.position.y,  duration);
	}


	public static NewMovement[] create(AnimatableModelInstance originObject, float eX,
			float eY, int i) {
		
		
		//make the component movements
		
		//face towards quickly
		NewRotateLeft facemovement = NewFaceTowards.create(originObject, eX,eY, 150);
		
		//back away slowly
		NewForward backward = new NewForward(-70, 3000);
		
		//turn again quickly
		NewRotateLeft turnaround = new NewRotateLeft(180,150);
		
		//run off
		NewForward run = new NewForward(380, 400);
				
		//turn again quickly
		NewRotateLeft turnback = new NewRotateLeft(180,1000);
				
		//make array
		NewMovement movements[] = {facemovement,backward,turnaround,run,turnback};
		
		//return the array		
		return movements;
	}


}
