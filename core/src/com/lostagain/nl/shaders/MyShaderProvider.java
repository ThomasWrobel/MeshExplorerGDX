package com.lostagain.nl.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class MyShaderProvider extends DefaultShaderProvider {
	public final DefaultShader.Config config;
	final static String logstag = "ME.MyShaderProvider";
	//known shaders
	static public enum shadertypes {
		prettynoise,
		invert,
		standardlibgdx, 
		noise,
		distancefield,
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
		default:
			return super.createShader(renderable);
			
		}
		//return new DefaultShader(renderable, new DefaultShader.Config());
		
	}
}