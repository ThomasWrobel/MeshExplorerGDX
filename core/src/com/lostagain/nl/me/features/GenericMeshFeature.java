package com.lostagain.nl.me.features;

import com.badlogic.gdx.graphics.g3d.Model;
import com.lostagain.nl.me.features.MeshIcon.FeatureState;
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
public abstract class GenericMeshFeature extends AnimatableModelInstance  {
	
	private MeshIcon associatedIcon;
	
	
	
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
	 * 
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
	 *  
	public void close(){
		
		Runnable closeIcon = new Runnable(){
			@Override
			public void run() {
				associatedIcon.animateClose();
			}			
		};
		associatedIcon.show();
		fadeOut(400,closeIcon);

		
	}**/
	
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
	
	
	


	
	
	
}
