package com.lostagain.nl.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.me.domain.MEDomain;
import com.lostagain.nl.me.gui.ScreenUtils;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.shaders.ConceptBeamShader.ConceptBeamAttribute;
import com.lostagain.nl.shaders.InvertShader.InvertAttribute;
import com.lostagain.nl.shaders.MyShaderProvider.shadertypes;
import com.lostagain.nl.shaders.PrettyNoiseShader.PrettyNoiseShaderAttribute;

/**
 * Basic normal-colourish shader.
 * good for a template for other shaders
 * 
 * @author Tom
 *
 */
public class NoiseShader implements Shader {

	final static String logstag = "ME.NoiseShader";


	public static class NoiseShaderAttribute extends Attribute {
		public final static String Alias = "NoiseShaderAttribute";
		public final static long ID = register(Alias);

		public boolean rgbmode = false;
		public Color tintcolor;
		public boolean alphaFromLocation=false;

		/**
		 * The presence of this parameter will cause the NoiseShader to be used
		 * @param rgbmode - if the noise is the full color
		 * @param tintcolor - color of the tint
		 */
		public NoiseShaderAttribute (final boolean rgbmode,final Color tintcolor,final boolean alphaFromLocation) {

			super(ID);
			this.rgbmode = rgbmode;
			this.tintcolor = tintcolor;
			this.alphaFromLocation= alphaFromLocation;
		}

		@Override
		public Attribute copy () {
			return new NoiseShaderAttribute(rgbmode,tintcolor,alphaFromLocation);
		}

		@Override
		protected boolean equals (Attribute other) {
			if (
					(((NoiseShaderAttribute)other).rgbmode == rgbmode) &&
					(((NoiseShaderAttribute)other).alphaFromLocation == alphaFromLocation) &&
					(((NoiseShaderAttribute)other).tintcolor == tintcolor) 
					)

			{
				return true;

			}
			return false;
		}
		
		@Override
		public int compareTo(Attribute o) {
	        
		    return 1;
		        
		}
	}	


	ShaderProgram program;
	Camera camera;
	RenderContext context;

	int u_projViewTrans;
	int u_worldTrans;
	int u_sampler2D;
	int resolution;
	int mouse;
	int u_time;
	int u_alpha;

	String prefix = "";

	public NoiseShader(Renderable renderable) {

		prefix = createPrefix(renderable);



	}

	/** creates a little string that goes at the top of the fragment and vertext shader to specify a particular mode.
	 * This is a one time deal upon the shaders creation, unlike uniforms that can be set and changed as we go along**/
	private String createPrefix(Renderable renderable) {

		//we get the noise shader types settings
		NoiseShaderAttribute beamStyle = (NoiseShaderAttribute)renderable.material.get(NoiseShaderAttribute.ID);   	 

		//if we are using rgb mode we define that flag
		if (beamStyle.rgbmode){
			prefix = prefix + "#define rgbmodeFlag \n";
		}

		return prefix;
	}

	@Override
	public void init () {

		String vert = Gdx.files.internal("shaders/noise.vertex.glsl").readString();
		String frag = Gdx.files.internal("shaders/noise.fragment.glsl").readString();


		program = new ShaderProgram(prefix + vert, prefix + frag);

		if (!program.isCompiled()){
			throw new GdxRuntimeException(program.getLog());
		}

		u_projViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans = program.getUniformLocation("u_worldTrans");
		u_sampler2D =   program.getUniformLocation("u_diffuseTexture");

		resolution =   program.getUniformLocation("resolution");
		mouse =  program.getUniformLocation("mouse");
		u_time =   program.getUniformLocation("u_time");
		u_alpha =   program.getUniformLocation("u_alpha");
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

		context.setBlending(true,GL20.GL_SRC_ALPHA ,GL20.GL_ONE_MINUS_SRC_ALPHA);


		// context.setDepthTest(GL20.GL_LEQUAL);    	  
		// context.setCullFace(GL20.GL_BACK);
	}

	@Override
	public void render (Renderable renderable) {  
		//set the variable for the objects world transform to be passed to the shader
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);


		//program.setUniformf(TexCoord, value);

		//if (renderable.material.get(TextureAttribute.Diffuse)!=null){

		//	 Texture testtexture = ((TextureAttribute)renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture;      		 
		// program.setUniformi(u_sampler2D, context.textureBinder.bind(testtexture));



		NoiseShaderAttribute style = (NoiseShaderAttribute)renderable.material.get(NoiseShaderAttribute.ID);




		if (style.alphaFromLocation){
			//get current position on the game field
			Vector3 position = MainExplorationView.currentPos;
			
			float alpha = Location.getCorruptionAt(position,800);
			//we should also add height based corruption ontop of this, so the user can't zoom out too much
			float standardHeight = ScreenUtils.getSuitableDefaultCameraHeight()-50; //the -50 ensures it has a little noise even at standard height
			float heightbasedopacity = (MainExplorationView.camera.transState.position.z-(standardHeight))/1000.0f; //	380.0f
			alpha=alpha+heightbasedopacity;
			
			program.setUniformf(u_alpha, alpha);

		} 
			else if (renderable.material.has(BlendingAttribute.Type))
		{
			program.setUniformf(u_alpha, ((BlendingAttribute)renderable.material.get(BlendingAttribute.Type)).opacity);

		}
			else
		{
			program.setUniformf(u_alpha, 1.0f);
		}



		program.setUniformi(mouse, 70, 70);






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

		if (instance.material.has(NoiseShaderAttribute.ID)){
			return true;
		}

		//	Gdx.app.log(logstag, "testing if noiseshader can render:"+shaderenum.toString());
		return false;


	}


}