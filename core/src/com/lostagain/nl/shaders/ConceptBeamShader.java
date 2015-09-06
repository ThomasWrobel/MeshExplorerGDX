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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lostagain.nl.shaders.MyShaderProvider.shadertypes;

/**
 * This shader handles the beam that projects from the players concept gun
 * 
 * @author Tom
 *
 */
public class ConceptBeamShader implements Shader {
	 ShaderProgram program;
	 Camera camera;
	 RenderContext context;
	 
	 final static String logstag = "ME.ConceptBeamShader";
	 
	   int u_projViewTrans;
	   int u_worldTrans;
	    int u_time;
	    int u_width;
	    int u_beamcolour;
	    int u_corecolour;
	    int u_shotFrequency;
	    
	   private float time;
	   
	   
	   ///------------------
		// Create a custom attribute, see https://github.com/libgdx/libgdx/wiki/Material-and-environment
		// See also: http://blog.xoppa.com/using-materials-with-libgdx/
	   /**
		 * The presence of this parameter will cause the ConceptBeamShader to be used
		 * */
		public static class ConceptBeamAttribute extends Attribute {
			public final static String Alias = "ConceptBeamAttribute";
			public final static long ID = register(Alias);

			public float width;
			public Color beamcolor;
			public Color corecolor;
			public float shotFrequency;
			
			/**
			 * The presence of this parameter will cause the ConceptBeamShader to be used
			 * @param width - width of beam
			 * @param beamcolor - its color
			 * @param corecolor - color of its core (normally white for a intense glow at the middle of the beam)
			 */
			public ConceptBeamAttribute (final float width,final Color beamcolor,final float shotFrequency, final Color corecolor ) {
				
				super(ID);
				this.width = width;
				this.beamcolor = beamcolor;
				this.corecolor = corecolor;
				this.shotFrequency = shotFrequency;
				
			}

			@Override
			public Attribute copy () {
				return new ConceptBeamAttribute(width,beamcolor,shotFrequency,corecolor);
			}

			@Override
			protected boolean equals (Attribute other) {
				if (
					(((ConceptBeamAttribute)other).width == width) &&
					(((ConceptBeamAttribute)other).beamcolor == beamcolor) &&
					(((ConceptBeamAttribute)other).shotFrequency == shotFrequency) &&
					(((ConceptBeamAttribute)other).corecolor == corecolor) 
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
			   double otherwidth = ((ConceptBeamAttribute)o).width; //just picking width here arbitarily. Theres no real reason for conceptbeams to be rendered in a different order relative to eachother
			   //so the order can be pretty arbitarily - theres only going to be one beam on the screen at a time anyway.
			   
			        
			    return width == otherwidth ? 0 : (width < otherwidth ? -1 : 1);
			        
			}
		}	
	   
	   
    @Override
    public void init () {
    	
    	Gdx.app.log(logstag, "init concept beam");
    	
    	
    	  String vert = Gdx.files.internal("shaders/conceptbeamvert.glsl").readString();
          String frag = Gdx.files.internal("shaders/conceptbeamfrag.glsl").readString();
          
          program = new ShaderProgram(vert, frag);
          if (!program.isCompiled()){
              throw new GdxRuntimeException(program.getLog());
          }
          
          u_projViewTrans = program.getUniformLocation("u_projViewTrans");
          u_worldTrans    = program.getUniformLocation("u_worldTrans");
          u_time          = program.getUniformLocation("u_time"); 
          
          //beam style
          u_width = program.getUniformLocation("u_width"); 
          u_corecolour = program.getUniformLocation("u_corecolour"); 
          u_beamcolour = program.getUniformLocation("u_beamcolour"); 
          u_shotFrequency = program.getUniformLocation("u_shotFrequency"); 
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
    	  
    	//  glEnable(GL_BLEND);
    	 // glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    	 
    	 // context.setBlending(true,GL20.GL_SRC_ALPHA ,GL20.GL_ONE_MINUS_SRC_ALPHA);
    	  
    	  
    	  
    	  //lower for additive
    	  context.setBlending(true,GL20.GL_ONE ,GL20.GL_ONE);    	  
    	  context.setDepthTest(GL20.GL_DEPTH_TEST);    	
    	  context.setDepthTest(GL20.GL_DEPTH_BUFFER_BIT);    	    	  
         // context.setCullFace(GL20.GL_BACK);// (no culling for now, beam visible both sides)
    	 context.setCullFace(GL20.GL_NONE);// (no culling for now, beam visible both sides)
          
    	  
    }
    
    @Override
    public void render (Renderable renderable) {  
    	//set the variable for the objects world transform to be passed to the shader

    	 program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
    	 
    	 
    	 ConceptBeamAttribute beamStyle = (ConceptBeamAttribute)renderable.material.get(ConceptBeamAttribute.ID);
    	 
    	 program.setUniformf(u_width, beamStyle.width);//testAttr.width    	 
    	 program.setUniformf(u_beamcolour, beamStyle.beamcolor);//testAttr.width
    	 program.setUniformf(u_corecolour, beamStyle.corecolor);//testAttr.width
      	 program.setUniformf(u_shotFrequency, beamStyle.shotFrequency);
      	  
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

    	if (instance.material.has(ConceptBeamAttribute.ID)){
    		return true;
    	}

    	return false;
    }
}