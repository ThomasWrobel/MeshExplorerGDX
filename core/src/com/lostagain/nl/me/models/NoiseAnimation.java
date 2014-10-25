package com.lostagain.nl.me.models;

import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NoiseAnimation implements ApplicationListener {

	//static Logger Log = Logger.getLogger("ME.TestAnimation");

	final static String logstag = "ME.NoiseAnimation";
	
    private static final int    FRAME_COLS = 5;     // #1
    private static final int    FRAME_ROWS = 1;     // #2

    Animation           noiseAnimation;      // #3
    Texture             noiseImages;      // #4
    TextureRegion[]         noiseFrames;     // #5
    SpriteBatch         spriteBatch;        // #6
    TextureRegion           currentFrame;       // #7

    float stateTime;                    // #8

    @Override
    public void create() {
    	Gdx.app.log(logstag,"creating NoiseAnimation");
    	
    	//could be generated rather then from a file? Save space? Increasing loading time?
        noiseImages = new Texture(Gdx.files.internal("data/noise.png")); // #9
        
        
        TextureRegion[][] tmp = TextureRegion.split(noiseImages, noiseImages.getWidth()/FRAME_COLS, noiseImages.getHeight()/FRAME_ROWS);              // #10
        noiseFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                noiseFrames[index++] = tmp[i][j];
            }
        }
        Gdx.app.log(logstag,"creating noise Animation");
        
        noiseAnimation = new Animation(0.1f, noiseFrames);      // #11
        spriteBatch = new SpriteBatch();                // #12
        stateTime = 0f;                         // #13
    }

    
    public TextureRegion getKeyFrame(float delta){
    	
    	   stateTime += delta; //Gdx.graphics.getDeltaTime();           // #15
           currentFrame = noiseAnimation.getKeyFrame(stateTime, true); 
           return currentFrame;
    }
    
    @Override
    public void render() {
    	/*
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);                        // #14
        stateTime += Gdx.graphics.getDeltaTime();           // #15
        currentFrame = noiseAnimation.getKeyFrame(stateTime, true);  // #16
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, 50, 50);             // #17
        spriteBatch.end();*/
    }

	@Override
	public void resize(int width, int height) {
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