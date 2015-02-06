package com.lostagain.nl.me.movements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class FaceAndMoveTo  {


	final static String logstag = "ME.FaceTowards";
	//static Matrix4 Left = new Matrix4().setToRotation(0, 0, 1, 90);
	static public Movement[] create(ModelInstance originObject, ModelInstance targetObject, int duration) {
		
		
		Vector3 posT = new Vector3();
		targetObject.transform.getTranslation(posT);		
			
		return create(originObject, posT.x, posT.y, duration);
	}
	
	
	public static Movement[] create(ModelInstance originObject,
			Vector3 dropsPositionAsVector, int duration) {

		Vector2 posT = new Vector2(dropsPositionAsVector.x,dropsPositionAsVector.y);
		
		return create(originObject, posT.x, posT.y, duration);
	}

	/**
	 * currently a crude method that only supports rotations on a 2d plane
	 * If this system is to be expanded for 3d movements, a better way to find the difference between the current angle
	 * and the needed angle will need to be found.
	 * (as motions are relative to face something it needs to know where its facing already so it knows how far to turn)
	 * 
	 * @param originObject
	 * @param ex
	 * @param ey
	 * @param duration
	 * @return
	 */
	public static  Movement[] create(ModelInstance originObject, float ex, float ey, int duration) {

		Vector3 posO = new Vector3();
		originObject.transform.getTranslation(posO);
		
		Quaternion rotation = new Quaternion();
		originObject.transform.getRotation(rotation,true); //existing rotation
		
		 Vector3 axisVec = new Vector3();
	        float existingangle = (float) (rotation.getAxisAngle(axisVec) * axisVec.nor().z);
	        existingangle = existingangle < 0 ? existingangle + 360 : existingangle; //convert <0 values
		
		Gdx.app.log(logstag,"_____________________________________existing angle="+existingangle); //relative to x axis pointing left anticlockwise
	
		
		Vector2 fromPoint = new Vector2(posO.x,posO.y);
		Vector2 tooPoint  = new Vector2(ex,ey);

		fromPoint.sub(tooPoint);      

		//Log.info("length="+corner2.len());

		float angle =  180+fromPoint.angle(); //absolute angle between the two objects
		Gdx.app.log(logstag,"________target angle="+angle); //should point towards ex/ey 
		float distance = fromPoint.len();

		
		//difference between this angle and existing one
		angle = angle - existingangle;
		
		RotateLeft rot  = new RotateLeft(angle,500);
		Forward forward = new Forward(distance,duration-500);
				
		return new Movement[]{rot,forward};
		
	}


}
