package com.lostagain.nl.me.newmovements;

import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.ModelManagment.RenderOrder;

/**
 * In order to more easily handle animations on objects we make them all extend this, and use this ones functions for all updates.
 * 
 * Aside from easily animating position,rotation and scale, this class also adds "lookAt" abilities, positioning objects relative, 
 * and showing/hiding them by automatically adding/removing them from the render list in model management
 * 
 * 
 * @author Tom on the excellent and helpful advice of Xoppa
 *
 */
public class AnimatableModelInstance extends ModelInstance {
	final static String logstag = "ME.AnimatableModelInstance";

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

	//how the parent object (if any) effects this one;
	/** Determines if the position is inherited **/
	//in future we can have x/y/z separate for more advanced behaviors
	boolean inheritedPosition = true;
	/** Determines if the rotation is inherited **/
	//in future we can have x/y/z separate for more advanced behaviors
	boolean inheritedRotation = true;
	/** Determines if the scale is inherited **/
	boolean inheritedScale = true;

	/**
	 * The local bounding box is the boundary of this object, not counting anything attached to it.
	 * If its bull, it needs to be created again which any call like getWidth() will do automatically.
	 */
	private BoundingBox localBoundingBox;
	
	/**
	 * The collision box is the bounding box multiplied by its current transform.
	 * So, effectively, its the real boundary's the object currently has in the co-ordinate system of
	 * where its positioned.
	 * The collisionBox is updated every time its moved assuming its ever been requested.
	 * collisionBox.mul(super.getMatrixTransform());
	 **/
	private BoundingBox collisionBox;
	
	/**
	 * Determines if its added to the render list or not
	 * @param model
	 */
	public boolean visible = true;

	
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

	//Method used to update the transform from a parent
	//Works exactly like setTransform, but firsts tests if a particular type of transform should be inherited
	public void inheritTransform ( PosRotScale newState) {
		if (inheritedPosition){
			transState.position.set(newState.position);
		}
		if (inheritedRotation){
			transState.rotation.set(newState.rotation);
		}
		if (inheritedScale){
			transState.scale.set(newState.scale);
		}
		sycnTransform();
	}

	/** should be called after ANY set of change to its transState before it will be reflected in the model visually**/
	public void sycnTransform() {
		super.transform.set(transState.position, transState.rotation, transState.scale);
		
		//now we check if theres a collision box to update
		if (collisionBox!=null){
			recalculateCollisionBox();
		}

		//now update all attached objects too;
		updateAllAttachedObjects();
	}

	/*** Convince to quickly set the position. If doing a more complex change make a PosRotScale and call setTransform **/
	public void setToPosition(Vector3 vector3) {		
		transState.position.set(vector3);
		sycnTransform();
	}
	/*** Convince to quickly set the rotation. If doing a more complex change make a PosRotScale and call setTransform **/
	public void setToRotation(Quaternion angle) {		
		transState.rotation.set(angle);
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
		visible = false;
		
		//we also hide things positioned relatively to this. Nothing overrides this
		for (AnimatableModelInstance object : attachlist.keySet()) {
			object.hide();
		}
	}

	/** shows it by adding it to the render lists.
	 * This only works if it was previously hidden. It should currently be added manually once first so it knows its render order setting
	 * This might change in future **/
	public void show(){		
		ModelManagment.addmodel(this,currentRenderPlacement);
		visible = true;
		
		//we also show things positioned relatively to this unless they have visible false set
		for (AnimatableModelInstance object : attachlist.keySet()) {
			if (object.visible){
				object.show();
			}
		}
	}

	
	public float getWidth(){
		if (localBoundingBox==null){
			createBoundBox();
		}
		return localBoundingBox.getWidth();
	}
	public float getHeight(){
		if (localBoundingBox==null){
			createBoundBox();
		}
		return localBoundingBox.getHeight();
	}
	
	public Vector3 getCenter(){
		if (localBoundingBox==null){
			createBoundBox();
		}
		Vector3 center = new Vector3();
		return localBoundingBox.getCenter(center);
	}
	
