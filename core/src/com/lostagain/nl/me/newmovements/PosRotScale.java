package com.lostagain.nl.me.newmovements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Stores position, rotation and scale and some useful functions
 * 
 * The intention is to use this rather then constantly converting to a Matrix4 and back again to interpolate stuff between states
 * 
 * Note;
 * Position - position in world space
 * Rotation - rotation in world space
 * Scale - The objects scale (I think this is thus different to a matrix where it would be the scale or the co-ordinates relative to 0,0,0 or something)
 * 
 * @author Tom
 *
 */
public class PosRotScale {

	private static String logstag="ME.PosRotScale";
	
	Vector3 position = new Vector3();
	Quaternion rotation = new Quaternion();		
	Vector3 scale = new Vector3(1f,1f,1f);
	
	public PosRotScale(Vector3 position, Quaternion rotation, Vector3 scale) {
		super();
		
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		
	}
	/**
	 *  Stores a position, rotation and scale.
	 *  
	 *  Scale defaults to 1f,1f,1f
	 *  The rest 0,0,0
	 *  
	 *  
	 */
	public PosRotScale() {
		
	}
	
	public PosRotScale(Matrix4 lastLocation) {
		
		this.setToMatrix(lastLocation);
		
		
	}

	/**
	 * creates a new pos rot scale with only a position set to x,y,z  scale remains 1,1,1 and rotation 0,0,0
	 * @param x
	 * @param y
	 * @param z
	 */
	public PosRotScale(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	/**
	 * This displaces one PosRotScale by another
	 * The incoming changeBy is treated as being as relative to current object and its rotation
	 * So a change of x/y/z  50,0,0 in position on a object at 10,10,10 will result in 60,10,10 if its angle is zero.  
	 * If the angle is not zero however, it will move 50,0,0 relative to where its currently "looking"  
	 * 
	 * Locations are added relative to the objects rotation
	 * Rotations also just get added 
	 * Scaling, however, gets multiplied
	 * 
	 * @return
	 */
	public PosRotScale displaceBy(PosRotScale changeByThisAmount) {
		
		//prerotate its location (this ensures we are working in co-ordinates relative to the current object)
		PosRotScale changeByThisAmountRot = changeByThisAmount.copy();
		
		Vector3 existingAxis = new Vector3();
		
		float existingAngle = rotation.getAxisAngle(existingAxis);		
		changeByThisAmountRot.position.rotate(existingAxis,existingAngle);
		
		
		
		//------------------	
		position = position.add(changeByThisAmountRot.position);		
		rotation = rotation.mul(changeByThisAmountRot.rotation);
		
		scale = scale.scl(changeByThisAmountRot.scale);

		Gdx.app.log(logstag, " new scale: "+scale.toString() );	
		
		
		return this;
	}
	
	public PosRotScale copy() {
		
		return new PosRotScale(position.cpy(),rotation.cpy(),scale.cpy());
	}

	/**
	 * Not yet sure of this function, especially about scaling position(?)
	 * @param lastLocation
	 */
	public void setToMatrix(Matrix4 lastLocation) {
		
		lastLocation.getTranslation(position); //does this need to be divided by the scale?
		lastLocation.getRotation(rotation);
		lastLocation.getScale(scale); 
		
	}

	/**
	 * replaces the Rotation with the new Rotation  (doesn't effect anything else)
	 * @param this
	 */
	public PosRotScale setToRotation(int i, int j, int k, float angleInDeg) {
		rotation.set(new Vector3(i,j,k), angleInDeg);

		Gdx.app.log(logstag, " setting rot to angle :"+rotation.getAngle());	
		
		
		return this;
	}

	
	/**
	 * replaces the position with the new position  (doesn't effect anything else)
	 * @param destination_loc
	 */
	public PosRotScale setToPosition(Vector3 newposition) {
		position = newposition.cpy();

		return this;
	}

	/**
	 * replaces the scale with the new scale (doesn't effect anything else)
	 * @param newscale
	 * @return
	 */
	public PosRotScale setToScaling(Vector3 newscale) {
		scale = newscale.cpy();
		
		return this;
	}

	/**
	 * makes a matrix from this PosRotScale 
	 * 
	 * basically; new Matrix4(position,rotation.nor(),scale);
	 * 
	 * Note the rotation is normalized, which is required by Matirx4
	 * 
	 * @return
	 */
	public Matrix4 createMatrix() {
		// TODO Auto-generated method stub
		return  new Matrix4(position,rotation.nor(),scale);
	}
	
	
	
}
