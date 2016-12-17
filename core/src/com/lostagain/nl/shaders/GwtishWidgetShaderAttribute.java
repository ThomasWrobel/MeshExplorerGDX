package com.lostagain.nl.shaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.lostagain.nl.GWTish.Style.TextAlign;
import com.lostagain.nl.GWTish.Style.TextVerticalAlign;

/**
 * a shader attribute that controlls all style settings for the shader
 * 
 * GWTish widgets are rendered with a special distance field based shader that can cope with text well at various distances,
 * as well as supply various background and border options 
 *
 * @author darkflame
 *
 */
public class GwtishWidgetShaderAttribute extends Attribute {
	public static Logger Log = Logger.getLogger("GwtishWidgetShaderAttribute"); //not we are using this rather then gdxs to allow level control per tag

	public final static String Alias = "GwtishWidgetDistanceFieldAttribute";
	public final static long ID = register(Alias);

	/**
	 * the actual map that defines the text, if any
	 */
	public Texture distanceFieldTextureMap;
	
	/**
	 * If clear is specified for the text colour,outline colour, glow and shader colour, then the text rendering
	 * is disabled completely. This should be used if you just wish to use the background shader on GWTish widgets
	 */
	public Color textColour          = Color.WHITE;
	public float width               = 0; //Can't yet work out how best to make this work in the shader

	public float outlinerInnerLimit  = 10f; //Arbitrarily big size for no outline 0.2 is a good default
	public float outlinerOuterLimit  = 10f; //Arbitrarily big size for no outline 0.05 for default v_outlinerOuterLimit
	public Color outlineColour       = Color.CLEAR;

	public float glowSize            = 0.0f; //size of glow (values above 1 will look strange)
	public Color glowColour          = new Color(0.0f,1.0f,0.0f,1.0f); 

	public float shadowXDisplacement = 1.0f;
	public float shadowYDisplacement = 1.0f;
	public float shadowBlur          = 0.0f;
	public Color shadowColour        = Color.CLEAR;

	
	
	
	public enum TextScalingMode {
		natural,fitarea,fitPreserveRatio
	}


	/**
	 * The relationship between the text and the size of the model.
	 * Do we scale it to fit or not?
	 * ("Fit" in this case means its dimensions match the area of the model minus the area of the padding)
	 */
	public TextScalingMode textScaleingMode = TextScalingMode.natural;
	
	public TextVerticalAlign textAlignmentVertical = TextVerticalAlign.TOP;
	public TextAlign textAlignmentHorizontal = TextAlign.LEFT;
	

	/**
	 * manually controll text scale. Atm this is arbitary and expiremental atm, hopefully pixel based in future.
	 * (or, at least, pixel based on the texture, the end result could still be any sized)
	 */
	public float textScale = 1.0f;


	/**
	 * Will be used to displace the text image from the top left corner of the shader	  
	 */
	public float paddingLeft = 0.0f;

	/**
	 * Will be used to displace the text image from the top left corner of the shader	  
	 */
	public float paddingTop = 0.0f;



	public float getTextScale() {
		return textScale;
	}


	public void setTextScale(float textScale) {
		this.textScale = textScale;
	}

	//---------------------
	//---------new backgroud parameters (moved from widget background attribute)

	public float borderWidth = 1f;
	public Color backColor = Color.CLEAR;
	public Color borderColour = Color.CLEAR;


	/**
	 * the radius of the curved corners
	 * Behaves strangly if less then 1f.
	 * (the background starts becoming the colour of the border)
	 */
	public float cornerRadius = 1f;

	//-------------------------------------
	//--------------

	
	//--------------------------
	//------filter handling
	//--------------------------
	//Work in progress
	public boolean usesPostFilter=false; //should be set to true if filter values arnt default values
	public float filter_brightness = 1.0f; //default values
	public float filter_contrast   = 1.0f;
	public float filter_saturation = 1.0f;
	
	
	

	/**
	 * Temp variable only. Controls a multiplier factor for opacity that applys to all Get statements of colour components (like getTextColor)
	 * This should be reset to 1 in most situations.
	 */
	public float Overall_Opacity_Multiplier = 1f;

