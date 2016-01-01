package com.lostagain.nl.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lostagain.nl.shaders.DistanceFieldShaderForDataObjects.DistanceFieldttribute;
import com.lostagain.nl.shaders.MyShaderProvider.shadertypes;
import com.lostagain.nl.shaders.NoiseShader.NoiseShaderAttribute;
import com.lostagain.nl.shaders.PrettyNoiseShader.PrettyNoiseShaderAttribute;

/**
 * Basic normal-colourish shader.
 * good for a template for other shaders
 * 
 * @author Tom
 *
 */
public class templateShader extends DefaultShader {

	 final static String logstag = "ME.InvertShader";
	 
	 public static class  templateAttribute extends Attribute {
			public final static String Alias = "PrettyNoiseShaderAttribute";
			public final static long ID = register(Alias);

			public boolean rgbmode = false;
			public Color tintcolor;
			
			/**
			 * The presence of this parameter will cause the NoiseShader to be used
			 * @param rgbmode - if the noise is the full color
			 * @param tintcolor - color of the tint
			 */
			public  templateAttribute (final boolean rgbmode,final Color tintcolor) {
				
				super(ID);
				this.rgbmode = rgbmode;
				this.tintcolor = tintcolor;
				
			}

			@Override
			public Attribute copy () {
				return new  templateAttribute(rgbmode,tintcolor);
			}

			@Override
			protected boolean equals (Attribute other) {
				if (
					((( templateAttribute)other).rgbmode == rgbmode) &&
					((( templateAttribute)other).tintcolor == tintcolor) 
					)
				
				{
					return true;
					
				}
				return false;
			}
			
			//compare should be implemented to determain the order of rendering within the same type of shader
			//this is done based on a attribute within the shader - it can be any value to test
			@Override
			public int compareTo(Attribute o) {
				
			   //Ensuring attribute we are comparing too is the same type, if not we truth
			   if (type != o.type) return type < o.type ? -1 : 1; //if not the same type and less then we return -1 else we return 1
				 			 
			   //if they are the same type we continue	
			   //double otherValue= ((templateAttribute)o).value; //any value here
			        
			   // return smoothing == otherSmooth ? 0 : (smoothing < otherSmooth ? -1 : 1);
			        return 0;
			}
		}	
	 
	 
	 
	 public templateShader(Renderable renderable) {
		super(renderable);
		// TODO Auto-generated constructor stub
	}

	ShaderProgram program;
	 Camera camera;
	 RenderContext context;
	 
	   int u_projViewTrans;
	    int u_worldTrans;
	    int u_sampler2D;
	  
    @Override
    public void init () {
    	
    	  String vert = Gdx.files.internal("shaders/defaulttest.vertex.glsl").readString();
          String frag = Gdx.files.internal("shaders/defaulttest.fragment.glsl").readString();
          
          program = new ShaderProgram(vert, frag);
          if (!program.isCompiled()){
              throw new GdxRuntimeException(program.getLog());
          }
          
          u_projViewTrans = program.getUniformLocation("u_projViewTrans");
          u_worldTrans = program.getUniformLocation("u_worldTrans");
          u_sampler2D =   program.getUniformLocation("u_diffuseTexture");
    }
    
    //@Override
    public void dispose () {
    	
    	program.dispose();
    	
    }
    
   // @Override
    public void begin (Camera camera, RenderContext context) {  
    	
    	   this.camera = camera;
           this.context = context;
           
    	  program.begin();
    	  //the the variable for the cameras projectino to be passed to the shader
    	  program.setUniformMatrix(u_projViewTrans, camera.combined);
    	  
    	  context.setDepthTest(GL20.GL_LEQUAL);    	  
          context.setCullFace(GL20.GL_BACK);
          
    	  
    }
    
   // @Override
    public void render (Renderable renderable) {  
    	//set the variable for the objects world transform to be passed to the shader
    	 program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
    	 
    	 
    	 //program.setUniformf(TexCoord, value);
    	 
    	 if (renderable.material.get(TextureAttribute.Diffuse)!=null){
    		 
    		 Texture testtexture = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;  
    		 
    		 program.setUniformi(u_sampler2D, context.textureBinder.bind(testtexture));
    		 
    		 
    		 
    	 }// else {
    		// program.setUniformi(u_sampler2D, 0);
    	 //}
    	 
    	 
    	 
    	 
    	 //program.setUniformi(u_sampler2D, 0);
    	 renderable.meshPart.render(program);
		 
     	/*	 pre 1.7.1 https://github.com/libgdx/libgdx/pull/3483
    	 renderable.mesh.render(program,
    	            renderable.primitiveType,
    	            renderable.meshPartOffset,
    	            renderable.meshPartSize);*/
    }
    
   // @Override
    public void end () { 
    	
    	 program.end();
    }
    
   // @Override
    public int compareTo (Shader other) {
        return 0;
    }
    
   // @Override
    public boolean canRender (Renderable instance) {
    	
    	if (instance.material.has( templateAttribute.ID)){
    		return true;
    	}
    	
    //	Gdx.app.log(logstag, "testing if noiseshader can render:"+shaderenum.toString());
    	return false;
    	
    }
    
    
}