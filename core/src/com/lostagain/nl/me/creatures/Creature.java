package com.lostagain.nl.me.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.hitable;

public class Creature implements hitable {


	private static String logstag="ME.Creature";
	
	ModelInstance creaturemodel;
	
	//current location
	float x = 0;
	float y = 0;
	float z = 0;
	
	//parent population
	Population parentpolution;
	
	//query that defines what removes it
	String query="";
	
	//drops, if any
	SSSNode drops[];
	
	//should be changed based on the size of the creature
	int hitradius = 50;
	
	//Note, if needed we can calculate the radius and position we should use for hits
	//with the below method;
	//calculateBoundingBox(bounds);
    //center.set(bounds.getCenter());
    //dimensions.set(bounds.getDimensions());
    //radius = dimensions.len() / 2f;
    
	
	

	public Creature(float x, float y, Population parentPopulation) {
		
		this.x=x;
		this.y=y;
		this.parentpolution=parentPopulation;
		
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




	@Override
	public void fireTouchDown() {
		
		ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);
		
		attribute.color.set(Color.GREEN);
		
		
	}




	@Override
	public void fireTouchUp() {
		
		//really this check shouldnt be needed but it seems this fires sometimes while its being destroyed, not sure why?
		if (creaturemodel!=null){
			ColorAttribute attribute = creaturemodel.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);
		
			attribute.color.set(Color.RED);
		}
	}
	
	protected void destroy() {
		

		Gdx.app.log(logstag,"_destroying model ");
		
		//remove from visuals
		ModelManagment.removeModel(creaturemodel);
		ModelManagment.removeHitable(this);
				
		//ensure its gone
		creaturemodel=null;
		
		//remove from population
		parentpolution.removeFromPopulation(this);
		
	}
	
	
	
	
}