	//enum help manage preset styles
	public enum presetTextStyle {
		NULL_DONTRENDERTEXT ( 1,Color.CLEAR ,10,10,Color.CLEAR,0   ,Color.CLEAR, Color.CLEAR,-0.6f,0.6f,0.3f),		
		standardWithShadow ( 1,Color.BLACK ,10,10,Color.CLEAR,0   ,Color.CLEAR, Color.BLACK,-0.6f,0.6f,0.3f),
		standardWithRedGlow(1f,Color.BLACK ,10,10,Color.CLEAR,0.7f,Color.RED  , Color.BLACK,   0f,  0f,  0f),
		whiteWithShadow    ( 1,Color.WHITE ,10,10,Color.CLEAR,0   ,Color.CLEAR, Color.BLACK,-1f,1f,0.5f);

		private Color textColour;
		private float width;
		private float outlinerInnerLimit;
		private float outlinerOuterLimit;
		private Color outlineColour;
		private float glowSize;
		private Color glowColour;
		private float shadowXDisplacement;
		private float shadowYDisplacement;
		private float shadowBlur;
		private Color shadowColour;

		presetTextStyle(
				float width,
				Color textColour,
				float outlinerInnerLimit,
				float outlinerOuterLimit, 
				Color outlineColour,
				float glowSize, 
				Color glowColour,
				Color shadowColour,
				float shadowXDisplacement, 
				float shadowYDisplacement,
				float shadowBlur){



			this.textColour = textColour.cpy();
			this.width = width;
			this.outlinerInnerLimit = outlinerInnerLimit;
			this.outlinerOuterLimit = outlinerOuterLimit;
			this.outlineColour = outlineColour.cpy(); //Note we copy the colours to ensure we dont maintain a referance to the original and thus open the presets up for changes
			this.glowSize = glowSize;
			this.glowColour = glowColour.cpy();

			//Gdx.app.log(logstag, this.name()+" glowColour set to:"+this.glowColour);


			this.shadowXDisplacement = shadowXDisplacement;
			this.shadowYDisplacement = shadowYDisplacement;
			this.shadowBlur = shadowBlur;
			this.shadowColour = shadowColour.cpy();


		}

	}

	/**
	 * Create a distance field attribute from a preset
	 * @param preset
	 */
	public GwtishWidgetShaderAttribute (GwtishWidgetShaderAttribute.presetTextStyle preset) {
		super(ID);

		setToPreset(preset);

	}


	public void setToPreset(GwtishWidgetShaderAttribute.presetTextStyle preset) {
		this.textColour         = preset.textColour.cpy();
		this.width              = preset.width;
		this.outlinerInnerLimit = preset.outlinerInnerLimit;
		this.outlinerOuterLimit = preset.outlinerOuterLimit;
		this.outlineColour      = preset.outlineColour.cpy();
		this.glowSize           = preset.glowSize;
		this.glowColour         = preset.glowColour.cpy();

		//Gdx.app.log(logstag, " glowColour on this atrib set to:"+this.glowColour);

		this.shadowXDisplacement = preset.shadowXDisplacement;
		this.shadowYDisplacement = preset.shadowYDisplacement;
		this.shadowBlur = preset.shadowBlur;
		this.shadowColour = preset.shadowColour.cpy();
	}


	public GwtishWidgetShaderAttribute(
			float width,
			Color textColour,
			float outlinerInnerLimit,
			float outlinerOuterLimit, 
			Color outlineColour,
			float glowSize, 
			Color glowColour,
			Color shadowColour,
			float shadowXDisplacement, 
			float shadowYDisplacement,
			float shadowBlur, 
			float paddingTop,
			float paddingLeft,
			TextScalingMode textScaleing,
			final float glowWidth,final Color backColor, final Color borderColour , final float cornerRadius,
			Texture text
			) {

		super(ID);

		this.textColour = textColour.cpy();
		this.width = width;
		this.outlinerInnerLimit = outlinerInnerLimit;
		this.outlinerOuterLimit = outlinerOuterLimit;
		this.outlineColour = outlineColour.cpy();;
		this.glowSize = glowSize;
		this.glowColour = glowColour.cpy();;
		this.shadowXDisplacement = shadowXDisplacement;
		this.shadowYDisplacement = shadowYDisplacement;
		this.shadowBlur = shadowBlur;
		this.shadowColour = shadowColour.cpy();
		this.paddingTop = paddingTop;
		this.paddingLeft =paddingLeft;
		this.textScaleingMode=textScaleing;

		//background params
		this.borderWidth = glowWidth;
		if (backColor!=null){
			this.backColor = backColor.cpy();
		}
		if (borderColour!=null){
			this.borderColour = borderColour.cpy();
		}
		this.cornerRadius=cornerRadius;
		this.distanceFieldTextureMap=text;
	}

