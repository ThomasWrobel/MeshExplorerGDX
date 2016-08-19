package com.lostagain.nl.me.camera;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lostagain.nl.GWTish.PosRotScale;
import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;
import com.lostagain.nl.GWTish.Management.GWTishModelManagement;
import com.lostagain.nl.GWTish.Management.IsAnimatableModelInstance;

/**
 * In order to more easily handle animations on camera we make them all extend this, and use this ones functions for all updates
 * This should be very similar to AnimatableObject class (later perhaps a interface of Animateable should be made if we have more then these two?)
 * 
 *  
 * 
 * @author Tom on the excellent and helpful advice of Xoppa
 *
 */
public class AnimatablePerspectiveCamera extends PerspectiveCamera implements IsAnimatableModelInstance {

	final static String logstag = "ME.AnimatablePerspectiveCamera";

	//Use this instead of the models matrix
	public final PosRotScale transState = new PosRotScale();

	//This is only here to mask the superclasss one. (or at least help prevent it from accidently being used, as it still can with a cast)
	//  private Matrix4 transform = new Matrix4();
	//  private Vector3 position; //use super. instead!
	//  private Vector3 direction; //use super. instead!
	//  private Vector3 up; //use super. instead!


	HashMap<IsAnimatableModelInstance,PosRotScale> attachlist = new HashMap<IsAnimatableModelInstance,PosRotScale>();


	//NOTE: A lot of these things are purely here to emulate animatable object functions as we are implementing the interface of that 
	//-------------

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
	 * Determines if we inherit visibility from parent.
	 * NOTE: If inheriting both the parents visibility AND the local visibility have to be set to true for this object to render
	 * @param model
	 */
	public boolean inheritVisibility = true;

	/**
	 * Determines if we should render this icon or not.
	 * 
	 * NOTE: If inheriting both the parents visibility AND the local visibility have to be set to true for this object to render
	 * Please use "isVisible()" to check for effective visibility
	 * @param model
	 */	
	public boolean localVisibility = true;

	/** What THIS object is attached too, if anything **/
	protected IsAnimatableModelInstance parentObject = null;

	
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

