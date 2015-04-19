package com.lostagain.nl.me.models;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.me.gui.ConceptGun;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.shaders.ConceptBeamShader;

/**
 * A 3d model used to represent the concept beam being fired.
 * It is a flat plan, alligned to go from the target to cameras fire point.
 * It also has a impact point at the end - a seperate plane that will represent the effect where the beam hits the model.
 * 
 * The beams look is determained by the concept ammo currently equiped.
 * Currently this is just the color, but in future it could effect the style of the beam.
 * The concept gun being used also will effect the beam.
 * 
 * Controlled by concept ammo;
 * 
 * 1. Color
 * 2. Style
 * 3. How it effects the target (not controlled here)
 * 
 * Controlled by concept gun type;
 * 
 * 1. Width
 * 2. Speed of pulses
 * 3. Gun fire point (not controlled here)
 * 4. Also how it effects the target (not controlled here)
 * 
 * Typically the concept can be considered the "ammo" - with different things effected by different bits of ammo
 * The concept gun itself controls the rate of fire.
 * 
 * 
 * 
 * **/
public class ConceptBeam extends AnimatableModelInstance {
	
	 //defaults (may change, but then the model will need adjusting or maybe dumped and recreated?)
	static float  defaultWidth = 50f;
	static float  defaultLength = 700f;
	
	

	//ammo that fired this beam
	private SSSNode currentBeamsConcept;	
	
	//ammo dependent styles
	private ArrayList<Color> currentColors = new ArrayList<Color>();

	
	public ConceptBeam() {
		super(makeModel(defaultWidth,defaultLength));	
		
		//attach impact point at end
		
	}

	private static Model makeModel(float width,float length) {

		float hw = width/2f;		
		Model beammodel = ModelMaker.createRectangle(-hw,0,hw,length,0, getStandardBeamMaterial());		//when setting up we get the default material	
		return beammodel;
	}
	

	private static Material getStandardBeamMaterial() {
		
		float currentFireFrequency = ConceptGun.FireFrequency;
		
		Color BeamColor = Color.RED;

		Material currentBeamMaterial = new Material("BeamMaterial", 				
				               new ConceptBeamShader.ConceptBeamAttribute(0.35f,BeamColor,currentFireFrequency,Color.WHITE),
							   new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,0.99f));
		 
		
		return currentBeamMaterial;
	}

	
	public void beamFired(float FireFrequency){
		
		this.show();
		
		//ensure style is upto date (if this ever gets too slow it could be updated when the ammo changes instead of before firing)
		Color BeamColor = new Color(randomColorFromConcept());
		
		this.materials.get(0).set(new ConceptBeamShader.ConceptBeamAttribute(0.35f,BeamColor,FireFrequency,Color.WHITE));
		
		
		
		
	}
	
	
	/**
	 * Gets a random color from the ammos color selection. (most ammos have one color right now)
	 * Defaults to red
	 * @return
	 */
	private Color randomColorFromConcept() {		
		if (currentColors.size()==0){
			return Color.RED;	
		}
		int p = (int) (Math.random()*currentColors.size());


		return currentColors.get(p);

	}
	
	public void updateBeam(SSSNode currentBeamsConcept,float currentFireFrequency){
		this.currentBeamsConcept=currentBeamsConcept;
		
		//first update the color selection
		currentColors = DefaultStyles.getColorsFromNode(currentBeamsConcept); //we only use the first color

		if (currentColors==null){
			currentColors = new ArrayList<Color>();
			currentColors.add(Color.RED); //red by default
		}
		
		//next time the beam is fired the new style will be used
		
	}
	
}
