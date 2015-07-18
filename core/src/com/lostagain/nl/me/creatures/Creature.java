package com.lostagain.nl.me.creatures;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.ME;
import com.lostagain.nl.me.creatures.Population.destructOn;
import com.lostagain.nl.me.gui.ConceptGun;
import com.lostagain.nl.me.gui.Inventory;
import com.lostagain.nl.me.gui.STMemory;
import com.lostagain.nl.me.locationFeatures.LocationsHub;
import com.lostagain.nl.me.models.Animating;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.NEWREPEAT;
import com.lostagain.nl.me.newmovements.NewFaceAndMoveTo;
import com.lostagain.nl.me.newmovements.NewForward;
import com.lostagain.nl.me.newmovements.NewJerk2D;
import com.lostagain.nl.me.newmovements.NewMoveTo;
import com.lostagain.nl.me.newmovements.NewMovementController;
import com.lostagain.nl.me.newmovements.NewRelativeScale;
import com.lostagain.nl.me.newmovements.NewRotateLeft;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.me.objects.DataObject;
import com.lostagain.nl.uti.Uti;

public class Creature implements hitable , Animating {
	private static String logstag="ME.Creature";
	
	/** The modelInstance that defines this creature **/
	AnimatableModelInstance creaturemodel;
	
	final static int zPlane = 70; //the horizontal plane the creatures exist on. should be used for all z values in positions.
	
	/**
	 * the starting location of the creature 
	 */
	PosRotScale origin = new PosRotScale();
	
	Matrix4 currerntScale =  new Matrix4().scl(1f);
	
	//movement (switching to new system)
	NewMovementController movementControll;//,new Forward(-300,1000)
	
	//parent population
	Population parentpolution;
		
	//drops, if any
	ArrayList<SSSNode> drops=	new ArrayList<SSSNode>();
	
	
	//Note, if needed we can calculate the radius and position we should use for hits
	//with the below method;
	//calculateBoundingBox(bounds);
    //center.set(bounds.getCenter());
    //dimensions.set(bounds.getDimensions());
    //radius = dimensions.len() / 2f;
    
	destructOn destructionType = destructOn.clicks; //defaults to a clicks if no query specified
	int numOfHitsLeft = 10;
	
	
	enum CreatureState {
		alive,dieing,dead;
	}
	
	CreatureState currentState = CreatureState.alive;
	
	
	//query that defines what removes it
	String queryToDestroy; 
	
	//base color
	Color creaturesNormalColor = Color.WHITE;
	
	//current color
	Color creaturesCurrentColor = creaturesNormalColor.cpy();
			
	//lighter color for when clicked on
	Color hitColor = Color.WHITE; //Color.GREEN.cpy().add(.5f, .5f, .5f, 1f);
	
	//should be changed based on the size of the creature
	int hitradius = 30;
	
	float lastHitDistance = -1f; //no hit by default
	
	//----------------------------------------------------------------------------------------------------------
	//Below defines the settings for handling frame based animations (ie, related to image changes not movement)
	//
	/** 
	 * Defines various animations that can be performed on this creatures image. 
	 * Currently only damage taken is used.
	 * Also defines the durations of each in the brackets. 
	 **/
	enum CreatureAnimationType {
		none(-1.00f),
		damageTaken(2.600f), //currently goes to white instantly and fades back to the creatures normal color over 2.6 seconds
		beingDestroyed(0.200f),
		appearing(0.500f);
		
		private final float duration; // in seconds
		
		CreatureAnimationType (float duration) {
	        this.duration = duration;
	    }
		/** The total duration this animation should take **/
	    public float duration() { return duration; }
	    
	}
	
	/** The currently playing sprite animation, none by default. (Note; This is no the movement, only frame changes) **/
	CreatureAnimationType currentlyPlayingAnimation = CreatureAnimationType.none;
	
	/** The time we are into the currently playing animation, messured in seconds ***/
	float currentAnimationTime = 0.0f;
	//
	//-----------------------------------------------------------------------------------------------------------
	

	public Creature(Population parentPopulation, int hitPoints, String queryToDestroy, destructOn destructionType) {
		
		//this.x=x;
		//this.y=y;
		this.parentpolution=parentPopulation;		
		this.destructionType = destructionType;
		this.numOfHitsLeft = hitPoints;
		this.queryToDestroy = queryToDestroy;
				
	}



