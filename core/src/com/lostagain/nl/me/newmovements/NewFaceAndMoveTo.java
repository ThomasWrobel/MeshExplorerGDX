package com.lostagain.nl.me.newmovements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
//import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;

public class NewFaceAndMoveTo  {


	final static String logstag = "ME.NewFaceAndMoveTo";
	//static Matrix4 Left = new Matrix4().setToRotation(0, 0, 1, 90);
	static public NewMovement[] create(AnimatableModelInstance originObject, AnimatableModelInstance targetObject, int duration) {
		
		

		Vector3 posT = targetObject.transState.position.cpy();	
			
		return create(originObject, posT.x, posT.y, duration);
	}
	
	
	public static NewMovement[] create(AnimatableModelInstance originObject,
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
	 * Note; This works good enough for most 2d uses right now but isnt totally accurate
	 * 
	 * @param originObject
	 * @param ex
	 * @param ey
	 * @param duration
	 * @return
	 */
	public static  NewMovement[] create(AnimatableModelInstance originObject, float ex, float ey, int duration) {
		

		Vector3 posO = originObject.transState.position.cpy();	
		//Vector3 posO = new Vector3();
		//originObject.transform.getTranslation(posO);
		
		Quaternion rotation = originObject.transState.rotation.cpy();	
		//Quaternion rotation = new Quaternion();
		//originObject.transform.getRotation(rotation,true); //existing rotation
		
		 Vector3 axisVec = new Vector3();
	        float existingangle = (float) (rotation.getAxisAngle(axisVec) * axisVec.nor().z);
	        existingangle = existingangle < 0 ? existingangle + 360 : existingangle; //convert <0 values
		
		Gdx.app.log(logstag,"_____________________________________existing angle="+existingangle); //relative to x axis pointing left anticlockwise
	
		//float scalex = originObject.transform.getScaleX(); //get how much the object has been scaled
		//float scaley = originObject.transform.getScaleY();
		
		Vector2 fromPoint = new Vector2(posO.x,posO.y); //we need to scale them down due to how the positions might have been scaled up if the matrix is scaled (I hate how matrixs work :-/ Just cant get it in my head)
		Vector2 tooPoint  = new Vector2(ex,ey);

		fromPoint.sub(tooPoint);      

		//Log.info("length="+corner2.len());

		float angle =  180+fromPoint.angle(); //absolute angle between the two objects
		Gdx.app.log(logstag,"________target angle="+angle); //should point towards ex/ey 
		
		float distance = fromPoint.len();

		
		//difference between this angle and existing one
		angle = angle - existingangle;
		
		NewRotateLeft rot  = new NewRotateLeft(angle,500);
		NewForward forward = new NewForward(distance,duration-500);
				
		return new NewMovement[]{rot,forward};
		
	}


}
