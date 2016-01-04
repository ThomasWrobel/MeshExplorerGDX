package com.lostagain.nl.me.features;

import com.badlogic.gdx.Gdx;
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
import com.lostagain.nl.me.gui.ScreenUtils;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;

/***
 * 
 * An info box will be a simple box that shows three labels, one for title, subtitle and one for contents.
 * The labels are automatically added under eachother with a set margin between the two
 * 
 * Thats it, nothing else fancy, just a bit of text aligned in the middle.
 * Designed mostly to be used at the center of locations
 * 
 ***/
public class InfoBox extends VerticalPanel implements GenericMeshFeature {
	final static String logstag = "GWTish.InfoBox";
	
	VerticalPanel infoBoxsPanel = new VerticalPanel(); //default panel;
	
	Label subtitleLabel;
	
	float FixedWidth = 440f; //info boxs are a fixed width but if needed expand downwards
	
	
	public InfoBox(String title, String subtitle ,String contents) {	
		super();
		super.setSpaceing(3f); //set a small spacing between elements vertically
		super.setPadding(12f); //padding around border
		
		super.getStyle().clearBackgroundColor(); //set the back colour (excluding border)
		super.getStyle().clearBorderColor();
		
		//add labels
		float scale = 1.3f;
		Label titleLabel = new Label(title,FixedWidth*scale);
		titleLabel.setToScale(new Vector3(scale,scale,scale)); //title larger then normal 
		titleLabel.setLabelBackColor(Color.CLEAR);
		
		
		super.add(titleLabel);
		
		
		if (!subtitle.isEmpty()){
			scale = 0.5f;
			subtitleLabel = new Label(subtitle,FixedWidth*(1/scale)); //note; double width as we are shrinking to half the size	
			subtitleLabel.setToScale(new Vector3(scale,scale,scale)); //subtitle smaller then title

			subtitleLabel.setLabelBackColor(Color.CLEAR);
			super.add(subtitleLabel);

		
		}
		
		if (!contents.isEmpty()){
			scale = 0.7f;
			Label contentLabel = new Label(contents,FixedWidth*(1/scale));	
			contentLabel.setToScale(new Vector3(scale,scale,scale)); //content smaller then title

			contentLabel.setLabelBackColor(Color.CLEAR);
			super.add(contentLabel);

			
		}
		
		
		//labels should have transparent backgrounds by default
	
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

	
	public void setSubtitle(String string) {
		subtitleLabel.setText(string);		
		
	}

	MeshIcon parentIcon = null;
	@Override
	public void setParentMeshIcon(MeshIcon icon) {
		parentIcon = icon;
		return;
	}

	@Override
	public MeshIcon getParentMeshIcon() {
		return parentIcon;
	}
	
	@Override
	public Vector3 getDefaultCameraPosition() {
		//gets the center of this email on the stage
		Vector3 center = getCenterOnStage();
		center.z = ScreenUtils.getSuitableDefaultCameraHeight();
		
		return center;
	}

	//Overridden just for a test
	@Override
	public void setZIndex(int index, String group) {
		Gdx.app.log(logstag,"_-(setZIndex on infobox to:"+index+","+group+")-_");
		super.setZIndex(index, group);
	}
	
	
	
	
	
	
	
	
	
	
}
