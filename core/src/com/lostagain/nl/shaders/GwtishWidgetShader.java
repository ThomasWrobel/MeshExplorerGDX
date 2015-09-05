package com.lostagain.nl.shaders;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

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
	int u_backGlowWidth;
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
		
		u_backGlowWidth     = program.getUniformLocation("u_backGlowWidth"); 
		u_backBackColor     = program.getUniformLocation("u_backBackColor"); 
		u_backCoreColor     = program.getUniformLocation("u_backCoreColor");  		
		u_backGlowColor     = program.getUniformLocation("u_backGlowColor"); 
		u_backCornerRadius  = program.getUniformLocation("u_backCornerRadius"); 

		Gdx.app.log(logstag, "(now the background ones....");



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

		context.setBlending(true,GL20.GL_SRC_ALPHA ,GL20.GL_ONE_MINUS_SRC_ALPHA);
		context.setDepthTest(GL20.GL_LESS);    		
		//	context.setDepthTest(GL20.GL_NONE);    	
		//	context.setDepthTest(GL20.GL_GREATER); 
	}

	@Override	
	public void render (Renderable renderable) {  

		//set the variable for the objects world transform to be passed to the shader
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);

		float w = renderable.mesh.calculateBoundingBox().getWidth();
		float h = renderable.mesh.calculateBoundingBox().getHeight();


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
		}
		
		

		//	Gdx.app.log(logstag, "glowColour:"+textStyleData.glowColour);
		setSizeUniform(w,h,textStyleData.paddingLeft,textStyleData.paddingTop);


		if (renderable.material.get(TextureAttribute.Diffuse)!=null){

			Texture distanceFieldTextureMap = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;      		 
			program.setUniformi(u_sampler2D, context.textureBinder.bind(distanceFieldTextureMap));    		    		 

		}

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
		program.setUniformf(u_textPaddingX,textStyleData.paddingLeft);
		program.setUniformf(u_textPaddingY,textStyleData.paddingTop);
		
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
			program.setUniformf(u_backGlowWidth,    0f);  	 
			program.setUniformf(u_backBackColor,    Color.CLEAR);
			program.setUniformf(u_backCoreColor,    Color.CLEAR); 
			program.setUniformf(u_backCornerRadius, 1f); 
			
		} else {
			
			program.setUniformf(u_backGlowWidth,    backgroundParameters.glowWidth   );  	 
			program.setUniformf(u_backBackColor,    backgroundParameters.getBackColor()   );
			program.setUniformf(u_backCoreColor,    backgroundParameters.getBorderColour()); 
			program.setUniformf(u_backCornerRadius, backgroundParameters.cornerRadius); 
		
			
		}

		
		

		renderable.mesh.render(program,
				renderable.primitiveType,
				renderable.meshPartOffset,
				renderable.meshPartSize);
	}

	public void setSizeUniform(float w, float h,float paddingLeft, float paddingTop) {

		program.setUniformf(u_resolution, w,h);


		//we also need the difference in size between the total widget size and the size of the text on it
		//This is expressed as a ratio
	    float sizeDiffX = w / (w-paddingLeft);
	    float sizeDiffY = h / (h-paddingTop);
	    
	    program.setUniformf(u_sizeDiff,sizeDiffX, sizeDiffY);


		//NOTE: this is the pixel step of the texture.
		//The widget might be much larger, w/h ONLY be the effective size of the text on the widget,ignoring any padding.
		//for this reason we subtract the padding first

		w = w - paddingLeft; //in future right as well;
		h = h - paddingTop;  //and bottom
		
		program.setUniformf(u_texture_pixel_step,(1/w), (1/h));
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