	/**
	 * 
	 * @param glowWidth
	 * @param backColor
	 * @param borderColour
	 * @param cornerRadius
	 */
	public GwtishWidgetShaderAttribute(final float glowWidth,final Color backColor, final Color borderColour , final float cornerRadius) {

		super(ID);
		this.borderWidth = glowWidth;
		this.backColor = backColor.cpy();
		this.borderColour = borderColour.cpy();
		this.cornerRadius=cornerRadius;

	}

	/**
	 * The presence of this parameter will cause the DistanceFieldAttribute to be used
	 * @param textColour
	 * @param width - no effect cant work out how to do this correctly in the shader file
	 */
	public GwtishWidgetShaderAttribute (final Color textColour,final float width) {

		super(ID);
		this.textColour =  textColour.cpy();
		this.width = width;

	}
	/**
	 * 
	 * @param type
	 * @param textColour
	 * @param width - no effect yet. 
	 * @param outlinerInnerLimit (0-1)
	 * @param outlinerOuterLimit (0-1)
	 */
	public GwtishWidgetShaderAttribute(long type, Color textColour,
			float width, float outlinerInnerLimit,
			float outlinerOuterLimit) {
		super(ID);
		this.textColour = textColour.cpy();
		this.width = width;
		this.outlinerInnerLimit = outlinerInnerLimit;
		this.outlinerOuterLimit = outlinerOuterLimit;
	}

	@Override
	public Attribute copy () {

		return new GwtishWidgetShaderAttribute(width,
				textColour,
				outlinerInnerLimit,
				outlinerOuterLimit, 
				outlineColour,
				glowSize, 
				glowColour,
				shadowColour,
				shadowXDisplacement, 
				shadowYDisplacement,
				shadowBlur,
				paddingTop,
				paddingLeft,
				textScaleingMode,
				borderWidth,
				backColor,
				borderColour,
				cornerRadius,
				distanceFieldTextureMap);

	}



	@Override
	protected boolean equals (Attribute other) {
		if (
				(((GwtishWidgetShaderAttribute)other).width == width) &&
				(((GwtishWidgetShaderAttribute)other).textColour == textColour)  &&
				(((GwtishWidgetShaderAttribute)other).outlinerInnerLimit == outlinerInnerLimit) &&
				(((GwtishWidgetShaderAttribute)other).outlinerOuterLimit == outlinerOuterLimit)  &&
				(((GwtishWidgetShaderAttribute)other).outlineColour == outlineColour) &&
				(((GwtishWidgetShaderAttribute)other).glowSize == glowSize)  &&
				(((GwtishWidgetShaderAttribute)other).glowColour == glowColour) &&
				(((GwtishWidgetShaderAttribute)other).shadowColour == shadowColour)  &&
				(((GwtishWidgetShaderAttribute)other).shadowXDisplacement == shadowXDisplacement) &&
				(((GwtishWidgetShaderAttribute)other).shadowYDisplacement == shadowYDisplacement)  &&
				(((GwtishWidgetShaderAttribute)other).shadowBlur == shadowBlur) &&
				(((GwtishWidgetShaderAttribute)other).paddingTop == paddingTop) &&
				(((GwtishWidgetShaderAttribute)other).paddingLeft == paddingLeft) &&
				(((GwtishWidgetShaderAttribute)other).textScaleingMode == textScaleingMode) &&
				(((GwtishWidgetShaderAttribute)other).borderWidth == borderWidth      ) &&
				(((GwtishWidgetShaderAttribute)other).cornerRadius == cornerRadius) &&
				(((GwtishWidgetShaderAttribute)other).backColor == backColor      ) &&
				(((GwtishWidgetShaderAttribute)other).borderColour == borderColour) 


				)

		{
			return true;

		}
		return false;
	}


