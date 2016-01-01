package com.lostagain.nl.shaders;

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
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.me.features.LinkStoreObject;
import com.lostagain.nl.shaders.DistanceFieldShaderForDataObjects.DistanceFieldttribute;
import com.lostagain.nl.shaders.MyShaderProvider.shadertypes;
import com.lostagain.nl.shaders.NoiseShader.NoiseShaderAttribute;
import com.lostagain.nl.shaders.PrettyNoiseShader.PrettyNoiseShaderAttribute;

/**
 * Work in progress normal map shader
 * https://gist.github.com/mattdesl/4653464 is the referance
 * 
 * 
 * 
 * 
 * 
 * @author Tom
 *
 */
public class NormalMapShader extends DefaultShader {

	 final static String logstag = "ME.NormalMapShader";
	 
	 public static class  NormalMapShaderAttribute extends Attribute {
			public final static String Alias = "NormalMapShaderAttribute";
			public final static long ID = register(Alias);

			public Texture normalMap;
			
			/**
			 * The presence of this parameter will cause the NormalMapShaderAttribute to be used
			 * @param rgbmode - if the noise is the full color
			 * @param tintcolor - color of the tint
			 */
			public  NormalMapShaderAttribute (final Texture normalMap) {
				
				super(ID);
				this.normalMap = normalMap;
				
			}

			@Override
			public Attribute copy () {
				return new  NormalMapShaderAttribute(normalMap);
			}

