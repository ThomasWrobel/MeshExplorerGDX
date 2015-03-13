package com.lostagain.nl.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.lostagain.nl.shaders.ConceptBeamShader.ConceptBeamAttribute;
import com.lostagain.nl.shaders.TextureNoiseShader.TextureNoiseAttribute;

public class InvertShader extends DefaultShader {
	
	  static String vert = Gdx.files.internal("shaders/invert.vertex.glsl").readString();
      static String frag = Gdx.files.internal("shaders/invert.fragment.glsl").readString();
      
		public static class InvertAttribute extends Attribute {
			
			public final static String Alias = "InvertShaderAttribute";
			public final static long ID = register(Alias);

			/**
			 * The presence of this parameter will cause the InvertShader to be used
			 */
			public InvertAttribute () {				
				super(ID);				
			}

			@Override
			public Attribute copy () {
				return new InvertAttribute();
			}

			@Override
			protected boolean equals (Attribute other) {				
				return true;
			}
		}	
		
		
	public InvertShader(Renderable renderable) {
		super(renderable, new DefaultShader.Config(vert, frag));
		
	}
	
	 @Override
	    public boolean canRender (Renderable instance) {

	    	if (instance.material.has(InvertAttribute.ID)){
	    		return true;
	    	}
	    	
	    //	Gdx.app.log(logstag, "testing if noiseshader can render:"+shaderenum.toString());
	    	return false;
	  
	    	
	    }
}
