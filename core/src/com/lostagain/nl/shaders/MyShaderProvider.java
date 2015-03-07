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
	
	
	//attribute types stored in shader types themselves

	
	
	
	
	
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
		
		//new method for selection (we should slowly move the things from the switch statement to this method)
		if (renderable.material.has(ConceptBeamShader.ConceptBeamAttribute.ID)){
			return new ConceptBeamShader();			
		}
		
		if (renderable.material.has(NoiseShader.NoiseShaderAttribute.ID)){
			return new NoiseShader(renderable);			
		}
		
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
	
		case distancefield:
		{
			return new DistanceFieldShader();
		}
		case subtlegrid:
		{
			return new PrettyBackground();
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