package com.lostagain.nl.me.movements;

import com.badlogic.gdx.math.Matrix4;

public class RotateLeft extends Movement {
	//static Matrix4 Left = new Matrix4().setToRotation(0, 0, 1, 90);
	public RotateLeft(float angleInDeg,float durationMS) {
		
		super(new Matrix4().setToRotation(0, 0, 1, angleInDeg), durationMS);
		currenttype = MovementTypes.Rotate;
	}

}
