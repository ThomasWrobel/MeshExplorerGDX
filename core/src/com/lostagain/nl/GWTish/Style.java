package com.lostagain.nl.GWTish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.lostagain.nl.GWTish.Style.Unit;
import com.lostagain.nl.GWTish.Management.ZIndexAttribute;
import com.lostagain.nl.GWTish.Management.ZIndexGroup;
import com.lostagain.nl.shaders.GwtishWidgetShaderAttribute.StyleParam;
import com.lostagain.nl.shaders.GwtishWidgetShaderAttribute.presetTextStyle;
import com.lostagain.nl.shaders.GwtishWidgetShaderAttribute;

/**
 * stores style parameters for Elements
 * @author Tom
 *
 */
public class Style {
	final static String logstag = "GWTish.Style";
	public static Logger Log = Logger.getLogger(logstag); //not we are using this rather then gdxs to allow level control per tag
	
	
	Element elementWithStyle;
	Material objectsMaterial = null;

	GwtishWidgetShaderAttribute textStyle;
	
//	GwtishWidgetBackgroundAttribute    backStyle;
	
	
	
	//enums for shader changes (layout is below)
	
	
	//Not used yet, but shouldn't be too hard to modify a shader to add a underline or overline
		public enum TextDecoration {
			     NONE ,
			     UNDERLINE,
			     OVERLINE ,
			     LINE_THROUGH;
		}
		
		
		
	
	/**
	 * The style object must be given the objects material, which for most functions needs to use the distancefieldshader
	 * Its this material which will have its attributes changed
	 * 
	 * @param objectsMaterial
	 **/
	public Style(Element elementWithStyle,Material mat) {
		this.objectsMaterial=mat;
		this.elementWithStyle=elementWithStyle;
		
		//shaders controlled by two attributes;
		textStyle     = ((GwtishWidgetShaderAttribute)objectsMaterial.get(GwtishWidgetShaderAttribute.ID));
	//	backStyle     = ((GwtishWidgetBackgroundAttribute)objectsMaterial.get(GwtishWidgetBackgroundAttribute.ID));
	
		//(one of these might be null, if so they will be created and added on demand)

		GwtishWidgetShaderAttribute materialAccordingToStyle = (GwtishWidgetShaderAttribute) mat.get(GwtishWidgetShaderAttribute.ID);
		if (materialAccordingToStyle!=null){
			Log.info( "3fitarea set as:"+materialAccordingToStyle.textScaleingMode ); 
		} else {
			Log.info( "no textstyle set" ); 
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

		//	Log.info("_________setting color to:"+col);
			//Log.info("_________setting color to:"+col);
			
			textStyle.textColour.set(col);
		}

	}
	 /* This is normally the shadow text color 
	 * (Only supported on objects using the DistanceFieldShader)
	 * @param col
	 */
	public void setShadowColor(Color col){

		if (textStyle!=null){
			
		//	Log.info("_________setting shadow color to:"+col);
			
			textStyle.shadowColour.set(col);
		}

	}
	
	public void setTextGlowColor(Color col) {
		if (textStyle!=null){
			
			Log.info("_________setting glow color to:"+col);
			
			textStyle.glowColour.set(col);
		}
	}
	public void setTextGlowSize(float size) {
		if (textStyle!=null){			
			Log.info("_________setting glow size to:"+size);			
			textStyle.glowSize= size;
		}
	}
	public void setTextOutineLimits(float inner,float outer ) {
		if (textStyle!=null){
			
			Log.info("_________setting inner limit to:"+inner);			
			textStyle.outlinerInnerLimit = inner;
			Log.info("_________setting outer limit to:"+outer);			
			textStyle.outlinerOuterLimit = outer;
		}
	}
	public void setTextOutlineColor(Color col) {
		if (textStyle!=null){
			
			Log.info("_________setting outline color to:"+col);
			
			textStyle.outlineColour.set(col); 
		}
	}
	
	
	/**
	 * Sets the width of the background border.
	 * Should be in world units, but it looks odd if too big.
	 * 
	 * @param borderWidth
	 */
	public void setBorderWidth(float borderWidth) {
		
	//	nullParameterCheck(bordercol);
		
	//	createBackgroundAttributeIfNeeded();
	//	Log.info("_________setting bordercoll:"+bordercol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		//if (backStyle!=null){

			textStyle.borderWidth = borderWidth;
			
		//	glowingSquare.glowColor = bordercol;
	//	}

	}
	/**
	 * @param bordercol
	 */
	public void setBorderColor(Color bordercol) {
		
		nullParameterCheck(bordercol);
		
	//	createBackgroundAttributeIfNeeded();
	//	Log.info("_________setting bordercoll:"+bordercol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		//if (backStyle!=null){

		textStyle.borderColour = bordercol;
		//	glowingSquare.glowColor = bordercol;
	//	}

	}
	
	public void setBorderRadius(float radius) {
	//	createBackgroundAttributeIfNeeded();
			textStyle.cornerRadius = radius;
	}

