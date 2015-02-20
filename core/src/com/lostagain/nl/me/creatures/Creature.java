package com.lostagain.nl.me.creatures;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.me.LocationGUI.LocationsHub;
import com.lostagain.nl.me.creatures.Population.destructOn;
import com.lostagain.nl.me.gui.ConceptGun;
import com.lostagain.nl.me.gui.Inventory;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.movements.FaceAndMoveTo;
import com.lostagain.nl.me.movements.FaceTowards;
import com.lostagain.nl.me.movements.Forward;
import com.lostagain.nl.me.movements.Jerk2D;
import com.lostagain.nl.me.movements.MoveTo;
import com.lostagain.nl.me.movements.MovementController;
import com.lostagain.nl.me.movements.REPEAT;
import com.lostagain.nl.me.movements.RelativeScale;
import com.lostagain.nl.me.movements.RotateLeft;
import com.lostagain.nl.me.movements.RunAwayFrom;
import com.lostagain.nl.me.newmovements.NewForward;
import com.lostagain.nl.me.newmovements.NewMovementController;
import com.lostagain.nl.me.newmovements.NewRotateLeft;
import com.lostagain.nl.me.objects.DataObject;
import com.lostagain.nl.uti.Uti;

public class Creature implements hitable {


	private static String logstag="ME.Creature";
	
	ModelInstance creaturemodel;
	
final static int zPlane = 70; //the horizontal plane the creatures exist on. should be used for all z values in positions.
	//current location
	float x = 0;
	float y = 0;
	float z = 0;
	
	/**
	 * the starting location of the creature 
	 */
	Matrix4 origin = new Matrix4();
	Matrix4 currerntScale =  new Matrix4().scl(1f);
	
	//movement (switching to new system)
	NewMovementController movementControll;//,new Forward(-300,1000)
	
	//parent population
	Population parentpolution;
		
	//drops, if any
	ArrayList<SSSNode> drops=	new ArrayList<SSSNode>();
	
	//should be changed based on the size of the creature
	int hitradius = 50;
	
	//Note, if needed we can calculate the radius and position we should use for hits
	//with the below method;
	//calculateBoundingBox(bounds);
    //center.set(bounds.getCenter());
    //dimensions.set(bounds.getDimensions());
    //radius = dimensions.len() / 2f;
    
	destructOn destructionType = destructOn.clicks; //defaults to a clicks if no query specified
	int numOfHitsLeft = 10;
	boolean destroyed = false;
	
	//query that defines what removes it
	String queryToDestroy; 
	
	//base color
	Color crearturesColor = Color.WHITE;
		
	
	//lighter color for when clicked on
	Color hitColor = Color.GREEN.cpy().add(.5f, .5f, .5f, 1f);
	

	public Creature(float x, float y, Population parentPopulation, int hitPoints, String queryToDestroy, destructOn destructionType) {
		
		
		this.x=x;
		this.y=y;
		this.parentpolution=parentPopulation;
		

		this.destructionType = destructionType;
		this.numOfHitsLeft = hitPoints;
		this.queryToDestroy = queryToDestroy;
		
		
	}



	@Override
	public Vector3 getCenter() {
		
		Vector3 tmp = new Vector3();
		creaturemodel.transform.getTranslation(tmp);
				
		return tmp;  // new Vector3(x,y,z);
	}




	@Override
	public int getRadius() {
		return hitradius;
	}




	@Override
	public Matrix4 getTransform() {
		return creaturemodel.transform;
	}




	public void setmodel(ModelInstance model) {
		
		creaturemodel = model;
		
		//set to model lists
		ModelManagment.addmodel(creaturemodel);
		ModelManagment.addHitable(this);
		
		//make bigger (test only)
		creaturemodel.transform.mul(new Matrix4().setToScaling(0.5f, 2.5f,0.5f));
		
		movementControll = new NewMovementController(creaturemodel.transform);//new Forward(200,3000),new RotateLeft(90,1000), new REPEAT());
		
		//movementControll = new MovementController(creaturemodel.transform,new Jerk2D(creaturemodel,30f,50f,400f,4000f));//new Forward(200,3000),new RotateLeft(90,1000), new REPEAT());
		//movementControll = new MovementController(creaturemodel.transform, new Forward(200,3000),new RotateLeft(90,1000), new REPEAT());//
	//	startCreaturesStandardMovement();	
	}

	public void setColor(Color newcol){

		ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);

