package com.lostagain.nl;

import java.awt.Button;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.me.LocationGUI.Link;
import com.lostagain.nl.me.LocationGUI.Location;
import com.lostagain.nl.me.LocationGUI.LocationsHub;
import com.lostagain.nl.me.creatures.BasicInfovore;
import com.lostagain.nl.me.gui.ConceptGun;
import com.lostagain.nl.me.gui.GUIBar;
import com.lostagain.nl.me.gui.Old_Inventory;
import com.lostagain.nl.me.models.BackgroundManager;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.objects.DataObject;
import com.lostagain.nl.uti.SpiffyGenericTween;
import com.lostagain.nl.uti.SpiffyTweenConstructor;
import com.lostagain.nl.uti.SpiffyVector2Tween;
import com.lostagain.nl.uti.SpiffyVector3Tween;

/** The main exploration view, which lets them see LocationURIs 
 * **/
public class MainExplorationView implements Screen {


	final static String logstag = "ME.MainExplorationView";

	private static boolean LeftButtonDown = false;

	final ME game;

	static Location currentlyOpenLocation;
	

	public static Stage gameStage;		
	public static Stage guiStage;

	public static Vector3 currentPos = new Vector3(PlayersData.homelocationX,PlayersData.homelocationY+2,500f); //note we start high up and zoom in at the start as a little intro
	
	
	
	public static Float CurrentZoom = 1f;
	

	static Float LookAtX = 0f;
	static Float LookAtY = 0f;



	/** Used to tell if the player is at their home pc **/
	static boolean isAtHome=true;

	static Texture customCursor = null;

	static Texture customCursorTest = null;

	Texture dropImage;
	static Texture bucketImage;

	Sound dropSound;
	Music rainMusic;
	public static Camera camera;

	
	Rectangle bucket;
	Array<Rectangle> raindrops;
	long lastDropTime;
	int dropsGathered;

	public static GUIBar usersGUI;

	//static SpiffyGenericTween<Double> currentCameraTweenX;
	//static SpiffyGenericTween<Double> currentCameraTweenY;
	static SpiffyVector3Tween currentCameraTween;
	
	static  Timer cameraTimer = new Timer();
	static  Task cameraTweenTask;

	static boolean dragging = false;
	static  boolean coasting = false;
	
	static private long dragstart;
	static boolean cancelnextdragclick = false; //overrides explorer drags from happening if a user clicks certain panels (ie, scrollpanel)
	static private double MotionDisX = 0;
	static private double MotionDisY = 0;

	Float startdragx_exview = 0.0f;
	Float startdragy_exview = 0.0f;
	
	
	int startdragxscreen = 0;
	int startdragyscreen = 0;
	
    int drag_dis_x = 0;
    int drag_dis_y = 0;
    
    boolean newtouch=true; //if a touch event has just started
    
	boolean touchedAModel = false;
    
 
	//final thing should use perspective
	enum cammode{
		ortha,perspective
	}    
	cammode currentmode = cammode.ortha;

	/*
	private Image testdataobject = new DataObject(StaticSSSNodes.knows,"12");
	private Image testdataobject2 = new DataObject(StaticSSSNodes.asciidecoder,"123456");
	private Image testdataobject3 = new DataObject(StaticSSSNodes.language,"1234567890");
	private Image testdataobject4 = new DataObject(StaticSSSNodes.SecuredBy,"1234567890ABCDEFGH");

*
*/
	
	private static class DistanceFieldShader extends ShaderProgram {
		public DistanceFieldShader () {
			super(Gdx.files.internal("shaders/distancefield.vert"), Gdx.files.internal("shaders/distancefield.frag"));
			if (!isCompiled()) {
				throw new RuntimeException("Shader compilation failed:\n" + getLog());
			}
		}

		/** @param smoothing a value between 0 and 1 */
		public void setSmoothing (float smoothing) {
			float delta = 0.5f * MathUtils.clamp(smoothing, 0, 1);
			setUniformf("u_lower", 0.5f - delta);
			setUniformf("u_upper", 0.5f + delta);
		}
	}
	/*
	public static class MyShaderProvider extends DefaultShaderProvider {
	    @Override
	    protected Shader createShader (Renderable renderable) {
	        if (renderable.material.has(ColorAttribute.Diffuse))
	        	
	            return new MyShader(renderable);
	        else
	            return super.createShader(renderable);
	    }
	}
	*/
	
