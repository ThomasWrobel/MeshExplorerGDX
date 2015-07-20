package com.lostagain.nl.me.newmovements;

import java.util.Set;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public interface IsAnimatableModelInstance {

	//Method used to update the transform
	public abstract void setTransform(PosRotScale newState);

	//Method used to update the transform from a parent
	//Works exactly like setTransform, but firsts tests if a particular type of transform should be inherited
	public abstract void inheritTransform(PosRotScale newState);

	/** should be called after ANY set of change to its transState before it will be reflected in the model visually**/
	public abstract void sycnTransform();

	/*** Convince to quickly set the position. If doing a more complex change make a PosRotScale and call setTransform **/
	public abstract void setToPosition(Vector3 vector3);

	/*** Convince to quickly set the rotation. If doing a more complex change make a PosRotScale and call setTransform **/
	public abstract void setToRotation(Quaternion angle);

	/*** Convince to quickly set the scale. If doing a more complex change make a PosRotScale and call setTransform **/
	public abstract void setToscale(Vector3 scale);

	/** try to avoid using this, use the transState to update/change things then sync to reflect them in the instance.
	 * This is just here when you need to get the transform, dont change it with this **/
	public abstract Matrix4 getMatrixTransform();

	/** hides it by removing it from the render lists **/
	public abstract void hide();

	/**
	 * Shows it by adding it to the render lists.
	 * This only works if it was previously hidden. It should currently be added manually once first so it knows its render order setting
	 * This might change in future **/
	public abstract void show();

	public abstract float getWidth();

	public abstract float getHeight();

	public abstract Vector3 getCenterOnStage();
	
	public abstract Vector3 getCenterOfBoundingBox();

	public abstract BoundingBox getLocalCollisionBox();

	public abstract BoundingBox getLocalBoundingBox();

	/** 
	 * Lets you stick one object to another. Its position and rotation will shift as its parent does.
	 * You can specific a PosRotScale for its displacement from parent.
	 * Note; This should check for inheritance loops at some point it does not at the moment
	 * 
	 * Note; Displacement is not copied. Changes to the given displacement will continue to effect the objects position 
	 * **/
	public abstract void attachThis(AnimatableModelInstance objectToAttach,
			PosRotScale displacement);

	public abstract void removeAttachment(AnimatableModelInstance objectToRemove);

	public abstract void updateAtachment(AnimatableModelInstance object,
			PosRotScale displacement);

	/** Sets this model to "lookat" the target models vector3 location by aligning this models xAxis(1,0,0) to point at the target **/
	public abstract void lookAt(AnimatableModelInstance target);

	/** Sets this model to lookat the target models vector3 location **/
	public abstract void lookAt(AnimatableModelInstance target, Vector3 Axis);

	/** 
	 * Method to find the axis-angle between this AnimatableModelInstances and another relative to the xAxis (1,0,0)
	 * 
	 * @return Quaternion of angle 
	 * **/
	public abstract Quaternion getAngleTo(AnimatableModelInstance target);

	/** 
	 * Method to find the axis-angle between this AnimatableModelInstances and another relative to the xAxis.
	 * 
	 * @return Quaternion of angle 
	 * **/

	public abstract Quaternion getAngleTo(AnimatableModelInstance target,
			Vector3 Axis);

	public abstract Set<AnimatableModelInstance> getAttachments();

	public abstract void setInheritedPosition(boolean inheritedPosition);

	public abstract void setInheritedRotation(boolean inheritedRotation);

	public abstract void setInheritedScale(boolean inheritedScale);

	public abstract void setInheritedVisibility(boolean inheritVisibility);

	public abstract boolean isInheriteingVisibility();
	
	/**
	 * Determains if this object is visible or not.
	 * This means, if its inheiriting visibility, that its personal visibility setting is true AND its parents is
	 * @return
	 */
	public abstract boolean isVisible();
	

}