	@Override
	public int compareTo(Attribute o) {

		//Ensuring attribute we are comparing too is the same type, if not we truth
		if (type != o.type) return type < o.type ? -1 : 1; //if not the same type and less then we return -1 else we return 1

		//if they are the same type we continue	
		double otherwidth = ((GwtishWidgetShaderAttribute)o).width; //just picking width here arbitarily. Theres no real reason for these to be rendered in a different order relative to eachother
		//so the order can be pretty arbitarily.

		return width == otherwidth ? 0 : (width < otherwidth ? -1 : 1);

	}


	public float getOverall_Opacity_Multiplier() {
		return Overall_Opacity_Multiplier;
	}


	/**
	 * This value will be multiplied by the alpha channel of any get...Color() method used.
	 * The idea is to use it as a temp value to allow BlendingAttribute opacity to effect the text in the shader too.
	 * REMEMBER TO RESET THIS VALUE TO 1 BY DEFAULT IF NO BLENDING IS SET
	 * @param overall_Opacity_Multiplier
	 */
	public void setOverall_Opacity_Multiplier(float overall_Opacity_Multiplier) {
		Overall_Opacity_Multiplier = overall_Opacity_Multiplier;
	}

	public Color getTextColour() {
		Color effectiveTextColour = textColour.cpy();
		effectiveTextColour.a = effectiveTextColour.a * Overall_Opacity_Multiplier;
		return effectiveTextColour;
	}

	public Color getOutlineColour() {
		Color effectiveOutlineColour = outlineColour.cpy();
		effectiveOutlineColour.a = effectiveOutlineColour.a * Overall_Opacity_Multiplier;
		return effectiveOutlineColour;
	}
	public Color getGlowColour() {
		Color effectiveGlowColour = glowColour.cpy();
		effectiveGlowColour.a = effectiveGlowColour.a * Overall_Opacity_Multiplier;
		return effectiveGlowColour;
	}
	public Color getShadowColour() {
		Color effectiveShadowColour = shadowColour.cpy();
		effectiveShadowColour.a = effectiveShadowColour.a * Overall_Opacity_Multiplier;
		return effectiveShadowColour;
	}


	/**
	 * Sets the displacement from the top/left of the widget where this text will be rendered.
	 * Note; Should be kept in-sync with the widgets own padding, to ensure the widgets size correctly reflects
	 * the padding (ie, not cropped)
	 * 
	 * @param paddingX
	 * @param paddingY
	 */
	public void setPaddingX(float paddingX,float paddingY) {
		this.paddingLeft = paddingX;
		this.paddingTop  = paddingY;
	}


	public void setTextScaleing(TextScalingMode textScaleing) {
		this.textScaleingMode = textScaleing;
	}


	public Color getBackColor() {

		Color effectiveBackColour = backColor.cpy();
		effectiveBackColour.a = effectiveBackColour.a * Overall_Opacity_Multiplier;

		return effectiveBackColour;
	}

	public Color getBorderColour() {
		Color effectiveBorderColour = borderColour.cpy();
		effectiveBorderColour.a = effectiveBorderColour.a * Overall_Opacity_Multiplier;

		return effectiveBorderColour;
	}

	//--------------------------------------------------
	//---------------------------------------
	//--------------------------
	/**
	 * Below is WIP animation system stuff
	 */

	/**
	 * enum specifying type of style parameter, used for the animation system 
	 */
	public enum StyleParam {
		/** text color **/
		color,
		/** back color **/
		backcolor,
		/***
		 * glowSize
		 */
		glowSize,
		/**
		 * glowColour
		 */
		glowColour,
		/**
		 * shadowColour
		 */
		shadowColour,
		/**
		 * shadowXDisplacement
		 */
		shadowXDisplacement,
		/**
		 * shadowYDisplacement
		 */
		shadowYDisplacement,
		/**
		 * borderColor
		 */
		borderColor;
	}

