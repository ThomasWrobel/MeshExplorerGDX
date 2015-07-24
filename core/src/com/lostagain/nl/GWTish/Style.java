package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.lostagain.nl.shaders.DistanceFieldShader.DistanceFieldAttribute.presetTextStyle;
import com.lostagain.nl.shaders.GlowingSquareShader;
import com.lostagain.nl.shaders.DistanceFieldShader.DistanceFieldAttribute;
import com.lostagain.nl.shaders.GlowingSquareShader.GlowingSquareAttribute;

/**
 * stores style parameters for objects
 * @author Tom
 *
 */
public class Style {

	final static String logstag = "GWTish.Style";
	Material objectsMaterial = null;

	DistanceFieldAttribute textStyle;
	GlowingSquareAttribute glowingSquare;
	/**
	 * The style object must be given the objects material, which for most functions needs to use the distancefieldshader	 * 
	 * @param objectsMaterial
	 */
	public Style(Material mat) {
		this.objectsMaterial=mat;


		//eventually we might want to convert the second shaders functions into the first, and handle them all by one shader?
		//(so we can have text with a border, etc)
		textStyle     = ((DistanceFieldAttribute)objectsMaterial.get(DistanceFieldAttribute.ID));
		glowingSquare = ((GlowingSquareShader.GlowingSquareAttribute)objectsMaterial.get(GlowingSquareShader.GlowingSquareAttribute.ID));


	}

	/**
	 * Sets the color of the object.
	 * This is normally the text color 
	 * (Only supported on objects using the DistanceFieldShader)
	 * @param col
	 */
	public void setColor(Color col){

		if (textStyle!=null){
			
			Gdx.app.log(logstag,"_________setting color to:"+col);
			
			textStyle.textColour.set(col);
		}

	}
	 /* This is normally the shadow text color 
	 * (Only supported on objects using the DistanceFieldShader)
	 * @param col
	 */
	public void setShadowColor(Color col){

		if (textStyle!=null){
			
			Gdx.app.log(logstag,"_________setting shadow color to:"+col);
			
			textStyle.shadowColour.set(col);
		}

	}
	/**
	 * only works with GlowingSquareAttribute shaders right now
	 * @param bordercol
	 */
	public void setBorderColor(Color bordercol) {

	//	Gdx.app.log(logstag,"_________setting bordercoll:"+bordercol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		if (glowingSquare!=null){

			glowingSquare.coreColor = bordercol;
			glowingSquare.glowColor = bordercol;
		}

	}

	/**
	 * Sets the background color
	 * @param opacity
	 */
	public void setBackgroundColor(Color backcol){		


		//Gdx.app.log(logstag,"______________backcol:"+backcol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		if (glowingSquare!=null){
			//	GlowingSquareAttribute backtexture = ((GlowingSquareShader.GlowingSquareAttribute)objectsMaterial.get(GlowingSquareShader.GlowingSquareAttribute.ID));
			glowingSquare.backColor = backcol;
		}
		
		if (textStyle!=null){
			objectsMaterial.set( ColorAttribute.createDiffuse(backcol));
		}
		
	}

	/**
	 * FOR TESTING ONLY, don't use
	 * @return
	 */
	public Material getMaterial() {
		return objectsMaterial;
	}
	public void clearShadowColor() {
		setShadowColor(Color.CLEAR); 
		
	}
	public void clearBorderColor() {
		setBorderColor(Color.CLEAR);		
	}
	public void clearColor() {
		setColor(Color.CLEAR); 
		
	}
	public void clearBackgroundColor() {
		setBackgroundColor(Color.CLEAR); 
		
	}
	

	public void setTextStyle(presetTextStyle standardwithshadow) {
		if (textStyle!=null){
			textStyle.setToPreset(standardwithshadow);
		}
	}

}
