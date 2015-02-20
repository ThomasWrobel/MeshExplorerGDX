package com.lostagain.nl.me.newmovements;

import com.badlogic.gdx.math.Vector3;

/** rotates relatively to a position **/
public class NewRelativeScale extends NewMovement {
	//static Matrix4 Left = new Matrix4().setToRotation(0, 0, 1, 90);
	public NewRelativeScale(float scale,float durationMS) {
		
		//super(new Matrix4().setToScaling(new Vector3(scale,scale,scale)), durationMS);
		super(new PosRotScale().setToScaling(new Vector3(scale,scale,scale)), durationMS);
		
		
		currenttype = MovementTypes.Relative;
	}

}
