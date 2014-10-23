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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.me.LocationGUI.GUIBar;
import com.lostagain.nl.me.LocationGUI.Inventory;
import com.lostagain.nl.me.LocationGUI.Link;
import com.lostagain.nl.me.LocationGUI.Location;
import com.lostagain.nl.me.LocationGUI.LocationsHub;
import com.lostagain.nl.me.creatures.BasicInfovore;
import com.lostagain.nl.me.models.BackgroundManager;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.uti.SpiffyGenericTween;
import com.lostagain.nl.uti.SpiffyTweenConstructor;

/** The main exploration view, which lets them see LocationURIs 
 * **/
public class MainExplorationView implements Screen {

	//static Logger Log = Logger.getLogger("ME.MainExplorationView");

	final static String logstag = "ME.MainExplorationView";

	final ME game;

	static LocationsHub currentlyOpenLocation;

	public static Stage gameStage;

	
	static Stage guiStage;

	static Float CurrentX = 100f;


	static Float CurrentY = 100f;	
	static Float CurrentZ = 400f;
	static Float CurrentZoom = 1f;

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

	public static GUIBar usersGUI = new GUIBar();

	static SpiffyGenericTween<Double> currentCameraTweenX;
	static SpiffyGenericTween<Double> currentCameraTweenY;
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
    
    
 
	//final thing should use perspective
	enum cammode{
		ortha,perspective
	}    
	cammode currentmode = cammode.ortha;


	//controlls the 3d background
	public static  BackgroundManager background = new BackgroundManager();





	public static  LinkedList<LocationsHub> LastLocation = new LinkedList<LocationsHub>();
	static LocationsHub currentTargetLocation;
	