	private void createBoundBox() {
		localBoundingBox = new BoundingBox();
		 super.calculateBoundingBox(localBoundingBox);
		 	
		
	}
	

	private void recalculateCollisionBox() {
		
		//the bounding box is a prerequisite
		if (localBoundingBox==null){
			createBoundBox();
		}
		//so is an existing collisionBox
		if (collisionBox==null){
			collisionBox = new BoundingBox();
		}
		//ok, now we know we have both we set one to the other
		collisionBox.set(localBoundingBox);
		//Then the collision box gets multiplied by our current position so its boundarys match the real space co-ordinates
		collisionBox.mul(getMatrixTransform());

		Gdx.app.log(logstag,"collision box="+getMatrixTransform());
		Gdx.app.log(logstag,"collision box="+collisionBox);
	}
	
	public BoundingBox getLocalCollisionBox() {
		if (collisionBox==null){
			recalculateCollisionBox();
		}
		return collisionBox;
	}
	
	
	public BoundingBox getLocalBoundingBox() {
		if (localBoundingBox==null){
			createBoundBox();
		}
		return localBoundingBox;
	}
	/** 
	 * Lets you stick one object to another. Its position and rotation will shift as its parent does.
	 * You can specific a PosRotScale for its displacement from parent.
	 * Note; This should check for inheritance loops at some point it does not at the moment
	 * 
	 * Note; Displacement is not copied. Changes to the given displacement will continue to effect the objects position 
	 * **/
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
			object.inheritTransform(newposition);


		}
	}

	public void updateAtachment(AnimatableModelInstance object,
			PosRotScale displacement) {

		attachlist.put(object, displacement);

	}


	/** Sets this model to "lookat" the target models vector3 location by aligning this models xAxis(1,0,0) to point at the target **/
	public void lookAt(AnimatableModelInstance target){			
		Quaternion angle = getAngleTo(target);			
		setToRotation(angle);			
	}

	/** Sets this model to lookat the target models vector3 location **/
	public void lookAt(AnimatableModelInstance target, Vector3 Axis){			
		Quaternion angle = getAngleTo(target,Axis);			
		setToRotation(angle);			
	}

	/** 
	 * Method to find the axis-angle between this AnimatableModelInstances and another relative to the xAxis (1,0,0)
	 * 
	 * @return Quaternion of angle 
	 * **/
	public Quaternion getAngleTo(AnimatableModelInstance target) {
		return  getAngleTo(target, new Vector3(1,0,0));
	}

	/** 
	 * Method to find the axis-angle between this AnimatableModelInstances and another relative to the xAxis.
	 * 
	 * @return Quaternion of angle 
	 * **/

	public Quaternion getAngleTo(AnimatableModelInstance target, Vector3 Axis) {

		Vector3 thisPoint = this.transState.position.cpy();
		Vector3 targetPoint = target.transState.position.cpy();

		//get difference (which is the same as target relative to 0,0,0 if this point was 0,0,0)
		targetPoint.sub(thisPoint);			
		targetPoint.nor();

		//test if they are in the same the same place, if so just return default angle
		if (targetPoint.len() < 0.01f) 
		{
			return new Quaternion();
		}

		//else we use this difference and find its angle relative to the Axis
		//Vector3 xAxis = new Vector3(1,0,0);

		Quaternion result = new Quaternion();		

		result.setFromCross(Axis,targetPoint);


		return result;

	}

	public Set<AnimatableModelInstance> getAttachments() {

		return attachlist.keySet();
	}

	public void setInheritedPosition(boolean inheritedPosition) {
		this.inheritedPosition = inheritedPosition;
	}

	public void setInheritedRotation(boolean inheritedRotation) {
		this.inheritedRotation = inheritedRotation;
	}

	public void setInheritedScale(boolean inheritedScale) {
		this.inheritedScale = inheritedScale;
	}

	public boolean isVisible() {
		return visible;
	}

}
