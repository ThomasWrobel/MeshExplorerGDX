package com.lostagain.nl.me.models;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.me.creatures.Creature;
import com.lostagain.nl.me.domain.MEDomain;
import com.lostagain.nl.shaders.ConceptBeamShader;
import com.lostagain.nl.shaders.MyShaderProvider;

public class ModelManagment {

	private static String logstag="ME.ModelManagment";

	/** All the 3d models that appear BEHIND the sprite ones **/
	public static Array<ModelInstance> allBackgroundInstances = new Array<ModelInstance>();

	/** All the 3d models that appear INFRONT of the sprite ones **/
	public static Array<ModelInstance> allForgroundInstances = new Array<ModelInstance>();

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


	static public enum RenderOrder {
		behindStage,infrontStage,zdecides
	}


	/** adds the model to the render list.
	 * At this time it chooses if its a background or forground object based on its Z position
	 * If Z is less then the stage Z (0) its behind
	 * If its more then its 	in front**/
	public static void addmodel(ModelInstance model, RenderOrder order) {	
		
		//ignore if present already
		if (allBackgroundInstances.contains(model, true) || allForgroundInstances.contains(model, true)){
			Gdx.app.log(logstag,"________model already on a render list");
			return;
		}
		

		float Z = model.transform.getValues()[Matrix4.M23];
		Gdx.app.log(logstag,"z = "+Z);

		if (order == RenderOrder.behindStage){
			allBackgroundInstances.add(model);
			return;
		}

		if (order == RenderOrder.infrontStage){
			allForgroundInstances.add(model);
			return;
		}

		//depending on if we are above/below the stage position we add it accordingly		
		if (Z<5){
			allBackgroundInstances.add(model);
		} else {
			allForgroundInstances.add(model);
		}
		

	}

	public static void removeModel(ModelInstance model) {
		allBackgroundInstances.removeValue(model,true);
		allForgroundInstances.removeValue(model,true);
	}

/**
 * tests the sort order of foreground objects
 * @param deltatime
 */
	//public void testSortOrder(float deltatime){
		
	//	modelBatch.getRenderableSorter().sort(MainExplorationView.camera,);
		
		
	//}


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
				ColorAttribute.createDiffuse(Color.RED), 
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
				FloatAttribute.createShininess(16f),
				new ConceptBeamShader.ConceptBeamAttribute(0.4f,Color.BLUE,5.0f,Color.WHITE)
				);






		ModelInstance instance = ModelMaker.createRectangleAt(0, 0, 30, 200, 200, Color.BLACK, testmaterial2); // new ModelInstance(model1); 
		instance.userData = MyShaderProvider.shadertypes.conceptbeam;
		Renderable renderableWithAttribute = new Renderable();
		instance.getRenderable(renderableWithAttribute);

		//	Boolean defaultCanRender = test.canRender(renderableWithAttribute);

		ModelManagment.addmodel(centermaker,RenderOrder.infrontStage);
		ModelManagment.addmodel(instance,RenderOrder.infrontStage);

		//	Gdx.app.log(logstag,"default created with attribute = "+defaultCanRender);




		//--------------			
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



		ModelInstance colortest = ModelMaker.createRectangleAt(0, -900, 130, 200, 200, Color.BLACK, testmaterial3); 
		ModelManagment.addmodel(colortest,RenderOrder.infrontStage);


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

	/**
	 * 
	 * 
	 * New version;
	 * 
	 * 1. Get everything under cursor
	 *    - remember highest hitblocker
	 *    - remember highest hit
	 * 
	 *  2. If not penetrating test highest hit against highest clickblocker
	 *     If higher, then hit and exist.
	 *     
	 *    
	 * 3. If penetrating Loop over potential hits
	 *  - count as hit if higher then highest clickblocker
	 * 
	 * @param ray
	 * @param hitsPenetrate  - if the hits penetrate it means everything under the cursor will count as hit, not just the top.
	 * @return
	 */
	public static boolean testForHits(Ray ray,boolean hitsPenetrate) {

		Gdx.app.log(logstag,"_-testing hit in :"+hitables.size+" models");
		Gdx.app.log(logstag,"_-testing ray at :"+ray.origin.x+","+ray.origin.y);


		Vector3 position = new Vector3();
		
		ArrayList<hitable> everyThingUnderCursor = new ArrayList<hitable>();
		hitable closestNonBlockerTouched = null;	    
		hitable closestBlockerTouched = null;

		for (int i = 0; i < hitables.size; ++i) {

			final hitable instance = hitables.get(i);

			//instance.getTransform().getTranslation(position);
			//position.add(instance.getCenter());
			position = instance.getCenter();

			float dist2 = ray.origin.dst2(position);


			//first check if it hits at all. We base this on the hitables internal tester
			//this lets different hitables use different intersect types (ie, radius, boundingbox, polygon etc)
			
		//	if (Intersector.intersectRaySphere(ray, instance.getCenter(), instance.getRadius(), null)) {
			
			if (!instance.rayHits(ray)){
				//if it didn't hit we can just skip to the next thing to test
				continue;
			}

			

			Gdx.app.log(logstag,"_hit in :"+i);
			
			
				//set last hit range
				instance.setLastHitsRange(dist2);


				//if its a blocker we see if its higher then the last blocker
				if (instance.isBlocker()){

					//if none set just continue
					if (closestBlockerTouched==null){
						closestBlockerTouched=instance;
						continue;
					}
					//else we test if its closer
					if (instance.getLastHitsRange()<closestBlockerTouched.getLastHitsRange()){
						closestBlockerTouched=instance;
						continue;
					}


				} else {

					//add to every not hitable under cursor list
					everyThingUnderCursor.add(instance);

					//if its not a blocker we do the same tests for normal objects

					//if none set just continue
					if (closestNonBlockerTouched==null){
						closestNonBlockerTouched=instance;
						continue;
					}
					//else we test if its closer
					if (instance.getLastHitsRange()<closestNonBlockerTouched.getLastHitsRange()){
						closestNonBlockerTouched=instance;
						continue;
					}


				}







		}

		//now we have all the things potential hit, we check for actual hits

		//If we arnt penetrating we just see if the highest hitable is higher then the highest blocker
		//if so we hit it and exit
		if (!hitsPenetrate && closestNonBlockerTouched!=null){

			if (closestBlockerTouched==null){
				closestNonBlockerTouched.fireTouchDown();
				//	mousedownOn.add(closestBlockerTouched); //hmm....not sure if we should use this anymore
				return true;
			}

			if (closestBlockerTouched.getLastHitsRange()<closestNonBlockerTouched.getLastHitsRange()){

				closestNonBlockerTouched.fireTouchDown();
				//	mousedownOn.add(closestBlockerTouched); //hmm....not sure if we should use this anymore
				return true;
			}
		}

		//if not we loop and hit anything above the highest blocker

		for (hitable instance : everyThingUnderCursor) {



			if (closestBlockerTouched==null || instance.getLastHitsRange()<closestBlockerTouched.getLastHitsRange()){

				instance.fireTouchDown();





			}


		}

		// if (highest!=null){
		//  	closestNonBlockerTouched.fireTouchDown();
		//mousedownOn.add(closestBlockerTouched); //hmm....not sure if we should use this anymore
		//	return true;
		//}

		//    if (result!=-1 && !hitsPenetrate){
		//    	Gdx.app.log(logstag,"_closest hit was_"+result);
		//       closestBlockerTouched.fireTouchDown();
		//       mousedownOn.add(closestBlockerTouched);

		//   	return true;
		//   }
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
