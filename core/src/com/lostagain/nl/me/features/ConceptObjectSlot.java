package com.lostagain.nl.me.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.collision.Ray;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.GWTish.Widget;
import com.lostagain.nl.me.gui.STMemory;
import com.lostagain.nl.me.gui.DataObjectSlot.OnDropRunnable;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.me.objects.DataObject;

public class ConceptObjectSlot extends Widget implements hitable {

	final static String logstag = "ME.ConceptObjectSlot";

	static float PADDING = 5f;
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

	private Runnable runAfterSomethingDraggedOff;

	/** Necessary as part of hit detection **/
	private float lastHitDistance; 
	
	/**
	 * ConceptObject slots are slots that can hold conceptobjects.
	 * They are positioned about their center points, not top left
	 * 	 * They allow the objects to be dragged in or out, if correctly enabled to do so.
	 * They can also report back when something is dropped on them
	 */
	public ConceptObjectSlot() {
		
		super(WIDTH,HEIGHT,Widget.MODELALIGNMENT.CENTER); //easier if we are centralized
		super.setBackgroundColor(Color.LIGHT_GRAY);
		setApperanceAsEmpty();
		
		//add to the hitables, so we can detect when a mouseup releases a object over us
		ModelManagment.addHitable(this);
	}
	
	
	/** fired when a object is dropped onto it **/
	private void onDrop(ConceptObject object){
		
		//check if we are currently accepting drops
		if (this.currentMode==SlotMode.OutOnly || this.currentMode==SlotMode.Locked ){
			Gdx.app.log(logstag," Slot not accepting drops ");
			return;
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
		Gdx.app.log(logstag," attaching object ");
		objectCurrentlyStored = object;
		
		this.attachThis(object, new PosRotScale(0f,0f,2f));
		setApperanceAsInUse();
		//ensure its visible
		object.show();
		//associate its attachment
		object.setAsAttachedToObject(this);
		
		//runAfterSomethingDroppedOn.run(drop);
		
	}
	
	/** fired when a object is attempted to be dragged from it **/
	boolean onDrag(){
		//cancel and return false if not allowed
		if (currentMode==SlotMode.InOnly || currentMode == SlotMode.Locked){
			return false;
		}
		//else remove
		this.removeAttachment(objectCurrentlyStored);
		objectCurrentlyStored=null;
		
		//updateapperance
		setApperanceAsEmpty() ;
		
		
		
		if (runAfterSomethingDraggedOff!=null){
			runAfterSomethingDraggedOff.run();
		}
		return true;
		
	}
	private void setApperanceAsEmpty() {
		this.setBorderColor(Color.BLUE);		
		
	}
	private void setApperanceAsInUse() {
		this.setBorderColor(Color.GREEN);		
		
	}


	private void animatedRejection() {
		// TODO Auto-generated method stub
		
	}

	
	public void setOnDragRun(Runnable runnable) {
		
		runAfterSomethingDraggedOff = runnable;
		
		
	}
	
	public void setOnDropRun(OnDropRunnable runnable) {
		
		runAfterSomethingDroppedOn = runnable;
		
		
	}

	
	public interface OnDropRunnable {
		public void run(DataObject drop);
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
	public void fireTouchDown() {
		// TODO Auto-generated method stub
		
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
	public void fireDragStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLastHitsRange(float range) {

		//Gdx.app.log(logstag,"setting hittable hit range to:"+range);
		lastHitDistance = range;
	}

	@Override
	public float getLastHitsRange() {
		
		return lastHitDistance;
	}

	@Override
	public boolean isBlocker() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rayHits(Ray ray) {
		boolean hit = Intersector.intersectRayBoundsFast(ray, this.getLocalCollisionBox());
		Gdx.app.log(logstag,"testing for hit on concept object:"+hit);
		return hit;
	}

}
