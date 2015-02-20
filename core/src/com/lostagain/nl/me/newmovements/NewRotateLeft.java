package com.lostagain.nl.me.newmovements;


/** rotates relatively to a position **/
public class NewRotateLeft extends NewMovement {
	public NewRotateLeft(float angleInDeg,float durationMS) {
		
		super(new PosRotScale().setToRotation(0, 0, 1, angleInDeg), durationMS);
		currenttype = MovementTypes.Relative;
	}

}
