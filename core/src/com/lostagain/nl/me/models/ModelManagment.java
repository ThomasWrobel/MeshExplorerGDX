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
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.lostagain.nl.me.creatures.Creature;
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
	        

			

	    	NoiseAnimation testNoise = new NoiseAnimation();
			Gdx.app.log(logstag,"creating testbounc");
			testNoise.create();
			
			FileHandle imageFileHandle3 = Gdx.files.internal("data/badlogic.jpg"); 
	        
	        //Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
	        Texture blobtexture = new Texture(imageFileHandle3);
	      //  blobtexture.bind(0);
	                
	        
	        Material blob = new Material(
	        		ColorAttribute.createDiffuse(Color.BLUE), 
					ColorAttribute.createSpecular(Color.WHITE),
					new BlendingAttribute(1f), 
					FloatAttribute.createShininess(16f));

	        blob.set(TextureAttribute.createDiffuse(blobtexture));
			
			
			
			//Model  model1 = modelBuilder.createSphere(150, 150, 150, 20, 20,
			//		blob,Usage.Position | Usage.Normal | Usage.TextureCoordinates );
			
			//String alias = model1.meshes.get(0).getVertexAttribute(Usage.TextureCoordinates).alias;
	        
		//	Model model1 = ModelMaker.createRectangleAt(0, 0, 30, 200, 200, Color.BLACK, blob);
			

			//Gdx.app.log(logstag,"aliasaliasaliasalias = "+alias);
	//

			//model2.meshes.get(0).getVertexAttribute(Usage.TextureCoordinates).alias = "a_texCoord";
			
			//  Model model3 = createRectangle(-10f,500f,10f,-500f,0f);


			//  ModelInstance model4 = createRectangleAt(50f,50f,10f,200f,0f);

			ModelInstance instance = ModelMaker.createRectangleAt(0, 0, 30, 200, 200, Color.BLACK, blob); // new ModelInstance(model1); 
			
			instance.userData = MyShaderProvider.shadertypes.conceptbeam;
			
			ModelManagment.addmodel(instance);

			//CameraOverlay = MessyModelMaker.addNoiseRectangle(0,0,300,300,true);
			//CameraOverlay.materials.get(0).set( new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.0f));
			
			//CameraOverlay.userData =MyShaderProvider.shadertypes.noise; 
			//instance2.userData = MyShaderProvider.shadertypes.distancefield;
			//ModelManagment.addmodel(CameraOverlay);
			
			
			
			
			
			//for some reason transparency's dont work till we add something with a transparent texture for the first time

	    	Texture texture = new Texture(Gdx.files.internal("data/dfield.png"), false);
			texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
			
			Model model2 = modelBuilder.createBox(220f, 220f, 15f, 
					new Material(TextureAttribute.createDiffuse(texture),new BlendingAttribute(0.6f)),
					Usage.Position | Usage.Normal | Usage.TextureCoordinates);
	        ModelInstance instance3 = new ModelInstance(model2);      

			ModelManagment.addmodel(instance3);
			
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
		
        
		Gdx.app.log(logstag,"_-testing ray in :"+ray.origin.x+" models");
		Gdx.app.log(logstag,"_-testing ray in :"+ray.origin.y+" models");
		
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

            closesttouched.fireTouchDown();
            mousedownOn.add(closesttouched);
            
	    	return true;
	    }
		return false;
	}

	public static void addHitable(hitable model) {
		hitables.add(model);
		
	}

	public static void removeHitable(hitable model) {
		
		hitables.removeValue(model,true);

		
		
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
