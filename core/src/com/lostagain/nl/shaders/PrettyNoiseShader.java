package com.lostagain.nl.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lostagain.nl.shaders.MyShaderProvider.shadertypes;
import com.lostagain.nl.shaders.NoiseShader.NoiseShaderAttribute;

/**
 * Basic normal-colourish shader.
 * good for a template for other shaders
 * 
 * @author Tom
 *
 */
public class PrettyNoiseShader implements Shader {
	 ShaderProgram program;
	 Camera camera;
	 RenderContext context;
	 
	   int u_projViewTrans;
	   int u_worldTrans;
	    int u_time;
	    
	   private float time;
	   
	   
		public static class PrettyNoiseShaderAttribute extends Attribute {
			public final static String Alias = "PrettyNoiseShaderAttribute";
			public final static long ID = register(Alias);

			public boolean rgbmode = false;
			public Color tintcolor;
			
			/**
			 * The presence of this parameter will cause the NoiseShader to be used
			 * @param rgbmode - if the noise is the full color
			 * @param tintcolor - color of the tint
			 */
			public PrettyNoiseShaderAttribute (final boolean rgbmode,final Color tintcolor) {
				
				super(ID);
				this.rgbmode = rgbmode;
				this.tintcolor = tintcolor;
				
			}

			@Override
			public Attribute copy () {
				return new NoiseShaderAttribute(rgbmode,tintcolor);
			}

			@Override
			protected boolean equals (Attribute other) {
				if (
					(((PrettyNoiseShaderAttribute)other).rgbmode == rgbmode) &&
					(((PrettyNoiseShaderAttribute)other).tintcolor == tintcolor) 
					)
				
				{
					return true;
					
				}
				return false;
			}
		}	
		
	   
    @Override
    public void init () {
    	
    	  String vert = Gdx.files.internal("shaders/prettynoise.vertex.glsl").readString();
          String frag = Gdx.files.internal("shaders/prettynoise.fragment.glsl").readString();
          
          program = new ShaderProgram(vert, frag);
          if (!program.isCompiled()){
              throw new GdxRuntimeException(program.getLog());
          }
          
          u_projViewTrans = program.getUniformLocation("u_projViewTrans");
          u_worldTrans = program.getUniformLocation("u_worldTrans");
          u_time = program.getUniformLocation("u_time"); 
    }
    
    @Override
    public void dispose () {
    	
    	program.dispose();
    	
    }
    
    @Override
    public void begin (Camera camera, RenderContext context) {  
    	
    	   this.camera = camera;
           this.context = context;
           //update time
     	  time = time+ Gdx.graphics.getDeltaTime();
     	  
    	  program.begin();
    	  //the the variable for the cameras projectino to be passed to the shader
    	  program.setUniformMatrix(u_projViewTrans, camera.combined);
    	  program.setUniformf(u_time, time);
    	  
    	  
    	  
    	  context.setDepthTest(GL20.GL_LEQUAL);    	  
          context.setCullFace(GL20.GL_BACK);
          
    	  
    }
    
    @Override
    public void render (Renderable renderable) {  
    	//set the variable for the objects world transform to be passed to the shader
    	 program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
    	 
    	 
    	 renderable.mesh.render(program,
    	            renderable.primitiveType,
    	            renderable.meshPartOffset,
    	            renderable.meshPartSize);
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
	shadertypes shaderenum = (shadertypes) instance.userData;
    	
    	if (shaderenum==shadertypes.prettynoise){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    
}