	@Override
	public Vector3 getCenterOfBoundingBox() {
		
		Vector3 tmp = creaturemodel.transState.position.cpy();
				
		return tmp;  // new Vector3(x,y,z);
	}




	//@Override
	public int getRadius() {
		return hitradius;
	}
//



	@Override
	public PosRotScale getTransform() {
		return creaturemodel.transState;
	}




	public void setmodel(AnimatableModelInstance model) {
		
		creaturemodel = model;
		
		
		//set to model lists
		ModelManagment.addmodel(creaturemodel,ModelManagment.RenderOrder.zdecides);
		ModelManagment.addHitable(this);
		
		//make bigger (test only)
		//creaturemodel.transform.mul(new Matrix4().setToScaling(0.5f, 2.5f,0.5f));
		//rotate (test only)
		//creaturemodel.transform.mul(new Matrix4().setToRotation(new Vector3(0f,0f,1f), 45));
		
		
		PosRotScale startScaleAndRotation = new PosRotScale();
		
		
	//	startScaleAndRotation.setToPosition(new Vector3(0f, 0f, 50f)); //now we offset from the existing position
		
	//	startScaleAndRotation.setToRotation(0f, 0f, 1f, 45);
		//startScaleAndRotation.setToScaling(new Vector3(0.5f, 2.5f,0.5f));
		
		Gdx.app.log(logstag, " setting to: "+startScaleAndRotation.toString());	
		Matrix4 test = startScaleAndRotation.createMatrix();
		PosRotScale test2 = new PosRotScale(test);
		Gdx.app.log(logstag, " check after conversion: "+test2.toString());	
		
		creaturemodel.transState.displaceBy(startScaleAndRotation);
		creaturemodel.sycnTransform();
		
		//creaturemodel.transform.mul(startScaleAndRotation.createMatrix()); 
		
		
		
		movementControll = new NewMovementController(creaturemodel.transState);//new Forward(200,3000),new RotateLeft(90,1000), new REPEAT());
		
		//movementControll = new MovementController(creaturemodel.transform,new Jerk2D(creaturemodel,30f,50f,400f,4000f));//new Forward(200,3000),new RotateLeft(90,1000), new REPEAT());
		//movementControll = new MovementController(creaturemodel.transform, new Forward(200,3000),new RotateLeft(90,1000), new REPEAT());//
		startCreaturesStandardMovement();	
	}

	/** sets the normal color of the creature when no effects or changes are applied.
	 * The creature will typically revert back to this color after changes.**/
	public void setNormalColor(Color newcol){
		
		creaturesNormalColor = newcol;
		
	}
	
	/** set current color, ie taking into account effects like damage or health**/
	public void setColor(Color newcol){

		ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);
		attribute.color.set(newcol);
		
		creaturesCurrentColor = newcol;

