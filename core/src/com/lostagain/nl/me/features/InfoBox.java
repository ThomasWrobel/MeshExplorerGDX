package com.lostagain.nl.me.features;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.LabelBase.TextureAndCursorObject;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;

/***
 * 
 * An info box will be a simple box that shows two labels, one for title and one for contents.
 * The labels are automatically added under eachother with a set margin between the two
 * 
 * Thats it, nothing else fancy, just a bit of text aligned in the middle.
 * Designed mostly to be used at the center of locations
 * 
 ***/
public class InfoBox extends VerticalPanel implements GenericMeshFeature {
	VerticalPanel infoBoxsPanel = new VerticalPanel(); //default panel;
	
	//public InfoBox(String title, String contents) {		
		
	//	this(title,contents);
		
	//}
	
	public InfoBox(String title,String contents) {	
		super();
		super.setSpaceing(4f); //set a small spacing between elements vertically
		super.setBackgroundColor(Color.CLEAR); //set the back colour (excluding border)
		
		//add default labels
		Label testLabel = new Label(title);
		super.add(testLabel);
		Label testLabel2 = new Label(contents);	
		testLabel2.setToscale(new Vector3(0.7f,0.7f,0.7f)); //content smaller then title
		super.add(testLabel2);
		
		//labels should have transparent backgrounds by default
		testLabel.setLabelBackColor(Color.CLEAR);
		testLabel2.setLabelBackColor(Color.CLEAR);
		//
	}

	/*
	private static Model generateModel(String contents,int width,int height) {
		
		TextureAndCursorObject texttexture = Label.generateTextureNormal(contents, width, height, 1f);
		
		
		Material infoBoxsBackgroundMaterial = 	new Material("InfoBoxBackground",TextureAttribute.createDiffuse(texttexture.textureItself),
				new BlendingAttribute(1f), //makes it addaive?
				ColorAttribute.createDiffuse(Color.RED));
				//new DistanceFieldShader.DistanceFieldAttribute(Color.MAGENTA,5));
		
		float hw = width/2;
		float hh = height/2;
		
		
		((BlendingAttribute)infoBoxsBackgroundMaterial.get(BlendingAttribute.Type)).opacity = 0.1f;
		
		Model model = ModelMaker.createRectangle(-hw, -hh, hw, hh, 0, infoBoxsBackgroundMaterial);

		((BlendingAttribute)infoBoxsBackgroundMaterial.get(BlendingAttribute.Type)).opacity = 1f;
		return model;
	}*/
	
	

	
	/**
	 * Updates the appearance based on the current state and how far we are into it.
	 * Typically this would be a fade, with the alpha being the fade value
	 * **/
	public void updateApperance(float alpha,FeatureState currentState){

		setOpacity(alpha);
	}

	@Override
	public AnimatableModelInstance getAnimatableModelInstance() {
		return this;
	}


	

}
