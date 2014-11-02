package com.lostagain.nl.me.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.me.creatures.Population.destructOn;
import com.lostagain.nl.me.models.Animating;
import com.lostagain.nl.me.models.InfovoreAnimation;
import com.lostagain.nl.me.models.ModelManagment;

/** defines what a basic infovore looks like and how it behaves**/
public class BasicInfovore extends Creature implements Animating {
	

	private static String logstag="ME.BasicInfovore";
	
    //infovore stats:

	//-----		
	
	//movement? (static)
	
	//how it looks? (static)
	
	//Note; If too intensive we can make this static
	//then change the animation update so its updated once per population TYPE not for each 
	//creature individually.
	//this will, however, make all the creatures animate in sycn which isnt so nice
	InfovoreAnimation idealAnimation = new InfovoreAnimation();
	
	public BasicInfovore(Population parentPopulation, float x, float y,
			int hitPoints, String queryToDestroy, destructOn destructionType){
		
		super(x,y,parentPopulation,hitPoints, queryToDestroy, destructionType);
		
		
		
		//ensure infovote animation is setup
		if (!idealAnimation.isSetup()){
			idealAnimation.create();
		}
		
		createmodel(x,y,-10);
				
		
		
	}

	private void createmodel(float  x,float y,float z) {

		
		// just a cube for now
			
		BlendingAttribute blendingAttribute2 = new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.9f);
		
				//.createBox(15f, 15f, 15f, 
		Material mat = new Material(ColorAttribute.createDiffuse(Color.MAROON));
			//	Usage.Position | Usage.Normal);
		
		//
		//FileHandle imageFileHandle = Gdx.files.internal("data/infovorebasic.png"); 
		//Texture blobtexture = new Texture(imageFileHandle);
		
        mat.set(TextureAttribute.createDiffuse(idealAnimation.getKeyFrame(0)));
        
		
		
		Model model = createRectangle( -25, -25, 50, 50, 0, Color.MAROON, mat );
        mat.set(blendingAttribute2);
        
		
		creaturemodel = new ModelInstance(model);				
		creaturemodel.transform.setToTranslation(x,y,z);
		
		super.setmodel(creaturemodel);
		
		//as this model is animated we should add it to the model manager as animated
		ModelManagment.addAnimating(this);
		
		
		
		
		
	}

	//if too slow this method could be exchanged with one in population
	//that grabs a common frame for all the population then sets all its creatures too it
	public void updateAnimationFrame(float deltatime){
		
		TextureRegion currentimage = idealAnimation.getKeyFrame(deltatime);
		TextureAttribute attribute = creaturemodel.materials.get(0).get(TextureAttribute.class, TextureAttribute.Diffuse);
			
			if (attribute!=null){			
				attribute.set(currentimage);		
			} 
					
		
	}

	static private Model createRectangle(float x1,float y1,float x2,float y2,float z,Color MColor,Material mat ) {

		
		Vector3 corner1 = new Vector3(x1,y1,z);
		Vector3 corner2 = new Vector3(x2,y1,z);
		Vector3 corner3 = new Vector3(x2,y2,z);
		Vector3 corner4 = new Vector3(x1,y2,z);	
	
    
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder meshBuilder;

		//Node node = modelBuilder.node();
		//node.translation.set(11,11,5);		
		
		meshBuilder = modelBuilder.part("bit", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal|Usage.TextureCoordinates, mat);

		
		//meshBuilder.cone(5, 5, 5, 10);
		
		VertexInfo newtest1 = new VertexInfo();
		Vector3 testnorm=new Vector3(0,1,0);
		newtest1.set(corner1, testnorm, Color.WHITE, new Vector2(0f,0f));

		VertexInfo newtest2 = new VertexInfo();
		newtest2.set(corner2, testnorm, Color.WHITE, new Vector2(0f,1f));

		VertexInfo newtest3 = new VertexInfo();
		newtest3.set(corner3, testnorm, Color.WHITE, new Vector2(1f,1f));
		
		VertexInfo newtest4 = new VertexInfo();
		newtest4.set(corner4, testnorm, Color.WHITE, new Vector2(1f,0f));
		
		meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);
	 
		

		Model model = modelBuilder.end();
		
		

		return model;
	}
	
	@Override
	public void fireTouchDown() {
		super.fireTouchDown();
		
		
		
	}




	@Override
	public void fireTouchUp() {
		super.fireTouchUp();
	

		
	}

	@Override
	protected void destroy() {
		
		super.destroy();

		Gdx.app.log(logstag,"removeAnimating");
		ModelManagment.removeAnimating(this);
		
	}
	
	
	
}