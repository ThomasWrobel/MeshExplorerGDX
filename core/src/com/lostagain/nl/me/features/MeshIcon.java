package com.lostagain.nl.me.features;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;

/** 
 * Will become a 3d icon that can be clicked to turn into a interactive meshFeature.
 * 
 *  Mesh icons are square, with a somewhat customizable border and background style.
 *  They have a short bit of text in the middle, maybe in future an icon representing their content type.
 *  
 *  They extend AnimatableModelInstance so we can animated them latter if we wish 
 * **/
public class MeshIcon extends AnimatableModelInstance {

	//generic icon stuff
	public enum IconType {
		Email,
		Software,
		Links,
		Abilitys,
		Info,
		OTHER; //used as a catch all for unique features.
	}
	
	static final float iconWidth  = 100f; //standard width and height of all icons
	static final float iconHeight = 100f;
	
	
	//this icons stuff
	IconType thisIconsType = null;
	Location parentLocation = null;
	
	GenericMeshFeature assocatiedFeature = null;
	
	/** 
	 * Creates a icon of the specified type.
	 * The idea is this is placed relative to the parent location (or rather the parent locations infoIcon which should always be at the center)
	 *  
	 * **/
	public MeshIcon(IconType type,Location parentLocation,GenericMeshFeature assocatiedFeature) {		
		super(generateIconModel(type.name()));
		
		thisIconsType = type;
		this.parentLocation = parentLocation;
		this.assocatiedFeature = assocatiedFeature;
	}

	/** 
	 * Generates the generic icon model 
	 * 
	 * Currently just a colored rectangle. 
	 * 
	 * TODO: Switch to text+outline
	 * TODO: Then to distancemap shader type
	 * **/
	static private Model generateIconModel(String title){
		
		String name = title;
				
		//make its material (this will change in future to something more pretty)
		//we will eventually need a two-texture custom shader
		//one texture = background style 
		//second texture = distance field shader for sharp text
		
		//for now, we just use a simple texture
        Material material = new Material(
        		ColorAttribute.createDiffuse(Color.CYAN), 
				ColorAttribute.createSpecular(Color.WHITE),
				new BlendingAttribute(1f), 
				FloatAttribute.createShininess(16f));
		
		//work out half widths and heights
		float hw = iconWidth/2;
		float hh = iconHeight/2;
		
		//we create the rectangle at negative half the width and height to positive half the width and height.
		//this ensures its center point is at 0,0
		Model model = ModelMaker.createRectangle(-hw, -hh, hw, hh, 0, material);
		
		return model;		
	}
	
	/** triggers the icon to open showing its contents (assocatiedFeature) **/
	public void open(){
		
	}
	
	/** triggers the icon to close hiding its contents (assocatiedFeature) **/
	public void close(){
		
	}
	
	/** runs the close animation.
	 * Should only be called by the associated GenericMeshFeature.
	 * Do not run from elsewhere **/
	public void animateClose(){
		
	}
	
	/** runs the open animation.
	 * Should only be called by itself, hence private **/
	private void animateOpen(){
		
	}
}
