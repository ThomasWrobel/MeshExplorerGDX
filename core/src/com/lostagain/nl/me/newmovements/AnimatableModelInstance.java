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
public class AnimatableModelInstance extends ModelInstance implements IsAnimatableModelInstance {
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
	
	/** What THIS object is attached too, if anything **/
	AnimatableModelInstance parentObject = null;
	
	
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
	
	

	
	// this is just an example constructor, make sure to implement the constructor you need
	public AnimatableModelInstance (Model model) {
		super(model);
	}

	//Method used to update the transform
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setTransform(com.lostagain.nl.me.newmovements.PosRotScale)
	 */
	@Override
	public void setTransform ( PosRotScale newState) {

		transState.position.set(newState.position);
		transState.rotation.set(newState.rotation);
		transState.scale.set(newState.scale);

		sycnTransform();
	}

	//Method used to update the transform from a parent
	//Works exactly like setTransform, but firsts tests if a particular type of transform should be inherited
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#inheritTransform(com.lostagain.nl.me.newmovements.PosRotScale)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#sycnTransform()
	 */
	@Override
	public void sycnTransform() {
		super.transform.set(transState.position, transState.rotation, transState.scale);
		
		//now we check if theres a collision box to update
		if (collisionBox!=null){
			recalculateCollisionBox();
		}

		//now update all attached objects too;
		updateAllAttachedObjects();
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setToPosition(com.badlogic.gdx.math.Vector3)
	 */
	@Override
	public void setToPosition(Vector3 vector3) {		
		transState.position.set(vector3);
		sycnTransform();
	}
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setToRotation(com.badlogic.gdx.math.Quaternion)
	 */
	@Override
	public void setToRotation(Quaternion angle) {		
		transState.rotation.set(angle);
		sycnTransform();
	}
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setToscale(com.badlogic.gdx.math.Vector3)
	 */
	@Override
	public void setToscale(Vector3 scale) {		
		transState.scale.set(scale);
		sycnTransform();
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getMatrixTransform()
	 */
	@Override
	public Matrix4 getMatrixTransform() {		
		return super.transform;
	}
	
	public void hide(){	
		hide(true);
	}	

	private void hide(boolean setlocalVisibility){		
		currentRenderPlacement = ModelManagment.removeModel(this);	
		
		if (setlocalVisibility){
			localVisibility = false;
		}
		
		//we also hide things positioned relatively to this. Nothing overrides this
		for (AnimatableModelInstance object : attachlist.keySet()) {
			if (object.isInheriteingVisibility()){
				object.hide(false);
			}
		}
	}
	
	public void show(){	
		show(true);
	}
	
	private void show(boolean setlocalVisibility){		
		ModelManagment.addmodel(this,currentRenderPlacement);
		if (setlocalVisibility){
			localVisibility = true;
		}
		
		//we also show things positioned relatively to this unless they have visible false set
		for (AnimatableModelInstance object : attachlist.keySet()) {
			if (object.isInheriteingVisibility()  && object.isVisible() ){
				object.show(false);
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getWidth()
	 */
	@Override
	public float getWidth(){
		if (localBoundingBox==null){
			createBoundBox();
		}
		return localBoundingBox.getWidth();
	}
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getHeight()
	 */
	@Override
	public float getHeight(){
		if (localBoundingBox==null){
			createBoundBox();
		}
		return localBoundingBox.getHeight();
	}
	
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getCenter()
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getLocalCollisionBox()
	 */
	@Override
	public BoundingBox getLocalCollisionBox() {
		if (collisionBox==null){
			recalculateCollisionBox();
		}
		return collisionBox;
	}
	
	
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getLocalBoundingBox()
	 */
	@Override
	public BoundingBox getLocalBoundingBox() {
		if (localBoundingBox==null){
			createBoundBox();
		}
		return localBoundingBox;
	}
	
	protected void wasResized(){
		if (localBoundingBox!=null){
			createBoundBox();
		}
		if (collisionBox!=null){
			recalculateCollisionBox();
		}
		
	}
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#attachThis(com.lostagain.nl.me.newmovements.AnimatableModelInstance, com.lostagain.nl.me.newmovements.PosRotScale)
	 */
	@Override
	public void attachThis(AnimatableModelInstance objectToAttach, PosRotScale displacement){

		//	Gdx.app.log(logstag,"_____________________________________adding object "); 

		//add if not already there
		if (!attachlist.containsKey(objectToAttach))
		{
			attachlist.put(objectToAttach, displacement);
			
			//associate this as the parent object
			objectToAttach.parentObject=this;
			
			//give it a initial update
			PosRotScale newposition = transState.copy().displaceBy(attachlist.get(objectToAttach));
			objectToAttach.inheritTransform(newposition);
			
			
		} else {
			Gdx.app.log(logstag,"_____________________________________already attached so repositioning to new displacement"); 
			this.updateAtachment(objectToAttach, displacement);
			
		}

		//	Gdx.app.log(logstag,"_____________________________________total objects now: "+attachlist.size()); 

	}
	
	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#removeAttachment(com.lostagain.nl.me.newmovements.AnimatableModelInstance)
	 */
	@Override
	public void removeAttachment(AnimatableModelInstance objectToRemove){
		
		if (attachlist.containsKey(objectToRemove))
		{
			attachlist.remove(objectToRemove);
			//remove this as the parent object
			objectToRemove.parentObject=null;
		}
		
		
	}
	
	

	protected void updateAllAttachedObjects(){

		//Gdx.app.log(logstag,"_____________________________________updateAllAttachedObjects ="+attachlist.size()); 

		for (AnimatableModelInstance object : attachlist.keySet()) {

			PosRotScale newposition = transState.copy().displaceBy(attachlist.get(object));
			object.inheritTransform(newposition);


		}
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#updateAtachment(com.lostagain.nl.me.newmovements.AnimatableModelInstance, com.lostagain.nl.me.newmovements.PosRotScale)
	 */
	@Override
	public void updateAtachment(AnimatableModelInstance object,
			PosRotScale displacement) {

		attachlist.put(object, displacement);

	}


	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#lookAt(com.lostagain.nl.me.newmovements.AnimatableModelInstance)
	 */
	@Override
	public void lookAt(AnimatableModelInstance target){			
		Quaternion angle = getAngleTo(target);			
		setToRotation(angle);			
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#lookAt(com.lostagain.nl.me.newmovements.AnimatableModelInstance, com.badlogic.gdx.math.Vector3)
	 */
	@Override
	public void lookAt(AnimatableModelInstance target, Vector3 Axis){			
		Quaternion angle = getAngleTo(target,Axis);			
		setToRotation(angle);			
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getAngleTo(com.lostagain.nl.me.newmovements.AnimatableModelInstance)
	 */
	@Override
	public Quaternion getAngleTo(AnimatableModelInstance target) {
		return  getAngleTo(target, new Vector3(1,0,0));
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getAngleTo(com.lostagain.nl.me.newmovements.AnimatableModelInstance, com.badlogic.gdx.math.Vector3)
	 */

	@Override
	public Quaternion getAngleTo(AnimatableModelInstance target, Vector3 Axis) {

		Vector3 thisPoint   = this.transState.position.cpy();
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

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#getAttachments()
	 */
	@Override
	public Set<AnimatableModelInstance> getAttachments() {

		return attachlist.keySet();
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setInheritedPosition(boolean)
	 */
	@Override
	public void setInheritedPosition(boolean inheritedPosition) {
		this.inheritedPosition = inheritedPosition;
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setInheritedRotation(boolean)
	 */
	@Override
	public void setInheritedRotation(boolean inheritedRotation) {
		this.inheritedRotation = inheritedRotation;
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setInheritedScale(boolean)
	 */
	@Override
	public void setInheritedScale(boolean inheritedScale) {
		this.inheritedScale = inheritedScale;
	}

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#setInheritedVisibility(boolean)
	 */
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

	/* (non-Javadoc)
	 * @see com.lostagain.nl.me.newmovements.IsAnimatableModelInstance#isInheriteingVisibility()
	 */
	@Override
	public boolean isInheriteingVisibility() {
		return inheritVisibility;
	}

	@Override
	public boolean isVisible() {
		
		//if local visibility is false, or we are not inheriting the visibility, then our localvisibility should match are visibility
		if (parentObject==null || localVisibility==false || !inheritVisibility ){
			return localVisibility;
		}
		
		///if we are inheriting and we are not hidden then our visibility should match our parents
		return parentObject.isVisible();
				
	}

	
	
	

}