			@Override
			protected boolean equals (Attribute other) {
				if (
					(((NormalMapShaderAttribute)other).normalMap == normalMap) 
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
	 
	 
	 
	 public NormalMapShader(Renderable renderable) {
		super(renderable);	
		// TODO Auto-generated constructor stub
	}

	 
	 

		
		final String VERT =  
				"attribute vec4 "+ShaderProgram.POSITION_ATTRIBUTE+";\n" +
				"attribute vec4 "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
				"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
				
				"uniform mat4 u_projTrans;\n" + 
				"varying vec4 vColor;\n" +
				"varying vec2 vTexCoord;\n" +
				
				"void main() {\n" +  
				"	vColor = "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
				"	vTexCoord = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
				"	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
				"}";
		
		//no changes except for LOWP for color values
		//we would store this in a file for increased readability
		final String FRAG = 
				//GL ES specific stuff
				  "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" + //
				"//attributes from vertex shader\n" + 
				"varying LOWP vec4 vColor;\n" + 
				"varying vec2 vTexCoord;\n" + 
				"\n" + 
				"//our texture samplers\n" + 
				"uniform sampler2D u_texture;   //diffuse map\n" + 
				"uniform sampler2D u_normals;   //normal map\n" + 
				"\n" + 
				"//values used for shading algorithm...\n" + 
				"uniform vec2 Resolution;         //resolution of screen\n" + 
				"uniform vec3 LightPos;           //light position, normalized\n" + 
				"uniform LOWP vec4 LightColor;    //light RGBA -- alpha is intensity\n" + 
				"uniform LOWP vec4 AmbientColor;  //ambient RGBA -- alpha is intensity \n" + 
				"uniform vec3 Falloff;            //attenuation coefficients\n" + 
				"\n" + 
				"void main() {\n" + 
				"	//RGBA of our diffuse color\n" + 
				"	vec4 DiffuseColor = texture2D(u_texture, vTexCoord);\n" + 
				"	\n" + 
				"	//RGB of our normal map\n" + 
				"	vec3 NormalMap = texture2D(u_normals, vTexCoord).rgb;\n" + 
				"	\n" + 
				"	//The delta position of light\n" + 
				"	vec3 LightDir = vec3(LightPos.xy - (gl_FragCoord.xy / Resolution.xy), LightPos.z);\n" + 
				"	\n" + 
				"	//Correct for aspect ratio\n" + 
				"	LightDir.x *= Resolution.x / Resolution.y;\n" + 
				"	\n" + 
				"	//Determine distance (used for attenuation) BEFORE we normalize our LightDir\n" + 
				"	float D = length(LightDir);\n" + 
				"	\n" + 
				"	//normalize our vectors\n" + 
				"	vec3 N = normalize(NormalMap * 2.0 - 1.0);\n" + 
				"	vec3 L = normalize(LightDir);\n" + 
				"	\n" + 
				"	//Pre-multiply light color with intensity\n" + 
				"	//Then perform \"N dot L\" to determine our diffuse term\n" + 
				"	vec3 Diffuse = (LightColor.rgb * LightColor.a) * max(dot(N, L), 0.0);\n" + 
				"\n" + 
				"	//pre-multiply ambient color with intensity\n" + 
				"	vec3 Ambient = AmbientColor.rgb * AmbientColor.a;\n" + 
				"	\n" + 
				"	//calculate attenuation\n" + 
				"	float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );\n" + 
				"	\n" + 
				"	//the calculation which brings it all together\n" + 
				"	vec3 Intensity = Ambient + Diffuse * Attenuation;\n" + 
				"	vec3 FinalColor = DiffuseColor.rgb * Intensity;\n" + 
				"	gl_FragColor = vColor * vec4(FinalColor, DiffuseColor.a);\n" + 
				"}";
	 
		

		//our constants...
		//public static final float DEFAULT_LIGHT_Z = 0.075f;
		public static final float AMBIENT_INTENSITY = 0.2f;
		//public static final float LIGHT_INTENSITY = 1f;
		
	//	public static final Vector3 LIGHT_POS = new Vector3(0f,0f,DEFAULT_LIGHT_Z);
		
		//Light RGB and intensity (alpha)
		public static final Vector3 LIGHT_COLOR = new Vector3(1f, 0.8f, 0.6f);

		//Ambient RGB and intensity (alpha)
		public static final Vector3 AMBIENT_COLOR = new Vector3(0.6f, 0.6f, 1f);

		//Attenuation coefficients for light falloff
		public static final Vector3 FALLOFF = new Vector3(.4f, 3f, 20f);	
		
		
		//temp fixed textures
		Texture diffuseMap, diffuseNormals;
		
	 /*
	ShaderProgram program;
	
	 */
		
	   int u_projViewTrans;
	    int u_worldTrans;
	    int u_sampler2D;
	    int u_vcolor;
		 Camera camera;
		 RenderContext context;
		 
		int u_normals;
	    int LightColor;
	    int AmbientColor;
	    int Falloff;
	    int LightPos;
	    int Resolution;
	    
    @Override
    public void init () {

        //temp fixed textures
      
	      
	      
    	  String vert = Gdx.files.internal("shaders/normalshadervert.glsl").readString();
          String frag = Gdx.files.internal("shaders/normalshaderfrag.glsl").readString();

          ShaderProgram.pedantic = true;

          program = new ShaderProgram(vert, frag);
          
          if (!program.isCompiled()){
              throw new GdxRuntimeException(program.getLog());
          }
          //print warnings
  		  if (program.getLog().length()!=0)
  		  {
  	          Gdx.app.log(logstag, "normal shader log:"+program.getLog());
  		  }
  		  
  		  
          //set uniform locations
          u_projViewTrans = program.getUniformLocation("u_projViewTrans");
          u_worldTrans    = program.getUniformLocation("u_worldTrans");
  		  
  		  
  		  u_normals    = program.getUniformLocation("u_normals");
          LightColor   = program.getUniformLocation("LightColor");
          AmbientColor = program.getUniformLocation("AmbientColor");
          Falloff      = program.getUniformLocation("Falloff");
          LightPos     = program.getUniformLocation("LightPos");          
          Resolution   = program.getUniformLocation("Resolution");
          
          u_vcolor  = program.getUniformLocation("u_vcolor");
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

		context.setBlending(true,GL20.GL_SRC_ALPHA ,GL20.GL_ONE_MINUS_SRC_ALPHA);		
		context.setDepthTest(GL20.GL_LESS);    	
		 
		 
   	    //the the variable for the cameras projection to be passed to the shader
  	    program.setUniformMatrix(u_projViewTrans, camera.combined);
  	    
    	//our normal map
    	program.setUniformi(u_normals, 1); //GL_TEXTURE1
    			
    	
    	//get the scenes light (currently just one at the moment, linked to the mouse)
    	PointLight mouselight = MainExplorationView.mouseLight;
    	
    			//light/ambient colors
    			//LibGDX doesn't have Vector4 class at the moment, so we pass them individually...
    //	program.setUniformf(LightColor, LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, mouselight.intensity);
    	
    	program.setUniformf(LightColor, mouselight.color.r, mouselight.color.g, mouselight.color.b, mouselight.intensity);
    	program.setUniformf(AmbientColor, AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
    	program.setUniformf(Falloff, FALLOFF);

        //do we need to do this each time?
        program.setUniformf(Resolution, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
          
        
        
        //set up the light info here (as its the same for all normal map shaders in the scene)
        
	    Vector2 mouseLight = MainExplorationView.gameStage.stageToScreenCoordinates( new Vector2(mouselight.position.x, mouselight.position.y));
 	 	//get co-ordinates in the range 0-1
 		float x =  (mouseLight.x) / (float)Gdx.graphics.getWidth();
 		float y = ((MainExplorationView.gameStage.getHeight() - mouseLight.y ) / (float)Gdx.graphics.getHeight()); 		
 	      		
 		//send to GLSL
 		program.setUniformf(LightPos, new Vector3(x,y,mouselight.position.z));
 		
    }
    
  
    
    @Override
    public void render (Renderable renderable) {  
    	
    	//set the variable for the objects world transform to be passed to the shader
    	 program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
    	     	 
    	 
    	//update light position, normalized to screen resolution
    //	Vector2 Mouse = ME.getCurrentCursorScreenPosition();
    
 	//	float x = (Mouse.x) / (float)Gdx.graphics.getWidth();
 	//	float y = ((MainExplorationView.gameStage.getHeight() - Mouse.y ) / (float)Gdx.graphics.getHeight());

    	
 	//	LIGHT_POS.x = x;
 	//	LIGHT_POS.y = y;

        
 		//we need to get the main lights position as screen relative
 	//	Vector2 mouseLight = MainExplorationView.gameStage.stageToScreenCoordinates( new Vector2(MainExplorationView.mouseLight.position.x, MainExplorationView.mouseLight.position.y));
 	 	
 	//	float x =  (mouseLight.x) / (float)Gdx.graphics.getWidth();
 	 //	//float y = -((MainExplorationView.gameStage.getHeight() - mouseLight.y ) / (float)Gdx.graphics.getHeight()); 		
 	 //	float y = ((MainExplorationView.gameStage.getHeight() - mouseLight.y ) / (float)Gdx.graphics.getHeight()); 		
 	     
 //	 	LIGHT_POS.x = x;
 	// 	LIGHT_POS.y = y;
 	 	
	//	Gdx.app.log(logstag,"mouseLight"+x+","+y);
 		
 		//send a Vector4f to GLSL
 		//program.setUniformf(LightPos, new Vector3(x,y,NormalMapShader.DEFAULT_LIGHT_Z));
 		
 		//program.setUniformf(LightPos, LIGHT_POS);
 	 	
 		//if we have a color diffuse we use that, else we use white as the vertex colors
 		if (renderable.material.get(ColorAttribute.Diffuse)!=null){
 			
 			
    		 Color vcol = ((ColorAttribute)renderable.material.get(ColorAttribute.Diffuse)).color; 		 

  		//	Gdx.app.log(logstag, "setting to colour:"+vcol);
    		 program.setUniformf(u_vcolor, vcol);    		    		 
    		 
    	 } else {
    		 
    		// Gdx.app.log(logstag, "glowColour white");
    		 
    		 program.setUniformf(u_vcolor, Color.WHITE);    	
    	 }
 		
 		//set correct textures	
		 Texture diffuseMapTexture = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;      
		 diffuseMap        = diffuseMapTexture; 
 		
		 NormalMapShaderAttribute normal = ((NormalMapShaderAttribute)renderable.material.get(NormalMapShaderAttribute.ID)); 
		 diffuseNormals    = normal.normalMap;
 		
 		
 		//bind normal map to texture unit 1
 		diffuseNormals.bind(1);
 		
 		//bind diffuse color to texture unit 0
 		//important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
 		diffuseMap.bind(0);
    	 

   	 //    program.setUniformi(u_sampler2D, 0);
    //	 program.setUniformi(u_normals, 1);
 		renderable.meshPart.render(program);
 		/* pre 1.7.1 https://github.com/libgdx/libgdx/pull/3483
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
    	
    	if (instance.material.has( NormalMapShaderAttribute.ID)){

    		return true;
    	}
    	
    	return false;
    	
    }
    
    
}