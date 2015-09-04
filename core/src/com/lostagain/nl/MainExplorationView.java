package com.lostagain.nl;

import java.util.ArrayList;
import java.util.Vector;

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
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.ME.GameMode;
import com.lostagain.nl.GWTish.Button;
import com.lostagain.nl.GWTish.ClickHandler;
import com.lostagain.nl.GWTish.EXAMPLES;
import com.lostagain.nl.GWTish.HorizontalPanel;
import com.lostagain.nl.GWTish.Image;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.me.camera.DebugCamera;
import com.lostagain.nl.me.camera.MECamera;
import com.lostagain.nl.me.features.ConceptGunPanel;
import com.lostagain.nl.me.features.ConceptObject;
import com.lostagain.nl.me.features.ConceptObjectSlot;
import com.lostagain.nl.me.features.DataRequestManager;
import com.lostagain.nl.me.features.DataRequestScreen;
import com.lostagain.nl.me.features.InfoBox;
import com.lostagain.nl.me.features.InventoryPanel;
import com.lostagain.nl.me.features.LocationHub;
import com.lostagain.nl.me.features.MeshIcon;
import com.lostagain.nl.me.features.ProgressBar;
import com.lostagain.nl.me.gui.GUIBar;
import com.lostagain.nl.me.gui.InfoPopUp;
import com.lostagain.nl.me.gui.STMemory;
import com.lostagain.nl.me.gui.ScanManager;
import com.lostagain.nl.me.gui.ScreenUtils;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.locationFeatures.LocationsHub;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.ModelManagment.RenderOrder;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.models.objectType;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.me.particles.exampleParticleManagement;
import com.lostagain.nl.shaders.InvertShader;
import com.lostagain.nl.shaders.NormalMapShader;

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

	public static Vector3 currentPos = new Vector3(PlayersData.homelocationX+(LocationsHub.sizeX/2),PlayersData.homelocationY+(LocationsHub.sizeY/2),2000f); //note we start high up and zoom in at the start as a little intro
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

	//not usedanymore
	//public static GUIBar usersGUI;


	public static InfoPopUp infoPopUp = new InfoPopUp();

	//	static SpiffyVector3Tween currentCameraTween;

	//static  Timer cameraTimer = new Timer();
	//	static  Task cameraTweenTask;


	static boolean dragging = false;
	/**
	 * used to force the scene dragging to start in situations when it normally wouldn't (for example the email page allows dragging of scene even if you click on the email itself)
	 */
	static boolean forceDragStart = false; 
	static  boolean coasting = false;

	static private long dragstart;
	static boolean cancelnextdragclick = false; //overrides explorer drags from happening if a user clicks certain panels (ie, scrollpanel)
	static private double MotionDisX = 0;
	static private double MotionDisY = 0;

	static float startdragx_exview = 0.0f;
	static float startdragy_exview = 0.0f;


	static int startdragxscreen = 0;
	static int startdragyscreen = 0;

	static  int drag_dis_x = 0;
	static int drag_dis_y = 0;

	//   boolean newtouch=true; //if a touch event has just started
	//  boolean newUp = false; //if the next time the mouse is up represents a mouse release

	Vector2 touchStartedAt = null;

	public static hitable touchedAModel = null;
	ArrayList<hitable> lastHits = new ArrayList<hitable>();

	/**  mouse state (still being implemented. Will replace newtouch and newup)
	 **/
	public static enum TouchState {
		/** screen is not touched or mouse is not down **/
		NONE,
		/**
		 * the mouse has just gone down or the screen has just been touced
		 */
		NewTouchDown,
		/**
		 * screen is being touched or mouse is down
		 */
		TouchDown,
		/**
		 * mouse or touch just went up
		 */
		NewTouchUp,

		/**
		 * The mouse has just moved a bit since being down  **/
		NewDrag,

		/**
		 * The mouse has moved a bit since being down and is still being held **/
		Dragging;

	}

	/** the current state of mouse or touch **/
	TouchState currentTouchState = TouchState.NONE;



	public static ShaderProgram distancefieldshader;


	//Controls the 3d background
	public static  ModelManagment the3dscene = new ModelManagment();




	//Enviromental Settings (currently just a point light linked to the mouse
	/**
	 * Controlls the light source linked to the mouse.
	 * Currently used by the normal map shader
	 */
	public static PointLight mouseLight = new PointLight();
	// public Environment environment;



	/** Sets up the stage,gui and camera for first time. 
	 * Currently still has a lot of testing code that should be removed **/
	public MainExplorationView(final ME gam) {

		this.game = gam;


		Gdx.app.log(logstag,"setting up stage and stuff");

		//create the game gui interface
		guiStage = new Stage();

		Gdx.app.log(logstag,"setting up stage and stuff..");
		//	usersGUI = new GUIBar();
		//	guiStage.addActor(usersGUI);

		//usersGUI.validate();

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


		//	String vert = Gdx.files.internal("shaders/distancefieldvert_spritebatch.glsl").readString();
		//   String frag = Gdx.files.internal("shaders/distancefieldfrag.glsl").readString();

		//Note this shader needs fixs in its glsl file - we commented out working fwdith stuff to make it work on the web

		//String prefix = createPrefix(renderable, this.get);
		/*
    	Gdx.app.log(logstag,"creating distance field shader");
         distancefieldshader = new ShaderProgram(vert, frag);

     	Gdx.app.log(logstag,"created distance field shader");

        if (!distancefieldshader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + distancefieldshader.getLog());
        int u_diffuseColor =  distancefieldshader.getUniformLocation("u_diffuseColor");      
        int u_colorFlag    =  distancefieldshader.getUniformLocation("u_colorFlag");

     	Gdx.app.log(logstag,"setting uniforms on shader");

        distancefieldshader.setUniformf(u_diffuseColor, Color.ORANGE);
        distancefieldshader.setUniformf(u_colorFlag, 1f);
		 */
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
		the3dscene.setup();


		PlayersData.homeLoc = new Location(PlayersData.computersuri, PlayersData.homelocationX,PlayersData.homelocationY);

		//	addnewlocation( PlayersData.homeLoc.locationsHub,200,500);


		//	String info = "location\n123456789\n12345";
		//	Label testFeature2 = new Label(info);
		//	testFeature2.setToPosition(new Vector3(0,0,20f));
		//	ModelManagment.addmodel(testFeature2,ModelManagment.RenderOrder.zdecides);


		//	InfoBox testFeature = new InfoBox("Title","Content Text");

		//	MeshIcon iconTest   = new MeshIcon(MeshIcon.IconType.Info,PlayersData.homeLoc,testFeature);
		//	iconTest.setToRotation(new Quaternion(Vector3.X, 45)); //random rotation test
		//	iconTest.setToPosition(new Vector3(120f,670f,0f));
		//	ModelManagment.addmodel(iconTest,ModelManagment.RenderOrder.zdecides);


		//	testRequestScreen.setToPosition(new Vector3(320f,550f,0f));
		//	ModelManagment.addmodel(testRequestScreen,ModelManagment.RenderOrder.zdecides);


		/*
		ConceptObjectSlot slotTest = new ConceptObjectSlot();
		slotTest.setToPosition(new Vector3(450f,670f,0f));
		ModelManagment.addmodel(slotTest,ModelManagment.RenderOrder.zdecides);
		 */


		/*
		LocationHub testhub = new LocationHub(PlayersData.computersuri,PlayersData.homeLoc);
		testhub.setToPosition(new Vector3(450f,750f,0f));
		ModelManagment.addmodel(testhub,ModelManagment.RenderOrder.zdecides);


		ProgressBar testbar = new ProgressBar(30, 10, 200);
		testbar.setToPosition(new Vector3(350f,700f,0f));
		testbar.setValue(100);

		ModelManagment.addmodel(testbar,ModelManagment.RenderOrder.zdecides);
		 */

		//	testhub.addLineTo(iconTest);
		/*
		testlabel.setLabelBackColor(new Color(0.3f,0.3f,1f,0.9f));
		//testlabel.setSizeAs(100, 100);

		testlabel.setToPosition(new Vector3(120,550,0));

		testlabel2.setLabelBackColor(new Color(0.3f,0.3f,1f,0.6f));
		testlabel2.setTextScale(0.6f);		//not functional
		testlabel2.setText("(file url of computer test)");

		testlabel2.setToPosition(new Vector3(120f,470f,0f));


		// environment = new Environment();
	    //     environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
	    //   environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		ModelManagment.addmodel(testlabel,ModelManagment.RenderOrder.zdecides);
		testlabel.setOpacity(0.85f);

		ModelManagment.addmodel(testlabel2,ModelManagment.RenderOrder.zdecides);
		testlabel2.setOpacity(0.5f);
		 */


		/*

			//Now lets try making a vertical panel
			VerticalPanel testverticalPanel = new VerticalPanel();


		//	testPanel.add(testlabel);
			//testPanel.add(testlabel2);
			Label testlabel3 = new Label("test lab 1");
			testverticalPanel.add(testlabel3);

			Label testlabel4 = new Label("test lab 2");
			testverticalPanel.add(testlabel4);

			//testPanel.setSpaceing(11f);

			testverticalPanel.setToPosition(new Vector3(300f,-300f,0f));
			ModelManagment.addmodel(testverticalPanel,ModelManagment.RenderOrder.zdecides);

			HorizontalPanel testHorizontalPanel = new HorizontalPanel();


			//	testPanel.add(testlabel);
				//testPanel.add(testlabel2);
				Label testlabel3h = new Label("|11|");
				testHorizontalPanel.add(testlabel3h);
				testHorizontalPanel.add(testverticalPanel);
				Label testlabel4h = new Label("|2222222222222222222|");
				testHorizontalPanel.add(testlabel4h);


				Label testlabel5h = new Label("|33|");
				testHorizontalPanel.add(testlabel5h);
				//testPanel.setSpaceing(11f);

				testHorizontalPanel.setToPosition(new Vector3(540f,-300f,0f));
				ModelManagment.addmodel(testHorizontalPanel,ModelManagment.RenderOrder.zdecides);
		 */
		//gameStage.setDebugAll(true);

		//------------------------

		//update screen parameters (these give the game slightly different views/apperence for different screen resolutions
		ScreenUtils.setup(gameStage.getWidth(), gameStage.getHeight());	

		//finally start the game for the player by moving the camera and displayinght welcome message
		Gdx.app.log(logstag,"centering on starting location & displaying welcome message");
		Color startupMessageColour = new Color(0f,0.8f,0f, 0.5f);


		infoPopUp.sheduleMessage(".", startupMessageColour, 1000);
		infoPopUp.sheduleMessage("..", startupMessageColour, 1200);
		infoPopUp.sheduleMessage("..", startupMessageColour, 1500);
		infoPopUp.sheduleMessage("...Modem Emulation Starting", startupMessageColour, 1600);
		infoPopUp.sheduleMessage(".Done", startupMessageColour, 1800);
		infoPopUp.sheduleMessage("Mesh Explorer Boot Complete", startupMessageColour, 2100);
		infoPopUp.sheduleMessage("..Initalising", startupMessageColour, 2200);
		infoPopUp.sheduleMessage(".Done", startupMessageColour, 4200);
		infoPopUp.sheduleMessage("Welcome To The Mesh", Color.WHITE, 5200);		
		infoPopUp.sheduleMessage("Please check your messages and links to get started", Color.WHITE, 8200);

		ME.centerViewOn( PlayersData.homeLoc,ScreenUtils.getSuitableDefaultCameraHeight(),7000);


		//Default light settings
		mouseLight.set(Color.ORANGE, 1f, 2f, 0.075f, 1f);

		//more tests if on developer mode
		if (ME.currentGameMode == ME.GameMode.Developer){

			addDeveloperDrops();

			//also setup particle effects (test for now)
			exampleParticleManagement.setup(camera);


		}


	}



	/**
	 * adds a bunch of drops to the scene to aid development work
	 * (ie, give access to stuff earlier without needing to play though early gameplay over and over)
	 * 
	 */
	private void addDeveloperDrops() {

		//we get all the nodes we want as a list then drop them near the start location
		ArrayList<SSSNode> developerDropList = new ArrayList<SSSNode>();
		developerDropList.add(StaticSSSNodes.PrototypeConceptGun);	
		developerDropList.add(PlayersData.computersuri);			
		developerDropList.add(StaticSSSNodes.BasicInventory);
		developerDropList.add(StaticSSSNodes.ExpandedInventory);
		developerDropList.add(StaticSSSNodes.standardgui);
		SSSNode applenode = SSSNode.getNodeByLabel("Granny Smith apple");
		developerDropList.add(applenode);	
		SSSNode sunnode = SSSNode.getNodeByLabel("Sun");
		developerDropList.add(sunnode);	
		//SSSNode waternode = SSSNode.getNodeByLabel("Water");
		//developerDropList.add(waternode);	

		float y = 0; //just a position nudge to lay things out nicer
		for (SSSNode node : developerDropList) {

			ConceptObject coTest = new ConceptObject(node);
			coTest.setToPosition(new Vector3(366f,100f+(y*50),0f));
			ModelManagment.addmodel(coTest,ModelManagment.RenderOrder.zdecides);
			y++;

		}

		/*
		ConceptObject coTest = new ConceptObject(PlayersData.computersuri);
		coTest.setToPosition(new Vector3(320f,450f,0f));
		ModelManagment.addmodel(coTest,ModelManagment.RenderOrder.zdecides);

		//-----
		SSSNode greennode = SSSNode.getNodeByLabel("Granny Smith apple");
		ConceptObject green = new ConceptObject(greennode);
		green.setToPosition(new Vector3(320f,470f,0f));
		ModelManagment.addmodel(green,ModelManagment.RenderOrder.zdecides);
		//--
		//SSSNode applenode = SSSNode.getNodeByLabel("Pear");
		ConceptObject BasicInventory = new ConceptObject(StaticSSSNodes.BasicInventory);
		BasicInventory.setToPosition(new Vector3(320f,485f,0f));
		ModelManagment.addmodel(BasicInventory,ModelManagment.RenderOrder.zdecides);

		ConceptObject ExpandedInventory = new ConceptObject(StaticSSSNodes.ExpandedInventory);
		ExpandedInventory.setToPosition(new Vector3(320f,485f,0f));
		ModelManagment.addmodel(ExpandedInventory,ModelManagment.RenderOrder.zdecides);

		ConceptObject GUI = new ConceptObject(StaticSSSNodes.standardgui);
		GUI.setToPosition(new Vector3(320f,485f,0f));
		ModelManagment.addmodel(GUI,ModelManagment.RenderOrder.zdecides);
		 */

		SSSNode bobsl =  SSSNode.getNodeByLabel("Bobs Outpost");
		SSSNode bobs2 = SSSNode.getNodeByLabel("BobsOutpost");
		//	bobs2.addLabel("bobs outpost");
		bobs2.addLabel("bobs label");
		String bobslabels = bobs2.getAllPLabels();

		infoPopUp.sheduleMessage(bobs2.PURI, Color.PINK, 8800);
		infoPopUp.sheduleMessage(bobslabels, Color.PINK, 8800);

		//	ConceptObject bobconcept = new ConceptObject(bobs2);	
		//	GUI.setToPosition(new Vector3(320f,485f,0f));
		//	ModelManagment.addmodel(bobconcept,ModelManagment.RenderOrder.zdecides);




		//slot 
		final ConceptObjectSlot slotTest = new ConceptObjectSlot();
		slotTest.setToPosition(new Vector3(250f,250f,0f));
		ModelManagment.addmodel(slotTest,ModelManagment.RenderOrder.zdecides);


		//---

		Button tempbutton = new Button("test sort",new ClickHandler() {			
			@Override
			public void onClick() {
				Gdx.app.log(logstag,"____debug button pressed  snapShotNextSort setting... ");
				ModelManagment.mysorter.snapShotNextSort();
				//ModelManagment.mysorter.testSort();

			}
		});


		/*
		Button tempbuttonBigger = new Button(200,35,new Runnable(){
			@Override
			public void run() {
				slotTest.setToScale(new Vector3(2f,2f,2f));

			}			
		});*/

		tempbutton.setToPosition(new Vector3(450f,610f,0f));
		//tempbuttonBigger.setToPosition(new Vector3(450f,570f,0f));
		//
		ModelManagment.addmodel(tempbutton,ModelManagment.RenderOrder.zdecides);
		//ModelManagment.addmodel(tempbuttonBigger,ModelManagment.RenderOrder.zdecides);


		//GWTish Tests
		final Label exampleLabel = EXAMPLES.getLabelExample();
		exampleLabel.setToPosition(new Vector3(1000f,0f,0f));
		ModelManagment.addmodel(exampleLabel,ModelManagment.RenderOrder.zdecides);
		
		final Label exampleLabel2 = EXAMPLES.getLabelExampleGreenGlow();
		exampleLabel2.setToPosition(new Vector3(1000f,-40f,0f));
		ModelManagment.addmodel(exampleLabel2,ModelManagment.RenderOrder.zdecides);
		
		final Label exampleLabel3 = EXAMPLES.getLabelExampleFunky();
		exampleLabel3.setToPosition(new Vector3(1000f,-80f,0f));
		ModelManagment.addmodel(exampleLabel3,ModelManagment.RenderOrder.zdecides);
		
		final Label exampleLabel4 = EXAMPLES.getLabelExampleLong();
		exampleLabel4.setToPosition(new Vector3(1000f,-130f,0f));
		ModelManagment.addmodel(exampleLabel4,ModelManagment.RenderOrder.zdecides);
		
		final HorizontalPanel exampleHP = EXAMPLES.getHorizontalPanelExample();
		exampleHP.setToPosition(new Vector3(1000f,-490f,0f));
		ModelManagment.addmodel(exampleHP,ModelManagment.RenderOrder.zdecides);
		
		

		final VerticalPanel exampleComposit = EXAMPLES.getMixedExample();
		exampleComposit.setToPosition(new Vector3(1000f,-600f,0f));
		ModelManagment.addmodel(exampleComposit,ModelManagment.RenderOrder.zdecides);
		//---------------------------
		
		

		ShaderTestPanel shaderTests = new ShaderTestPanel();
		shaderTests.setToScale(new Vector3(0.2f,0.2f,0.2f));
		shaderTests.setToPosition(new Vector3(100f,430f,40f));	
		ModelManagment.addmodel(shaderTests,ModelManagment.RenderOrder.infrontStage);
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

	//	MainExplorationView.addnewlocationHub(locationsNEWHub,X, -Y);
	public static void addnewlocationHub(LocationHub newloc,int x,int y) {
		newloc.setToPosition(new Vector3(x,y,0));
		ModelManagment.addmodel(newloc,RenderOrder.zdecides);
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
		the3dscene.updateAnimatedBacks(delta);
		ModelManagment.updateObjectMovementAndFrames(delta);//--

		//we start other updates

		//update the guns animation
		//	usersGUI.ConceptGun.update(delta);  //old

		//update any scans and their  bars
		ScanManager.update(delta);

		//match mouseLight to mouse location
		Vector2 currentMouseOnStage = ME.getCurrentStageCursorPosition();		
		mouseLight.position.x = currentMouseOnStage.x;
		mouseLight.position.y = currentMouseOnStage.y;
		//z? intensity?
		///////////
		boolean hasbackground = false;
		if (hasbackground){

			if (debugCamera.active){
				the3dscene.modelBatch.begin( debugCamera);
			} else {
				the3dscene.modelBatch.begin( camera);
			}




			//rcontext.begin();
			//testdefaultShader.begin(camera, rcontext);		
			the3dscene.modelBatch.render(ModelManagment.allBackgroundInstances); //currently disabled



			//testdefaultShader.end();
			the3dscene.modelBatch.end();	

		}

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
			the3dscene.modelBatch.begin( debugCamera);
		} else {
			the3dscene.modelBatch.begin( camera);
		}
		//rcontext.begin();
		//testdefaultShader.begin(camera, rcontext);		
		the3dscene.modelBatch.render(ModelManagment.allForgroundInstances);

		the3dscene.modelBatch.render(exampleParticleManagement.prepareAndGetParticleSystem());

		//testdefaultShader.end();
		the3dscene.modelBatch.end();	
		//background.modelBatch.getRenderContext().end();

		//new touch system
		//first we update the state if needed based on what it was before
		//and what it is now
		if (Gdx.input.isTouched()) {
			//if we are touched we look at state we were before
			switch (currentTouchState) {
			case NONE:			
				currentTouchState = TouchState.NewTouchDown; //if we wernt touching before then its a new touch!
				break;
			case NewTouchDown:
				currentTouchState = TouchState.TouchDown; //if we previously were a new touchdown, then now we are a non-new touchdown
				break;
			case NewTouchUp:
				currentTouchState = TouchState.NewTouchDown; //if we just touched up and then the very next frame touched down....then the user is suspiciously fast
				break;
			case TouchDown:
				currentTouchState = TouchState.TouchDown; //still a touchdown unless the mouse moved;


				Vector2 currentLoc = new Vector2(Gdx.input.getX(),Gdx.input.getY());
				if (touchStartedAt.dst2(currentLoc)>5){							
					currentTouchState = TouchState.NewDrag;
				}				

				break;
			case NewDrag:
				currentTouchState = TouchState.Dragging; //newdrags become Dragging 
				break;				
			case Dragging:
				currentTouchState = TouchState.Dragging; //do nothing
				break;
			}

		} else {

			switch (currentTouchState) {
			case NONE:	
				currentTouchState = TouchState.NONE; //still none
				break;
			case NewTouchDown:
				currentTouchState = TouchState.NewTouchUp; //we were touching now are not, so its a new touchup.
				break;
			case NewTouchUp:
				currentTouchState = TouchState.NONE; //now none
				break;
			case TouchDown:
				currentTouchState = TouchState.NewTouchUp; //we were touching now are not, so its a new touchup.
				break;
			case NewDrag:
				currentTouchState = TouchState.NewTouchUp; //we were touching now are not, so its a new touchup.
				break;
			case Dragging:
				currentTouchState = TouchState.NewTouchUp; //we were touching now are not, so its a new touchup.
				break;
			}

		}


		//now we are touchstate is anything but none we get a ray for it
		if (currentTouchState!=TouchState.NONE)
		{
			Ray ray = ME.getCurrentStageCursorRay();

			Gdx.app.log(logstag,"currentTouchState:"+currentTouchState.name());
			lastHits = ModelManagment.getHitables(ray,true,currentTouchState);

			if (lastHits.size()>0){
				touchedAModel = lastHits.get(0);
			} else {
				touchedAModel = null;
			}


			Boolean fireEnabled=  !ConceptGunPanel.isDisabled();

			if (PlayersData.playersConceptGun!=null){
				PlayersData.playersConceptGun.update(delta); //update the gun in case its firing
			}

			if (currentTouchState == TouchState.NewTouchDown){		
				//if the concept gun is enabled we start shooting

				objectType type = null;
				//we get the interaction type here as we don't want to fire at bits of the interface
				if (touchedAModel!=null){
					type = touchedAModel.getInteractionType();
				}

				if (fireEnabled && type != objectType.Interface ){
					//start firing gun
					fireEnabled = PlayersData.playersConceptGun.fireAt(Gdx.input.getX(), Gdx.input.getY());		 //new gun

				}

			}

			if (currentTouchState==TouchState.NewTouchDown){
				Gdx.app.log(logstag," new touch down");

				touchStartedAt = new Vector2(Gdx.input.getX(),Gdx.input.getY());

			}

			if (currentTouchState==TouchState.NewTouchUp){

				Gdx.app.log(logstag," new touch up");

				touchedAModel=null;
				lastHits.clear();
				ModelManagment.untouchAll(); 				

				//stop firing 
				if (fireEnabled && PlayersData.playersConceptGun!=null){
					PlayersData.playersConceptGun.cancelFire();
				}

			}
			//if we are touching we also need to see if we have moved a bit
			//if so, then we need to inform the model manager to fire any dragevents if needed (ie, for objects held)
			//This is not the same as the scene itself being dragged. In fact they are multly exclusive

			//	if (currentTouchState==TouchState.TouchDown){
			//	Vector2 currentLoc = new Vector2(Gdx.input.getX(),Gdx.input.getY());

			//	if (touchStartedAt.dst2(currentLoc)>5){		

			//		currentTouchState = TouchState.NewDrag;
			//	ModelManagment.fireDragStartOnAll();	
			//	}

			//	}

		} else {

			ModelManagment.fireDragStartEnd(); 
			lastHits.clear();

		}

		//ensure just dropped list is clear (
		STMemory.clearJustDropedList();




		if (Gdx.input.isTouched()) {
			//newUp=true; //the next mouse release will be a new one

			//if (newtouch ){	
			//if (currentTouchState == TouchState.NewTouchDown){							
			//fire gun if not disabled

			/*

				Boolean fireEnabled= false;
				if (PlayersData.playersConceptGun!=null){
					fireEnabled = PlayersData.playersConceptGun.fireAt(Gdx.input.getX(), Gdx.input.getY());		 //new gun

				}

				//else fire normal click event (note; the gun will also trigger normal click events right now too! shot to click action!)
				if (!fireEnabled){

					//we only run these if the gun is disabled. This is because shooting triggers the same events ; see above
					Ray ray = ME.getCurrentStageCursorRay();

					MainExplorationView.touchedAModel = ModelManagment.testForHits(ray,true,true);

					if (MainExplorationView.touchedAModel!=null){
						Gdx.app.log(logstag,"_-(hit at least one thing)-_");
					} else {
						Gdx.app.log(logstag,"_-(hit nothing)-_");
					}

				}

			 */

			//	touchStartedAt = new Vector2(Gdx.input.getX(),Gdx.input.getY());

			//}


			//if holding a item we should move it under the cursor
			if (STMemory.isHoldingItem()){
				//	Ray ray = ME.getCurrentStageCursorRay(); //note: optimization might be possible for this ray function. We shouldn't get the ray again if it already has been got this frame
				//STMemory.currentlyHeldNEW.show();
				Vector2 cp = ME.getCurrentCursorScreenPosition();
				//Vector3 holdPosition= new Vector3(cursorPositionOnStage.x,cursorPositionOnStage.y,100f); //100 
				Vector3 holdPosition =  MainExplorationView.camera.getDisplacementForScreenCoOrdinates(cp.x, cp.y, 300f); //300 is experimental holding position

				PosRotScale cameraRelativePos = new PosRotScale(holdPosition);
				PosRotScale newposition = MainExplorationView.camera.transState.copy().displaceBy(cameraRelativePos);


				STMemory.currentlyHeldNEW.setTransform(newposition);

			}


			//before starting a new drag we have to do a lot of checks to make sure drags are allowed right now
			//this includes checking we arnt already dragging
			//checking no pending event has canceled the next drag (like if the user is moving a scrollable window)
			//checking they arnt "dragging" a concept object rather then the landscape (currentlyHeld)
			//and finally checking if they are allowed to drag at all (ie, maybe the gun is in use and its disabled normal movement)
			if (!dragging
					&& !cancelnextdragclick 
					&& (touchedAModel==null) 
					&& !STMemory.isHoldingItem()
					&& !movementControllDisabled){
				dragging = true;
				dragstart = TimeUtils.millis();


				startdragxscreen = Gdx.input.getX();
				startdragyscreen = Gdx.input.getY();

				Gdx.app.log(logstag,"x="+startdragxscreen+",y="+startdragyscreen);

				startdragx_exview = currentPos.x;
				startdragy_exview = currentPos.y;

			} else if ((!cancelnextdragclick && (touchedAModel==null) && !movementControllDisabled) || forceDragStart) {

				drag_dis_x = Gdx.input.getX()-startdragxscreen;
				drag_dis_y = Gdx.input.getY()-startdragyscreen;

				currentPos.x = startdragx_exview-drag_dis_x;
				currentPos.y = startdragy_exview+drag_dis_y;


			}

			//newtouch = false;

		} else if (dragging == true && !movementControllDisabled){
			Gdx.app.log(logstag,"setting drag to false click");
			//if no longer touching stop dragging
			dragging = false;
			forceDragStart=false;

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
			//	Gdx.app.log(logstag,"MotionDisX:"+MotionDisX);
			//Gdx.app.log(logstag,"MotionDisY:"+MotionDisY);

		} else if (cancelnextdragclick) {

			Gdx.app.log(logstag,"reenable click");
			cancelnextdragclick = false;
		}

		//(touchedAModel!=null) &&
		//if (newUp && !Gdx.input.isTouched()){


		/*
		if (currentTouchState == TouchState.NewTouchUp){	
			Gdx.app.log(logstag,"_-released with model-_");
			touchedAModel=null;
			//	newUp=false;
			//now we need to check if we were over anything when we released, as potentially this means dropping a object onto something
			Ray ray = ME.getCurrentStageCursorRay();
			//MainExplorationView.touchedAModel = ModelManagment.testForHits(ray,false,false);

			if (MainExplorationView.touchedAModel!=null){
				Gdx.app.log(logstag,"_-(mouse up over something)-_");
				MainExplorationView.touchedAModel.fireTouchUp();
				ModelManagment.mousedownOn.remove(touchedAModel);
			} else {
				Gdx.app.log(logstag,"_-(mouse released but no hit)-_");
			}
			touchedAModel=null;

			//untouch everything else that was previously touched (but we might not be over anymore)
			ModelManagment.untouchAll(); 

		}*/

		//	if (!Gdx.input.isTouched() && !newtouch){

		/*
		if (currentTouchState == TouchState.NONE){

			Gdx.app.log(logstag,"_-released touch-_");
			float tx = Gdx.input.getX();
			float ty = -Gdx.input.getY()+gameStage.getHeight();

			//Actor test = usersGUI.hit(tx,ty, false);
			//if (test!=null){
			//	Gdx.app.log(logstag,"__"+test.getClass().getName());
			//}
		}
		 */
		//if (currentTouchState == TouchState.NONE){
		//if (!Gdx.input.isTouched()){
		//newtouch = true;
		//ModelManagment.fireDragStartEnd(); 
		//}

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

		//if not in production we display the position
		if (ME.currentGameMode != GameMode.Production){
			ME.font.draw( ME.interfaceSpriteBatch, "x:: " + currentPos.x+" y:: " + currentPos.y+" z::"+ currentPos.z+"  -"+currentTouchState.toString(), 10,25);

			if (lastHits.size()>0){
				String curString = "";
				for (hitable current : lastHits) {
					String name = current.getClass().getSimpleName();
					curString = curString+name+",";
				}


				ME.font.draw( ME.interfaceSpriteBatch, "="+curString, 10,40);

			}

		}

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

		camera.updateDummyCam();
		camera.updateScreenRelativeAttachments();

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

		/*
		usersGUI.ConceptGun.invalidateHierarchy();
		usersGUI.ConceptGun.validate();
		usersGUI.setNeedsRepopulating(true); //temp fix for layout resizing, this should be handled correctly by splitting widget positioning from widget adding and letting the invalidate/validate handle the repositioning

		usersGUI.invalidateHierarchy();
		usersGUI.validate();
		 */

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

		the3dscene.dispose();

	}


	/**
	 * sets the color and intensity 
	 * @param col
	 * @param Intensity
	 */
	public static void setMouseLight(Color col, float Intensity){
		mouseLight.color.set(col);
		mouseLight.intensity=Intensity;
	}

	/**
	 * sets the light to the default
	 */
	public static void resetMouseLight(){
		mouseLight.set(Color.ORANGE, 1f, 2f, 0.075f, 1f);
	}


	/**
	 * lets us manually trigging dragging of the landscape from another object
	 */
	public static void setAsDragging() {

		dragging = true;
		forceDragStart = true;
		movementControllDisabled = false;

		dragstart = TimeUtils.millis();


		startdragxscreen = Gdx.input.getX();
		startdragyscreen = Gdx.input.getY();

		Gdx.app.log(logstag,"x="+startdragxscreen+",y="+startdragyscreen);

		startdragx_exview = currentPos.x;
		startdragy_exview = currentPos.y;
		cancelnextdragclick = false;
		touchedAModel=null;
	}

}