	/** 
	 * Should be called after ANY set of change to its transState before it will be reflected in the model visually
	 * This can be heavily optimized by not calling the super.update at all, but rather combining it into this function
	 * Note the overlay with the way the direction and up is set
	 * **/
	public void sycnTransform() {

		super.position.set(transState.position);

		//	Gdx.app.log(logstag, "______________________new     position="+super.position.x+","+super.position.y+","+super.position.z+")");

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
	 * Lets you stick one object to another. Its position and rotation will shift as its parent does. <br>
	 * You can specific a PosRotScale for its displacement from parent. <br>
	 * Note; This should check for inheritance loops at some point it does not at the moment <br>
	 * <br>
	 * Note; Displacement is not copied. Changes to the given displacement will continue to effect the objects position<br>
	 * Note3: Displacement should also contain any existing scaleing you have applied, **/
	public void attachThis(IsAnimatableModelInstance objectToAttach, PosRotScale displacement){

		
		//add if not already there else update
		if (!attachlist.containsKey(objectToAttach))
		{


			//associate this as the parent object
			objectToAttach.setParentObject(this);
			
			attachlist.put(objectToAttach, displacement);

			
		} else {

			Gdx.app.log(logstag,"updating attachment:"+displacement.toString());
			attachlist.put(objectToAttach, displacement); 
			//we now should visually sycn positions
			sycnAttachedObjectsPosition(objectToAttach);
		}

		
	}



	private void updateAllAttachedObjects(){

	//	Gdx.app.log(logstag,"_____________________________________updateAllAttachedObjects ="+attachlist.size()); 

		for (IsAnimatableModelInstance object : attachlist.keySet()) {

			sycnAttachedObjectsPosition(object);
			//
			//	Gdx.app.log(logstag,"_____________________________________cam position is ="+transState.position); 
//
			//	Gdx.app.log(logstag,"_____________________________________displacement ="+displacement); 
			//.app.log(logstag,"_____________________________________setting attached position to ="+newposition); 

		}
	}

	protected void sycnAttachedObjectsPosition(IsAnimatableModelInstance object) {
		PosRotScale displacement = attachlist.get(object);
		PosRotScale newposition = transState.copy().displaceBy(displacement);			
		object.setTransform(newposition);
	}


	public void updateAtachment(IsAnimatableModelInstance object,
			PosRotScale lazerbeamdisplacement) {

		attachlist.put(object, lazerbeamdisplacement);

	}
	
	@Override
	public Quaternion getAngleTo(Vector3 target, Vector3 Axis) {
		return getAngleTo(target, Axis);
	}
	
	@Override
	public Quaternion getAngleTo(IsAnimatableModelInstance target) {
		return  getAngleTo(target, new Vector3(1,0,0));
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getAngleTo(com.lostagain.nl.me.newmovements.AnimatableModelInstance, com.badlogic.gdx.math.Vector3)
	 */

	@Override
	public Quaternion getAngleTo(IsAnimatableModelInstance target, Vector3 Axis) {

		Vector3 thisPoint   = this.transState.position.cpy();
		Vector3 targetPoint = target.getTransform().position.cpy();

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

	@Override
	public void removeAttachment(IsAnimatableModelInstance objectToRemove){

		if (attachlist.containsKey(objectToRemove))
		{
			attachlist.remove(objectToRemove);
			//remove this as the parent object
			objectToRemove.setParentObject(null);
		}


	}


	@Override
	public void setInheritedPosition(boolean inheritedPosition) {
		this.inheritedPosition = inheritedPosition;
	}

	@Override
	public void setInheritedRotation(boolean inheritedRotation) {
		this.inheritedRotation = inheritedRotation;
	}

	@Override
	public void setInheritedScale(boolean inheritedScale) {
		this.inheritedScale = inheritedScale;
	}

	/**
	 * sets the visibility to be inherited from any parent object.
	 */
	@Override
	public void setInheritedVisibility(boolean inheritVisibility) {
		this.inheritVisibility = inheritVisibility;

		//update our visibility
		//if our local visibility is false we just ensure we are hidden, nothing else to change
		if (localVisibility==false){
			this.hide(false);


			return;
		} else {
			//if our local visibility is true then we base it on the parent setting
			if (this.parentObject.isVisible()){
				this.show(false); //NOTE the false, this is used so we dont disturb the local visibility setting
			} else {
				this.hide(false);
			}


		}

	}


	/**
	 * returns no opp
	 */
	public void hide(){	
		//no opp
	}	

	/**
	 * returns no opp
	 */
	public void hide(boolean setlocalVisibility){		
		//no opp
	}

	/**
	 * returns no opp
	 */
	public void show(){	
		//no opp
	}

	/**
	 * returns no opp
	 */
	public void show(boolean setlocalVisibility){		

		//no opp
	}
	/**
	 * returns no opp
	 */
	@Override
	public float getScaledWidth(){		
		return 0;
	}

	/**
	 * returns no opp
	 */
	@Override
	public float getScaledHeight(){		
		return 0;
	}
	/**
	 * returnsno opp
	 */
	@Override
	public float getWidth(){
		//---
		return 0;
	}
	/**
	 * returns no opp
	 */
	@Override
	public float getHeight(){
		//----
		return 0;
	}

	
	/**
	 * returns current visibility
	 */
	@Override
	public boolean isVisible() {
		
		//if local visibility is false, or we are not inheriting the visibility, then our localvisibility should match are visibility
		if (getParentObject()==null || localVisibility==false || !inheritVisibility ){
			return localVisibility;
		}
		
		///if we are inheriting and we are not hidden then our visibility should match our parents
		return getParentObject().isVisible();
				
	}
	
	
	@Override
	public void inheritTransform ( PosRotScale newState) {

		if (inheritedPosition){
			transState.position.set(newState.position);
		}
		if (inheritedRotation){
			transState.rotation.set(newState.rotation);
		}
		if (inheritedScale){

			//transState.scale.set(newState.scale);
			setToscale(newState.scale,false);


		}

		sycnTransform();
	}


	/**
	 * returns no opp
	 */
	@Override
	public void setToScale(Vector3 scale) {	
		//setToscale(scale,true);
	}


	/**
	 * returns no opp
	 */
	private void setToscale(Vector3 scale,boolean sycn) {		

		//transState.scale.set(scale);

		//if we have had our scale change we need to also scale our attachment points position
		//this is so they stayed pinned to the same place
		//for (AnimatableModelInstance object : attachlist.keySet()) {

		//	PosRotScale displacement = attachlist.get(object);
		//	displacement.position.scl(scale);
		//	updateAtachment(object, displacement);

		//}
		//if (sycn){
		//	sycnTransform();
		//}
	}

	/**
	 * returns no opp
	 */
	@Override
	public Matrix4 getMatrixTransform() {		
		return null;
	}


	/**
	 * returns no opp
	 */
	public Vector3 getCenterOnStage(){
		return null;
	}


	/**
	 * returns no opp
	 */
	@Override
	public Vector3 getCenterOfBoundingBox() {
		return null;
	}

	/**
	 * returns no opp
	 */
	@Override
	public BoundingBox getLocalCollisionBox() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * returns no opp
	 */
	@Override
	public BoundingBox getLocalBoundingBox() {
		return null;
	}


	@Override
	public void lookAt(IsAnimatableModelInstance target){			
		Quaternion angle = getAngleTo(target);			
		setToRotation(angle);			
	}

	@Override
	public void lookAt(IsAnimatableModelInstance target, Vector3 Axis){			
		Quaternion angle = getAngleTo(target,Axis);			
		setToRotation(angle);			
	}
	
	
	@Override
	public void lookAt(Vector3 target, Vector3 Axis) {
		Quaternion angle = getAngleTo(target,Axis);			
		setToRotation(angle);	
	}
	


	
	public PosRotScale getAttachmentsPoint(AnimatableModelInstance object){
		return attachlist.get(object);
	}

	@Override
	public Set<IsAnimatableModelInstance> getAttachments() {
		return attachlist.keySet();
	}

	@Override
	public Set<IsAnimatableModelInstance> getAllAttachments() {

		Set<IsAnimatableModelInstance> attachments = new HashSet();
		Set<IsAnimatableModelInstance> direct_children = new HashSet<IsAnimatableModelInstance>(attachlist.keySet());
		
		attachments.addAll(direct_children);
		
		for (IsAnimatableModelInstance childAttach : direct_children) {
			
			attachments.addAll(childAttach.getAllAttachments());
			
		}				
		
		
		return attachments;					
		
	}

	/**
	 * returns no opp
	 */
	@Override
	public boolean isInheriteingVisibility() {
		return false;
	}
	/**
	 * returns no opp
	 */
	@Override
	public BoundingBox getLocalCollisionBox(boolean onceOnly) {
		return null;
	}

	@Override
	public IsAnimatableModelInstance getParentObject() {
		return parentObject;
	}

	@Override
	public void setParentObject(IsAnimatableModelInstance parentObject) {
		this.parentObject = parentObject;
		if (this.parentObject!=null){
			this.parentObject.removeAttachment(this);	
		}
	}

	@Override
	public PosRotScale getTransform() {
		return this.transState;
	}


	/**
	 * Note; returns the displacement specification of the specified object from this one. NOT a copy of it.
	 * Changing the returned value will change the displacement
	 */
	public PosRotScale getAttachmentsPoint(IsAnimatableModelInstance object){
		return attachlist.get(object);
	}

	@Override
	public void fireTouchUp() {
		// TODO Auto-generated method stub
		
	}

}
