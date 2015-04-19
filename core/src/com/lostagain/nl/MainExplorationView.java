package com.lostagain.nl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.me.camera.DebugCamera;
import com.lostagain.nl.me.camera.MECamera;
import com.lostagain.nl.me.gui.GUIBar;
import com.lostagain.nl.me.gui.InfoPopUp;
import com.lostagain.nl.me.gui.STMemory;
import com.lostagain.nl.me.gui.ScanManager;
import com.lostagain.nl.me.gui.ScreenUtils;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.locationFeatures.LocationsHub;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.ModelManagment;

/** The main exploration view, which lets them see LocationURIs 
 * **/
public class MainExplorationView implements Screen {

	final static String logstag = "ME.MainExplorationView";

	private static boolean LeftButtonDown = false;

	final ME game;

	static Location currentlyOpenLocation;
	

	public static Stage gameStage;		
	public static Stage guiStage;

	




	static Texture customCursor = null;
	static Texture customCursorTest = null;

	Texture dropImage;
	static Texture bucketImage;

	Sound dropSound;
	Music rainMusic;
	
	
	//debug camera
	public static DebugCamera debugCamera = new DebugCamera();
	
	//Current camera settings
	public static MECamera camera = new MECamera();
	
	public static Vector3 currentPos = new Vector3(PlayersData.homelocationX+(LocationsHub.sizeX/2),PlayersData.homelocationY+(LocationsHub.sizeY/2),1000f); //note we start high up and zoom in at the start as a little intro
	//public static Vector3 zoomToAtStartPos = new Vector3(PlayersData.homelocationX+(LocationsHub.sizeX/2),PlayersData.homelocationY+(LocationsHub.sizeY/2),444f); //note we start high up and zoom in at the start as a little intro
	
	public static Float CurrentZoom = 1f;	

	static Float LookAtX = 0f;
	static Float LookAtY = 0f;
	
	/** disables the users control over the movement (ie, dragging) **/
	static boolean movementControllDisabled = false;
	
	/** I dont know really how to use this correctly yet :-/ **/
	public static RenderContext rcontext;
	
	Rectangle bucket;
	Array<Rectangle> raindrops;
	long lastDropTime;
	int dropsGathered;

	public static GUIBar usersGUI;
	
	
	public static InfoPopUp infoPopUp = new InfoPopUp();
	
//	static SpiffyVector3Tween currentCameraTween;
	
	//static  Timer cameraTimer = new Timer();
//	static  Task cameraTweenTask;

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
    
	public static boolean touchedAModel = false;
	
    
 
	

	/*
	private Image testdataobject = new DataObject(StaticSSSNodes.knows,"12");
	private Image testdataobject2 = new DataObject(StaticSSSNodes.asciidecoder,"123456");
	private Image testdataobject3 = new DataObject(StaticSSSNodes.language,"1234567890");
	private Image testdataobject4 = new DataObject(StaticSSSNodes.SecuredBy,"1234567890ABCDEFGH");

*
*/
	/*
	private static class DistanceFieldShader extends ShaderProgram {
		public DistanceFieldShader () {
			super(Gdx.files.internal("shaders/distancefield.vert"), Gdx.files.internal("shaders/distancefield.frag"));
			if (!isCompiled()) {
				throw new RuntimeException("Shader compilation failed:\n" + getLog());
			}
		}

		 @param smoothing a value between 0 and 1 
		public void setSmoothing (float smoothing) {
			float delta = 0.5f * MathUtils.clamp(smoothing, 0, 1);
			setUniformf("u_lower", 0.5f - delta);
			setUniformf("u_upper", 0.5f + delta);
		}
	}*/
	
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
	
	//DistanceFieldShader testshader = new DistanceFieldShader();
	
	
	public static ShaderProgram distancefieldshader;
	
	Label testlabel = new Label("ME.ModelManagment: _-testing ray in :425.04813 models\r\n" + 
			"ME.ModelManagment: _-testing ray in :699.75104 models\r\n" + 
			"ME.MainExplorationView: _-touch down on a model-_\r\n" + 
			"ME.MainExplorationView: x=360,y=446\r\n" + 
			"ME.MainExplorationView: setting drag to false click\r\n" + 
			"ME.MainExplorationView: \r\n" + 
			" drag displacement time:339\r\n" + 
			"ME.MainExplorationView: MotionDisX:15.04424778761062\r\n" + 
			"ME.MainExplorationView: MotionDisY:-61.6519174041298\r\n" + 
			"ME.MainExplorationView: _-released touch-_\r\n" + 
			"ME.MainExplorationView: __com.badlogic.gdx.scenes.scene2d.ui.Label");
	
