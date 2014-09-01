package com.lostagain.nl;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.lostagain.nl.LocationGUI.LocationContainer;

public class MainMenuScreen implements Screen {

	static Logger Log = Logger.getLogger("MainMenuScreen");
	 final ME game;
	    OrthographicCamera camera;

	    public MainMenuScreen(final ME gam) {
	        game = gam;

	        camera = new OrthographicCamera();
	        camera.setToOrtho(true, 800, 480);
	        
	        

	    }
	    
	    
	    
@Override
public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0.2f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    camera.update();
  //  game.batch.setProjectionMatrix(camera.combined);
    


    //for development we skip the title screen
   // if (Gdx.input.isTouched()) {
   //    
   // }
}

public void start(){
	
	Log.info("start");
	 game.setScreen(new MainExplorationView(game));
     dispose();
}


@Override
public void resize(int width, int height) {
	// TODO Auto-generated method stub
}

@Override
public void show() {
	// TODO Auto-generated method stub
	
}

@Override
public void hide() {
	// TODO Auto-generated method stub
	
}

@Override
public void pause() {
	// TODO Auto-generated method stub
	
}

@Override
public void resume() {
	// TODO Auto-generated method stub
	
}

@Override
public void dispose() {
	// TODO Auto-generated method stub
	
}


}