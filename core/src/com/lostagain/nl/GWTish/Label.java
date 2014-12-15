package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.lostagain.nl.me.models.ModelMaker;

/** A Libgdx label that will eventually emulate most of the features of a GWT label (ish)
 * The most significant thing here though is we enable it to use distance mapped fonts in a 3d view **/
public class Label {

	String contents = "TEST";
	Model labelModel = null;
	
	//image
	Image testImage;
	
	//setup
	Boolean setup = false;
	
	//defaults
	BitmapFont defaultFont;
	
	public Label (String contents){
		this.contents=contents;
		
		if (!setup){
			firstTimeSetUp();
			setup=true;
		}
		
		createModel();
		
		
		
	}
	
	private void createModel() {
		
		Material mat = new Material(ColorAttribute.createDiffuse(Color.MAROON));
	
		//mat.set(TextureAttribute.createDiffuse(idealAnimation.getKeyFrame(0)));
		
		labelModel = ModelMaker.createRectangle(0, 0, 100,100, 0, Color.BLUE, mat);
		
	}

	public void firstTimeSetUp(){

    	Texture texture = new Texture(Gdx.files.internal("data/dfield.png"), true);
		texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
		
		testImage  = new Image(texture);
		defaultFont = new BitmapFont(Gdx.files.internal("data/dfield.fnt"), new TextureRegion(texture), false);
		
	}

	public Image getModel() {
		
		return testImage;
	}
	
	
}