	//Controls the 3d background
	public static  ModelManagment background = new ModelManagment();


	
	
	// public Environment environment;


	/** Sets up the stage,gui and camera for first time. 
	 * Currently still has a lot of testing code that should be removed **/
	public MainExplorationView(final ME gam) {

		this.game = gam;

    	
		Gdx.app.log(logstag,"setting up stage and stuff");

		//create the game gui interface
		guiStage = new Stage();

		Gdx.app.log(logstag,"setting up stage and stuff..");
		usersGUI = new GUIBar();
		guiStage.addActor(usersGUI);
	
		usersGUI.validate();

		camera.setToPosition(MainExplorationView.currentPos);
		debugCamera.setToPosition(MainExplorationView.currentPos);
		
		
		
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

	

		//create test shader
		Gdx.app.log(logstag,"creating distance field shader");
		 String vert = Gdx.files.internal("shaders/distancefieldvert_spritebatch.glsl").readString();
        String frag = Gdx.files.internal("shaders/distancefieldfrag.glsl").readString();
        
        //String prefix = createPrefix(renderable, this.get);
        
         distancefieldshader = new ShaderProgram(vert, frag);

         
        if (!distancefieldshader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + distancefieldshader.getLog());
        int u_diffuseColor =  distancefieldshader.getUniformLocation("u_diffuseColor");      
        int u_colorFlag    =  distancefieldshader.getUniformLocation("u_colorFlag");
        
        distancefieldshader.setUniformf(u_diffuseColor, Color.ORANGE);
        distancefieldshader.setUniformf(u_colorFlag, 1f);
      
		Gdx.app.log(logstag,"creating game stage");

		gameStage = new PerspectiveStage();
		
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(guiStage);
		multiplexer.addProcessor(gameStage);
		Gdx.input.setInputProcessor(multiplexer);
		
		gameStage.getViewport().setCamera(camera);//because we use a special camera class we cast first to ensure all the supervariables will be used 
		// stage.getCamera().translate(0, 250, 0);

		gameStage.getCamera().update();

		camera.update(true);
		
		Gdx.app.log(logstag,"creating background");
		background.setup();
		

		PlayersData.homeLoc = new Location(PlayersData.computersuri, PlayersData.homelocationX,PlayersData.homelocationY);

	//	addnewlocation( PlayersData.homeLoc.locationsHub,200,500);



		//test shader
		// String vert = Gdx.files.internal("data/test.vertex.glsl").readString();
		 //   String frag = Gdx.files.internal("data/test.fragment.glsl").readString();
		 //   shader = new DefaultShader(renderable, new DefaultShader.Config(vert, frag));
		 //   shader.init();
		
		ModelInstance testlabinstance = testlabel.getModel();

		Matrix4 newmatrix = new Matrix4();
		testlabinstance.transform.setToTranslation(690,900,0);
		newmatrix.setToRotation(0, 0, 1, -90);
		testlabinstance.transform.mul(newmatrix);
		
		// environment = new Environment();
	    //     environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
	    //   environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		MessyModelMaker.addToBackground(testlabinstance);

		
		//gameStage.setDebugAll(true);
		
		//------------------------

		//update screen parameters (these give the game slightly different views/apperence for different screen resolutions
		ScreenUtils.setup(gameStage.getWidth(), gameStage.getHeight());	
		
		//finally start the game for the player by moving the camera and displayinght welcome message
		Gdx.app.log(logstag,"centering on starting location & displaying welcome message");
		
		infoPopUp.displayMessage("Welcome To The Mesh");		
		ME.centerViewOn( PlayersData.homeLoc,ScreenUtils.getSuitableDefaultCameraHeight());

	}

	public static void setCursor(Texture curimage){

		if (curimage!=null){
			customCursor = curimage;
		} else {
			customCursor = null;
		}

	}

