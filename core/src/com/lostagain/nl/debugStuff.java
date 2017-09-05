package com.lostagain.nl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.lostagain.nl.GWTish.Management.GWTishModelManagement;
import com.lostagain.nl.GWTish.Management.GWTishModelManagement.RenderOrder;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.shaders.ConceptBeamShader;
import com.lostagain.nl.shaders.ConceptBeamShader.ConceptBeamAttribute;

public class debugStuff {

	public static void addDebuggingTestModels() {
		/**
		 * test shhader for lazer beam
		 */
		Material beamShader = new Material(
				ColorAttribute.createDiffuse(Color.BLUE), 
				ColorAttribute.createSpecular(Color.WHITE),
				new BlendingAttribute(1f), 
				FloatAttribute.createShininess(16f),
				new ConceptBeamShader.ConceptBeamAttribute(0.4f,Color.BLUE,5.0f,Color.WHITE)
				);
	
	
	
	
	
		/**
		 * test shhader for lazer beam
		 */
		ModelInstance beamtest = ModelMaker.createRectangleAt(500, 1000, 30, 200, 200, Color.BLACK, beamShader); // new ModelInstance(model1); 
	
		//	Renderable renderableWithAttribute = new Renderable();
		//	beamtest.getRenderable(renderableWithAttribute);
	
		//	Boolean defaultCanRender = test.canRender(renderableWithAttribute);
	
	
		//	Gdx.app.log(logstag,"default created with attribute = "+defaultCanRender);
	
	
	
		//--------------	
	
		//The following was a test of how the domains colour range should look. Re-enable during testing.
	
		/*
		Pixmap colourMapAsPixMap = MEDomain.getHomeDomain().getDomainsColourMap().getPixMap(200, 200);
		//Pixmap colourMapAsPixMap = MessyModelMaker.createNoiseImage(200, 200);
	
	
		Texture colmap = new Texture(colourMapAsPixMap);
	
		Material testmaterial3 = new Material
				(
						ColorAttribute.createSpecular(Color.WHITE),
						new BlendingAttribute(1f), 
						FloatAttribute.createShininess(16f),
						TextureAttribute.createDiffuse(colmap)
						);
	
	
	
		ModelInstance colortest = ModelMaker.createRectangleAt(0, 900, 130, 200, 200, Color.BLACK, testmaterial3); 
		 */
	
	
		//ModelManagment.addmodel(centermaker,RenderOrder.infrontStage);
	
	
	
		if (GameMode.currentGameMode!=GameMode.Production){
			GWTishModelManagement.addmodel(beamtest,GWTishModelManagement.RenderOrder.OVERLAY);
			//	ModelManagment.addmodel(colortest,RenderOrder.infrontStage);
	
			GWTishModelManagement.addTestModels();
		}
	}

}