	class stylestate {
		public stylestate(Color value, float time) {
			super();
			this.colorValue = value;
			this.time = time;
		}
		public stylestate(float value, float time) {
			super();
			this.floatValue = value;
			this.time = time;
		}
		Color colorValue;
		float floatValue;
		float time;
	}


	/**
	 * total time for transition. currently applys to all styleparam types changing
	 * ms
	 */
	float totalAnimationTime = -1; //no animation


	int TransitionIterationCount = -1; //infinite
	
	public void setTransitionLength(float totalAnimationTime) {
		this.totalAnimationTime = totalAnimationTime;
		if (totalAnimationTime>0){
			animating=true;
		}
	}

	public void setTransitionIterationCount(int count) {
		this.TransitionIterationCount = count;
	}
	
	double currentTotalTime = 0.0f; //ms
	boolean animating=false;


	public void updateDelta(double d){
		
		if (!animating){
			return;
		}

		//make sure time is also set
		if (totalAnimationTime<0){
			animating=false;
			return;
		}


		currentTotalTime=currentTotalTime+d;
		//Log.info("current total delta:"+currentTotalTime);
		
		//are we still animating?
		if ((TransitionIterationCount!=-1) && ((currentTotalTime/totalAnimationTime)>TransitionIterationCount)){
			animating=false;
			return;
			
		}

		//work out percentage into animation (0.0-1.0)
		double timeleft = currentTotalTime%totalAnimationTime;	//currently we loop forever
		double percentage = timeleft/totalAnimationTime;



		setPercentageIntoAnimation(percentage);
	}

	/**
	 * a list of arrays, each containing animations for a particular transition between style parameter states
	 */
	HashMap<StyleParam,ArrayList<stylestate>> allTransitionStates =  new HashMap<StyleParam,ArrayList<stylestate>>();

	

	/*
		@keyframes example {
		    0%   {background-color: red;}
		    25%  {background-color: yellow;}
		    50%  {background-color: blue;}
		    100% {background-color: green;}
		}
	 */

	/**
	 * 
	 * @param type
	 * @param time - between 0 and 1
	 * @param value
	 */
	public void addTransitionState(StyleParam type, float time, Color value) {


		//check time is in range
		if (time>1.0f || time <0.0f){
			return;
		}

		stylestate newstate = new stylestate(value,time);	
		
		addTransitionState(type, newstate);

	}

	/**
	 * 
	 * @param type
	 * @param time - between 0 and 1
	 * @param value
	 */
	public void addTransitionState(StyleParam type, float time, float value) {


		//check time is in range
		if (time>1.0f || time <0.0f){
			return;
		}

		stylestate newstate = new stylestate(value,time);	
		
		addTransitionState(type, newstate);

	}


	public void addTransitionState(StyleParam type, stylestate newstate) {

		//ensure animation
		animating=true;
		//
		ArrayList<stylestate> arrayList = allTransitionStates.get(type);

		if (arrayList == null){			//if needed add a new type to the transition list
			arrayList = new ArrayList<stylestate>();
			allTransitionStates.put(type, arrayList);
		}
	
		arrayList.add(newstate); //add state
	}

	/**
	 * 
	 * @return
	 */
	public String debugTransitionStates(){

		String transitionStates = "\n";
		transitionStates = transitionStates + " Transition Time:"+totalAnimationTime+" \n";

		for (StyleParam stylePramType : StyleParam.values()) {

			ArrayList<stylestate> states = allTransitionStates.get(stylePramType);
			if (states != null){
				transitionStates = transitionStates + " Animation for "+stylePramType.toString()+". \n";

				for (stylestate stylestate : states) {
					transitionStates = transitionStates + " ---- "+stylestate.time+"  :  "+stylestate.colorValue+" \n";

				}	

			} else {
				transitionStates = transitionStates + " No animation for "+stylePramType.toString()+". \n";
			}


		}

		return transitionStates;
	}






