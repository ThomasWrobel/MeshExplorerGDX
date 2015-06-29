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
import com.lostagain.nl.me.models.ModelManagment;
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
	
	//all generic mesh features must be able to fade in and out, so we track the opacity here
	float Opacity = 0f;
	float fadeDuration = 0.500f;
	float timeIntoFade = 0.0f;
	enum FeatureState {
		appearing,disapearing,normal,hidden;
	}
	FeatureState currentState = FeatureState.hidden;

	public InfoBox(String contents) {		
		this(contents, 300, 200);
	
	}
	public InfoBox(String contents,int width,int height) {		
		super(generateModel(contents,width,height));
	
	}

	private static Model generateModel(String contents,int width,int height) {
		
		Texture texttexture = Label.generateTexture(contents, width, height, 1f);
		
		Material mat = 	new Material(TextureAttribute.createDiffuse(texttexture),
				new BlendingAttribute(1f), //makes it addaive?
				ColorAttribute.createDiffuse(Color.CYAN));
				//new DistanceFieldShader.DistanceFieldAttribute(Color.MAGENTA,5));
		
		float hw = width/2;
		float hh = height/2;
		
		Model model = ModelMaker.createRectangle(-hw, -hh, hw, hh, 0, mat);
		
		return model;
	}
	
	
	
	
	@Override
	void fadeIn(float duration, Runnable runAfterFadeIn) {
		currentState = FeatureState.appearing;
		Opacity = 0f;
		ModelManagment.addAnimating(this);
	}
	
	@Override
	void fadeOut(float duration, Runnable runAfterFadeOut) {
		currentState = FeatureState.disapearing;
		Opacity = 1f;
		ModelManagment.addAnimating(this);
	}
	
	@Override
	void updateFade(float delta) {
		
		timeIntoFade = timeIntoFade+delta;
		float ratio = timeIntoFade/fadeDuration;	
				
		switch (currentState) {
		case appearing:
			Opacity = ratio;
			if (ratio==1){
				ModelManagment.removeAnimating(this);
				currentState = FeatureState.normal;
			}
			break;
		case disapearing:
			Opacity = 1-ratio;
			if (ratio==1){
				ModelManagment.removeAnimating(this);
				currentState = FeatureState.hidden;
			}
			break;
		case hidden:
			Opacity = 0f;
			break;
		case normal:
			Opacity = 1f;
			break;
		
		}
		
		
		
		
	}

}
