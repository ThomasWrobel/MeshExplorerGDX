package com.lostagain.nl.shaders;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lostagain.nl.shaders.GwtishWidgetDistanceFieldAttribute.TextScalingMode;

/**
 * The goal of this shader is to combine the features of the distancefieldshader with the glowingrectangle background shader
 * as such it has two sets of attributes, one for each.
 * 
 * @author Tom
 *
 */
public class GwtishWidgetShader implements Shader {
	ShaderProgram program;



	Camera camera;
	RenderContext context;

	final static String logstag = "ME.GwtishWidgetShader";


	//-----------------------------------------------
	//This shader heavily relies on two attribute types
	//
	//GwtishWidgetDistanceFieldAttribute.java  - handles the distance field aspects of the shader (this would typically be how the text is rendered. Its colour, glow,shadow etc)
	//GwtishWidgetBackgroundAttribute.java     - handles the background. WIP solution that lets you have a curved cornered rectangle as the widgets background
	//
	//These are in their own files purely for neatness
	//
	//-----------------------------------------------
	//-------------------------------------
	//------------------------
	//-------------
	//----

	int u_projViewTrans;
	int u_worldTrans;
	int u_sampler2D; 

	int a_colorFlag;
	int u_textColour;
	//int u_backColour;

	int u_texture_pixel_step;
	int u_resolution;
	int u_sizeDiff;

	//padding
	int u_textPaddingX;
	int u_textPaddingY;

	//glow
	int  u_textGlowColor;
	int  u_textGlowSize  ; //size of glow (values above 1 will look strange)

	//outline
	int  u_outColor ;
	int  u_outlinerInnerLimit; //Arbitrarily big size for no outline
	int  u_outlinerOuterLimit; //Arbitrarily big size for no outline

	//shadow
	int  u_shadowXDisplacement;
	int  u_shadowYDisplacement;
	int  u_shadowBlur;
	int  u_shadowColour;

	//background
	int u_backBorderWidth;
	int u_backBackColor;
	int u_backCoreColor; 		
	int u_backGlowColor;
	int u_backCornerRadius;



	@Override
	public void init () {

		Gdx.app.log(logstag, "initialising gwtish widget shader");

		String vert = Gdx.files.internal("shaders/gwtishwidgetshader_vert.glsl").readString();
		String frag = Gdx.files.internal("shaders/gwtishwidgetshader_frag.glsl").readString();

		//String prefix = createPrefix(renderable, this.get);

		program = new ShaderProgram(vert, frag);

		if (!program.isCompiled()){
			throw new GdxRuntimeException(program.getLog());
		}


		Gdx.app.log(logstag, "setting GwtishWidgetShader uniform locations");
		Gdx.app.log(logstag, "(first the distance field ones....");

		u_projViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans = program.getUniformLocation("u_worldTrans");
		u_sampler2D =   program.getUniformLocation("u_texture");

		a_colorFlag =  program.getUniformLocation("u_colorFlag");

		u_texture_pixel_step =  program.getUniformLocation("u_pixel_step");
		u_resolution  =  program.getUniformLocation("u_resolution");
		u_sizeDiff=  program.getUniformLocation("u_sizeDiff");
		//text and back color
		u_textColour =  program.getUniformLocation("u_textColor");
		//u_backColour =  program.getUniformLocation("u_backColor");

		//padding
		u_textPaddingX = program.getUniformLocation("u_textPaddingX");
		u_textPaddingY = program.getUniformLocation("u_textPaddingY");


		//glow
		u_textGlowColor = program.getUniformLocation("u_glowColor");
		u_textGlowSize  = program.getUniformLocation("u_glowSize"); //size of glow (values above 1 will look strange)

		//outline
		u_outColor           = program.getUniformLocation("u_outColor");
		u_outlinerInnerLimit = program.getUniformLocation("u_outlinerInnerLimit"); //Arbitrarily big size for no outline
		u_outlinerOuterLimit = program.getUniformLocation("u_outlinerOuterLimit"); //Arbitrarily big size for no outline

		//shadow
		u_shadowXDisplacement = program.getUniformLocation("u_shadowXDisplacement");
		u_shadowYDisplacement = program.getUniformLocation("u_shadowYDisplacement");
		u_shadowBlur          = program.getUniformLocation("u_shadowBlur");
		u_shadowColour        = program.getUniformLocation("u_shadowColour");

		//background
		//square style

		Gdx.app.log(logstag, "(now the background ones....");
		u_backBorderWidth     = program.getUniformLocation("u_backBorderWidth"); 
		u_backBackColor     = program.getUniformLocation("u_backBackColor"); 
		u_backCoreColor     = program.getUniformLocation("u_backCoreColor");  		
		u_backGlowColor     = program.getUniformLocation("u_backGlowColor"); 
		u_backCornerRadius  = program.getUniformLocation("u_backCornerRadius"); 


		Gdx.app.log(logstag, "....)");


	}

