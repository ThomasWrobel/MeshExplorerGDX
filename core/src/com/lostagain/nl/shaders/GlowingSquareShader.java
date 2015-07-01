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
import com.lostagain.nl.shaders.DistanceFieldShader.DistanceFieldAttribute;
import com.lostagain.nl.shaders.MyShaderProvider.shadertypes;

/**
 * This shader that simple makes a glowing square.
 * It will be used as the standard background for many interface things
 * 
 * @author Tom
 *
 */
public class GlowingSquareShader implements Shader {
	 ShaderProgram program;
	 Camera camera;
	 RenderContext context;
	 
	 final static String logstag = "ME.GlowingSquareShader";
	 
	   int u_projViewTrans;
	   int u_worldTrans;
	    int u_time;

	    int u_pixel_step;
        
        int u_glowWidth;
        int u_glowColor;
	    int u_backColor;
	    int u_coreColor;
	   
	   
	    
	   private float time;
	   
	   
	   ///------------------
		// Create a custom attribute, see https://github.com/libgdx/libgdx/wiki/Material-and-environment
		// See also: http://blog.xoppa.com/using-materials-with-libgdx/
	   /**
		 * The presence of this parameter will cause the ConceptBeamShader to be used
		 * */
		public static class GlowingSquareAttribute extends Attribute {
			public final static String Alias = "GlowingSquareAttribute";
			public final static long ID = register(Alias);

			public float glowWidth;
			public Color backColor;
			public Color coreColor;
			public Color glowColor;

		    
			/**
			 * The presence of this parameter will cause the ConceptBeamShader to be used
			 * @param width - width of beam
			 * @param beamcolor - its color
			 * @param corecolor - color of its core (normally white for a intense glow at the middle of the beam)
			 */
			public GlowingSquareAttribute (final float glowWidth,final Color glowColor,final Color backColor, final Color coreColor ) {
				
				super(ID);
				this.glowWidth = glowWidth;
				this.backColor = backColor;
				this.coreColor = coreColor;
				this.glowColor = glowColor;
				
			}

			@Override
			public Attribute copy () {
				return new GlowingSquareAttribute(glowWidth,glowColor,backColor,coreColor);
			}

			@Override
			protected boolean equals (Attribute other) {
				if (
					(((GlowingSquareAttribute)other).glowWidth == glowWidth) &&
					(((GlowingSquareAttribute)other).glowColor == glowColor) &&
					(((GlowingSquareAttribute)other).backColor == backColor) &&
					(((GlowingSquareAttribute)other).coreColor == coreColor) 
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
			   double otherwidth = ((GlowingSquareAttribute)o).glowWidth; //just picking width here arbitarily for the moment
			   //not sure yet when draw order will be important for glowing square backgrounds
			   
			        
			    return glowWidth == otherwidth ? 0 : (glowWidth < otherwidth ? -1 : 1);
			        
			}
		}	
	   
	   
    @Override
    public void init () {
    	
    	Gdx.app.log(logstag, "init square shader");
    	
    		//Note; shader now made yet
    	  String vert = Gdx.files.internal("shaders/glowingsquarevert.glsl").readString();
          String frag = Gdx.files.internal("shaders/glowingsquarefrag.glsl").readString();
          
          program = new ShaderProgram(vert, frag);
          if (!program.isCompiled()){
              throw new GdxRuntimeException(program.getLog());
          }
          
          u_projViewTrans = program.getUniformLocation("u_projViewTrans");
          u_worldTrans    = program.getUniformLocation("u_worldTrans");
          u_time          = program.getUniformLocation("u_time"); 
          u_pixel_step =  program.getUniformLocation("u_pixel_step");
          	//square style
		  u_glowWidth = program.getUniformLocation("u_glowWidth"); 
          u_backColor = program.getUniformLocation("u_backColor"); 
          u_coreColor = program.getUniformLocation("u_coreColor"); 
          u_glowColor = program.getUniformLocation("u_glowColor"); 
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
    	 
    	  context.setBlending(true,GL20.GL_SRC_ALPHA ,GL20.GL_ONE_MINUS_SRC_ALPHA);
    	  
    	  
    	  
    	  //lower for additive
    	 // context.setBlending(true,GL20.GL_ONE ,GL20.GL_ONE);    	  
    	 // context.setDepthTest(GL20.GL_DEPTH_TEST);    	
    	//  context.setDepthTest(GL20.GL_DEPTH_BUFFER_BIT);    	    	  
        //  context.setCullFace(GL20.GL_BACK);
          
    	  
    }
    
    @Override
    public void render (Renderable renderable) {  
    	//set the variable for the objects world transform to be passed to the shader

    	 program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
    	 
    	 
    	 GlowingSquareAttribute squareStyle = (GlowingSquareAttribute)renderable.material.get(GlowingSquareAttribute.ID);
    	
    	 program.setUniformf(u_glowWidth, squareStyle.glowWidth);//testAttr.width    	 
    	 program.setUniformf(u_backColor, squareStyle.backColor);//testAttr.width
    	 program.setUniformf(u_coreColor, squareStyle.coreColor);//testAttr.width
      	 program.setUniformf(u_glowColor, squareStyle.glowColor);
    	 float w = renderable.mesh.calculateBoundingBox().getWidth();
    	 float h = renderable.mesh.calculateBoundingBox().getHeight();
    	 
		setSizeUniform(w,h);
		
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

    	if (instance.material.has(GlowingSquareAttribute.ID)){
    		return true;
    	}

    	return false;
    }
}