package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.lostagain.nl.shaders.GwtishWidgetDistanceFieldAttribute.presetTextStyle;
import com.lostagain.nl.shaders.GwtishWidgetBackgroundAttribute;
import com.lostagain.nl.shaders.GwtishWidgetDistanceFieldAttribute;

/**
 * stores style parameters for Elements
 * @author Tom
 *
 */
public class Style {
	final static String logstag = "GWTish.Style";
	Element elementWithStyle;
	Material objectsMaterial = null;

	GwtishWidgetDistanceFieldAttribute textStyle;
	GwtishWidgetBackgroundAttribute    backStyle;
	
	
	
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
		
		//shaders controlled by two attributes;
		textStyle     = ((GwtishWidgetDistanceFieldAttribute)objectsMaterial.get(GwtishWidgetDistanceFieldAttribute.ID));
		backStyle     = ((GwtishWidgetBackgroundAttribute)objectsMaterial.get(GwtishWidgetBackgroundAttribute.ID));
		//(one of these might be null, if so they will be created and added on demand)
		
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
	
	public void setTextGlowColor(Color col) {
		if (textStyle!=null){
			
			Gdx.app.log(logstag,"_________setting glow color to:"+col);
			
			textStyle.glowColour.set(col);
		}
	}
	public void setTextGlowSize(float size) {
		if (textStyle!=null){
			
			Gdx.app.log(logstag,"_________setting glow size to:"+size);
			
			textStyle.glowSize= size;
		}
	}
	public void setTextOutineLimits(float inner,float outer ) {
		if (textStyle!=null){
			
			Gdx.app.log(logstag,"_________setting inner limit to:"+inner);			
			textStyle.outlinerInnerLimit = inner;
			Gdx.app.log(logstag,"_________setting outer limit to:"+outer);			
			textStyle.outlinerOuterLimit = outer;
		}
	}
	public void setTextOutlineColor(Color col) {
		if (textStyle!=null){
			
			Gdx.app.log(logstag,"_________setting outline color to:"+col);
			
			textStyle.outlineColour.set(col); 
		}
	}
	
	
	/**
	 * only works with GlowingSquareAttribute shaders right now
	 * @param bordercol
	 */
	public void setBorderColor(Color bordercol) {
		createBackgroundAttributeIfNeeded();
	//	Gdx.app.log(logstag,"_________setting bordercoll:"+bordercol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		//if (backStyle!=null){

			backStyle.borderColour = bordercol;
		//	glowingSquare.glowColor = bordercol;
	//	}

	}

	/**
	 * Sets the background color
	 * @param opacity
	 */
	public void setBackgroundColor(Color backcol){		
		createBackgroundAttributeIfNeeded();

		//Gdx.app.log(logstag,"______________backcol:"+backcol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		//if (backStyle!=null){
			//	GlowingSquareAttribute backtexture = ((GlowingSquareShader.GlowingSquareAttribute)objectsMaterial.get(GlowingSquareShader.GlowingSquareAttribute.ID));
			backStyle.backColor = backcol;
		//}
		
	//	if (textStyle!=null){
	//		objectsMaterial.set( ColorAttribute.createDiffuse(backcol));
	//	}
		
	}
	
	private void createBackgroundAttributeIfNeeded() {
		if (backStyle==null){
			backStyle =  new GwtishWidgetBackgroundAttribute(1f,Color.CLEAR,Color.CLEAR,1.0f);
			this.addAttributeToShader(backStyle);
		}
	}

	/**
	 * Sets the opacity of this widget.
	 * Specifically it adds a blendering style with the opacity set
	 * 
	 * This opacity will be used in the shader to effect both the backcolour and text colour without altering their colour setting
	 * (ie, if there colour is only 0.5 opacity anyway, then setting the opacity to 1.0 means it will still be 0.5 opacity)
	 * 
	 * @param opacity
	 */
	public void setOpacity(float opacity) {

		
		this.addAttributeToShader(new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,opacity));
		
	}
	
	
	
	/**
	 * Sets z-index value and groupname
	 * 
	 * @param opacity
	 */
	public void setZIndex(int index, String group) {

			objectsMaterial.set( new ZIndexAttribute(index+1,group) );
		
		
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
