package com.lostagain.nl.me.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.lostagain.nl.me.creatures.Creature;
import com.lostagain.nl.shaders.ConceptBeamShader;
import com.lostagain.nl.shaders.MyShaderProvider;

public class ModelManagment {

	private static String logstag="ME.ModelManagment";
	
	//might need to be divided into transparent and non-transparent at some point
	//for optimization
	public static Array<ModelInstance> allModelInstances = new Array<ModelInstance>();
	
	public static MyShaderProvider myshaderprovider = new MyShaderProvider();
	ModelBuilder modelBuilder = new ModelBuilder();


	public ModelBatch modelBatch;
	//Using Array here as its a GDX thing rather then ArrayList...not sure what difference it makes
	/**all hitable models **/
	public static Array<hitable> hitables = new Array<hitable>();
	public static Array<hitable> mousedownOn = new Array<hitable>();

	/**all model with texture animations **/
	public static Array<Animating> animatingobjects = new Array<Animating>();

	/**all models currently moving **/
	public static Array<Creature> movingObjects = new Array<Creature>();
	
	public static void addmodel(ModelInstance model) {
		allModelInstances.add(model);
		
	}

	public static void removeModel(ModelInstance model) {
		allModelInstances.removeValue(model,true);
	}

public void updateAnimatedBacks(float deltatime){
		
		
		//replaced with noise shader !
		/*
		TextureRegion currentimage = testNoise.getKeyFrame(deltatime);
		
		for (ModelInstance instance : animatedbacks) {
			
			TextureAttribute attribute = instance.materials.get(0).get(TextureAttribute.class, TextureAttribute.Diffuse);
			
			if (attribute!=null){			
				attribute.set(currentimage );		
			} else {
				Gdx.app.log(logstag,"________************________________attribute is null:");
			}
					
		}*/
		
		
	}
	public void setup(){

	       // String vert = Gdx.files.internal("shaders/test.vertex.glsl").readString();//"shaders/distancefield.vert"
	       // String frag = Gdx.files.internal("shaders/test.fragment.glsl").readString();
	        
			//modelBatch = new ModelBatch(vert,frag);
	//	 MyShaderProvider myshaderprovider = new MyShaderProvider();
		
	        modelBatch = new ModelBatch(myshaderprovider);
	        

	        //First we add one object at the center with a defaultshader used
	       // Its VERY important to use a defaultshader object as the first thing created, else
	        //the default shader will get confused and think it can render things with other shaders too.
	        //This is because to figure out if it can render something it always compared to the first object it gets.
	        
	        //We also have to have transparence on the first shader we make else transparency wont be supported at all
	        Material testmaterial = new Material(
	        		ColorAttribute.createDiffuse(Color.BLUE), 
					ColorAttribute.createSpecular(Color.WHITE),
					new BlendingAttribute(1f), 
					FloatAttribute.createShininess(16f));
	        

	        ModelInstance centermaker = ModelMaker.createCenterPoint(testmaterial);

			
			Renderable renderableWithoutAttribute = new Renderable();
			centermaker.getRenderable(renderableWithoutAttribute);
			
	    	DefaultShader test = new DefaultShader(renderableWithoutAttribute);
	    	
	        Material testmaterial2 = new Material(
	        		ColorAttribute.createDiffuse(Color.BLUE), 
					ColorAttribute.createSpecular(Color.WHITE),
					new BlendingAttribute(1f), 
					FloatAttribute.createShininess(16f),new ConceptBeamShader.ConceptBeamAttribute(0.4f,Color.BLUE,Color.WHITE));

	    	 

		
	    	 
	    	 
	    		ModelInstance instance = ModelMaker.createRectangleAt(0, 0, 30, 200, 200, Color.BLACK, testmaterial2); // new ModelInstance(model1); 
	    		instance.userData = MyShaderProvider.shadertypes.conceptbeam;
	    		Renderable renderableWithAttribute = new Renderable();
			    instance.getRenderable(renderableWithAttribute);
	    	
	    	Boolean defaultCanRender = test.canRender(renderableWithAttribute);

			ModelManagment.addmodel(centermaker);
			ModelManagment.addmodel(instance);

		  	Gdx.app.log(logstag,"default created with attribute = "+defaultCanRender);
			
			
			
			
	        //A lot of earlier code used for creating and debugging objects and shaders is below
			//While its not critical, please leave for now as its a good reference for cutting and pasting tests

	    	//NoiseAnimation testNoise = new NoiseAnimation();
			//Gdx.app.log(logstag,"creating testbounc");
			//testNoise.create();
			
			//FileHandle imageFileHandle3 = Gdx.files.internal("data/badlogic.jpg"); 
	        
	        //Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
	       // Texture blobtexture = new Texture(imageFileHandle3);
	      //  blobtexture.bind(0);
	                
	        /*
	        Material testmaterial = new Material(
	        		ColorAttribute.createDiffuse(Color.BLUE), 
					ColorAttribute.createSpecular(Color.WHITE),
					new BlendingAttribute(1f), 
					FloatAttribute.createShininess(16f),new ConceptBeamShader.ConceptBeamAttribute(0.4f,Color.BLUE,Color.WHITE));

	       // testmaterial.set(TextureAttribute.createDiffuse(blobtexture));
	        
	    	ModelInstance instance = ModelMaker.createRectangleAt(0, 0, 30, 200, 200, Color.BLACK, testmaterial); // new ModelInstance(model1); 
			Renderable renderableWithAttribute = new Renderable();
			instance.getRenderable(renderableWithAttribute);
			
	    	DefaultShader test = new DefaultShader(renderableWithAttribute);
	    	Boolean defaultCanRender = test.canRender(renderableWithAttribute);
	    	//If true this means a default shader created with a renderable with the custom attribute will by rendered by DefaultShader
	    	Gdx.app.log(logstag,"default created with attribute = "+defaultCanRender);
	    	
	    	
	    	ModelInstance instance2 = ModelMaker.createRectangleAt(0, 0, 30, 200, 200, Color.BLACK,null);
	    	Renderable renderableWithoutAttribute = new Renderable();
	    	instance2.getRenderable(renderableWithoutAttribute);
			
	    	DefaultShader test2 = new DefaultShader(renderableWithoutAttribute);
	    	Boolean defaultCanRender2 = test2.canRender(renderableWithAttribute); //now we test if the shader created without the attribute will render with one.
	    	//If true this means a default shader will think it can render 
	    	Gdx.app.log(logstag,"default created without attribute = "+defaultCanRender2);
	    	
			
			
			
			//Model  model1 = modelBuilder.createSphere(150, 150, 150, 20, 20,
			//		blob,Usage.Position | Usage.Normal | Usage.TextureCoordinates );
			
			//String alias = model1.meshes.get(0).getVertexAttribute(Usage.TextureCoordinates).alias;
	        
		//	Model model1 = ModelMaker.createRectangleAt(0, 0, 30, 200, 200, Color.BLACK, blob);
			

			//Gdx.app.log(logstag,"aliasaliasaliasalias = "+alias);
	//

			//model2.meshes.get(0).getVertexAttribute(Usage.TextureCoordinates).alias = "a_texCoord";
			
			//  Model model3 = createRectangle(-10f,500f,10f,-500f,0f);


			//  ModelInstance model4 = createRectangleAt(50f,50f,10f,200f,0f);

			
			
			instance.userData = MyShaderProvider.shadertypes.conceptbeam;
			
			ModelManagment.addmodel(instance);

			//CameraOverlay = MessyModelMaker.addNoiseRectangle(0,0,300,300,true);
			//CameraOverlay.materials.get(0).set( new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.0f));
			
			//CameraOverlay.userData =MyShaderProvider.shadertypes.noise; 
			//instance2.userData = MyShaderProvider.shadertypes.distancefield;
			//ModelManagment.addmodel(CameraOverlay);
			
			
			
			*/
			
			//for some reason transparency's dont work till we add something with a transparent texture for the first time

		  	/*
	    	Texture texture = new Texture(Gdx.files.internal("data/dfield.png"), false);
			texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
			
			Model model2 = modelBuilder.createBox(220f, 220f, 15f, 
					new Material(TextureAttribute.createDiffuse(texture),new BlendingAttribute(0.6f)),
					Usage.Position | Usage.Normal | Usage.TextureCoordinates);
	        ModelInstance instance3 = new ModelInstance(model2);      

			ModelManagment.addmodel(instance3);
			*/
		  	
			//instance.transform.setToTranslation(200,500,10);

			//  environment = new Environment();
			//  environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
			// environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}
	
	public static boolean testForHit(Ray ray) {
		
		Gdx.app.log(logstag,"_-testing hit in :"+hitables.size+" models");


		// 
		 
		// test.getTransform().getTranslation(position);
      //  position.add(test.getCenter());

		//Gdx.app.log(logstag,"_-testing hit in :"+position.x +" x");
		//Gdx.app.log(logstag,"_-testing hit in :"+position.y +" y");
		
        
		Gdx.app.log(logstag,"_-testing ray at :"+ray.origin.x+","+ray.origin.y);
		
		
		Vector3 position = new Vector3();
		int result = -1;
	    float distance = -1;
	    hitable closesttouched = null;
	    
	    
	    for (int i = 0; i < hitables.size; ++i) {
	    	
	        final hitable instance = hitables.get(i);
	 
	        //instance.getTransform().getTranslation(position);
	      //  position.add(instance.getCenter());
	        
	        float dist2 = ray.origin.dst2(position);
	        if (distance >= 0f && dist2 > distance)
	            continue;
	 
	        if (Intersector.intersectRaySphere(ray, instance.getCenter(), instance.getRadius(), null)) {
	    		Gdx.app.log(logstag,"_hit in :"+i);
	    		
	            result = i;
	            distance = dist2;
	            
	             closesttouched = instance;
	            
	            
	            
	        }
	        
	        
	        
	        
	    }
	 
	    if (result!=-1){
	    	Gdx.app.log(logstag,"_closest hit was_"+result);
            closesttouched.fireTouchDown();
            mousedownOn.add(closesttouched);
            
	    	return true;
	    }
		return false;
	}

	public static void addHitable(hitable model) {
		hitables.add(model);
		
	}

	public static boolean removeHitable(hitable model) {
		
		return hitables.removeValue(model,true);

		
		
	}
	public static void addMoving(Creature model) {
		movingObjects.add(model);
		
	}

	public static void removeMoving(Creature model) 
	{
		
		movingObjects.removeValue(model,true);
		
	}
	public static void addAnimating(Animating model) {
		animatingobjects.add(model);
		
	}

	public static void removeAnimating(Animating model) 
	{
		
		animatingobjects.removeValue(model,true);
		
	}
	
	public static void untouchAll() {

		Gdx.app.log(logstag,"_-mousedownOn size to untouch:"+mousedownOn.size);
		
		for (hitable model : mousedownOn) {
			model.fireTouchUp();		
			
		}
		mousedownOn.clear();
		//Gdx.app.log(logstag,"_----------removing md:"+mousedownOn.size);
		//Boolean removedtest = mousedownOn.removeValue(model,true);
		        
		//Gdx.app.log(logstag,"_-mousedownOn:"+mousedownOn.size);
	}
	
	public void dispose() {


		modelBatch.dispose();

	}


	public static void updateObjectMovementAndFrames(float deltatime){
					
		for (Animating instance : animatingobjects) {
			
			instance.updateAnimationFrame(deltatime);
			
		}
		
	    for (Creature instance : movingObjects) {
			
			instance.updatePosition(deltatime);			
			
		}
		
	}
	
}
