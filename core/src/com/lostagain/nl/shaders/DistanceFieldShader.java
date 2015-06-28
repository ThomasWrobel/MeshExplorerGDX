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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lostagain.nl.shaders.ConceptBeamShader.ConceptBeamAttribute;
import com.lostagain.nl.shaders.MyShaderProvider.shadertypes;
import com.lostagain.nl.shaders.NoiseShader.NoiseShaderAttribute;
import com.lostagain.nl.shaders.PrettyBackground.PrettyBackgroundAttribute;

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
	    
		public static class DistanceFieldAttribute extends Attribute {
			
			public final static String Alias = "DistanceFieldAttribute";
			public final static long ID = register(Alias);

			public Color textColour          = Color.CLEAR;
			public float width               = 0; //cant yet work out how best to make this work in the shader
			
			public float outlinerInnerLimit  = 10f; //Arbitrarily big size for no outline
			public float outlinerOuterLimit  = 10f; //Arbitrarily big size for no outline
			public Color outlineColour       = Color.RED;
			
			public float glowSize            = 0.0f; //size of glow (values above 1 will look strange)
			public Color glowColour          = Color.BLACK;
			
			public float shadowXDisplacement = 1.0f;
			public float shadowYDisplacement = 1.0f;
			public float shadowBlur          = 0.0f;
			public Color shadowColour        = Color.CLEAR;
			
			
			
			
			public DistanceFieldAttribute(long type, Color textColour,
					float width, float outlinerInnerLimit,
					float outlinerOuterLimit, Color outlineColour,
					float glowSize, Color glowColour,
					float shadowXDisplacement, float shadowYDisplacement,
					float shadowBlur, Color shadowColour) {
				super(type);
				this.textColour = textColour;
				this.width = width;
				this.outlinerInnerLimit = outlinerInnerLimit;
				this.outlinerOuterLimit = outlinerOuterLimit;
				this.outlineColour = outlineColour;
				this.glowSize = glowSize;
				this.glowColour = glowColour;
				this.shadowXDisplacement = shadowXDisplacement;
				this.shadowYDisplacement = shadowYDisplacement;
				this.shadowBlur = shadowBlur;
				this.shadowColour = shadowColour;
			}
			/**
			 * The presence of this parameter will cause the DistanceFieldAttribute to be used
			 * @param textColour
			 * @param width - no effect cant work out how to do this correctly in the shader file
			 */
			public DistanceFieldAttribute (final Color textColour,final float width) {
				
				super(ID);
				this.textColour =  textColour;
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
				this.textColour = textColour;
				this.width = width;
				this.outlinerInnerLimit = outlinerInnerLimit;
				this.outlinerOuterLimit = outlinerOuterLimit;
			}

			@Override
			public Attribute copy () {
				return new DistanceFieldAttribute(textColour,width);
			}

			@Override
			protected boolean equals (Attribute other) {
				if (
					(((DistanceFieldAttribute)other).textColour == textColour) &&
					(((DistanceFieldAttribute)other).width == width) 
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
		}	
	    
    @Override
    public void init () {
    	
    	  String vert = Gdx.files.internal("shaders/distancefieldvert.glsl").readString();
          String frag = Gdx.files.internal("shaders/distancefieldfrag.glsl").readString();
          
          //String prefix = createPrefix(renderable, this.get);
          
          program = new ShaderProgram(vert, frag);
          
          if (!program.isCompiled()){
              throw new GdxRuntimeException(program.getLog());
          }
          
          u_projViewTrans = program.getUniformLocation("u_projViewTrans");
          u_worldTrans = program.getUniformLocation("u_worldTrans");
          u_sampler2D =   program.getUniformLocation("u_texture");
          
          a_colorFlag =  program.getUniformLocation("u_colorFlag");
          u_textColour =  program.getUniformLocation("u_textColor");
          u_backColour =  program.getUniformLocation("u_backColor");
          u_pixel_step =  program.getUniformLocation("u_pixel_step");
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
    	  
    	  context.setDepthTest(GL20.GL_LEQUAL);    	  
          context.setCullFace(GL20.GL_BACK);
          
    }
    
    @Override
    public void render (Renderable renderable) {  
    	
    	//set the variable for the objects world transform to be passed to the shader
    	 program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
    	 
    	 float w = renderable.mesh.calculateBoundingBox().getWidth();
    	 float h = renderable.mesh.calculateBoundingBox().getHeight();
    	 
    	//Gdx.app.log(logstag, "element size w= "+w+",h="+h);

    	 DistanceFieldAttribute textStyleData = (DistanceFieldAttribute)renderable.material.get(DistanceFieldAttribute.ID);
    	 
    	
		setSizeUniform(w,h);
    	 
    	 
    	 if (renderable.material.get(TextureAttribute.Diffuse)!=null){
    		 
    		 Texture testtexture = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;      		 
    		 program.setUniformi(u_sampler2D, context.textureBinder.bind(testtexture));    		    		 
    		 
    	 }
    	 
    	 //back color comes from diffuse
		 Color backcolor = ((ColorAttribute)renderable.material.get(ColorAttribute.Diffuse)).color;
    	 Color textColour = Color.ORANGE;
    	 if (renderable.material.has(ColorAttribute.Diffuse)){	
    		     		
    		//text from attribute
    		 textColour = textStyleData.textColour;    		 
    		 	
    		 program.setUniformf(a_colorFlag,1);
    		 
    	 } else {
    		 //if not we assume default text colour
    		 program.setUniformf(a_colorFlag,0);
    		 
    		 
    	 }

		 program.setUniformf(u_backColour, backcolor); 
		 program.setUniformf(u_textColour, textColour);   
		 
		 
		 
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
    
    
}