		attribute.color.set(newcol);
		crearturesColor = newcol;
		
	}


	@Override
	public void fireTouchDown() {
		
		ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);
		
		attribute.color.set(hitColor);
		

		hit();
	}




	@Override
	public void fireTouchUp() {
		
		//really this check shouldn't be needed but it seems this fires sometimes while its being destroyed, not sure why?
		if (creaturemodel!=null){
			ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);
		
			attribute.color.set( crearturesColor);
		}
		
		
		
	}




	private void hit() {
		Gdx.app.log(logstag, " creature hit  ");	
		if (destroyed){
			Gdx.app.log(logstag, " already being destroyed ");
			return;
		}
		//ConceptGun.animateImpactEffect();

		//when hit move away randomly		
		
		float angle = (float) (Math.random()*360);
		
		Gdx.app.log(logstag, " setting movementControll currently moving: "+movementControll.isMoving());	
		//movementControll.setMovement(creaturemodel.transform,false,new RotateLeft(angle,60),new Forward(50,250));
		
		movementControll.setMovement(creaturemodel.transform,false,new NewRotateLeft(90,90),new NewForward(50,250));
		
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
				this.destroy();
				
			}
		}

		if (destructionType == destructOn.query){
			
			//two ways of using a query on a creature; direct and with the gun
			//we thus check the gun first 
			SSSNode appliedConcept;
			if (!ConceptGun.disabledFire){
				appliedConcept = ConceptGun.equipedConcept;
			} else if (Inventory.currentlyHeld!=null) {
				appliedConcept = Inventory.currentlyHeld.itemsnode;					
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
							//fire the destroy command on this creature
							Creature.this.destroy();
					
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
	
	/** runs the animation of this creature getting damaged **/
	protected void damaged() {

		Gdx.app.log(logstag,"_________creature damaged");
		
		final ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);		
		
		
		float r = (float) Math.random();
		float g = (float) Math.random();
		float b = (float) Math.random();
		Color col =  new Color();
		Color.rgba8888ToColor(col, Color.rgba8888(r, g, b,1.0f) );
		
		
				Gdx.app.log(logstag,"_________creature col="+col.toString());
				
				setColor( col);
		//attribute.color.set(col) ;
		
		
		
		//make this thing bigger (slowly gets bigger then explodes is the plan)

		currerntScale.scl(1.2f);
		//creaturemodel.transform.scl(1.1f);

		Gdx.app.log(logstag,"making bigger:"+currerntScale.getScaleX());
		Gdx.app.log(logstag,"animating:"+movementControll.isMoving());
		Gdx.app.log(logstag,"resuming after:"+movementControll.isGoingToResumeAfter());
		
		float durationOfEnlargement = 500f; //the time taken to enlarge. We also use this to set a timer for when the creature should restart moving
		//movementControll.setMovement(creaturemodel.transform,false,new RelativeScale(1.2f,durationOfEnlargement));
		
		//update radius
		this.hitradius = (int) (hitradius * 1.2f);
		
		
		
		Timer.schedule(new Task(){

			@Override
			public void run() {
				//after enlarging we restart the creatures standard movement.
				Creature.this.startCreaturesStandardMovement();
				
				// TODO Auto-generated method stub
				attribute.color.set( crearturesColor );
			}
			
			
		}, (durationOfEnlargement+150)/1000); //the movement should start again shortly after the enlargement ends. We devide by 1000 as Timer.schedule needs the time in seconds, not ms 
		
	}




	protected void startCreaturesStandardMovement() {
		if (destroyed){
			Gdx.app.log(logstag, " already being destroyed,cantset movement ");
			return;
		}
				

		Gdx.app.log(logstag,"startCreaturesStandardMovement scale="+creaturemodel.transform.getScaleX()+","+creaturemodel.transform.getScaleY());
		
		//movementControll = new NewMovementController(creaturemodel.transform,new Jerk2D(creaturemodel,30f,50f,400f,8000f), new REPEAT());
		
		//movementControll.setMovement(creaturemodel.transform,false,new Jerk2D(creaturemodel,30f,50f,400f,4000f), new REPEAT());
		
	}



	protected void destroy() {
		destroyed=true;
		//ConceptGun.animateImpactEffect();
		//remove from visuals
		ModelManagment.removeModel(creaturemodel);

		Gdx.app.log(logstag,"_destroying model....removed:"+ModelManagment.removeHitable(this));
		
		
		movementControll.clearMovement();
				
	
				
		//ensure its gone
		creaturemodel=null;
		
		//remove from population
		parentpolution.removeFromPopulation(this);
		
		//create drops, if any	
		if (drops!=null && drops.size()>0){

			Gdx.app.log(logstag,"droping drops with the dropdrops(drops) call.");
			dropdrops(drops);
		}
	}




	private void dropdrops(ArrayList<SSSNode> drops2) {
		
		
		for (SSSNode dropsnode : drops2) {
			
			//create new object for it
			DataObject newdrop = new DataObject(dropsnode); //string for debuging, will be removed
			
			//add to world
			Gdx.app.log(logstag, "creating drop on screen");
			
			float x = (float) (this.x+ (-20+Math.random()*40));			
			float y = (float) (this.y+ (-20+Math.random()*40));
						
			MainExplorationView.addnewdrop(newdrop,x, y);			
			
		}
		
	}
	
	
	
	public void updatePosition(float delta){
		
		if (movementControll!=null && movementControll.isMoving()){
			
			Matrix4 displacementFromOrigin = movementControll.getUpdate(delta).cpy();		
			creaturemodel.transform =  displacementFromOrigin;//.mul(currerntScale); // displacementFromOrigin;// .scl(currerntScale); //origin.cpy().mul(displacementFromOrigin);
		
			//Gdx.app.log(logstag, "_______________current size="+creaturemodel.transform .getScaleX()+","+creaturemodel.transform .getScaleY()+","+creaturemodel.transform .getScaleZ()+")");
			
		}
		
		
	}




	public void addDrop(SSSNode drop) {
		Gdx.app.log(logstag, "creating drop");
		drops.add(drop);
		
	}



	public void fireReactionToDrop(Vector3 dropsPositionAsVector, DataObject newdrop) {
		
		
		float EX = parentpolution.centeredOnThisLocation.getHubsX(Align.center);
		float EY = parentpolution.centeredOnThisLocation.getHubsY(Align.center);
		float EZ = getCenter().z;
		//temp disabled while converting to new movement system
		//movementControll.setMovement(creaturemodel.transform,false,FaceAndMoveTo.create(creaturemodel, dropsPositionAsVector,2000));
		//the false above should be true
		
	}
	
	public float getEyeSightRange(){
		return 250f;
	}
	
	
}