		Gdx.app.log(logstag, " crearturesColor ="+creaturesCurrentColor.toString());
	}

	public void setHitColor(Color newcol){

		
		hitColor = newcol;
		
	}

	@Override
	public void fireTouchDown() {
		
		//ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);
		
		//attribute.color.set(hitColor);
		

		hit();
	}




	@Override
	public void fireTouchUp() {
		
		//really this check shouldn't be needed but it seems this fires sometimes while its being destroyed, not sure why?
		if (creaturemodel!=null){
			//ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);
		
			//attribute.color.set( crearturesColor);
		}
		
		
		
	}




	private void hit() {
		Gdx.app.log(logstag, " creature hit  ");	
		if (currentState != CreatureState.alive){
			Gdx.app.log(logstag, " already being destroyed ");
			return;
		}
		//ConceptGun.animateImpactEffect();

		//when hit move away randomly		
		
		float angle = (float) (Math.random()*360);
		
		Gdx.app.log(logstag, " setting movementControll currently moving: "+movementControll.isMoving());	
		//movementControll.setMovement(creaturemodel.transform,false,new RotateLeft(angle,60),new Forward(50,250));
		
	//	movementControll.setMovement(creaturemodel.transform,false,new NewRotateLeft(90,90),new NewForward(50,250));
		
		//test motion
		//float EX = parentpolution.centeredOnThisLocation.getHubsX(Align.center);
		//float EY = parentpolution.centeredOnThisLocation.getHubsY(Align.center);
		//float EZ = getCenter().z;
		//movementControll.setMovement(creaturemodel.transform);//
		//run to center of location; creaturemodel.transform,true,MoveTo.create(creaturemodel.transform, EX, EY,EZ,3000)
		
				
		
		
		
		if (destructionType == destructOn.cant){
			return; //invincible
			
		}
		
		if (destructionType == destructOn.clicks){

			Gdx.app.log(logstag,"_removed clickpoint");			
			numOfHitsLeft--;
			
			if (numOfHitsLeft<1){
				Creature.this.startDieing();
				
			}
		}

		if (destructionType == destructOn.query){
			
			//two ways of using a query on a creature; direct and with the gun
			//we thus check the gun first 
			SSSNode appliedConcept;
			if (!ConceptGun.isDisabled()){
				appliedConcept = ConceptGun.equipedConcept;
			} else if (STMemory.isHoldingItem()) {
				appliedConcept = STMemory.currentlyHeld.itemsnode;					
			} else {
				Gdx.app.log(logstag,"_nothing currently equiped to fight creature");	
				return;
			}
			
			
				Gdx.app.log(logstag,"_testing vulnerability against:"+ConceptGun.equipedConcept);	
				//Gdx.app.log(logstag,"_testing vulnerability against:"+Old_Inventory.currentlyHeld.itemsnode.getPLabel());	
				Gdx.app.log(logstag,"_vulnerability is:"+queryToDestroy);	
			
				Uti.testIfInQueryResults(queryToDestroy,appliedConcept, new Runnable() {
					
					@Override
					public void run() {
						// this is what happens if the concept was in the acceptable results
						//for what can destroy (or at least damage) this creature
						numOfHitsLeft--;
						Gdx.app.log(logstag, " creature numOfHitsLeft  "+numOfHitsLeft);
						if (numOfHitsLeft<1){
							//fire the start dieing command on this creature
							Creature.this.startDieing();
					
						} else {
							Creature.this.damaged();
							
							
						}
					}
				}, new Runnable() {

					@Override
					public void run() {
						// this is what happens if the concept was not in the acceptable results
						
						//(dont do anything atm, but in future we should have an effect for no damage?)
						
					}
					
				});
			
			
				
			
			
			
		}
	}
	
	protected void startDieing() {
		
		currentState = CreatureState.dieing;
		//starts the sequence for the death animation
		//once finished destroy() should be triggered
		currentlyPlayingAnimation = CreatureAnimationType.beingDestroyed;
		
		
		//reset the time into the current animation (all animations start from 0)
		currentAnimationTime = 0f;					 
		//add to animation list which updates frames based on delta
		//the updateAnimationFrame function will remove it from this list when the animation is finished.
		//(that is, when currentAnimationTime = the animations duration defined in its enum)
		ModelManagment.addAnimating(this);
		
		
	}



	/** Runs the standard animation of this creature getting damaged.
	 * Can be overridden by subtypes for different effects.
	 * 
	 *  This effect will grow the creature and make it glow white for a short period **/
	protected void damaged() {

		Gdx.app.log(logstag,"_________creature damaged");
		
	//	final ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);		
		

		/*
		float r = (float) Math.random();
		float g = (float) Math.random();
		float b = (float) Math.random();
		
		Color col =  new Color();		

		Gdx.app.log(logstag,"_________creature before col="+col.toString());
	//	col = crearturesColor.mul(new Color(1.1f,1.1f,1.1f,1.0f));
		Color.rgba8888ToColor(col, Color.rgba8888(r, g, b,1.0f) );		
		
				
		setColor(col);
		Gdx.app.log(logstag,"_________creature after col="+crearturesColor.toString());
		
		
		Color newhitcol =  new Color();		
		newhitcol = hitColor.mul(new Color(1.1f,1.1f,1.1f,1.0f));
		
		
	//	Color.rgba8888ToColor(col, Color.rgba8888(r, g, b,1.0f) );		
		
		Gdx.app.log(logstag,"_________creature col="+col.toString());
				
		setHitColor(newhitcol);
		*/
		
		
		//attribute.color.set(col) ;
		
		
		
		//make this thing bigger (slowly gets bigger then explodes is the plan)

		currerntScale.scl(1.2f);
		//creaturemodel.transform.scl(1.1f);

		Gdx.app.log(logstag,"making bigger:"+currerntScale.getScaleX());
		Gdx.app.log(logstag,"animating:"+movementControll.isMoving());
		Gdx.app.log(logstag,"resuming after:"+movementControll.isGoingToResumeAfter());
		
		float durationOfEnlargement = 150f; //the time taken to enlarge. We also use this to set a timer for when the creature should restart moving
		
		movementControll.setMovement(creaturemodel.transState,false,new NewRelativeScale(1.2f,durationOfEnlargement));
		
		//update radius
		hitradius = (int) (hitradius * 1.2f);
				
		
		Timer.schedule(new Task(){

			@Override
			public void run() {
				
				//after enlarging we restart the creatures standard movement.
				Creature.this.startCreaturesStandardMovement();
			//	attribute.color.set( crearturesColor );
								
			}
			
			
		}, (durationOfEnlargement+250)/1000); //the movement should start again shortly after the enlargement ends. We devide by 1000 as Timer.schedule needs the time in seconds, not ms 
				
		
		
		
		
		//new trigger animation (which currently is just a tint transition)
		currentlyPlayingAnimation = CreatureAnimationType.damageTaken;		
		//reset the time into the current animation (all animations start from 0)
		currentAnimationTime = 0f;					 
		//add to animation list which updates frames based on delta
		//the updateAnimationFrame function will remove it from this list when the animation is finished.
		//(that is, when currentAnimationTime = the animations duration defined in its enum)
		ModelManagment.addAnimating(this);
				
	}




	protected void startCreaturesStandardMovement() {
		if (currentState != CreatureState.alive){
			Gdx.app.log(logstag, " already being destroyed,cantset movement ");
			return;
		}
				

		Gdx.app.log(logstag,"startCreaturesStandardMovement");
		
		//movementControll = new NewMovementController(creaturemodel.transform,new NewJerk2D(creaturemodel,30f,50f,400f,8000f), new NEWREPEAT());
		
		//movementControll = new NewMovementController(creaturemodel.transform,new NewRotateLeft(90,590),new NewForward(50,2050),new NEWREPEAT());
		//movementControll.setMovement(creaturemodel.transform,false,new NewRotateLeft(90,590),new NewForward(50,2050),new NEWREPEAT());
		//new NewJerk2D(creaturemodel,70f,80f,2000f,8000f)
		
		
		movementControll.setMovement(creaturemodel.transState,false,new NewJerk2D(creaturemodel,70f,80f,500f,2000000f), new NEWREPEAT());
		
		
	}


	/** The final actions that happen after a creature is destroyed.
	 * This should be triggered after all destroying animations have finished. (no animations should be triggered from this function)
	 * It triggers the drops, sets its state to dead, and removes it from the render lists and hitable lists **/
	protected void destroy() {
		
		//create drops, if any	(has to be done before the model is destroyed)
				if (drops!=null && drops.size()>0){

					Gdx.app.log(logstag,"droping drops with the dropdrops(drops) call.");
					dropdrops(drops);
				}
				
		
		
		//destroyed=true;
		currentState = CreatureState.dead;
		
		//ConceptGun.animateImpactEffect();
		//remove from visuals
		ModelManagment.removeModel(creaturemodel);

		Gdx.app.log(logstag,"_destroying model....removed:"+ModelManagment.removeHitable(this));
		
		
		movementControll.clearMovement();
				
	
				
		//ensure its gone
		creaturemodel=null;
		
		//remove from population
		parentpolution.removeFromPopulation(this);
		
		
		
		
	}




	private void dropdrops(ArrayList<SSSNode> drops2) {
		
		
		for (SSSNode dropsnode : drops2) {
			
			//create new object for it
			DataObject newdrop = new DataObject(dropsnode); //string for debuging, will be removed
			
			//add to world
			Gdx.app.log(logstag, "creating drop on screen");
			
			float x = (float) (this.getCenterOfBoundingBox().x+ (-20+Math.random()*40));			
			float y = (float) (this.getCenterOfBoundingBox().y+ (-20+Math.random()*40));
			
						
			ME.addnewdrop(newdrop,x, y);			
			
		}
		
	}
	
	
	
	public void updatePosition(float delta){
		
		if (movementControll!=null && movementControll.isMoving()){
			
			creaturemodel.setTransform(movementControll.getUpdate(delta));			
			
			//Matrix4 displacementFromOrigin = movementControll.getUpdate(delta).cpy();		
			//creaturemodel.transform =  displacementFromOrigin;//.mul(currerntScale); // displacementFromOrigin;// .scl(currerntScale); //origin.cpy().mul(displacementFromOrigin);
		    //Gdx.app.log(logstag, "_______________current size="+creaturemodel.transform .getScaleX()+","+creaturemodel.transform .getScaleY()+","+creaturemodel.transform .getScaleZ()+")");
			
		}		
		
	}



	/**
	 * Updates the current animation frame, which in this case means color by default
	 * NOTE: if calling this by super bare in mind it will remove itself from the animation list once its duration has past
	 * @param delta - The time in seconds since the last render. 
	 */
	public void updateAnimationFrame(float delta){
			
		//add the The time in seconds since the last render to the current animation time
		currentAnimationTime = currentAnimationTime + delta;
		
		//work out alpha (currenttime divided by total time)
		//This means alpha 0 = start of animation and alpha 1 = end of animation
		float alpha = currentAnimationTime / currentlyPlayingAnimation.duration();

		Gdx.app.log(logstag, "alpha="+alpha+" currentAnimation:"+currentlyPlayingAnimation.name());
		
		switch (currentlyPlayingAnimation){
		
		case appearing:
			break;
			
		case beingDestroyed:
		{
			//as this should happen straight after a hit its a fade from white to transparent
			Color trans = hitColor.cpy();
			trans.lerp(Color.CLEAR, alpha);
			this.setColor(trans);
			
			break;
		}
		case damageTaken:
		{
			//the animation change that happens while damage is taken
			//it basically starts the hit color and returns to its normal colour
			//Gdx.app.log(logstag, "(between :"+creaturesNormalColor.toString()+" and "+hitColor.toString()+")");			
			Color trans = hitColor.cpy();
			trans.lerp(creaturesNormalColor, alpha);
			this.setColor(trans);

			//Gdx.app.log(logstag, "newcol="+trans.toString()+"");			
			
			break;
		}
		case none:
			break;
			
		default:
			break;
		
		}
		
		//If we are at the end of the frame animation we remove from the animated object sets
		//and set the animation playing to none
		if (currentAnimationTime>currentlyPlayingAnimation.duration()){
			
			//if the creature was beingDestroyed, we fire destroy() now
			if (currentlyPlayingAnimation == CreatureAnimationType.beingDestroyed){
				destroy();
			}
			
			currentAnimationTime=0.0f; //reset time ready for next animation
			currentlyPlayingAnimation=CreatureAnimationType.none; //set animation playing to none
			ModelManagment.removeAnimating(this);//remove it from the animation list (which is what triggers this updateanimationFrame function			
		}
		
	}


	public void addDrop(SSSNode drop) {
		Gdx.app.log(logstag, "creating drop");
		drops.add(drop);
		
	}



	public void fireReactionToDrop(Vector3 dropsPositionAsVector, DataObject newdrop) {
		
		
		float EX = parentpolution.centeredOnThisLocation.getHubsX(Align.center);
		float EY = parentpolution.centeredOnThisLocation.getHubsY(Align.center);
		float EZ = getCenterOfBoundingBox().z;
		//temp disabled while converting to new movement system
		
		movementControll.setMovement(creaturemodel.transState,false,NewFaceAndMoveTo.create(creaturemodel, dropsPositionAsVector,2000));
		
		//temp resume after (really shouldn't need this but currently autoresume seems broke)
		
		
		Timer.schedule(new Task(){

			@Override
			public void run() {
				if (creaturemodel==null){
					return;
				}
				
				Gdx.app.log(logstag, "___resuming movement after NewMoveTo___");
				Gdx.app.log(logstag, "___Current State is: ___"+ creaturemodel.transState.toString());
								
				movementControll.setMovement(creaturemodel.transState,false,new NewJerk2D(creaturemodel,70f,80f,5000f,600000f), new NEWREPEAT());

			}
			
			
		}, (2000+150)/1000); //the movement should start again shortly after the enlargement ends. We divide by 1000 as Timer.schedule needs the time in seconds, not ms 
		
		//movementControll.setMovement(creaturemodel.transform,false,NewMoveTo.create(creaturemodel, dropsPositionAsVector,2000));
		
		//the false above should be true
		
	}
	
	public float getEyeSightRange(){
		return 250f;
	}



	@Override
	public void setLastHitsRange(float range) {
		lastHitDistance=range;
		
	}



	@Override
	public float getLastHitsRange() {
		return lastHitDistance;
	}



	@Override
	public boolean isBlocker() {
		return false;
	}



	@Override
	public boolean rayHits(Ray ray) {
				
		return 	Intersector.intersectRaySphere(ray, this.getCenterOfBoundingBox(), this.getRadius(), null);
		
	}



	@Override
	public void fireDragStart() {
		// TODO Auto-generated method stub
		
	}
	
	
}
