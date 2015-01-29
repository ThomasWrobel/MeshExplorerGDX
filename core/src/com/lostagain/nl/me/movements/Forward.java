package com.lostagain.nl.me.movements;

import com.badlogic.gdx.math.Matrix4;

public class Forward extends Movement {
	
	
	public Forward(float distance, float durationMS) {
		super(new Matrix4().setToTranslation(distance, 0, 0), durationMS); //note; we have to do this all in one line because super must be the first thing in a constructor

		currenttype = MovementTypes.Forward;
	}

}
