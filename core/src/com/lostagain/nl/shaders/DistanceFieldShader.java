package com.lostagain.nl.shaders;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
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
 * Basic normal-colourish shader.
 * good for a template for other shaders
 * 
 * @author Tom
 *
 */
public class DistanceFieldShader implements Shader {
	ShaderProgram program;
	
	 

	Camera camera;
	 RenderContext context;
	 
	 final static String logstag = "ME.DistanceFieldShader";
	 
	   int u_projViewTrans;
	    int u_worldTrans;
	    int u_sampler2D; 
	    
	    int a_colorFlag;
	    int u_textColour;
	    int u_backColour;
	    
	    int u_pixel_step;
	    
	    //glow
	    int  u_glowColor;
	    int  u_glowSize  ; //size of glow (values above 1 will look strange)
        		
        //outline
	    int  u_outColor ;
	    int  u_outlinerInnerLimit; //Arbitrarily big size for no outline
	    int  u_outlinerOuterLimit; //Arbitrarily big size for no outline

        //shadow
	    int  u_shadowXDisplacement;
	    int  u_shadowYDisplacement;
	    int  u_shadowBlur;
	    int  u_shadowColour;
       
		public static class DistanceFieldAttribute extends Attribute {
			
			public final static String Alias = "DistanceFieldAttribute";
			public final static long ID = register(Alias);

			public Color textColour          = Color.CLEAR;
			public float width               = 0; //cant yet work out how best to make this work in the shader
			
			public float outlinerInnerLimit  = 10f; //Arbitrarily big size for no outline
			public float outlinerOuterLimit  = 10f; //Arbitrarily big size for no outline
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
			public DistanceFieldAttribute (presetTextStyle preset) {
				super(ID);
				
				setToPreset(preset);
					
			}


