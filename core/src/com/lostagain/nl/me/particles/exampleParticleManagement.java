package com.lostagain.nl.me.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.lostagain.nl.me.newmovements.PosRotScale;

public class exampleParticleManagement {

	private static final String PARTICLE = "particles/point.pfx";
	private static final String PARTICLE_SMALLEXPLOSION = "particles/pinkboom.pfx";
	
	static ParticleSystem particleSystem;
	static PointSpriteParticleBatch pointSpriteBatch;
	static AssetManager assets;
	
	private static PFXPool smallExplosionPool;
	
	private static class PFXPool extends Pool<ParticleEffect> {
	    private ParticleEffect sourceEffect;

	    public PFXPool(ParticleEffect sourceEffect) {
	        this.sourceEffect = sourceEffect;
	    }

	    @Override
	    public void free(ParticleEffect pfx) {
	        pfx.reset();
	        super.free(pfx);
	    }

	    @Override
	    protected ParticleEffect newObject() {
	        return sourceEffect.copy();
	    }
	}
	
	public static void setup(Camera cam){
		
		// ParticleSystem is a singleton class, we get the instance instead of creating a new object:
		particleSystem = ParticleSystem.get();
		//create particle batch
		pointSpriteBatch = new PointSpriteParticleBatch();
		pointSpriteBatch.setCamera(cam);
		particleSystem.add(pointSpriteBatch);
		
		//load the particle assets
		//AssetManager assets = new AssetManager();
		ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
		
		ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());
		        
		assets = new AssetManager();
		        
		assets.setLoader(ParticleEffect.class, loader);

		//assets.load(PARTICLE, ParticleEffect.class, loadParam);
		assets.load(PARTICLE_SMALLEXPLOSION, ParticleEffect.class, loadParam);
		
		
		
		assets.finishLoading();
		
		
		//default test

		Vector3 targetPos = new Vector3(100f,10f,10f);
		PosRotScale defaultPosition = new PosRotScale(targetPos);
		defaultPosition.setToRotation(1, 0, 0, 90);
		

		ParticleEffect originalEffect = assets.get(PARTICLE_SMALLEXPLOSION);
				
		// we cannot use the originalEffect, we must make a copy each time we create new particle effect
		ParticleEffect effect = originalEffect.copy();
		smallExplosionPool = new PFXPool(effect);
		
	//	addStaticEffect(defaultPosition,effect);
		
		
		
	}


	public void addExplosion(PosRotScale location){
		
		//the style of the particles come from both what blew up and what weapon was used
		//the size the the explosion should reflect the power of the weapon
		//the style of the particles should reflect what blew up
		//(also the colour of the weapon should effect the glow)
		
		//To do this we will need dynamic explosion styles eventually
		//for now its all just a fixed style
		

		ParticleEffect originalEffect =  assets.get(PARTICLE_SMALLEXPLOSION);
		// we cannot use the originalEffect, we must make a copy each time we create new particle effect
		ParticleEffect effect = smallExplosionPool.obtain(); // originalEffect.copy();
		
		addStaticEffect(location,effect);
		
		//smallExplosionPool.free(effect); (needs to be run after explosion is finnished)
	}

	/**
	 * adds a partical effect at a fixed location and rotation specified.
	 * (leave scale 1f/1f/1f - mess's up if scaled. If scaling is needed edit the source file)
	 * remember the effect supplied should be a copy of the original in assets
	 * 
	 * @param location
	 */
	private static void addStaticEffect(PosRotScale location, ParticleEffect effect) {
		
		
		effect.init();
		effect.start();  // optional: particle will begin playing immediately
		
		
		
		Matrix4 targetMatrix = location.createMatrix();
		//targetMatrix.idt();
		//targetMatrix.translate(targetPos);
		//targetMatrix.rotate(Vector3.X, 90);
		
		effect.setTransform(targetMatrix);
	//	effect.scale(33f, 33f, 33f);
			
		particleSystem.add(effect);
		
		
	}
	
	
	
	public static ParticleSystem prepareAndGetParticleSystem() {
	
		
	    particleSystem.update(); // technically not necessary for rendering
	    particleSystem.begin();
	    particleSystem.draw();
	    particleSystem.end();
	    return particleSystem;
	}
	
}
