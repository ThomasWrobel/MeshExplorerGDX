package com.lostagain.nl.me.newmovements;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.ModelManagment.RenderOrder;

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
    
    /** a temp variable used to remember if this should be drawn on the background or foreground. 
     * This is used to help hide/show objects remembering their place **/
    RenderOrder currentRenderPlacement=null;
    

    /** list of things attached to this object. These things will all move and rotate with it **/
    HashMap<AnimatableModelInstance,PosRotScale> attachlist = new HashMap<AnimatableModelInstance,PosRotScale>();
    
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

    /** should be called after ANY set of change to its transState before it will be reflected in the model visually**/
    public void sycnTransform() {
		super.transform.set(transState.position, transState.rotation, transState.scale);
		
    	//now update all attached objects too;
    	updateAllAttachedObjects();
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

	/** try to avoid using this, use the transState to update/change things then sync to reflect them in the instance.
	 * This is just here when you need to get the transform, dont change it with this **/
	public Matrix4 getMatrixTransform() {		
		return super.transform;
	}
	
	/** hides it by removing it from the render lists **/
	public void hide(){		
		currentRenderPlacement = ModelManagment.removeModel(this);	
		
		//we also hide things positioned relatively to this
		for (AnimatableModelInstance object : attachlist.keySet()) {
			object.hide();
		}
	}

	/** shows it by adding it to the render lists.
	 * This only works if it was previously hidden. It should currently be added manually once first so it knows its render order setting
	 * This might change in future **/
	public void show(){		
		ModelManagment.addmodel(this,currentRenderPlacement);
		
		//we also show things positioned relatively to this
		for (AnimatableModelInstance object : attachlist.keySet()) {
			object.show();
		}
	}
	
	
	 
		/** 
		 * Lets you stick one object to another. Its position and rotation will shift as its parent does.
		 * You can specific a PosRotScale for its displacement from parent.
		 * Note; This should check for inheritance loops at some point it does not at the moment
		 * 
		 * Note; Displacement is not copied. Changes to the given displacement will continue to effect the objects position**/
		public void attachThis(AnimatableModelInstance objectToAttach, PosRotScale displacement){

		//	Gdx.app.log(logstag,"_____________________________________adding object "); 
			
			//add if not already there
			if (!attachlist.containsKey(objectToAttach))
			{
				attachlist.put(objectToAttach, displacement);
			}
			

		//	Gdx.app.log(logstag,"_____________________________________total objects now: "+attachlist.size()); 
			
			
		}
		
		private void updateAllAttachedObjects(){

			//Gdx.app.log(logstag,"_____________________________________updateAllAttachedObjects ="+attachlist.size()); 
			
			for (AnimatableModelInstance object : attachlist.keySet()) {
				
				PosRotScale newposition = transState.copy().displaceBy(attachlist.get(object));
				object.setTransform(newposition);
	//
			//	Gdx.app.log(logstag,"_____________________________________cam position is ="+transState.position); 
			//	Gdx.app.log(logstag,"_____________________________________setting attached position to ="+newposition); 
				
			}
		}

		public void updateAtachment(AnimatableModelInstance object,
				PosRotScale displacement) {
			
			attachlist.put(object, displacement);
			
		}
	
}
