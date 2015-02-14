package com.lostagain.nl.me.movements;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/** rotates relatively to a position **/
public class RelativeScale extends Movement {
	//static Matrix4 Left = new Matrix4().setToRotation(0, 0, 1, 90);
	public RelativeScale(float scale,float durationMS) {
		
		super(new Matrix4().setToScaling(new Vector3(scale,scale,scale)), durationMS);
		
		currenttype = MovementTypes.Relative;
	}

}
