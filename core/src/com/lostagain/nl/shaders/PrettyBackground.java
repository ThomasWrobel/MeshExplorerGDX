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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.me.domain.MEDomain;
import com.lostagain.nl.shaders.InvertShader.InvertAttribute;
import com.lostagain.nl.shaders.MyShaderProvider.shadertypes;
import com.lostagain.nl.shaders.NoiseShader.NoiseShaderAttribute;
import com.lostagain.nl.shaders.PrettyNoiseShader.PrettyNoiseShaderAttribute;

/**
 * creates a PrettyBackground shader - currently using one from shadertoy (will replace)
 * It only works in the background behind everyone else.
 * 
 * @author Tom
 *
 */
public class PrettyBackground implements Shader {
	 ShaderProgram program;
	 Camera camera;
	 RenderContext context;
	 
	   int u_projViewTrans;
	   int u_worldTrans;
	    int u_time;
	    int u_resolution;
	    int u_mixcolour;
	    
	   private float time;
	   
	   
		public static class PrettyBackgroundAttribute extends Attribute {
			public final static String Alias = "PrettyBackgroundAttribute";
			public final static long ID = register(Alias);
			
			/**
			 * The presence of this parameter will cause the PrettyBackground to be used
			 * 
			 * @param rgbmode - if the noise is the full color
			 * @param tintcolor - color of the tint
			 */
			public PrettyBackgroundAttribute () {
				
				super(ID);
				
			}

			@Override
			public Attribute copy () {
				return new PrettyBackgroundAttribute();
			}

			@Override
			protected boolean equals (Attribute other) {
				if (
					(true) //no checks because no params yet for this shader
					)
				
				{
					return true;
					
				}
				return false;
			}

			@Override
			public int compareTo(Attribute o) {
				// TODO Auto-generated method stub
				return 0;
			}
		}	
	   
	   
    @Override
    public void init () {
    	
    	  String vert = Gdx.files.internal("shaders/subtlegrid.vertex.glsl").readString();
          String frag = Gdx.files.internal("shaders/subtlegrid.fragment.glsl").readString();
          
          program = new ShaderProgram(vert, frag);
          if (!program.isCompiled()){
              throw new GdxRuntimeException(program.getLog());
          }
          
          u_projViewTrans = program.getUniformLocation("u_projViewTrans");
          u_worldTrans = program.getUniformLocation("u_worldTrans");
          u_time = program.getUniformLocation("u_time"); 
          u_resolution = program.getUniformLocation("u_resolution"); 
          
          u_mixcolour = program.getUniformLocation("u_mixcolour"); 
          
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
    	  
    	  //the the variable for the cameras projection to be passed to the shader
    	  program.setUniformMatrix(u_projViewTrans, camera.combined);
    	  program.setUniformf(u_time, time);    	  
    	  program.setUniformf(u_resolution, 400,400);
    	  
    	  //get current position on the game field
    	  Vector3 position = MainExplorationView.currentPos;
    	  
    	  
    	  
    	  program.setUniformf(u_mixcolour, MEDomain.getColorForPosition(position));
    	  
    	  
    	  
    	  
    	 // context.setDepthTest(GL20.GL_LEQUAL);   
    	  context.setDepthTest(0);    	//We set to zero so this is behind everything 
    	  
          context.setCullFace(GL20.GL_BACK);
          
    	  
    }
    
    @Override
    public void render (Renderable renderable) {  
    	//set the variable for the objects world transform to be passed to the shader
    	 program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
    	 
    	 renderable.meshPart.render(program);
		 
     	/*	 pre 1.7.1 https://github.com/libgdx/libgdx/pull/3483
    	 renderable.mesh.render(program,
    	            renderable.primitiveType,
    	            renderable.meshPartOffset,
    	            renderable.meshPartSize);*/
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

    	if (instance.material.has(PrettyBackgroundAttribute.ID)){
    		return true;
    	}
    	
    //	Gdx.app.log(logstag, "testing if noiseshader can render:"+shaderenum.toString());
    	return false;
  
    	
    }
    
    
}