	@Override
	public void dispose () {

		program.dispose();

	}

	@Override
	public void begin (Camera camera, RenderContext context) {  

		this.camera = camera;
		this.context = context;

		program.begin();
		
		//the the variable for the cameras projectino to be passed to the shader
		program.setUniformMatrix(u_projViewTrans, camera.combined);

		// context.setDepthTest(GL20.GL_LEQUAL);    	  
		//context.setCullFace(GL20.GL_BACK);
		
		//Standard blending;
		context.setBlending(true,GL20.GL_SRC_ALPHA ,GL20.GL_ONE_MINUS_SRC_ALPHA);
		context.setDepthTest(GL20.GL_LESS);  
		context.setDepthTest(GL20.GL_NONE); //NEW: Completely disable depth testing as Jamgames have lots of things at the same position
		//instead we manuallt sort with zindex attributes
		//TODO: have a setting for turning this GL20.GL_NONE on/off?
		
		
		//http://stackoverflow.com/questions/32487074/libgdx-eliminate-transparency-artifacts-when-using-cameragroupstrategy-with-dec
		//Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		//-------------
		//(currently we need a way to optionally use GL_NONE for overlays
		
		
		//	context.setDepthTest(GL20.GL_NONE);    	
		//	context.setDepthTest(GL20.GL_GREATER); 
	}

	@Override	
	public void render (Renderable renderable) {  

		//set the variable for the objects world transform to be passed to the shader
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);

		//float w = renderable.mesh.calculateBoundingBox().getWidth();
		//float h = renderable.mesh.calculateBoundingBox().getHeight();

		float w = renderable.meshPart.mesh.calculateBoundingBox().getWidth();
		float h = renderable.meshPart.mesh.calculateBoundingBox().getHeight();
		
		//GWTish widgets are controlled by two style attributes
		//A distance field shader for text styling
		GwtishWidgetDistanceFieldAttribute textStyleData = (GwtishWidgetDistanceFieldAttribute)renderable.material.get(GwtishWidgetDistanceFieldAttribute.ID);
		//And a background shader which lets us have curved corners in the background
		GwtishWidgetBackgroundAttribute backgroundParameters = (GwtishWidgetBackgroundAttribute)renderable.material.get(GwtishWidgetBackgroundAttribute.ID);
		//(one of these attributes may be null, but not both)



		//if textStyleData is null, we assume the null dataset for it
		if (textStyleData==null){
			textStyleData = new GwtishWidgetDistanceFieldAttribute(GwtishWidgetDistanceFieldAttribute.presetTextStyle.NULL_DONTRENDERTEXT);
			//note; 
			//this is currently not very efficient - we should have a flag system for "no text" rather then needing COLOR 0,0,0,0 to be set on all color settings  
			
			//distance field attribute shader should also have a texture to go with it
			
		}

		//sometimes extra padding has to be added when scaling
		//this is because we centralize by default when the scaling has resulted in extra space either vertically or horizontally.
		//maybe in future we have other options?
		float textScale_height_pad = 0;
		float textScale_width_pad  = 0;	
		
