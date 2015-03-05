package com.lostagain.nl.me.camera;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * In order to more easily handle animations on camera we make them all extend this, and use this ones functions for all updates
 * This should be very similar to AnimatableObject class (later perhaps a interface of Animateable should be made if we have more then these two?)
 * 
 *  
 * 
 * @author Tom on the excellent and helpful advice of Xoppa
 *
 */
public class AnimatablePerspectiveCamera extends PerspectiveCamera {

	final static String logstag = "ME.AnimatablePerspectiveCamera";
	//Use this instead of the models matrix
    public final PosRotScale transState = new PosRotScale();
    
    //This is only here to mask the superclasss one. (or at least help prevent it from accidently being used, as it still can with a cast)
  //  private Matrix4 transform = new Matrix4();
  //  private Vector3 position; //use super. instead!
  //  private Vector3 direction; //use super. instead!
  //  private Vector3 up; //use super. instead!
    
        
    HashMap<AnimatableModelInstance,PosRotScale> attachlist = new HashMap<AnimatableModelInstance,PosRotScale>();
    		
    
    // this is just an example constructor, make sure to implement the constructor you need
    public AnimatablePerspectiveCamera (float fieldOfViewY, float viewportWidth, float viewportHeight) {
    	super(fieldOfViewY, viewportWidth, viewportHeight);
    }
	
	 //Method used to update the transform
    public void setTransform ( PosRotScale newState) {
    	
    	transState.position.set(newState.position);
    	transState.rotation.set(newState.rotation);
    	transState.scale.set(newState.scale);
    	    	
    	
        sycnTransform();
    }

    /** should be called after ANY set of change to its transState before it will be reflected in the model visualy
     * This can be heavily optimized by not calling the super.update at all, but rather combining it into this function
     * Note the overlay with the way the direction and up is set**/
    public void sycnTransform() {
    
    	super.position.set(transState.position);
    	
    	Gdx.app.log(logstag, "______________________new     position="+super.position.x+","+super.position.y+","+super.position.z+")");
		
    	//these rotations might not be correct
    	final Vector3 tmp = new Vector3();
    	
    	Vector3 axis = new Vector3 ();
    	transState.rotation.getAxisAngle(axis);
    	
    	Vector3 newD = new Vector3(0, 0, -1).rotate(axis, transState.rotation.getAngle());
    	direction.set(newD);
    	Vector3 newU = new Vector3(0, 1, 0).rotate(axis, transState.rotation.getAngle());
    	up.set(newU);
    	
    	super.view.setToLookAt(position, tmp.set(position).add(direction), up);
    	super.view.rotate(transState.rotation);
    	
    	
		//super.transform.set(transState.position, transState.rotation, transState.scale);
    	super.update();
    	
    	//now update all attached objects too;
    	updateAllAttachedObjects();
	}

	/*** Convince to quickly set the position to a copy of this one. If doing a more complex change make a PosRotScale and call setTransform **/
	public void setToPosition(Vector3 vector3) {		
		transState.position.set(vector3);
		sycnTransform();
	}
	/*** Convince to quickly set the rotation to a copy of this one. If doing a more complex change make a PosRotScale and call setTransform **/
	public void setToRotation(Quaternion vector3) {		
		transState.rotation.set(vector3);
		sycnTransform();
	}
	
	/*** Convince to quickly set the rotation to a copy of this one. If doing a more complex change make a PosRotScale and call setTransform **/
	public void rotate(Vector3 axis, float angle) {
		
		
		transState.rotation.mul(new Quaternion(new Vector3(axis),angle));
		
		
		
		sycnTransform();
		
		
	}
	
	/*** Convince to quickly set the scale to a copy of this one. If doing a more complex change make a PosRotScale and call setTransform **/
	public void setToscale(Vector3 scale) {		
		transState.scale.set(scale);
		sycnTransform();
	}
	

    
	/** stick to
	 * Lets you stick one object to another. Its position and rotation will shift as its parent does.
	 * You can specific a PosRotScale for its displacement from parent.
	 * Note; This should check for inheritance loops at some point it does not at the moment**/
	public void attachThis(AnimatableModelInstance objectToAttach, PosRotScale displacement){

		Gdx.app.log(logstag,"_____________________________________adding object "); 
		
		//add if not already there
		if (!attachlist.containsKey(objectToAttach))
		{
			attachlist.put(objectToAttach, displacement);
		}
		

		Gdx.app.log(logstag,"_____________________________________total objects now: "+attachlist.size()); 
		
		
	}
	
	private void updateAllAttachedObjects(){

		Gdx.app.log(logstag,"_____________________________________updateAllAttachedObjects ="+attachlist.size()); 
		
		for (AnimatableModelInstance object : attachlist.keySet()) {
			
			PosRotScale newposition = transState.copy().displaceBy(attachlist.get(object));
			object.setTransform(newposition);

			Gdx.app.log(logstag,"_____________________________________cam position is ="+transState.position); 
			Gdx.app.log(logstag,"_____________________________________setting attached position to ="+newposition); 
			
		}
	}
	
	
}
