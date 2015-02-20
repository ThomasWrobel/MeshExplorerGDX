package com.lostagain.nl.me.newmovements;

import com.badlogic.gdx.math.Matrix4;

public class NewForward extends NewMovement {
	
	
	public NewForward(float distance, float durationMS) {
		super(new PosRotScale(distance,0f,0f),durationMS);
		//super(new Matrix4().setToTranslation(distance, 0, 0), durationMS); //note; we have to do this all in one line because super must be the first thing in a constructor

		currenttype = MovementTypes.Relative;
	}

}
