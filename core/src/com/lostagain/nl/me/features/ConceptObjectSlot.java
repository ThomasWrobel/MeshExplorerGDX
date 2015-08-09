package com.lostagain.nl.me.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.GWTish.Widget;
import com.lostagain.nl.me.gui.STMemory;
import com.lostagain.nl.me.gui.DataObjectSlot.OnDropRunnable;
import com.lostagain.nl.me.models.Animating;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.NewForward;
import com.lostagain.nl.me.newmovements.NewMovementController;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.me.objects.DataObject;

/**
 * A slot that takes ConceptObjects into it and can respond if its accepted or not
 * 
 * @author Tom
 *
 */
public class ConceptObjectSlot extends Widget implements hitable,Animating {

	final static String logstag = "ME.ConceptObjectSlot";

	static float PADDING = 8f;
    static float WIDTH  = ConceptObject.StandardConceptWidth+PADDING;
    static float HEIGHT = ConceptObject.StandardConceptHeight+PADDING;
    
    
	enum SlotMode {
		/** ConceptObjects can be dragged in and out **/
		Normal,
		/** ConceptObjects can be dragged in but not out **/
		InOnly,
		/** ConceptObjects can be dragged out but not in ***/
		OutOnly,
		/** ConceptObjects can't go in or out**/
		Locked		
	}
	
	SlotMode currentMode = SlotMode.Normal;

	ConceptObject objectCurrentlyStored = null;
	
	private OnDropRunnable runAfterSomethingDroppedOn;

	private OnDragRunnable runAfterSomethingDraggedOff;
	

	/** Necessary as part of hit detection **/
	private float lastHitDistance;

	private Color CurrentBackColour = Color.LIGHT_GRAY; 
	
	/**
	 * ConceptObject slots are slots that can hold conceptobjects.
	 * They are positioned about their center points, not top left
	 * They allow the objects to be dragged in or out, if correctly enabled to do so.
	 * They can also report back when something is dropped on them
	 **/
	public ConceptObjectSlot() {
		
		super(WIDTH,HEIGHT,Widget.MODELALIGNMENT.TOPLEFT);
		super.getStyle().setBackgroundColor(Color.LIGHT_GRAY);

		setApperanceAsEmpty();
		
		//set as hitables, so we can detect when a mouseup releases a object over us
		//ModelManagment.addHitable(this);
		this.setAsHitable(true);
		
	}
	
	private void onDrop(ConceptObject object){
		onDrop(object,false);
	}
	
	/** fired when a object is dropped onto it **/
	private void onDrop(ConceptObject object,boolean overrideLock){
		
		//check if we are currently accepting drops
		if (!overrideLock){
			if (this.currentMode==SlotMode.OutOnly || this.currentMode==SlotMode.Locked ){
				Gdx.app.log(logstag," Slot not accepting drops ");
				return;
			}
		}
		
		//check if are accepting of this type of conceptobject
		if (!willAccept(object)){
			Gdx.app.log(logstag," Slot not accepting THIS drop ");
			this.animatedRejection();
			return;
		}
		
		//if theres an existing object we remove it and set it as being held 
		if (objectCurrentlyStored!=null){
			Gdx.app.log(logstag," swapping object ");
			objectCurrentlyStored.triggerPickedUp();
		} else {
			//clear currently held
			STMemory.clearCurrentlyHeld();
			 MainExplorationView.setCursor(null);
		}
		
		//attach new object and store
	
		objectCurrentlyStored = object;
		
		//attach point should be true center, not pivot point
		//our pivot should always be top left
		//conceptobjects should always be center
		//Therefor the new middle is the incoming conceptobjects center point scaled to its new size (ie, our size)
		//(note; we dont use the existing scale, as it might change when attached)	
		Vector3 dimensionsOf = new Vector3();
		
		objectCurrentlyStored.getLocalBoundingBox().getDimensions(dimensionsOf);
		
		Vector3 scaledObject = dimensionsOf;//.scl(this.transState.scale);
		
		Gdx.app.log(logstag," attaching object at half of:"+scaledObject);
		//PosRotScale displacement = new PosRotScale(objectCurrentlyStored.getPivotsDisplacementFromCenterOfBoundingBox());
		
		
		PosRotScale displacement = new PosRotScale((scaledObject.x/2)+(PADDING/2),-(scaledObject.y/2)-(PADDING/2),2f);
		
		
		//ensure its visible
		object.show();
		
		//ATTACH
		attachThis(object, displacement);
		
		//setApperanceAsInUse();
		refreshApperanceBasedOnMode();
		
		//associate its attachment
		object.setInheritedVisibility(true);
		
		object.setAsAttachedToObject(this);
		
		if (runAfterSomethingDroppedOn!=null){
			runAfterSomethingDroppedOn.run(object);
		}
	}
	
