package com.lostagain.nl.shaders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class GwtishWidgetDistanceFieldAttribute extends Attribute {
	
	public final static String Alias = "GwtishWidgetDistanceFieldAttribute";
	public final static long ID = register(Alias);

	/**
	 * If clear is specified for the text colour,outline colour, glow and shader colour, then the text rendering
	 * is disabled completely. This should be used if you just wish to use the background shader on GWTish widgets
	 */
	public Color textColour          = Color.WHITE;
	public float width               = 0; //cant yet work out how best to make this work in the shader
	
	public float outlinerInnerLimit  = 10f; //Arbitrarily big size for no outline 0.2 is a good default
	public float outlinerOuterLimit  = 10f; //Arbitrarily big size for no outline 0.05 for default v_outlinerOuterLimit
	public Color outlineColour       = Color.CLEAR;
	
	public float glowSize            = 0.0f; //size of glow (values above 1 will look strange)
	public Color glowColour          = new Color(0.0f,1.0f,0.0f,1.0f); 
	
	public float shadowXDisplacement = 1.0f;
	public float shadowYDisplacement = 1.0f;
	public float shadowBlur          = 0.0f;
	public Color shadowColour        = Color.CLEAR;
	
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
	public GwtishWidgetDistanceFieldAttribute (GwtishWidgetDistanceFieldAttribute.presetTextStyle preset) {
		super(ID);
		
		setToPreset(preset);
			
	}


	public void setToPreset(GwtishWidgetDistanceFieldAttribute.presetTextStyle preset) {
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
			
	
	public GwtishWidgetDistanceFieldAttribute(
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
			float shadowBlur 
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
		this.shadowColour = shadowColour.cpy();;
	}
	/**
	 * The presence of this parameter will cause the DistanceFieldAttribute to be used
	 * @param textColour
	 * @param width - no effect cant work out how to do this correctly in the shader file
	 */
	public GwtishWidgetDistanceFieldAttribute (final Color textColour,final float width) {
		
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
	public GwtishWidgetDistanceFieldAttribute(long type, Color textColour,
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
		
		return new GwtishWidgetDistanceFieldAttribute(width,
				 textColour,
				 outlinerInnerLimit,
				 outlinerOuterLimit, 
				 outlineColour,
				 glowSize, 
				 glowColour,
				 shadowColour,
				 shadowXDisplacement, 
				 shadowYDisplacement,
				 shadowBlur );
		
	}

	
	
	@Override
	protected boolean equals (Attribute other) {
		if (
			(((GwtishWidgetDistanceFieldAttribute)other).width == width) &&
			(((GwtishWidgetDistanceFieldAttribute)other).textColour == textColour)  &&
			(((GwtishWidgetDistanceFieldAttribute)other).outlinerInnerLimit == outlinerInnerLimit) &&
			(((GwtishWidgetDistanceFieldAttribute)other).outlinerOuterLimit == outlinerOuterLimit)  &&
			(((GwtishWidgetDistanceFieldAttribute)other).outlineColour == outlineColour) &&
			(((GwtishWidgetDistanceFieldAttribute)other).glowSize == glowSize)  &&
			(((GwtishWidgetDistanceFieldAttribute)other).glowColour == glowColour) &&
			(((GwtishWidgetDistanceFieldAttribute)other).shadowColour == shadowColour)  &&
			(((GwtishWidgetDistanceFieldAttribute)other).shadowXDisplacement == shadowXDisplacement) &&
			(((GwtishWidgetDistanceFieldAttribute)other).shadowYDisplacement == shadowYDisplacement)  &&
			(((GwtishWidgetDistanceFieldAttribute)other).shadowBlur == shadowBlur) 
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
	   double otherwidth = ((GwtishWidgetDistanceFieldAttribute)o).width; //just picking width here arbitarily. Theres no real reason for these to be rendered in a different order relative to eachother
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
	
	
	
}