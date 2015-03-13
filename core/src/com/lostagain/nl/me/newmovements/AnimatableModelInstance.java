package com.lostagain.nl.me.newmovements;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * In order to more easily handle animations on objects we make them all extend this, and use this ones functions for all updates
 * 
 * 
 * @author Tom on the excellent and helpful advice of Xoppa
 *
 */
public class AnimatableModelInstance extends ModelInstance {
	
	//Use this instead of the models matrix
    public final PosRotScale transState = new PosRotScale();
    
    //This is only here to mask the superclasss one. (or at least help prevent it from accidently being used, as it still can with a cast)
    //If this is used in any way there is a problem with the code!
    //check for the yellow squiggle under it in eclipse, if its missing theres likely a problem!
    private Matrix4 transform = new Matrix4();
    
    // this is just an example constructor, make sure to implement the constructor you need
    public AnimatableModelInstance (Model model) {
        super(model);
    }
	
	 //Method used to update the transform
    public void setTransform ( PosRotScale newState) {
    	
    	transState.position.set(newState.position);
    	transState.rotation.set(newState.rotation);
    	transState.scale.set(newState.scale);
    	    	
        sycnTransform();
    }

    /** should be called after ANY set of change to its transState before it will be reflected in the model visualy**/
    public void sycnTransform() {
		super.transform.set(transState.position, transState.rotation, transState.scale);
	}

	/*** Convince to quickly set the position. If doing a more complex change make a PosRotScale and call setTransform **/
	public void setToPosition(Vector3 vector3) {		
		transState.position.set(vector3);
		sycnTransform();
	}
	/*** Convince to quickly set the rotation. If doing a more complex change make a PosRotScale and call setTransform **/
	public void setToRotation(Quaternion vector3) {		
		transState.rotation.set(vector3);
		sycnTransform();
	}
	/*** Convince to quickly set the scale. If doing a more complex change make a PosRotScale and call setTransform **/
	public void setToscale(Vector3 scale) {		
		transState.scale.set(scale);
		sycnTransform();
	}

	/** try to avoid using this, use the transState to update/change things then sycn to reflect them in the instance.
	 * This is just here when you need to get the transform, dont change it with this **/
	public Matrix4 getMatrixTransform() {		
		return super.transform;
	}
    
}
