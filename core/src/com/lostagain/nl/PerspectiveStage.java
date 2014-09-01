package com.lostagain.nl;

import java.util.logging.Logger;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;

/** a version of Stage that can handle clicks in 3d space
 * ref; http://pixelscientists.com/blog/posts/3d-ingame-ui-with-scene2dui-in-libgdx **/
public class PerspectiveStage  extends Stage {


	static Logger Log = Logger.getLogger("PerspectiveStage");
	
	    @Override
	    public Vector2 screenToStageCoordinates (Vector2 screenCoords) {
	    	
	        Ray pickRay = getViewport().getPickRay(screenCoords.x, screenCoords.y);
	        
	        
	        Vector3 intersection = new Vector3(0, 0, 1);
	        
	        if (Intersector.intersectRayPlane(pickRay, new Plane(new Vector3(0, 0, 1), Vector3.Zero), intersection)) {
	                screenCoords.x = intersection.x;
	                screenCoords.y = intersection.y;
	        } else {
	                screenCoords.x = Float.MAX_VALUE;
	                screenCoords.y = Float.MAX_VALUE;
	        }

	    //    Log.info(screenCoords.x+", "+screenCoords.y  );
	        
	        
	        
	        return screenCoords;
	    }

	
}