	enum ejectStyle{
		Finished,Rejected
	}
	public void ejectConcept() {
		ejectConcept(ejectStyle.Rejected);
	}
	
	public void ejectConcept(ejectStyle style) {
		if (objectCurrentlyStored==null){
			Gdx.app.log(logstag,"No concept to eject");
			return;
		}
		
		Gdx.app.log(logstag,"ejecting concept in slot");
		
		removeAttachment(objectCurrentlyStored);
		objectCurrentlyStored.setAsDropped();
		
		//reset any scale changes (concepts should always be 1:1:1 when on stage)
		objectCurrentlyStored.setToScale(new Vector3(1f,1f,1f));
	
		
		//randomly dump outside (animate this later?)
		
		Vector3 newPosition = this.getCenterOnStage().cpy();
		//newPosition.y = newPosition.y-100;
		
		ME.addnewdrop(objectCurrentlyStored, newPosition.x, newPosition.y,newPosition.z+5); //a little above the slot to start
		
		//different animation based on type of ejection
		switch (style) {
		case Rejected:
			objectCurrentlyStored.setMovement(new NewForward(150,1000));
			break;
		default:
			objectCurrentlyStored.setMovement(new NewForward(150,1000)); //todo:
			break;
		}
		
		
		
		//setApperanceAsEmpty();
		refreshApperanceBasedOnMode();
		
		objectCurrentlyStored=null;
		
	}
	
	/** fired when a object is attempted to be dragged from it **/
	boolean onDrag(){
		ConceptObject objectBeingRemoved = objectCurrentlyStored;
		//cancel and return false if not allowed
		if (currentMode==SlotMode.InOnly || currentMode == SlotMode.Locked){
			Gdx.app.log(logstag,"slot mode is currently:"+currentMode);
			return false;
		}
		//reset any scale changes (concepts should always be 1:1:1 when on stage)
		objectCurrentlyStored.setToScale(new Vector3(1f,1f,1f));
	
		//else remove
		this.removeAttachment(objectCurrentlyStored);
		objectCurrentlyStored=null;
		
		//updateapperance
		//setApperanceAsEmpty() ;
		refreshApperanceBasedOnMode();
		
		
		if (runAfterSomethingDraggedOff!=null){
			runAfterSomethingDraggedOff.run(objectBeingRemoved);
		}
		return true;
		
	}
	private void setApperanceAsEmpty() {
		super.getStyle().setBorderColor(Color.LIGHT_GRAY);	
		CurrentBackColour = Color.LIGHT_GRAY;
		super.getStyle().setBackgroundColor(CurrentBackColour);
		
	}
	private void setApperanceAsInUse() {
		super.getStyle().setBorderColor(Color.GREEN);		
		CurrentBackColour = Color.GREEN;
		super.getStyle().setBackgroundColor(CurrentBackColour);
		
	}
	private void setApperanceAsLocked() {		
		super.getStyle().setBorderColor(Color.RED);			
		CurrentBackColour = Color.LIGHT_GRAY;
		super.getStyle().setBackgroundColor(CurrentBackColour);
		
		//really should have a overlay object here of some sort - possibly with a cross - to help enforce the idea that nothing can be removed yet
		
		
	}

	
	protected void animatedRejection() {
		
		//flash red maybe?
		rejectionAnimationPlaying = true;
		ModelManagment.addAnimating(this);
		timeElypsed = 0;
		
		
	}

	
	public void setOnDragRun(OnDragRunnable runnable) {
		
		runAfterSomethingDraggedOff = runnable;
		
		
	}
	
