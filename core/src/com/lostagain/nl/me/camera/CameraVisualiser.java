package com.lostagain.nl.me.camera;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.GWTish.PosRotScale;
import com.lostagain.nl.GWTish.Management.AnimatableModelInstance;
import com.lostagain.nl.me.models.GWTishModelManagement;
import com.lostagain.nl.me.models.GWTishModelManagement.RenderOrder;

/** To visualizer a camera,which helps when debugging.
 * 
 * Currently is just a cube with a axis displayed on it.
 * In future showing the field of view / view ranges might be nicer **/
public class CameraVisualiser extends AnimatableModelInstance {


    static Material visualiserStyle = new Material(
    		ColorAttribute.createDiffuse(Color.PINK), 
			ColorAttribute.createSpecular(Color.WHITE),
			new BlendingAttribute(1f), 
			FloatAttribute.createShininess(16f));
    	
	
	public CameraVisualiser() {		
		
		super(createCenterPointModel(visualiserStyle));
		
		//attach a block to test the attachment (really this should be built into the main model instance, but as this is only for debugging who cares?
		ModelBuilder modelBuilder = new ModelBuilder();

        Material RED = new Material(
        		ColorAttribute.createDiffuse(Color.RED), 
				ColorAttribute.createSpecular(Color.WHITE),
				new BlendingAttribute(1f), 
				FloatAttribute.createShininess(16f));

		//modelBuilder.begin();
		Model cube =  modelBuilder.createBox(20, 20, 20, RED, Usage.Position | Usage.Normal | Usage.TextureCoordinates );
		

		
		
		AnimatableModelInstance objectToAttach = new AnimatableModelInstance(cube);	
		GWTishModelManagement.addmodel(objectToAttach, RenderOrder.OVERLAY);
		this.attachThis(objectToAttach, new PosRotScale(0f,0f,0f));
		
		
	}


	
	
	
	private static Model createCenterPointModel(Material material) {
			
			
			ModelBuilder modelBuilder = new ModelBuilder();
			//note; maybe these things could be pre-created and stored rather then a new one each time?
			Model model =  modelBuilder.createXYZCoordinates(75f, material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
			
		
		return model;
	}
	
	
}
