package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.lostagain.nl.GWTish.Management.ZIndexAttribute;
import com.lostagain.nl.GWTish.Management.ZIndexGroup;
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

		GwtishWidgetDistanceFieldAttribute materialAccordingToStyle = (GwtishWidgetDistanceFieldAttribute) mat.get(GwtishWidgetDistanceFieldAttribute.ID);
		if (materialAccordingToStyle!=null){
			Gdx.app.log(logstag, "3fitarea set as:"+materialAccordingToStyle.textScaleing ); 
		} else {
			Gdx.app.log(logstag, "no textstyle set" ); 
		}
	}

	/**
	 * Sets the color of the object.
	 * This is normally the text color 
	 * (Only supported on objects using the DistanceFieldShader)
	 * @param col
	 */
	public void setColor(Color col){

		nullParameterCheck(col);
		
		if (textStyle!=null){
			
			//Gdx.app.log(logstag,"_________setting color to:"+col);
			
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
	public void setBorderWidth(float glowWidth) {
		
	//	nullParameterCheck(bordercol);
		
		createBackgroundAttributeIfNeeded();
	//	Gdx.app.log(logstag,"_________setting bordercoll:"+bordercol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		//if (backStyle!=null){

			backStyle.glowWidth = glowWidth;
		//	glowingSquare.glowColor = bordercol;
	//	}

	}
	/**
	 * only works with GlowingSquareAttribute shaders right now
	 * @param bordercol
	 */
	public void setBorderColor(Color bordercol) {
		
		nullParameterCheck(bordercol);
		
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
		
		nullParameterCheck(backcol);
		
		
		createBackgroundAttributeIfNeeded();

		
		
		//Gdx.app.log(logstag,"______________backcol:"+backcol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		//if (backStyle!=null){
			//	GlowingSquareAttribute backtexture = ((GlowingSquareShader.GlowingSquareAttribute)objectsMaterial.get(GlowingSquareShader.GlowingSquareAttribute.ID));
			backStyle.backColor.set(backcol);
		//}
		
	//	if (textStyle!=null){
	//		objectsMaterial.set( ColorAttribute.createDiffuse(backcol));
	//	}
		
	}

	private void nullParameterCheck(Color backcol) {
		if (backcol==null){
			Gdx.app.log(logstag,"colour can not be null",new Throwable("null specified for backcolour, this will break shader rendering"));
		}
	}
	
	private void createBackgroundAttributeIfNeeded() {
		
		if (backStyle==null){
			
			Gdx.app.log(logstag,"_________(creating default background shader attribute)");
			
			backStyle =  new GwtishWidgetBackgroundAttribute(1f,Color.CLEAR,Color.CLEAR,1.0f);
			this.addAttributeToShader(backStyle);
			
		}
		
	}
	
	private void createTextAttributeIfNeeded() {
		
		if (textStyle==null){			

			Gdx.app.log(logstag,"_________(creating default text shader attribute)");
			//if we are creating one automatically on demand, everything is set to clear
			textStyle  = new GwtishWidgetDistanceFieldAttribute(GwtishWidgetDistanceFieldAttribute.presetTextStyle.NULL_DONTRENDERTEXT);
			addAttributeToShader(textStyle);			
		}
		
	}
	
	/**
	 * Sets the opacity of this widget.
	 * Specifically it adds a blending style with the opacity set
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

			objectsMaterial.set( new ZIndexAttribute(index,group) );
		
		
	}
	
	/**
	 * Sets z-index value and groupname
	 * 
	 * @param opacity
	 */
	public void setZIndex(int index, ZIndexGroup group) {

			objectsMaterial.set( new ZIndexAttribute(index+1,group) );
		
	}
	
	
	public boolean hasZIndex() {
		return objectsMaterial.has(ZIndexAttribute.ID);		
	}
	
	/**
	 * returns -1 if no zindex set
	 * @return
	 */
	public int getZIndexValue() {
		
		if (hasZIndex()){
			return ((ZIndexAttribute)objectsMaterial.get(ZIndexAttribute.ID)).zIndex;			
		}
		
		return -1;
	}

	
	
	public ZIndexGroup getZIndexGroup() {
		
		if (hasZIndex()){
			return ((ZIndexAttribute)objectsMaterial.get(ZIndexAttribute.ID)).group;
		}
		
		return null;		
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
	public void clearGlowColor() {
		setTextGlowColor(Color.CLEAR); 		
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
	  
	  float PaddingLeft = 0f;
	  float PaddingTop  = 0f;
	  float PaddingRight = 0f;
	  float PaddingBottom  = 0f;
	  
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
	
	/**
	 * Experimental, this effects the shaders impression of the text size only, and doesnt
	 * effect widget size at all
	 */
	public void setTextScale(float scale){
		createTextAttributeIfNeeded();
		textStyle.textScale = scale;				
		//layoutStyleChanged();
	}

	/**
	 * Sets this widgets padding on all four sides.
	 * This will set the shader to render any text inwards by this amount, as well as setting the left padding variable 
	 * 
	 * Unit is assumed to be the world units of your stage
	 * Padding should not exceed widget size/2 if size is fixed
	 * 
	 * @param padding
	 **/
	public void setPadding(float padding){
		PaddingLeft = padding;
		PaddingRight = padding;
		PaddingTop = padding;
		PaddingBottom = padding;
			
		
		createTextAttributeIfNeeded();
		textStyle.paddingLeft = padding;
		textStyle.paddingTop = padding;
				
		layoutStyleChanged();
	}
	
	/**
	 * Sets this widgets left padding.
	 * This will set the shader to render any text inwards by this amount, as well as setting the left padding variable 
	 * 
	 * Unit is assumed to be the world units of your stage
	 * 
	 * @param Left
	 **/
	public void setPaddingLeft(float Left){
		PaddingLeft = Left;
		
		createTextAttributeIfNeeded();
		textStyle.paddingLeft = Left;
		layoutStyleChanged();
	}
	
	/**	 
	 * Unit is assumed to be the world units of your stage
	 * This will set the shader to render any text inwards by this amount, as well as setting the top padding variable 
	 *
	 * @param Top
	 **/
	public void setPaddingTop(float Top){
		PaddingTop = Top;
		
		createTextAttributeIfNeeded();
		textStyle.paddingTop = Top;
		layoutStyleChanged();
	}
	
	/**
	 * Sets this widgets Right padding.
	 * This will set the shader to render any text inwards by this amount, as well as setting the left padding variable 
	 * 
	 * Unit is assumed to be the world units of your stage
	 * 
	 * @param Right
	 **/
	public void setPaddingRight(float Right){
		PaddingRight = Right;
		
		createTextAttributeIfNeeded();
		//textStyle.paddingLeft = Left;
		layoutStyleChanged();
	}
	
	/**	 
	 * Unit is assumed to be the world units of your stage
	 * This will set the shader to render any text inwards by this amount, as well as setting the top padding variable 
	 *
	 * @param Bottom
	 **/
	public void setPaddingBottom(float Bottom){
		PaddingBottom = Bottom;
		
		createTextAttributeIfNeeded();
		//textStyle.paddingTop = Top;
		layoutStyleChanged();
	}

	public float getPaddingLeft() {
		return PaddingLeft;
	}

	public float getPaddingTop() {
		return PaddingTop;
	}

	public float getPaddingRight() {
		return PaddingRight;
	}

	public float getPaddingBottom() {
		return PaddingBottom;
	}



}