	public void setOnDropRun(OnDropRunnable runnable) {
		
		runAfterSomethingDroppedOn = runnable;
		
		
	}

	
	public interface OnDropRunnable {
		public void run(ConceptObject drop);
	}
	public interface OnDragRunnable {
		public void run(ConceptObject drop);
	}


	/** can be overrideden by classes that extend this to allow selective objects to be addable  **/
	public boolean willAccept(ConceptObject object){
		return true;
	}

	@Override
	public PosRotScale getTransform() {
		return super.transState;
	}



	@Override
	public void fireTouchUp() {
		Gdx.app.log(logstag,"_-(fireTouchUp)-_");
		
		if (STMemory.isHoldingItem()){
			
			Gdx.app.log(logstag,"_-(mouse up while holding:"+STMemory.currentlyHeldNEW.itemsnode.getPLabel()+")-_");
			 
			onDrop(STMemory.currentlyHeldNEW);
			 
		}
	
	}



	@Override
	public boolean isBlocker() {
		return true;
	}

	//@Override
	//public boolean rayHits(Ray ray) {
	//	boolean hit = Intersector.intersectRayBoundsFast(ray, this.getLocalCollisionBox());
	//	Gdx.app.log(logstag,"testing for hit on concept object slot:"+hit);
	//	return hit;
	//}


	@Override
	public void setOpacity(float opacity) {
		super.setOpacity(opacity);
		//if we have a concept fade that too;
		if (this.objectCurrentlyStored!=null){
			objectCurrentlyStored.setOpacity(opacity);
		}
		
	}


	
	public void setAsCointaining(ConceptObject newConceptObject) {		
		onDrop(newConceptObject,true);		
	}


	public void setCurrentMode(SlotMode currentMode) {
		this.currentMode = currentMode;
		
		refreshApperanceBasedOnMode();
	}

	private void refreshApperanceBasedOnMode() {
		switch (currentMode) {		
		case Locked:
			 setApperanceAsLocked();
			break;
		case InOnly:
		case Normal:
		case OutOnly:
				if (objectCurrentlyStored==null){
					 setApperanceAsEmpty();
				} else {
					setApperanceAsInUse();
				}
			 
			break;
		}
	}

	boolean rejectionAnimationPlaying = false;
	float rejectionDuration = 1000/1000;
	float timeElypsed;
	@Override
	public void updateAnimationFrame(float deltatime) {
		
		if (rejectionAnimationPlaying){
			
			timeElypsed = timeElypsed + deltatime; 
			
			float ratio = timeElypsed/rejectionDuration;
			
			//in order to cycle the colour change we scale between 0 and 2PI then use sin (as Sin 2PI = 1)
			float alpha = (float) Math.sin((ratio*Math.PI*2.0f*3.0)); //the 3 is the number of repeats
			
			Color normal = CurrentBackColour.cpy();
			Color changeTo = Color.RED;
			
			normal.lerp(changeTo, alpha);
			
			this.getStyle().setBackgroundColor(normal);
			
			
						
			if (timeElypsed>rejectionDuration){	

				getStyle().setBackgroundColor(CurrentBackColour);
				ModelManagment.removeAnimating(this);	
				rejectionAnimationPlaying=false;
				
			}
		}
		
	}

}