		if (renderable.material.get(TextureAttribute.Diffuse)!=null){

			Texture distanceFieldTextureMap = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;      		 
			distanceFieldTextureMap.setFilter(TextureFilter.Linear, TextureFilter.Linear); //not needed
			program.setUniformi(u_sampler2D, context.textureBinder.bind(distanceFieldTextureMap));    		    		 
	
			//Now we need to supply the pixel step of the texture as this is different to the overall one
			float tw = distanceFieldTextureMap.getWidth(); //add padding here?
			float th = distanceFieldTextureMap.getHeight();

			
			//depending on sizemode though, we might still want to use the models size - thus stretching the texture		
			if (textStyleData.textScaleing.equals(TextScalingMode.fitarea)){
				
				
				 tw = w;
				 th = h;				 
				//	Gdx.app.log(logstag, "fitarea detected in shader. size set as:"+tw+","+th); 
					
			} else if (textStyleData.textScaleing.equals(TextScalingMode.fitPreserveRatio)){
							
				//padding totals can only, at most, be the size of the widget
				//if they exceed it, we should take midpoint between them
				/*
				float totalPaddingWidth =  (textStyleData.paddingLeft*2f);
				if (totalPaddingWidth>w){
					totalPaddingWidth=w;
				}
				float totalPaddingHeight =  (textStyleData.paddingTop*2f);
				if (totalPaddingHeight>h){
					totalPaddingHeight=h;
				}
				*/
			//	tw = tw + (totalPaddingWidth); //should also be right
			//	th = th + (totalPaddingHeight); // should also be +bottom
				
				//float scale = Math.min((w-totalPaddingWidth)/tw, (h-totalPaddingHeight)/th); //amount texture should be scaled down by
				
				
				//temp experiment;
				float scale = textStyleData.textScale;

				//scale = scale/4;				
				tw = scale*tw;
				th = scale*th;				
				
				//autopad the smaller dimension to centralize
				//this might not be correct, see commented out one that will require padding information on all 4 sides
				if (th<(h)){
					//pad height
					textScale_height_pad = (((h/2)-textStyleData.paddingTop)-(th/2));					
					
				}
				if (tw<(w)){
					//pad width
					textScale_width_pad = (((w/2)-textStyleData.paddingLeft)-(tw/2));					
				}
				
				
				//Perhaps not do this at all if there's uneven padding?
				/*
				if (th<(h-totalPaddingHeight)){
					//pad height
					textScale_height_pad = ((h-totalPaddingHeight)-th)/2;					
					
				}
				if (tw<(w-totalPaddingWidth)){
					//pad width
					textScale_width_pad = ((w-totalPaddingWidth)-tw)/2;					
				}
				*/
				
			}
			
			
			program.setUniformf(u_texture_pixel_step,(1/tw), (1/th));

			//we also supply the ratio between the image and the overall model size
			//this lets us have a texture at a arbitrary position within the models shader
			float sizeDiffX = w / tw;
			float sizeDiffY = h / th;

			program.setUniformf(u_sizeDiff,sizeDiffX, sizeDiffY);
		}
		
		
		//	Gdx.app.log(logstag, "glowColour:"+textStyleData.glowColour);
		setSizeUniform(w,h);//,textStyleData.paddingLeft,textStyleData.paddingTop


		//Back color used to come from diffuse (this is being removed)
		//ColorAttribute ColAttribute = ((ColorAttribute)renderable.material.get(ColorAttribute.Diffuse));
		//Color backcolor = Color.CLEAR; //default clear back color
		//if (ColAttribute!=null){
		//	backcolor = ColAttribute.color.cpy();
		//}
		//--------------------

		//and we multiply it by the opacity
		BlendingAttribute backgroundOpacity = ((BlendingAttribute)renderable.material.get(BlendingAttribute.Type));
		if (backgroundOpacity!=null){

			//	backcolor.a = backcolor.a*backgroundOpacity.opacity;                    // Temp.Really Blending should effect everything, not just the background

			if (textStyleData!=null){
				textStyleData.setOverall_Opacity_Multiplier(backgroundOpacity.opacity);
			}
			if (backgroundParameters!=null){
				backgroundParameters.setOverall_Opacity_Multiplier(backgroundOpacity.opacity);
			}

		} else {

			if (textStyleData!=null){
				textStyleData.setOverall_Opacity_Multiplier(1f);
			}

			if (backgroundParameters!=null){
				backgroundParameters.setOverall_Opacity_Multiplier(1f);
			}

		}



		// Color textColour = Color.ORANGE;
		// if (renderable.material.has(ColorAttribute.Diffuse)){	    		     		
		//text from attribute
		Color textColour = textStyleData.getTextColour();   	

		program.setUniformf(a_colorFlag,1);

		// } else {
		//if not we assume default text color
		//	 program.setUniformf(a_colorFlag,0);    	 //this would give color based on texture	 

		/// }


