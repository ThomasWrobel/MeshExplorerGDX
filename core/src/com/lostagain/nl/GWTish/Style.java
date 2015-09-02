package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.lostagain.nl.shaders.DistanceFieldShader.DistanceFieldAttribute.presetTextStyle;
import com.lostagain.nl.shaders.GlowingSquareShader;
import com.lostagain.nl.shaders.DistanceFieldShader.DistanceFieldAttribute;
import com.lostagain.nl.shaders.GlowingSquareShader.GlowingSquareAttribute;

/**
 * stores style parameters for Elements
 * @author Tom
 *
 */
public class Style {
	final static String logstag = "GWTish.Style";
	Element elementWithStyle;
	Material objectsMaterial = null;

	DistanceFieldAttribute textStyle;
	GlowingSquareAttribute glowingSquare;
	
	
	
	//enums for shader changes (layout is below)
	
	
	//Not used yet, but shouldn't be too hard to modify a shader to add a underline or overline
		public enum TextDecoration {
			     NONE ,
			     UNDERLINE,
			     OVERLINE ,
			     LINE_THROUGH;
		}
		
		
		
	
	/**
	 * The style object must be given the objects material, which for most functions needs to use the distancefieldshader	 * 
	 * @param objectsMaterial
	 **/
	public Style(Element elementWithStyle,Material mat) {
		this.objectsMaterial=mat;
		this.elementWithStyle=elementWithStyle;

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
		//	glowingSquare.glowColor = bordercol;
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
	 * Sets the background color
	 * @param opacity
	 */
	public void addAttributeToShader(Attribute attribute){		

			objectsMaterial.set( attribute);
		
		
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
	
	//-------------------
	//-------------------
	//layout related styles
	//-------------------
	//-------------------
//ref; http://grepcode.com/file/repo1.maven.org/maven2/com.google.gwt/gwt-user/2.0.4/com/google/gwt/dom/client/Style.java
	
	/**
	 * Enum for the text-align property.
	 */
	  public enum TextAlign {
	    CENTER ,
	    @Deprecated
	    JUSTIFY,
	    LEFT,
	    RIGHT;	    
	  }
	  
	  TextAlign textAlignment = TextAlign.LEFT;
	  
	
	
	/**
	 * should be fired when any style related to layout is changed.
	 * ie. text alignment,padding etc
	 * pure Shader changes don't need to fire this.
	 * This method then fires a update function on the object with this style
	 **/
	private void layoutStyleChanged(){
		if (elementWithStyle!=null){
			elementWithStyle.layoutStyleChanged();
		}
		
		
	}

	public TextAlign getTextAlignment() {
		return textAlignment;
	}

	public void setTextAlignment(TextAlign textAlignment) {
		this.textAlignment = textAlignment;
		layoutStyleChanged();
	}
	

}
