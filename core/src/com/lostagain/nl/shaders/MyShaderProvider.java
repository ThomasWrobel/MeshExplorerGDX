package com.lostagain.nl.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;


/**
 * To help manage shader.
 * Stores custom attributes that can be used for object materials, these custom attributes will then trigger a specific shader
 * 
 * @author Tom
 *
 */
public class MyShaderProvider extends DefaultShaderProvider {
	public final DefaultShader.Config config;
	final static String logstag = "ME.MyShaderProvider";
	
	
	//attribute types
	// Create a custom attribute, see https://github.com/libgdx/libgdx/wiki/Material-and-environment
		// See also: http://blog.xoppa.com/using-materials-with-libgdx/
		public static class ConceptBeamAttribute extends Attribute {
			public final static String Alias = "ConceptBeamAttribute";
			public final static long ID = register(Alias);

			public float width;
			public Color beamcolor;
			public Color corecolor;
			
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
	
	
	
	
	
	//known shaders
	static public enum shadertypes {
		prettynoise,
		subtlegrid,
		invert,
		standardlibgdx, 
		noise,
		distancefield,
		distancefieldfordataobjects,
		conceptbeam
	}

	public MyShaderProvider (final DefaultShader.Config config) {
		this.config = (config == null) ? new DefaultShader.Config() : config;
	}

	public MyShaderProvider (final String vertexShader, final String fragmentShader) {
		this(new DefaultShader.Config(vertexShader, fragmentShader));
		
		
	}

	public MyShaderProvider (final FileHandle vertexShader, final FileHandle fragmentShader) {
		this(vertexShader.readString(), fragmentShader.readString());
	}

	public MyShaderProvider () {
		this(null);
	}
	
	public void testListShader(Renderable instance){
		
		for (Shader shader : shaders) {
			
			Gdx.app.log(logstag, "shader="+shader.getClass().getName());
			
			Gdx.app.log(logstag, "can render="+shader.canRender(instance));
			
		}
	}
	
	@Override
	protected Shader createShader (final Renderable renderable) {
		
		//pick shader based on renderable?
		shadertypes shaderenum = (shadertypes) renderable.userData;
	
		
		if (shaderenum==null){
				return super.createShader(renderable);
		}
		Gdx.app.log(logstag, "shaderenum="+shaderenum.toString());
			
			
		switch (shaderenum) {
		
		case prettynoise:
		{			
			return new PrettyNoiseShader();			
		}
		case invert:
		{
	    	  String vert = Gdx.files.internal("shaders/invert.vertex.glsl").readString();
	          String frag = Gdx.files.internal("shaders/invert.fragment.glsl").readString();
	          
	          
			return new DefaultShader(renderable, new DefaultShader.Config(vert, frag)); // new InvertShader(renderable);
		}
		case noise:
		{
			return new NoiseShader();
		}
		case conceptbeam:
		{
			Gdx.app.log(logstag, "creating concept gun beam ");
			return new ConceptBeamShader();
		}
		case distancefield:
		{
			return new DistanceFieldShader();
		}
		case subtlegrid:
		{
			return new SubtleGrid();
		}
		case distancefieldfordataobjects:
		{
			return new DistanceFieldShaderForDataObjects();
		}
		default:
			return super.createShader(renderable);
			
		}
		//return new DefaultShader(renderable, new DefaultShader.Config());
		
	}
}