package com.lostagain.nl.me.movements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

public class RunAwayFrom  {


	final static String logstag = "ME.RunAwayFrom";
	
	
	/**
	 *  Will quickly fade an object, then back away a bit. Then, finnally, turn quickly and run off.
	 *  
	 * @param originObject
	 * @param runawayfrom
	 * @param duration
	 * @return
	 */
	
	static public Movement[] create(ModelInstance originObject, ModelInstance runawayfrom, int duration) {
		
		//make the component movements
		Vector3 posT = new Vector3();
		runawayfrom.transform.getTranslation(posT);		
				
		//return the array		
		return create(originObject, posT.x, posT.y,  duration);
	}


	public static Movement[] create(ModelInstance originObject, float eX,
			float eY, int i) {
		
		
		//make the component movements
		
		//face towards quickly
		RotateLeft facemovement = FaceTowards.create(originObject, eX,eY, 150);
		
		//back away slowly
		Forward backward = new Forward(-70, 3000);
		
		//turn again quickly
		RotateLeft turnaround = new RotateLeft(180,150);
		
		//run off
		Forward run = new Forward(380, 400);
				
		//turn again quickly
		RotateLeft turnback = new RotateLeft(180,1000);
				
		//make array
		Movement movements[] = {facemovement,backward,turnaround,run,turnback};
		
		//return the array		
		return movements;
	}


}
