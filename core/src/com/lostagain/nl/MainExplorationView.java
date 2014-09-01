package com.lostagain.nl;

import java.awt.Button;
import java.util.Iterator;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.LocationGUI.GUIBar;
import com.lostagain.nl.LocationGUI.Inventory;
import com.lostagain.nl.LocationGUI.Link;
import com.lostagain.nl.LocationGUI.LocationContainer;
import com.lostagain.nl.temp.SpiffyGenericTween;
import com.lostagain.nl.temp.SpiffyTweenConstructor;

/** The main exploration view, which lets them see LocationURIs 
 * **/
public class MainExplorationView implements Screen {

	static Logger Log = Logger.getLogger("ME.MainExplorationView");
    final ME game;
    
	static Stage gameStage;
	
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
    Camera camera;
    
    Rectangle bucket;
    Array<Rectangle> raindrops;
    long lastDropTime;
    int dropsGathered;
    
    public static GUIBar usersGUI = new GUIBar();
    
    static SpiffyGenericTween<Double> currentCameraTweenX;
    static SpiffyGenericTween<Double> currentCameraTweenY;
    static  Timer cameraTimer = new Timer();
    static  Task cameraTweenTask;
	
    
    
    //final thing should use perspective
    enum cammode{
    	ortha,perspective
    }    
    cammode currentmode = cammode.ortha;
    
    //controlls the 3d background
   public static  BackgroundManager background = new BackgroundManager();
    
    
   
    public MainExplorationView(final ME gam) {
    	
        this.game = gam;

        Log.info("setting up stage and stuff");
        
        //create the game gui interface
        guiStage = new Stage();

        Log.info("setting up stage and stuff..");
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
   	   				Log.info ("camera at end setting to:"+newX);   	 
   	   				
   	   				CurrentX=(float) newX;
   	   				CurrentY=(float) newY;
   	   				
   	   				LookAtX = CurrentX; 
   	   				LookAtY = CurrentY;
   					
   				}
   				
   				
				
			}
   			
   		};
   	 

        Log.info("creating textures");
        
        //to flip the y co-ordinate 
        //my brain cant take bottom left 0, I need top left zero!
     //   OrthographicCamera guicam= new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      //  guicam.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
     //   guicam.update();
      //  guiStage.getViewport().setCamera(guicam);
      //  guiStage.getCamera().update();
        
        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("data\\droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("data\\bucket.png"));
        customCursorTest  = new Texture(Gdx.files.internal("data\\bucket.png"));
        
        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("data\\drop.mp3"));
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
        

        Log.info("creating game stage");

    	  gameStage = new Stage();
    	  
    	  InputMultiplexer multiplexer = new InputMultiplexer();
    	  multiplexer.addProcessor(guiStage);
    	  multiplexer.addProcessor(gameStage);
    	  Gdx.input.setInputProcessor(multiplexer);
    	  
          gameStage.getViewport().setCamera(camera);
         // stage.getCamera().translate(0, 250, 0);
          
      	  gameStage.getCamera().update();

          camera.update(true);
          
          
      	   PlayersData.homeLoc = new LocationContainer(PlayersData.computersuri);
      	  
          addnewlocation( PlayersData.homeLoc,200,500);
          

          Log.info("centering cameraf");
    	  
    	  centerViewOn( PlayersData.homeLoc);

    	  
    	  //create background
    	  background.setup();
    	  
    	  
    	gameStage.setDebugAll(true);
    	
       

    }
    
    public static void setCursor(Texture curimage){
    	
    	if (curimage!=null){
    		customCursor = curimage;
    	} else {
    		customCursor = null;
    	}
    	
    }

    public static void addnewlocation(LocationContainer homeLoc,int x,int y) {
    	    	
  	  homeLoc.setPosition(x,y);
  	  
  	homeLoc.setClip(false);
  	
  	  gameStage.addActor(homeLoc);
		
	}


    
    
    public static void centerViewOn(LocationContainer locationcontainer){
    	   
    	//CurrentX=locationcontainer.getCenterX();  //getX()+(locationcontainer.getWidth()/2);
    	//CurrentY=locationcontainer.getCenterY(); //getY()+(locationcontainer.getHeight()/2);
    	
    	float newX = locationcontainer.getCenterX();
    	float newY = locationcontainer.getCenterY();
    	
    	//asign new tweens
		 currentCameraTweenX = SpiffyTweenConstructor.Create(CurrentX.doubleValue(),newX, 25);
		 currentCameraTweenY = SpiffyTweenConstructor.Create(CurrentY.doubleValue(),newY, 25);
    	
		//ensure camera animator is running
		 if (!cameraTweenTask.isScheduled()){
			 Log.info("triggering timer");
			 cameraTimer.scheduleTask(cameraTweenTask, 0.1f);
			 
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
        background.modelBatch.begin( camera);
        background.modelBatch.render(background.instances);
        background.modelBatch.end();
        
        gameStage.getViewport().setCamera(camera);
        
        gameStage.act(Gdx.graphics.getDeltaTime());
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
        ME.font.draw( ME.batch, "X:: " + CurrentX+" Y:: " + CurrentY, 10,45);
        ME.font.draw( ME.batch, "Z:: " + CurrentZ, 10, 25);
        
   //    game.batch.draw(bucketImage, bucket.x, bucket.y);
        
        
        ME.batch.end();

        // process user input
      //  if (Gdx.input.isTouched()) {
        	
        	
          //  Vector3 touchPos = new Vector3();
          //  touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
          //  camera.unproject(touchPos);
      //  }
        
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
        

        
        /*

        
        
        // check if we need to create a new raindrop
       
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
        {
            spawnRaindrop();
        }
        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we increase the 
        // value our drops counter and add a sound effect.
        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0)
                iter.remove();
            if (raindrop.overlaps(bucket)) {
                dropsGathered++;
                dropSound.play();
                iter.remove();
            }
        }
        */
        
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
    	
    	
    	Log.info("height="+gameStage.getHeight());
    	Log.info("w="+gameStage.getWidth());
    	
    	
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
		
		  //The user is considered to be home straight away, if they are heading home
		  // we dont wait till after the "push" animation
	      // because the "new network node" being created makes use of this variable
		  if (linksToThisPC.equals(PlayersData.computersuri)){
			  isAtHome = true;
		  } else {
			  isAtHome = false;		  
		  }
		  
		  //get the node screen.
		  //This will automatically check if it already exists
		  //else it will create a new one
		  LocationContainer screen = LocationContainer.getLocation(linksToThisPC);
		  
		  
		  ME.currentlyOpenLocation = screen;
		  
		  //get the x and y of the location and goto it
		//  float x = screen.getCenterX();
		 // float y = screen.getCenterY();
		  
		  
		  
		  centerViewOn(ME.currentlyOpenLocation);
		  
		
	}

	public static void gotoHomeLoc() {

  	  centerViewOn( PlayersData.homeLoc);

		
	}


}