	DistanceFieldShader testshader = new DistanceFieldShader();
	
	Label test = new Label("test test test");
	
	//controlls the 3d background
	public static  BackgroundManager background = new BackgroundManager();





	public static  LinkedList<Location> LastLocation = new LinkedList<Location>();
	static Location currentTargetLocation;
	
	



	public MainExplorationView(final ME gam) {

		this.game = gam;

    	
		Gdx.app.log(logstag,"setting up stage and stuff");

		//create the game gui interface
		guiStage = new Stage();

		Gdx.app.log(logstag,"setting up stage and stuff..");
		usersGUI = new GUIBar();
		guiStage.addActor(usersGUI);
	
		usersGUI.validate();
	//	guiStage.addActor(usersGUI.STMemoryPop); //temp should be part of gui
		//guiStage.addActor(usersGUI.ConceptGun); //temp should be part of gui
	//	usersGUI.ConceptGun.validate();

		cameraTweenTask = new Task() {
			@Override
			public void run() {

				//currently this sets the co-ordinates directly.
				//to make this smoother we should retrieve the co-ordinate difference
				//and multiply by delta in the draw loop
				//This should give smoother look over non-stable framerates
				//Additionally; The difference in x/y should change to slow down the camera near
				//the end of its movement.
				//Maybe difference * sin for a smooth speed up/slow down curve?
				//double newX=currentCameraTweenX.next();   				   				
				//double newY=currentCameraTweenY.next();
				
				Vector3 newpos = currentCameraTween.next();

				LookAtX = currentPos.x; //one step lag?
				LookAtY = currentPos.y;
 
				
				//currentPos.x=(float) newX;
				//currentPos.y=(float) newY;
				currentPos = newpos;
				
						
				//if (currentCameraTweenX.hasNext()){
				
				if (currentCameraTween.hasNext()){
					//reschedule
					if (!cameraTweenTask.isScheduled()){
						cameraTimer.scheduleTask(this, 0.1f);
					}

				} else {

					//newX=currentCameraTweenX.endPoint();  				   				
					//newY=currentCameraTweenY.endPoint();  
					newpos = currentCameraTween.endPoint();
					
					Gdx.app.log(logstag,"camera at end setting to:"+newpos);   	 

					//currentPos.x=(float) newX;
					//currentPos.y=(float) newY;
					currentPos = newpos;
					
					LookAtX = currentPos.x; 
					LookAtY = currentPos.y;
					
					//targetLocation
					 if (MainExplorationView.LastLocation.size()==0 || currentTargetLocation!=MainExplorationView.LastLocation.getLast()){
						 MainExplorationView.LastLocation.add(currentTargetLocation);
					 }

				}



			}

		};


		Gdx.app.log(logstag,"creating textures");

		//to flip the y co-ordinate 
		//my brain cant take bottom left 0, I need top left zero!
		//   OrthographicCamera guicam= new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//  guicam.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//   guicam.update();
		//  guiStage.getViewport().setCamera(guicam);
		//  guiStage.getCamera().update();

		// load the images for the droplet and the bucket, 64x64 pixels each
		//dropImage = new Texture(Gdx.files.internal("data\\droplet.png"));
		//bucketImage = new Texture(Gdx.files.internal("data\\bucket.png"));
		customCursorTest  = new Texture(Gdx.files.internal("data\\bucket.png"));

		// load the drop sound effect and the rain background "music"
		//dropSound = Gdx.audio.newSound(Gdx.files.internal("data\\drop.mp3"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("data\\rain.mp3"));
		rainMusic.setLooping(true);

		// create the camera and the SpriteBatch
		if ( currentmode == cammode.ortha){
			camera = new OrthographicCamera();

			((OrthographicCamera)camera).setToOrtho(false, 640,480);

		} else {
			camera = new PerspectiveCamera(60, 640,480); // new OrthographicCamera();
		}
		// camera.setToOrtho(false, 1600, 960);
		camera.near=0.5f;
		camera.far=900.0f;
		//camera.translate(10, 25);
		// camera.direction.set(-1, 0, 0);



		//create background


		Gdx.app.log(logstag,"creating game stage");

		gameStage = new Stage();

		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(guiStage);
		multiplexer.addProcessor(gameStage);
		Gdx.input.setInputProcessor(multiplexer);
		
		gameStage.getViewport().setCamera(camera);
		// stage.getCamera().translate(0, 250, 0);

		gameStage.getCamera().update();

		camera.update(true);
		
		Gdx.app.log(logstag,"creating background");
		background.setup();
		

		PlayersData.homeLoc = new Location(PlayersData.computersuri, PlayersData.homelocationX,PlayersData.homelocationY);

	//	addnewlocation( PlayersData.homeLoc.locationsHub,200,500);


		Gdx.app.log(logstag,"centering cameraf");

		centerViewOn( PlayersData.homeLoc,444);

		//test shader
		// String vert = Gdx.files.internal("data/test.vertex.glsl").readString();
		 //   String frag = Gdx.files.internal("data/test.fragment.glsl").readString();
		 //   shader = new DefaultShader(renderable, new DefaultShader.Config(vert, frag));
		 //   shader.init();
		
		
		//add a test label
		gameStage.addActor(test.getModel());

		
		//gameStage.setDebugAll(true);



	}

