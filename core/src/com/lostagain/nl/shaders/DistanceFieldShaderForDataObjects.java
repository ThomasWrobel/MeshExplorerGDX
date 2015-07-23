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
import com.lostagain.nl.shaders.DistanceFieldShader.DistanceFieldAttribute;
import com.lostagain.nl.shaders.MyShaderProvider.shadertypes;

/**
 * 
 * @author Tom
 *
 */
public class DistanceFieldShaderForDataObjects implements Shader {

	public static DistanceFieldShaderForDataObjects Default = new DistanceFieldShaderForDataObjects();
		 
	 ///------------------
	// Create a custom attribute, see https://github.com/libgdx/libgdx/wiki/Material-and-environment
	// See also: http://blog.xoppa.com/using-materials-with-libgdx/
   /**
	 * The presence of this parameter will cause the DistanceFieldttribute to be used
	 * */
	public static class DistanceFieldttribute extends Attribute {
		public final static String Alias = "DistanceFieldttribute";
		public final static long ID = register(Alias);

		public float smoothing;
		public Color fontcolour;
		public Color backcolour;
		/**
		 * The presence of this parameter will cause the DistanceFieldShaderForDataObjects to be used
		 * @param smoothing - how smooth the font is rendered at (should be set to 0.25/(filesmooth*fontfilescale))
		 * @param fontcolour - the colour of the font (defaults to the colour in the texture if null)
		 * @param backcolour - background colour (defaults to transparent if null)
		 */
		public DistanceFieldttribute (final float smoothing,final Color fontcolour,final Color backcolour ) {
			
			super(ID);
			this.smoothing = smoothing;
			this.fontcolour =fontcolour;
			this.backcolour = backcolour;
		}

		@Override
		public Attribute copy () {
			return new DistanceFieldttribute(smoothing,fontcolour,backcolour);
		}

		@Override
		protected boolean equals (Attribute other) {
			if ((((DistanceFieldttribute)other).smoothing == smoothing) &&
				(((DistanceFieldttribute)other).fontcolour == fontcolour) &&
				(((DistanceFieldttribute)other).backcolour == backcolour) 
				){
				return true;
				
			}
			return false;
		}
		
		@Override
		public int compareTo(Attribute o) {
			
		   //Ensuring attribute we are comparing too is the same type, if not we truth
		   if (type != o.type) return type < o.type ? -1 : 1; //if not the same type and less then we return -1 else we return 1
			 			 
		   //if they are the same type we continue	
		   double otherSmooth = ((DistanceFieldttribute)o).smoothing; //just picking width here arbitarily. Theres no real reason for these to be rendered in a different order relative to eachother
		   //so the order can be pretty arbitarily.
		        
		    return smoothing == otherSmooth ? 0 : (smoothing < otherSmooth ? -1 : 1);
		        
		}
	}	
	
	
	public ShaderProgram program;
		 
	 Camera camera;
	 RenderContext context;
	 
	 final static String logstag = "ME.DistanceFieldShaderForDataObjects";
	 
	   int u_projViewTrans;
	    int u_worldTrans;
	    int u_sampler2D; 
	    
	    int a_colorFlag;
	    int u_diffuseColor;
	    int u_pixel_step;
	    
	    
    @Override
    public void init () {
    	
    	  String vert = Gdx.files.internal("shaders/distancefieldvert_spritebatch.glsl").readString();
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
          u_diffuseColor =  program.getUniformLocation("u_diffuseColor");
          
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
    	  
    		 program.setUniformf(a_colorFlag,0f);
    		 program.setUniformf(u_diffuseColor, Color.ORANGE);
    		 
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
    		
    	
		setSizeUniform(w,h);
    	 
    	 
    	 if (renderable.material.get(TextureAttribute.Diffuse)!=null){
    		 
    		 Texture testtexture = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;      		 
    		 program.setUniformi(u_sampler2D, context.textureBinder.bind(testtexture));    		    		 
    		 
    	 }
    	 
    	 if (renderable.material.has(ColorAttribute.Diffuse)){				
    		 program.setUniformf(a_colorFlag,1f);
    		 program.setUniformf(u_diffuseColor, ((ColorAttribute)renderable.material.get(ColorAttribute.Diffuse)).color);    		 
    	 } else {
    		 program.setUniformf(a_colorFlag,0f);
    		 program.setUniformf(u_diffuseColor, Color.ORANGE);
    	 }
		 
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
    	
    	shadertypes shaderenum = (shadertypes) instance.userData;
    	if (shaderenum==null){
    		return false; 
    	}
    //	Gdx.app.log(logstag, "testing if distance field can render:"+shaderenum.toString());
    	
    	if (shaderenum==shadertypes.distancefieldfordataobjects){
    		return true;
    	} else {
    		return false;
    	}
    }

    /**
     * returns the default copy of this shader, compiling it if needed
     * @return
     */
	public static ShaderProgram getProgram() {
		if (Default.program==null){
			Default.init();
		}
		// TODO Auto-generated method stub
		return Default.program;
	}

	
	public static void setDefaults(float f, Color orange) {
		
		Default.program.setUniformf(Default.a_colorFlag,1f);
		Default.program.setUniformf(Default.u_diffuseColor, Color.ORANGE);
		
	}
    
    
}