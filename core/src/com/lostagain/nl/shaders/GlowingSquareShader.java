package com.lostagain.nl.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
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
	int u_resolution;

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
		//public Color glowColor;


		/**
		 * The presence of this parameter will cause the ConceptBeamShader to be used
		 * @param width - width of beam
		 * @param  glowColor - its color
		 * @param corecolor - color of its core (normally white for a intense glow at the middle of the beam)
		 */
		public GlowingSquareAttribute (final float glowWidth,final Color backColor, final Color coreColor ) {

			super(ID);
			this.glowWidth = glowWidth;
			this.backColor = backColor.cpy();
			this.coreColor = coreColor.cpy();
			//this.glowColor = glowColor.cpy();

		}

		@Override
		public Attribute copy () {
			return new GlowingSquareAttribute(glowWidth,backColor,coreColor);
		}

		@Override
		protected boolean equals (Attribute other) {
			if (
					(((GlowingSquareAttribute)other).glowWidth == glowWidth) &&
				//	(((GlowingSquareAttribute)other).glowColor == glowColor) &&
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

		Gdx.app.log(logstag, "initialising rectangle outline shader");

		//Note; shader now made yet
		String vert = Gdx.files.internal("shaders/glowingrectanglevert.glsl").readString();
		String frag = Gdx.files.internal("shaders/glowingrectanglefrag.glsl").readString();

		program = new ShaderProgram(vert, frag);

		if (!program.isCompiled()){
			throw new GdxRuntimeException(program.getLog());
		}

		Gdx.app.log(logstag, "storing shaders uniform locations");

		u_projViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans    = program.getUniformLocation("u_worldTrans");
		u_time          = program.getUniformLocation("u_time"); 
		u_pixel_step    = program.getUniformLocation("u_pixel_step");
		u_resolution    = program.getUniformLocation("u_resolution");

		//square style
		u_glowWidth     = program.getUniformLocation("u_glowWidth"); 
		u_backColor     = program.getUniformLocation("u_backColor"); 
		u_coreColor     = program.getUniformLocation("u_coreColor"); 
		u_glowColor     = program.getUniformLocation("u_glowColor"); 
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

		context.setDepthTest(GL20.GL_LESS); 
	//	context.setDepthTest(GL20.GL_GREATER);    	
			
	//	if (){
		//	context.setDepthTest(GL20.GL_NONE);   
		//}
		
		
		//lower for additive
		// context.setBlending(true,GL20.GL_ONE ,GL20.GL_ONE);    	  
		// context.setDepthTest(GL20.GL_DEPTH_TEST);    	
		//  context.setDepthTest(GL20.GL_DEPTH_BUFFER_BIT);    	    	  
		//  context.setCullFace(GL20.GL_BACK);
//

	}

	@Override
	public void render (Renderable renderable) {  
		
		//set the variable for the objects world transform to be passed to the shader
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);


		GlowingSquareAttribute squareStyle = (GlowingSquareAttribute)renderable.material.get(GlowingSquareAttribute.ID);
		Color back = squareStyle.backColor.cpy(); //squareStyle can be null at this point for some reason?
		Color core = squareStyle.coreColor.cpy();
		

		BlendingAttribute blending = ((BlendingAttribute)renderable.material.get(BlendingAttribute.Type));
		if (blending!=null){

			float opacity = blending.opacity;


			back.a = back.a * opacity;
			core.a = core.a * opacity;
			//glow.a = glow.a * opacity;
		}

		program.setUniformf(u_glowWidth, squareStyle.glowWidth);  	 
		program.setUniformf(u_backColor, back);
		program.setUniformf(u_coreColor, core);
		//program.setUniformf(u_glowColor, glow);

		float w = renderable.mesh.calculateBoundingBox().getWidth();
		float h = renderable.mesh.calculateBoundingBox().getHeight();

		setSizeUniform(w,h);

		renderable.mesh.render(program,
				renderable.primitiveType,
				renderable.meshPartOffset,
				renderable.meshPartSize);
	}
	public void setSizeUniform(float w, float h) {

		program.setUniformf(u_resolution, w,h);
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