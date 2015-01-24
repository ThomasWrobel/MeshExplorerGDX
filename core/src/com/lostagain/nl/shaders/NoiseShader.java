package com.lostagain.nl.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.shaders.MyShaderProvider.shadertypes;

/**
 * Basic normal-colourish shader.
 * good for a template for other shaders
 * 
 * @author Tom
 *
 */
public class NoiseShader implements Shader {
	
	final static String logstag = "ME.NoiseShader";
	
	
	
	 ShaderProgram program;
	 Camera camera;
	 RenderContext context;
	 
	  int u_projViewTrans;
	  int u_worldTrans;
	  int u_sampler2D;
	  int resolution;
	  int mouse;
	  int u_time;
	  
    @Override
    public void init () {
    	
    	  String vert = Gdx.files.internal("shaders/noise.vertex.glsl").readString();
          String frag = Gdx.files.internal("shaders/noise.fragment.glsl").readString();
          
          program = new ShaderProgram(vert, frag);
          if (!program.isCompiled()){
              throw new GdxRuntimeException(program.getLog());
          }
          
          u_projViewTrans = program.getUniformLocation("u_projViewTrans");
          u_worldTrans = program.getUniformLocation("u_worldTrans");
          u_sampler2D =   program.getUniformLocation("u_diffuseTexture");
          
          resolution =   program.getUniformLocation("resolution");
          mouse =  program.getUniformLocation("mouse");
          u_time =   program.getUniformLocation("u_time");
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
    	  //the the variable for the cameras projection to be passed to the shader
    	  program.setUniformMatrix(u_projViewTrans, camera.combined);

 		 program.setUniformf(resolution, camera.viewportWidth, camera.viewportHeight);
 		 
 		float ctime = (float) (System.currentTimeMillis()%100000); //gives 0-100000
 		

		
		//Gdx.app.log(logstag,"ctime = "+ctime);
 		
 		
 		 program.setUniformf(u_time,(ctime/1000.0f)); //time range is now 0.001 - 5.000 
 		 
    	  context.setDepthTest(GL20.GL_LEQUAL);    	  
          context.setCullFace(GL20.GL_BACK);
          
    	  
    }
    
    @Override
    public void render (Renderable renderable) {  
    	//set the variable for the objects world transform to be passed to the shader
    	 program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
    	 
    	 
    	 //program.setUniformf(TexCoord, value);
    	 
    	 //if (renderable.material.get(TextureAttribute.Diffuse)!=null){
    		 
    	//	 Texture testtexture = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;      		 
    		// program.setUniformi(u_sampler2D, context.textureBinder.bind(testtexture));
    		 
    		
    		 
    		 program.setUniformi(mouse, 70, 70);
    		 
    		 
    		 
    	// }// else {
    		// program.setUniformi(u_sampler2D, 0);
    	 //}
    	 
    	 
    	 
    	 
    	 //program.setUniformi(u_sampler2D, 0);
    	
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
    	
    	if (shaderenum==shadertypes.noise){
    		return true;
    	} else {
    		return false;
    	}
    	
    }
    
    
}