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
			/**
			 * The presence of this parameter will cause the ConceptBeamShader to be used
			 * @param width - width of beam
			 * @param beamcolor - its color
			 * @param corecolor - color of its core (normally white for a intense glow at the middle of the beam)
			 */
			public ConceptBeamAttribute (final float width,final Color beamcolor,final Color corecolor ) {
				
				super(ID);
				this.width = width;
				this.beamcolor = beamcolor;
				this.corecolor = corecolor;
			}

			@Override
			public Attribute copy () {
				return new ConceptBeamAttribute(width,beamcolor,corecolor);
			}

			@Override
			protected boolean equals (Attribute other) {
				if ((((ConceptBeamAttribute)other).width == width) &&
					(((ConceptBeamAttribute)other).beamcolor == beamcolor) &&
					(((ConceptBeamAttribute)other).corecolor == corecolor) 
					){
					return true;
					
				}
				return false;
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
          u_worldTrans = program.getUniformLocation("u_worldTrans");
          u_time = program.getUniformLocation("u_time"); 
          
          //beam style
          u_width = program.getUniformLocation("u_width"); 
          u_corecolour = program.getUniformLocation("u_corecolour"); 
          u_beamcolour = program.getUniformLocation("u_beamcolour"); 
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
    	 
    	 
    	 ConceptBeamAttribute beamStyle = (ConceptBeamAttribute)renderable.material.get(ConceptBeamAttribute.ID);
    	 program.setUniformf(u_width, beamStyle.width);//testAttr.width
    	 program.setUniformf(u_beamcolour, beamStyle.beamcolor);//testAttr.width
    	 program.setUniformf(u_corecolour, beamStyle.corecolor);//testAttr.width
    	 
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
    /*
	shadertypes shaderenum = (shadertypes) instance.userData;
	if (shaderenum==null){
		return false;
	}
	//Gdx.app.log(logstag, "testing if concept beam can render:"+shaderenum.toString());
	
    	if (shaderenum==shadertypes.conceptbeam){
    		return true;
    	} else {
    		return false;
    	}
    	
    }
    */
    	return false;
    }
}