			public void setToPreset(presetTextStyle preset) {
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
					
			
			public DistanceFieldAttribute(
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
			public DistanceFieldAttribute (final Color textColour,final float width) {
				
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
			public DistanceFieldAttribute(long type, Color textColour,
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
				
				return new DistanceFieldAttribute(width,
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
					(((DistanceFieldAttribute)other).width == width) &&
					(((DistanceFieldAttribute)other).textColour == textColour)  &&
					(((DistanceFieldAttribute)other).outlinerInnerLimit == outlinerInnerLimit) &&
					(((DistanceFieldAttribute)other).outlinerOuterLimit == outlinerOuterLimit)  &&
					(((DistanceFieldAttribute)other).outlineColour == outlineColour) &&
					(((DistanceFieldAttribute)other).glowSize == glowSize)  &&
					(((DistanceFieldAttribute)other).glowColour == glowColour) &&
					(((DistanceFieldAttribute)other).shadowColour == shadowColour)  &&
					(((DistanceFieldAttribute)other).shadowXDisplacement == shadowXDisplacement) &&
					(((DistanceFieldAttribute)other).shadowYDisplacement == shadowYDisplacement)  &&
					(((DistanceFieldAttribute)other).shadowBlur == shadowBlur) 
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
			   double otherwidth = ((DistanceFieldAttribute)o).width; //just picking width here arbitarily. Theres no real reason for these to be rendered in a different order relative to eachother
			   //so the order can be pretty arbitarily.
			        
			    return width == otherwidth ? 0 : (width < otherwidth ? -1 : 1);
			        
			}


			public float getOverall_Opacity_Multiplier() {
				return Overall_Opacity_Multiplier;
			}


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
	    
    @Override
    public void init () {
    	
    	Gdx.app.log(logstag, "initialising distance field shader");
    	
    	  String vert = Gdx.files.internal("shaders/distancefieldvert.glsl").readString();
          String frag = Gdx.files.internal("shaders/distancefieldfrag.glsl").readString();
          
          //String prefix = createPrefix(renderable, this.get);
          
          program = new ShaderProgram(vert, frag);
          
          if (!program.isCompiled()){
              throw new GdxRuntimeException(program.getLog());
          }

          
      	  Gdx.app.log(logstag, "setting distance field shaders uniform location");
      	
          u_projViewTrans = program.getUniformLocation("u_projViewTrans");
          u_worldTrans = program.getUniformLocation("u_worldTrans");
          u_sampler2D =   program.getUniformLocation("u_texture");
          
          a_colorFlag =  program.getUniformLocation("u_colorFlag");
         
          u_pixel_step =  program.getUniformLocation("u_pixel_step");
          
          //text and back color
          u_textColour =  program.getUniformLocation("u_textColor");
          u_backColour =  program.getUniformLocation("u_backColor");
          
          //glow
        u_glowColor = program.getUniformLocation("u_glowColor");
        u_glowSize  = program.getUniformLocation("u_glowSize"); //size of glow (values above 1 will look strange)
        		
        	//outline
        u_outColor           = program.getUniformLocation("u_outColor");
        u_outlinerInnerLimit = program.getUniformLocation("u_outlinerInnerLimit"); //Arbitrarily big size for no outline
        u_outlinerOuterLimit = program.getUniformLocation("u_outlinerOuterLimit"); //Arbitrarily big size for no outline

        //shadow
       u_shadowXDisplacement = program.getUniformLocation("u_shadowXDisplacement");
       u_shadowYDisplacement = program.getUniformLocation("u_shadowYDisplacement");
       u_shadowBlur          = program.getUniformLocation("u_shadowBlur");
       u_shadowColour        = program.getUniformLocation("u_shadowColour");
          
          
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
    	 
    	//Gdx.app.log(logstag, "element size w= "+w+",h="+h);

    	 DistanceFieldAttribute textStyleData = (DistanceFieldAttribute)renderable.material.get(DistanceFieldAttribute.ID);
    	 

		//	Gdx.app.log(logstag, "glowColour:"+textStyleData.glowColour);
		setSizeUniform(w,h);
    	 
    	 
    	 if (renderable.material.get(TextureAttribute.Diffuse)!=null){
    		 
    		 Texture distanceFieldTextureMap = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;      		 
    		 program.setUniformi(u_sampler2D, context.textureBinder.bind(distanceFieldTextureMap));    		    		 
    		 
    	 }
    	 
    	 //back color comes from diffuse
		 Color backcolor = ((ColorAttribute)renderable.material.get(ColorAttribute.Diffuse)).color.cpy();
		 
		 //and we multiply it by the opacity
		 BlendingAttribute backgroundOpacity = ((BlendingAttribute)renderable.material.get(BlendingAttribute.Type));
		 if (backgroundOpacity!=null){
			 backcolor.a = backcolor.a*backgroundOpacity.opacity; //Temp. Really Blending should effect everything, not just the background
		 }
		 
    	// Color textColour = Color.ORANGE;
    	// if (renderable.material.has(ColorAttribute.Diffuse)){	    		     		
    		//text from attribute
		 Color textColour = textStyleData.getTextColour();   	
		
    		 program.setUniformf(a_colorFlag,1);
    		 
    	// } else {
    		 //if not we assume default text color
    	//	 program.setUniformf(a_colorFlag,0);    	 //this would give colour based on texture	 
    		 
    	/// }
    	 
    	 
    	     //text ans back color
		 program.setUniformf(u_backColour, backcolor); 
		 program.setUniformf(u_textColour, textColour);  
		 
		     //glow
		 program.setUniformf(u_glowColor,textStyleData.getGlowColour());
		 program.setUniformf(u_glowSize ,textStyleData.glowSize); //size of glow (values above 1 will look strange)
	        		
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
		 
		 
    	 renderable.mesh.render(program,
    	            renderable.primitiveType,
    	            renderable.meshPartOffset,
    	            renderable.meshPartSize);
    }
    
     public void setSizeUniform(float w, float h) {
    	
    	 program.setUniformf(u_pixel_step,(1/w), (1/h));
		
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

    	if (instance.material.has(DistanceFieldAttribute.ID)){
    		return true;
    	}
    	
    //	Gdx.app.log(logstag, "testing if noiseshader can render:"+shaderenum.toString());
    	return false;
  
    	
    }

	public static DistanceFieldShader Default = new DistanceFieldShader();
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