		//text ans back color
		//program.setUniformf(u_backColour, backcolor); //back color is redundant now we have GwtishWidgetBackgroundAttribute as well
		program.setUniformf(u_textColour, textColour);  

		//displacements
		program.setUniformf(u_textPaddingX, textScale_width_pad+textStyleData.paddingLeft);
		program.setUniformf(u_textPaddingY, textScale_height_pad+textStyleData.paddingTop);

		//glow
		program.setUniformf(u_textGlowColor,textStyleData.getGlowColour());
		program.setUniformf(u_textGlowSize ,textStyleData.glowSize); //size of glow (values above 1 will look strange)

		//	Gdx.app.log(logstag, "glowColour:"+textStyleData.glowColour);

		//outline
		program.setUniformf(u_outColor,textStyleData.getOutlineColour());
		program.setUniformf(u_outlinerInnerLimit,textStyleData.outlinerInnerLimit); 
		program.setUniformf(u_outlinerOuterLimit,textStyleData.outlinerOuterLimit); 

		//shadow
		program.setUniformf(u_shadowXDisplacement,textStyleData.shadowXDisplacement);
		program.setUniformf(u_shadowYDisplacement,textStyleData.shadowYDisplacement);
		program.setUniformf(u_shadowBlur,textStyleData.shadowBlur);
		program.setUniformf(u_shadowColour,textStyleData.getShadowColour());




		if (backgroundParameters==null){
			//(if no background specified its just transparent)	 	
			program.setUniformf(u_backBorderWidth,    0f);  	 
			program.setUniformf(u_backBackColor,    Color.CLEAR);
			program.setUniformf(u_backCoreColor,    Color.CLEAR); 
			program.setUniformf(u_backCornerRadius, 1f); 

		} else {

			program.setUniformf(u_backBorderWidth,    backgroundParameters.borderWidth   );  	 
			program.setUniformf(u_backBackColor,    backgroundParameters.getBackColor()   );
			program.setUniformf(u_backCoreColor,    backgroundParameters.getBorderColour()); 
			program.setUniformf(u_backCornerRadius, backgroundParameters.cornerRadius); 


		}



		 renderable.meshPart.render(program);
		 
	    	/*	 pre 1.7.1 https://github.com/libgdx/libgdx/pull/3483
		renderable.mesh.render(program,
				renderable.primitiveType,
				renderable.meshPartOffset,
				renderable.meshPartSize); */
	}

	public void setSizeUniform(float w, float h) {
		//,float paddingLeft, float paddingTop)
		program.setUniformf(u_resolution, w,h);


		//we also need the difference in size between the total widget size and the size of the text on it
		//This is expressed as a ratio
		//   float sizeDiffX = w / (w-paddingLeft);
		//    float sizeDiffY = h / (h-paddingTop);

		//   program.setUniformf(u_sizeDiff,sizeDiffX, sizeDiffY);


		//NOTE: this is the pixel step of the texture.
		//The widget might be much larger, w/h ONLY be the effective size of the text on the widget,ignoring any padding.
		//for this reason we subtract the padding first

		//	w = w - paddingLeft; //in future right as well;
		//	h = h - paddingTop;  //and bottom

		//program.setUniformf(u_texture_pixel_step,(1/w), (1/h));
	}

	@Override
	public void end () { 

		program.end();
	}

	@Override
	public int compareTo (Shader other) {
		return 0;
	}

	@Override
	public boolean canRender (Renderable instance) {

		if (instance.material.has(GwtishWidgetDistanceFieldAttribute.ID) || instance.material.has(GwtishWidgetBackgroundAttribute.ID)){
			return true;
		}

		return false;


	}

	public static GwtishWidgetShader Default = new GwtishWidgetShader();
	/**
	 * returns the default copy of this shader, compiling it if needed
	 * @return
	 */
	public static ShaderProgram getProgram() {
		if (Default.program==null){
			Default.init();
			int u_diffuseColor =  Default.program.getUniformLocation("u_diffuseColor");      
			int u_colorFlag    =  Default.program.getUniformLocation("u_colorFlag");
			int u_textColour    =  Default.program.getUniformLocation("u_textColour");

			Default.program.setUniformf(u_colorFlag, 1f);
			Default.program.setUniformf(u_diffuseColor, Color.RED);

			Default.program.setUniformf(u_textColour, Color.RED);  

		}
		// TODO Auto-generated method stub
		return Default.program;
	}


}