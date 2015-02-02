package com.lostagain.nl.me.camera;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.shaders.MyShaderProvider;

public class EffectOverlay extends ModelInstance {

	//public static ModelInstance CameraOverlay = null;
	static int w = 300;
	static int h = 300;
	
	
	public EffectOverlay() {		
		super(MessyModelMaker.createRectangle(0-(w/2), 0-(w/2), 0+(w/2),0+(h/2), -110, Color.BLACK, MessyModelMaker.createNoiseMaterial()));
			
		//CameraOverlay = MessyModelMaker.addNoiseRectangle(0,0,300,300,true);
		
		materials.get(0).set( new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.0f));		
		userData = MyShaderProvider.shadertypes.noise; 

		ModelManagment.addmodel(this);
		
	}


	public void setEffectOpacity(float opacity) {
		
		((BlendingAttribute)this.materials.get(0).get(BlendingAttribute.Type)).opacity = opacity;

		
	}


	
	
	
}
