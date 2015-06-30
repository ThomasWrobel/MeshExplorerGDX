package com.lostagain.nl.me.features;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.shaders.DistanceFieldShader;

/**
 * 
 * Will be a simple text box using a distance field shader font.
 * MeshIcons can open up to show this bit of text.
 * 
 * Thats it, nothing else fancy, just a bit of text aligned in the middle.
 * Designed mostly to be used at the center of locations
 * 
 * **/
public class InfoBox extends GenericMeshFeature {
	
	public InfoBox(String contents) {		
		this(contents, 300, 200);
		
	
	}
	public InfoBox(String contents,int width,int height) {		
		super(generateModel(contents,width,height));
	
		//slower fadein then default
		this.setFadeDuration(3000);
		
	}

	private static Model generateModel(String contents,int width,int height) {
		
		Texture texttexture = Label.generateTexture(contents, width, height, 1f);
		
		Material infoBoxsBackgroundMaterial = 	new Material("InfoBoxBackground",//TextureAttribute.createDiffuse(texttexture),
				new BlendingAttribute(1f), //makes it addaive?
				ColorAttribute.createDiffuse(Color.RED));
				//new DistanceFieldShader.DistanceFieldAttribute(Color.MAGENTA,5));
		
		float hw = width/2;
		float hh = height/2;
		
		
		((BlendingAttribute)infoBoxsBackgroundMaterial.get(BlendingAttribute.Type)).opacity = 0.1f;
		
		Model model = ModelMaker.createRectangle(-hw, -hh, hw, hh, 0, infoBoxsBackgroundMaterial);

		((BlendingAttribute)infoBoxsBackgroundMaterial.get(BlendingAttribute.Type)).opacity = 1f;
		return model;
	}
	
	public void setOpacity(float opacity){
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial("InfoBoxBackground");
		((BlendingAttribute)infoBoxsMaterial.get(BlendingAttribute.Type)).opacity = opacity;
		
		
	}
	
	/**Updates the appearance based on the current state and how far we are into it.
	 * Typically this would be a fade, with the alpha being the fade value**/
	void updateApperance(float alpha,FeatureState currentState){

		setOpacity(Opacity);
	}

}