	public MainExplorationView(final ME gam) {

		this.game = gam;

		Gdx.app.log(logstag,"setting up stage and stuff");

		//create the game gui interface
		guiStage = new Stage();

		Gdx.app.log(logstag,"setting up stage and stuff..");
		guiStage.addActor(usersGUI);

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
				double newX=currentCameraTweenX.next();   				   				
				double newY=currentCameraTweenY.next();

				LookAtX = CurrentX; //one step lag?
				LookAtY = CurrentY;

				//	Log.info ("camera setting to:"+newX);

				CurrentX=(float) newX;
				CurrentY=(float) newY;

				if (currentCameraTweenX.hasNext()){

					//reschedule
					if (!cameraTweenTask.isScheduled()){
						cameraTimer.scheduleTask(this, 0.1f);
					}

				} else {

					newX=currentCameraTweenX.endPoint();  				   				
					newY=currentCameraTweenY.endPoint();  	   				
					Gdx.app.log(logstag,"camera at end setting to:"+newX);   	 

					CurrentX=(float) newX;
					CurrentY=(float) newY;

					LookAtX = CurrentX; 
					LookAtY = CurrentY;
					
					//targetLocation
					 if (currentTargetLocation!=MainExplorationView.LastLocation.getLast()){
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
		

		PlayersData.homeLoc = new LocationsHub(PlayersData.computersuri);

		addnewlocation( PlayersData.homeLoc,200,500);


		Gdx.app.log(logstag,"centering cameraf");

		centerViewOn( PlayersData.homeLoc);



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
			newloc.addClosedBackground();
			
		}
		

		//add some test creatures
		BasicInfovore creature1 = new BasicInfovore(x,y,null);
		BasicInfovore creature2 = new BasicInfovore(x+44,y+44,null);
	}


	public static void centerViewOn(LocationsHub locationcontainer){
		
		coasting = false;
		dragging = false;
		
		centerViewOn(locationcontainer, true);
		
	}


	public static void centerViewOn(LocationsHub locationcontainer, boolean addLocationToUndo){

		//CurrentX=locationcontainer.getCenterX();  //getX()+(locationcontainer.getWidth()/2);
		//CurrentY=locationcontainer.getCenterY(); //getY()+(locationcontainer.getHeight()/2);

		float newX = locationcontainer.getX(Align.center);
		float newY = locationcontainer.getY(Align.center);

		//asign new tweens
		currentCameraTweenX = SpiffyTweenConstructor.Create(CurrentX.doubleValue(),newX, 25);
		currentCameraTweenY = SpiffyTweenConstructor.Create(CurrentY.doubleValue(),newY, 25);

		currentTargetLocation = locationcontainer;
		//ensure camera animator is running
		if (!cameraTweenTask.isScheduled()){
			Gdx.app.log(logstag,"triggering timer");
			cameraTimer.scheduleTask(cameraTweenTask, 0.1f);

		}

		
		//add the requested location to the  array list, but only if its different from
				//the last location.
		LocationsHub lastlocstored =null;;
		try {
			lastlocstored = LastLocation.getLast();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (lastlocstored!=null && addLocationToUndo){
 			
			if (locationcontainer!=lastlocstored){
				
				Gdx.app.log(logstag,"adding="+locationcontainer.LocationsNode.toString());

				LastLocation.add(locationcontainer);
				
				for (LocationsHub test : LastLocation) {
					
					Gdx.app.log(logstag,"LastLocation="+test.LocationsNode.getPLabel());
					
				}
				
			}
			
		} else {
			
			for (LocationsHub test : LastLocation) {
				
				Gdx.app.log(logstag,"LastLocation="+test.LocationsNode.getPLabel());
				
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
		camera.position.set(CurrentX, CurrentY, CurrentZ);

		// create the camera and the SpriteBatch
		if ( currentmode == cammode.ortha){        	 
			((OrthographicCamera)camera).zoom = CurrentZoom;
		} 


		// camera.lookAt(LookAtX, LookAtY, -10);

		camera.update();

		//Note we draw this here because its in the background and should appear behind the other elements
		//Each render is sort of like a "layer" and appears in the order they are rendered
		//regardless of 3d positions within that layer
		background.updateAnimatedBacks(delta);
		background.modelBatch.begin( camera);
		background.modelBatch.render(ModelManagment.allModelInstances);
		background.modelBatch.end();	

		gameStage.getViewport().setCamera(camera);

		gameStage.act(delta); //Gdx.graphics.getDeltaTime()
		gameStage.draw();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		//  game.batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the bucket and
		// all drops
		ME.batch.begin();

		if (customCursor!=null){

			float xc = Gdx.input.getX();
			float yc = -Gdx.input.getY()+gameStage.getHeight();

			ME.batch.draw(customCursor, (xc-(customCursor.getWidth()/2)), (yc-customCursor.getHeight()/2));


		}

		//    game.font.draw(game.batch, "Drops Collected:: " + dropsGathered, 0, 480);
		ME.font.draw( ME.batch, "X:: " + CurrentX+" Y:: " + CurrentY+"...."+dragging+"(x="+drag_dis_x+",y="+drag_dis_y+")", 10,45);
		ME.font.draw( ME.batch, "Z:: " + CurrentZ, 10, 25);

		//    game.batch.draw(bucketImage, bucket.x, bucket.y);


		ME.batch.end();

		// process user input
		//  if (Gdx.input.isTouched()) {


		//  Vector3 touchPos = new Vector3();
		//  touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		//  camera.unproject(touchPos);
		//  }
		
		if (Gdx.input.isTouched()) {
			
			if (!dragging && !cancelnextdragclick){
				dragging = true;
				dragstart = TimeUtils.millis();
				
				
				startdragxscreen = Gdx.input.getX();
				startdragyscreen = Gdx.input.getY();
								
				Gdx.app.log(logstag,"x="+startdragxscreen+",y="+startdragyscreen);
				
				startdragx_exview = CurrentX;
				startdragy_exview = CurrentY;
				
			} else if (!cancelnextdragclick) {
				
				 drag_dis_x = Gdx.input.getX()-startdragxscreen;
				 drag_dis_y = Gdx.input.getY()-startdragyscreen;
				
				 CurrentX = startdragx_exview-drag_dis_x;
				 CurrentY = startdragy_exview+drag_dis_y;
				 
			}
			
			

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
				CurrentX = (float) (CurrentX-(MotionDisX*delta));
				CurrentY = (float) (CurrentY+(MotionDisY*delta));
			}
		}
		
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		{
			CurrentX = CurrentX-(200* Gdx.graphics.getDeltaTime());        	
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		{        	
			CurrentX = CurrentX+(200* Gdx.graphics.getDeltaTime());
		}

		if (Gdx.input.isKeyPressed(Keys.UP))
		{
			CurrentY = CurrentY+(200* Gdx.graphics.getDeltaTime());        	
		}

		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{        	
			CurrentY = CurrentY-(200* Gdx.graphics.getDeltaTime());
		}
		if (Gdx.input.isKeyPressed(Keys.Z))
		{
			CurrentZ = CurrentZ+(150* Gdx.graphics.getDeltaTime()); 
			CurrentZoom = CurrentZoom +(2* Gdx.graphics.getDeltaTime()); 
		}

		if (Gdx.input.isKeyPressed(Keys.A) && CurrentZ>0.5)
		{        	
			CurrentZ = CurrentZ-(150* Gdx.graphics.getDeltaTime());
			CurrentZoom = CurrentZoom -(2* Gdx.graphics.getDeltaTime()); 
		}

		if (Gdx.input.isButtonPressed(Buttons.LEFT)){
			ME.playersInventory.dropHeldItem();
		}




		//now update the gui
		guiStage.draw();

	}

	@Override
	public void resize(int width, int height) {

		if ( currentmode == cammode.ortha){
			camera = new OrthographicCamera(width, height);

			((OrthographicCamera)camera).setToOrtho(false, 1600, 960);

		} else {
			camera = new PerspectiveCamera(60,width,height); // new OrthographicCamera();
		}

		//camera = new PerspectiveCamera(60,width,height); //new OrthographicCamera(width, height);
		camera.translate(height/2,width/2, 0);
		camera.near=0.5f;
		camera.far=900.0f;
		camera.update();

		gameStage.getViewport().setCamera(camera);
		gameStage.getViewport().update(width, height, true);       	
		guiStage.getViewport().update(width, height, true);


		Gdx.app.log(logstag,"height="+gameStage.getHeight());
		Gdx.app.log(logstag,"w="+gameStage.getWidth());


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
		LocationsHub screen = Location.getLocation(linksToThisPC);


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
		
		for (LocationsHub test : LastLocation) {
			
			Gdx.app.log(logstag,"LastLocations="+test.LocationsNode.getPLabel());
			
		}
		
		//remove current location (which should be the last added)
		if (LastLocation.getLast()==null){
			return;
		}
		LastLocation.removeLast();			
		
		//goto the last one if theres one
		LocationsHub requested = LastLocation.getLast(); //gwt cant use peeklast

		if (requested!=null){
			
			Gdx.app.log(logstag,"last location is:"+requested.LocationsNode.getPLabel());		
			LastLocation.removeLast();			
			centerViewOn( requested,false );
			
		} else {

			Gdx.app.log(logstag,"no last location");
			
		}
	}


}