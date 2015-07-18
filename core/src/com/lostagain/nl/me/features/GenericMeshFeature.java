package com.lostagain.nl.me.features;

import com.lostagain.nl.me.features.MeshIcon.FeatureState;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.IsAnimatableModelInstance;



/**
 * All interactive mesh features should both be AnimatableModelInstance and implement this.
 * eg, a linkscreen, or a email page.
 * 
 * They can be any sort of AnimatableModelInstance and this interface ensures they have a few extra functions 
 * needed for it to work with a MeshIcon
 * 
 * @author Tom
 *
 */
// SomeClass<T extends Fragment & SomeInterface>
interface GenericMeshFeature extends IsAnimatableModelInstance {
	
	//private MeshIcon associatedIcon;
	
	/**
	 * All features have to both be a proper AnimatableModelInstance and return themselves with this method
	 * @return
	 */
	AnimatableModelInstance getAnimatableModelInstance();
	
	//public GenericMeshFeature(Model model) {
	//	super(model);
		//generic mesh features dont inherit visibility from their MeshIcon parents
	//	super.setInheritedVisibility(false);
		
		
	//}
	
	
	//public void setAssociatedIcon(MeshIcon associatedIcon){		
	//	this.associatedIcon=associatedIcon;
	//}
	
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
	void updateApperance(float alpha,FeatureState currentState);

	/** clears this features contents ready to refill/refresh it**/
	void clear();

	/** the widget should allow others to monitor its size changes. If using GWTish widgets for mesh features, this will
	 * be implemented already.
	 * If not just, fire the specified runnable any time the size fininishs changing **/
	void addOnSizeChangeHandler(Runnable runnable);
	
	
	//abstract void fadeIn(float duration,Runnable runAfterFadeIn);
	//abstract void fadeOut(float duration,Runnable runAfterFadeOut);
	
	/** called every frame to update any fade in progress **/
	//abstract void updateFade(float delta);
	


	


	
	
	
}