	public static void setCursor(Texture curimage){

		if (curimage!=null){
			customCursor = curimage;
		} else {
			customCursor = null;
		}

	}

	public static void addnewlocation(LocationsHub newloc,int x,int y) {

		newloc.setPosition(x,y);
		newloc.setClip(false);

		gameStage.addActor(newloc);
		
		//add noise back if closed and added to stage
		if (newloc.closed){
			
			newloc.setClosedBackground();
			
		}
		

	}


	public static void centerViewOn(Location currentlyOpenLocation2){		
		
		coasting = false;
		dragging = false;		
		centerViewOn(currentlyOpenLocation2, currentPos.z,true); //set position in all dimensions but z which we keep the same
				
	}
	public static void centerViewOn(Location currentlyOpenLocation2, float newZ){		
		
		coasting = false;
		dragging = false;		
		centerViewOn(currentlyOpenLocation2, newZ,true); //set position in all dimensions but z which we keep the same
				
	}
	public static void centerViewOn(Location locationcontainer, boolean addLocationToUndo){

		coasting = false;
		dragging = false;		
		centerViewOn(locationcontainer, currentPos.z,addLocationToUndo); //set position in all dimensions but z which we keep the same
	}

	public static void centerViewOn(Location locationcontainer, float newZ, boolean addLocationToUndo){

		//CurrentX=locationcontainer.getCenterX();  //getX()+(locationcontainer.getWidth()/2);
		//CurrentY=locationcontainer.getCenterY(); //getY()+(locationcontainer.getHeight()/2);

		float newX = locationcontainer.getHubsX(Align.center);
		float newY = locationcontainer.getHubsY(Align.center);
		Vector3 dest = new Vector3(newX,newY,newZ);
		

		//asign new tweens
		//currentCameraTweenX = SpiffyTweenConstructor.Create(CurrentX.doubleValue(),newX, 25);
		//currentCameraTweenY = SpiffyTweenConstructor.Create(CurrentY.doubleValue(),newY, 25);
		
		currentCameraTween = new SpiffyVector3Tween(currentPos,dest, 25);
		//Vector3 test1 = new Vector3(0,0,0);
		//Vector3 est2  = new Vector3(10,10,10);
		//SpiffyVector3Tween testtwe = new SpiffyVector3Tween(test1,est2, 25);
		//Gdx.app.log(logstag,"end = "+testtwe.endPoint().x);
		
		currentTargetLocation = locationcontainer;
		//ensure camera animator is running
		if (!cameraTweenTask.isScheduled()){
			Gdx.app.log(logstag,"triggering timer");
			cameraTimer.scheduleTask(cameraTweenTask, 0.1f);

		}

		
		//add the requested location to the  array list, but only if its different from
				//the last location.
		Location lastlocstored =null;;
		try {
			lastlocstored = LastLocation.getLast();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (lastlocstored!=null && addLocationToUndo){
 			
			if (locationcontainer!=lastlocstored){
				
				Gdx.app.log(logstag,"adding="+locationcontainer.locationsnode.toString());

				LastLocation.add(locationcontainer);
				
				for (Location test : LastLocation) {
					
					Gdx.app.log(logstag,"LastLocation="+test.locationsnode.getPLabel());
					
				}
				
			}
			
		} else {
			
			for (Location test : LastLocation) {
				
				Gdx.app.log(logstag,"LastLocation="+test.locationsnode.getPLabel());
				
			}
			LastLocation.add(locationcontainer);
			
		}
		
		
	}

	@Override
	public void render(float delta) {


		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);



		// tell the camera to update its matrices.
		//camera.translate(50, 0);

		//stage.getCamera().translate(50, 0, 0);

		//camera.rotate(45, 0, 0, 1);
		//camera.position.set(CurrentX, CurrentY, CurrentZ);
		camera.position.set(currentPos);

		
		// create the camera and the SpriteBatch
		if ( currentmode == cammode.ortha){        	 
					
			
			
			float newzoom = (0.0133333315344f*currentPos.z)-4.9199985961765f;
			CurrentZoom = newzoom;
			((OrthographicCamera)camera).zoom = CurrentZoom;
			

			//Gdx.app.log(logstag,"CurrentZoom="+CurrentZoom);
		} 


		// camera.lookAt(LookAtX, LookAtY, -10);

		camera.update();

		//Note we draw this here because its in the background and should appear behind the other elements
		//Each render is sort of like a "layer" and appears in the order they are rendered
		//regardless of 3d positions within that layer
		background.updateAnimatedBacks(delta);
		ModelManagment.updateAnimatedBacks(delta);//--
		
		usersGUI.ConceptGun.update(delta);
		background.modelBatch.begin( camera);
		background.modelBatch.render(ModelManagment.allModelInstances 	);
		background.modelBatch.end();	
	
		testshader.begin();
	//	testshader.setUniformMatrix("u_projTrans", camera.getProjectionMatrix());
		testshader.setUniformi("u_texture", 0);
		ModelManagment.allModelInstances.get(0).model.meshes.get(0).render(testshader,  GL20.GL_TRIANGLES);
		testshader.end();
		
		gameStage.getViewport().setCamera(camera);

		gameStage.act(delta); //Gdx.graphics.getDeltaTime()
		gameStage.draw();
		
		
		//  game.batch.setProjectionMatrix(camera.combined);

		// process user input
		//  if (Gdx.input.isTouched()) {


		//  Vector3 touchPos = new Vector3();
		//  touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		//  camera.unproject(touchPos);
		//  }
		
		if (Gdx.input.isTouched()) {
			
			//test if we clicked a 3d model
			if (newtouch ){
				
				//trigger concept gun
				usersGUI.ConceptGun.fireAt(Gdx.input.getX(), Gdx.input.getY());				
				
				Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
				touchedAModel = ModelManagment.testForHit(ray);
				Gdx.app.log(logstag,"_-touch down on a model-_");
					
			}
			
			
			
			if (!dragging && !cancelnextdragclick && !touchedAModel && Old_Inventory.currentlyHeld == null){
				dragging = true;
				dragstart = TimeUtils.millis();
				
				
				startdragxscreen = Gdx.input.getX();
				startdragyscreen = Gdx.input.getY();
								
				Gdx.app.log(logstag,"x="+startdragxscreen+",y="+startdragyscreen);
				
				startdragx_exview = currentPos.x;
				startdragy_exview = currentPos.y;
				
			} else if (!cancelnextdragclick && !touchedAModel) {
				
				 drag_dis_x = Gdx.input.getX()-startdragxscreen;
				 drag_dis_y = Gdx.input.getY()-startdragyscreen;
				
				 currentPos.x = startdragx_exview-drag_dis_x;
				 currentPos.y = startdragy_exview+drag_dis_y;
				 
			}
			

			newtouch = false;

		} else if (dragging == true){
			Gdx.app.log(logstag,"setting drag to false click");
			//if no longer touching stop dragging
			dragging = false;
			
			//last displacement on mouse/touch up
			 drag_dis_x = Gdx.input.getX()-startdragxscreen;
			 drag_dis_y = Gdx.input.getY()-startdragyscreen;
			
			//we can record the start/end of the drag times to get drag duration
			//with this and the displacement we can get pixels per second, and thus
			//form a movement
			
			long period = TimeUtils.millis() - dragstart;
			Gdx.app.log(logstag,"\n drag displacement time:"+period);
	//
//			// displacement per unit of time;
			
			
			if (!coasting){
				coasting = true;
				//if we are not coasting
				MotionDisX = ((double) drag_dis_x / (double) period) * 50;
				MotionDisY = ((double) drag_dis_y / (double) period) * 50;
			} else {
				//if coasting in the same direction we add to the speed
				if (Math.signum(MotionDisX)==Math.signum(drag_dis_x)){
					MotionDisX = MotionDisX+((double) drag_dis_x / (double) period) * 50;
				} else {
					//we overwrite
					MotionDisX = ((double) drag_dis_x / (double) period) * 50;
				}
				
				//if coasting in the same direction we add
				if (Math.signum(MotionDisY)==Math.signum(drag_dis_y)){
					MotionDisY = MotionDisY+((double) drag_dis_y / (double) period) * 50;
				} else {
					MotionDisY = ((double) drag_dis_y / (double) period) * 50;
				}
				
			}
			Gdx.app.log(logstag,"MotionDisX:"+MotionDisX);
			Gdx.app.log(logstag,"MotionDisY:"+MotionDisY);
			
		} else if (cancelnextdragclick) {

			Gdx.app.log(logstag,"reenable click");
			cancelnextdragclick = false;
		}
		
		if (touchedAModel && !Gdx.input.isTouched()){
			Gdx.app.log(logstag,"_-released touch on a model-_");
			touchedAModel=false;
			ModelManagment.untouchAll();
		}
		
		if (!Gdx.input.isTouched() && !newtouch){
			
			Gdx.app.log(logstag,"_-released touch-_");
			float tx = Gdx.input.getX();
			float ty = -Gdx.input.getY()+gameStage.getHeight();
			
			Actor test = usersGUI.hit(tx,ty, false);
			if (test!=null){
				Gdx.app.log(logstag,"__"+test.getClass().getName());
			}
		}
		
		if (!Gdx.input.isTouched()){
			newtouch = true;
		}
		
		if (coasting){
			//update the position based on the speed of the coast
			
			MotionDisX = (MotionDisX / (1+(0.8f*delta)));
			MotionDisY = (MotionDisY / (1+(0.8f*delta)));
			
			//Gdx.app.log(logstag,"\n slow by:"+(1+(0.8f*delta)));
			
			// stop
			Boolean isMoving = false;
			if ((MotionDisX < 3) && (MotionDisX > -3)) {
				MotionDisX = 0;
			} else {
				isMoving = true;
			}
			if ((MotionDisY < 3) && (MotionDisY > -3)) {
				MotionDisY = 0;
			} else {
				isMoving = true;
			}

			if (!(isMoving)) {
				Gdx.app.log(logstag,"\n stoped");
				coasting = false;

			} else {
				currentPos.x = (float) (currentPos.x-(MotionDisX*delta));
				currentPos.y = (float) (currentPos.y+(MotionDisY*delta));
			}
		}
		
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		{
			currentPos.x = currentPos.x-(200* Gdx.graphics.getDeltaTime());        	
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		{        	
			currentPos.x = currentPos.x+(200* Gdx.graphics.getDeltaTime());
		}

		if (Gdx.input.isKeyPressed(Keys.UP))
		{
			currentPos.y = currentPos.y+(200* Gdx.graphics.getDeltaTime());        	
		}

		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{        	
			currentPos.y = currentPos.y-(200* Gdx.graphics.getDeltaTime());
		}
		if (Gdx.input.isKeyPressed(Keys.Z))
		{
			currentPos.z = currentPos.z+(150* Gdx.graphics.getDeltaTime()); 

			if ( currentmode == cammode.ortha){
			//	CurrentZoom = CurrentZoom +(2* Gdx.graphics.getDeltaTime()); 

				float newzoom = (0.0133333315344f*currentPos.z)-4.9199985961765f;	//the formular works out a ratio between zoom and z position			
				CurrentZoom = newzoom;
				
				//Gdx.app.log(logstag,currentPos.z+","+CurrentZoom);
			}
		}

		if (Gdx.input.isKeyPressed(Keys.A) && currentPos.z>0.5)
		{        	
			currentPos.z = currentPos.z-(150* Gdx.graphics.getDeltaTime());

			if ( currentmode == cammode.ortha){
				
				float newzoom = (0.0133333315344f*currentPos.z)-4.9199985961765f;	//the formula works out a ratio between zoom and z position			
				CurrentZoom = newzoom;
				//CurrentZoom = CurrentZoom -(2* Gdx.graphics.getDeltaTime()); 

				//Gdx.app.log(logstag,"CurrentZoom="+CurrentZoom+" = "+currentPos.z);

				//Gdx.app.log(logstag,currentPos.z+","+CurrentZoom);
				//0.002780689 = 369.2085
				
				//currentPos.z-369.2085
				//900.4018 -369.2085=  531.2
				
			}
		}

		if (!Gdx.input.isButtonPressed(Buttons.LEFT) && LeftButtonDown){
			
			//Old_Inventory.dropHeldItem();
			LeftButtonDown=false;
		}
		
		if (Gdx.input.isButtonPressed(Buttons.LEFT)){	
			
			LeftButtonDown=true;
		}

		//now update the gui

		guiStage.act(Gdx.graphics.getDeltaTime());
		guiStage.draw();


		// begin a new batch (for interface text and the cursor)
		ME.batch.begin();

		if (customCursor!=null){

			float xc = Gdx.input.getX();
			float yc = -Gdx.input.getY()+gameStage.getHeight();

			ME.batch.draw(customCursor, (xc-(customCursor.getWidth()/2)), (yc-(customCursor.getHeight()/2)));

	//		Gdx.app.log(logstag,"customCursor.."+xc+","+yc+" height="+ (customCursor.getHeight()/2));
			
			
		}

		//    game.font.draw(game.batch, "Drops Collected:: " + dropsGathered, 0, 480);
		ME.font.draw( ME.batch, "X:: " + currentPos.x+" Y:: " + currentPos.y+"...."+dragging+"(x="+drag_dis_x+",y="+drag_dis_y+")", 10,45);
		ME.font.draw( ME.batch, "Z:: " + currentPos.z, 10, 25);

		//    game.batch.draw(bucketImage, bucket.x, bucket.y);
		ME.batch.end();

	}

	@Override
	public void resize(int width, int height) {
		

		Gdx.app.log(logstag,"resizeing to.."+width+","+height);
		
		
		if ( currentmode == cammode.ortha){
			camera = new OrthographicCamera(width, width);

			((OrthographicCamera)camera).setToOrtho(false, width, width);

		} else {
			camera = new PerspectiveCamera(60,width,height); // new OrthographicCamera();
		}
		
		
		//update sprite batch for new resolution 
		Matrix4 viewMatrix = new Matrix4();
	    viewMatrix.setToOrtho2D(0, 0,width, height);
	    ME.batch.setProjectionMatrix(viewMatrix);
		

		//camera = new PerspectiveCamera(60,width,height); //new OrthographicCamera(width, height);
		camera.translate(height/2,width/2, 0);
		camera.near=0.5f;
		camera.far=900.0f;
		camera.update();
		
		gameStage.getViewport().setCamera(camera);
		gameStage.getViewport().setScreenSize(width, height);
		gameStage.getViewport().setWorldSize(width, height);
		gameStage.getViewport().update(width, height, true);  

		guiStage.getViewport().setScreenSize(width, height);
		guiStage.getViewport().setWorldSize(width, height);
		guiStage.getViewport().update(width, height, true);
		

		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(guiStage);
		multiplexer.addProcessor(gameStage);
		Gdx.input.setInputProcessor(multiplexer);

		usersGUI.ConceptGun.invalidateHierarchy();
		usersGUI.ConceptGun.validate();
		usersGUI.setNeedsRepopulating(true); //temp fix for layout resizing, this should be handled correctly by splitting widget positioning from widget adding and letting the invalidate/validate handle the repositioning
		
		usersGUI.invalidateHierarchy();
		usersGUI.validate();
		
		Gdx.app.log(logstag,"height="+guiStage.getHeight());
		Gdx.app.log(logstag,"w="+guiStage.getWidth());

		Gdx.app.log(logstag,"gameStage height="+gameStage.getHeight());
		Gdx.app.log(logstag,"gameStage w="+gameStage.getWidth());



	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		rainMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		gameStage.dispose();

		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();

		background.dispose();

	}


	public static void gotoLocation(SSSNode linksToThisPC) {



		//flag if the user is home
		if (linksToThisPC.equals(PlayersData.computersuri)){
			isAtHome = true;
		} else {
			isAtHome = false;		  
		}

		//get the node screen.
		//This will automatically check if it already exists
		//else it will create a new one
		Location screen = Location.getLocationHub(linksToThisPC);
		


		currentlyOpenLocation = screen;
		centerViewOn(currentlyOpenLocation);



	}
/*
	public static void toggleDragOnOff(){
		
			cancelnextdragclick=!cancelnextdragclick;
				
	}
	
	
	public static void enableDrag(){
		
		cancelnextdragclick = false;
		
		
	}*/
	
	public static void disableDrag(){
		
		dragging = false;
		cancelnextdragclick = true;
		
		
	}

	public static void gotoHomeLoc() {

		centerViewOn( PlayersData.homeLoc);

	}

	public static void gotoLastLocation() {

		Gdx.app.log(logstag,"goto to last location");
		
		for (Location test : LastLocation) {
			
			Gdx.app.log(logstag,"LastLocations="+test.locationsnode.getPLabel());
			
		}
		
		if (LastLocation.size()==0){
			return;
		}
		

		//remove current location (which should be the last added)
		LastLocation.removeLast();			
		
		if (LastLocation.size()==0){
				return;
		}
		
		
		//goto the last one if theres one
		Location requested = LastLocation.getLast(); //gwt can't use peeklast
		

		if (requested!=null){
			
			Gdx.app.log(logstag,"last location is:"+requested.locationsnode.getPLabel());		
			LastLocation.removeLast();			
			centerViewOn( requested,false );
			
		} else {

			Gdx.app.log(logstag,"no last location");
			
		}
	}

	public static void addnewdrop(DataObject newdrop, double x, double y) {

		 Gdx.app.log(logstag,"_____________:dropping ");
		 
		//Image dropimage = new Image(newdrop);		
		newdrop.setPosition((int)x - (newdrop.getWidth()/2),(int)y- (newdrop.getHeight()/2));
		
		double deg = (Math.random()*30)-15; 		
		newdrop.setRotation((float) deg);
		
		//ensure its clickable (else how will you pick it up?)
		
		newdrop.setTouchable(Touchable.enabled);
			
		
		gameStage.addActor(newdrop);	
	}

	
	public static Vector2 getCurrentStageCursorPosition() {

		float xc = Gdx.input.getX();
		float yc = Gdx.input.getY();//-gameStage.getHeight();
		
		Vector2 vec = new Vector2(xc,yc);
		 gameStage.screenToStageCoordinates(vec);
		
	//	 Gdx.app.log(logstag,"_____________:yc "+yc+"="+vec.y);
		
		return vec;
	}

	public static Vector2 getCurrentCursorScreenPosition() {

		float xc = Gdx.input.getX();
		float yc = Gdx.input.getY();//-gameStage.getHeight();
		
		Vector2 vec = new Vector2(xc,yc);
		
	//	 Gdx.app.log(logstag,"_____________:yc "+yc+"="+vec.y);
		
		return vec;
	}
}