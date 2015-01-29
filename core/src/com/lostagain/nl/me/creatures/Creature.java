package com.lostagain.nl.me.creatures;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
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
import com.lostagain.nl.me.movements.MovementController;
import com.lostagain.nl.me.objects.DataObject;
import com.lostagain.nl.uti.Uti;

public class Creature implements hitable {


	private static String logstag="ME.Creature";
	
	ModelInstance creaturemodel;
	
	//current location
	float x = 0;
	float y = 0;
	float z = 0;
	Matrix4 origin = new Matrix4();
	
	
	//movement
	MovementController movementControll = new MovementController();
	
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

	//query that defines what removes it
	String queryToDestroy; 
	
	//base color
	Color crearturesColor = Color.GREEN;
	//lighter color for when clicked on
	Color hitColor = Color.GREEN.cpy().add(.5f, .5f, .5f, .5f);
	

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
				
		return new Vector3(x,y,z);
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
		
		
	}




	@Override
	public void fireTouchUp() {
		
		//really this check shouldn't be needed but it seems this fires sometimes while its being destroyed, not sure why?
		if (creaturemodel!=null){
			ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);
		
			attribute.color.set( crearturesColor);
		}
		
		
		hit();
		
	}




	private void hit() {
		
		ConceptGun.animateImpactEffect();
		
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
		
		final ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);		
		attribute.color.set( Color.RED );
		
		
		Timer.schedule(new Task(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				attribute.color.set( crearturesColor );
			}
			
			
		}, 0.2f);
	}




	protected void destroy() {

		ConceptGun.animateImpactEffect();
		
		Gdx.app.log(logstag,"_destroying model ");
		
		//remove from visuals
		ModelManagment.removeModel(creaturemodel);
		ModelManagment.removeHitable(this);
				
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
			
			double x = this.x+ (-20+Math.random()*40);			
			double y = this.y+ (-20+Math.random()*40);
						
			MainExplorationView.addnewdrop(newdrop,x, y);			
			
		}
		
	}
	
	
	
	public void updatePosition(float delta){
		
		Matrix4 displacementFromOrigin = movementControll.update(delta);
		
	
		
		
		creaturemodel.transform = origin.cpy().mul(displacementFromOrigin);
		
	}




	public void addDrop(SSSNode drop) {
		Gdx.app.log(logstag, "creating drop");
		drops.add(drop);
		
	}
	
	
	
	
}
