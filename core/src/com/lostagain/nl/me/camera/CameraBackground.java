package com.lostagain.nl.me.camera;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.shaders.MyShaderProvider;

public class CameraBackground extends ModelInstance {

	//public static ModelInstance CameraOverlay = null;
	static int w = 3000;
	static int h = 3000;
	

    static Material defaultMat = new Material( new DepthTestAttribute(0, false),ColorAttribute.createDiffuse(Color.MAROON));
    
    
	public CameraBackground() {		
		super(MessyModelMaker.createRectangle(0-(w/2), 0-(w/2), 0+(w/2),0+(h/2), -910, Color.BLACK, defaultMat));
			
		//CameraOverlay = MessyModelMaker.addNoiseRectangle(0,0,300,300,true);
			
		materials.get(0).set( new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.6f));		
		
		userData = MyShaderProvider.shadertypes.subtlegrid; 

		ModelManagment.addmodel(this);
		
	}


	public void setEffectOpacity(float opacity) {
		
		((BlendingAttribute)this.materials.get(0).get(BlendingAttribute.Type)).opacity = opacity;

		
	}


	
	
	
}