	//
	//In order to implement animated css like emulation, for each frame we will need to update the various attribute values to what they should
	//be before they are passed to the shader
	//0. Work out current % into animation. 1-((total duration - current time)/100)
	//1. Loop over each type of attribute ( enum in Style for these. Color,Opacity,Border size etc)
	//2. check for the presence of a Transition for this type, and if so get it
	//3. loop in the transition, if present, to find the current start and end floats, as well as how far into it we should be.
	//4. Use this value to lerp between start and end
	//5. Set the attribute we were checking to this new interpolated value
	//6. continue for other attributes
	//

	public void setPercentageIntoAnimation(double percentageIntoAnimation){
	//	Log.info("__________SetPercentageIntoAnimation:"+percentageIntoAnimation);


		for (StyleParam styleParamType : StyleParam.values()) {
			ArrayList<stylestate> states = allTransitionStates.get(styleParamType);

			if (states != null){

				//	Color startColor = null;
				//	Color endColor = null;
				//	float currentSegmentStartTime = 0.0f;
				//	float currentSegmentEndTime = 0.0f;

				stylestate startState = null;
				stylestate endState = null;



				//start will be the first stylestate who's time position is more then our current percentage
				//end will be the one after that.
				for (stylestate stylestate : states) {

					if (percentageIntoAnimation>=stylestate.time) {				
						//	startColor=stylestate.value;
						//	currentSegmentStartTime = stylestate.time;
						startState = stylestate;
						continue;
					} 

					if (startState != null && percentageIntoAnimation<=stylestate.time) {
						//if start is already set we know we are looking for the end
						//	endColor = stylestate.value;
						//	currentSegmentEndTime = stylestate.time;
						endState = stylestate;
						break;
					} 

				}

				//if end state wasn't specified, it loops to the start
				float segmentDuration =0.0f;
				if (endState==null){
					endState = states.get(0);
					//total segment duration (as percentage range)
					 segmentDuration = 1.0f - startState.time;

				} else {
					//total segment duration (as percentage range)
					 segmentDuration = endState.time - startState.time;

				}
				

				//how far into it? (0.0 to 1.0)
				float intoSegment = (float) ((percentageIntoAnimation-startState.time)/segmentDuration);

				//Log.info("In the middle of a "+styleParamType.toString()+" transition going from:"+startColor+" to "+endColor);
				//Log.info("Duration of this transition segment is:"+segmentDuration+"    ("+currentSegmentStartTime+">>>>"+currentSegmentEndTime+")");
				//Log.info("we are at:"+intoSegment);

				//no we have the magic "intoSegment" number we can use this to lurp between the start and end
				switch (styleParamType) {
				case backcolor:
				{

					Color newcolor = startState.colorValue.cpy().lerp(endState.colorValue, intoSegment);						
					backColor.set(newcolor);						
					break;
				}
				case color:
				{
					Color newcolor = startState.colorValue.cpy().lerp(endState.colorValue, intoSegment);						
					textColour.set(newcolor);						
					break;			
				}
				case glowSize:
				{
					float newsize = MathUtils.lerp(startState.floatValue, endState.floatValue, intoSegment);
					
					glowSize = newsize;
					break;
				}
				case glowColour:
				{
					Color newcolor = startState.colorValue.cpy().lerp(endState.colorValue, intoSegment);						
					glowColour.set(newcolor);						
					break;
				}
				case shadowColour:
				{
					Color newcolor = startState.colorValue.cpy().lerp(endState.colorValue, intoSegment);						
					shadowColour.set(newcolor);						
					break;
				}
				case shadowXDisplacement:
					shadowXDisplacement = MathUtils.lerp(startState.floatValue, endState.floatValue, intoSegment);					
					break;
				case shadowYDisplacement:
					shadowYDisplacement = MathUtils.lerp(startState.floatValue, endState.floatValue, intoSegment);
					
					break;
				case borderColor:
					Color newcolor = startState.colorValue.cpy().lerp(endState.colorValue, intoSegment);						
					 borderColour.set(newcolor);						
				
					
					break;
				default:
					break;
				}


			}


		}


	}


	public boolean hasText() {
		if (distanceFieldTextureMap!=null){
			return true;
		}
		return false;
	}


	public boolean hasFilters() {
		return usesPostFilter;
	}




}