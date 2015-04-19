
/**
 * This new package will eventually take over the job of much in locationFeatures.
 * 
 * Rather then having pages of information the idea is to have clickable icons that expand into a bigger interactable objects
 * We can then limit how many can be open at once.
 * This makes refreshing them easier and screen resources more manageable.
 * 
 * Should also make the game a bit more fun rather then having pages of information.
 * 
 * genericMeshIcon.java - a generic clickable icon that can expand to another mesh feature when "opened"
 *                        also provides a close method to animate back to the shut state.
 *                        
 * genericMeshFeature.java - something the icon will expand into. All interactable features expand this.	                       
 *        
 * genericLocationLock.java - a special feature with no icon. It cacts as a "RepairScreen" that needs nodes before the rest of the
 *                             features can be accessed. Its big, open by default and styled differently.   
 *                             Once unlocked it vanishs leaving a Icon containing a info screen beyond - this marks the center of all features for that location.            
 * 
 * Unlike previous 3d interfaces, these should all use depthmap fonts to work at any scale.
 * For that they must support custom shaders 
 * 
 * 
 * TODO: Make this stuff
 * TODO2: Use this stuff
 * 
 * @author Tom
 *
 */
package com.lostagain.nl.me.features;