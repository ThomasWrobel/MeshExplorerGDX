package com.lostagain.nl.shaders;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lostagain.nl.shaders.MyShaderProvider.shadertypes;

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
	    
	    int a_usesDiffuseColor;
	    int u_diffuseColor;
	    
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
          u_sampler2D =   program.getUniformLocation("u_diffuseTexture");
          
        //  a_usesDiffuseColor =  program.getUniformLocation("a_usesDiffuseColor");
          u_diffuseColor =  program.getUniformLocation("u_diffuseColor");
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
    	 
    	 if (renderable.material.get(TextureAttribute.Diffuse)!=null){
    		 
    		 Texture testtexture = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;  
    		 
    		 program.setUniformi(u_sampler2D, context.textureBinder.bind(testtexture));
    		    		 
    		 
    	 }
    	 
    	 if (renderable.material.has(ColorAttribute.Diffuse)){
				
    		// program.setUniformf(a_usesDiffuseColor,1);
    		 program.setUniformf(u_diffuseColor, ((ColorAttribute)renderable.material.get(ColorAttribute.Diffuse)).color);
    	 } else {
    		// program.setUniformf(a_usesDiffuseColor,0);
    		 program.setUniformf(u_diffuseColor, Color.ORANGE);
    	 }
    	 
    	 
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
    	
    	if (shaderenum==shadertypes.distancefield){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    
}