	public static void addnewlocationHub(LocationsHub newloc,int x,int y) {

		newloc.setPosition(x,y);
		newloc.setClip(false);

		gameStage.addActor(newloc);
		
		//add noise back if closed and added to stage
		if (newloc.closed){
			
			newloc.setClosedBackground();
			
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
		
	//	camera.setTargetPosition(currentPos);
		camera.updatePosition(delta);
		
		//camera.position.set(currentPos);
		
		//also set camera overlay
	//	MECamera.mainOverlay.transform.setToTranslation(currentPos.x, currentPos.y, currentPos.z);
				
		
		//change opacity of overlay based on z
		//in future we probably need to change this to something "effect defendant" so different conditions can trigger different camera effects
		
		
		// create the camera and the SpriteBatch
		/*
		if ( currentmode == cammode.ortha){        	 	
					
			
			
			float newzoom = (0.0133333315344f*currentPos.z)-4.9199985961765f;
			CurrentZoom = newzoom;
			((OrthographicCamera)camera).zoom = CurrentZoom;
			

			//Gdx.app.log(logstag,"CurrentZoom="+CurrentZoom);
		} 
*/

		// camera.lookAt(LookAtX, LookAtY, -10);

		camera.update();

		if (debugCamera.active){
			debugCamera.update();
		}
		
		
		
		//Note we draw this here because its in the background and should appear behind the other elements
		//Each render is sort of like a "layer" and appears in the order they are rendered
		//regardless of 3d positions within that layer
		background.updateAnimatedBacks(delta);
		ModelManagment.updateObjectMovementAndFrames(delta);//--
		
		//we start other updates
		
		//update the guns animation
		usersGUI.ConceptGun.update(delta);
		
		//update any scans and their  bars
		ScanManager.update(delta);
		
		if (debugCamera.active){
			background.modelBatch.begin( debugCamera);
		} else {
			background.modelBatch.begin( camera);
		}
		
		//rcontext.begin();
		//testdefaultShader.begin(camera, rcontext);		
		background.modelBatch.render(ModelManagment.allBackgroundInstances);
		
		//testdefaultShader.end();
		background.modelBatch.end();	
		
		//rcontext.end();		
		//testshader.begin();
		//testshader.setUniformMatrix("u_projTrans", camera.projection);
		//testshader.setUniformi("u_texture", 0);
		//ModelManagment.allModelInstances.get(0).model.meshes.get(0).render(testshader,  GL20.GL_TRIANGLES);
		//testshader.end();
		if (debugCamera.active){
			gameStage.getViewport().setCamera(debugCamera);
		} else {
			gameStage.getViewport().setCamera(camera);
		}
		
		gameStage.act(delta); //Gdx.graphics.getDeltaTime()
		gameStage.draw();
	
		//background.modelBatch.getRenderContext().begin();
		//background.modelBatch.getRenderContext().setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		//background.modelBatch.getRenderContext().setCullFace(GL20.GL_FRONT);
		if (debugCamera.active){
			background.modelBatch.begin( debugCamera);
		} else {
			background.modelBatch.begin( camera);
		}
		//rcontext.begin();
		//testdefaultShader.begin(camera, rcontext);		
		background.modelBatch.render(ModelManagment.allForgroundInstances);
		
		//testdefaultShader.end();
		background.modelBatch.end();	
		//background.modelBatch.getRenderContext().end();

		//  game.batch.setProjectionMatrix(camera.combined);

		if (Gdx.input.isTouched()) {
			
			//test if we clicked a 3d model
			if (newtouch ){				
				//trigger concept gun
				usersGUI.ConceptGun.fireAt(Gdx.input.getX(), Gdx.input.getY());		
			}
			
			
			
			//before starting a new drag we have to do a lot of checks to make sure drags are allowed right now
			//this includes checking we arnt already dragging
			//checking no pending event has canceled the next drag (like if the user is moving a scrollable window)
			//checking they arnt "dragging" a concept object rather then the landscape (currentlyHeld)
			//and finnally checking if they are allowed to drag at all (ie, maybe the gun is in use and its disabled normal movement)
			if (!dragging
				&& !cancelnextdragclick 
				&& !touchedAModel 
				&& STMemory.currentlyHeld == null
				&& !movementControllDisabled){
				dragging = true;
				dragstart = TimeUtils.millis();
				
				
				startdragxscreen = Gdx.input.getX();
				startdragyscreen = Gdx.input.getY();
								
				Gdx.app.log(logstag,"x="+startdragxscreen+",y="+startdragyscreen);
				
				startdragx_exview = currentPos.x;
				startdragy_exview = currentPos.y;
				
			} else if (!cancelnextdragclick && !touchedAModel && !movementControllDisabled) {
				
				 drag_dis_x = Gdx.input.getX()-startdragxscreen;
				 drag_dis_y = Gdx.input.getY()-startdragyscreen;
				
				 currentPos.x = startdragx_exview-drag_dis_x;
				 currentPos.y = startdragy_exview+drag_dis_y;
				 
			}
			

			newtouch = false;

		} else if (dragging == true && !movementControllDisabled){
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
			
			
			if (!coasting && !movementControllDisabled){
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
		
		//if not on debug handle the movements
		if (!debugCamera.active){
			handleStandardCameraInputs();
		} else {
			debugCamera.handleInput();
		}
		
		if (Gdx.input.isKeyPressed(Keys.Q))
		{        	
			debugCamera.setActive(!debugCamera.active);
			
			infoPopUp.displayMessage("DEBUG CAMERA:"+debugCamera.active,Color.RED);
			
			

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
		
		ME.interfaceSpriteBatch.begin();
		

  		//gameStage.getBatch().setShader(program);
  		//ME.batch.setShader(program);
		
		//update displayed info for messages being popped up.
		infoPopUp.update(delta);
  		
		if (customCursor!=null){

			float xc = Gdx.input.getX();
			float yc = -Gdx.input.getY()+gameStage.getHeight();

			ME.interfaceSpriteBatch.draw(customCursor, (xc-(customCursor.getWidth()/2)), (yc-(customCursor.getHeight()/2)));

	//		Gdx.app.log(logstag,"customCursor.."+xc+","+yc+" height="+ (customCursor.getHeight()/2));
			
			
		}

		//    game.font.draw(game.batch, "Drops Collected:: " + dropsGathered, 0, 480);
		ME.font.draw( ME.interfaceSpriteBatch, "x:: " + currentPos.x+" y:: " + currentPos.y+" z::"+ currentPos.z, 10,25);

		//    game.batch.draw(bucketImage, bucket.x, bucket.y);
		ME.interfaceSpriteBatch.end();

	}

	private void handleStandardCameraInputs() {
		if (Gdx.input.isKeyPressed(Keys.P))
		{
			camera.rotate(new Vector3(0, 1,0),3);       	
		}
		if (Gdx.input.isKeyPressed(Keys.L))
		{
			camera.rotate(new Vector3(0, 1,0),-3);       	
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
			
		//	camera.setTargetPosition(currentPos);
			currentPos.y = currentPos.y+(200* Gdx.graphics.getDeltaTime());
		}

		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{        	
			currentPos.y = currentPos.y-(200* Gdx.graphics.getDeltaTime());
		}
		if (Gdx.input.isKeyPressed(Keys.Z))
		{
			currentPos.z = currentPos.z+(150* Gdx.graphics.getDeltaTime()); 
/*
			if ( currentmode == cammode.ortha){
			//	CurrentZoom = CurrentZoom +(2* Gdx.graphics.getDeltaTime()); 

				float newzoom = (0.0133333315344f*currentPos.z)-4.9199985961765f;	//the formula works out a ratio between zoom and z position			
				CurrentZoom = newzoom;
				
				//Gdx.app.log(logstag,currentPos.z+","+CurrentZoom);
			}*/
		}

		if (Gdx.input.isKeyPressed(Keys.A) && currentPos.z>0.5)
		{        	
			currentPos.z = currentPos.z-(150* Gdx.graphics.getDeltaTime());
/*
			if ( currentmode == cammode.ortha){
				
				float newzoom = (0.0133333315344f*currentPos.z)-4.9199985961765f;	//the formula works out a ratio between zoom and z position			
				CurrentZoom = newzoom;
				
			}*/
		}
		
	
	}

	@Override
	public void resize(int width, int height) {		

		Gdx.app.log(logstag,"resizeing to..."+width+","+height);
		
		//update screen params
		ScreenUtils.setup(width, height);
		
		//ensure params are reset
		camera.fieldOfView = 60;
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		
		
		//update sprite batch for new resolution 
		Matrix4 viewMatrix = new Matrix4();
	    viewMatrix.setToOrtho2D(0, 0,width, height);
	    ME.interfaceSpriteBatch.setProjectionMatrix(viewMatrix);
		

		//camera = new PerspectiveCamera(60,width,height); //new OrthographicCamera(width, height);
	    ((PerspectiveCamera)camera).translate(height/2,width/2, 0);
	    ((PerspectiveCamera)camera).near=0.5f;
	    ((PerspectiveCamera)camera).far=1900.0f;
	    ((PerspectiveCamera)camera).update();
		
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
		
		Gdx.app.log(logstag,"stage height="+guiStage.getHeight());
		Gdx.app.log(logstag,"stage width="+guiStage.getWidth());

		Gdx.app.log(logstag,"gameStage height="+gameStage.getHeight());
		Gdx.app.log(logstag,"gameStage width="+gameStage.getWidth());



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
}