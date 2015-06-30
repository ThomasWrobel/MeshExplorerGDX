package com.lostagain.nl.me.features;

import com.badlogic.gdx.graphics.g3d.Model;
import com.lostagain.nl.me.models.Animating;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.ModelManagment.RenderOrder;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;



/**
 * All interactive mesh features should expand this.
 * eg, a linkscreen, or a email page.
 * 
 * Features are triggered from an associated icon that can be clicked to expand into the feature, and then either manually
 * closed, or automatically closed once a certain threshold are open at once.
 * 
 * @author Tom
 *
 */
public abstract class GenericMeshFeature extends AnimatableModelInstance implements Animating {
	
	private MeshIcon associatedIcon;
	
	
	//various things to handle animation of appearing/disaspering
	enum FeatureState {
		appearing,disapearing,normal,hidden;
	}
	FeatureState currentState = FeatureState.hidden;
	protected float Opacity = 0f;
	float fadeDuration = 0.500f;
	float timeIntoFade = 0.0f;
	Runnable runAfterFadeIn = null;
	Runnable runAfterFadeOut = null;
	//----------------------------------------------------
	
	public GenericMeshFeature(Model model) {
		super(model);
		//generic mesh features dont inherit visibility from their MeshIcon parents
		super.setInheritedVisibility(false);
		
		
	}
	
	
	public void setAssociatedIcon(MeshIcon associatedIcon){		
		this.associatedIcon=associatedIcon;
	}
	/** 
	 * Actions that run when this feature is opened (typically opened from its associated icon)
	 * This triggers a quick fade in animation, while also telling its associated icon to hide itself after the fadeIn is finished
	 * 
	 * **/
	public void open(){		

		Runnable hideIcon = new Runnable(){
			@Override
			public void run() {
				associatedIcon.hide();
			}			
		};
		fadeIn(400,hideIcon);
	}
	
	/**
	 *  Actions that run when this feature is closed,
	 * Triggers a quick fade out, while also telling its associated icon to unhide itself.
	 * Once the fadeout is finished it then tells its associated icon to run its close animation
	 *  **/
	public void close(){
		
		Runnable closeIcon = new Runnable(){
			@Override
			public void run() {
				associatedIcon.animateClose();
			}			
		};
		associatedIcon.show();
		fadeOut(400,closeIcon);

		
	}
	
	/** 
	 * Called every frame to update any animations in progress
	 * representing appearing or disappearing.
	 * This can be as simple as setting the opacity to the alpha value.	
	 * 
	 * @param alpha - Alpha goes from 0-1 when state is appearing and from 1-0 when state is disappearing
	 * @param currentState - appearing,disappearing,normal or hidden
	 **/
	abstract void updateApperance(float alpha,FeatureState currentState);
	
	//abstract void fadeIn(float duration,Runnable runAfterFadeIn);
	//abstract void fadeOut(float duration,Runnable runAfterFadeOut);
	
	/** called every frame to update any fade in progress **/
	//abstract void updateFade(float delta);
	
	
	public void updateAnimationFrame(float delta){
		updateFade(delta);
		
	}


	void fadeIn(float duration, Runnable runAfterFadeIn) {
		currentState = FeatureState.appearing;
		Opacity = 0f;
		ModelManagment.addmodel(this, RenderOrder.zdecides);
		
		ModelManagment.addAnimating(this);
		this.runAfterFadeIn= runAfterFadeIn;
	}


	void fadeOut(float duration, Runnable runAfterFadeOut) {
		currentState = FeatureState.disapearing;
		Opacity = 1f;
		ModelManagment.addAnimating(this);
		this.runAfterFadeOut= runAfterFadeOut;
	}


	void updateFade(float delta) {
		
		timeIntoFade = timeIntoFade+delta;
		float ratio = timeIntoFade/fadeDuration;	
				
		switch (currentState) {
		case appearing:
			Opacity = ratio;
			if (ratio>1){
				ModelManagment.removeAnimating(this);
				currentState = FeatureState.normal;
				runAfterFadeIn.run();
			}
			break;
		case disapearing:
			Opacity = 1-ratio;
			if (ratio>1){
				ModelManagment.removeAnimating(this);
				currentState = FeatureState.hidden;
				runAfterFadeOut.run();
				
			}
			break;
		case hidden:
			Opacity = 0f;
			ModelManagment.removeModel(this);
			return;
		case normal:
			Opacity = 1f;
			break;
		
		}
		
		updateApperance(Opacity,currentState);
		
		
	}


	public void setFadeDuration(float fadeDuration) {
		this.fadeDuration = fadeDuration;
	}
	
	
	
	
	
	
}
