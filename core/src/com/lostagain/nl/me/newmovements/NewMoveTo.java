package com.lostagain.nl.me.newmovements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
//import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.lostagain.nl.GWTish.PosRotScale;
import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.NewMovement.MovementTypes;

public class NewMoveTo  {


	final static String logstag = "ME.MoveTo";
	
	//static Matrix4 Left = new Matrix4().setToRotation(0, 0, 1, 90);
	
	static public NewMovement create(AnimatableModelInstance originObject, AnimatableModelInstance targetObject, int duration) {
		
		Vector3 posT = targetObject.transState.position.cpy();	
			
		return create(originObject.transState, posT.x, posT.y,posT.z, duration);
	}
	
	
	static public NewMovement create(PosRotScale originLoc,Vector3 destination_loc, int duration) {

	//	Matrix4 destinationmatrix = originLoc.cpy().setTranslation(destination_loc);		
		PosRotScale destinationState = originLoc.copy(); //make a new PosRotScale from the original location
		
		//but we change its position
		destinationState.setToPosition(destination_loc);

		Gdx.app.log(logstag, "_______________requested pos  :"+destination_loc);
		
				
		NewMovement newmovement= new NewMovement(destinationState,duration);
		newmovement.currenttype = MovementTypes.Absolute;

		Gdx.app.log(logstag, "_______________destination pos after creation:"+newmovement.destination.position);
			
		return newmovement;
	}

	/**
	 * moves to a position without effecting rotation
	 * 
	 * @param originObject
	 * @param ex
	 * @param ey
	 * @param duration
	 * @return
	 */
	public static  NewMovement create(PosRotScale originLoc, float ex, float ey,float ez, int duration) {

/*
		Vector3 posO = new Vector3();
		originObject.transform.getTranslation(posO);
		
		Quaternion orotation = new Quaternion();
		originObject.transform.getRotation(orotation,true); //existing rotation
		
		//first we create a matrix for the destination location reusing the same rotation
		 Vector3 destination_loc = new Vector3(ex,ey,ez);
		 Vector3 destination_scale = new Vector3(1,1,1);
		// Matrix4 destination = new Matrix4(destination_loc,orotation,destination_scale);
		 
		 //the difference is the new movement
		Matrix4 oldloc = originObject.transform.cpy();
		
		 Gdx.app.log(logstag, "_____________________________________________destination_loc:"+destination_loc.x+","+destination_loc.y+","+destination_loc.z);
			
		 Vector3 destination_loc2 =  destination_loc.mul(oldloc);
		 
		 Gdx.app.log(logstag, "________________________=______destination_loc:"+destination_loc2.x+","+destination_loc2.y+","+destination_loc2.z);
			
			
		Matrix4 destinationmatrix = new Matrix4().setTranslation(destination_loc2);
		
		*/
		 Vector3 destination_loc = new Vector3(ex,ey,ez);
		 
		return create( originLoc, destination_loc,  duration);
		
	}


	public static NewMovement create(AnimatableModelInstance creaturemodel,
			Vector3 dropsPositionAsVector, int duration) {
		
		
		return create(creaturemodel.transState, dropsPositionAsVector,  duration);
	}


	


}
