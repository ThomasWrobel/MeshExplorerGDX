package com.lostagain.nl.me.camera;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.shaders.MyShaderProvider;
import com.lostagain.nl.shaders.NoiseShader;

public class EffectOverlay extends AnimatableModelInstance {

	//public static ModelInstance CameraOverlay = null;
	static int w = 300;
	static int h = 300;
	
	
	public EffectOverlay() {		
		super(MessyModelMaker.createRectangle(0-(w/2), 0-(w/2), 0+(w/2),0+(h/2), -110, Color.BLACK, MessyModelMaker.createNoiseMaterial()));
			

		
       // new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.5f),
		//  ColorAttribute.createDiffuse(Color.RED)
		
		
		materials.get(0).set( new NoiseShader.NoiseShaderAttribute(false,Color.ORANGE));
							//  new NoiseShader.NoiseShaderAttribute(false,Color.ORANGE));		
		//userData = MyShaderProvider.shadertypes.noise; 

		ModelManagment.addmodel(this,ModelManagment.RenderOrder.infrontStage);
		
	}


	public void setEffectOpacity(float opacity) {
		
		((BlendingAttribute)this.materials.get(0).get(BlendingAttribute.Type)).opacity = opacity;

		
	}


	
	
	
}
