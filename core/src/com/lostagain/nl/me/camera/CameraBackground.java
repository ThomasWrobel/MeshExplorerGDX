package com.lostagain.nl.me.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;
import com.lostagain.nl.me.models.GWTishModelManagement;
import com.lostagain.nl.me.models.GWTishModelManagement.RenderOrder;
import com.lostagain.nl.shaders.GameBackgroundShader;
import com.lostagain.nl.shaders.MyShaderProvider;
import com.lostagain.nl.shaders.PrettyBackground;

public class CameraBackground extends AnimatableModelInstance {

	//public static ModelInstance CameraOverlay = null;
	static int w = 1000;
	static int h = 1000;
	

    static Material defaultMat = new Material( new DepthTestAttribute(0, false),ColorAttribute.createDiffuse(Color.MAROON));
    
    
	public CameraBackground() {		
		
		super(MessyModelMaker.createRectangle(0-(w/2), 0-(w/2), 0+(w/2),0+(h/2), 0, Color.BLACK, defaultMat)); //z was -910
			
		//CameraOverlay = MessyModelMaker.addNoiseRectangle(0,0,300,300,true);
			
	//	materials.get(0).set( new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,1f));	
		
		//test texture
		
		Texture texture = new Texture(Gdx.files.internal("data/rock_n.png")); //just giving them a random normal map for now to test
		
		materials.get(0).set( new GameBackgroundShader.GameBackgroundShaderAttribute(texture));		
		
		FileHandle imageFileHandle = Gdx.files.internal("data/infovours/genericinfovour.png"); 
		Texture blobtexture = new Texture(imageFileHandle);		

		materials.get(0).set(TextureAttribute.createDiffuse(blobtexture));

		GWTishModelManagement.addmodel(this,GWTishModelManagement.RenderOrder.STANDARD);
		
		
	}


	public void setEffectOpacity(float opacity) {
		
		((BlendingAttribute)this.materials.get(0).get(BlendingAttribute.Type)).opacity = opacity;

		
	}


	
	
	
}