	/**
	 * Sets the background color
	 * @param opacity
	 */
	public void setBackgroundColor(Color backcol){	
		
		nullParameterCheck(backcol);
		
		
		//createBackgroundAttributeIfNeeded();

		
		
		//Log.info("______________backcol:"+backcol);

		//get the material from the model
		//Material infoBoxsMaterial = this.getMaterial(SHADERFORBACKGROUND);
		//if (backStyle!=null){
			//	GlowingSquareAttribute backtexture = ((GlowingSquareShader.GlowingSquareAttribute)objectsMaterial.get(GlowingSquareShader.GlowingSquareAttribute.ID));
		textStyle.backColor.set(backcol);
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
	
	/*
	private void createBackgroundAttributeIfNeeded() {
		
		if (backStyle==null){
			
			Log.info("_________(creating default background shader attribute)");
			
			backStyle =  new GwtishWidgetBackgroundAttribute(1f,Color.CLEAR,Color.CLEAR,1.0f);
			this.addAttributeToShader(backStyle);
			
		}
		
	}*/
	
	private void createTextAttributeIfNeeded() {
		
		if (textStyle==null){			

			Log.info("_________(creating default text shader attribute)");
			//if we are creating one automatically on demand, everything is set to clear
			textStyle  = new GwtishWidgetShaderAttribute(GwtishWidgetShaderAttribute.presetTextStyle.NULL_DONTRENDERTEXT);
			addAttributeToShader(textStyle);			
		}
		
	}
	
	//BlendingAttribute blendAttribute;
	
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

		//create blend shader if needed, else change the one we have
		
/*
		Log.info("_________(request opacity setting)");
		Log.info("_________(attributes:)"+objectsMaterial.size());

		Log.info("_________(objectsMaterial)"+BlendingAttribute.Type);
		Log.info("_________(objectsMaterial)"+objectsMaterial.has(BlendingAttribute.Type));
		*/
		
		
		if (!objectsMaterial.has(BlendingAttribute.Type)){
			
			BlendingAttribute blendAttribute = new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,opacity);
			this.addAttributeToShader(blendAttribute);
			
		} else {
			
			((BlendingAttribute) objectsMaterial.get(BlendingAttribute.Type)).opacity = opacity;
		}
		
		
		
	}
	
	public float getOpacity() {		
		if (objectsMaterial.has(BlendingAttribute.Type)){
			return ((BlendingAttribute) objectsMaterial.get(BlendingAttribute.Type)).opacity;
		}
		return 1.0f;
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
	 * Sets z-index value and group
	 * 
	 * @param opacity
	 */
	public void setZIndex(int index, ZIndexGroup group) {

			objectsMaterial.set( new ZIndexAttribute(index,group) ); 
		
	}
	
	
	public void clearZIndex() {
		if (hasZIndex()){
			objectsMaterial.remove(ZIndexAttribute.ID);
		}
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
	 * Enum for the style unit
	 * PX is normally the one supported for now on Label settings, but others will hopefully be supported eventually
	 * Dont use others for now
	 * @author darkflame
	 *
	 */	
	 public enum Unit {
		 /** special unit used to indicate a not-set value **/
		 NOTSET,
		 /** Unitless, this means if used for lineheight the number gets multiplied by the fontsize**/
		 UNITLESS,
		 PX,
		 /**percentage, should be % in strings **/
		 PCT, 
		 MM,
		 CM,
		 IN,
		 PC,
		 PT,
		 EX		 
	 }
	
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
	
	
	double lineHeight   = -1;
	Unit lineHeightUnit = Unit.NOTSET; 
	
	public void setLineHeight(double value,
            Style.Unit unit) {
		lineHeight = value;
		lineHeightUnit=unit;
		layoutStyleChanged();
		
	}
	/**
	 * returns -1 if no line height has been set
	 * @return
	 */
	public double getLineHeightValue(){
		if (lineHeightUnit==Unit.NOTSET){
			return -1;
		}		
		return lineHeight;		
	}
	
	public Unit getLineHeightUnit(){
		return lineHeightUnit;
	}
	
	/**
	 * DONT CHANGE THIS VALUE.
	 * This is only here right now for experiments, allthough the setting itself is needed for fixedsized labels to work internally.
	 * 
	 * Ultimately, this effects the shaders impression of the text size *only*, and doesn't
	 * effect widget size at all
	 * 
	 * In future, hopefully soon, there will be proper font size controll. Dont use this as a replacement!
	 */
	public void setTextScale(float scale){
		createTextAttributeIfNeeded();
		textStyle.textScale = scale;				
	//	layoutStyleChanged();
	}
	

	//font sizing implementation wip
	double fontSize = -1;
	Unit fontSizeUnit =  Unit.NOTSET;
	
	public double getFontSize() {
		if (fontSizeUnit==Unit.NOTSET){
			return -1;
		}		
		return fontSize;
	}

	public Unit getFontSizeUnit() {
		return fontSizeUnit;
	}

	/**
	 * not implemented yet
	 * @param size
	 * @param unit (px only for now)
	 */
	public void setFontSize(int size, Unit unit) {
		fontSize = size;
		fontSizeUnit = unit;
		//setting the font size might change the layout a lot

		layoutStyleChanged();
	}
	//------------
	
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
	 * NOTE; if using fit-area labels, padding has no effect
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
	


	//---------------
	//wip animation stuff

	public void addTransitionState(StyleParam type, float time, Color value) {
		textStyle.addTransitionState(type, time, value);
		
	}
	public void addTransitionState(StyleParam type, float time, float value) {
		textStyle.addTransitionState(type, time, value);
		
	}
	public void setTransitionLength(float totalAnimationTime) {
		textStyle.setTransitionLength(totalAnimationTime);
		
	}
	public void setTransitionIterationCount(int totalAnimationTime) {
		textStyle.setTransitionIterationCount(totalAnimationTime);
		
	}
	public void debugTransitionStates(){		
		Log.info(textStyle.debugTransitionStates());		
	}
	

	public void setPercentageIntoAnimation(float percentageIntoAnimation){
		textStyle.setPercentageIntoAnimation(percentageIntoAnimation);
	}

	/**
	 * updates the animation, if any
	 * @param f
	 */
	public void updateDelta(float f) {
		textStyle.updateDelta(f);
	}

	
	
}
